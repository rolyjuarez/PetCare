import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Mascota, MascotaRequest } from '../models/mascota.model';

@Injectable({ providedIn: 'root' })
export class MascotaService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Mascota>>> {
    return this.api.getPaged('/mascotas', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Mascota>> {
    return this.api.get(`/mascotas/${id}`);
  }

  getByCliente(clienteId: number): Observable<ApiResponse<Mascota[]>> {
    return this.api.get(`/mascotas/cliente/${clienteId}`);
  }

  create(data: MascotaRequest): Observable<ApiResponse<Mascota>> {
    return this.api.post('/mascotas', data);
  }

  update(id: number, data: MascotaRequest): Observable<ApiResponse<Mascota>> {
    return this.api.put(`/mascotas/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/mascotas/${id}`);
  }
}
