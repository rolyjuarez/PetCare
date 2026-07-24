import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MenuService } from '../../../core/services/menu.service';
import { ToastService } from '../../../core/services/toast.service';
import { MenuItem } from '../../../core/models/menu.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-menu-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './menu-list.component.html'
})
export class MenuListComponent implements OnInit {
  items = signal<MenuItem[]>([]);
  searchTerm = '';
  filterActivo = '';
  showForm = signal(false);
  loading = signal(false);
  editMode = signal(false);
  editingId = signal<number | null>(null);
  error = signal('');
  private allItems: MenuItem[] = [];

  form = {
    nombre: '',
    descripcion: '',
    icono: '',
    url: '',
    orden: 0,
    activo: true
  };

  constructor(
    private menuService: MenuService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadMenus();
  }

  loadMenus(): void {
    const params: any = { size: 100 };
    if (this.searchTerm) params.nombre = this.searchTerm;
    if (this.filterActivo !== '') params.activo = this.filterActivo;

    this.menuService.getAll(params).subscribe({
      next: (res) => {
        if (res.success) {
          this.allItems = res.data.content;
          this.items.set(this.allItems);
        }
      }
    });
  }

  onSearch(): void {
    this.loadMenus();
  }

  onFilterActivo(): void {
    this.loadMenus();
  }

  openForm(): void {
    this.resetForm();
    this.editMode.set(false);
    this.editingId.set(null);
    this.showForm.set(true);
  }

  openEdit(item: MenuItem): void {
    this.editMode.set(true);
    this.menuService.getById(item.id).subscribe({
      next: (res) => {
        if (res.success) {
          const full = res.data;
          this.editingId.set(full.id);
          this.form = {
            nombre: full.nombre || '',
            descripcion: full.descripcion || '',
            icono: full.icono || '',
            url: full.url || '',
            orden: full.orden || 0,
            activo: full.activo ?? true
          };
          this.showForm.set(true);
        }
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Error al cargar menu');
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

    const body = {
      nombre: this.form.nombre,
      descripcion: this.form.descripcion,
      icono: this.form.icono,
      url: this.form.url,
      orden: this.form.orden,
      activo: this.form.activo
    };

    this.menuService.create(body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Menu creado exitosamente');
        this.showForm.set(false);
        this.loadMenus();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al crear menu';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  private onUpdate(): void {
    this.error.set('');
    this.loading.set(true);

    const body = {
      nombre: this.form.nombre,
      descripcion: this.form.descripcion,
      icono: this.form.icono,
      url: this.form.url,
      orden: this.form.orden,
      activo: this.form.activo
    };

    this.menuService.update(this.editingId()!, body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Menu actualizado exitosamente');
        this.showForm.set(false);
        this.loadMenus();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al actualizar menu';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  deleteItem(item: MenuItem): void {
    Swal.fire({
      title: 'Eliminar menu',
      text: `¿Estas seguro de eliminar "${item.nombre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Si, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.menuService.delete(item.id).subscribe({
          next: () => {
            this.toast.success('Menu eliminado exitosamente');
            this.loadMenus();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  private resetForm(): void {
    this.form = {
      nombre: '',
      descripcion: '',
      icono: '',
      url: '',
      orden: 0,
      activo: true
    };
  }
}
