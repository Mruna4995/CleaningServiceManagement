import { StaffLoginResponse } from './../../Stafflogin-response.model';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';



@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:8082/auth'; // Spring Boot base path

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: { email: string; password: string }) {
    return this.http.post<{ token: string, userName: string }>(`${this.baseUrl}/login`, credentials);
  }
  register(userData: any) {
  return this.http.post('http://localhost:8082/api/user/register', userData);
}


  saveAuthData(token: string, userName: string) {
    localStorage.setItem('auth_token', token);
    localStorage.setItem('user_name', userName);
  }


  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_name');
    this.router.navigate(['/auth/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('auth_token');
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  getUserName(): string {
    return localStorage.getItem('user_name') || 'Admin';
  }
  getUserId(): number {
  const token = localStorage.getItem('auth_token');
  if (!token) return 0;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.userId || 0;
  } catch (error) {
    console.error('Error decoding JWT:', error);
    return 0;
  }
}
}
