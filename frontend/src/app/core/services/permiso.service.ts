import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Permiso, PermisoRequest } from '../models/permiso.model';

@Injectable({ providedIn: 'root' })
export class PermisoService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Permiso>>> {
    return this.api.getPaged('/permisos', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Permiso>> {
    return this.api.get(`/permisos/${id}`);
  }

  create(data: PermisoRequest): Observable<ApiResponse<Permiso>> {
    return this.api.post('/permisos', data);
  }

  update(id: number, data: PermisoRequest): Observable<ApiResponse<Permiso>> {
    return this.api.put(`/permisos/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/permisos/${id}`);
  }
}
