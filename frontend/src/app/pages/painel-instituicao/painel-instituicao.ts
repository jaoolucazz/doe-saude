import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { FileUploadModule, FileSelectEvent } from 'primeng/fileupload';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { ProgressBarModule } from 'primeng/progressbar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { SelectModule } from 'primeng/select';
import { TagModule } from 'primeng/tag';
import { TextareaModule } from 'primeng/textarea';
import { Doacao, StatusDoacao } from '../../core/models/doacao.model';
import {
  Necessidade,
  PUBLICOS_ALVO,
  TipoBeneficiario,
  TipoNecessidade,
  Urgencia
} from '../../core/models/necessidade.model';
import { TIPOS_SANGUINEOS, UsuarioPerfil } from '../../core/models/usuario.model';
import { DoacaoService } from '../../core/services/doacao.service';
import { NecessidadeService } from '../../core/services/necessidade.service';
import { UploadService } from '../../core/services/upload.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { environment } from '../../../environments/environment';
import { atrasoDev } from '../../core/utils/atraso-dev';

@Component({
  selector: 'app-painel-instituicao',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    ButtonModule,
    CardModule,
    SelectModule,
    InputTextModule,
    TextareaModule,
    InputNumberModule,
    FileUploadModule,
    TagModule,
    MessageModule,
    ProgressBarModule,
    ProgressSpinnerModule
  ],
  templateUrl: './painel-instituicao.html'
})
export class PainelInstituicao {
  private readonly fb = inject(FormBuilder);
  private readonly necessidadeService = inject(NecessidadeService);
  private readonly doacaoService = inject(DoacaoService);
  private readonly uploadService = inject(UploadService);
  private readonly usuarioService = inject(UsuarioService);

