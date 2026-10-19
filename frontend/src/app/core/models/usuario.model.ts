export type TipoUsuario = 'DOADOR' | 'INSTITUICAO' | 'ADMIN';

export type TipoSanguineo =
  | 'A_POSITIVO'
  | 'A_NEGATIVO'
  | 'B_POSITIVO'
  | 'B_NEGATIVO'
  | 'AB_POSITIVO'
  | 'AB_NEGATIVO'
  | 'O_POSITIVO'
  | 'O_NEGATIVO';

export const TIPOS_SANGUINEOS: { valor: TipoSanguineo; rotulo: string }[] = [
  { valor: 'A_POSITIVO', rotulo: 'A+' },
  { valor: 'A_NEGATIVO', rotulo: 'A-' },
  { valor: 'B_POSITIVO', rotulo: 'B+' },
  { valor: 'B_NEGATIVO', rotulo: 'B-' },
  { valor: 'AB_POSITIVO', rotulo: 'AB+' },
  { valor: 'AB_NEGATIVO', rotulo: 'AB-' },
  { valor: 'O_POSITIVO', rotulo: 'O+' },
  { valor: 'O_NEGATIVO', rotulo: 'O-' }
];

export interface RegistroRequest {
  nome: string;
  email: string;
  senha: string;
  tipo: TipoUsuario;
  telefone?: string;
  tipoSanguineo?: TipoSanguineo;
  nomeInstituicao?: string;
  endereco?: string;
}

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface AuthResponse {
  token: string;
  id: number;
  nome: string;
  email: string;
  tipo: TipoUsuario;
}

export interface UsuarioPerfil {
  id: number;
  nome: string;
  email: string;
  telefone?: string;
  tipo: TipoUsuario;
  tipoSanguineo?: TipoSanguineo;
  nomeInstituicao?: string;
  endereco?: string;
  fotoPerfilUrl?: string;
}
