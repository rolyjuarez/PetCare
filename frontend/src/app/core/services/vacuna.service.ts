import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Vacuna, VacunaRequest, MascotaVacuna } from '../models/vacuna.model';

@Injectable({ providedIn: 'root' })
export class VacunaService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Vacuna>>> {
    return this.api.getPaged('/vacunas', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Vacuna>> {
    return this.api.get(`/vacunas/${id}`);
  }

  getByMascota(mascotaId: number): Observable<ApiResponse<MascotaVacuna[]>> {
    return this.api.get(`/vacunas/mascota/${mascotaId}`);
  }

  create(data: VacunaRequest): Observable<ApiResponse<Vacuna>> {
    return this.api.post('/vacunas', data);
  }

  aplicar(mascotaId: number, vacunaId: number, data: Partial<MascotaVacuna>): Observable<ApiResponse<MascotaVacuna>> {
    return this.api.post(`/vacunas/mascota/${mascotaId}/vacuna/${vacunaId}`, data);
  }

  update(id: number, data: VacunaRequest): Observable<ApiResponse<Vacuna>> {
    return this.api.put(`/vacunas/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/vacunas/${id}`);
  }
}
