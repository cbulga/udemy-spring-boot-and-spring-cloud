import { Component, OnInit } from '@angular/core';

import { ArticoliService } from 'src/services/data/articoli.service';
import { IArticoli } from 'src/app/models/Articoli';

@Component({
  selector: 'app-grid-articoli',
  templateUrl: './grid-articoli.component.html',
  styleUrls: ['./grid-articoli.component.css']
})
export class GridArticoliComponent implements OnInit {

  articoli$ : IArticoli[] = [];
  errore : string = "";

  constructor(private articoliService: ArticoliService) { }

  ngOnInit(): void {

    this.articoliService.getArticoliByDesc('Barilla').subscribe({
      next: this.handleResponse.bind(this),
      error: this.handleError.bind(this)
    });

  }

  handleResponse(response: any) {
    this.articoli$ = response;
  }

  handleError(error: Object) {
    this.errore = error.toString();
  }


  handleEdit = (codart : string) => {
    console.log("Cliccato tasto modifica del codice " + codart);


  }

  handleDelete = (codart : string) => {
    console.log("Cliccato tasto elimina del codice " + codart);

    this.articoli$.splice(this.articoli$.findIndex(x => x.codArt === codart), 1);
    console.log(this.articoli$);

  }

}
