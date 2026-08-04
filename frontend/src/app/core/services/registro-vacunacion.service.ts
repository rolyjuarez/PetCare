import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';

export interface RegistroVacunacion {
  id: number;
  mascotaId: number;
  mascotaNombre: string;
  vacunaId: number;
  vacunaNombre: string;
  fechaAplicacion: string;
  fechaVencimiento: string;
  lote: string;
  veterinario: string;
  observaciones: string;
  certificadoUrl: string;
}

export interface RegistroVacunacionRequest {
  mascotaId: number;
  vacunaId: number;
  fechaAplicacion: string;
  fechaVencimiento: string;
  lote?: string;
  veterinario?: string;
  observaciones?: string;
  certificadoUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class RegistroVacunacionService {
  constructor(private api: ApiService) {}

  getByMascota(mascotaId: number): Observable<ApiResponse<PagedResponse<RegistroVacunacion>>> {
    return this.api.getPaged('/registros-vacunacion', 0, 50, { mascotaId });
  }

  create(data: RegistroVacunacionRequest): Observable<ApiResponse<RegistroVacunacion>> {
    return this.api.post('/registros-vacunacion', data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/registros-vacunacion/${id}`);
  }
}
