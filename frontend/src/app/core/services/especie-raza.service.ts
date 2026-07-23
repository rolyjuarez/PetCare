import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';

export interface Especie {
  id: number;
  nombre: string;
}

export interface Raza {
  id: number;
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class EspecieService {
  constructor(private api: ApiService) {}

  getAll(): Observable<ApiResponse<PagedResponse<Especie>>> {
    return this.api.getPaged('/especies', 0, 100);
  }
}

@Injectable({ providedIn: 'root' })
export class RazaService {
  constructor(private api: ApiService) {}

  getByEspecie(especieId: number): Observable<ApiResponse<Raza[]>> {
    return this.api.get(`/razas/by-especie/${especieId}`);
  }
}
