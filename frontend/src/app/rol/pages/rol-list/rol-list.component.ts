import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RolService } from '../../../core/services/rol.service';
import { ToastService } from '../../../core/services/toast.service';
import { Rol } from '../../../core/models/rol.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-rol-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './rol-list.component.html'
})
export class RolListComponent implements OnInit {
  items = signal<Rol[]>([]);
  searchTerm = '';
  showForm = signal(false);
  editMode = signal(false);
  editingId = signal<number | null>(null);
  loading = signal(false);
  error = signal('');
  private allItems: Rol[] = [];

  form = {
    nombre: '',
    descripcion: '',
    activo: true
  };

  constructor(
    private rolService: RolService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.rolService.getAll({ size: 100 }).subscribe({
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
    this.items.set(this.allItems.filter(r => r.nombre.toLowerCase().includes(term)));
  }

  openForm(): void {
    this.resetForm();
    this.editMode.set(false);
    this.editingId.set(null);
    this.showForm.set(true);
  }

  openEdit(rol: Rol): void {
    this.editMode.set(true);
    this.rolService.getById(rol.id).subscribe({
      next: (res) => {
        if (res.success) {
          const full = res.data;
          this.editingId.set(full.id);
          this.form = {
            nombre: full.nombre || '',
            descripcion: full.descripcion || '',
            activo: full.activo
          };
          this.showForm.set(true);
        }
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Error al cargar rol');
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
      descripcion: this.form.descripcion,
      activo: this.form.activo
    };

    this.rolService.create(body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Rol registrado exitosamente');
        this.showForm.set(false);
        this.loadRoles();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al registrar rol';
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
      descripcion: this.form.descripcion,
      activo: this.form.activo
    };

    this.rolService.update(this.editingId()!, body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Rol actualizado exitosamente');
        this.showForm.set(false);
        this.loadRoles();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al actualizar rol';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  deleteRol(rol: Rol): void {
    Swal.fire({
      title: 'Eliminar rol',
      text: `¿Estás seguro de eliminar el rol "${rol.nombre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.rolService.delete(rol.id).subscribe({
          next: () => {
            this.toast.success('Rol eliminado exitosamente');
            this.loadRoles();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  private resetForm(): void {
    this.form = { nombre: '', descripcion: '', activo: true };
  }
}
