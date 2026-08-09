export interface Promocion {
  id: number;
  proveedorId?: number;
  servicioId?: number;
  servicioNombre?: string;
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
  servicioId: number;
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
  proveedorId: number;
  servicioId: number;
  servicioNombre: string;
  codigo: string;
  nombre: string;
  descripcion: string;
  tipoDescuento: string;
  valorDescuento: number;
  fechaInicio: string;
  fechaFin: string;
  activa: boolean;
}
