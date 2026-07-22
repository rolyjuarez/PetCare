export interface Vacuna {
  id: number;
  nombre: string;
  descripcion: string;
  fabricante: string;
  dosisRequeridas: number;
  intervaloDias: number;
  activo: boolean;
  createdAt: string;
}

export interface VacunaRequest {
  nombre: string;
  descripcion: string;
  fabricante: string;
  dosisRequeridas: number;
  intervaloDias: number;
}

export interface MascotaVacuna {
  id: number;
  mascotaId: number;
  mascotaNombre: string;
  vacunaId: number;
  vacunaNombre: string;
  fechaAplicacion: string;
  fechaProximaDosis: string;
  numeroDosis: number;
  veterinario: string;
  notas: string;
}
