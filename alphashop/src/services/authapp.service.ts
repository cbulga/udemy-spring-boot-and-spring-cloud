import { HttpClient, HttpHeaders } from '@angular/common/http';

import { ApiMsg } from 'src/app/models/ApiMsg';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthappService {

  server : string = environment.server;
  port : string = environment.port;

  constructor(private httpClient : HttpClient) { }

  autenticaService(UserId: string, Password: string) {

    let AuthString : string = "Basic " + window.btoa(UserId + ":" + Password);

    let headers = new HttpHeaders(
      {Authorization:  AuthString}
    )

    return this.httpClient.get<ApiMsg>(
      `http://${this.server}:${this.port}/api/articoli/test`, {headers}).pipe(
        map(
          data => {
            sessionStorage.setItem("Utente", UserId);
            sessionStorage.setItem("AuthToken", AuthString);
            return data;
          }
        )
      );

  }

  /*
  autentica = (userid: string, password: string) : boolean => {
    var retVal = (userid === 'Nicola' && password === '123_Stella') ? true : false;

    if (retVal) {
      sessionStorage.setItem("Utente",userid);
    }

    return retVal;
  }
  */

  loggedUser = (): string | null => (sessionStorage.getItem("Utente")) ? sessionStorage.getItem("Utente") : "";

  isLogged = (): boolean => (sessionStorage.getItem("Utente")) ? true : false;

  clearUser = (): void => sessionStorage.removeItem("Utente");

  clearAll = (): void => sessionStorage.clear();
}
