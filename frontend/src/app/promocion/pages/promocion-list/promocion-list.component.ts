import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PromocionService } from '../../../core/services/promocion.service';
import { Promocion } from '../../../core/models/promocion.model';

@Component({
  selector: 'app-promocion-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './promocion-list.component.html'
})
export class PromocionListComponent implements OnInit {
  items = signal<Promocion[]>([]);
  searchTerm = '';
  private allItems: Promocion[] = [];

  constructor(private promocionService: PromocionService) {}

  ngOnInit(): void {
    this.loadPromociones();
  }

  loadPromociones(): void {
    this.promocionService.getAll({ size: 100 }).subscribe({
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
        p.descripcion.toLowerCase().includes(term)
      )
    );
  }
}
