import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Rol, RolRequest } from '../models/rol.model';

@Injectable({ providedIn: 'root' })
export class RolService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Rol>>> {
    return this.api.getPaged('/roles', params?.page, params?.size, params);
  }

  getAllActivos(): Observable<ApiResponse<Rol[]>> {
    return this.api.get('/roles/activos');
  }

  getById(id: number): Observable<ApiResponse<Rol>> {
    return this.api.get(`/roles/${id}`);
  }

  create(data: RolRequest): Observable<ApiResponse<Rol>> {
    return this.api.post('/roles', data);
  }

  update(id: number, data: RolRequest): Observable<ApiResponse<Rol>> {
    return this.api.put(`/roles/${id}`, data);
  }

  toggleActivo(id: number): Observable<ApiResponse<Rol>> {
    return this.api.put(`/roles/${id}/toggle`, {});
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/roles/${id}`);
  }
}
