import { Routes, Router, UrlTree } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { inject } from '@angular/core';

function isCliente(): boolean {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') ?? 'null');
    return userInfo?.roles?.includes('CLIENTE') ?? false;
  } catch {
    return false;
  }
}

function redirectByRole(): UrlTree {
  const router = inject(Router);
  return router.parseUrl(isCliente() ? '/app/mascotas' : '/app/dashboard');
}

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
      { path: 'client-dashboard', loadComponent: () => import('./dashboard/pages/client-dashboard/client-dashboard.component').then(m => m.ClientDashboardComponent) },
      { path: 'profile', loadComponent: () => import('./profile/profile.component').then(m => m.ProfileComponent) },
      { path: 'personas', loadComponent: () => import('./persona/pages/persona-list/persona-list.component').then(m => m.PersonaListComponent) },
      { path: 'usuarios', loadComponent: () => import('./usuario/pages/usuario-list/usuario-list.component').then(m => m.UsuarioListComponent) },
      { path: 'roles', loadComponent: () => import('./rol/pages/rol-list/rol-list.component').then(m => m.RolListComponent) },
      { path: 'admin/personas', loadComponent: () => import('./persona/pages/persona-list/persona-list.component').then(m => m.PersonaListComponent) },
      { path: 'admin/usuarios', loadComponent: () => import('./usuario/pages/usuario-list/usuario-list.component').then(m => m.UsuarioListComponent) },
      { path: 'admin/roles', loadComponent: () => import('./rol/pages/rol-list/rol-list.component').then(m => m.RolListComponent) },
      { path: 'admin/permisos', loadComponent: () => import('./permiso/pages/permiso-list/permiso-list.component').then(m => m.PermisoListComponent) },
      { path: 'admin/menus', loadComponent: () => import('./menu/pages/menu-list/menu-list.component').then(m => m.MenuListComponent) },
      { path: 'mascotas', loadComponent: () => import('./mascota/pages/mascota-list/mascota-list.component').then(m => m.MascotaListComponent) },
      { path: 'clientes', loadComponent: () => import('./cliente/pages/cliente-list/cliente-list.component').then(m => m.ClienteListComponent) },
      { path: 'clientes/:id/mascotas', loadComponent: () => import('./mascota/pages/mascota-cliente/mascota-cliente.component').then(m => m.MascotaClienteComponent) },
      { path: 'mascotas/:id', loadComponent: () => import('./mascota/pages/mascota-detail/mascota-detail.component').then(m => m.MascotaDetailComponent) },
      { path: 'proveedores', loadComponent: () => import('./proveedor/pages/proveedor-list/proveedor-list.component').then(m => m.ProveedorListComponent) },
      { path: 'proveedor/reservas', loadComponent: () => import('./proveedor/pages/solicitud-list/solicitud-list.component').then(m => m.SolicitudListComponent) },
      { path: 'proveedor/servicios', loadComponent: () => import('./proveedor/pages/mis-servicios/mis-servicios.component').then(m => m.MisServiciosComponent) },
      { path: 'proveedor/promociones', loadComponent: () => import('./proveedor/pages/promociones-proveedor/promociones-proveedor.component').then(m => m.PromocionesProveedorComponent) },
      { path: 'servicios', loadComponent: () => import('./servicio/pages/servicio-list/servicio-list.component').then(m => m.ServicioListComponent) },
      { path: 'reservas', loadComponent: () => import('./reserva/pages/reserva-list/reserva-list.component').then(m => m.ReservaListComponent) },
      { path: 'vacunas', loadComponent: () => import('./vacuna/pages/vacuna-list/vacuna-list.component').then(m => m.VacunaListComponent) },
      { path: 'promociones', loadComponent: () => import('./promocion/pages/promocion-list/promocion-list.component').then(m => m.PromocionListComponent) },
      { path: 'pagos', loadComponent: () => import('./pago/pages/pago-list/pago-list.component').then(m => m.PagoListComponent) },
      { path: 'reportes', loadComponent: () => import('./reportes/pages/reportes/reportes.component').then(m => m.ReportesComponent) },
      { path: '', canActivate: [redirectByRole], children: [] },
    ],
  },
  { path: '**', redirectTo: '' },
];
