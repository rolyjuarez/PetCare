import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Usuario, UsuarioRequest } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Usuario>>> {
    return this.api.getPaged('/usuarios', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Usuario>> {
    return this.api.get(`/usuarios/${id}`);
  }

  create(data: UsuarioRequest): Observable<ApiResponse<Usuario>> {
    return this.api.post('/usuarios', data);
  }

  update(id: number, data: Partial<UsuarioRequest>): Observable<ApiResponse<Usuario>> {
    return this.api.put(`/usuarios/${id}`, data);
  }

  toggleActivo(id: number): Observable<ApiResponse<Usuario>> {
    return this.api.put(`/usuarios/${id}/toggle`, {});
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/usuarios/${id}`);
  }
}
