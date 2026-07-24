import { Component, signal, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProveedorService } from '../../../core/services/proveedor.service';
import { ServicioService, Servicio } from '../../../core/services/servicio.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { Proveedor, DisponibilidadItem } from '../../../core/models/proveedor.model';
import { Ciudad } from '../../../core/models/auth.model';
import { environment } from '../../../../environments/environment';
import Swal from 'sweetalert2';

declare const google: any;

@Component({
  selector: 'app-proveedor-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './proveedor-list.component.html'
})
export class ProveedorListComponent implements OnInit, AfterViewInit, OnDestroy {
  items = signal<Proveedor[]>([]);
  servicios = signal<Servicio[]>([]);
  ciudades = signal<Ciudad[]>([]);
  searchTerm = '';
  showForm = signal(false);
  loading = signal(false);
  editMode = signal(false);
  editingProveedor = signal<Proveedor | null>(null);
  error = signal('');
  dispDias = [0, 1, 2, 3, 4, 5, 6];
  dispDiaLabels = ['Domingo', 'Lunes', 'Martes', 'Miercoles', 'Jueves', 'Viernes', 'Sabado'];
  private allItems: Proveedor[] = [];
  private map: any = null;
  private marker: any = null;

  form = {
    username: '',
    password: '',
    nombre: '',
    primerApellido: '',
    segundoApellido: '',
    ci: '',
    telefono: '',
    email: '',
    fechaNacimiento: '',
    genero: 'M',
    calle: '',
    numero: '',
    referencia: '',
    ciudadId: 1,
    latitud: null as number | null,
    longitud: null as number | null,
    radioCoberturaKm: 10,
    empresa: '',
    descripcion: '',
    servicioIds: [] as number[],
    disponibilidades: [] as DisponibilidadItem[]
  };

  newDisp = {
    servicioId: null as number | null,
    diaSemana: 1,
    horaInicio: '08:00',
    horaFin: '12:00'
  };

  constructor(
    private proveedorService: ProveedorService,
    private servicioService: ServicioService,
    private auth: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadProveedores();
    this.loadServicios();
    this.loadCiudades();
  }

  ngAfterViewInit(): void {}

  ngOnDestroy(): void {
    this.destroyMap();
  }

  loadProveedores(): void {
    this.proveedorService.getAll({ size: 100 }).subscribe({
      next: (res) => {
        if (res.success) {
          this.allItems = res.data.content;
          this.items.set(this.allItems);
        }
      }
    });
  }

  loadServicios(): void {
    this.servicioService.getAll({ size: 100 }).subscribe({
      next: (res) => {
        if (res.success) {
          this.servicios.set(res.data.content);
        }
      }
    });
  }

  loadCiudades(): void {
    this.auth.getCiudades().subscribe({
      next: (data) => this.ciudades.set(data)
    });
  }

  onCiudadChange(): void {
    const ciudad = this.ciudades().find(c => c.id === Number(this.form.ciudadId));
    if (ciudad && this.map) {
      const center = { lat: ciudad.latitud || -16.5, lng: ciudad.longitud || -68.15 };
      this.map.setCenter(center);
      this.map.setZoom(13);
      if (this.marker) {
        this.marker.setPosition(center);
      }
      this.form.latitud = center.lat;
      this.form.longitud = center.lng;
    }
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase();
    if (!term) {
      this.items.set(this.allItems);
      return;
    }
    this.items.set(
      this.allItems.filter(p =>
        p.personaNombre?.toLowerCase().includes(term) ||
        p.empresa?.toLowerCase().includes(term) ||
        p.descripcion?.toLowerCase().includes(term) ||
        p.especialidades?.some(e => e.toLowerCase().includes(term))
      )
    );
  }

  openForm(): void {
    this.resetForm();
    this.editMode.set(false);
    this.editingProveedor.set(null);
    this.showForm.set(true);
    setTimeout(() => this.initMap(), 100);
  }

