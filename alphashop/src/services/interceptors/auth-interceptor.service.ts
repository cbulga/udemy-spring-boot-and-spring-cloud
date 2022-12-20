import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';

import { AuthJwtService } from '../authJwt.service';
import { AuthappService } from '../authapp.service';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService  implements HttpInterceptor {

  constructor(private Auth: AuthJwtService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    /*
    let UserId : string = "Nicola";
    let Password : string = "123_Stella";
    */

    var AuthToken =  this.Auth.getAuthToken();

    if (this.Auth.loggedUser())
    {
      req = req.clone({
        setHeaders : {Authorization : AuthToken}
      });
    }

    return next.handle(req);

  }
}
