export interface Rol {
  id: number;
  nombre: string;
  descripcion: string;
  permisos: string[];
  activo: boolean;
  createdAt: string;
}

export interface RolRequest {
  nombre: string;
  descripcion: string;
  permisoIds: number[];
}
