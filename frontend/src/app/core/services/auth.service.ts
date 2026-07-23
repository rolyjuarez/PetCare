import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, map } from 'rxjs';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { LoginRequest, LoginResponse, UserInfo, RegisterRequest, Ciudad } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = '/api/v1/auth';
  private currentUserSignal = signal<UserInfo | null>(null);

  currentUser = this.currentUserSignal.asReadonly();
  isAuthenticated = computed(() => !!this.currentUserSignal());

  constructor(private http: HttpClient, private router: Router) {
    this.loadFromStorage();
  }

  login(request: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        if (response.success) {
          localStorage.setItem('accessToken', response.data.accessToken);
          localStorage.setItem('refreshToken', response.data.refreshToken);
          localStorage.setItem('userInfo', JSON.stringify(response.data.userInfo));
          this.currentUserSignal.set(response.data.userInfo);
        }
      })
    );
  }

  register(request: RegisterRequest): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/register`, request);
  }

  forgotPassword(email: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.apiUrl}/reset-password`, { token, newPassword });
  }

  getCiudades(): Observable<Ciudad[]> {
    return this.http.get<ApiResponse<PagedResponse<Ciudad>>>('/api/v1/ciudades?size=100').pipe(
      map(response => response.data?.content || [])
    );
  }

  refresh(): Observable<ApiResponse<LoginResponse>> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(response => {
        if (response.success) {
          localStorage.setItem('accessToken', response.data.accessToken);
          localStorage.setItem('refreshToken', response.data.refreshToken);
          localStorage.setItem('userInfo', JSON.stringify(response.data.userInfo));
          this.currentUserSignal.set(response.data.userInfo);
        }
      })
    );
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/logout`, {}).subscribe();
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userInfo');
    this.currentUserSignal.set(null);
    this.router.navigate(['/']);
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  hasRole(role: string): boolean {
    const user = this.currentUserSignal();
    if (user?.roles?.includes(role)) return true;
    try {
      const stored = JSON.parse(localStorage.getItem('userInfo') ?? 'null');
      return stored?.roles?.includes(role) ?? false;
    } catch {
      return false;
    }
  }

  hasPermission(permission: string): boolean {
    const user = this.currentUserSignal();
    if (user?.permissions?.includes(permission)) return true;
    try {
      const stored = JSON.parse(localStorage.getItem('userInfo') ?? 'null');
      return stored?.permissions?.includes(permission) ?? false;
    } catch {
      return false;
    }
  }

  private loadFromStorage(): void {
    const token = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    const userInfoStr = localStorage.getItem('userInfo');
    if (token && refreshToken) {
      if (userInfoStr) {
        try {
          this.currentUserSignal.set(JSON.parse(userInfoStr));
        } catch {}
      }
      this.refresh().subscribe({ error: () => {} });
    }
  }
}
