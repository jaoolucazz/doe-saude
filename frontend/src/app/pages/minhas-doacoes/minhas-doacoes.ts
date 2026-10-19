import { Component, inject, signal } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { FileSelectEvent, FileUploadModule } from 'primeng/fileupload';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TagModule } from 'primeng/tag';
import { Doacao, StatusDoacao } from '../../core/models/doacao.model';
import { TIPOS_SANGUINEOS, UsuarioPerfil } from '../../core/models/usuario.model';
import { DoacaoService } from '../../core/services/doacao.service';
import { UploadService } from '../../core/services/upload.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { environment } from '../../../environments/environment';
import { atrasoDev } from '../../core/utils/atraso-dev';

@Component({
  selector: 'app-minhas-doacoes',
  standalone: true,
  imports: [CardModule, TagModule, ButtonModule, ProgressSpinnerModule, FileUploadModule],
  templateUrl: './minhas-doacoes.html'
})
export class MinhasDoacoes {
  private readonly doacaoService = inject(DoacaoService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly uploadService = inject(UploadService);

  protected readonly doacoes = signal<Doacao[]>([]);
  protected readonly perfil = signal<UsuarioPerfil | null>(null);
  protected readonly carregandoDoacoes = signal(true);
  protected readonly carregandoPerfil = signal(true);
  protected readonly cancelandoId = signal<number | null>(null);
  protected readonly enviandoFoto = signal(false);

  constructor() {
    this.carregarDoacoes();
    this.carregarPerfil();
  }

  carregarPerfil(): void {
    this.carregandoPerfil.set(true);
    this.usuarioService.meuPerfil().pipe(atrasoDev()).subscribe({
      next: (perfil) => {
        this.perfil.set(perfil);
        this.carregandoPerfil.set(false);
      },
      error: () => this.carregandoPerfil.set(false)
    });
  }

  carregarDoacoes(): void {
    this.carregandoDoacoes.set(true);
    this.doacaoService.listarMinhas().pipe(atrasoDev()).subscribe({
      next: (lista) => {
        this.doacoes.set(lista);
        this.carregandoDoacoes.set(false);
      },
      error: () => this.carregandoDoacoes.set(false)
    });
  }

  urlFoto(caminho?: string): string {
    return caminho ? `${environment.arquivosUrl}${caminho}` : '';
  }

  selecionarFoto(evento: FileSelectEvent): void {
    const arquivo = evento.files?.[0];
    if (!arquivo) {
      return;
    }

    this.enviandoFoto.set(true);
    this.uploadService.enviarImagem(arquivo).subscribe({
      next: (resposta) => {
        this.usuarioService.atualizarFotoPerfil(resposta.url).subscribe({
          next: (perfil) => {
            this.perfil.set(perfil);
            this.enviandoFoto.set(false);
          },
          error: () => this.enviandoFoto.set(false)
        });
      },
      error: () => this.enviandoFoto.set(false)
    });
  }

  rotuloTipoSanguineo(valor?: string): string {
    return TIPOS_SANGUINEOS.find((t) => t.valor === valor)?.rotulo ?? '';
  }

  severidade(status: StatusDoacao): 'info' | 'success' | 'danger' | 'secondary' {
    if (status === 'RECEBIDA') return 'success';
    if (status === 'NAO_RECEBIDA') return 'danger';
    if (status === 'CANCELADA') return 'secondary';
    return 'info';
  }

  rotulo(status: StatusDoacao): string {
    if (status === 'RECEBIDA') return 'Recebida';
    if (status === 'NAO_RECEBIDA') return 'Não recebida';
    if (status === 'CANCELADA') return 'Cancelada por você';
    return 'Aguardando confirmação';
  }

  cancelar(id: number): void {
    this.cancelandoId.set(id);
    this.doacaoService.cancelar(id).subscribe({
      next: () => {
        this.cancelandoId.set(null);
        this.carregarDoacoes();
      },
      error: () => this.cancelandoId.set(null)
    });
  }
}
