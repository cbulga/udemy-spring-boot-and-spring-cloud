import { Component, OnInit } from '@angular/core';

import { AuthJwtService } from 'src/services/authJwt.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent implements OnInit {

  constructor(private Auth: AuthJwtService) { }

  ngOnInit(): void {
    this.Auth.clearAll();
  }

}
