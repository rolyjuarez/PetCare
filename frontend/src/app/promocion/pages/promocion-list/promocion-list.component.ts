import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PromocionService } from '../../../core/services/promocion.service';
import { Promocion, PromocionRequest } from '../../../core/models/promocion.model';
import { ToastService } from '../../../core/services/toast.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-promocion-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './promocion-list.component.html'
})
export class PromocionListComponent implements OnInit {
  items = signal<Promocion[]>([]);
  searchTerm = '';
  showForm = signal(false);
  loading = signal(false);
  editMode = signal(false);
  editingId = signal<number | null>(null);
  error = signal('');
  private allItems: Promocion[] = [];

  form: PromocionRequest = {
    codigo: '',
    nombre: '',
    descripcion: '',
    tipoDescuento: 'PERCENTAGE',
    valorDescuento: 0,
    fechaInicio: '',
    fechaFin: '',
    activa: true,
    limiteUsos: null as unknown as number
  };

  constructor(
    private promocionService: PromocionService,
    private toast: ToastService
  ) {}

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
        p.nombre.toLowerCase().includes(term) ||
        p.codigo.toLowerCase().includes(term) ||
        (p.descripcion && p.descripcion.toLowerCase().includes(term))
      )
    );
  }

  openForm(): void {
    this.resetForm();
    this.editMode.set(false);
    this.editingId.set(null);
    this.showForm.set(true);
  }

  openEdit(promocion: Promocion): void {
    this.editMode.set(true);
    this.editingId.set(promocion.id);
    this.promocionService.getById(promocion.id).subscribe({
      next: (res) => {
        if (res.success) {
          const full = res.data;
          this.form = {
            codigo: full.codigo,
            nombre: full.nombre,
            descripcion: full.descripcion || '',
            tipoDescuento: full.tipoDescuento,
            valorDescuento: full.valorDescuento,
            fechaInicio: this.formatDateTime(full.fechaInicio),
            fechaFin: this.formatDateTime(full.fechaFin),
            activa: full.activa,
            limiteUsos: full.limiteUsos
          };
          this.showForm.set(true);
        }
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Error al cargar promocion');
      }
    });
  }

  closeForm(): void {
    this.showForm.set(false);
    this.error.set('');
    this.editMode.set(false);
    this.editingId.set(null);
  }

  onSubmit(): void {
    if (!this.form.codigo.trim() || !this.form.nombre.trim() || !this.form.valorDescuento ||
        !this.form.fechaInicio || !this.form.fechaFin) {
      this.error.set('Codigo, nombre, valor, fecha inicio y fecha fin son obligatorios');
      return;
    }

    this.error.set('');
    this.loading.set(true);

    const body: PromocionRequest = {
      codigo: this.form.codigo,
      nombre: this.form.nombre,
      descripcion: this.form.descripcion,
      tipoDescuento: this.form.tipoDescuento,
      valorDescuento: this.form.valorDescuento,
      fechaInicio: this.toIsoDateTime(this.form.fechaInicio),
      fechaFin: this.toIsoDateTime(this.form.fechaFin),
      activa: this.form.activa,
      limiteUsos: this.form.limiteUsos
    };

    if (this.editMode()) {
      this.promocionService.update(this.editingId()!, body).subscribe({
        next: () => {
          this.loading.set(false);
          this.toast.success('Promocion actualizada exitosamente');
          this.showForm.set(false);
          this.loadPromociones();
        },
        error: (err) => {
          this.loading.set(false);
          const msg = err.error?.message || 'Error al actualizar promocion';
          this.error.set(msg);
          this.toast.error(msg);
        }
      });
    } else {
      this.promocionService.create(body).subscribe({
        next: () => {
          this.loading.set(false);
          this.toast.success('Promocion registrada exitosamente');
          this.showForm.set(false);
          this.loadPromociones();
        },
        error: (err) => {
          this.loading.set(false);
          const msg = err.error?.message || 'Error al registrar promocion';
          this.error.set(msg);
          this.toast.error(msg);
        }
      });
    }
  }

  deletePromocion(promocion: Promocion): void {
    Swal.fire({
      title: 'Eliminar promocion',
      text: `¿Estás seguro de eliminar "${promocion.nombre}"?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.promocionService.delete(promocion.id).subscribe({
          next: () => {
            this.toast.success('Promocion eliminada exitosamente');
            this.loadPromociones();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  private resetForm(): void {
    this.form = {
      codigo: '',
      nombre: '',
      descripcion: '',
    tipoDescuento: 'PERCENTAGE',
      valorDescuento: 0,
      fechaInicio: '',
      fechaFin: '',
      activa: true,
      limiteUsos: null as unknown as number
    };
  }

  private formatDateTime(value: any): string {
    if (!value) return '';
    if (typeof value === 'string') {
      const normalized = value.replace(' ', 'T');
      return normalized.substring(0, 16);
    }
    if (Array.isArray(value)) {
      const [y, m, d, h = 0, min = 0] = value;
      return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}T${String(h).padStart(2, '0')}:${String(min).padStart(2, '0')}`;
    }
    return '';
  }

  private toIsoDateTime(value: string): string {
    if (!value) return '';
    if (value.length === 16) return value + ':00';
    return value;
  }
}
