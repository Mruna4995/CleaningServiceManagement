import { Component, OnInit } from '@angular/core';
import { SidebarService } from '../../layout/services/sidebar.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  userName: string = 'Admin';

  constructor(
    private sidebarService: SidebarService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // 🔁 If you stored the username separately during login or registration
    const storedName = localStorage.getItem('user_name');
    this.userName = storedName || 'Admin';
  }

  toggleSidebar(): void {
    this.sidebarService.toggle();
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_name'); // optional, if you stored it
    this.router.navigate(['/auth/login']);
  }
}
