import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Doacao, DoacaoRequest, StatusDoacao } from '../models/doacao.model';

@Injectable({ providedIn: 'root' })
export class DoacaoService {
  private readonly baseUrl = `${environment.apiUrl}/doacoes`;

  constructor(private readonly http: HttpClient) {}

  agendar(request: DoacaoRequest): Observable<Doacao> {
    return this.http.post<Doacao>(this.baseUrl, request);
  }

  listarMinhas(): Observable<Doacao[]> {
    return this.http.get<Doacao[]>(`${this.baseUrl}/minhas`);
  }

  listarRecebidas(): Observable<Doacao[]> {
    return this.http.get<Doacao[]>(`${this.baseUrl}/recebidas`);
  }

  atualizarStatus(id: number, status: StatusDoacao): Observable<Doacao> {
    return this.http.patch<Doacao>(`${this.baseUrl}/${id}/status`, { status });
  }

  cancelar(id: number): Observable<Doacao> {
    return this.http.patch<Doacao>(`${this.baseUrl}/${id}/cancelar`, {});
  }
}
