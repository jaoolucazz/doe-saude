import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-esqueci-senha',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, ButtonModule, CardModule, InputTextModule, MessageModule],
  templateUrl: './esqueci-senha.html'
})
export class EsqueciSenha {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly etapa = signal<'EMAIL' | 'CODIGO'>('EMAIL');
  protected readonly enviando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);
  protected readonly mensagemOk = signal<string | null>(null);

  protected readonly formularioEmail = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]]
  });

  protected readonly formularioCodigo = this.fb.nonNullable.group({
    codigo: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
    novaSenha: ['', [Validators.required, Validators.minLength(6)]],
    confirmarSenha: ['', [Validators.required]]
  });

  pedirCodigo(): void {
    if (this.formularioEmail.invalid) {
      this.formularioEmail.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.mensagemErro.set(null);

    this.auth.esqueciSenha({ email: this.formularioEmail.getRawValue().email }).subscribe({
      next: () => {
        this.enviando.set(false);
        this.etapa.set('CODIGO');
        this.mensagemOk.set(
          'Se esse e-mail existir e tiver telefone cadastrado, enviamos um código de 6 dígitos por WhatsApp. Ele vale por 10 minutos.'
        );
      },
      error: (erro: HttpErrorResponse) => {
        this.enviando.set(false);
        this.mensagemErro.set(erro.error?.mensagem ?? 'Não foi possível enviar o código. Tente novamente.');
      }
    });
  }

  redefinirSenha(): void {
    if (this.formularioCodigo.invalid) {
      this.formularioCodigo.markAllAsTouched();
      return;
    }

    const { codigo, novaSenha, confirmarSenha } = this.formularioCodigo.getRawValue();
    if (novaSenha !== confirmarSenha) {
      this.mensagemErro.set('As senhas não conferem.');
      return;
    }

    this.enviando.set(true);
    this.mensagemErro.set(null);

    this.auth
      .redefinirSenha({ email: this.formularioEmail.getRawValue().email, codigo, novaSenha })
      .subscribe({
        next: () => {
          this.enviando.set(false);
          this.router.navigate(['/login'], { queryParams: { motivo: 'senha-redefinida' } });
        },
        error: (erro: HttpErrorResponse) => {
          this.enviando.set(false);
          this.mensagemErro.set(erro.error?.mensagem ?? 'Não foi possível redefinir a senha. Tente novamente.');
        }
      });
  }

  voltarParaEmail(): void {
    this.etapa.set('EMAIL');
    this.mensagemErro.set(null);
    this.mensagemOk.set(null);
    this.formularioCodigo.reset();
  }
}
