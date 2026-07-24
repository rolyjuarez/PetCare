import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../../core/services/usuario.service';
import { ToastService } from '../../../core/services/toast.service';
import { Usuario } from '../../../core/models/usuario.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuario-list.component.html'
})
export class UsuarioListComponent implements OnInit {
  items = signal<Usuario[]>([]);
  showForm = signal(false);
  editMode = signal(false);
  editingId = signal<number | null>(null);
  loading = signal(false);
  error = signal('');
  searchTerm = '';
  private allItems: Usuario[] = [];

  form = {
    username: '',
    password: '',
    personaId: null as number | null,
    activo: true
  };

  constructor(
    private usuarioService: UsuarioService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadUsuarios();
  }

  loadUsuarios(): void {
    this.usuarioService.getAll({ size: 100 }).subscribe({
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
      this.allItems.filter(u =>
        u.username?.toLowerCase().includes(term) ||
        u.personaNombre?.toLowerCase().includes(term)
      )
    );
  }

  openForm(): void {
    this.resetForm();
    this.editMode.set(false);
    this.editingId.set(null);
    this.showForm.set(true);
  }

  openEdit(usuario: Usuario): void {
    this.editMode.set(true);
    this.usuarioService.getById(usuario.id).subscribe({
      next: (res) => {
        if (res.success) {
          const full = res.data;
          this.editingId.set(full.id);
          this.form = {
            username: full.username || '',
            password: '',
            personaId: full.personaId ?? null,
            activo: full.activo
          };
          this.showForm.set(true);
        }
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Error al cargar usuario');
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
      username: this.form.username,
      password: this.form.password,
      personaId: Number(this.form.personaId),
      activo: this.form.activo
    };

    this.usuarioService.create(body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Usuario registrado exitosamente');
        this.showForm.set(false);
        this.loadUsuarios();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al registrar usuario';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  private onUpdate(): void {
    this.error.set('');
    this.loading.set(true);

    const body: any = {
      username: this.form.username,
      personaId: Number(this.form.personaId),
      activo: this.form.activo
    };
    if (this.form.password) {
      body.password = this.form.password;
    }

    this.usuarioService.update(this.editingId()!, body).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Usuario actualizado exitosamente');
        this.showForm.set(false);
        this.loadUsuarios();
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err.error?.message || 'Error al actualizar usuario';
        this.error.set(msg);
        this.toast.error(msg);
      }
    });
  }

  deleteUsuario(usuario: Usuario): void {
    Swal.fire({
      title: 'Eliminar usuario',
      text: `¿Estás seguro de eliminar al usuario ${usuario.username}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.usuarioService.delete(usuario.id).subscribe({
          next: () => {
            this.toast.success('Usuario eliminado exitosamente');
            this.loadUsuarios();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  toggleActivo(usuario: Usuario): void {
    this.usuarioService.toggleActivo(usuario.id).subscribe({
      next: () => {
        this.toast.success(`Usuario ${usuario.activo ? 'desactivado' : 'activado'} exitosamente`);
        this.loadUsuarios();
      },
      error: (err) => this.toast.error(err.error?.message || 'Error al cambiar estado')
    });
  }

  private resetForm(): void {
    this.form = { username: '', password: '', personaId: null, activo: true };
    this.error.set('');
  }
}
