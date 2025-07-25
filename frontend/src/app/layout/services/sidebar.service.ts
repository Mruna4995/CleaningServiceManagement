import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';



@Injectable({ providedIn: 'root' })
export class SidebarService {
  private _visible = false;

  toggle() {
    this._visible = !this._visible;
  }

  isVisible(): boolean {
    return this._visible;
  }
}
