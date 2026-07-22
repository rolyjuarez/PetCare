export interface Persona {
  id: number;
  nombre: string;
  primerApellido: string;
  segundoApellido: string;
  ci: string;
  telefono: string;
  email: string;
  fechaNacimiento: string;
  genero: string;
  direccionId: number;
  createdAt: string;
  updatedAt: string;
}

export interface PersonaRequest {
  nombre: string;
  primerApellido: string;
  segundoApellido: string;
  ci: string;
  telefono: string;
  email: string;
  fechaNacimiento: string;
  genero: string;
  direccionId?: number;
}

export interface PersonaSummary {
  id: number;
  nombreCompleto: string;
  ci: string;
  telefono: string;
  email: string;
}
