import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface UploadResponse {
  url: string;
}

@Injectable({ providedIn: 'root' })
export class UploadService {
  constructor(private readonly http: HttpClient) {}

  enviarImagem(arquivo: File): Observable<UploadResponse> {
    const formData = new FormData();
    formData.append('arquivo', arquivo);
    return this.http.post<UploadResponse>(`${environment.apiUrl}/uploads`, formData);
  }
}
