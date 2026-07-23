import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  username = '';
  password = '';
  loading = signal(false);
  error = signal('');
  showPassword = signal(false);

  constructor(private auth: AuthService, private router: Router) {}

  onLogin(): void {
    this.loading.set(true);
    this.error.set('');

    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          const roles = res.data.userInfo?.roles ?? [];
          console.log('LOGIN DEBUG - roles:', roles, 'includes CLIENTE:', roles.includes('CLIENTE'));
          const target = roles.includes('CLIENTE') ? '/app/client-dashboard' : '/app/dashboard';
          console.log('LOGIN DEBUG - navigating to:', target);
          this.router.navigate([target]);
        } else {
          this.error.set(res.message || 'Credenciales incorrectas');
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.message || 'Error de conexion');
      }
    });
  }
}
