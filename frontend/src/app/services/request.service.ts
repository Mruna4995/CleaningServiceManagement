import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';

// Cleaning request model interface (adjust path if model is separated)
export interface CleaningRequest {
  id: number;
  userId?: number;
  name: string;
  email: string;
  phone: string;
  roomNo: string;
  address: string;
  locationUrl?: string;
  serviceType: string;
  status?: string;
  assignedTo?: string;
  createdAt?: string;
  staffEmail?: string;
  selectedStaff?: { name: string; email: string };

}

@Injectable({
  providedIn: 'root'
})
export class RequestService {
  private getAuthHeaders(): HttpHeaders {
  const token = localStorage.getItem('token'); // or sessionStorage if you're using that
  return new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });
}


  private baseUrl = 'http://localhost:8082/api/requests';


  constructor(private http: HttpClient) {}
getPendingRequests(): Observable<any> {
  const token = localStorage.getItem('token');
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.get(`${this.baseUrl}/pending`, { headers });
}

  /**
   * Create a new cleaning request (used by regular user)
   */
  createRequest(request: CleaningRequest): Observable<any> {
  return this.http.post('http://localhost:8082/api/requests', request, {
    responseType: 'text' as 'json' // ✅ This line fixes the error
  });

}

getAllRequestsForAdmin(): Observable<CleaningRequest[]> {
  return this.http.get<CleaningRequest[]>(`http://localhost:8082/api/cleaning/all`);
}



  /**
   * Get all requests assigned to a staff member by email
   */
  getRequestsByStaffEmail(email: string): Observable<CleaningRequest[]> {
    return this.http.get<CleaningRequest[]>(`${this.baseUrl}/staff/${email}`);
  }

  /**
   * Get all pending requests (admin view)
   */


  /**
   * Get all requests made by a specific user (user dashboard)
   */
  getUserRequests(userId: number): Observable<CleaningRequest[]> {
    return this.http.get<CleaningRequest[]>(`${this.baseUrl}/user/${userId}`);
  }

  /**
   * Update status of a request (admin or staff)
   */
  // ✅ Get all cleaning requests
  getAllRequests(): Observable<CleaningRequest[]> {
    return this.http.get<CleaningRequest[]>(`${this.baseUrl}`);
  }



  // ✅ Update status generally (not used in approve/reject buttons)
 updateRequestStatus(id: number, status: string): Observable<any> {
  if (status === 'APPROVED') {
    return this.http.post(`${this.baseUrl}/${id}/approve`, {}, {
      responseType: 'text' as 'json' // ✅ forces HttpClient to treat plain text as valid response
    });
  } else if (status === 'REJECTED') {
    return this.http.post(`${this.baseUrl}/${id}/reject`, {}, {
      responseType: 'text' as 'json'
    });
  } else {
    return this.http.put(`${this.baseUrl}/update-status/${id}`, { status });
  }
}







  /**
   * Optional: Delete a request by ID
   */
  deleteRequest(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }


}


  /**
   * Optional: Get all requests (for super admin maybe)
   */

