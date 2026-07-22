import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PagoService } from '../../../core/services/pago.service';
import { Pago } from '../../../core/models/pago.model';

@Component({
  selector: 'app-pago-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './pago-list.component.html'
})
export class PagoListComponent implements OnInit {
  items = signal<Pago[]>([]);
  searchTerm = '';
  private allItems: Pago[] = [];

  constructor(private pagoService: PagoService) {}

  ngOnInit(): void {
    this.loadPagos();
  }

  loadPagos(): void {
    this.pagoService.getAll({ size: 100 }).subscribe({
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
      this.allItems.filter(p =>
        p.codigo.toLowerCase().includes(term) ||
        p.clienteNombre.toLowerCase().includes(term) ||
        (p.referencia && p.referencia.toLowerCase().includes(term))
      )
    );
  }
}
