export interface Promocion {
  id: number;
  codigo: string;
  descripcion: string;
  porcentajeDescuento: number;
  fechaInicio: string;
  fechaFin: string;
  activo: boolean;
  usoMaximo: number;
  usoActual: number;
  servicioIds: number[];
  servicioNombres: string[];
  createdAt: string;
}

export interface PromocionRequest {
  codigo: string;
  descripcion: string;
  porcentajeDescuento: number;
  fechaInicio: string;
  fechaFin: string;
  usoMaximo: number;
  servicioIds: number[];
}
