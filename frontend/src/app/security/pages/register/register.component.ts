import { Component, signal, OnInit, OnDestroy, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { RegisterRequest, Ciudad } from '../../../core/models/auth.model';
import { environment } from '../../../../environments/environment';

declare const google: any;

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styles: [`
    :host { display: block; min-height: 100vh; background: linear-gradient(135deg, #0f172a 0%, #1e293b 40%, #134e4a 100%); }
    .register-container { max-width: 560px; margin: 0 auto; padding: 2rem 1rem; }
    .register-card { background: white; border-radius: 1rem; box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden; }
    .register-header { padding: 2rem 2rem 1rem; text-align: center; }
    .register-header .paw-circle { width: 64px; height: 64px; border-radius: 50%; background: linear-gradient(135deg, #22c55e, #16a34a); display: inline-flex; align-items: center; justify-content: center; font-size: 1.8rem; box-shadow: 0 8px 32px rgba(34,197,94,0.3); margin-bottom: 0.75rem; }
    .register-header h2 { font-size: 1.5rem; font-weight: 700; color: #0f172a; margin: 0; }
    .register-header p { color: #64748b; font-size: 0.9rem; margin-top: 0.25rem; }

    .stepper { display: flex; justify-content: center; gap: 0; padding: 1.5rem 2rem; border-bottom: 1px solid #e2e8f0; }
    .step { display: flex; align-items: center; gap: 0.5rem; font-size: 0.85rem; color: #94a3b8; }
    .step.active { color: #22c55e; font-weight: 600; }
    .step.completed { color: #16a34a; }
    .step-number { width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: 700; border: 2px solid #e2e8f0; color: #94a3b8; }
    .step.active .step-number { border-color: #22c55e; background: #22c55e; color: white; }
    .step.completed .step-number { border-color: #16a34a; background: #16a34a; color: white; }
    .step-line { width: 40px; height: 2px; background: #e2e8f0; margin: 0 0.5rem; }
    .step-line.active { background: #22c55e; }

    .form-body { padding: 1.5rem 2rem; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
    .form-group { margin-bottom: 1rem; }
    .form-group.full { grid-column: 1 / -1; }
    .form-group label { display: block; font-size: 0.8rem; font-weight: 600; color: #334155; margin-bottom: 0.35rem; text-transform: uppercase; letter-spacing: 0.03em; }
    .form-group label .required { color: #ef4444; }
    .form-group input, .form-group select, .form-group textarea {
      width: 100%; padding: 0.7rem 0.85rem; border: 2px solid #e2e8f0; border-radius: 0.6rem;
      font-size: 0.9rem; background: white; color: #0f172a; transition: all 0.2s; outline: none;
    }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus { border-color: #22c55e; box-shadow: 0 0 0 3px rgba(34,197,94,0.15); }

    .input-wrapper { position: relative; }
    .input-wrapper .input-icon { position: absolute; left: 1rem; top: 50%; transform: translateY(-50%); color: #94a3b8; font-size: 1.2rem; pointer-events: none; }
    .input-wrapper input { padding-left: 2.8rem !important; }
    .input-wrapper .toggle-pw { position: absolute; right: 0.5rem; top: 50%; transform: translateY(-50%); background: none; border: none; cursor: pointer; color: #94a3b8; padding: 0.25rem; display: flex; align-items: center; transition: color 0.2s; }
    .input-wrapper .toggle-pw:hover { color: #64748b; }
    .input-wrapper .toggle-pw .material-icons { font-size: 1.2rem; }

    .map-container { height: 300px; border-radius: 0.6rem; border: 2px solid #e2e8f0; margin-top: 0.5rem; overflow: hidden; }
    .map-hint { font-size: 0.75rem; color: #64748b; margin-top: 0.25rem; }
    .map-coords { display: flex; gap: 1rem; margin-top: 0.5rem; }
    .map-coords .form-group { flex: 1; margin-bottom: 0; }

    .form-actions { display: flex; gap: 0.75rem; padding: 1rem 2rem 1.5rem; }
    .btn { padding: 0.75rem 1.5rem; border: none; border-radius: 0.6rem; font-size: 0.9rem; font-weight: 600; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; justify-content: center; gap: 0.5rem; }
    .btn-primary { background: linear-gradient(135deg, #22c55e, #16a34a); color: white; flex: 1; }
    .btn-primary:hover { transform: translateY(-1px); box-shadow: 0 4px 16px rgba(34,197,94,0.4); }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }
    .btn-secondary { background: #f1f5f9; color: #475569; }
    .btn-secondary:hover { background: #e2e8f0; }

    .login-link { text-align: center; padding: 1rem 2rem 1.5rem; border-top: 1px solid #e2e8f0; }
    .login-link a { color: #22c55e; text-decoration: none; font-size: 0.85rem; font-weight: 600; }
    .login-link a:hover { text-decoration: underline; }

    .spinner { width: 18px; height: 18px; border: 2.5px solid rgba(255,255,255,0.3); border-top-color: white; border-radius: 50%; animation: spin 0.6s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }

    @media (max-width: 640px) {
      .form-row { grid-template-columns: 1fr; }
      .stepper { flex-wrap: wrap; }
    }
  `]
})
export class RegisterComponent implements OnInit, OnDestroy {
  currentStep = signal(1);
  loading = signal(false);
  showPassword = signal(false);
  showConfirmPassword = signal(false);
  ciudades = signal<Ciudad[]>([]);
  map: any = null;
  marker: any = null;
  mapInitialized = false;
  pendingCenter: { lat: number; lng: number } | null = null;

