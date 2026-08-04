import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Proveedor, ProveedorFullCreate, ProveedorFullUpdate, ProveedorServicio, ProveedorServicioRequest } from '../models/proveedor.model';

@Injectable({ providedIn: 'root' })
export class ProveedorService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Proveedor>>> {
    return this.api.getPaged('/proveedores', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Proveedor>> {
    return this.api.get(`/proveedores/${id}`);
  }

  getMe(): Observable<ApiResponse<Proveedor>> {
    return this.api.get('/proveedores/my');
  }

  setRequiereCertificado(proveedorId: number, servicioId: number, requiereCertificado: boolean): Observable<ApiResponse<Proveedor>> {
    return this.api.put(`/proveedores/${proveedorId}/especialidad/${servicioId}`, { requiereCertificado });
  }

  createFull(data: ProveedorFullCreate): Observable<ApiResponse<Proveedor>> {
    return this.api.post('/proveedores/full', data);
  }

  updateFull(id: number, data: ProveedorFullUpdate): Observable<ApiResponse<Proveedor>> {
    return this.api.put(`/proveedores/${id}/full`, data);
  }

  hasReservas(id: number): Observable<ApiResponse<{ hasReservas: boolean }>> {
    return this.api.get(`/proveedores/${id}/has-reservas`);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/proveedores/${id}`);
  }

  myServicios(): Observable<ApiResponse<ProveedorServicio[]>> {
    return this.api.get('/proveedores/my/servicios');
  }

  createServicio(data: ProveedorServicioRequest): Observable<ApiResponse<ProveedorServicio>> {
    return this.api.post('/proveedores/my/servicios', data);
  }

  updateServicio(id: number, data: ProveedorServicioRequest): Observable<ApiResponse<ProveedorServicio>> {
    return this.api.put(`/proveedores/my/servicios/${id}`, data);
  }

  setServicioActivo(id: number, activo: boolean): Observable<ApiResponse<ProveedorServicio>> {
    return this.api.put(`/proveedores/my/servicios/${id}/activo`, { activo });
  }

  deleteServicio(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/proveedores/my/servicios/${id}`);
  }
}
