import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Promocion } from '../models/promocion.model';

export type { Promocion };

@Injectable({ providedIn: 'root' })
export class PromocionService {
  constructor(private api: ApiService) {}

  getActive(): Observable<ApiResponse<Promocion[]>> {
    return this.api.get('/promociones/active');
  }

  getAll(params?: any): Observable<ApiResponse<PagedResponse<Promocion>>> {
    return this.api.getPaged('/promociones', params?.page, params?.size, params);
  }
}
