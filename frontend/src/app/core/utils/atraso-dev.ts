import { delay, MonoTypeOperatorFunction, pipe } from 'rxjs';
import { environment } from '../../../environments/environment';

/**
 * Atraso artificial só em desenvolvimento, pra dar tempo de ver spinners/loading.
 * Em produção (environment.production = true) não faz nada.
 */
export function atrasoDev<T>(ms = 1000): MonoTypeOperatorFunction<T> {
  return pipe(delay(environment.production ? 0 : ms));
}
