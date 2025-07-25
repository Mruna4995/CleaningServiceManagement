import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-staff-profile',
  templateUrl: './staff-profile.component.html',
  styleUrls: ['./staff-profile.component.scss']
})
export class StaffProfileComponent implements OnInit {
  name: string = '';
  email: string = '';
  role: string = '';
  assigned: boolean = false;
  joinedDate: string = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Example: Load from localStorage after login
    const userData = localStorage.getItem('staff_info');
    if (userData) {
      const user = JSON.parse(userData);
      this.name = user.name || 'Staff';
      this.email = user.email || 'staff@example.com';
      this.role = user.role || 'staff';
      this.assigned = user.assigned || false;
      this.joinedDate = user.joined || 'N/A';
    } else {
      console.warn('No staff_info found in localStorage');
    }
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('staff_info');
    this.router.navigate(['/auth/login']);
  }
}
