import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse } from '../models/api-response.model';

export interface Raza { id: number; nombre: string; especieId: number; }

@Injectable({ providedIn: 'root' })
export class RazaService {
  constructor(private api: ApiService) {}

  getByEspecie(especieId: number): Observable<ApiResponse<Raza[]>> {
    return this.api.get(`/razas/by-especie/${especieId}`);
  }
}
