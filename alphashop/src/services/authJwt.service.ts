import { HttpClient, HttpHeaders } from '@angular/common/http';

import { AppCookieService } from './app-cookie.service';
import { Injectable } from '@angular/core';
import { LocalStorageService } from './local-storage.service';
import { SessionStorageService } from './session-storage.service';
import { Token } from 'src/app/models/Token';
import { environment } from 'src/environments/environment';
import { map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthJwtService {

  server : string = environment.server;
  port : string = environment.port;

  constructor(private httpClient : HttpClient, private storageService : AppCookieService ) { }

  autenticaService(username: string, password: string) {

    return this.httpClient.post<Token>(
      `${environment.authServerUri}`, {username, password}).pipe(
        map(
          data => {
            this.storageService.set("Utente", username);
            this.storageService.set("AuthToken", `Bearer ${data.token}`)
            //sessionStorage.setItem("Utente", username);
            //sessionStorage.setItem("AuthToken", `Bearer ${data.token}`);
            return data;
          }
        )
      );

  }

  getAuthToken = () : string => {

    let AuthHeader : string = "";
    var AuthToken =  this.storageService.get("AuthToken");  //sessionStorage.getItem("AuthToken");

    if (AuthToken != null)
      AuthHeader = AuthToken;

    return AuthHeader;
  }

  loggedUser = (): string | null => (this.storageService.get("Utente")) ? this.storageService.get("Utente") : "";

  isLogged = (): boolean => (this.storageService.get("Utente")) ? true : false;

  clearUser = (): void => this.storageService.remove("Utente");

  clearAll = (): void => this.storageService.clear();
}
