import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { SelectModule } from 'primeng/select';
import { TextareaModule } from 'primeng/textarea';
import { AuthService } from '../../core/services/auth.service';
import { TIPOS_SANGUINEOS, TipoUsuario } from '../../core/models/usuario.model';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    CardModule,
    InputTextModule,
    SelectModule,
    TextareaModule,
    MessageModule
  ],
  templateUrl: './registro.html'
})
export class Registro {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly tiposConta = [
    { valor: 'DOADOR', rotulo: 'Doador (pessoa física ou empresa)' },
    { valor: 'INSTITUICAO', rotulo: 'Instituição que recebe doações' }
  ];
  protected readonly tiposSanguineos = TIPOS_SANGUINEOS;
  protected readonly enviando = signal(false);
  protected readonly mensagemErro = signal<string | null>(null);

  protected readonly formulario = this.fb.nonNullable.group({
    tipo: ['DOADOR' as TipoUsuario, [Validators.required]],
    nome: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
    telefone: [''],
    tipoSanguineo: [''],
    nomeInstituicao: [''],
    endereco: ['']
  });

  get ehDoador(): boolean {
    return this.formulario.controls.tipo.value === 'DOADOR';
  }

  enviar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.mensagemErro.set(null);

    const valores = this.formulario.getRawValue();

    this.auth
      .registrar({
        tipo: valores.tipo,
        nome: valores.nome,
        email: valores.email,
        senha: valores.senha,
        telefone: valores.telefone || undefined,
        tipoSanguineo: valores.tipo === 'DOADOR' ? (valores.tipoSanguineo as any) || undefined : undefined,
        nomeInstituicao: valores.tipo === 'INSTITUICAO' ? valores.nomeInstituicao || undefined : undefined,
        endereco: valores.tipo === 'INSTITUICAO' ? valores.endereco || undefined : undefined
      })
      .subscribe({
        next: () => {
          this.enviando.set(false);
          this.router.navigate(['/necessidades']);
        },
        error: (erro: HttpErrorResponse) => {
          this.enviando.set(false);
          this.mensagemErro.set(erro.error?.mensagem ?? 'Não foi possível concluir o cadastro.');
        }
      });
  }
}
