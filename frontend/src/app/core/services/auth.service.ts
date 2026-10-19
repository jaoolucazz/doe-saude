import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegistroRequest, TipoUsuario } from '../models/usuario.model';

const CHAVE_STORAGE = 'doesaude.auth';

interface SessaoArmazenada {
  token: string;
  id: number;
  nome: string;
  email: string;
  tipo: TipoUsuario;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly sessao = signal<SessaoArmazenada | null>(this.carregarDoStorage());

  readonly usuario = computed(() => this.sessao());
  readonly autenticado = computed(() => this.sessao() !== null);
  readonly ehDoador = computed(() => this.sessao()?.tipo === 'DOADOR');
  readonly ehInstituicao = computed(() => this.sessao()?.tipo === 'INSTITUICAO');

  constructor(private readonly http: HttpClient) {}

  get token(): string | null {
    return this.sessao()?.token ?? null;
  }

  registrar(request: RegistroRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/registrar`, request)
      .pipe(tap((resposta) => this.salvarSessao(resposta)));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, request)
      .pipe(tap((resposta) => this.salvarSessao(resposta)));
  }

  logout(): void {
    localStorage.removeItem(CHAVE_STORAGE);
    this.sessao.set(null);
  }

  private salvarSessao(resposta: AuthResponse): void {
    const sessao: SessaoArmazenada = { ...resposta };
    try {
      localStorage.setItem(CHAVE_STORAGE, JSON.stringify(sessao));
    } catch {
      // armazenamento indisponível (ex.: navegação privada) - segue só em memória
    }
    this.sessao.set(sessao);
  }

  private carregarDoStorage(): SessaoArmazenada | null {
    try {
      const bruto = localStorage.getItem(CHAVE_STORAGE);
      return bruto ? (JSON.parse(bruto) as SessaoArmazenada) : null;
    } catch {
      return null;
    }
  }
}
