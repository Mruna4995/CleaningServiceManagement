import { Component } from '@angular/core';
import { SidebarService } from '../../layout/services/sidebar.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
export class FooterComponent {
   constructor(public sidebarService: SidebarService) {}


}
