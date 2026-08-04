export interface Promocion {
  id: number;
  proveedorId?: number;
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
  createdAt?: string;
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

export interface PromocionSummary {
  id: number;
  codigo: string;
  nombre: string;
  tipoDescuento: string;
  valorDescuento: number;
  fechaFin: string;
  activa: boolean;
}
