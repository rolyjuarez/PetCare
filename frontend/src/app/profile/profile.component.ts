import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../core/services/auth.service';
import { ToastService } from '../core/services/toast.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  loadingProfile = signal(false);
  savingProfile = signal(false);
  changingPassword = signal(false);
  showPassword = signal(false);
  showNewPassword = signal(false);
  showConfirmPassword = signal(false);

  profileForm = {
    nombre: '',
    primerApellido: '',
    segundoApellido: '',
    ci: '',
    email: '',
    telefono: ''
  };

  passwordForm = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  constructor(
    private auth: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loadingProfile.set(true);
    this.auth.getProfile().subscribe({
      next: (res) => {
        this.loadingProfile.set(false);
        if (res.success && res.data) {
          const d = res.data;
          this.profileForm = {
            nombre: d.nombre || '',
            primerApellido: d.primerApellido || '',
            segundoApellido: d.segundoApellido || '',
            ci: d.ci || '',
            email: d.email || '',
            telefono: d.telefono || ''
          };
        }
      },
      error: (err) => {
        this.loadingProfile.set(false);
        this.toast.error(err.error?.message || 'Error al cargar perfil');
      }
    });
  }

  saveProfile(): void {
    this.savingProfile.set(true);
    const body = {
      nombre: this.profileForm.nombre,
      primerApellido: this.profileForm.primerApellido,
      segundoApellido: this.profileForm.segundoApellido || null,
      email: this.profileForm.email,
      telefono: this.profileForm.telefono
    };

    this.auth.updateProfile(body).subscribe({
      next: (res) => {
        this.savingProfile.set(false);
        if (res.success) {
          this.toast.success('Perfil actualizado exitosamente');
          this.updateStoredUserInfo();
        } else {
          this.toast.error(res.message || 'Error al actualizar perfil');
        }
      },
      error: (err) => {
        this.savingProfile.set(false);
        this.toast.error(err.error?.message || 'Error al actualizar perfil');
      }
    });
  }

  changePassword(): void {
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.toast.error('Las contrasenas no coinciden');
      return;
    }

    this.changingPassword.set(true);
    this.auth.changePassword(this.passwordForm.oldPassword, this.passwordForm.newPassword).subscribe({
      next: (res) => {
        this.changingPassword.set(false);
        if (res.success) {
          this.toast.success('Contrasena cambiada exitosamente');
          this.passwordForm = { oldPassword: '', newPassword: '', confirmPassword: '' };
        } else {
          this.toast.error(res.message || 'Error al cambiar contrasena');
        }
      },
      error: (err) => {
        this.changingPassword.set(false);
        this.toast.error(err.error?.message || 'Error al cambiar contrasena');
      }
    });
  }

  private updateStoredUserInfo(): void {
    const user = this.auth.currentUser();
    if (user) {
      const updated = { ...user, nombre: this.profileForm.nombre };
      localStorage.setItem('userInfo', JSON.stringify(updated));
    }
  }
}
