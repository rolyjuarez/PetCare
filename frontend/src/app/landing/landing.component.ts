import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  styles: [`
    :host { display: block; height: 100vh; overflow: hidden; }

    .split-container {
      display: flex;
      height: 100vh;
    }

    /* ── LEFT PANEL ── */
    .left-panel {
      position: relative;
      flex: 1.2;
      background: linear-gradient(135deg, #0f172a 0%, #1e293b 40%, #134e4a 100%);
      overflow: hidden;
      display: flex;
      flex-direction: column;
      justify-content: center;
      padding: 3rem 4rem;
      color: white;
    }

    /* Animated mesh gradient blobs */
    .blob {
      position: absolute;
      border-radius: 50%;
      filter: blur(80px);
      opacity: 0.4;
      animation: float 8s ease-in-out infinite;
    }
    .blob-1 {
      width: 500px; height: 500px;
      background: radial-gradient(circle, #22c55e, transparent);
      top: -100px; left: -100px;
      animation-delay: 0s;
    }
    .blob-2 {
      width: 400px; height: 400px;
      background: radial-gradient(circle, #a855f7, transparent);
      bottom: -80px; right: -60px;
      animation-delay: -3s;
      animation-duration: 10s;
    }
    .blob-3 {
      width: 300px; height: 300px;
      background: radial-gradient(circle, #06b6d4, transparent);
      top: 50%; left: 60%;
      animation-delay: -5s;
      animation-duration: 12s;
    }

    @keyframes float {
      0%, 100% { transform: translate(0, 0) scale(1); }
      33% { transform: translate(30px, -30px) scale(1.05); }
      66% { transform: translate(-20px, 20px) scale(0.95); }
    }

    /* Floating pet emoji particles */
    .particle {
      position: absolute;
      font-size: 1.5rem;
      opacity: 0.15;
      animation: particleFloat 6s ease-in-out infinite;
      pointer-events: none;
    }
    .particle:nth-child(1) { top: 10%; left: 15%; animation-delay: 0s; animation-duration: 7s; }
    .particle:nth-child(2) { top: 70%; left: 10%; animation-delay: -1s; animation-duration: 8s; font-size: 2rem; }
    .particle:nth-child(3) { top: 20%; right: 20%; animation-delay: -2s; animation-duration: 6s; }
    .particle:nth-child(4) { bottom: 15%; left: 40%; animation-delay: -3s; animation-duration: 9s; font-size: 2.5rem; }
    .particle:nth-child(5) { top: 50%; right: 10%; animation-delay: -4s; animation-duration: 7.5s; }
    .particle:nth-child(6) { bottom: 30%; right: 30%; animation-delay: -2.5s; animation-duration: 8.5s; font-size: 1.8rem; }
    .particle:nth-child(7) { top: 80%; left: 50%; animation-delay: -1.5s; animation-duration: 6.5s; }
    .particle:nth-child(8) { top: 5%; left: 70%; animation-delay: -3.5s; animation-duration: 10s; font-size: 2rem; }

    @keyframes particleFloat {
      0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0.15; }
      50% { transform: translateY(-20px) rotate(15deg); opacity: 0.3; }
    }

    .left-content {
      position: relative;
      z-index: 10;
    }

    .brand-badge {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      background: rgba(34, 197, 94, 0.15);
      border: 1px solid rgba(34, 197, 94, 0.3);
      border-radius: 9999px;
      padding: 0.4rem 1rem;
      font-size: 0.8rem;
      color: #4ade80;
      letter-spacing: 0.05em;
      text-transform: uppercase;
      margin-bottom: 1.5rem;
      backdrop-filter: blur(8px);
      animation: fadeInUp 0.8s ease-out;
    }

    .hero-title {
      font-size: 3.5rem;
      font-weight: 800;
      line-height: 1.1;
      margin-bottom: 1.5rem;
      animation: fadeInUp 0.8s ease-out 0.2s both;
    }
    .hero-title .highlight {
      background: linear-gradient(135deg, #22c55e, #a855f7);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .hero-desc {
      font-size: 1.1rem;
      color: #94a3b8;
      line-height: 1.7;
      max-width: 500px;
      margin-bottom: 2.5rem;
      animation: fadeInUp 0.8s ease-out 0.4s both;
    }

    @keyframes fadeInUp {
      from { opacity: 0; transform: translateY(20px); }
      to { opacity: 1; transform: translateY(0); }
    }

    /* Service cards */
    .services-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 1rem;
      animation: fadeInUp 0.8s ease-out 0.6s both;
    }

    .service-card {
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.08);
      border-radius: 1rem;
      padding: 1.25rem;
      backdrop-filter: blur(12px);
      transition: all 0.3s ease;
      cursor: default;
    }
    .service-card:hover {
      background: rgba(255, 255, 255, 0.1);
      border-color: rgba(34, 197, 94, 0.3);
      transform: translateY(-4px);
      box-shadow: 0 8px 32px rgba(34, 197, 94, 0.15);
    }
    .service-card .icon {
      font-size: 2rem;
      margin-bottom: 0.5rem;
      display: block;
    }
    .service-card .name {
      font-weight: 600;
      font-size: 0.9rem;
      margin-bottom: 0.25rem;
    }
    .service-card .desc {
      font-size: 0.75rem;
      color: #94a3b8;
      line-height: 1.4;
    }

    /* Stats bar */
    .stats-bar {
      display: flex;
      gap: 2.5rem;
      margin-top: 2.5rem;
      padding-top: 2rem;
      border-top: 1px solid rgba(255, 255, 255, 0.08);
      animation: fadeInUp 0.8s ease-out 0.8s both;
    }
    .stat-item .number {
      font-size: 1.8rem;
      font-weight: 800;
      background: linear-gradient(135deg, #22c55e, #a855f7);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    .stat-item .label {
      font-size: 0.75rem;
      color: #64748b;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      margin-top: 0.1rem;
    }

    /* ── RIGHT PANEL (Login) ── */
    .right-panel {
      flex: 0.8;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f8fafc;
      position: relative;
      overflow: hidden;
    }

    .right-panel::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -50%;
      width: 100%;
      height: 100%;
      background: radial-gradient(circle, rgba(34, 197, 94, 0.05) 0%, transparent 70%);
      animation: float 15s ease-in-out infinite;
    }

    .login-container {
      width: 100%;
      max-width: 400px;
      padding: 2.5rem;
      position: relative;
      z-index: 10;
    }

    .login-logo {
      text-align: center;
      margin-bottom: 2rem;
    }
    .login-logo .paw-circle {
      width: 72px;
      height: 72px;
      border-radius: 50%;
      background: linear-gradient(135deg, #22c55e, #16a34a);
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-size: 2rem;
      box-shadow: 0 8px 32px rgba(34, 197, 94, 0.3);
      margin-bottom: 1rem;
      animation: pulse-glow 2s ease-in-out infinite;
    }
    @keyframes pulse-glow {
      0%, 100% { box-shadow: 0 8px 32px rgba(34, 197, 94, 0.3); }
      50% { box-shadow: 0 8px 48px rgba(34, 197, 94, 0.5); }
    }

    .login-logo h2 {
      font-size: 1.5rem;
      font-weight: 700;
      color: #0f172a;
    }
    .login-logo p {
      color: #64748b;
      font-size: 0.9rem;
      margin-top: 0.25rem;
    }

    .form-group {
      margin-bottom: 1.25rem;
    }
    .form-group label {
      display: block;
      font-size: 0.8rem;
      font-weight: 600;
      color: #334155;
      margin-bottom: 0.4rem;
      text-transform: uppercase;
      letter-spacing: 0.03em;
    }

    .input-wrapper {
      position: relative;
    }
    .input-wrapper .input-icon {
      position: absolute;
      left: 1rem;
      top: 50%;
      transform: translateY(-50%);
      color: #94a3b8;
      font-size: 1.2rem;
    }
    .input-wrapper input {
      width: 100%;
      padding: 0.85rem 1rem 0.85rem 2.8rem;
      border: 2px solid #e2e8f0;
      border-radius: 0.75rem;
      font-size: 0.95rem;
      background: white;
      color: #0f172a;
      transition: all 0.2s ease;
      outline: none;
    }
    .input-wrapper input:focus {
      border-color: #22c55e;
      box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.15);
    }
    .input-wrapper input::placeholder {
      color: #94a3b8;
    }

    .input-wrapper .toggle-pw {
      position: absolute;
      right: 0.75rem;
      top: 50%;
      transform: translateY(-50%);
      background: none;
      border: none;
      cursor: pointer;
      color: #94a3b8;
      padding: 0.25rem;
      display: flex;
      align-items: center;
      transition: color 0.2s;
    }
    .input-wrapper .toggle-pw:hover { color: #64748b; }
    .input-wrapper .toggle-pw .material-icons { font-size: 1.2rem; }

    .login-btn {
      width: 100%;
      padding: 0.9rem;
      border: none;
      border-radius: 0.75rem;
      background: linear-gradient(135deg, #22c55e, #16a34a);
      color: white;
      font-size: 1rem;
      font-weight: 700;
      cursor: pointer;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      margin-top: 0.5rem;
      position: relative;
      overflow: hidden;
    }
    .login-btn::before {
      content: '';
      position: absolute;
      top: 0; left: -100%; width: 100%; height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
      transition: left 0.5s ease;
    }
    .login-btn:hover::before { left: 100%; }
    .login-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 24px rgba(34, 197, 94, 0.4);
    }
    .login-btn:active { transform: translateY(0); }
    .login-btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none !important;
    }

    .error-alert {
      background: #fef2f2;
      border: 1px solid #fca5a5;
      color: #dc2626;
      padding: 0.75rem 1rem;
      border-radius: 0.75rem;
      font-size: 0.85rem;
      margin-bottom: 1rem;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      animation: shake 0.4s ease-in-out;
    }
    @keyframes shake {
      0%, 100% { transform: translateX(0); }
      25% { transform: translateX(-5px); }
      75% { transform: translateX(5px); }
    }

    .forgot-link {
      text-align: center;
      margin-top: 1.25rem;
    }
    .forgot-link a {
      color: #64748b;
      font-size: 0.8rem;
      text-decoration: none;
      transition: color 0.2s;
    }
    .forgot-link a:hover { color: #22c55e; }

    /* Footer links */
    .footer-links {
      position: absolute;
      bottom: 1.5rem;
      left: 0;
      right: 0;
      text-align: center;
      display: flex;
      justify-content: center;
      gap: 1.5rem;
      font-size: 0.75rem;
      color: #94a3b8;
    }
    .footer-links a {
      color: #94a3b8;
      text-decoration: none;
      transition: color 0.2s;
    }
    .footer-links a:hover { color: #22c55e; }

    /* Responsive */
    @media (max-width: 1024px) {
      .split-container { flex-direction: column; }
      .left-panel { display: none; }
      .right-panel { flex: 1; }
    }

    /* Spinner */
    .spinner {
      width: 20px; height: 20px;
      border: 2.5px solid rgba(255,255,255,0.3);
      border-top-color: white;
      border-radius: 50%;
      animation: spin 0.6s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }

    /* Typing cursor */
    .typing-cursor {
      display: inline-block;
      width: 3px;
      height: 1em;
      background: #22c55e;
      margin-left: 2px;
      animation: blink 1s step-end infinite;
      vertical-align: text-bottom;
    }
    @keyframes blink { 50% { opacity: 0; } }

    /* ═══ PLAYING PETS SCENE ═══ */
    .pets-scene {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 220px;
      pointer-events: none;
      z-index: 5;
      overflow: hidden;
    }

    /* ── Ground with grass ── */
    .ground {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 50px;
    }
    .ground::before {
      content: '';
      position: absolute;
      bottom: 30px;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent 5%, rgba(34,197,94,0.12) 30%, rgba(34,197,94,0.18) 50%, rgba(34,197,94,0.12) 70%, transparent 95%);
    }
    .grass {
      position: absolute;
      bottom: 30px;
      font-size: 0.7rem;
      opacity: 0.3;
      animation: grassSway 4s ease-in-out infinite;
    }
    .grass:nth-child(1) { left: 8%; animation-delay: 0s; }
    .grass:nth-child(2) { left: 22%; animation-delay: -0.8s; font-size: 0.6rem; }
    .grass:nth-child(3) { left: 38%; animation-delay: -1.5s; }
    .grass:nth-child(4) { left: 55%; animation-delay: -0.3s; font-size: 0.55rem; }
    .grass:nth-child(5) { left: 72%; animation-delay: -2s; }
    .grass:nth-child(6) { left: 88%; animation-delay: -1.2s; font-size: 0.65rem; }

    @keyframes grassSway {
      0%, 100% { transform: skewX(0deg); }
      25% { transform: skewX(3deg); }
      75% { transform: skewX(-3deg); }
    }

    /* ── Dog (left side, happy idle) ── */
    .pet-dog {
      position: absolute;
      bottom: 32px;
      left: 18%;
      font-size: 2.6rem;
      animation: dogBreathe 2.5s ease-in-out infinite;
      filter: drop-shadow(0 3px 10px rgba(0,0,0,0.15));
      transform-origin: bottom center;
    }

    @keyframes dogBreathe {
      0%, 100% { transform: translateY(0) rotate(0deg); }
      30% { transform: translateY(-6px) rotate(-2deg); }
      60% { transform: translateY(-3px) rotate(1deg); }
    }

    /* ── Cat (right side, elegant sit) ── */
    .pet-cat {
      position: absolute;
      bottom: 32px;
      right: 18%;
      font-size: 2.4rem;
      animation: catIdle 3.5s ease-in-out infinite;
      filter: drop-shadow(0 3px 10px rgba(0,0,0,0.15));
      transform-origin: bottom center;
    }

    @keyframes catIdle {
      0%, 100% { transform: translateY(0) scaleX(-1); }
      40% { transform: translateY(-4px) scaleX(-1) rotate(-2deg); }
      70% { transform: translateY(-2px) scaleX(-1) rotate(1deg); }
    }

    /* Cat tail */
    .cat-tail {
      position: absolute;
      bottom: 38px;
      right: calc(18% - 16px);
      width: 20px;
      height: 4px;
      background: linear-gradient(90deg, transparent, #94a3b8);
      border-radius: 2px;
      transform-origin: right center;
      animation: tailSway 2s ease-in-out infinite alternate;
    }

    @keyframes tailSway {
      from { transform: rotate(-15deg) translateY(-2px); }
      to { transform: rotate(15deg) translateY(2px); }
    }

    /* ── Ball (bounces gently between them) ── */
    .pet-ball {
      position: absolute;
      bottom: 32px;
      left: 50%;
      transform: translateX(-50%);
      width: 16px;
      height: 16px;
      background: radial-gradient(circle at 35% 30%, #fde68a, #f59e0b, #d97706);
      border-radius: 50%;
      animation: ballPlay 5s cubic-bezier(0.36, 0, 0.66, -0.56) infinite;
      box-shadow: 0 2px 6px rgba(0,0,0,0.2);
    }
    .pet-ball::after {
      content: '';
      position: absolute;
      top: 3px;
      left: 4px;
      width: 4px;
      height: 3px;
      background: rgba(255,255,255,0.5);
      border-radius: 50%;
    }

    @keyframes ballPlay {
      0% { left: 42%; bottom: 32px; }
      12% { left: 44%; bottom: 80px; }
      24% { left: 50%; bottom: 32px; }
      36% { left: 52%; bottom: 65px; }
      48% { left: 56%; bottom: 32px; }
      60% { left: 54%; bottom: 50px; }
      72% { left: 48%; bottom: 32px; }
      84% { left: 45%; bottom: 45px; }
      100% { left: 42%; bottom: 32px; }
    }

    /* Ball shadow */
    .ball-shadow {
      position: absolute;
      bottom: 30px;
      left: 50%;
      transform: translateX(-50%);
      width: 14px;
      height: 4px;
      background: rgba(0,0,0,0.1);
      border-radius: 50%;
      animation: shadowPulse 5s cubic-bezier(0.36, 0, 0.66, -0.56) infinite;
    }

    @keyframes shadowPulse {
      0% { left: 42%; opacity: 0.6; transform: scaleX(1); }
      12% { left: 44%; opacity: 0.2; transform: scaleX(0.6); }
      24% { left: 50%; opacity: 0.6; transform: scaleX(1); }
      36% { left: 52%; opacity: 0.3; transform: scaleX(0.7); }
      48% { left: 56%; opacity: 0.6; transform: scaleX(1); }
      60% { left: 54%; opacity: 0.35; transform: scaleX(0.75); }
      72% { left: 48%; opacity: 0.6; transform: scaleX(1); }
      84% { left: 45%; opacity: 0.4; transform: scaleX(0.8); }
      100% { left: 42%; opacity: 0.6; transform: scaleX(1); }
    }

    /* ── Butterfly (lazy drift) ── */
    .pet-butterfly {
      position: absolute;
      bottom: 120px;
      left: 40%;
      font-size: 1.3rem;
      animation: butterflyDrift 12s ease-in-out infinite;
      filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
    }

    @keyframes butterflyDrift {
      0% { transform: translate(0, 0) rotate(0deg); }
      15% { transform: translate(40px, -20px) rotate(8deg); }
      30% { transform: translate(20px, -35px) rotate(-5deg); }
      50% { transform: translate(-30px, -15px) rotate(10deg); }
      70% { transform: translate(-10px, -40px) rotate(-8deg); }
      85% { transform: translate(25px, -10px) rotate(3deg); }
      100% { transform: translate(0, 0) rotate(0deg); }
    }

    /* ── Floating leaves ── */
    .leaf {
      position: absolute;
      font-size: 0.9rem;
      opacity: 0;
      pointer-events: none;
    }
    .leaf:nth-child(1) {
      top: 10%; left: 15%;
      animation: leafFall 8s ease-in-out 0s infinite;
    }
    .leaf:nth-child(2) {
      top: 5%; left: 55%;
      animation: leafFall 10s ease-in-out 3s infinite;
      font-size: 0.7rem;
    }
    .leaf:nth-child(3) {
      top: 15%; left: 80%;
      animation: leafFall 9s ease-in-out 6s infinite;
      font-size: 0.8rem;
    }

    @keyframes leafFall {
      0% { opacity: 0; transform: translate(0, 0) rotate(0deg); }
      10% { opacity: 0.4; }
      50% { opacity: 0.25; transform: translate(20px, 60px) rotate(180deg); }
      100% { opacity: 0; transform: translate(-10px, 140px) rotate(360deg); }
    }

    /* ── Soft bokeh circles (depth) ── */
    .bokeh {
      position: absolute;
      border-radius: 50%;
      pointer-events: none;
      opacity: 0;
      animation: bokehPulse 6s ease-in-out infinite;
    }
    .bokeh:nth-child(1) {
      width: 60px; height: 60px;
      background: radial-gradient(circle, rgba(34,197,94,0.08), transparent);
      top: 20%; left: 10%;
      animation-delay: 0s;
    }
    .bokeh:nth-child(2) {
      width: 45px; height: 45px;
      background: radial-gradient(circle, rgba(168,85,247,0.06), transparent);
      top: 35%; right: 15%;
      animation-delay: -2s;
    }
    .bokeh:nth-child(3) {
      width: 55px; height: 55px;
      background: radial-gradient(circle, rgba(6,182,212,0.06), transparent);
      bottom: 40%; left: 45%;
      animation-delay: -4s;
    }

    @keyframes bokehPulse {
      0%, 100% { opacity: 0; transform: scale(0.8); }
      50% { opacity: 1; transform: scale(1.1); }
    }
  `],
  templateUrl: './landing.component.html'
})
export class LandingComponent implements OnInit {
  username = '';
  password = '';
  error = signal('');
  loading = signal(false);
  showPassword = signal(false);

  services = signal([
    { icon: '🩺', name: 'Veterinaria', desc: 'Consultas y tratamientos en casa' },
    { icon: '✂️', name: 'Peluquería', desc: 'Estética canina y felina' },
    { icon: '🦮', name: 'Paseos', desc: 'Ejercicio y socialización diaria' },
    { icon: '🏠', name: 'Hospedaje', desc: 'Cuidado temporal con amor' },
    { icon: '💉', name: 'Vacunación', desc: 'Programas de prevención' },
    { icon: '🍽️', name: 'Nutrición', desc: 'Dietas personalizadas' },
  ]);

  stats = signal([
    { value: '2,500+', label: 'Mascotas atendidas' },
    { value: '180+', label: 'Profesionales' },
    { value: '4.9', label: 'Calificación promedio' },
    { value: '24/7', label: 'Disponibilidad' },
  ]);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/app/dashboard']);
    }
  }

  onLogin() {
    this.error.set('');
    this.loading.set(true);
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/app/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message || 'Credenciales incorrectas. Intenta de nuevo.');
      }
    });
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }

  goToForgotPassword() {
    this.router.navigate(['/forgot-password']);
  }
}
