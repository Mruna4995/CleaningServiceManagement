import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

interface LoginResponse {
  token: string;
  email: string;
  name: string;
  role: string;
  joinedDate: string;
  assigned: boolean;
}

@Injectable({ providedIn: 'root' })
export class AdminAuthService {
  private authUrl = 'http://localhost:8082/api/auth/login';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.authUrl, { email, password }).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
        localStorage.setItem('email', response.email);
        localStorage.setItem('name', response.name);
      })
    );
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  logout(): void {
    localStorage.clear();
  }
}
