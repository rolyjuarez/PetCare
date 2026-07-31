import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SolicitudService } from '../../../core/services/solicitud.service';
import { SolicitudReserva } from '../../../core/models/solicitud.model';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-solicitud-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './solicitud-list.component.html'
})
export class SolicitudListComponent implements OnInit {
  items = signal<SolicitudReserva[]>([]);
  filterEstado = signal<string>('');
  searchTerm = '';
  loading = signal(false);
  showRechazarModal = signal(false);
  selected = signal<SolicitudReserva | null>(null);
  motivoRechazo = '';
  private allItems: SolicitudReserva[] = [];

  constructor(
    private solicitudService: SolicitudService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.solicitudService.listar(this.filterEstado() || undefined, 0, 100).subscribe({
      next: (res) => {
        if (res.success) {
          this.allItems = res.data.content;
          this.applyFilter();
        }
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  onFilterChange(): void {
    this.load();
  }

  private applyFilter(): void {
    const estado = this.filterEstado();
    let result = estado ? this.allItems.filter(s => s.estado === estado) : this.allItems;
    const term = this.searchTerm.toLowerCase().trim();
    if (term) {
      result = result.filter(s =>
        s.codigo.toLowerCase().includes(term) ||
        s.clienteNombre.toLowerCase().includes(term) ||
        s.servicioNombre.toLowerCase().includes(term)
      );
    }
    this.items.set(result);
  }

  onSearch(): void {
    this.applyFilter();
  }

  aceptar(solicitud: SolicitudReserva): void {
    if (!confirm(`¿Aceptar la solicitud ${solicitud.codigo} de ${solicitud.clienteNombre}?`)) {
      return;
    }
    this.solicitudService.aceptar(solicitud.id).subscribe({
      next: () => {
        this.toast.success('Solicitud aceptada');
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message || 'Error al aceptar la solicitud')
    });
  }

  openRechazar(solicitud: SolicitudReserva): void {
    this.selected.set(solicitud);
    this.motivoRechazo = solicitud.motivoRechazo || '';
    this.showRechazarModal.set(true);
  }

  closeRechazar(): void {
    this.showRechazarModal.set(false);
    this.selected.set(null);
  }

  confirmRechazar(): void {
    const s = this.selected();
    if (!s) return;
    if (!this.motivoRechazo.trim()) {
      this.toast.error('Debe indicar un motivo de rechazo');
      return;
    }
    this.solicitudService.rechazar(s.id, this.motivoRechazo.trim()).subscribe({
      next: () => {
        this.toast.success('Solicitud rechazada');
        this.closeRechazar();
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message || 'Error al rechazar la solicitud')
    });
  }

  estadoColor(estado: string): string {
    switch (estado) {
      case 'PENDIENTE': return '#f59e0b';
      case 'ACEPTADA': return '#10b981';
      case 'RECHAZADA': return '#ef4444';
      default: return '#6b7280';
    }
  }
}
