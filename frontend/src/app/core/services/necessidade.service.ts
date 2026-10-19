import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TipoSanguineo } from '../models/usuario.model';
import {
  Necessidade,
  NecessidadeRequest,
  PublicoAlvo,
  StatusNecessidade,
  TipoNecessidade
} from '../models/necessidade.model';

export interface FiltroNecessidades {
  tipo?: TipoNecessidade;
  tipoSanguineo?: TipoSanguineo;
  publicoAlvo?: PublicoAlvo;
  status?: StatusNecessidade;
}

@Injectable({ providedIn: 'root' })
export class NecessidadeService {
  private readonly baseUrl = `${environment.apiUrl}/necessidades`;

  constructor(private readonly http: HttpClient) {}

  listar(filtro: FiltroNecessidades = {}): Observable<Necessidade[]> {
    let params = new HttpParams();
    if (filtro.tipo) params = params.set('tipo', filtro.tipo);
    if (filtro.tipoSanguineo) params = params.set('tipoSanguineo', filtro.tipoSanguineo);
    if (filtro.publicoAlvo) params = params.set('publicoAlvo', filtro.publicoAlvo);
    if (filtro.status) params = params.set('status', filtro.status);

    return this.http.get<Necessidade[]>(this.baseUrl, { params });
  }

  buscarPorId(id: number): Observable<Necessidade> {
    return this.http.get<Necessidade>(`${this.baseUrl}/${id}`);
  }

  listarMinhas(): Observable<Necessidade[]> {
    return this.http.get<Necessidade[]>(`${this.baseUrl}/minhas`);
  }

  criar(request: NecessidadeRequest): Observable<Necessidade> {
    return this.http.post<Necessidade>(this.baseUrl, request);
  }

  cancelar(id: number): Observable<Necessidade> {
    return this.http.patch<Necessidade>(`${this.baseUrl}/${id}/cancelar`, {});
  }
}
