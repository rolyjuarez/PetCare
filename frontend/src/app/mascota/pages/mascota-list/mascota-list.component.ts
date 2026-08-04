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
import { ProveedorService } from '../../../core/services/proveedor.service';
import { Proveedor } from '../../../core/models/proveedor.model';
import { ReservaService, Reserva, ReservaRequest, DisponibilidadSlots, SlotDisponible, ModalidadInfo } from '../../../core/services/reserva.service';
import { PagoService } from '../../../core/services/pago.service';
import { PagoResponse, ProcesarPagoRequest } from '../../../core/models/pago.model';
import { modalidadLabel } from '../../../core/models/proveedor.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-mascota-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './mascota-list.component.html'
})
export class MascotaListComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @ViewChild('certificadoInput') certificadoInput!: ElementRef<HTMLInputElement>;

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
  proveedores = signal<Proveedor[]>([]);
  slotsPorFecha = signal<DisponibilidadSlots[]>([]);
  selectedFecha = signal<string | null>(null);
  selectedSlot = signal<SlotDisponible | null>(null);
  selectedProveedor = signal<Proveedor | null>(null);
  selectedServicio = signal<Servicio | null>(null);
  reservaForm = {
    proveedorId: null as number | null,
    servicioId: null as number | null,
    fechaReserva: '',
    horaInicio: '',
    horaFin: '',
    notas: '',
    registroVacunacionId: null as number | null,
    modalidadEntrega: '',
    latitud: null as number | null,
    longitud: null as number | null,
    direccionReferencia: ''
  };

  modalidadesDisponibles = signal<ModalidadInfo[]>([]);
  selectedModalidad = signal<ModalidadInfo | null>(null);

  // Pago modal
  showPagoModal = signal(false);
  pagoReserva = signal<Reserva | null>(null);
  pago = signal<PagoResponse | null>(null);
  pagoLoading = signal(false);
  tarjetaForm = {
    numero: '',
    titular: '',
    expira: '',
    cvv: ''
  };

  reservasPorMascota = signal<Map<number, Reserva[]>>(new Map());

  editReservaId = signal<number | null>(null);
  editReserva = signal<Reserva | null>(null);

  // Certificado de vacunación en la reserva
  requiereCertificado = signal(false);
  registrosMascota = signal<RegistroVacunacion[]>([]);
  certificadoRegistroId = signal<number | null>(null);
  modoNuevoRegistro = signal(false);
  nuevoRegistroForm = {
    vacunaId: null as number | null,
    fechaAplicacion: '',
    fechaVencimiento: ''
  };
  certificadoFile = signal<File | null>(null);
  certificadoPreview = signal<string | null>(null);

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
    private proveedorService: ProveedorService,
    private reservaService: ReservaService,
    private pagoService: PagoService
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
    this.proveedorService.getAll({ size: 100 }).subscribe({
      next: (res) => { if (res.success) this.proveedores.set(res.data.content.filter(p => p.verificado !== false)); }
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
    this.reservaForm.proveedorId = reserva.proveedorId ?? null;
    this.reservaForm.servicioId = reserva.servicioId;
    this.reservaForm.fechaReserva = typeof reserva.fechaReserva === 'string' ? reserva.fechaReserva.substring(0, 10) : (reserva.fechaReserva || '');
    this.reservaForm.horaInicio = reserva.horaInicio || '';
    this.reservaForm.horaFin = reserva.horaFin || '';
    this.reservaForm.notas = reserva.notas || '';
    this.reservaForm.modalidadEntrega = reserva.modalidadEntrega || '';
    this.reservaForm.latitud = reserva.latitud ?? null;
    this.reservaForm.longitud = reserva.longitud ?? null;
    this.reservaForm.direccionReferencia = reserva.direccionReferencia || '';
    const prov = this.proveedores().find(p => p.id === reserva.proveedorId);
    if (prov) this.selectedProveedor.set(prov);
    const svc = this.servicios().find(s => s.id === reserva.servicioId);
    if (svc) this.selectedServicio.set(svc);
    this.selectedFecha.set(this.reservaForm.fechaReserva || null);
    this.showReservaModal.set(true);
    this.reservaService.getById(reserva.id).subscribe({
      next: (res) => {
        if (res.success) {
          const r = res.data;
          this.reservaForm.servicioId = r.servicioId;
          this.reservaForm.fechaReserva = typeof r.fechaReserva === 'string' ? r.fechaReserva.substring(0, 10) : (r.fechaReserva || '');
          this.reservaForm.horaInicio = r.horaInicio || '';
          this.reservaForm.horaFin = r.horaFin || '';
          this.reservaForm.notas = r.notas || '';
          this.reservaForm.registroVacunacionId = r.registroVacunacionId ?? null;
          this.reservaForm.modalidadEntrega = r.modalidadEntrega || '';
          this.reservaForm.latitud = r.latitud ?? null;
          this.reservaForm.longitud = r.longitud ?? null;
          this.reservaForm.direccionReferencia = r.direccionReferencia || '';
          this.editReserva.set(r);
          if (r.proveedorId) {
            this.reservaForm.proveedorId = r.proveedorId;
            this.selectedProveedor.set(this.proveedores().find(p => p.id === r.proveedorId) ?? null);
            this.loadSlots();
          }
        }
      }
    });
  }

  closeReservaModal(): void {
    this.showReservaModal.set(false);
    this.reservaMascota.set(null);
    this.editReservaId.set(null);
    this.editReserva.set(null);
    this.slotsPorFecha.set([]);
    this.selectedFecha.set(null);
    this.selectedSlot.set(null);
    this.selectedProveedor.set(null);
    this.selectedServicio.set(null);
    this.modalidadesDisponibles.set([]);
    this.selectedModalidad.set(null);
    this.resetCertificadoState();
  }

  onProveedorChangeReserva(): void {
    this.reservaForm.servicioId = null;
    this.reservaForm.fechaReserva = '';
    this.reservaForm.horaInicio = '';
    this.reservaForm.horaFin = '';
    this.reservaForm.modalidadEntrega = '';
    this.slotsPorFecha.set([]);
    this.selectedFecha.set(null);
    this.selectedSlot.set(null);
    this.selectedServicio.set(null);
    this.modalidadesDisponibles.set([]);
    this.selectedModalidad.set(null);
    this.selectedProveedor.set(this.proveedores().find(p => p.id === this.reservaForm.proveedorId) ?? null);
  }

  serviciosDeProveedor(): Servicio[] {
    const prov = this.proveedores().find(p => p.id === this.reservaForm.proveedorId);
    if (!prov || !prov.servicioIds || prov.servicioIds.length === 0) {
      return this.servicios();
    }
    return this.servicios().filter(s => prov.servicioIds.includes(s.id));
  }

  onServicioChangeReserva(): void {
    this.reservaForm.fechaReserva = '';
    this.reservaForm.horaInicio = '';
    this.reservaForm.horaFin = '';
    this.reservaForm.modalidadEntrega = '';
    this.slotsPorFecha.set([]);
    this.selectedFecha.set(null);
    this.selectedSlot.set(null);
    this.selectedServicio.set(null);
    this.modalidadesDisponibles.set([]);
    this.selectedModalidad.set(null);
    this.resetCertificadoState();
    if (this.reservaForm.servicioId) {
      const svc = this.servicios().find(s => s.id === this.reservaForm.servicioId);
      if (svc) this.selectedServicio.set(svc);
    }
    this.loadSlots();
  }

  selectModalidad(m: ModalidadInfo): void {
    this.selectedModalidad.set(m);
    this.reservaForm.modalidadEntrega = m.modalidad;
  }

  requiereCoordenadas(): boolean {
    return this.reservaForm.modalidadEntrega === 'DOMICILIO' || this.reservaForm.modalidadEntrega === 'RECOGIDA_ENTREGA';
  }

  loadSlots(): void {
    const proveedorId = this.reservaForm.proveedorId;
    const servicioId = this.reservaForm.servicioId;
    if (!proveedorId || !servicioId) {
      this.slotsPorFecha.set([]);
      return;
    }
    const desde = this.toDateStr(new Date());
    const hasta = this.addDaysStr(new Date(), 13);
    this.reservaService.getSlots(proveedorId, servicioId, desde, hasta, this.editReservaId() ?? undefined).subscribe({
      next: (res) => {
        if (res.success) {
          this.slotsPorFecha.set(res.data);
          this.requiereCertificado.set(res.data.some(s => s.requiereCertificado));
          const mods = res.data.length > 0 ? res.data[0].modalidades : [];
          this.modalidadesDisponibles.set(mods || []);
          if (this.editReservaId() && this.reservaForm.modalidadEntrega) {
            this.selectedModalidad.set((mods || []).find(m => m.modalidad === this.reservaForm.modalidadEntrega) ?? null);
          }
          if (this.requiereCertificado()) {
            this.loadRegistrosParaCertificado();
          }
          if (this.editReservaId() && this.reservaForm.fechaReserva) {
            this.selectedFecha.set(this.reservaForm.fechaReserva);
          }
        }
      }
    });
  }

  availableFechas(): string[] {
    return this.slotsPorFecha().map(s => s.fecha);
  }

  slotsDeFecha(): SlotDisponible[] {
    const fecha = this.selectedFecha();
    if (!fecha) return [];
    const item = this.slotsPorFecha().find(s => s.fecha === fecha);
    return item ? item.slots : [];
  }

  selectFecha(fecha: string): void {
    this.reservaForm.fechaReserva = fecha;
    this.selectedFecha.set(fecha);
    this.reservaForm.horaInicio = '';
    this.reservaForm.horaFin = '';
    this.selectedSlot.set(null);
  }

  selectSlot(slot: SlotDisponible): void {
    this.selectedSlot.set(slot);
    this.reservaForm.horaInicio = slot.horaInicio;
    this.reservaForm.horaFin = slot.horaFin;
  }

  // --- Certificado de vacunación en reserva ---
  private resetCertificadoState(): void {
    this.requiereCertificado.set(false);
    this.registrosMascota.set([]);
    this.certificadoRegistroId.set(null);
    this.modoNuevoRegistro.set(false);
    this.reservaForm.registroVacunacionId = null;
    this.nuevoRegistroForm = { vacunaId: null, fechaAplicacion: '', fechaVencimiento: '' };
    this.certificadoFile.set(null);
    this.certificadoPreview.set(null);
  }

  private loadRegistrosParaCertificado(): void {
    const m = this.reservaMascota();
    if (!m) return;
    this.registroVacService.getByMascota(m.id).subscribe({
      next: (res) => {
        if (res.success) {
          this.registrosMascota.set(res.data.content.filter(r => !!r.certificadoUrl));
          if (this.reservaForm.registroVacunacionId) {
            this.certificadoRegistroId.set(this.reservaForm.registroVacunacionId);
          }
        }
      }
    });
  }

  seleccionarRegistro(vacId: number): void {
    this.certificadoRegistroId.set(vacId);
    this.reservaForm.registroVacunacionId = vacId;
    this.modoNuevoRegistro.set(false);
  }

  activarNuevoRegistro(): void {
    this.modoNuevoRegistro.set(true);
    this.certificadoRegistroId.set(null);
    this.reservaForm.registroVacunacionId = null;
  }

  onCertificadoFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.certificadoFile.set(file);
      const reader = new FileReader();
      reader.onload = () => this.certificadoPreview.set(reader.result as string);
      reader.readAsDataURL(file);
    }
  }

  quitarCertificado(): void {
    this.certificadoFile.set(null);
    this.certificadoPreview.set(null);
    if (this.certificadoInput) this.certificadoInput.nativeElement.value = '';
  }

  crearRegistroConCertificado(): void {
    const m = this.reservaMascota();
    if (!m) {
      this.toast.error('No se pudo identificar la mascota');
      return;
    }
    if (!this.nuevoRegistroForm.vacunaId || !this.nuevoRegistroForm.fechaAplicacion || !this.nuevoRegistroForm.fechaVencimiento) {
      this.toast.error('Complete vacuna, fecha de aplicación y vencimiento');
      return;
    }
    const file = this.certificadoFile();
    if (!file) {
      this.toast.error('Debe adjuntar el certificado de vacunación');
      return;
    }
    this.loading.set(true);
    this.uploadService.uploadCertificado(file).subscribe({
      next: (uploadRes) => {
        const data: RegistroVacunacionRequest = {
          mascotaId: m.id,
          vacunaId: this.nuevoRegistroForm.vacunaId!,
          fechaAplicacion: this.nuevoRegistroForm.fechaAplicacion,
          fechaVencimiento: this.nuevoRegistroForm.fechaVencimiento,
          certificadoUrl: uploadRes.url
        };
        this.registroVacService.create(data).subscribe({
          next: (res) => {
            this.loading.set(false);
            if (res.success) {
              this.toast.success('Certificado registrado');
              this.seleccionarRegistro(res.data.id);
              this.loadRegistrosParaCertificado();
            }
          },
          error: (err) => {
            this.loading.set(false);
            this.toast.error(err.error?.message || 'Error al guardar el certificado');
          }
        });
      },
      error: () => {
        this.loading.set(false);
        this.toast.error('Error al subir el certificado');
      }
    });
  }

  registroSeleccionado(vac: RegistroVacunacion): boolean {
    return this.certificadoRegistroId() === vac.id;
  }

  formatFechaCorta(fecha: string): string {
    if (!fecha) return '';
    const parts = fecha.split('-');
    if (parts.length === 3) {
      const d = new Date(+parts[0], +parts[1] - 1, +parts[2]);
      return d.toLocaleDateString('es', { weekday: 'short', day: '2-digit', month: 'short' });
    }
    return fecha;
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
    if (!m || !this.reservaForm.proveedorId || !this.reservaForm.servicioId || !this.reservaForm.fechaReserva || !this.reservaForm.horaInicio || !this.reservaForm.horaFin) {
      this.toast.error('Seleccione proveedor, servicio, fecha y horario');
      return;
    }
    if (!this.reservaForm.modalidadEntrega) {
      this.toast.error('Seleccione una modalidad de entrega');
      return;
    }
    if (this.requiereCoordenadas()) {
      if (this.reservaForm.latitud == null || this.reservaForm.longitud == null) {
        this.toast.error('Para esta modalidad debe indicar la latitud y longitud del lugar');
        return;
      }
    }
    if (this.requiereCertificado() && !this.reservaForm.registroVacunacionId) {
      this.toast.error('Este servicio requiere un certificado de vacunación');
      return;
    }
    this.loading.set(true);
    const svc = this.servicios().find(s => s.id === this.reservaForm.servicioId);
    const mod = this.selectedModalidad();
    const precioTotal = (svc ? svc.precioBase : 0) + (mod ? mod.costoAdicional : 0);
    const data: ReservaRequest = {
      clienteId: m.clienteId,
      proveedorId: this.reservaForm.proveedorId,
      servicioId: this.reservaForm.servicioId,
      mascotaId: m.id,
      fechaReserva: this.reservaForm.fechaReserva,
      fechaInicio: this.reservaForm.fechaReserva,
      horaInicio: this.reservaForm.horaInicio,
      horaFin: this.reservaForm.horaFin,
      notas: this.reservaForm.notas,
      precioTotal,
      registroVacunacionId: this.reservaForm.registroVacunacionId ?? undefined,
      modalidadEntrega: this.reservaForm.modalidadEntrega,
      latitud: this.reservaForm.latitud ?? undefined,
      longitud: this.reservaForm.longitud ?? undefined,
      direccionReferencia: this.reservaForm.direccionReferencia || undefined
    };
    const edit = this.editReserva();
    if (edit && (edit as any).fechaFin) data.fechaFin = (edit as any).fechaFin;
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

  private toDateStr(date: Date): string {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }

  private addDaysStr(date: Date, days: number): string {
    const copy = new Date(date);
    copy.setDate(copy.getDate() + days);
    return this.toDateStr(copy);
  }

  private resetReservaForm(): void {
    this.reservaForm.proveedorId = null;
    this.reservaForm.servicioId = null;
    this.reservaForm.fechaReserva = '';
    this.reservaForm.horaInicio = '';
    this.reservaForm.horaFin = '';
    this.reservaForm.notas = '';
    this.reservaForm.registroVacunacionId = null;
    this.reservaForm.modalidadEntrega = '';
    this.reservaForm.latitud = null;
    this.reservaForm.longitud = null;
    this.reservaForm.direccionReferencia = '';
    this.slotsPorFecha.set([]);
    this.selectedFecha.set(null);
    this.selectedSlot.set(null);
    this.selectedProveedor.set(null);
    this.selectedServicio.set(null);
    this.modalidadesDisponibles.set([]);
    this.selectedModalidad.set(null);
    this.resetCertificadoState();
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

  modalidadLabel(m: string): string {
    return modalidadLabel(m);
  }

  // --- Pago modal ---
  abrirPago(mascota: Mascota, reserva: Reserva): void {
    this.pagoReserva.set(reserva);
    this.pago.set(null);
    this.tarjetaForm = { numero: '', titular: '', expira: '', cvv: '' };
    this.showPagoModal.set(true);
    this.cargarPago();
  }

  cargarPago(): void {
    const reserva = this.pagoReserva();
    if (!reserva) return;
    this.pagoLoading.set(true);
    this.pagoService.getByReserva(reserva.id).subscribe({
      next: (res) => {
        this.pagoLoading.set(false);
        if (res.success) this.pago.set(res.data);
      },
      error: () => this.pagoLoading.set(false)
    });
  }

  closePagoModal(): void {
    this.showPagoModal.set(false);
    this.pagoReserva.set(null);
    this.pago.set(null);
  }

  procesarPago(): void {
    const pago = this.pago();
    if (!pago) return;
    if (!this.tarjetaForm.numero.trim() || !this.tarjetaForm.titular.trim()
        || !this.tarjetaForm.expira.trim() || !this.tarjetaForm.cvv.trim()) {
      this.toast.error('Complete los datos de la tarjeta');
      return;
    }
    this.pagoLoading.set(true);
    const data: ProcesarPagoRequest = {
      metodoPago: 'TARJETA',
      tarjeta: {
        numero: this.tarjetaForm.numero.trim(),
        titular: this.tarjetaForm.titular.trim(),
        expira: this.tarjetaForm.expira.trim(),
        cvv: this.tarjetaForm.cvv.trim()
      }
    };
    this.pagoService.procesar(pago.id, data).subscribe({
      next: (res) => {
        this.pagoLoading.set(false);
        if (res.success) {
          this.toast.success('Pago procesado exitosamente');
          this.pago.set(res.data);
          this.loadReservas();
        }
      },
      error: (err) => {
        this.pagoLoading.set(false);
        this.toast.error(err.error?.message || 'Error al procesar el pago');
      }
    });
  }

  reembolsarPago(): void {
    const pago = this.pago();
    if (!pago) return;
    Swal.fire({
      title: 'Reembolsar pago',
      text: `¿Estás seguro de reembolsar el pago de la reserva ${pago.codigoReserva}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, reembolsar',
      cancelButtonText: 'No'
    }).then((result) => {
      if (result.isConfirmed) {
        this.pagoService.reembolsar(pago.id).subscribe({
          next: (res) => {
            if (res.success) {
              this.toast.success('Pago reembolsado');
              this.pago.set(res.data);
              this.loadReservas();
            }
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al reembolsar el pago')
        });
      }
    });
  }

  formatMonto(v: number | undefined | null): string {
    return v == null ? '0' : String(v);
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
