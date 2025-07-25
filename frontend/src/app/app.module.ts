import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';


import { LayoutModule } from './layout/layout/layout.module';
import { AuthModule } from './features/auth/auth/auth.module';
import { AdminModule } from './features/admin/admin/admin.module';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './services/auth.interceptor';
import { CleaningRequestModule } from './features/cleaning-request/cleaning-request.module';

import { StaffProfileComponent } from './features/staff/staff-profile/staff-profile.component';
import { SendRequestComponent } from './features/user/send-request/send-request.component';

import { AdminLoginComponent } from './features/admin/pages/admin-login/admin-login.component';
import { ContactComponent } from './features/auth/contact/contact.component';
import { OurServicesComponent } from './features/ourservices/our-services/our-services.component';
import { FooterComponent } from './layout/footer/footer.component';


@NgModule({
  declarations: [
    AppComponent,
    ContactComponent,
    OurServicesComponent,




       // ✅ Only declare components created directly in app (like AppComponent)
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LayoutModule,
    AuthModule,
    AdminModule,
    CleaningRequestModule
  ],
  providers: [ {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }],
  bootstrap: [AppComponent]
})
export class AppModule { }
