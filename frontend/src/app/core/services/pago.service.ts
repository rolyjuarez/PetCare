import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Pago, PagoRequest } from '../models/pago.model';

@Injectable({ providedIn: 'root' })
export class PagoService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Pago>>> {
    return this.api.getPaged('/pagos', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Pago>> {
    return this.api.get(`/pagos/${id}`);
  }

  getByReserva(reservaId: number): Observable<ApiResponse<Pago>> {
    return this.api.get(`/pagos/reserva/${reservaId}`);
  }

  create(data: PagoRequest): Observable<ApiResponse<Pago>> {
    return this.api.post('/pagos', data);
  }

  reembolsar(id: number): Observable<ApiResponse<Pago>> {
    return this.api.put(`/pagos/${id}/reembolsar`, {});
  }
}
