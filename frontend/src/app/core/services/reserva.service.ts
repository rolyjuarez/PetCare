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
  fechaReserva: string;
  fechaInicio: string;
  fechaFin: string;
  horaInicio: string;
  horaFin: string;
  notas: string;
  precioTotal: number;
  clienteNombre: string;
  proveedorNombre: string;
  servicioNombre: string;
  mascotaNombre: string;
  estadoReservaNombre: string;
  estadoReservaColor: string;
}

export interface ReservaRequest {
  clienteId: number;
  proveedorId: number;
  servicioId: number;
  mascotaId: number;
  fechaReserva: string;
  fechaInicio: string;
  horaInicio: string;
  horaFin: string;
  notas?: string;
  precioTotal?: number;
}

@Injectable({ providedIn: 'root' })
export class ReservaService {
  constructor(private api: ApiService) {}

  create(data: ReservaRequest): Observable<ApiResponse<Reserva>> {
    return this.api.post('/reservas', data);
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
}
