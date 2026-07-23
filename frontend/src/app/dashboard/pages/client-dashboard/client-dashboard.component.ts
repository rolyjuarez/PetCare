import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MascotaService } from '../../../core/services/mascota.service';
import { PromocionService, Promocion } from '../../../core/services/promocion.service';
import { Mascota } from '../../../core/models/mascota.model';

@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './client-dashboard.component.html'
})
export class ClientDashboardComponent implements OnInit {
  mascotas = signal<Mascota[]>([]);
  promociones = signal<Promocion[]>([]);

  constructor(
    private mascotaService: MascotaService,
    private promocionService: PromocionService
  ) {}

  ngOnInit(): void {
    this.mascotaService.getMy().subscribe({
      next: (res) => {
        if (res.success) this.mascotas.set(res.data);
      }
    });
    this.promocionService.getActive().subscribe({
      next: (res) => {
        if (res.success) this.promociones.set(res.data);
      }
    });
  }

  calculateAge(fechaNacimiento: any): string {
    if (!fechaNacimiento) return '-';
    let birth: Date;
    if (typeof fechaNacimiento === 'string') birth = new Date(fechaNacimiento);
    else if (Array.isArray(fechaNacimiento)) birth = new Date(fechaNacimiento[0], fechaNacimiento[1] - 1, fechaNacimiento[2]);
    else birth = new Date(fechaNacimiento);
    const today = new Date();
    const years = today.getFullYear() - birth.getFullYear();
    const months = today.getMonth() - birth.getMonth();
    if (years > 0) return `${years} año(s)`;
    if (months > 0) return `${months} mes(es)`;
    return 'Menos de 1 mes';
  }
}
