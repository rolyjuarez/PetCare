export interface Promocion {
  id: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  tipoDescuento: string;
  valorDescuento: number;
  fechaInicio: string;
  fechaFin: string;
  activa: boolean;
  limiteUsos: number;
  usosActuales: number;
}

export interface PromocionRequest {
  codigo: string;
  nombre: string;
  descripcion: string;
  tipoDescuento: string;
  valorDescuento: number;
  fechaInicio: string;
  fechaFin: string;
  activa: boolean;
  limiteUsos: number;
}
