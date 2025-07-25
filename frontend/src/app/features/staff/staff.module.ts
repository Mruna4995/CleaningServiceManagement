import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StaffRoutingModule } from './staff-routing.module';
import { StaffProfileComponent } from './staff-profile/staff-profile.component';

@NgModule({
  declarations: [StaffProfileComponent],
  imports: [
    CommonModule,
    StaffRoutingModule
  ]
})
export class StaffModule { }