  openEdit(proveedor: Proveedor): void {
    this.editMode.set(true);
    this.proveedorService.getById(proveedor.id).subscribe({
      next: (res) => {
        if (res.success) {
          const full = res.data;
          this.editingProveedor.set(full);
          this.form = {
            username: '',
            password: '',
            nombre: full.nombre || '',
            primerApellido: full.primerApellido || '',
            segundoApellido: full.segundoApellido || '',
            ci: full.ci || '',
            telefono: full.personaTelefono || '',
            email: full.personaEmail || '',
            fechaNacimiento: this.parseDate(full.fechaNacimiento),
            genero: full.genero || 'M',
            calle: full.calle || '',
            numero: full.numero || '',
            referencia: full.referencia || '',
            ciudadId: full.ciudadId || 1,
            latitud: full.latitud ?? null,
            longitud: full.longitud ?? null,
            radioCoberturaKm: full.radioCoberturaKm || 10,
            empresa: full.empresa || '',
            descripcion: full.descripcion || '',
            servicioIds: full.servicioIds ? [...full.servicioIds] : [],
            disponibilidades: full.disponibilidades ? full.disponibilidades.map(d => ({
              servicioId: d.servicioId,
              diaSemana: d.diaSemana,
              horaInicio: d.horaInicio?.substring(0, 5) || '08:00',
              horaFin: d.horaFin?.substring(0, 5) || '12:00'
            })) : []
          };
          this.showForm.set(true);
          setTimeout(() => this.initMap(), 100);
        }
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Error al cargar proveedor');
      }
    });
  }

  closeForm(): void {
    this.showForm.set(false);
    this.error.set('');
    this.editMode.set(false);
    this.editingProveedor.set(null);
    this.destroyMap();
  }

  toggleServicio(servicioId: number): void {
    const idx = this.form.servicioIds.indexOf(servicioId);
    if (idx >= 0) {
      this.form.servicioIds.splice(idx, 1);
    } else {
      this.form.servicioIds.push(servicioId);
    }
  }

  addDisponibilidad(): void {
    if (!this.newDisp.servicioId) return;
    this.form.disponibilidades.push({
      servicioId: this.newDisp.servicioId,
      diaSemana: this.newDisp.diaSemana,
      horaInicio: this.newDisp.horaInicio,
      horaFin: this.newDisp.horaFin
    });
    this.newDisp = { servicioId: null, diaSemana: 1, horaInicio: '08:00', horaFin: '12:00' };
  }

  removeDisponibilidad(index: number): void {
    this.form.disponibilidades.splice(index, 1);
  }

  getServicioNombre(servicioId: number): string {
    const s = this.servicios().find(s => s.id === servicioId);
    return s ? s.nombre : '';
  }

  onSubmit(): void {
    if (this.editMode()) {
      this.onUpdate();
    } else {
      this.onCreate();
    }
  }

