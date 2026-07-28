import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReservaService, Reserva } from '../../../core/services/reserva.service';
import { AuthService } from '../../../core/services/auth.service';
import { ProveedorService } from '../../../core/services/proveedor.service';
import { Proveedor } from '../../../core/models/proveedor.model';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-reserva-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './reserva-list.component.html'
})
export class ReservaListComponent implements OnInit {
  items = signal<Reserva[]>([]);
  searchTerm = '';
  isAdmin = false;
  showAsignarModal = signal(false);
  selectedReserva = signal<Reserva | null>(null);
  proveedores = signal<Proveedor[]>([]);
  proveedorId = signal<number | null>(null);
  loading = signal(false);
  private allItems: Reserva[] = [];

  constructor(
    private reservaService: ReservaService,
    private auth: AuthService,
    private proveedorService: ProveedorService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.auth.hasRole('ADMIN');
    this.loadReservas();
    if (this.isAdmin) {
      this.proveedorService.getAll({ size: 100 }).subscribe({
        next: (res) => { if (res.success) this.proveedores.set(res.data.content); }
      });
    }
  }

  loadReservas(): void {
    this.reservaService.getAll({ size: 100 }).subscribe({
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
      this.allItems.filter(r =>
        r.codigo.toLowerCase().includes(term) ||
        r.clienteNombre.toLowerCase().includes(term) ||
        r.servicioNombre.toLowerCase().includes(term)
      )
    );
  }

  openAsignarModal(reserva: Reserva): void {
    this.selectedReserva.set(reserva);
    this.proveedorId.set(null);
    this.showAsignarModal.set(true);
  }

  closeAsignarModal(): void {
    this.showAsignarModal.set(false);
    this.selectedReserva.set(null);
    this.proveedorId.set(null);
  }

  asignarProveedor(): void {
    const r = this.selectedReserva();
    if (!r || !this.proveedorId()) return;
    this.loading.set(true);
    this.reservaService.asignarProveedor(r.id, this.proveedorId()!).subscribe({
      next: () => {
        this.loading.set(false);
        this.toast.success('Proveedor asignado a la reserva');
        this.closeAsignarModal();
        this.loadReservas();
      },
      error: (err) => {
        this.loading.set(false);
        this.toast.error(err.error?.message || 'Error al asignar proveedor');
      }
    });
  }
}
