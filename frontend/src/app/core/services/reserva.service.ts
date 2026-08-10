import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';

export interface Reserva {
  id: number;
  codigo: string;
  clienteId: number;
  proveedorId: number;
  servicioId: number;
  mascotaId: number;
  estadoReservaId: number;
  registroVacunacionId?: number;
  modalidadEntrega?: string;
  latitud?: number;
  longitud?: number;
  direccionReferencia?: string;
  fechaReserva: string;
  fechaInicio: string;
  fechaFin: string;
  horaInicio: string;
  horaFin: string;
  notas: string;
  precioTotal: number;
  clienteNombre: string;
  proveedorNombre: string;
  proveedorEmpresa: string;
  servicioNombre: string;
  mascotaNombre: string;
  estadoReservaNombre: string;
  estadoReservaColor: string;
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
  notas?: string;
  precioTotal?: number;
  registroVacunacionId?: number;
  modalidadEntrega?: string;
  latitud?: number;
  longitud?: number;
  direccionReferencia?: string;
}

export interface SlotDisponible {
  horaInicio: string;
  horaFin: string;
}

export interface ModalidadInfo {
  id: number;
  modalidad: string;
  costoAdicional: number;
}

export interface DisponibilidadSlots {
  fecha: string;
  slots: SlotDisponible[];
  requiereCertificado: boolean;
  modalidades?: ModalidadInfo[];
}

@Injectable({ providedIn: 'root' })
export class ReservaService {
  constructor(private api: ApiService) {}

  create(data: ReservaRequest): Observable<ApiResponse<Reserva>> {
    return this.api.post('/reservas', data);
  }

  getById(id: number): Observable<ApiResponse<Reserva>> {
    return this.api.get(`/reservas/${id}`);
  }

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Reserva>>> {
    return this.api.getPaged('/reservas', params?.page, params?.size, params);
  }

  getByCliente(clienteId: number): Observable<ApiResponse<PagedResponse<Reserva>>> {
    return this.api.getPaged('/reservas', 0, 50, { clienteId });
  }

  getByMascota(mascotaId: number): Observable<ApiResponse<PagedResponse<Reserva>>> {
    return this.api.getPaged('/reservas', 0, 50, { mascotaId });
  }

  getSlots(proveedorId: number, servicioId: number, desde?: string, hasta?: string, excluirReservaId?: number): Observable<ApiResponse<DisponibilidadSlots[]>> {
    return this.api.get('/reservas/slots', { proveedorId, servicioId, desde, hasta, excluirReservaId });
  }

  update(id: number, data: ReservaRequest): Observable<ApiResponse<Reserva>> {
    return this.api.put(`/reservas/${id}`, data);
  }

  cancelar(id: number, motivo?: string): Observable<ApiResponse<Reserva>> {
    return this.api.put(`/reservas/${id}/cancelar`, { motivo: motivo || '' });
  }
}
