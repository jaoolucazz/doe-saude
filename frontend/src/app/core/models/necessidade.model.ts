import { TipoSanguineo } from './usuario.model';

export type TipoNecessidade = 'SANGUE' | 'MEDICAMENTO' | 'OUTRO';
export type Urgencia = 'BAIXA' | 'MEDIA' | 'ALTA';
export type StatusNecessidade = 'ABERTA' | 'ATENDIDA' | 'CANCELADA';
export type PublicoAlvo = 'BEBE' | 'CRIANCA' | 'ADULTO' | 'IDOSO';
export type TipoBeneficiario = 'PESSOA' | 'GRUPO';

export const PUBLICOS_ALVO: { valor: PublicoAlvo; rotulo: string }[] = [
  { valor: 'BEBE', rotulo: 'Bebês' },
  { valor: 'CRIANCA', rotulo: 'Crianças' },
  { valor: 'ADULTO', rotulo: 'Adultos' },
  { valor: 'IDOSO', rotulo: 'Idosos' }
];

export interface NecessidadeRequest {
  tipo: TipoNecessidade;
  descricao: string;
  tipoSanguineo?: TipoSanguineo;
  nomeItem?: string;
  publicoAlvo: PublicoAlvo;
  tipoBeneficiario: TipoBeneficiario;
  nomeBeneficiario: string;
  descricaoBeneficiario?: string;
  fotoBeneficiarioUrl?: string;
  quantidade: number;
  urgencia: Urgencia;
}

export interface Necessidade {
  id: number;
  instituicaoId: number;
  nomeInstituicao: string;
  tipo: TipoNecessidade;
  descricao: string;
  tipoSanguineo?: TipoSanguineo;
  nomeItem?: string;
  publicoAlvo: PublicoAlvo;
  tipoBeneficiario: TipoBeneficiario;
  nomeBeneficiario: string;
  descricaoBeneficiario?: string;
  fotoBeneficiarioUrl?: string;
  quantidade: number;
  quantidadeAtendida: number;
  urgencia: Urgencia;
  status: StatusNecessidade;
  criadoEm: string;
}
