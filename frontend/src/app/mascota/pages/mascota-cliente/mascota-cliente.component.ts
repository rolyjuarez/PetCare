import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MascotaService } from '../../../core/services/mascota.service';
import { UploadService } from '../../../core/services/upload.service';
import { EspecieService, RazaService, Especie, Raza } from '../../../core/services/especie-raza.service';
import { ToastService } from '../../../core/services/toast.service';
import { Mascota } from '../../../core/models/mascota.model';
import { environment } from '../../../../environments/environment';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-mascota-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './mascota-cliente.component.html'
})
export class MascotaClienteComponent implements OnInit {
  mascotas = signal<Mascota[]>([]);
  especies = signal<Especie[]>([]);
  razas = signal<Raza[]>([]);
  clienteId = 0;
  clienteNombre = '';
  showForm = signal(false);
  loading = signal(false);
  error = signal('');
  uploading = signal(false);
  previewUrl = signal('');
  editingId = signal<number | null>(null);
  private selectedFile: File | null = null;
  private _pendingRazaId: number | null = null;

  form = {
    nombre: '',
    fechaNacimiento: '',
    genero: 'M',
    peso: null as number | null,
    color: '',
    especieId: null as number | null,
    razaId: null as number | null
  };

  constructor(
    private route: ActivatedRoute,
    private mascotaService: MascotaService,
    private uploadService: UploadService,
    private especieService: EspecieService,
    private razaService: RazaService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.clienteId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadMascotas();
    this.loadEspecies();
  }

  loadMascotas(): void {
    this.mascotaService.getByCliente(this.clienteId).subscribe({
      next: (res) => {
        if (res.success) {
          this.mascotas.set(res.data);
          if (res.data.length > 0) {
            this.clienteNombre = res.data[0].clienteNombre || '';
          }
        }
      }
    });
  }

  loadEspecies(): void {
    this.especieService.getAll().subscribe({
      next: (res) => {
        if (res.success) {
          this.especies.set(res.data.content);
        }
      }
    });
  }

  onEspecieChange(): void {
    if (this.form.especieId) {
      this.razaService.getByEspecie(this.form.especieId).subscribe({
        next: (res) => {
          if (res.success) {
            this.razas.set(res.data);
            if (this.form.razaId === null && this._pendingRazaId != null) {
              this.form.razaId = this._pendingRazaId;
              this._pendingRazaId = null;
            }
          }
        }
      });
    } else {
      this.razas.set([]);
    }
    this.form.razaId = null;
  }

  openForm(): void {
    this.resetForm();
    this.editingId.set(null);
    this.showForm.set(true);
  }

  openEditForm(mascota: Mascota): void {
    this.editingId.set(mascota.id);
    this.form = {
      nombre: mascota.nombre,
      fechaNacimiento: this.parseDate(mascota.fechaNacimiento),
      genero: mascota.genero,
      peso: mascota.peso,
      color: mascota.color,
      especieId: mascota.especieId,
      razaId: mascota.razaId,
    };
    this.previewUrl.set(mascota.imagenUrl || '');
    if (mascota.especieId && mascota.razaId) {
      this._pendingRazaId = mascota.razaId;
      this.onEspecieChange();
    }
    this.showForm.set(true);
  }

  private parseDate(fecha: any): string {
    if (!fecha) return '';
    if (typeof fecha === 'string') return fecha.substring(0, 10);
    if (Array.isArray(fecha)) return `${fecha[0]}-${String(fecha[1]).padStart(2, '0')}-${String(fecha[2]).padStart(2, '0')}`;
    return '';
  }

  closeForm(): void {
    this.showForm.set(false);
    this.error.set('');
    this.editingId.set(null);
    this.previewUrl.set('');
    this.selectedFile = null;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => this.previewUrl.set(reader.result as string);
      reader.readAsDataURL(this.selectedFile);
    }
  }

  onSubmit(): void {
    this.error.set('');
    this.loading.set(true);

    const save = (imagenUrl: string | null) => {
      const body: any = {
        nombre: this.form.nombre,
        fechaNacimiento: this.form.fechaNacimiento,
        genero: this.form.genero,
        peso: this.form.peso,
        color: this.form.color,
        especieId: this.form.especieId,
        razaId: this.form.razaId,
        clienteId: this.clienteId
      };
      if (imagenUrl) body.imagenUrl = imagenUrl;

      const id = this.editingId();
      const obs = id
        ? this.mascotaService.update(id, body)
        : this.mascotaService.create(body);

      obs.subscribe({
        next: () => {
          this.loading.set(false);
          this.toast.success(id ? 'Mascota actualizada' : 'Mascota registrada exitosamente');
          this.showForm.set(false);
          this.loadMascotas();
        },
        error: (err) => {
          this.loading.set(false);
          const msg = err.error?.message || 'Error al guardar mascota';
          this.error.set(msg);
          this.toast.error(msg);
        }
      });
    };

    if (this.selectedFile) {
      this.uploading.set(true);
      this.uploadService.uploadMascotaFoto(this.selectedFile).subscribe({
        next: (res) => {
          this.uploading.set(false);
          save(res.url);
        },
        error: () => {
          this.uploading.set(false);
          save(null);
        }
      });
    } else {
      save(null);
    }
  }

  deleteMascota(mascota: Mascota): void {
    Swal.fire({
      title: 'Eliminar mascota',
      text: `¿Estás seguro de eliminar a ${mascota.nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.mascotaService.delete(mascota.id).subscribe({
          next: () => {
            this.toast.success('Mascota eliminada exitosamente');
            this.loadMascotas();
          },
          error: (err) => this.toast.error(err.error?.message || 'Error al eliminar')
        });
      }
    });
  }

  calculateAge(fechaNacimiento: any): string {
    if (!fechaNacimiento) return '-';
    let birth: Date;
    if (typeof fechaNacimiento === 'string') {
      birth = new Date(fechaNacimiento);
    } else if (Array.isArray(fechaNacimiento)) {
      birth = new Date(fechaNacimiento[0], fechaNacimiento[1] - 1, fechaNacimiento[2]);
    } else {
      birth = new Date(fechaNacimiento);
    }
    const today = new Date();
    const years = today.getFullYear() - birth.getFullYear();
    const months = today.getMonth() - birth.getMonth();
    if (years > 0) return `${years} año(s)`;
    if (months > 0) return `${months} mes(es)`;
    return 'Menos de 1 mes';
  }

  private resetForm(): void {
    this.form = {
      nombre: '', fechaNacimiento: '', genero: 'M',
      peso: null, color: '', especieId: null, razaId: null
    };
    this.previewUrl.set('');
    this.selectedFile = null;
    this.razas.set([]);
  }
}
