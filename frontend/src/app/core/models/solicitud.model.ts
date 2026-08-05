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
  modalidadEntrega?: string;
  direccionReferencia?: string;
  latitud?: number;
  longitud?: number;
  estado: string;
  motivoRechazo: string;
  registroVacunacionId?: number;
  vacunaInfo?: VacunaInfo;
  creadaEn: string;
  respondidaEn: string;
}

export interface VacunaInfo {
  id: number;
  mascotaId: number;
  vacunaId: number;
  vacunaNombre: string;
  fechaAplicacion: string;
  certificadoUrl: string;
}
