export interface Reserva {
  id: number;
  codigo: string;
  clienteId: number;
  clienteNombre: string;
  proveedorId: number;
  proveedorNombre: string;
  servicioId: number;
  servicioNombre: string;
  mascotaId: number;
  mascotaNombre: string;
  estadoReservaId: number;
  estadoReservaNombre: string;
  estadoReservaColor: string;
  fechaReserva: string;
  fechaInicio: string;
  fechaFin: string;
  horaInicio: string;
  horaFin: string;
  latitud: number;
  longitud: number;
  direccionReferencia: string;
  notas: string;
  precioTotal: number;
  createdAt: string;
}

export interface ReservaRequest {
  clienteId: number;
  proveedorId?: number;
  servicioId: number;
  mascotaId: number;
  fechaReserva: string;
  fechaInicio: string;
  fechaFin?: string;
  horaInicio: string;
  horaFin: string;
  latitud?: number;
  longitud?: number;
  direccionReferencia?: string;
  notas?: string;
  precioTotal: number;
}
