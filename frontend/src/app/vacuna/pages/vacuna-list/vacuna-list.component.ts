import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VacunaService } from '../../../core/services/vacuna.service';
import { Vacuna, VacunaRequest } from '../../../core/models/vacuna.model';
import { ToastService } from '../../../core/services/toast.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-vacuna-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './vacuna-list.component.html'
})
export class VacunaListComponent implements OnInit {
  items = signal<Vacuna[]>([]);
  searchTerm = '';
  showForm = signal(false);
  loading = signal(false);
  editMode = signal(false);
  editingId = signal<number | null>(null);
  error = signal('');
  private allItems: Vacuna[] = [];

  form: VacunaRequest = {
    nombre: '',
    descripcion: '',
    periodicidadMeses: 12
  };

  constructor(
    private vacunaService: VacunaService,
    private toast: ToastService
  ) {}

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
        (v.descripcion && v.descripcion.toLowerCase().includes(term))
      )
    );
  }

  openForm(): void {
    this.resetForm();
    this.editMode.set(false);
    this.editingId.set(null);
    this.showForm.set(true);
  }

  openEdit(vacuna: Vacuna): void {
    this.editMode.set(true);
    this.editingId.set(vacuna.id);
    this.form = {
      nombre: vacuna.nombre,
      descripcion: vacuna.descripcion || '',
      periodicidadMeses: vacuna.periodicidadMeses
    };
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.error.set('');
    this.editMode.set(false);
    this.editingId.set(null);
  }

  onSubmit(): void {
    if (!this.form.nombre.trim() || !this.form.periodicidadMeses) {
      this.error.set('Nombre y periodicidad son obligatorios');
      return;
    }

    this.error.set('');
    this.loading.set(true);

    if (this.editMode()) {
      this.vacunaService.update(this.editingId()!, this.form).subscribe({
        next: () => {
          this.loading.set(false);
          this.toast.success('Vacuna actualizada exitosamente');
          this.showForm.set(false);
          this.loadVacunas();
        },
        error: (err) => {
          this.loading.set(false);
          const msg = err.error?.message || 'Error al actualizar vacuna';
          this.error.set(msg);
          this.toast.error(msg);
        }
      });
    } else {
      this.vacunaService.create(this.form).subscribe({
        next: () => {
          this.loading.set(false);
          this.toast.success('Vacuna registrada exitosamente');
          this.showForm.set(false);
          this.loadVacunas();
        },
        error: (err) => {
          this.loading.set(false);
          const msg = err.error?.message || 'Error al registrar vacuna';
          this.error.set(msg);
          this.toast.error(msg);
        }
      });
    }
  }

  deleteVacuna(vacuna: Vacuna): void {
    Swal.fire({
      title: 'Eliminar vacuna',
      text: `¿Estás seguro de eliminar "${vacuna.nombre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.vacunaService.delete(vacuna.id).subscribe({
          next: () => {
            this.toast.success('Vacuna eliminada exitosamente');
            this.loadVacunas();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  private resetForm(): void {
    this.form = {
      nombre: '',
      descripcion: '',
      periodicidadMeses: 12
    };
  }
}
