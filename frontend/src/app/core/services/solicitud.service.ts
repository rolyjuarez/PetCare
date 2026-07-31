import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ApiResponse, PagedResponse } from '../models/api-response.model';
import { SolicitudReserva } from '../models/solicitud.model';

@Injectable({ providedIn: 'root' })
export class SolicitudService {
  constructor(private api: ApiService) {}

  listar(estado?: string, page: number = 0, size: number = 20): Observable<ApiResponse<PagedResponse<SolicitudReserva>>> {
    return this.api.getPaged('/proveedor/solicitudes', page, size, { estado });
  }

  aceptar(id: number): Observable<ApiResponse<SolicitudReserva>> {
    return this.api.put(`/proveedor/solicitudes/${id}/aceptar`, {});
  }

  rechazar(id: number, motivoRechazo: string): Observable<ApiResponse<SolicitudReserva>> {
    return this.api.put(`/proveedor/solicitudes/${id}/rechazar`, { motivoRechazo });
  }
}
