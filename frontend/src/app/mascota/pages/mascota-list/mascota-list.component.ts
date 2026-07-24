import { Component, signal, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MascotaService } from '../../../core/services/mascota.service';
import { AuthService } from '../../../core/services/auth.service';
import { EspecieService, Especie } from '../../../core/services/especie.service';
import { RazaService, Raza } from '../../../core/services/raza.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { UploadService } from '../../../core/services/upload.service';
import { Mascota, MascotaRequest } from '../../../core/models/mascota.model';

@Component({
  selector: 'app-mascota-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './mascota-list.component.html'
})
export class MascotaListComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  items = signal<Mascota[]>([]);
  isCliente = false;
  searchTerm = '';
  showModal = signal(false);
  loading = signal(false);
  error = signal('');
  previewUrl = signal<string | null>(null);
  selectedFile = signal<File | null>(null);

  especies = signal<Especie[]>([]);
  razas = signal<Raza[]>([]);

  form = {
    nombre: '',
    fechaNacimiento: '',
    genero: 'M',
    peso: null as number | null,
    color: '',
    especieId: null as number | null,
    razaId: null as number | null,
  };

  private allItems: Mascota[] = [];
  private clienteId: number | null = null;

  constructor(
    private mascotaService: MascotaService,
    private auth: AuthService,
    private especieService: EspecieService,
    private razaService: RazaService,
    private clienteService: ClienteService,
    private uploadService: UploadService
  ) {}

  ngOnInit(): void {
    this.isCliente = this.auth.hasRole('CLIENTE');
    this.loadMascotas();
    this.especieService.getAll({ size: 100 }).subscribe({
      next: (res) => { if (res.success) this.especies.set(res.data.content); }
    });
    if (this.isCliente) {
      this.clienteService.getMe().subscribe({
        next: (res) => { if (res.success) this.clienteId = res.data.id; }
      });
    }
  }

  loadMascotas(): void {
    if (this.auth.hasRole('CLIENTE')) {
      this.mascotaService.getMy().subscribe({
        next: (res) => { if (res.success) { this.allItems = res.data; this.items.set(this.allItems); } }
      });
    } else {
      this.mascotaService.getAll({ size: 100 }).subscribe({
        next: (res) => { if (res.success) { this.allItems = res.data.content; this.items.set(this.allItems); } }
      });
    }
  }

  onEspecieChange(): void {
    this.form.razaId = null;
    this.razas.set([]);
    if (this.form.especieId) {
      this.razaService.getByEspecie(this.form.especieId).subscribe({
        next: (res) => { if (res.success) this.razas.set(res.data); }
      });
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      this.selectedFile.set(file);
      const reader = new FileReader();
      reader.onload = () => this.previewUrl.set(reader.result as string);
      reader.readAsDataURL(file);
    }
  }

  removeFile(): void {
    this.selectedFile.set(null);
    this.previewUrl.set(null);
    if (this.fileInput) this.fileInput.nativeElement.value = '';
  }

  openModal(): void {
    this.resetForm();
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
    this.error.set('');
    this.removeFile();
  }

  save(): void {
    if (!this.form.nombre || !this.form.fechaNacimiento || !this.form.especieId || !this.form.razaId) {
      this.error.set('Complete los campos obligatorios');
      return;
    }
    if (!this.clienteId && this.isCliente) {
      this.error.set('No se pudo identificar al cliente');
      return;
    }

    this.loading.set(true);
    this.error.set('');

    if (this.selectedFile()) {
      this.uploadService.uploadMascotaFoto(this.selectedFile()!).subscribe({
        next: (uploadRes) => {
          this.createMascota(uploadRes.url);
        },
        error: () => {
          this.loading.set(false);
          this.error.set('Error al subir la imagen');
        }
      });
    } else {
      this.createMascota(null);
    }
  }

  private createMascota(imagenUrl: string | null): void {
    const req: MascotaRequest = {
      nombre: this.form.nombre,
      fechaNacimiento: this.form.fechaNacimiento,
      genero: this.form.genero,
      peso: this.form.peso || 0,
      color: this.form.color,
      especieId: this.form.especieId!,
      razaId: this.form.razaId!,
      clienteId: this.clienteId!,
    };
    if (imagenUrl) req.imagenUrl = imagenUrl;

    this.mascotaService.create(req).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success) {
          this.closeModal();
          this.loadMascotas();
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err?.error?.message || 'Error al crear mascota');
      }
    });
  }

  private resetForm(): void {
    this.form = { nombre: '', fechaNacimiento: '', genero: 'M', peso: null, color: '', especieId: null, razaId: null };
    this.razas.set([]);
    this.error.set('');
  }

  calculateAge(fechaNacimiento: string): string {
    const birth = new Date(fechaNacimiento);
    const today = new Date();
    const years = today.getFullYear() - birth.getFullYear();
    const months = today.getMonth() - birth.getMonth();
    if (years > 0) return `${years} año(s)`;
    return `${months} mes(es)`;
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase();
    if (!term) { this.items.set(this.allItems); return; }
    this.items.set(
      this.allItems.filter(m =>
        m.nombre.toLowerCase().includes(term) ||
        m.especieNombre.toLowerCase().includes(term) ||
        m.clienteNombre.toLowerCase().includes(term)
      )
    );
  }
}
