export interface SolicitudReserva {
  id: number;
  reservaId: number;
  codigo: string;
  clienteId: number;
  clienteNombre: string;
  proveedorId: number;
  proveedorEmpresa: string;
  servicioId: number;
  servicioNombre: string;
  mascotaId: number;
  mascotaNombre: string;
  fechaInicio: string;
  horaInicio: string;
  precioTotal: number;
  estado: string;
  motivoRechazo: string;
  creadaEn: string;
  respondidaEn: string;
}
