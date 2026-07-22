import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./landing/landing.component').then(m => m.LandingComponent),
  },
  {
    path: 'login',
    loadComponent: () => import('./security/pages/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () => import('./security/pages/register/register.component').then(m => m.RegisterComponent),
  },
  {
    path: 'forgot-password',
    loadComponent: () => import('./security/pages/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent),
  },
  {
    path: 'reset-password',
    loadComponent: () => import('./security/pages/reset-password/reset-password.component').then(m => m.ResetPasswordComponent),
  },
  {
    path: 'app',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/layout.component').then(m => m.LayoutComponent),
    children: [
      { path: 'dashboard', loadComponent: () => import('./dashboard/pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'personas', loadComponent: () => import('./persona/pages/persona-list/persona-list.component').then(m => m.PersonaListComponent) },
      { path: 'usuarios', loadComponent: () => import('./usuario/pages/usuario-list/usuario-list.component').then(m => m.UsuarioListComponent) },
      { path: 'roles', loadComponent: () => import('./rol/pages/rol-list/rol-list.component').then(m => m.RolListComponent) },
      { path: 'mascotas', loadComponent: () => import('./mascota/pages/mascota-list/mascota-list.component').then(m => m.MascotaListComponent) },
      { path: 'clientes', loadComponent: () => import('./cliente/pages/cliente-list/cliente-list.component').then(m => m.ClienteListComponent) },
      { path: 'proveedores', loadComponent: () => import('./proveedor/pages/proveedor-list/proveedor-list.component').then(m => m.ProveedorListComponent) },
      { path: 'servicios', loadComponent: () => import('./servicio/pages/servicio-list/servicio-list.component').then(m => m.ServicioListComponent) },
      { path: 'reservas', loadComponent: () => import('./reserva/pages/reserva-list/reserva-list.component').then(m => m.ReservaListComponent) },
      { path: 'vacunas', loadComponent: () => import('./vacuna/pages/vacuna-list/vacuna-list.component').then(m => m.VacunaListComponent) },
      { path: 'promociones', loadComponent: () => import('./promocion/pages/promocion-list/promocion-list.component').then(m => m.PromocionListComponent) },
      { path: 'pagos', loadComponent: () => import('./pago/pages/pago-list/pago-list.component').then(m => m.PagoListComponent) },
      { path: 'reportes', loadComponent: () => import('./reportes/pages/reportes/reportes.component').then(m => m.ReportesComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
  { path: '**', redirectTo: '' },
];
