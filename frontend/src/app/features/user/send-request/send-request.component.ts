import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RequestService, CleaningRequest } from '../../../services/request.service';

@Component({
  selector: 'app-send-request',
  templateUrl: './send-request.component.html',
  styleUrls: ['./send-request.component.scss']
})
export class SendRequestComponent implements OnInit {
  requestForm!: FormGroup;

  constructor(
  private fb: FormBuilder,
  private requestService: RequestService // ✅ this line fixes your error
) {}
  ngOnInit(): void {
    this.requestForm = this.fb.group({
      name: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      email: ['', [Validators.required, Validators.email]],
      roomNo: ['', Validators.required],
      address: ['', Validators.required],
      locationUrl: [''],
      serviceType: ['', Validators.required]
    });
  }

 submitRequest() {
  if (this.requestForm.valid) {
    const cleaningRequest: CleaningRequest = {
      ...this.requestForm.value,
      userId: 1 // Replace with real user ID if needed
    };

    console.log("Submitting request:", cleaningRequest); // 🔍 debug

    this.requestService.createRequest(cleaningRequest).subscribe({
      next: () => {
        alert('✅ Request sent successfully');
        this.requestForm.reset(); // optional
      },
      error: (err) => {
        console.error('❌ Error submitting request:', err); // 🔍 key line
        alert('❌ Failed to submit request');
      }
    });
  } else {
    alert('⚠️ Please fill all required fields correctly.');
  }
}
}
