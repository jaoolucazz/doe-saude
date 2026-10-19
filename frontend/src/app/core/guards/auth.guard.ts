import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Rota raiz ('/'): decide pra onde mandar o visitante.
 * Logado -> lista de necessidades. Deslogado -> login (com "ver sem login" lá).
 */
export const landingGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return router.createUrlTree([auth.autenticado() ? '/necessidades' : '/login']);
};

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.autenticado()) {
    return true;
  }

  return router.createUrlTree(['/login'], { queryParams: { motivo: 'login-necessario' } });
};

export const doadorGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.ehDoador()) {
    return true;
  }

  return router.createUrlTree(['/necessidades'], { queryParams: { motivo: 'apenas-doador' } });
};

export const instituicaoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.ehInstituicao()) {
    return true;
  }

  return router.createUrlTree(['/necessidades'], { queryParams: { motivo: 'apenas-instituicao' } });
};
