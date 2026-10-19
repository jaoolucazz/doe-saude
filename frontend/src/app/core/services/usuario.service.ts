import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UsuarioPerfil } from '../models/usuario.model';

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  constructor(private readonly http: HttpClient) {}

  meuPerfil(): Observable<UsuarioPerfil> {
    return this.http.get<UsuarioPerfil>(`${environment.apiUrl}/usuarios/me`);
  }

  atualizarFotoPerfil(url: string): Observable<UsuarioPerfil> {
    return this.http.patch<UsuarioPerfil>(`${environment.apiUrl}/usuarios/me/foto`, { url });
  }
}
