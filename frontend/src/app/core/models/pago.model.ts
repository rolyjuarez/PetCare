export interface Pago {
  id: number;
  codigo: string;
  reservaId: number;
  reservaCodigo: string;
  clienteNombre: string;
  monto: number;
  montoDescuento: number;
  montoTotal: number;
  metodoPago: string;
  estadoPago: string;
  fechaPago: string;
  referencia: string;
  notas: string;
  createdAt: string;
}

export interface PagoRequest {
  reservaId: number;
  monto: number;
  metodoPago: string;
  referencia?: string;
  notas?: string;
  promocionCodigo?: string;
}
