import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';

export interface Vacuna {
  id: number;
  nombre: string;
  descripcion: string;
  periodicidadMeses: number;
}

@Injectable({ providedIn: 'root' })
export class VacunaCatalogService {
  constructor(private api: ApiService) {}

  getAll(): Observable<ApiResponse<PagedResponse<Vacuna>>> {
    return this.api.getPaged('/vacunas', 0, 100);
  }
}
