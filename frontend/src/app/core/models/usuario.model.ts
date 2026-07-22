export interface Usuario {
  id: number;
  username: string;
  nombre: string;
  email: string;
  personaId: number;
  personaNombre: string;
  activo: boolean;
  roles: string[];
  createdAt: string;
}

export interface UsuarioRequest {
  username: string;
  password: string;
  nombre: string;
  email: string;
  personaId: number;
  activo: boolean;
  rolIds: number[];
}
