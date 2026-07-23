import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MascotaService } from '../../../core/services/mascota.service';
import { ServicioService, Servicio, ProveedorSummary } from '../../../core/services/servicio.service';
import { DisponibilidadService, Disponibilidad } from '../../../core/services/disponibilidad.service';
import { ReservaService, Reserva } from '../../../core/services/reserva.service';
import { RegistroVacunacionService, RegistroVacunacion } from '../../../core/services/registro-vacunacion.service';
import { VacunaCatalogService, Vacuna } from '../../../core/services/vacuna-catalog.service';
import { ToastService } from '../../../core/services/toast.service';
import { Mascota } from '../../../core/models/mascota.model';
import { AuthService } from '../../../core/services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-mascota-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './mascota-detail.component.html'
})
export class MascotaDetailComponent implements OnInit {
  mascota = signal<Mascota | null>(null);
  vacunas = signal<RegistroVacunacion[]>([]);
  servicios = signal<Servicio[]>([]);
  proveedores = signal<ProveedorSummary[]>([]);
  disponibilidades = signal<Disponibilidad[]>([]);
  vacunasCatalog = signal<Vacuna[]>([]);
  activeTab = signal<'info' | 'vacunas' | 'reservar'>('info');
  showReservaForm = signal(false);
  showVacunaForm = signal(false);
  loading = signal(false);
  mascotaId = 0;

  reservaForm = {
    servicioId: null as number | null,
    proveedorId: null as number | null,
    fechaReserva: '',
    horaInicio: '',
    horaFin: '',
    notas: ''
  };

  vacunaForm = {
    vacunaId: null as number | null,
    fechaAplicacion: '',
    fechaVencimiento: '',
    lote: '',
    veterinario: '',
    observaciones: ''
  };

  constructor(
    private route: ActivatedRoute,
    private mascotaService: MascotaService,
    private servicioService: ServicioService,
    private disponibilidadService: DisponibilidadService,
    private reservaService: ReservaService,
    private registroVacService: RegistroVacunacionService,
    private vacunaCatalogService: VacunaCatalogService,
    private toast: ToastService,
    private auth: AuthService
  ) {}

  ngOnInit(): void {
    this.mascotaId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadMascota();
    this.loadVacunas();
    this.loadServicios();
    this.loadVacunaCatalog();
  }

  loadMascota(): void {
    this.mascotaService.getById(this.mascotaId).subscribe({
      next: (res) => {
        if (res.success) this.mascota.set(res.data);
      }
    });
  }

  loadVacunas(): void {
    this.registroVacService.getByMascota(this.mascotaId).subscribe({
      next: (res) => {
        if (res.success) this.vacunas.set(res.data.content);
      }
    });
  }

  loadServicios(): void {
    this.servicioService.getActive().subscribe({
      next: (res) => {
        if (res.success) this.servicios.set(res.data);
      }
    });
  }

  loadVacunaCatalog(): void {
    this.vacunaCatalogService.getAll().subscribe({
      next: (res) => {
        if (res.success) this.vacunasCatalog.set(res.data.content);
      }
    });
  }

  onServicioChange(): void {
    this.reservaForm.proveedorId = null;
    this.disponibilidades.set([]);
    if (this.reservaForm.servicioId) {
      this.servicioService.getProveedoresByServicio(this.reservaForm.servicioId).subscribe({
        next: (res) => {
          if (res.success) this.proveedores.set(res.data);
        }
      });
    } else {
      this.proveedores.set([]);
    }
  }

  onProveedorChange(): void {
    this.disponibilidades.set([]);
    if (this.reservaForm.proveedorId && this.reservaForm.servicioId) {
      this.disponibilidadService.getByProveedorServicio(this.reservaForm.proveedorId, this.reservaForm.servicioId).subscribe({
        next: (res) => {
          if (res.success) this.disponibilidades.set(res.data);
        }
      });
    }
  }

  onSubmitReserva(): void {
    this.loading.set(true);
    const m = this.mascota()!;
    const clienteId = m.clienteId;
    const data = {
      clienteId,
      proveedorId: this.reservaForm.proveedorId!,
      servicioId: this.reservaForm.servicioId!,
      mascotaId: this.mascotaId,
      fechaReserva: this.reservaForm.fechaReserva,
      fechaInicio: this.reservaForm.fechaReserva,
      horaInicio: this.reservaForm.horaInicio,
      horaFin: this.reservaForm.horaFin,
      notas: this.reservaForm.notas
    };
    this.reservaService.create(data).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Reserva creada exitosamente');
        this.showReservaForm.set(false);
        this.resetReservaForm();
      },
      error: (err) => {
        this.loading.set(false);
        this.toast.error(err.error?.message || 'Error al crear reserva');
      }
    });
  }

  onSubmitVacuna(): void {
    this.loading.set(true);
    const data = {
      mascotaId: this.mascotaId,
      vacunaId: this.vacunaForm.vacunaId!,
      fechaAplicacion: this.vacunaForm.fechaAplicacion,
      fechaVencimiento: this.vacunaForm.fechaVencimiento,
      lote: this.vacunaForm.lote,
      veterinario: this.vacunaForm.veterinario,
      observaciones: this.vacunaForm.observaciones
    };
    this.registroVacService.create(data).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Registro de vacunación registrado');
        this.showVacunaForm.set(false);
        this.resetVacunaForm();
        this.loadVacunas();
      },
      error: (err) => {
        this.loading.set(false);
        this.toast.error(err.error?.message || 'Error al registrar vacunación');
      }
    });
  }

  deleteVacuna(vac: RegistroVacunacion): void {
    Swal.fire({
      title: 'Eliminar registro',
      text: `¿Eliminar el registro de ${vac.vacunaNombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.registroVacService.delete(vac.id).subscribe({
          next: () => {
            this.toast.success('Registro eliminado');
            this.loadVacunas();
          },
          error: () => this.toast.error('Error al eliminar')
        });
      }
    });
  }

  private resetReservaForm(): void {
    this.reservaForm = { servicioId: null, proveedorId: null, fechaReserva: '', horaInicio: '', horaFin: '', notas: '' };
    this.proveedores.set([]);
    this.disponibilidades.set([]);
  }

  private resetVacunaForm(): void {
    this.vacunaForm = { vacunaId: null, fechaAplicacion: '', fechaVencimiento: '', lote: '', veterinario: '', observaciones: '' };
  }
}
