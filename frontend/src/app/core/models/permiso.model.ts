export interface Permiso {
  id: number;
  nombre: string;
  rolId: number;
  rolNombre: string;
  menuId: number;
  menuNombre: string;
  submenuId: number;
  submenuNombre: string;
  crear: boolean;
  leer: boolean;
  actualizar: boolean;
  eliminar: boolean;
}

export interface PermisoRequest {
  nombre: string;
  rolId: number;
  menuId: number;
  submenuId: number;
  crear: boolean;
  leer: boolean;
  actualizar: boolean;
  eliminar: boolean;
}
