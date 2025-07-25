import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http'; // ✅ Add this

import { LoginComponent } from '../login/login.component';
import { RegisterComponent } from '../register/register.component';
import { AuthRoutingModule } from './auth-routing.module';

@NgModule({
  declarations: [
    LoginComponent,
    RegisterComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule, // ✅ Important
    AuthRoutingModule
  ]
})
export class AuthModule {}
