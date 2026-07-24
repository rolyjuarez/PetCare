import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PermisoService } from '../../../core/services/permiso.service';
import { RolService } from '../../../core/services/rol.service';
import { MenuService } from '../../../core/services/menu.service';
import { ToastService } from '../../../core/services/toast.service';
import { Permiso } from '../../../core/models/permiso.model';
import { Rol } from '../../../core/models/rol.model';
import { MenuItem, Submenu } from '../../../core/models/menu.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-permiso-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './permiso-list.component.html'
})
export class PermisoListComponent implements OnInit {
  items = signal<Permiso[]>([]);
  roles = signal<Rol[]>([]);
  menus = signal<MenuItem[]>([]);
  submenus = signal<Submenu[]>([]);
  searchTerm = '';
  showForm = signal(false);
  loading = signal(false);
  editMode = signal(false);
  editingId = signal<number | null>(null);
  error = signal('');
  private allItems: Permiso[] = [];

  form = {
    nombre: '',
    rolId: null as number | null,
    menuId: null as number | null,
    submenuId: null as number | null,
    crear: false,
    leer: true,
    actualizar: false,
    eliminar: false
  };

  constructor(
    private permisoService: PermisoService,
    private rolService: RolService,
    private menuService: MenuService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadPermisos();
    this.loadRoles();
    this.loadMenus();
  }

  loadPermisos(): void {
    this.permisoService.getAll({ size: 100 }).subscribe({
      next: (res) => {
        if (res.success) {
          this.allItems = res.data.content;
          this.items.set(this.allItems);
        }
      }
    });
  }

  loadRoles(): void {
    this.rolService.getAllActivos().subscribe({
      next: (res) => {
        if (res.success) {
          this.roles.set(res.data);
        }
      }
    });
  }

  loadMenus(): void {
    this.menuService.getAll({ size: 100 }).subscribe({
      next: (res) => {
        if (res.success) {
          this.menus.set(res.data.content);
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
        p.nombre?.toLowerCase().includes(term) ||
        p.rolNombre?.toLowerCase().includes(term) ||
        p.menuNombre?.toLowerCase().includes(term) ||
        p.submenuNombre?.toLowerCase().includes(term)
      )
    );
  }

  onMenuChange(): void {
    const menu = this.menus().find(m => m.id === Number(this.form.menuId));
    this.submenus.set(menu?.submenus || []);
    this.form.submenuId = null;
  }

  openForm(): void {
    this.resetForm();
    this.editMode.set(false);
    this.editingId.set(null);
    this.showForm.set(true);
  }

  openEdit(item: Permiso): void {
    this.editMode.set(true);
    this.editingId.set(item.id);
    this.permisoService.getById(item.id).subscribe({
      next: (res) => {
        if (res.success) {
          const full = res.data;
          this.form = {
            nombre: full.nombre || '',
            rolId: full.rolId,
            menuId: full.menuId,
            submenuId: full.submenuId || null,
            crear: full.crear,
            leer: full.leer,
            actualizar: full.actualizar,
            eliminar: full.eliminar
          };
          this.onMenuChange();
          if (full.submenuId) {
            this.form.submenuId = full.submenuId;
          }
          this.showForm.set(true);
        }
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Error al cargar permiso');
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
      rolId: this.form.rolId,
      menuId: this.form.menuId,
      submenuId: this.form.submenuId || 0,
      crear: this.form.crear,
      leer: this.form.leer,
      actualizar: this.form.actualizar,
      eliminar: this.form.eliminar
    };

    this.permisoService.create(body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Permiso registrado exitosamente');
        this.showForm.set(false);
        this.loadPermisos();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al registrar permiso';
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
      rolId: this.form.rolId,
      menuId: this.form.menuId,
      submenuId: this.form.submenuId || 0,
      crear: this.form.crear,
      leer: this.form.leer,
      actualizar: this.form.actualizar,
      eliminar: this.form.eliminar
    };

    this.permisoService.update(this.editingId()!, body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Permiso actualizado exitosamente');
        this.showForm.set(false);
        this.loadPermisos();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al actualizar permiso';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  deleteItem(item: Permiso): void {
    Swal.fire({
      title: 'Eliminar permiso',
      text: `¿Estás seguro de eliminar el permiso "${item.nombre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.permisoService.delete(item.id).subscribe({
          next: () => {
            this.toast.success('Permiso eliminado exitosamente');
            this.loadPermisos();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  getRolNombre(rolId: number): string {
    return this.roles().find(r => r.id === rolId)?.nombre || '';
  }

  getMenuNombre(menuId: number): string {
    return this.menus().find(m => m.id === menuId)?.nombre || '';
  }

  getSubmenuNombre(submenuId: number): string {
    for (const menu of this.menus()) {
      const sub = menu.submenus?.find(s => s.id === submenuId);
      if (sub) return sub.nombre;
    }
    return '';
  }

  private resetForm(): void {
    this.form = {
      nombre: '',
      rolId: null,
      menuId: null,
      submenuId: null,
      crear: false,
      leer: true,
      actualizar: false,
      eliminar: false
    };
    this.submenus.set([]);
  }
}
