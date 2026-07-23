import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';

export interface Servicio {
  id: number;
  nombre: string;
  descripcion: string;
  duracionMinutos: number;
  precioBase: number;
  imagenUrl: string;
  activo: boolean;
  categoria: string;
}

export interface ProveedorSummary {
  id: number;
  nombre: string;
  descripcion: string;
  calificacion: number;
  verificado: boolean;
}

@Injectable({ providedIn: 'root' })
export class ServicioService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Servicio>>> {
    return this.api.getPaged('/servicios', params?.page, params?.size, params);
  }

  getActive(): Observable<ApiResponse<Servicio[]>> {
    return this.api.get('/servicios/active');
  }

  getProveedoresByServicio(servicioId: number): Observable<ApiResponse<ProveedorSummary[]>> {
    return this.api.get(`/proveedores/by-servicio/${servicioId}`);
  }
}
