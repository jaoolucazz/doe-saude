export type StatusDoacao = 'AGENDADA' | 'RECEBIDA' | 'NAO_RECEBIDA' | 'CANCELADA';

export interface DoacaoRequest {
  necessidadeId: number;
  quantidade: number;
  dataAgendada: string;
  observacao?: string;
}

export interface Doacao {
  id: number;
  doadorId: number;
  doadorNome: string;
  necessidadeId: number;
  necessidadeDescricao: string;
  quantidade: number;
  dataAgendada: string;
  status: StatusDoacao;
  observacao?: string;
  criadoEm: string;
}
