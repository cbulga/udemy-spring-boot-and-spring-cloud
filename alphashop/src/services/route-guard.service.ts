import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { AuthJwtService } from './authJwt.service';
import { AuthappService } from './authapp.service';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { JwtRolesService } from './jwt-roles.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RouteGuardService implements CanActivate {

  ruoli : string[] = new Array();

  constructor(private Auth: AuthJwtService, private router: Router,
    private roles: JwtRolesService,) { }

  canActivate(route: ActivatedRouteSnapshot, state:  RouterStateSnapshot)  {

    this.ruoli = this.roles.getRoles();

    if (!this.Auth.isLogged()) {
      this.router.navigate(['login'], { queryParams: {nologged: true}});
      return false;
    } else {

      let roles : string[] = new Array();
      roles = route.data['roles'];

      if (roles === null || roles.length === 0)
      {
        return true;
      }
      else if (this.ruoli.some(r => roles.includes(r)))
      {
        return true;
      }
      else
      {
        this.router.navigate(['forbidden']);
        return false;
      }
    }
  }
}
