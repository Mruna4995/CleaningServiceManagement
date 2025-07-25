import { AuthService } from './../features/auth/services/auth.service';
import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  const token = this.authService.getToken();

  // Skip auth for login and register requests
  const isPublicEndpoint =
    req.url.includes('/auth/login') || req.url.includes('/user/register');

  if (token && !isPublicEndpoint) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next.handle(authReq);
  }

  return next.handle(req);
}
}
