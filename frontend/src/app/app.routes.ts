import { Routes } from '@angular/router';
import { doadorGuard, instituicaoGuard, authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home').then((m) => m.Home)
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login)
  },
  {
    path: 'registro',
    loadComponent: () => import('./pages/registro/registro').then((m) => m.Registro)
  },
  {
    path: 'necessidades/:id',
    loadComponent: () =>
      import('./pages/necessidade-detalhe/necessidade-detalhe').then((m) => m.NecessidadeDetalhe)
  },
  {
    path: 'minhas-doacoes',
    canActivate: [authGuard, doadorGuard],
    loadComponent: () => import('./pages/minhas-doacoes/minhas-doacoes').then((m) => m.MinhasDoacoes)
  },
  {
    path: 'painel-instituicao',
    canActivate: [authGuard, instituicaoGuard],
    loadComponent: () =>
      import('./pages/painel-instituicao/painel-instituicao').then((m) => m.PainelInstituicao)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
