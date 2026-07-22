import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Cliente } from '../models/cliente.model';

@Injectable({ providedIn: 'root' })
export class ClienteService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Cliente>>> {
    return this.api.getPaged('/clientes', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Cliente>> {
    return this.api.get(`/clientes/${id}`);
  }

  create(data: Partial<Cliente>): Observable<ApiResponse<Cliente>> {
    return this.api.post('/clientes', data);
  }

  update(id: number, data: Partial<Cliente>): Observable<ApiResponse<Cliente>> {
    return this.api.put(`/clientes/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/clientes/${id}`);
  }
}
