import { Component, OnInit } from '@angular/core';

import { AuthJwtService } from 'src/services/authJwt.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(public AuthService: AuthJwtService) { }

  ngOnInit(): void {
  }

}
