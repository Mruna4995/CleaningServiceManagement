import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';

import { RequestService, CleaningRequest } from '../../../../services/request.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  requests: CleaningRequest[] = [];


  constructor(private requestService: RequestService, private http: HttpClient) {}

 ngOnInit(): void {
  this.loadRequests();
  this.requests.forEach(req => {
  if (!req.staffEmail) {
    req.staffEmail = ''; // Prevent undefined error
  }
});

    this.requestService.getPendingRequests().subscribe({
    next: (data) => {
      this.requests = data;
    },
    error: (err) => {
      console.error('Failed to load pending requests', err);
    }

  });

}

loadRequests(): void {

  this.requestService.getAllRequests().subscribe({
    next: (data: CleaningRequest[]) => {
      this.requests = data;
      console.log("Loaded requests", data); // ✅ Add this log
    },
    error: (err) => {
      console.error('Failed to load requests', err);
    }
  });
}

approve(id: number): void {
  console.log("Approve button clicked for ID:", id); // ✅ Add this log
  this.requestService.updateRequestStatus(id, 'APPROVED').subscribe({
    next: () => {
      alert('✅ Request approved successfully!');
      this.loadRequests();
    },
    error: (err) => {
      console.error('❌ Failed to approve request:', err);
    }
  });
}


reject(id: number): void {
  this.requestService.updateRequestStatus(id, 'REJECTED').subscribe({
    next: () => {
      alert('❌ Request rejected successfully.');
      this.loadRequests();
    },
    error: (err) => {
      console.error('❌ Failed to reject request:', err);
      alert('❌ Failed to reject request');
    }
  });
}
staffList = [
  { name: 'Mrunali Kamble', email: 'kamblemrunali9552@gmail.com' },
  { name: 'Sonal Patil', email: 'sonal@example.com' },
  { name: 'Mahesh Kulkarni', email: 'mahesh@example.com' }
];

assignStaff(id: number, staffName: string, staffEmail: string) {

  if (!staffName.trim|| !staffEmail.trim()) {
    alert('Please enter both staff name and email.');
    return;
  }

  this.http.put(`http://localhost:8082/api/requests/${id}/assign`, { assignedTo: staffName ,email: staffEmail})
    .subscribe({
      next: () => {
        alert('Staff assigned successfully!');
        this.loadRequests(); // reload to refresh table
      },
      error: err => {
        console.error('Failed to assign staff:', err);
        alert('Error assigning staff.');
      }
    });

}
}
