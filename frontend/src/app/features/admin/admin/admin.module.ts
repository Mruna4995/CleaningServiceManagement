import { AdminLoginComponent } from './../pages/admin-login/admin-login.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // ✅ REQUIRED for ngModel
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { AdminRoutingModule } from './admin-routing.module';

import { AdminDashboardComponent } from '../pages/admin-dashboard/admin-dashboard.component';

@NgModule({
  declarations: [
    AdminLoginComponent,
    AdminDashboardComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    AdminRoutingModule,// ✅ This must be present!
    RouterModule
  ]
})
export class AdminModule { }
