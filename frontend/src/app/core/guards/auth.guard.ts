import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = async (): Promise<boolean | UrlTree> => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  if (authService.getToken()) {
    try {
      await firstValueFrom(authService.refresh());
      if (authService.isAuthenticated()) {
        return true;
      }
    } catch {
      // token inválido o expirado: sin sesión
    }
  }

  return router.parseUrl('/login');
};
