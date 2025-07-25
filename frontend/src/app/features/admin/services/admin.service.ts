// admin.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  constructor(private http: HttpClient) {}

  getPendingRequests() {
    return this.http.get<any[]>('/api/admin/requests');
  }

  getAllStaff() {
    return this.http.get<any[]>('/api/staff/all');
  }

  assignRequest(requestId: number, staffId: number) {
    return this.http.post('/api/admin/assign', { requestId, staffId });
  }
}
