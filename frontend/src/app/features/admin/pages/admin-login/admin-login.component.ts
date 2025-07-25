import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-admin-login',
  templateUrl: './admin-login.component.html'
})
export class AdminLoginComponent {
  loginForm: FormGroup;

  constructor(private fb: FormBuilder, private http: HttpClient) {
    this.loginForm = this.fb.group({
      email: [''],
      password: ['']
    });
  }

  login(): void {
  const loginData = {
    email: this.loginForm.value.email,   // or hardcoded for test
    password: this.loginForm.value.password
  };

  console.log('Login request payload:', loginData); // ✅ Confirm what is being sent

  this.http.post('http://localhost:8082/admin/login', loginData, {
    responseType: 'text'  // Since backend returns plain text like "Admin login successful"
  }).subscribe({
    next: (response) => {
      console.log('✅ Login success:', response);
    },
    error: (err) => {
      console.error('❌ Login failed:', err);
    }
  });
}
}
