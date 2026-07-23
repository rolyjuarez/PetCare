import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { MenuItem } from '../models/menu.model';

@Injectable({ providedIn: 'root' })
export class MenuService {
  private apiUrl = '/api/v1/menus';

  constructor(private http: HttpClient) {}

  getMyMenus(): Observable<MenuItem[]> {
    return this.http.get<ApiResponse<MenuItem[]>>(`${this.apiUrl}/my-menus`).pipe(
      map(response => response.data || [])
    );
  }
}
