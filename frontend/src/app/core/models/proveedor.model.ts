export interface Proveedor {
  id: number;
  personaId: number;
  personaNombre: string;
  personaTelefono: string;
  latitud: number;
  longitud: number;
  radioCoberturaKm: number;
  descripcion: string;
  verificado: boolean;
  calificacion: number;
  especialidades: string[];
}
