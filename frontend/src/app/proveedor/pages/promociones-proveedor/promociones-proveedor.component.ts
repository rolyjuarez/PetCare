import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PromocionService } from '../../../core/services/promocion.service';
import { ProveedorService } from '../../../core/services/proveedor.service';
import { PromocionRequest, PromocionSummary } from '../../../core/models/promocion.model';
import { ProveedorServicio } from '../../../core/models/proveedor.model';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-promociones-proveedor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './promociones-proveedor.component.html'
})
export class PromocionesProveedorComponent implements OnInit {
  items = signal<PromocionSummary[]>([]);
  servicios = signal<ProveedorServicio[]>([]);
  loading = signal(false);
  submitting = signal(false);
  showForm = signal(false);
  proveedorId: number | null = null;

  form = {
    servicioId: null as number | null,
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
    private proveedorService: ProveedorService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadProveedor();
  }

  loadProveedor(): void {
    this.proveedorService.getMe().subscribe({
      next: (res) => {
        if (res.success && res.data?.id) {
          this.proveedorId = res.data.id;
          this.loadServicios();
          this.load();
        }
      },
      error: () => this.toast.error('No se pudo obtener el proveedor del usuario')
    });
  }

  load(): void {
    if (this.proveedorId == null) return;
    this.loading.set(true);
    this.promocionService.getByProveedor(this.proveedorId).subscribe({
      next: (res) => {
        if (res.success) this.items.set(res.data.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  loadServicios(): void {
    this.proveedorService.myServicios().subscribe({
      next: (res) => {
        if (res.success) this.servicios.set(res.data);
      }
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
      servicioId: this.servicios().length > 0 ? this.servicios()[0].id : null,
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
    if (!this.form.servicioId) {
      this.toast.error('Seleccione el servicio al que aplica el descuento');
      return;
    }
    if (!this.form.codigo.trim() || !this.form.nombre.trim()) {
      this.toast.error('Ingrese el código y el nombre de la promoción');
      return;
    }
    if (this.proveedorId == null) {
      this.toast.error('Proveedor no identificado');
      return;
    }
    const data: PromocionRequest = {
      servicioId: this.form.servicioId,
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
    this.promocionService.createByProveedor(this.proveedorId, data).subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.success('Descuento creado correctamente');
        this.closeForm();
        this.load();
      },
      error: (err) => {
        this.submitting.set(false);
        this.toast.error(err.error?.message || 'Error al crear el descuento');
      }
    });
  }

  eliminar(p: PromocionSummary): void {
    if (this.proveedorId == null) return;
    if (!window.confirm(`¿Eliminar el descuento "${p.nombre}"?`)) return;
    this.promocionService.deleteByProveedor(this.proveedorId, p.id).subscribe({
      next: () => {
        this.toast.success('Descuento eliminado');
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message || 'Error al eliminar el descuento')
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
