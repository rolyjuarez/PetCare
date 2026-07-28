import { Component, signal, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MascotaService } from '../../../core/services/mascota.service';
import { AuthService } from '../../../core/services/auth.service';
import { EspecieService, Especie } from '../../../core/services/especie.service';
import { RazaService, Raza } from '../../../core/services/raza.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { UploadService } from '../../../core/services/upload.service';
import { Mascota, MascotaRequest } from '../../../core/models/mascota.model';
import { ToastService } from '../../../core/services/toast.service';
import { RegistroVacunacionService, RegistroVacunacion, RegistroVacunacionRequest } from '../../../core/services/registro-vacunacion.service';
import { VacunaCatalogService, Vacuna } from '../../../core/services/vacuna-catalog.service';
import { ServicioService, Servicio } from '../../../core/services/servicio.service';
import { DisponibilidadService, Disponibilidad } from '../../../core/services/disponibilidad.service';
import { ReservaService, Reserva, ReservaRequest } from '../../../core/services/reserva.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-mascota-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './mascota-list.component.html'
})
export class MascotaListComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  items = signal<Mascota[]>([]);
  isCliente = false;
  searchTerm = '';
  showModal = signal(false);
  loading = signal(false);
  error = signal('');
  editingRestricted = signal(false);
  previewUrl = signal<string | null>(null);
  selectedFile = signal<File | null>(null);
  editingId = signal<number | null>(null);

  especies = signal<Especie[]>([]);
  razas = signal<Raza[]>([]);

  form = {
    nombre: '',
    fechaNacimiento: '',
    genero: 'M',
    peso: null as number | null,
    color: '',
    especieId: null as number | null,
    razaId: null as number | null,
  };

  private allItems: Mascota[] = [];
  private clienteId: number | null = null;
  private _pendingRazaId: number | null = null;

  // Vacuna modal
  showVacunaModal = signal(false);
  vacunaMascota = signal<Mascota | null>(null);
  vacunas = signal<RegistroVacunacion[]>([]);
  vacunasCatalog = signal<Vacuna[]>([]);
  vacunaForm = {
    vacunaId: null as number | null,
    fechaAplicacion: '',
    fechaVencimiento: '',
    lote: '',
    veterinario: '',
    observaciones: ''
  };

  // Reserva modal
  showReservaModal = signal(false);
  reservaMascota = signal<Mascota | null>(null);
  servicios = signal<Servicio[]>([]);
  disponibilidades = signal<Disponibilidad[]>([]);
  filteredDisponibilidades = signal<Disponibilidad[]>([]);
  selectedDisponibilidad = signal<Disponibilidad | null>(null);
  selectedServicio = signal<Servicio | null>(null);
  reservaForm = {
    servicioId: null as number | null,
    fechaReserva: '',
    horaInicio: '',
    horaFin: '',
    notas: ''
  };

  reservasPorMascota = signal<Map<number, Reserva[]>>(new Map());

  editReservaId = signal<number | null>(null);
  editReserva = signal<Reserva | null>(null);

  constructor(
    private mascotaService: MascotaService,
    private auth: AuthService,
    private especieService: EspecieService,
    private razaService: RazaService,
    private clienteService: ClienteService,
    private uploadService: UploadService,
    private toast: ToastService,
    private registroVacService: RegistroVacunacionService,
    private vacunaCatalogService: VacunaCatalogService,
    private servicioService: ServicioService,
    private disponibilidadService: DisponibilidadService,
    private reservaService: ReservaService
  ) {}

  ngOnInit(): void {
    this.isCliente = this.auth.hasRole('CLIENTE');
    this.loadMascotas();
    this.especieService.getAll({ size: 100 }).subscribe({
      next: (res) => { if (res.success) this.especies.set(res.data.content); }
    });
    this.vacunaCatalogService.getAll().subscribe({
      next: (res) => { if (res.success) this.vacunasCatalog.set(res.data.content); }
    });
    this.servicioService.getActive().subscribe({
      next: (res) => { if (res.success) this.servicios.set(res.data); }
    });
    if (this.isCliente) {
      this.clienteService.getMe().subscribe({
        next: (res) => { if (res.success) this.clienteId = res.data.id; }
      });
    }
  }

  loadMascotas(): void {
    const loadReservas = () => {
      this.reservaService.getAll({ size: 200 }).subscribe({
        next: (res) => {
          if (res.success) {
            const map = new Map<number, Reserva[]>();
            for (const r of res.data.content) {
              const list = map.get(r.mascotaId) || [];
              list.push(r);
              map.set(r.mascotaId, list);
            }
            this.reservasPorMascota.set(map);
          }
        }
      });
    };

    if (this.auth.hasRole('CLIENTE')) {
      this.mascotaService.getMy().subscribe({
        next: (res) => { if (res.success) { this.allItems = res.data; this.items.set(this.allItems); loadReservas(); } }
      });
    } else {
      this.mascotaService.getAll({ size: 100 }).subscribe({
        next: (res) => { if (res.success) { this.allItems = res.data.content; this.items.set(this.allItems); loadReservas(); } }
      });
    }
  }

  onEspecieChange(): void {
    this.form.razaId = null;
    this.razas.set([]);
    if (this.form.especieId) {
      this.razaService.getByEspecie(this.form.especieId).subscribe({
        next: (res) => {
          if (res.success) {
            this.razas.set(res.data);
            if (this.form.razaId === null && this._pendingRazaId != null) {
              this.form.razaId = this._pendingRazaId;
              this._pendingRazaId = null;
            }
          }
        }
      });
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.selectedFile.set(file);
      const reader = new FileReader();
      reader.onload = () => this.previewUrl.set(reader.result as string);
      reader.readAsDataURL(file);
    }
  }

  removeFile(): void {
    this.selectedFile.set(null);
    this.previewUrl.set(null);
    if (this.fileInput) this.fileInput.nativeElement.value = '';
  }

  openModal(): void {
    this.resetForm();
    this.editingId.set(null);
    this.showModal.set(true);
  }

  openEditModal(mascota: Mascota): void {
    this.editingId.set(mascota.id);
    this.form = {
      nombre: mascota.nombre,
      fechaNacimiento: this.parseDate(mascota.fechaNacimiento),
      genero: mascota.genero,
      peso: mascota.peso,
      color: mascota.color,
      especieId: mascota.especieId,
      razaId: mascota.razaId,
    };
    this.previewUrl.set(mascota.imagenUrl || null);
    if (mascota.especieId && mascota.razaId) {
      this._pendingRazaId = mascota.razaId;
      this.onEspecieChange();
    }
    const reservas = this.reservasPorMascota().get(mascota.id);
    if (reservas && reservas.length > 0) {
      this.editingRestricted.set(true);
    } else {
      this.registroVacService.getByMascota(mascota.id).subscribe({
        next: (res) => { this.editingRestricted.set(res.success && res.data.content.length > 0); }
      });
    }
    this.showModal.set(true);
  }

  private parseDate(fecha: any): string {
    if (!fecha) return '';
    if (typeof fecha === 'string') return fecha.substring(0, 10);
    if (Array.isArray(fecha)) return `${fecha[0]}-${String(fecha[1]).padStart(2, '0')}-${String(fecha[2]).padStart(2, '0')}`;
    return '';
  }

  closeModal(): void {
    this.showModal.set(false);
    this.error.set('');
    this.editingId.set(null);
    this.editingRestricted.set(false);
    this.removeFile();
  }

  save(): void {
    if (!this.form.nombre || !this.form.fechaNacimiento || !this.form.especieId || !this.form.razaId) {
      this.error.set('Complete los campos obligatorios');
      return;
    }
    if (!this.clienteId && this.isCliente) {
      this.error.set('No se pudo identificar al cliente');
      return;
    }

    this.loading.set(true);
    this.error.set('');

    if (this.selectedFile()) {
      this.uploadService.uploadMascotaFoto(this.selectedFile()!).subscribe({
        next: (uploadRes) => {
          this.saveMascota(uploadRes.url);
        },
        error: () => {
          this.loading.set(false);
          this.error.set('Error al subir la imagen');
        }
      });
    } else {
      this.saveMascota(this.previewUrl());
    }
  }

  private saveMascota(imagenUrl: string | null): void {
    const req: MascotaRequest = {
      nombre: this.form.nombre,
      fechaNacimiento: this.form.fechaNacimiento,
      genero: this.form.genero,
      peso: this.form.peso || 0,
      color: this.form.color,
      especieId: this.form.especieId!,
      razaId: this.form.razaId!,
      clienteId: this.clienteId!,
    };
    if (imagenUrl) req.imagenUrl = imagenUrl;

    const id = this.editingId();
    const obs = id
      ? this.mascotaService.update(id, req)
      : this.mascotaService.create(req);

    obs.subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          this.toast.success(id ? 'Mascota actualizada' : 'Mascota creada');
          this.closeModal();
          this.loadMascotas();
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message || 'Error al guardar mascota');
      }
    });
  }

  deleteMascota(mascota: Mascota): void {
    Swal.fire({
      title: 'Eliminar mascota',
      text: `¿Estás seguro de eliminar a ${mascota.nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.mascotaService.delete(mascota.id).subscribe({
          next: () => {
            this.toast.success('Mascota eliminada');
            this.loadMascotas();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  private resetForm(): void {
    this.form = { nombre: '', fechaNacimiento: '', genero: 'M', peso: null, color: '', especieId: null, razaId: null };
    this.razas.set([]);
    this.error.set('');
  }

  calculateAge(fechaNacimiento: string): string {
    const birth = new Date(fechaNacimiento);
    const today = new Date();
    const years = today.getFullYear() - birth.getFullYear();
    const months = today.getMonth() - birth.getMonth();
    if (years > 0) return `${years} año(s)`;
    return `${months} mes(es)`;
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase();
    if (!term) { this.items.set(this.allItems); return; }
    this.items.set(
      this.allItems.filter(m =>
        m.nombre.toLowerCase().includes(term) ||
        m.especieNombre.toLowerCase().includes(term) ||
        m.clienteNombre.toLowerCase().includes(term)
      )
    );
  }

  // --- Vacuna Modal ---
  openVacunaModal(mascota: Mascota): void {
    this.vacunaMascota.set(mascota);
    this.resetVacunaForm();
    this.loadVacunas(mascota.id);
    this.showVacunaModal.set(true);
  }

  closeVacunaModal(): void {
    this.showVacunaModal.set(false);
    this.vacunaMascota.set(null);
    this.vacunas.set([]);
  }

  private loadVacunas(mascotaId: number): void {
    this.registroVacService.getByMascota(mascotaId).subscribe({
      next: (res) => { if (res.success) this.vacunas.set(res.data.content); }
    });
  }

  onSubmitVacuna(): void {
    const m = this.vacunaMascota();
    if (!m || !this.vacunaForm.vacunaId || !this.vacunaForm.fechaAplicacion || !this.vacunaForm.fechaVencimiento) {
      this.toast.error('Complete los campos obligatorios');
      return;
    }
    this.loading.set(true);
    const data: RegistroVacunacionRequest = {
      mascotaId: m.id,
      vacunaId: this.vacunaForm.vacunaId,
      fechaAplicacion: this.vacunaForm.fechaAplicacion,
      fechaVencimiento: this.vacunaForm.fechaVencimiento,
      lote: this.vacunaForm.lote,
      veterinario: this.vacunaForm.veterinario,
      observaciones: this.vacunaForm.observaciones
    };
    this.registroVacService.create(data).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Vacunación registrada');
        this.resetVacunaForm();
        this.loadVacunas(m.id);
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
            const m = this.vacunaMascota();
            if (m) this.loadVacunas(m.id);
          },
          error: () => this.toast.error('Error al eliminar')
        });
      }
    });
  }

  private resetVacunaForm(): void {
    this.vacunaForm = { vacunaId: null, fechaAplicacion: '', fechaVencimiento: '', lote: '', veterinario: '', observaciones: '' };
  }

  // --- Reserva Modal ---
  openReservaModal(mascota: Mascota): void {
    this.reservaMascota.set(mascota);
    this.editReservaId.set(null);
    this.resetReservaForm();
    this.showReservaModal.set(true);
  }

  openEditReservaModal(mascota: Mascota, reserva: Reserva): void {
    this.editReservaId.set(reserva.id);
    this.editReserva.set(reserva);
    this.reservaMascota.set(mascota);
    this.resetReservaForm();
    this.reservaForm.servicioId = reserva.servicioId;
    this.reservaForm.fechaReserva = typeof reserva.fechaReserva === 'string' ? reserva.fechaReserva.substring(0, 10) : (reserva.fechaReserva || '');
    this.reservaForm.horaInicio = reserva.horaInicio || '';
    this.reservaForm.horaFin = reserva.horaFin || '';
    this.reservaForm.notas = reserva.notas || '';
    const svc = this.servicios().find(s => s.id === reserva.servicioId);
    if (svc) this.selectedServicio.set(svc);
    this.showReservaModal.set(true);
    this.disponibilidadService.getByServicio(reserva.servicioId).subscribe({
      next: (res) => {
        if (res.success) {
          this.disponibilidades.set(res.data);
          if (reserva.fechaReserva) {
            const dayOfWeek = new Date(this.reservaForm.fechaReserva).getDay();
            this.filteredDisponibilidades.set(res.data.filter(d => d.diaSemana === dayOfWeek));
          }
        }
      }
    });
    this.reservaService.getById(reserva.id).subscribe({
      next: (res) => {
        if (res.success) {
          const r = res.data;
          this.reservaForm.servicioId = r.servicioId;
          this.reservaForm.fechaReserva = typeof r.fechaReserva === 'string' ? r.fechaReserva.substring(0, 10) : (r.fechaReserva || '');
          this.reservaForm.horaInicio = r.horaInicio || '';
          this.reservaForm.horaFin = r.horaFin || '';
          this.reservaForm.notas = r.notas || '';
          this.editReserva.set(r);
        }
      }
    });
  }

  closeReservaModal(): void {
    this.showReservaModal.set(false);
    this.reservaMascota.set(null);
    this.editReservaId.set(null);
    this.editReserva.set(null);
    this.disponibilidades.set([]);
    this.filteredDisponibilidades.set([]);
    this.selectedDisponibilidad.set(null);
    this.selectedServicio.set(null);
  }

  onServicioChangeReserva(): void {
    this.reservaForm.fechaReserva = '';
    this.reservaForm.horaInicio = '';
    this.reservaForm.horaFin = '';
    this.disponibilidades.set([]);
    this.filteredDisponibilidades.set([]);
    this.selectedDisponibilidad.set(null);
    this.selectedServicio.set(null);
    if (this.reservaForm.servicioId) {
      const svc = this.servicios().find(s => s.id === this.reservaForm.servicioId);
      if (svc) this.selectedServicio.set(svc);
      this.disponibilidadService.getByServicio(this.reservaForm.servicioId).subscribe({
        next: (res) => { if (res.success) this.disponibilidades.set(res.data); }
      });
    }
  }

  onDateChange(): void {
    this.reservaForm.horaInicio = '';
    this.reservaForm.horaFin = '';
    this.selectedDisponibilidad.set(null);
    if (this.reservaForm.fechaReserva) {
      const dayOfWeek = new Date(this.reservaForm.fechaReserva).getDay();
      const filtered = this.disponibilidades().filter(d => d.diaSemana === dayOfWeek);
      this.filteredDisponibilidades.set(filtered);
    } else {
      this.filteredDisponibilidades.set([]);
    }
  }

  selectDisponibilidad(d: Disponibilidad): void {
    this.selectedDisponibilidad.set(d);
    this.reservaForm.horaInicio = d.horaInicio;
    this.reservaForm.horaFin = d.horaFin;
  }

  formatTimeShort(time: any): string {
    if (!time) return '';
    if (Array.isArray(time)) {
      const [h, m] = time;
      return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
    }
    return String(time).length > 5 ? String(time).substring(0, 5) : String(time);
  }

  formatDate(date: any): string {
    if (!date) return '';
    if (Array.isArray(date)) {
      if (date.length >= 3) {
        const [y, m, d] = date;
        return `${String(d).padStart(2, '0')}/${String(m).padStart(2, '0')}/${y}`;
      }
      return date.join('/');
    }
    if (typeof date === 'string') {
      const parts = date.split('T')[0].split('-');
      if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
    }
    return String(date);
  }

  onSubmitReserva(): void {
    const m = this.reservaMascota();
    if (!m || !this.reservaForm.servicioId || !this.reservaForm.fechaReserva || !this.reservaForm.horaInicio || !this.reservaForm.horaFin) {
      this.toast.error('Complete todos los campos obligatorios');
      return;
    }
    this.loading.set(true);
    const data: ReservaRequest = {
      clienteId: m.clienteId,
      servicioId: this.reservaForm.servicioId,
      mascotaId: m.id,
      fechaReserva: this.reservaForm.fechaReserva,
      fechaInicio: this.reservaForm.fechaReserva,
      horaInicio: this.reservaForm.horaInicio,
      horaFin: this.reservaForm.horaFin,
      notas: this.reservaForm.notas,
      precioTotal: 0
    };
    const edit = this.editReserva();
    if (edit) {
      if (edit.proveedorId) data.proveedorId = edit.proveedorId;
      if ((edit as any).fechaFin) data.fechaFin = (edit as any).fechaFin;
    }
    const editId = this.editReservaId();
    const obs = editId ? this.reservaService.update(editId, data) : this.reservaService.create(data);
    obs.subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success(editId ? 'Reserva actualizada' : 'Reserva creada exitosamente');
        this.closeReservaModal();
        this.loadReservas();
      },
      error: (err) => {
        this.loading.set(false);
        this.toast.error(err.error?.message || 'Error al guardar reserva');
      }
    });
  }

  private resetReservaForm(): void {
    this.reservaForm.servicioId = null;
    this.reservaForm.fechaReserva = '';
    this.reservaForm.horaInicio = '';
    this.reservaForm.horaFin = '';
    this.reservaForm.notas = '';
    this.selectedServicio.set(null);
  }

  cancelReserva(): void {
    const id = this.editReservaId();
    const reserva = this.editReserva();
    if (!id || !reserva) return;
    Swal.fire({
      title: 'Cancelar reserva',
      text: `¿Estás seguro de cancelar la reserva ${reserva.codigo}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, cancelar',
      cancelButtonText: 'No'
    }).then((result) => {
      if (result.isConfirmed) {
        this.reservaService.cancelar(id).subscribe({
          next: () => {
            this.toast.success('Reserva cancelada');
            this.closeReservaModal();
            this.loadReservas();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al cancelar reserva')
        });
      }
    });
  }

  private loadReservas(): void {
    this.reservaService.getAll({ size: 200 }).subscribe({
      next: (res) => {
        if (res.success) {
          const map = new Map<number, Reserva[]>();
          for (const r of res.data.content) {
            const list = map.get(r.mascotaId) || [];
            list.push(r);
            map.set(r.mascotaId, list);
          }
          this.reservasPorMascota.set(map);
        }
      }
    });
  }
}
