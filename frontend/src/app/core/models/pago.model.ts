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

export interface DescuentoAplicado {
  nombre: string;
  tipo: string;
  monto: number;
}

export interface PagoResponse {
  id: number;
  reservaId: number;
  codigoReserva: string;
  monto: number;
  montoOriginal: number;
  descuentoTotal: number;
  metodoPago: string;
  estadoPago: string;
  referenciaTransaccion: string;
  fechaPago: string;
  modalidadPago: string;
  intencionId: string;
  estadoSync: string;
  descuentosAplicados: DescuentoAplicado[];
}

export interface DatosTarjeta {
  numero: string;
  titular: string;
  expira: string;
  cvv: string;
}

export interface ProcesarPagoRequest {
  metodoPago: string;
  modalidadPago?: string;
  tarjeta?: DatosTarjeta;
}
