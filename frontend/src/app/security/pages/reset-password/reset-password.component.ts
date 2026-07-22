import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-password.component.html',
  styles: [`
    :host { display: block; min-height: 100vh; background: linear-gradient(135deg, #0f172a 0%, #1e293b 40%, #134e4a 100%); }
    .container { max-width: 440px; margin: 0 auto; padding: 4rem 1rem; }
    .card { background: white; border-radius: 1rem; box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden; }
    .card-header { padding: 2rem 2rem 1rem; text-align: center; }
    .card-header .paw-circle { width: 64px; height: 64px; border-radius: 50%; background: linear-gradient(135deg, #22c55e, #16a34a); display: inline-flex; align-items: center; justify-content: center; font-size: 1.8rem; box-shadow: 0 8px 32px rgba(34,197,94,0.3); margin-bottom: 0.75rem; }
    .card-header h2 { font-size: 1.5rem; font-weight: 700; color: #0f172a; margin: 0; }
    .card-header p { color: #64748b; font-size: 0.9rem; margin-top: 0.5rem; }
    .card-body { padding: 1rem 2rem 2rem; }
    .form-group { margin-bottom: 1rem; }
    .form-group label { display: block; font-size: 0.8rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem; text-transform: uppercase; }
    .form-group input { width: 100%; padding: 0.7rem 0.85rem; border: 2px solid #e2e8f0; border-radius: 0.6rem; font-size: 0.9rem; outline: none; transition: all 0.2s; }
    .form-group input:focus { border-color: #22c55e; box-shadow: 0 0 0 3px rgba(34,197,94,0.15); }
    .btn { width: 100%; padding: 0.8rem; border: none; border-radius: 0.6rem; font-size: 0.9rem; font-weight: 600; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; justify-content: center; gap: 0.5rem; background: linear-gradient(135deg, #22c55e, #16a34a); color: white; }
    .btn:hover { transform: translateY(-1px); box-shadow: 0 4px 16px rgba(34,197,94,0.4); }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }
    .alert { padding: 0.75rem 1rem; border-radius: 0.6rem; margin-bottom: 1rem; font-size: 0.85rem; display: flex; align-items: center; gap: 0.5rem; }
    .alert-error { background: #fef2f2; border: 1px solid #fca5a5; color: #dc2626; }
    .alert-success { background: #f0fdf4; border: 1px solid #86efac; color: #166534; }
    .back-link { text-align: center; padding: 1rem 2rem 1.5rem; border-top: 1px solid #e2e8f0; }
    .back-link a { color: #22c55e; text-decoration: none; font-size: 0.85rem; font-weight: 600; cursor: pointer; }
    .back-link a:hover { text-decoration: underline; }
    .spinner { width: 18px; height: 18px; border: 2.5px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 0.6s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class ResetPasswordComponent implements OnInit {
  token = '';
  newPassword = '';
  confirmPassword = '';
  loading = signal(false);
  error = signal('');
  success = signal(false);

  constructor(private auth: AuthService, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
    });
    if (!this.token) {
      this.error.set('Token de recuperacion no valido');
    }
  }

  onSubmit() {
    this.error.set('');
    if (!this.newPassword || this.newPassword.length < 6) {
      this.error.set('La contrasena debe tener al menos 6 caracteres');
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.error.set('Las contrasenas no coinciden');
      return;
    }
    this.loading.set(true);
    this.auth.resetPassword(this.token, this.newPassword).subscribe({
      next: () => { this.loading.set(false); this.success.set(true); },
      error: (err) => { this.loading.set(false); this.error.set(err.error?.message || 'Error al restablecer la contrasena'); }
    });
  }

  goToLogin() { this.router.navigate(['/']); }
}
