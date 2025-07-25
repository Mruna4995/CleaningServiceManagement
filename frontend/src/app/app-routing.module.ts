import { DashboardComponent } from './features/admin/dashboard/dashboard.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout/layout.component';
import { ContactComponent } from './features/auth/contact/contact.component';
import { OurServicesComponent } from './features/ourservices/our-services/our-services.component';
import { SendRequestComponent } from './features/user/send-request/send-request.component';


const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
       { path: 'ourservices', component: OurServicesComponent },
      {
        path: 'auth/contact',
        component: ContactComponent
      },
        { path: 'dashboard', component: DashboardComponent },

      {
        path: 'staff',
        loadChildren: () => import('./features/staff/staff.module').then(m => m.StaffModule)
      },
      {
  path: 'user/send-request',
  component: SendRequestComponent
},

      {
        path: 'auth',
        loadChildren: () => import('./features/auth/auth/auth.module').then(m => m.AuthModule)
      },
      {
        path: 'admin',
        loadChildren: () => import('./features/admin/admin/admin.module').then(m => m.AdminModule)
      },
      { path: '', redirectTo: '/dashboard', pathMatch: 'full' }

    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {} // ✅ Make sure this line exists
