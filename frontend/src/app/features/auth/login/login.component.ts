import { AuthService } from './../services/auth.service';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
// adjust the path if needed
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  submitted = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  // Easy access to form controls
  get f() {
    return this.loginForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;
    this.errorMessage = '';

    if (this.loginForm.invalid) {
      return;
    }

    const credentials = this.loginForm.value;

    this.authService.login(credentials).subscribe({
      next: (response: any) => {
        // Save token and username
        this.authService.saveAuthData(response.token, response.userName);
        this.router.navigate(['/admin/dashboard']); // redirect after login
      },
      error: (err) => {
        this.errorMessage = 'Invalid email or password';
        console.error('Login error:', err);
      }
    });
  }
}
