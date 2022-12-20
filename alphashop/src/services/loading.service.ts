import { BehaviorSubject } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {

  private loading = new BehaviorSubject<boolean>(false);

  public readonly loading$ = this.loading.asObservable();

  constructor() { }

  show = () => this.loading.next(true);

  hide = () => this.loading.next(false);
}