  private onCreate(): void {
    this.error.set('');
    this.loading.set(true);

    const body: any = {
      username: this.form.username,
      password: this.form.password,
      nombre: this.form.nombre,
      primerApellido: this.form.primerApellido,
      ci: this.form.ci,
      telefono: this.form.telefono,
      email: this.form.email,
      fechaNacimiento: this.form.fechaNacimiento || null,
      genero: this.form.genero,
      calle: this.form.calle,
      ciudadId: Number(this.form.ciudadId),
      latitud: this.form.latitud,
      longitud: this.form.longitud,
      radioCoberturaKm: this.form.radioCoberturaKm,
      empresa: this.form.empresa,
      descripcion: this.form.descripcion,
      servicioIds: this.form.servicioIds,
      disponibilidades: this.form.disponibilidades
    };
    if (this.form.segundoApellido) body.segundoApellido = this.form.segundoApellido;
    if (this.form.numero) body.numero = this.form.numero;
    if (this.form.referencia) body.referencia = this.form.referencia;

    this.proveedorService.createFull(body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Proveedor registrado exitosamente');
        this.showForm.set(false);
        this.loadProveedores();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al registrar proveedor';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  private onUpdate(): void {
    this.error.set('');
    this.loading.set(true);

    const body: any = {
      nombre: this.form.nombre,
      primerApellido: this.form.primerApellido,
      ci: this.form.ci,
      telefono: this.form.telefono,
      email: this.form.email,
      calle: this.form.calle,
      ciudadId: Number(this.form.ciudadId),
      latitud: this.form.latitud,
      longitud: this.form.longitud,
      radioCoberturaKm: this.form.radioCoberturaKm,
      empresa: this.form.empresa,
      descripcion: this.form.descripcion,
      servicioIds: this.form.servicioIds,
      disponibilidades: this.form.disponibilidades
    };
    if (this.form.segundoApellido) body.segundoApellido = this.form.segundoApellido;
    if (this.form.numero) body.numero = this.form.numero;
    if (this.form.referencia) body.referencia = this.form.referencia;

    this.proveedorService.updateFull(this.editingProveedor()!.id, body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Proveedor actualizado exitosamente');
        this.showForm.set(false);
        this.loadProveedores();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al actualizar proveedor';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  deleteProveedor(proveedor: Proveedor): void {
    this.proveedorService.hasReservas(proveedor.id).subscribe({
      next: (res) => {
        if (res.data.hasReservas) {
          Swal.fire({
            title: 'No se puede eliminar',
            text: `El proveedor ${proveedor.personaNombre} tiene reservas asociadas.`,
            icon: 'warning',
            confirmButtonColor: '#6366f1',
            confirmButtonText: 'Entendido'
          });
        } else {
          Swal.fire({
            title: 'Eliminar proveedor',
            text: `¿Estás seguro de eliminar a ${proveedor.personaNombre}?`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc2626',
            cancelButtonColor: '#6b7280',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
          }).then((result) => {
            if (result.isConfirmed) {
              this.proveedorService.delete(proveedor.id).subscribe({
                next: () => {
                  this.toast.success('Proveedor eliminado exitosamente');
                  this.loadProveedores();
                },
                error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
              });
            }
          });
        }
      }
    });
  }

  private initMap(): void {
    const mapEl = document.getElementById('proveedor-map');
    if (!mapEl || this.map) return;

    const loadMap = () => {
      const center = this.form.latitud && this.form.longitud
        ? { lat: this.form.latitud, lng: this.form.longitud }
        : { lat: -16.5, lng: -68.15 };

      this.map = new google.maps.Map(mapEl, {
        center,
        zoom: this.form.latitud ? 15 : 12,
        mapTypeControl: false
      });

      this.marker = new google.maps.Marker({
        position: center,
        map: this.map,
        draggable: true,
        title: 'Ubicacion del proveedor'
      });

      this.marker.addListener('dragend', () => {
        const pos = this.marker.getPosition();
        this.form.latitud = pos.lat();
        this.form.longitud = pos.lng();
      });

      this.map.addListener('click', (e: any) => {
        this.marker.setPosition(e.latLng);
        this.form.latitud = e.latLng.lat();
        this.form.longitud = e.latLng.lng();
      });
    };

    if ((window as any).google && (window as any).google.maps) {
      loadMap();
    } else {
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}`;
      script.async = true;
      script.defer = true;
      script.onload = () => loadMap();
      document.head.appendChild(script);
    }
  }

  private destroyMap(): void {
    this.map = null;
    this.marker = null;
  }

  private resetForm(): void {
    this.form = {
      username: '', password: '', nombre: '', primerApellido: '',
      segundoApellido: '', ci: '', telefono: '', email: '',
      fechaNacimiento: '', genero: 'M', calle: '', numero: '',
      referencia: '', ciudadId: 1, latitud: null, longitud: null,
      radioCoberturaKm: 10, empresa: '', descripcion: '', servicioIds: [],
      disponibilidades: []
    };
    this.newDisp = { servicioId: null, diaSemana: 1, horaInicio: '08:00', horaFin: '12:00' };
  }

  private parseDate(value: any): string {
    if (!value) return '';
    if (typeof value === 'string') return value.split('T')[0];
    if (Array.isArray(value)) {
      const [y, m, d] = value;
      return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
    }
    if (typeof value === 'object' && value.year) {
      return `${value.year}-${String(value.monthValue || value.month).padStart(2, '0')}-${String(value.dayOfMonth || value.day).padStart(2, '0')}`;
    }
    return new Date(value).toISOString().split('T')[0];
  }
}
