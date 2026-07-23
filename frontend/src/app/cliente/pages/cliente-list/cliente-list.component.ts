import { Component, signal, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../../core/services/cliente.service';
import { PersonaService } from '../../../core/services/persona.service';
import { DireccionService } from '../../../core/services/direccion.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { RouterLink } from '@angular/router';
import { ClienteSummary } from '../../../core/models/cliente.model';
import { PersonaRequest } from '../../../core/models/persona.model';
import { DireccionRequest } from '../../../core/models/direccion.model';
import { Ciudad } from '../../../core/models/auth.model';
import { environment } from '../../../../environments/environment';
import { forkJoin, of, switchMap } from 'rxjs';
import Swal from 'sweetalert2';

declare const google: any;

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cliente-list.component.html'
})
export class ClienteListComponent implements OnInit, AfterViewInit, OnDestroy {
  items = signal<ClienteSummary[]>([]);
  ciudades = signal<Ciudad[]>([]);
  searchTerm = '';
  showForm = signal(false);
  loading = signal(false);
  editMode = signal(false);
  editingCliente = signal<ClienteSummary | null>(null);
  error = signal('');
  private allItems: ClienteSummary[] = [];
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
    longitud: null as number | null
  };

  constructor(
    private clienteService: ClienteService,
    private personaService: PersonaService,
    private direccionService: DireccionService,
    private auth: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadClientes();
    this.loadCiudades();
  }

  ngAfterViewInit(): void {}

  ngOnDestroy(): void {
    this.destroyMap();
  }

  loadClientes(): void {
    this.clienteService.getAll({ size: 100 }).subscribe({
      next: (res) => {
        if (res.success) {
          this.allItems = res.data.content;
          this.items.set(this.allItems);
        }
      }
    });
  }

  loadCiudades(): void {
    this.auth.getCiudades().subscribe({
      next: (data) => this.ciudades.set(data)
    });
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase();
    if (!term) {
      this.items.set(this.allItems);
      return;
    }
    this.items.set(
      this.allItems.filter(c =>
        c.nombreCompleto?.toLowerCase().includes(term) ||
        c.ci?.includes(term)
      )
    );
  }

  openForm(): void {
    this.resetForm();
    this.editMode.set(false);
    this.editingCliente.set(null);
    this.showForm.set(true);
    setTimeout(() => this.initMap(), 100);
  }

  openEdit(cliente: ClienteSummary): void {
    this.editMode.set(true);
    this.editingCliente.set(cliente);
    this.form = {
      username: cliente.username || '',
      password: '',
      nombre: cliente.nombre || '',
      primerApellido: cliente.primerApellido || '',
      segundoApellido: cliente.segundoApellido || '',
      ci: cliente.ci || '',
      telefono: cliente.telefono || '',
      email: cliente.email || '',
      fechaNacimiento: this.parseDate(cliente.fechaNacimiento),
      genero: cliente.genero || 'M',
      calle: cliente.calle || '',
      numero: cliente.numero || '',
      referencia: cliente.referencia || '',
      ciudadId: cliente.ciudadId || 1,
      latitud: cliente.latitud ?? null,
      longitud: cliente.longitud ?? null
    };
    this.showForm.set(true);
    setTimeout(() => this.initMap(), 100);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.error.set('');
    this.editMode.set(false);
    this.editingCliente.set(null);
    this.destroyMap();
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

  onSubmit(): void {
    if (this.editMode()) {
      this.onUpdate();
    } else {
      this.onRegister();
    }
  }

  private onRegister(): void {
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
      fechaNacimiento: this.form.fechaNacimiento,
      genero: this.form.genero,
      calle: this.form.calle,
      ciudadId: Number(this.form.ciudadId),
      latitud: this.form.latitud,
      longitud: this.form.longitud
    };
    if (this.form.segundoApellido) body.segundoApellido = this.form.segundoApellido;
    if (this.form.numero) body.numero = this.form.numero;
    if (this.form.referencia) body.referencia = this.form.referencia;

    this.auth.register(body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Cliente registrado exitosamente');
        this.showForm.set(false);
        this.loadClientes();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al registrar cliente';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  private onUpdate(): void {
    this.error.set('');
    this.loading.set(true);

    const cliente = this.editingCliente()!;
    const direccionData: DireccionRequest = {
      calle: this.form.calle,
      numero: this.form.numero || '',
      latitud: this.form.latitud ?? undefined,
      longitud: this.form.longitud ?? undefined,
      referencia: this.form.referencia || '',
      ciudadId: Number(this.form.ciudadId)
    };

    const personaData: PersonaRequest = {
      nombre: this.form.nombre,
      primerApellido: this.form.primerApellido,
      segundoApellido: this.form.segundoApellido || '',
      ci: this.form.ci,
      telefono: this.form.telefono,
      email: this.form.email,
      fechaNacimiento: this.form.fechaNacimiento,
      genero: this.form.genero,
      direccionId: cliente.direccionId || undefined
    };

    const save$ = cliente.direccionId
      ? this.direccionService.update(cliente.direccionId, direccionData).pipe(
          switchMap(res => {
            personaData.direccionId = res.data.id;
            return this.personaService.update(cliente.personaId, personaData);
          })
        )
      : this.direccionService.create(direccionData).pipe(
          switchMap(res => {
            personaData.direccionId = res.data.id;
            return this.personaService.update(cliente.personaId, personaData);
          })
        );

    save$.subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Cliente actualizado exitosamente');
        this.showForm.set(false);
        this.loadClientes();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al actualizar cliente';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  deleteCliente(cliente: ClienteSummary): void {
    Swal.fire({
      title: 'Eliminar cliente',
      text: `¿Estás seguro de eliminar a ${cliente.nombreCompleto}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.clienteService.delete(cliente.id).subscribe({
          next: () => {
            this.toast.success('Cliente eliminado exitosamente');
            this.loadClientes();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  private initMap(): void {
    const mapEl = document.getElementById('register-map');
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
        title: 'Ubicacion del cliente'
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
      referencia: '', ciudadId: 1, latitud: null, longitud: null
    };
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
