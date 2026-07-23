export interface Direccion {
  id: number;
  calle: string;
  numero: string;
  piso: string;
  apartamento: string;
  latitud: number;
  longitud: number;
  referencia: string;
  ciudadId: number;
  ciudadNombre: string;
  estadoId: number;
  estadoNombre: string;
}

export interface DireccionRequest {
  calle: string;
  numero: string;
  piso?: string;
  apartamento?: string;
  latitud?: number;
  longitud?: number;
  referencia?: string;
  ciudadId: number;
  estadoId?: number;
}