  protected readonly tiposItem = [
    { valor: 'SANGUE', rotulo: 'Sangue' },
    { valor: 'MEDICAMENTO', rotulo: 'Medicamento' },
    { valor: 'OUTRO', rotulo: 'Outro item de saúde (fralda, cadeira de rodas, etc.)' }
  ];
  protected readonly opcoesBeneficiario = [
    { valor: 'GRUPO', rotulo: 'Um grupo (hospital, lar de idosos, clínica, etc.)' },
    { valor: 'PESSOA', rotulo: 'Uma pessoa específica' }
  ];
  protected readonly opcoesUrgencia = [
    { valor: 'BAIXA', rotulo: 'Baixa' },
    { valor: 'MEDIA', rotulo: 'Média' },
    { valor: 'ALTA', rotulo: 'Alta' }
  ];
  protected readonly tiposSanguineos = TIPOS_SANGUINEOS;
  protected readonly publicosAlvo = PUBLICOS_ALVO;
  protected readonly minhasNecessidades = signal<Necessidade[]>([]);
  protected readonly doacoesRecebidas = signal<Doacao[]>([]);
  protected readonly perfil = signal<UsuarioPerfil | null>(null);
  protected readonly carregandoPerfil = signal(true);
  protected readonly carregandoNecessidades = signal(true);
  protected readonly carregandoDoacoes = signal(true);
  protected readonly enviando = signal(false);
  protected readonly enviandoFoto = signal(false);
  protected readonly enviandoFotoPerfil = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);
  protected readonly fotoUrl = signal<string | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    tipo: ['SANGUE' as TipoNecessidade, [Validators.required]],
    descricao: ['', [Validators.required]],
    tipoSanguineo: ['', [Validators.required]],
    nomeItem: [''],
    publicoAlvo: ['ADULTO', [Validators.required]],
    tipoBeneficiario: ['GRUPO' as TipoBeneficiario, [Validators.required]],
    nomeBeneficiario: ['', [Validators.required]],
    descricaoBeneficiario: [''],
    quantidade: [1, [Validators.required, Validators.min(1)]],
    urgencia: ['MEDIA' as Urgencia, [Validators.required]]
  });

  constructor() {
    this.carregarNecessidades();
    this.carregarDoacoes();
    this.carregarPerfil();

    this.formulario.controls.tipo.valueChanges.subscribe((tipo) => {
      const tipoSanguineo = this.formulario.controls.tipoSanguineo;
      const nomeItem = this.formulario.controls.nomeItem;
      if (tipo === 'SANGUE') {
        tipoSanguineo.setValidators([Validators.required]);
        nomeItem.clearValidators();
      } else {
        nomeItem.setValidators([Validators.required]);
        tipoSanguineo.clearValidators();
      }
      tipoSanguineo.updateValueAndValidity();
      nomeItem.updateValueAndValidity();
    });
  }

  get ehSangue(): boolean {
    return this.formulario.controls.tipo.value === 'SANGUE';
  }

  get ehPessoa(): boolean {
    return this.formulario.controls.tipoBeneficiario.value === 'PESSOA';
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

  carregarNecessidades(): void {
    this.carregandoNecessidades.set(true);
    this.necessidadeService.listarMinhas().pipe(atrasoDev()).subscribe({
      next: (lista) => {
        this.minhasNecessidades.set(lista);
        this.carregandoNecessidades.set(false);
      },
      error: () => this.carregandoNecessidades.set(false)
    });
  }

  carregarDoacoes(): void {
    this.carregandoDoacoes.set(true);
    this.doacaoService.listarRecebidas().pipe(atrasoDev()).subscribe({
      next: (lista) => {
        this.doacoesRecebidas.set(lista);
        this.carregandoDoacoes.set(false);
      },
      error: () => this.carregandoDoacoes.set(false)
    });
  }

  urlFoto(caminho?: string): string {
    return caminho ? `${environment.arquivosUrl}${caminho}` : '';
  }

  selecionarFotoPerfil(evento: FileSelectEvent): void {
    const arquivo = evento.files?.[0];
    if (!arquivo) {
      return;
    }

    this.enviandoFotoPerfil.set(true);
    this.uploadService.enviarImagem(arquivo).subscribe({
      next: (resposta) => {
        this.usuarioService.atualizarFotoPerfil(resposta.url).subscribe({
          next: (perfil) => {
            this.perfil.set(perfil);
            this.enviandoFotoPerfil.set(false);
          },
          error: () => this.enviandoFotoPerfil.set(false)
        });
      },
      error: () => this.enviandoFotoPerfil.set(false)
    });
  }

  severidadeStatusNecessidade(status: string): 'success' | 'info' | 'danger' {
    if (status === 'ATENDIDA') return 'success';
    if (status === 'CANCELADA') return 'danger';
    return 'info';
  }

  percentual(n: Necessidade): number {
    if (n.quantidade <= 0) return 0;
    return Math.min(Math.round((n.quantidadeAtendida / n.quantidade) * 100), 100);
  }

  selecionarFoto(evento: FileSelectEvent): void {
    const arquivo = evento.files?.[0];
    if (!arquivo) {
      return;
    }

    this.enviandoFoto.set(true);
    this.uploadService.enviarImagem(arquivo).subscribe({
      next: (resposta) => {
        this.fotoUrl.set(resposta.url);
        this.enviandoFoto.set(false);
      },
      error: () => {
        this.mensagemErro.set('Não foi possível enviar a foto. Tente outra imagem.');
        this.enviandoFoto.set(false);
      }
    });
  }

  criarNecessidade(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.mensagemErro.set(null);
    const valores = this.formulario.getRawValue();

    this.necessidadeService
      .criar({
        tipo: valores.tipo,
        descricao: valores.descricao,
        tipoSanguineo: valores.tipo === 'SANGUE' ? (valores.tipoSanguineo as any) || undefined : undefined,
        nomeItem: valores.tipo !== 'SANGUE' ? valores.nomeItem || undefined : undefined,
        publicoAlvo: valores.publicoAlvo as any,
        tipoBeneficiario: valores.tipoBeneficiario,
        nomeBeneficiario: valores.nomeBeneficiario,
        descricaoBeneficiario: valores.descricaoBeneficiario || undefined,
        fotoBeneficiarioUrl: this.fotoUrl() || undefined,
        quantidade: valores.quantidade,
        urgencia: valores.urgencia
      })
      .subscribe({
        next: () => {
          this.enviando.set(false);
          this.fotoUrl.set(null);
          this.formulario.reset({
            tipo: 'SANGUE',
            tipoBeneficiario: 'GRUPO',
            publicoAlvo: 'ADULTO',
            quantidade: 1,
            urgencia: 'MEDIA'
          });
          this.carregarNecessidades();
        },
        error: (erro: HttpErrorResponse) => {
          this.enviando.set(false);
          this.mensagemErro.set(erro.error?.mensagem ?? 'Não foi possível cadastrar a necessidade.');
        }
      });
  }

  cancelarNecessidade(id: number): void {
    this.necessidadeService.cancelar(id).subscribe(() => this.carregarNecessidades());
  }

  atualizarStatusDoacao(id: number, status: StatusDoacao): void {
    this.doacaoService.atualizarStatus(id, status).subscribe(() => {
      this.carregarDoacoes();
      this.carregarNecessidades();
    });
  }
}
