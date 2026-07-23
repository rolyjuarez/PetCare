export interface LoginRequest { username: string; password: string; }
export interface LoginResponse { accessToken: string; refreshToken: string; tokenType: string; expiresIn: number; userInfo: UserInfo; }
export interface UserInfo { id: number; username: string; nombre: string; roles: string[]; permissions: string[]; }
export interface RefreshTokenRequest { refreshToken: string; }

export interface RegisterRequest {
  username: string;
  password: string;
  nombre: string;
  primerApellido: string;
  segundoApellido?: string | null;
  ci: string;
  telefono: string;
  email: string;
  fechaNacimiento: string;
  genero: string;
  calle: string;
  numero?: string | null;
  referencia?: string | null;
  ciudadId: number;
  latitud?: number | null;
  longitud?: number | null;
}

export interface Ciudad { id: number; nombre: string; codigo: string; latitud: number | null; longitud: number | null; }
