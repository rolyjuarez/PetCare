import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Reserva, ReservaRequest } from '../models/reserva.model';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Reserva>>> {
    return this.api.getPaged('/reservas', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Reserva>> {
    return this.api.get(`/reservas/${id}`);
  }

  getByCodigo(codigo: string): Observable<ApiResponse<Reserva>> {
    return this.api.get(`/reservas/codigo/${codigo}`);
  }

  create(data: ReservaRequest): Observable<ApiResponse<Reserva>> {
    return this.api.post('/reservas', data);
  }

  update(id: number, data: ReservaRequest): Observable<ApiResponse<Reserva>> {
    return this.api.put(`/reservas/${id}`, data);
  }

  cancelar(id: number): Observable<ApiResponse<Reserva>> {
    return this.api.put(`/reservas/${id}/cancelar`, {});
  }

  completar(id: number): Observable<ApiResponse<Reserva>> {
    return this.api.put(`/reservas/${id}/completar`, {});
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/reservas/${id}`);
  }
}
