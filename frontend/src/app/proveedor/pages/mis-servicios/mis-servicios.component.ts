import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProveedorService } from '../../../core/services/proveedor.service';
import { ProveedorServicio, ProveedorServicioModalidad, CATEGORIAS, MODALIDADES, modalidadLabel, categoriaLabel } from '../../../core/models/proveedor.model';
import { ToastService } from '../../../core/services/toast.service';

interface ModalidadForm {
  modalidad: string;
  costoAdicional: number;
  checked: boolean;
}

@Component({
  selector: 'app-mis-servicios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mis-servicios.component.html'
})
export class MisServiciosComponent implements OnInit {
  servicios = signal<ProveedorServicio[]>([]);
  loading = signal(false);
  showForm = signal(false);
  editId = signal<number | null>(null);
  submitting = signal(false);

  categorias = CATEGORIAS;
  modalidadesOptions = MODALIDADES;

  form = {
    nombre: '',
    descripcion: '',
    categoria: 'PELUQUERIA',
    duracionMinutos: 60,
    precioBase: 0,
    requiereCertificado: false,
    activo: true,
  };

  modalidadesForm: ModalidadForm[] = [];

  constructor(
    private proveedorService: ProveedorService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.proveedorService.myServiciosDesdeProviderService().subscribe({
      next: (res) => {
        if (res.success) this.servicios.set(res.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  modalidadLabel(m: string): string {
    return modalidadLabel(m);
  }

  categoriaLabel(c: string): string {
    return categoriaLabel(c);
  }

  openCreate(): void {
    this.editId.set(null);
    this.resetForm();
    this.showForm.set(true);
  }

  openEdit(svc: ProveedorServicio): void {
    this.editId.set(svc.id);
    this.form.nombre = svc.nombre;
    this.form.descripcion = svc.descripcion || '';
    this.form.categoria = svc.categoria;
    this.form.duracionMinutos = svc.duracionMinutos;
    this.form.precioBase = svc.precioBase;
    this.form.requiereCertificado = svc.requiereCertificado;
    this.form.activo = svc.activo;
    this.modalidadesForm = this.modalidadesOptions.map(m => {
      const existente = (svc.modalidades || []).find(x => x.modalidad === m);
      return {
        modalidad: m,
        costoAdicional: existente ? existente.costoAdicional : 0,
        checked: !!existente
      };
    });
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.editId.set(null);
  }

  private resetForm(): void {
    this.form = {
      nombre: '',
      descripcion: '',
      categoria: 'PELUQUERIA',
      duracionMinutos: 60,
      precioBase: 0,
      requiereCertificado: false,
      activo: true,
    };
    this.modalidadesForm = this.modalidadesOptions.map(m => ({
      modalidad: m,
      costoAdicional: 0,
      checked: m === 'EN_ESTABLECIMIENTO'
    }));
  }

  toggleModalidad(idx: number, checked: boolean): void {
    this.modalidadesForm[idx].checked = checked;
  }

  guardar(): void {
    const modalidades: ProveedorServicioModalidad[] = this.modalidadesForm
      .filter(m => m.checked)
      .map(m => ({ modalidad: m.modalidad, costoAdicional: Number(m.costoAdicional) || 0 }));
    if (!this.form.nombre.trim()) {
      this.toast.error('Ingrese el nombre del servicio');
      return;
    }
    if (modalidades.length === 0) {
      this.toast.error('Debe configurar al menos una modalidad de entrega');
      return;
    }
    if (this.form.duracionMinutos < 15) {
      this.toast.error('La duración mínima es 15 minutos');
      return;
    }
    const data = {
      nombre: this.form.nombre.trim(),
      descripcion: this.form.descripcion,
      categoria: this.form.categoria,
      duracionMinutos: this.form.duracionMinutos,
      precioBase: Number(this.form.precioBase) || 0,
      requiereCertificado: this.form.requiereCertificado,
      activo: this.form.activo,
      modalidades
    };
    this.submitting.set(true);
    const editId = this.editId();
    const obs = editId
      ? this.proveedorService.updateServicioDesdeProviderService(editId, data)
      : this.proveedorService.createServicioDesdeProviderService(data);
    obs.subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.success(editId ? 'Servicio actualizado' : 'Servicio creado');
        this.closeForm();
        this.load();
      },
      error: (err) => {
        this.submitting.set(false);
        this.toast.error(err.error?.message || 'Error al guardar el servicio');
      }
    });
  }

  toggleActivo(svc: ProveedorServicio): void {
    this.proveedorService.setServicioActivoDesdeProviderService(svc.id, !svc.activo).subscribe({
      next: (res) => {
        if (res.success) {
          this.toast.success(svc.activo ? 'Servicio desactivado' : 'Servicio activado');
          this.load();
        }
      },
      error: (err) => this.toast.error(err.error?.message || 'Error al cambiar el estado')
    });
  }

  eliminar(svc: ProveedorServicio): void {
    if (!window.confirm(`¿Eliminar el servicio "${svc.nombre}"?`)) return;
    this.proveedorService.deleteServicioDesdeProviderService(svc.id).subscribe({
      next: () => {
        this.toast.success('Servicio eliminado');
        this.load();
      },
      error: (err) => this.toast.error(err.error?.message || 'Error al eliminar el servicio')
    });
  }
}
