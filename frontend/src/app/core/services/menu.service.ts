import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { MenuItem, Submenu } from '../models/menu.model';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class MenuService {
  private apiUrl = '/api/v1/menus';

  constructor(private http: HttpClient, private api: ApiService) {}

  getMyMenus(): Observable<MenuItem[]> {
    return this.http.get<ApiResponse<MenuItem[]>>(`${this.apiUrl}/my-menus`).pipe(
      map(response => response.data || [])
    );
  }

  getAll(params?: any): Observable<ApiResponse<PagedResponse<MenuItem>>> {
    return this.api.getPaged('/menus', params?.page, params?.size, params);
  }

  getById(id: number): Observable<ApiResponse<MenuItem>> {
    return this.api.get(`/menus/${id}`);
  }

  create(data: Partial<MenuItem>): Observable<ApiResponse<MenuItem>> {
    return this.api.post('/menus', data);
  }

  update(id: number, data: Partial<MenuItem>): Observable<ApiResponse<MenuItem>> {
    return this.api.put(`/menus/${id}`, data);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.api.delete(`/menus/${id}`);
  }
}
