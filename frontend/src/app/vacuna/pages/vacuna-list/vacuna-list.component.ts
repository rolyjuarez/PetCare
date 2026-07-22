import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { VacunaService } from '../../../core/services/vacuna.service';
import { Vacuna } from '../../../core/models/vacuna.model';

@Component({
  selector: 'app-vacuna-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './vacuna-list.component.html'
})
export class VacunaListComponent implements OnInit {
  items = signal<Vacuna[]>([]);
  searchTerm = '';
  private allItems: Vacuna[] = [];

  constructor(private vacunaService: VacunaService) {}

  ngOnInit(): void {
    this.loadVacunas();
  }

  loadVacunas(): void {
    this.vacunaService.getAll({ size: 100 }).subscribe({
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
      this.allItems.filter(v =>
        v.nombre.toLowerCase().includes(term) ||
        v.fabricante.toLowerCase().includes(term)
      )
    );
  }
}
