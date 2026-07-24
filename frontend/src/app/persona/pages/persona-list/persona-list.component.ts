import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PersonaService } from '../../../core/services/persona.service';
import { ToastService } from '../../../core/services/toast.service';
import { PersonaSummary } from '../../../core/models/persona.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-persona-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './persona-list.component.html'
})
export class PersonaListComponent implements OnInit {
  items = signal<PersonaSummary[]>([]);
  searchTerm = '';
  showForm = signal(false);
  loading = signal(false);
  editMode = signal(false);
  editingId = signal<number | null>(null);
  error = signal('');
  private allItems: PersonaSummary[] = [];

  form = {
    nombre: '',
    primerApellido: '',
    segundoApellido: '',
    ci: '',
    telefono: '',
    email: '',
    fechaNacimiento: '',
    genero: 'M'
  };

  constructor(
    private personaService: PersonaService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadPersonas();
  }

  loadPersonas(): void {
    this.personaService.getAll({ size: 100 }).subscribe({
      next: (res) => {
        if (res.success) {
          this.allItems = res.data.content;
          this.items.set(this.allItems);
        }
      }
    });
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase();
    if (!term) {
      this.items.set(this.allItems);
      return;
    }
    this.items.set(
      this.allItems.filter(p =>
        p.nombreCompleto.toLowerCase().includes(term) ||
        p.ci.toLowerCase().includes(term) ||
        p.email.toLowerCase().includes(term)
      )
    );
  }

  openForm(): void {
    this.resetForm();
    this.editMode.set(false);
    this.editingId.set(null);
    this.showForm.set(true);
  }

  openEdit(item: PersonaSummary): void {
    this.editMode.set(true);
    this.personaService.getById(item.id).subscribe({
      next: (res) => {
        if (res.success) {
          const full = res.data;
          this.editingId.set(full.id);
          this.form = {
            nombre: full.nombre || '',
            primerApellido: full.primerApellido || '',
            segundoApellido: full.segundoApellido || '',
            ci: full.ci || '',
            telefono: full.telefono || '',
            email: full.email || '',
            fechaNacimiento: this.parseDate(full.fechaNacimiento),
            genero: full.genero || 'M'
          };
          this.showForm.set(true);
        }
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Error al cargar persona');
      }
    });
  }

  closeForm(): void {
    this.showForm.set(false);
    this.error.set('');
    this.editMode.set(false);
    this.editingId.set(null);
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
      nombre: this.form.nombre,
      primerApellido: this.form.primerApellido,
      ci: this.form.ci,
      telefono: this.form.telefono,
      email: this.form.email,
      fechaNacimiento: this.form.fechaNacimiento || null,
      genero: this.form.genero
    };
    if (this.form.segundoApellido) body.segundoApellido = this.form.segundoApellido;

    this.personaService.create(body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Persona registrada exitosamente');
        this.showForm.set(false);
        this.loadPersonas();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al registrar persona';
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
      fechaNacimiento: this.form.fechaNacimiento || null,
      genero: this.form.genero
    };
    if (this.form.segundoApellido) body.segundoApellido = this.form.segundoApellido;

    this.personaService.update(this.editingId()!, body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Persona actualizada exitosamente');
        this.showForm.set(false);
        this.loadPersonas();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al actualizar persona';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  onDelete(item: PersonaSummary): void {
    Swal.fire({
      title: 'Eliminar persona',
      text: `¿Estás seguro de eliminar a ${item.nombreCompleto}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.personaService.delete(item.id).subscribe({
          next: () => {
            this.toast.success('Persona eliminada exitosamente');
            this.loadPersonas();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  private resetForm(): void {
    this.form = {
      nombre: '', primerApellido: '', segundoApellido: '',
      ci: '', telefono: '', email: '',
      fechaNacimiento: '', genero: 'M'
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
