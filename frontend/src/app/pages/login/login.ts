import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../core/services/auth.service';

const MENSAGENS_MOTIVO: Record<string, string> = {
  'login-necessario': 'Entre com sua conta para acessar essa página.',
  'apenas-doador': 'Essa área é só para contas de doador.',
  'apenas-instituicao': 'Essa área é só para contas de instituição.'
};

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, ButtonModule, CardModule, InputTextModule, MessageModule],
  templateUrl: './login.html'
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly enviando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);
  protected readonly mensagemMotivo = signal<string | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required]]
  });

  constructor() {
    const motivo = this.route.snapshot.queryParamMap.get('motivo');
    if (motivo) {
      this.mensagemMotivo.set(MENSAGENS_MOTIVO[motivo] ?? null);
    }
  }

  enviar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.mensagemErro.set(null);

    this.auth.login(this.formulario.getRawValue()).subscribe({
      next: () => {
        this.enviando.set(false);
        this.router.navigate(['/necessidades']);
      },
      error: (erro: HttpErrorResponse) => {
        this.enviando.set(false);
        this.mensagemErro.set(erro.error?.mensagem ?? 'Não foi possível entrar. Tente novamente.');
      }
    });
  }
}
