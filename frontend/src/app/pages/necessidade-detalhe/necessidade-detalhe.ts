import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { ProgressBarModule } from 'primeng/progressbar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TagModule } from 'primeng/tag';
import { TextareaModule } from 'primeng/textarea';
import { Necessidade, Urgencia } from '../../core/models/necessidade.model';
import { AuthService } from '../../core/services/auth.service';
import { DoacaoService } from '../../core/services/doacao.service';
import { NecessidadeService } from '../../core/services/necessidade.service';
import { environment } from '../../../environments/environment';
import { atrasoDev } from '../../core/utils/atraso-dev';

@Component({
  selector: 'app-necessidade-detalhe',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    CardModule,
    TagModule,
    InputNumberModule,
    InputTextModule,
    TextareaModule,
    MessageModule,
    ProgressBarModule,
    ProgressSpinnerModule
  ],
  templateUrl: './necessidade-detalhe.html'
})
export class NecessidadeDetalhe {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly necessidadeService = inject(NecessidadeService);
  private readonly doacaoService = inject(DoacaoService);
  private readonly fb = inject(FormBuilder);
  protected readonly auth = inject(AuthService);

  protected readonly necessidade = signal<Necessidade | null>(null);
  protected readonly carregando = signal(true);
  protected readonly enviando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);
  protected readonly agendadoComSucesso = signal(false);

  protected readonly formulario = this.fb.nonNullable.group({
    quantidade: [1, [Validators.required, Validators.min(1)]],
    dataAgendada: ['', [Validators.required]],
    observacao: ['']
  });

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.necessidadeService.buscarPorId(id).pipe(atrasoDev()).subscribe({
      next: (n) => {
        this.necessidade.set(n);
        this.carregando.set(false);
      },
      error: () => this.carregando.set(false)
    });
  }

  severidadeUrgencia(urgencia: Urgencia): 'danger' | 'warn' | 'success' {
    if (urgencia === 'ALTA') return 'danger';
    if (urgencia === 'MEDIA') return 'warn';
    return 'success';
  }

  urlFoto(caminho?: string): string {
    return caminho ? `${environment.arquivosUrl}${caminho}` : '';
  }

  faltam(n: Necessidade): number {
    return Math.max(n.quantidade - n.quantidadeAtendida, 0);
  }

  percentual(n: Necessidade): number {
    if (n.quantidade <= 0) return 0;
    return Math.min(Math.round((n.quantidadeAtendida / n.quantidade) * 100), 100);
  }

  irParaLogin(): void {
    this.router.navigate(['/login']);
  }

  irParaRegistro(): void {
    this.router.navigate(['/registro']);
  }

  agendar(): void {
    const necessidade = this.necessidade();
    if (!necessidade || this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.mensagemErro.set(null);

    const valores = this.formulario.getRawValue();

    this.doacaoService
      .agendar({
        necessidadeId: necessidade.id,
        quantidade: valores.quantidade,
        dataAgendada: valores.dataAgendada,
        observacao: valores.observacao || undefined
      })
      .subscribe({
        next: () => {
          this.enviando.set(false);
          this.agendadoComSucesso.set(true);
        },
        error: (erro: HttpErrorResponse) => {
          this.enviando.set(false);
          this.mensagemErro.set(erro.error?.mensagem ?? 'Não foi possível agendar a doação.');
        }
      });
  }
}
