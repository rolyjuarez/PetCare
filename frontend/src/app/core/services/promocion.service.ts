import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Promocion, PromocionRequest } from '../models/promocion.model';

@Injectable({ providedIn: 'root' })
export class PromocionService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Promocion>>> {
    return this.api.getPaged('/promociones', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Promocion>> {
    return this.api.get(`/promociones/${id}`);
  }

  getByCodigo(codigo: string): Observable<ApiResponse<Promocion>> {
    return this.api.get(`/promociones/codigo/${codigo}`);
  }

  getActivas(): Observable<ApiResponse<Promocion[]>> {
    return this.api.get('/promociones/activas');
  }

  create(data: PromocionRequest): Observable<ApiResponse<Promocion>> {
    return this.api.post('/promociones', data);
  }

  update(id: number, data: PromocionRequest): Observable<ApiResponse<Promocion>> {
    return this.api.put(`/promociones/${id}`, data);
  }

  toggleActivo(id: number): Observable<ApiResponse<Promocion>> {
    return this.api.put(`/promociones/${id}/toggle`, {});
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/promociones/${id}`);
  }
}
