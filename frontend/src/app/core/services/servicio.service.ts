import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Servicio } from '../models/servicio.model';

@Injectable({ providedIn: 'root' })
export class ServicioService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Servicio>>> {
    return this.api.getPaged('/servicios', params?.page, params?.size, params);
  }

  getAllActivos(): Observable<ApiResponse<Servicio[]>> {
    return this.api.get('/servicios/activos');
  }

  getById(id: number): Observable<ApiResponse<Servicio>> {
    return this.api.get(`/servicios/${id}`);
  }

  create(data: Partial<Servicio>): Observable<ApiResponse<Servicio>> {
    return this.api.post('/servicios', data);
  }

  update(id: number, data: Partial<Servicio>): Observable<ApiResponse<Servicio>> {
    return this.api.put(`/servicios/${id}`, data);
  }

  toggleActivo(id: number): Observable<ApiResponse<Servicio>> {
    return this.api.put(`/servicios/${id}/toggle`, {});
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/servicios/${id}`);
  }
}
