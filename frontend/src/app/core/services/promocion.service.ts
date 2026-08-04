import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Promocion, PromocionRequest, PromocionSummary } from '../models/promocion.model';

@Injectable({ providedIn: 'root' })
export class PromocionService {
  constructor(private api: ApiService) {}

  getActive(): Observable<ApiResponse<Promocion[]>> {
    return this.api.get('/promociones/active');
  }

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Promocion>>> {
    return this.api.getPaged('/promociones', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Promocion>> {
    return this.api.get(`/promociones/${id}`);
  }

  create(data: PromocionRequest): Observable<ApiResponse<Promocion>> {
    return this.api.post('/promociones', data);
  }

  update(id: number, data: PromocionRequest): Observable<ApiResponse<Promocion>> {
    return this.api.put(`/promociones/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/promociones/${id}`);
  }

  getMyPromociones(page = 0, size = 50): Observable<ApiResponse<PagedResponse<PromocionSummary>>> {
    return this.api.getPaged('/proveedores/my/promociones', page, size);
  }

  createMia(data: PromocionRequest): Observable<ApiResponse<Promocion>> {
    return this.api.post('/proveedores/my/promociones', data);
  }

  updateMia(id: number, data: PromocionRequest): Observable<ApiResponse<Promocion>> {
    return this.api.put(`/proveedores/my/promociones/${id}`, data);
  }

  deleteMia(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/proveedores/my/promociones/${id}`);
  }

  notificar(id: number): Observable<ApiResponse<{ enviados: number }>> {
    return this.api.post(`/proveedores/my/promociones/${id}/notificar`, {});
  }
}
