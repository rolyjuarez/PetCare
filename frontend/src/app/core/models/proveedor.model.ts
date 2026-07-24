export interface Proveedor {
  id: number;
  personaId: number;
  usuarioId: number;
  personaNombre: string;
  personaTelefono: string;
  personaEmail: string;
  ci: string;
  nombre: string;
  primerApellido: string;
  segundoApellido: string;
  fechaNacimiento: any;
  genero: string;
  direccionId: number;
  calle: string;
  numero: string;
  referencia: string;
  ciudadId: number;
  latitud: number;
  longitud: number;
  empresa: string;
  radioCoberturaKm: number;
  descripcion: string;
  verificado: boolean;
  calificacion: number;
  especialidades: string[];
  servicioIds: number[];
  disponibilidades: ProveedorDisponibilidad[];
}

export interface ProveedorDisponibilidad {
  id: number;
  servicioId: number;
  servicioNombre: string;
  diaSemana: number;
  diaSemanaNombre: string;
  horaInicio: string;
  horaFin: string;
}

export interface ProveedorFullCreate {
  username: string;
  password: string;
  nombre: string;
  primerApellido: string;
  segundoApellido?: string;
  ci: string;
  telefono: string;
  email: string;
  fechaNacimiento?: string;
  genero: string;
  calle: string;
  numero?: string;
  referencia?: string;
  ciudadId: number;
  latitud?: number;
  longitud?: number;
  empresa: string;
  radioCoberturaKm?: number;
  descripcion?: string;
  servicioIds?: number[];
  disponibilidades?: DisponibilidadItem[];
}

export interface ProveedorFullUpdate {
  nombre: string;
  primerApellido: string;
  segundoApellido?: string;
  ci: string;
  telefono: string;
  email: string;
  calle: string;
  numero?: string;
  referencia?: string;
  ciudadId: number;
  latitud?: number;
  longitud?: number;
  empresa: string;
  radioCoberturaKm?: number;
  descripcion?: string;
  servicioIds?: number[];
  disponibilidades?: DisponibilidadItem[];
}

export interface DisponibilidadItem {
  servicioId: number;
  diaSemana: number;
  horaInicio: string;
  horaFin: string;
}
