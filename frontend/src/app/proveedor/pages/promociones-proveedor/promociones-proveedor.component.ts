import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PromocionService } from '../../../core/services/promocion.service';
import { PromocionSummary } from '../../../core/models/promocion.model';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-promociones-proveedor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './promociones-proveedor.component.html'
})
export class PromocionesProveedorComponent implements OnInit {
  items = signal<PromocionSummary[]>([]);
  loading = signal(false);
  submitting = signal(false);
  showForm = signal(false);
  notificando = signal<number | null>(null);

  form = {
    codigo: '',
    nombre: '',
    descripcion: '',
    tipoDescuento: 'PERCENTAGE',
    valorDescuento: 10,
    fechaInicio: this.toDateInput(new Date()),
    fechaFin: this.toDateInput(new Date()),
    activa: true,
    limiteUsos: 100
  };

  constructor(
    private promocionService: PromocionService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.promocionService.getMyPromociones().subscribe({
      next: (res) => {
        if (res.success) this.items.set(res.data.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  descuentoTexto(p: PromocionSummary): string {
    return p.tipoDescuento === 'PERCENTAGE' ? `${p.valorDescuento}%` : `Bs. ${p.valorDescuento}`;
  }

  formatFecha(fecha: string): string {
    if (!fecha) return '';
    return String(fecha).replace('T', ' ').substring(0, 16);
  }

  openForm(): void {
    this.form = {
      codigo: 'PROMO-' + Date.now().toString().slice(-6).toUpperCase(),
      nombre: '',
      descripcion: '',
      tipoDescuento: 'PERCENTAGE',
      valorDescuento: 10,
      fechaInicio: this.toDateInput(new Date()),
      fechaFin: this.toDateInput(new Date()),
      activa: true,
      limiteUsos: 100
    };
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
  }

  guardar(): void {
    if (!this.form.codigo.trim() || !this.form.nombre.trim()) {
      this.toast.error('Ingrese el código y el nombre de la promoción');
      return;
    }
    const data = {
      codigo: this.form.codigo.trim().toUpperCase(),
      nombre: this.form.nombre.trim(),
      descripcion: this.form.descripcion,
      tipoDescuento: this.form.tipoDescuento,
      valorDescuento: Number(this.form.valorDescuento) || 0,
      fechaInicio: this.toDateTime(this.form.fechaInicio),
      fechaFin: this.toDateTime(this.form.fechaFin),
      activa: this.form.activa,
      limiteUsos: Number(this.form.limiteUsos) || 1
    };
    this.submitting.set(true);
    this.promocionService.createMia(data).subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.success('Promoción creada. Los clientes serán notificados por correo.');
        this.closeForm();
        this.load();
      },
      error: (err) => {
        this.submitting.set(false);
        this.toast.error(err.error?.message || 'Error al crear la promoción');
      }
    });
  }

  eliminar(p: PromocionSummary): void {
    if (!window.confirm(`¿Eliminar la promoción "${p.nombre}"?`)) return;
    this.promocionService.deleteMia(p.id).subscribe({
      next: () => {
        this.toast.success('Promoción eliminada');
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message || 'Error al eliminar la promoción')
    });
  }

  notificar(p: PromocionSummary): void {
    this.notificando.set(p.id);
    this.promocionService.notificar(p.id).subscribe({
      next: (res) => {
        this.notificando.set(null);
        const enviados = res.data?.enviados ?? 0;
        this.toast.success(`Promoción notificada a ${enviados} cliente(s)`);
      },
      error: (err) => {
        this.notificando.set(null);
        this.toast.error(err.error?.message || 'Error al notificar la promoción');
      }
    });
  }

  private toDateInput(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  private toDateTime(dateInput: string): string {
    if (!dateInput) return '';
    return `${dateInput}T00:00:00`;
  }
}