  form: RegisterRequest = {
    username: '', password: '', nombre: '', primerApellido: '', segundoApellido: '',
    ci: '', telefono: '', email: '', fechaNacimiento: '', genero: '',
    calle: '', numero: '', referencia: '', ciudadId: 0, latitud: null, longitud: null
  };

  confirmarPassword = '';

  private static mapsLoaded = false;
  private static mapsLoading = false;

  constructor(private auth: AuthService, private router: Router, private ngZone: NgZone, private toast: ToastService) {}

  ngOnInit() {
    this.auth.getCiudades().subscribe({
      next: (ciudades) => {
        console.log('Ciudades cargadas:', ciudades);
        this.ciudades.set(ciudades);
      },
      error: (err) => {
        console.error('Error cargando ciudades:', err);
      }
    });
  }

  ngOnDestroy() {
    this.destroyMap();
  }

  private destroyMap() {
    if (this.marker) {
      this.marker.setMap(null);
      this.marker = null;
    }
    this.map = null;
    this.mapInitialized = false;
  }

  private loadGoogleMapsScript(): Promise<void> {
    if (RegisterComponent.mapsLoaded) return Promise.resolve();
    if (RegisterComponent.mapsLoading) {
      return new Promise((resolve) => {
        const check = setInterval(() => {
          if (RegisterComponent.mapsLoaded) { clearInterval(check); resolve(); }
        }, 100);
      });
    }
    RegisterComponent.mapsLoading = true;
    return new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}`;
      script.async = true;
      script.defer = true;
      script.onload = () => {
        RegisterComponent.mapsLoaded = true;
        RegisterComponent.mapsLoading = false;
        resolve();
      };
      script.onerror = () => {
        RegisterComponent.mapsLoading = false;
        reject(new Error('No se pudo cargar Google Maps'));
      };
      document.head.appendChild(script);
    });
  }

  private async initMap() {
    if (this.mapInitialized) return;
    const mapEl = document.getElementById('register-map');
    if (!mapEl) {
      console.warn('Map container not found, retrying...');
      setTimeout(() => this.initMap(), 200);
      return;
    }
    try {
      await this.loadGoogleMapsScript();
      this.map = new google.maps.Map(mapEl, {
        center: { lat: -16.5, lng: -68.15 },
        zoom: 12,
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false,
        zoomControl: true,
        styles: [
          { featureType: 'poi', stylers: [{ visibility: 'off' }] },
          { featureType: 'transit', stylers: [{ visibility: 'off' }] },
        ]
      });
      this.map.addListener('click', (e: any) => {
        this.ngZone.run(() => {
          const lat = e.latLng.lat();
          const lng = e.latLng.lng();
          this.form.latitud = lat;
          this.form.longitud = lng;
          this.placeMarker(lat, lng);
        });
      });
      this.mapInitialized = true;
      if (this.pendingCenter) {
        this.map.panTo(this.pendingCenter);
        this.map.setZoom(14);
        this.pendingCenter = null;
      }
    } catch (err) {
      console.error('Error initializing Google Maps:', err);
      this.toast.error('No se pudo cargar el mapa. Verifique su conexion a internet.');
    }
  }

  private placeMarker(lat: number, lng: number) {
    if (!this.map) return;
    if (this.marker) {
      this.marker.setMap(null);
    }
    this.marker = new google.maps.Marker({
      position: { lat, lng },
      map: this.map,
      icon: {
        path: google.maps.SymbolPath.CIRCLE,
        scale: 10,
        fillColor: '#22c55e',
        fillOpacity: 1,
        strokeColor: '#ffffff',
        strokeWeight: 3,
      },
      animation: google.maps.Animation.DROP,
    });
  }

  onCiudadChange() {
    const ciudad = this.ciudades().find(c => c.id === this.form.ciudadId);
    if (!ciudad || ciudad.latitud == null || ciudad.longitud == null) return;
    const lat = Number(ciudad.latitud);
    const lng = Number(ciudad.longitud);
    if (this.map) {
      this.map.panTo({ lat, lng });
      this.map.setZoom(14);
    } else {
      this.pendingCenter = { lat, lng };
    }
  }

  nextStep() {
    if (this.currentStep() === 1) {
      if (!this.validateStep1()) return;
    }
    this.currentStep.set(2);
    setTimeout(() => this.initMap(), 150);
  }

  prevStep() {
    this.currentStep.set(1);
  }

  private validateStep1(): boolean {
    if (!this.form.username || this.form.username.length < 4) {
      this.toast.error('El usuario debe tener al menos 4 caracteres');
      return false;
    }
    if (!this.form.password || this.form.password.length < 6) {
      this.toast.error('La contrasena debe tener al menos 6 caracteres');
      return false;
    }
    if (this.form.password !== this.confirmarPassword) {
      this.toast.error('Las contrasenas no coinciden');
      return false;
    }
    if (!this.form.nombre) { this.toast.error('El nombre es requerido'); return false; }
    if (!this.form.primerApellido) { this.toast.error('El primer apellido es requerido'); return false; }
    if (!this.form.ci) { this.toast.error('El CI es requerido'); return false; }
    if (!this.form.telefono) { this.toast.error('El telefono es requerido'); return false; }
    if (!this.form.email || !this.form.email.includes('@')) { this.toast.error('Ingrese un email valido'); return false; }
    if (!this.form.fechaNacimiento) { this.toast.error('La fecha de nacimiento es requerida'); return false; }
    if (!this.form.genero) { this.toast.error('Seleccione un genero'); return false; }
    return true;
  }

  onSubmit() {
    if (!this.form.calle) { this.toast.error('La calle es requerida'); return; }
    if (!this.form.ciudadId) { this.toast.error('Seleccione una ciudad'); return; }

    this.loading.set(true);
    this.auth.register(this.form).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Registro exitoso. Revise su correo para mas informacion.');
        setTimeout(() => this.router.navigate(['/']), 3000);
      },
      error: (err) => {
        this.loading.set(false);
        this.toast.error(err.error?.message || 'Error al registrar. Intente de nuevo.');
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/']);
  }
}
