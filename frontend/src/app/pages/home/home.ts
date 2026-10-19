import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { MessageModule } from 'primeng/message';
import { ProgressBarModule } from 'primeng/progressbar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { SelectModule } from 'primeng/select';
import { TagModule } from 'primeng/tag';
import { Necessidade, PUBLICOS_ALVO, PublicoAlvo, TipoNecessidade, Urgencia } from '../../core/models/necessidade.model';
import { TIPOS_SANGUINEOS, TipoSanguineo } from '../../core/models/usuario.model';
import { NecessidadeService } from '../../core/services/necessidade.service';
import { environment } from '../../../environments/environment';
import { atrasoDev } from '../../core/utils/atraso-dev';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FormsModule, CardModule, SelectModule, TagModule, ButtonModule, ProgressBarModule, ProgressSpinnerModule, MessageModule],
  templateUrl: './home.html'
})
export class Home {
  private readonly necessidadeService = inject(NecessidadeService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  private static readonly MENSAGENS_MOTIVO: Record<string, string> = {
    'apenas-doador': 'Essa área é só para contas de doador.',
    'apenas-instituicao': 'Essa área é só para contas de instituição.'
  };

  protected readonly mensagemMotivo = signal<string | null>(null);

  protected readonly opcoesTipo = [
    { valor: '', rotulo: 'Todos os tipos' },
    { valor: 'SANGUE', rotulo: 'Sangue' },
    { valor: 'MEDICAMENTO', rotulo: 'Medicamento' },
    { valor: 'OUTRO', rotulo: 'Outro item de saúde' }
  ];
  protected readonly opcoesTipoSanguineo = [{ valor: '', rotulo: 'Qualquer tipo sanguíneo' }, ...TIPOS_SANGUINEOS];
  protected readonly opcoesPublicoAlvo = [{ valor: '', rotulo: 'Qualquer público' }, ...PUBLICOS_ALVO];

  protected readonly necessidades = signal<Necessidade[]>([]);
  protected readonly carregando = signal(true);

  protected readonly totalPessoasAjudadas = computed(
    () => this.necessidades().filter((n) => n.quantidadeAtendida > 0).length
  );
  protected readonly totalItensRecebidos = computed(() =>
    this.necessidades().reduce((soma, n) => soma + n.quantidadeAtendida, 0)
  );

  protected filtroTipo: TipoNecessidade | '' = '';
  protected filtroTipoSanguineo: TipoSanguineo | '' = '';
  protected filtroPublicoAlvo: PublicoAlvo | '' = '';

  constructor() {
    this.buscar();
    const motivo = this.route.snapshot.queryParamMap.get('motivo');
    if (motivo) {
      this.mensagemMotivo.set(Home.MENSAGENS_MOTIVO[motivo] ?? null);
    }
  }

  severidadeUrgencia(urgencia: Urgencia): 'danger' | 'warn' | 'success' {
    if (urgencia === 'ALTA') return 'danger';
    if (urgencia === 'MEDIA') return 'warn';
    return 'success';
  }

  iconeTipo(n: Necessidade): string {
    if (n.tipo === 'SANGUE') return 'pi pi-heart-fill';
    if (n.tipo === 'MEDICAMENTO') return 'pi pi-box';
    return 'pi pi-plus-circle';
  }

  urlFoto(caminho?: string): string {
    return caminho ? `${environment.arquivosUrl}${caminho}` : '';
  }

  tituloNecessidade(n: Necessidade): string {
    if (n.tipo === 'SANGUE') {
      return `Sangue ${n.tipoSanguineo ?? ''}`;
    }
    return n.nomeItem ?? 'Item de saúde';
  }

  percentual(n: Necessidade): number {
    if (n.quantidade <= 0) return 0;
    return Math.min(Math.round((n.quantidadeAtendida / n.quantidade) * 100), 100);
  }

  abrir(id: number): void {
    this.router.navigate(['/necessidades', id]);
  }

  buscar(): void {
    this.carregando.set(true);
    this.necessidadeService
      .listar({
        tipo: this.filtroTipo || undefined,
        tipoSanguineo: this.filtroTipoSanguineo || undefined,
        publicoAlvo: this.filtroPublicoAlvo || undefined,
        status: 'ABERTA'
      })
      .pipe(atrasoDev())
      .subscribe({
        next: (lista) => {
          this.necessidades.set(lista);
          this.carregando.set(false);
        },
        error: () => this.carregando.set(false)
      });
  }
}
