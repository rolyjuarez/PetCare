import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RolService } from '../../../core/services/rol.service';
import { Rol } from '../../../core/models/rol.model';

@Component({
  selector: 'app-rol-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './rol-list.component.html'
})
export class RolListComponent implements OnInit {
  items = signal<Rol[]>([]);
  searchTerm = '';
  private allItems: Rol[] = [];

  constructor(private rolService: RolService) {}

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.rolService.getAll({ size: 100 }).subscribe({
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
    this.items.set(this.allItems.filter(r => r.nombre.toLowerCase().includes(term)));
  }
}
