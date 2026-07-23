export interface ClienteSummary {
  id: number;
  nombreCompleto: string;
  ci: string;
  telefono: string;
  personaId: number;
  nombre: string;
  primerApellido: string;
  segundoApellido: string;
  email: string;
  fechaNacimiento: string;
  genero: string;
  usuarioId: number;
  username: string;
  direccionId: number;
  calle: string;
  numero: string;
  referencia: string;
  ciudadId: number;
  ciudadNombre: string;
  latitud: number;
  longitud: number;
}

export interface Cliente {
  id: number;
  personaId: number;
  personaNombre: string;
  personaApellido: string;
  personaCi: string;
  personaTelefono: string;
  personaEmail: string;
  usuarioId: number;
  notas: string;
  createdAt: string;
}
