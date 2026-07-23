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
}
