import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse } from '../models/api-response.model';

export interface Disponibilidad {
  id: number;
  proveedorId: number;
  servicioId: number;
  servicioNombre: string;
  diaSemana: number;
  diaSemanaNombre: string;
  horaInicio: string;
  horaFin: string;
  activo: boolean;
}

@Injectable({ providedIn: 'root' })
export class DisponibilidadService {
  constructor(private api: ApiService) {}

  getByProveedorServicio(proveedorId: number, servicioId: number): Observable<ApiResponse<Disponibilidad[]>> {
    return this.api.get(`/disponibilidades/by-proveedor-servicio/${proveedorId}/${servicioId}`);
  }

  getByServicio(servicioId: number): Observable<ApiResponse<Disponibilidad[]>> {
    return this.api.get(`/disponibilidades/by-servicio/${servicioId}`);
  }

  create(data: DisponibilidadRequest): Observable<ApiResponse<Disponibilidad>> {
    return this.api.post('/disponibilidades', data);
  }

  update(id: number, data: DisponibilidadRequest): Observable<ApiResponse<Disponibilidad>> {
    return this.api.put(`/disponibilidades/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/disponibilidades/${id}`);
  }
}

export interface DisponibilidadRequest {
  proveedorId: number;
  servicioId: number;
  diaSemana: number;
  horaInicio: string;
  horaFin: string;
}
