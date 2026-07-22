import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Proveedor } from '../models/proveedor.model';

@Injectable({ providedIn: 'root' })
export class ProveedorService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Proveedor>>> {
    return this.api.getPaged('/proveedores', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Proveedor>> {
    return this.api.get(`/proveedores/${id}`);
  }

  getVerificados(): Observable<ApiResponse<Proveedor[]>> {
    return this.api.get('/proveedores/verificados');
  }

  create(data: Partial<Proveedor>): Observable<ApiResponse<Proveedor>> {
    return this.api.post('/proveedores', data);
  }

  update(id: number, data: Partial<Proveedor>): Observable<ApiResponse<Proveedor>> {
    return this.api.put(`/proveedores/${id}`, data);
  }

  verificar(id: number): Observable<ApiResponse<Proveedor>> {
    return this.api.put(`/proveedores/${id}/verificar`, {});
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/proveedores/${id}`);
  }
}
