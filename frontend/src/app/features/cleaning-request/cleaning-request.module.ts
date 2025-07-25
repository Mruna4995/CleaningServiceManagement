import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SendRequestComponent } from '../user/send-request/send-request.component';

@NgModule({
  declarations: [SendRequestComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule  // ✅ Add this line
  ]
})
export class CleaningRequestModule { }
