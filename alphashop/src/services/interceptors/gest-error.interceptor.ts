import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable()
export class GestErrorInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(request).pipe(
        catchError(err => {

        console.log(err);

        var error : string = (err.status > 0) ? err.error.message || err.statusText : 'Errore Generico. Impossibile Proseguire!';

        if ([403].indexOf(err.status) !== -1) {
            this.router.navigate(['forbidden']);
        }
        else if (err.status === 404) {
          error = "Impossibile trovare l'elemento oggetto della ricerca!";
        }

        return throwError(() => error);
    }))
  }
}
