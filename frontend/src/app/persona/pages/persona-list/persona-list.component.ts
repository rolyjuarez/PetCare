import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PersonaService } from '../../../core/services/persona.service';
import { PersonaSummary } from '../../../core/models/persona.model';

@Component({
  selector: 'app-persona-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './persona-list.component.html'
})
export class PersonaListComponent implements OnInit {
  items = signal<PersonaSummary[]>([]);
  searchTerm = '';
  private allItems: PersonaSummary[] = [];

  constructor(private personaService: PersonaService) {}

  ngOnInit(): void {
    this.loadPersonas();
  }

  loadPersonas(): void {
    this.personaService.getAll({ size: 100 }).subscribe({
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
        p.nombreCompleto.toLowerCase().includes(term) ||
        p.ci.includes(term) ||
        p.email.toLowerCase().includes(term)
      )
    );
  }
}
