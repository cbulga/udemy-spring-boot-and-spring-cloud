import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable, map, of } from 'rxjs';

import { ArticoliService } from 'src/services/data/articoli.service';
import { IArticoli } from 'src/app/models/Articoli';
import { JwtRolesService } from 'src/services/jwt-roles.service';
import { Ruoli } from 'src/app/models/Ruoli';

@Component({
  selector: 'app-articoli',
  templateUrl: './articoli.component.html',
  styleUrls: ['./articoli.component.css']
})
export class ArticoliComponent implements OnInit {

  articoli$: IArticoli[]  = []
  errore : string = "";

  pagina : number = 1;
  righe : number = 10;

  filter$: Observable<string | null> = of("");
  filter: string | null = "";

  filterType: number = 0;

  codart: string = "";
  isAdmin: boolean = true;

  constructor(private articoliService: ArticoliService,
    private route: ActivatedRoute,
    private router: Router,
    private roles: JwtRolesService) { }

  ngOnInit(): void {

    this.isAdmin =  (this.roles.getRoles().indexOf(Ruoli.amministratore) > -1) ? true : false;

    this.filter$ = this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('filter')),
    );

    this.filter$.subscribe(param => (this.filter = param));

    if (this.filter) {
      this.getArticoli(this.filter);
    }

  }

  refresh = () => {
    if (this.filter) {
      this.getArticoli(this.filter);
    }
  }

  getArticoli = (filter : string) => {

    this.errore = "";
    this.articoli$ = [];

    if (this.filterType === 0) {
      this.articoliService.getArticoliByCode(filter).subscribe({
        next: this.handleResponse.bind(this),
        error: this.handleError.bind(this)
      });
    }
    else if (this.filterType === 1)
    {
      this.articoliService.getArticoliByDesc(filter).subscribe({
        next: this.handleResponse.bind(this),
        error: this.handleError.bind(this)
      });
    }
    else if (this.filterType === 2)
    {
      this.articoliService.getArticoliByEan(filter).subscribe({
        next: this.handleResponse.bind(this),
        error: this.handleError.bind(this)
      });
    }
  }

  handleResponse(response : any) {

    if (this.filterType === 0 || this.filterType === 2)
    {
      let newArray : IArticoli[] = [...this.articoli$, response];
      this.articoli$ = newArray;
    }
    else
    {
      this.articoli$ = response;
    }

    this.filterType = 0;
  }

  handleError(error: any) {

    if (this.filter && this.filterType === 0) {
      this.filterType = 1;
      this.getArticoli(this.filter);
    }
    else if (this.filter && this.filterType === 1) {
      this.filterType = 2;
      this.getArticoli(this.filter);
    }
    else {
      this.errore = error;
      this.filterType = 0;
    }

  }

  Modifica = (CodArt: string) => {
    console.log(`Modifica articolo ${CodArt}`);

    this.router.navigate(['gestart',CodArt]);
  }

  Elimina = (CodArt: string) => {
    this.codart = CodArt;
    console.log(`Eliminazione articolo ${CodArt}`);

    this.articoliService.delArticoloByCodArt(CodArt).subscribe({
      next: this.handleOkDelete.bind(this),
      error: this.handleErrDelete.bind(this)
      }
    )

  }

  handleOkDelete(response: any) {
    console.log(response);

    this.articoli$ = this.articoli$.filter(item => item.codArt !== this.codart);
    this.codart = "";

  }

  handleErrDelete(error: any) {
    console.log(error);
    this.errore = error;
  }




}
