export interface Bitacora {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  accion: string;
  entidad: string;
  entidadId: number;
  detalles: string;
  ip: string;
  fecha: string;
}
