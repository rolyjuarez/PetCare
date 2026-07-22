import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ServicioService } from '../../../core/services/servicio.service';
import { Servicio } from '../../../core/models/servicio.model';

@Component({
  selector: 'app-servicio-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './servicio-list.component.html'
})
export class ServicioListComponent implements OnInit {
  items = signal<Servicio[]>([]);
  searchTerm = '';
  private allItems: Servicio[] = [];

  constructor(private servicioService: ServicioService) {}

  ngOnInit(): void {
    this.loadServicios();
  }

  loadServicios(): void {
    this.servicioService.getAll({ size: 100 }).subscribe({
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
      this.allItems.filter(s =>
        s.nombre.toLowerCase().includes(term) ||
        s.categoria.toLowerCase().includes(term)
      )
    );
  }
}
