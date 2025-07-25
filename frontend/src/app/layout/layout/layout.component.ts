import { SidebarService } from './../services/sidebar.service';
import { Component } from '@angular/core';


@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.scss']
})
export class LayoutComponent {
  constructor(public sidebarService: SidebarService) {}
}
