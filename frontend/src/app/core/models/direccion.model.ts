export interface Direccion {
  id: number;
  calle: string;
  numero: string;
  colonia: string;
  codigoPostal: string;
  ciudad: string;
  estado: string;
  pais: string;
  latitud: number;
  longitud: number;
}

export interface DireccionRequest {
  calle: string;
  numero: string;
  colonia: string;
  codigoPostal: string;
  ciudad: string;
  estado: string;
  pais: string;
  latitud?: number;
  longitud?: number;
}
