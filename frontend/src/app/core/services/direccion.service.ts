import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Direccion, DireccionRequest } from '../models/direccion.model';

@Injectable({ providedIn: 'root' })
export class DireccionService {
  constructor(private api: ApiService) {}

  getById(id: number): Observable<ApiResponse<Direccion>> {
    return this.api.get(`/direcciones/${id}`);
  }

  create(data: DireccionRequest): Observable<ApiResponse<Direccion>> {
    return this.api.post('/direcciones', data);
  }

  update(id: number, data: DireccionRequest): Observable<ApiResponse<Direccion>> {
    return this.api.put(`/direcciones/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/direcciones/${id}`);
  }
}
