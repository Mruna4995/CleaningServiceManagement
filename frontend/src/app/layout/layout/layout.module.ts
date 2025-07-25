import { FooterComponent } from './../footer/footer.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent } from './layout.component';
import { NavbarComponent } from '../navbar/navbar.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { RouterModule } from '@angular/router';

import { DashboardComponent } from 'app/features/admin/dashboard/dashboard.component';

@NgModule({
  declarations: [
    LayoutComponent,
    NavbarComponent,
    SidebarComponent,
    DashboardComponent,
    FooterComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ],
  exports: [
    LayoutComponent,
    NavbarComponent,   // ✅ Exported now
    SidebarComponent,
    FooterComponent   // ✅ Exported now
  ]
})
export class LayoutModule {}
