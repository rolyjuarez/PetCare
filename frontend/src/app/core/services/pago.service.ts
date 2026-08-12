import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Pago, PagoRequest, PagoResponse, ProcesarPagoRequest } from '../models/pago.model';

@Injectable({ providedIn: 'root' })
export class PagoService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Pago>>> {
    return this.api.getPaged('/pagos', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Pago>> {
    return this.api.get(`/pagos/${id}`);
  }

  getByReserva(reservaId: number): Observable<ApiResponse<PagoResponse>> {
    return this.api.get(`/pagos/reserva/${reservaId}`);
  }

  crearPagoDeReserva(reservaId: number): Observable<ApiResponse<PagoResponse>> {
    return this.api.post(`/pagos/reserva/${reservaId}`, {});
  }

  historial(reservaId: number, page = 0, size = 20): Observable<ApiResponse<PagedResponse<PagoResponse>>> {
    return this.api.getPaged(`/pagos/reserva/${reservaId}/historial`, page, size);
  }

  create(data: PagoRequest): Observable<ApiResponse<Pago>> {
    return this.api.post('/pagos', data);
  }

  procesar(id: number, data: ProcesarPagoRequest): Observable<ApiResponse<PagoResponse>> {
    return this.api.post(`/pagos/${id}/procesar`, data);
  }

  reembolsar(id: number): Observable<ApiResponse<PagoResponse>> {
    return this.api.post(`/pagos/${id}/reembolsar`, {});
  }
}
