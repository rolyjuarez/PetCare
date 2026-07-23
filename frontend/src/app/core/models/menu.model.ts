export interface Submenu {
  id: number;
  nombre: string;
  descripcion: string;
  icono: string;
  url: string;
  orden: number;
  activo: boolean;
}

export interface MenuItem {
  id: number;
  nombre: string;
  descripcion: string;
  icono: string;
  url: string;
  orden: number;
  activo: boolean;
  submenus: Submenu[];
}
