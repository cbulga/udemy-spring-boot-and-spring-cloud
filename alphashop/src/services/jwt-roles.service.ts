import { AuthJwtService } from './authJwt.service';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class JwtRolesService {

  constructor(private Auth: AuthJwtService) { }

  getRoles = () :  string[] => {

    let ruoli : string[] = new Array();
    let token : string = '';
    let items : any;

    token = this.Auth.getAuthToken();

    if (token.length > 50) {
      const helper = new JwtHelperService();
      const decodedToken = helper.decodeToken(token);

      items = decodedToken['authorities'];

      if (!Array.isArray(items))
        ruoli.push(items);
      else
        ruoli = items;
    }

    return ruoli;
  }
}
