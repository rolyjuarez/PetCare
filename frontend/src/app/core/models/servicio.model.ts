export interface Servicio {
  id: number;
  nombre: string;
  descripcion: string;
  duracionMinutos: number;
  precioBase: number;
  imagenUrl: string;
  activo: boolean;
  categoria: string;
  createdAt: string;
}
