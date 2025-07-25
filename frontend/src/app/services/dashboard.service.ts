import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly BASE_URL = 'http://localhost:8082/api/requests';

  constructor(private http: HttpClient) {}

  getAllRequests(): Observable<any[]> {
    return this.http.get<any[]>(this.BASE_URL);
  }

  getStats(): Observable<any> {
    return this.http.get(`${this.BASE_URL}/stats`);
  }
}
