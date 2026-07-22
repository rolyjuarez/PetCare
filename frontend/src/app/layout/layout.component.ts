import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.component.html'
})
export class LayoutComponent {
  sidebarOpen = signal(true);
  darkMode = signal(false);

  auth: AuthService;

  menuItems = [
    { route: '/app/dashboard', icon: 'dashboard', label: 'Dashboard' },
    { route: '/app/personas', icon: 'people', label: 'Personas' },
    { route: '/app/usuarios', icon: 'person', label: 'Usuarios' },
    { route: '/app/roles', icon: 'admin_panel_settings', label: 'Roles' },
    { route: '/app/mascotas', icon: 'pets', label: 'Mascotas' },
    { route: '/app/clientes', icon: 'contact_phone', label: 'Clientes' },
    { route: '/app/proveedores', icon: 'local_shipping', label: 'Proveedores' },
    { route: '/app/servicios', icon: 'design_services', label: 'Servicios' },
    { route: '/app/reservas', icon: 'event', label: 'Reservas' },
    { route: '/app/vacunas', icon: 'vaccines', label: 'Vacunas' },
    { route: '/app/promociones', icon: 'local_offer', label: 'Promociones' },
    { route: '/app/pagos', icon: 'payment', label: 'Pagos' },
    { route: '/app/reportes', icon: 'assessment', label: 'Reportes' }
  ];

  constructor(auth: AuthService) {
    this.auth = auth;
  }

  toggleSidebar(): void {
    this.sidebarOpen.update(v => !v);
  }

  toggleDarkMode(): void {
    this.darkMode.update(v => !v);
    document.documentElement.classList.toggle('dark');
  }
}
