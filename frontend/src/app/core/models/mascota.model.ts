export interface Mascota {
  id: number;
  nombre: string;
  fechaNacimiento: string;
  genero: string;
  peso: number;
  color: string;
  imagenUrl: string;
  especieId: number;
  especieNombre: string;
  razaId: number;
  razaNombre: string;
  clienteId: number;
  clienteNombre: string;
  createdAt: string;
}

export interface MascotaRequest {
  nombre: string;
  fechaNacimiento: string;
  genero: string;
  peso: number;
  color: string;
  especieId: number;
  razaId: number;
  clienteId: number;
}
