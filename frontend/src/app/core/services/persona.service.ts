import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { Persona, PersonaRequest, PersonaSummary } from '../models/persona.model';

@Injectable({ providedIn: 'root' })
export class PersonaService {
  constructor(private api: ApiService) {}

  getAll(params?: any): Observable<ApiResponse<PagedResponse<PersonaSummary>>> {
    return this.api.getPaged('/personas', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<Persona>> {
    return this.api.get(`/personas/${id}`);
  }

  create(data: PersonaRequest): Observable<ApiResponse<Persona>> {
    return this.api.post('/personas', data);
  }

  update(id: number, data: PersonaRequest): Observable<ApiResponse<Persona>> {
    return this.api.put(`/personas/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/personas/${id}`);
  }
}
