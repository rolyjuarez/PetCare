import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  stats = signal([
    { label: 'Reservas Hoy', value: 12, icon: 'event', color: '#22c55e' },
    { label: 'Clientes Activos', value: 156, icon: 'people', color: '#3b82f6' },
    { label: 'Proveedores', value: 28, icon: 'local_shipping', color: '#a855f7' },
    { label: 'Ingresos del Mes', value: '$12,450', icon: 'attach_money', color: '#f59e0b' }
  ]);

  recentReservas = signal<any[]>([
    { id: 1, codigo: 'RES-001', clienteNombre: 'Maria Garcia', servicioNombre: 'Paseo de Mascotas', fechaReserva: '2026-07-22', estadoReservaNombre: 'Confirmada', estadoReservaColor: '#22c55e' },
    { id: 2, codigo: 'RES-002', clienteNombre: 'Juan Lopez', servicioNombre: 'Baño y Corte', fechaReserva: '2026-07-22', estadoReservaNombre: 'Pendiente', estadoReservaColor: '#f59e0b' },
    { id: 3, codigo: 'RES-003', clienteNombre: 'Ana Martinez', servicioNombre: 'Veterinaria', fechaReserva: '2026-07-23', estadoReservaNombre: 'Programada', estadoReservaColor: '#3b82f6' },
    { id: 4, codigo: 'RES-004', clienteNombre: 'Carlos Ruiz', servicioNombre: 'Hospedaje', fechaReserva: '2026-07-24', estadoReservaNombre: 'Pendiente', estadoReservaColor: '#f59e0b' },
    { id: 5, codigo: 'RES-005', clienteNombre: 'Laura Fernandez', servicioNombre: 'Paseo de Mascotas', fechaReserva: '2026-07-22', estadoReservaNombre: 'Completada', estadoReservaColor: '#6b7280' }
  ]);

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadStats();
  }

  private loadStats(): void {
    this.api.get<any>('/dashboard/stats').subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.stats.set([
            { label: 'Reservas Hoy', value: res.data.reservasHoy ?? 0, icon: 'event', color: '#22c55e' },
            { label: 'Clientes Activos', value: res.data.clientesActivos ?? 0, icon: 'people', color: '#3b82f6' },
            { label: 'Proveedores', value: res.data.proveedores ?? 0, icon: 'local_shipping', color: '#a855f7' },
            { label: 'Ingresos del Mes', value: `$${(res.data.ingresosMes ?? 0).toLocaleString()}`, icon: 'attach_money', color: '#f59e0b' }
          ]);
        }
      },
      error: () => {}
    });
  }
}
