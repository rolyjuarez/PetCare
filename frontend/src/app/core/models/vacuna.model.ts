export interface Vacuna {
  id: number;
  nombre: string;
  descripcion: string;
  periodicidadMeses: number;
  createdAt: string;
}

export interface VacunaRequest {
  nombre: string;
  descripcion: string;
  periodicidadMeses: number;
}
