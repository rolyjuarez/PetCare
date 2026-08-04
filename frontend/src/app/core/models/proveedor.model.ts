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
  servicioIdsRequeridos: number[];
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

export interface ProveedorServicioModalidad {
  id?: number;
  modalidad: string;
  costoAdicional: number;
  activo?: boolean;
}

export interface ProveedorServicio {
  id: number;
  proveedorId: number;
  proveedorNombre: string;
  nombre: string;
  descripcion: string;
  categoria: string;
  duracionMinutos: number;
  precioBase: number;
  requiereCertificado: boolean;
  activo: boolean;
  modalidades: ProveedorServicioModalidad[];
  createdAt?: string;
}

export interface ProveedorServicioRequest {
  nombre: string;
  descripcion?: string;
  categoria: string;
  duracionMinutos: number;
  precioBase: number;
  requiereCertificado?: boolean;
  activo?: boolean;
  modalidades: ProveedorServicioModalidad[];
}

export const CATEGORIAS = ['PELUQUERIA', 'PASEO', 'ALOJAMIENTO', 'VETERINARIA'];

export const MODALIDADES = ['EN_ESTABLECIMIENTO', 'RECOGIDA_ENTREGA', 'DOMICILIO'];

export function modalidadLabel(m: string): string {
  switch (m) {
    case 'EN_ESTABLECIMIENTO': return 'En establecimiento';
    case 'RECOGIDA_ENTREGA': return 'Recogida y entrega';
    case 'DOMICILIO': return 'A domicilio';
    default: return m;
  }
}

export function categoriaLabel(c: string): string {
  switch (c) {
    case 'PELUQUERIA': return 'Peluquería';
    case 'PASEO': return 'Paseo';
    case 'ALOJAMIENTO': return 'Alojamiento';
    case 'VETERINARIA': return 'Veterinaria';
    default: return c;
  }
}
