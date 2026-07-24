import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';

export interface Especie { id: number; nombre: string; descripcion: string; }

@Injectable({ providedIn: 'root' })
export class EspecieService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Especie>>> {
    return this.api.getPaged('/especies', params?.page ?? 0, params?.size ?? 100, params);
  }
}
