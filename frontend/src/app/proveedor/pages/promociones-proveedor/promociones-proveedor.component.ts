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
  notificando = signal(false);
  showForm = signal(false);
  editId = signal<number | null>(null);

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
    this.loadServicios();
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.promocionService.getMyPromocionesDesdeProviderService(0, 100).subscribe({
      next: (res) => {
        if (res.success) this.items.set(res.data.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  loadServicios(): void {
    this.proveedorService.myServiciosDesdeProviderService().subscribe({
      next: (res) => {
        if (res.success) this.servicios.set(res.data);
      }
    });
  }

  servicioNombre(servicioId: number | null | undefined): string {
    const s = this.servicios().find(x => x.id === servicioId);
    return s ? s.nombre : (servicioId ? `Servicio #${servicioId}` : 'Todos los servicios');
  }

  descuentoTexto(p: PromocionSummary): string {
    return p.tipoDescuento === 'PERCENTAGE' ? `${p.valorDescuento}%` : `Bs. ${p.valorDescuento}`;
  }

  formatFecha(fecha: string): string {
    if (!fecha) return '';
    return String(fecha).replace('T', ' ').substring(0, 16);
  }

  openCreate(): void {
    this.editId.set(null);
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

  openEdit(p: PromocionSummary): void {
    this.editId.set(p.id);
    this.form = {
      servicioId: p.servicioId,
      codigo: p.codigo,
      nombre: p.nombre,
      descripcion: p.descripcion || '',
      tipoDescuento: p.tipoDescuento,
      valorDescuento: p.valorDescuento,
      fechaInicio: this.toDateInput(new Date(p.fechaInicio)),
      fechaFin: this.toDateInput(new Date(p.fechaFin)),
      activa: p.activa,
      limiteUsos: this.form.limiteUsos || 1
    };
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.editId.set(null);
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
    const editId = this.editId();
    const obs = editId
      ? this.promocionService.updateMiaDesdeProviderService(editId, data)
      : this.promocionService.createMiaDesdeProviderService(data);
    obs.subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.success(editId ? 'Descuento actualizado correctamente' : 'Descuento creado correctamente');
        this.closeForm();
        this.load();
      },
      error: (err) => {
        this.submitting.set(false);
        this.toast.error(err.error?.message || 'Error al guardar el descuento');
      }
    });
  }

  eliminar(p: PromocionSummary): void {
    if (!window.confirm(`¿Eliminar el descuento "${p.nombre}"?`)) return;
    this.promocionService.deleteMiaDesdeProviderService(p.id).subscribe({
      next: () => {
        this.toast.success('Descuento eliminado');
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message || 'Error al eliminar el descuento')
    });
  }

  notificar(p: PromocionSummary): void {
    this.notificando.set(true);
    this.promocionService.notificarDesdeProviderService(p.id).subscribe({
      next: (res) => {
        this.notificando.set(false);
        if (res.success) {
          this.toast.success(`Promoción notificada a ${res.data?.enviados ?? 0} cliente(s)`);
        }
      },
      error: (err) => {
        this.notificando.set(false);
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
