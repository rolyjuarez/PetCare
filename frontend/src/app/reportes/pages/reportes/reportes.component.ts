import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.component.html'
})
export class ReportesComponent {
  selectedReport = 'reservas';
  fechaInicio = '';
  fechaFin = '';

  reportGenerated = signal(false);
  reportTitle = signal('');
  summaryCards = signal<{ label: string; value: string }[]>([]);

  generarReporte(): void {
    const titles: Record<string, string> = {
      reservas: 'Reporte de Reservas por Periodo',
      ingresos: 'Reporte de Ingresos',
      clientes: 'Reporte de Clientes Activos',
      proveedores: 'Desempeno de Proveedores',
      servicios: 'Servicios Populares'
    };

    this.reportTitle.set(titles[this.selectedReport] || 'Reporte');

    this.summaryCards.set([
      { label: 'Total Registros', value: '245' },
      { label: 'Monto Total', value: '$18,500' },
      { label: 'Promedio', value: '$75.51' },
      { label: 'Variacion', value: '+12.5%' }
    ]);

    this.reportGenerated.set(true);
  }
}
