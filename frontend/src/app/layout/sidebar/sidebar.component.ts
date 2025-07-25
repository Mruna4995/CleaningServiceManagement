import { Component } from '@angular/core';
import { SidebarService } from '../../layout/services/sidebar.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  constructor(public sidebarService: SidebarService) {}

  closeSidebar(): void {
    this.sidebarService.toggle(); // Optional: call this if you want to close on item click
  }
}
