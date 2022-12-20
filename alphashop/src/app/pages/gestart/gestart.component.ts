import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { IArticoli, ICat, IIva } from 'src/app/models/Articoli';

import { ApiMsg } from 'src/app/models/ApiMsg';
import { ArticoliService } from 'src/services/data/articoli.service';

@Component({
  selector: 'app-gestart',
  templateUrl: './gestart.component.html',
  styleUrls: ['./gestart.component.css']
})
export class GestartComponent implements OnInit {

  title : string = "";
  isModifica : boolean = false;

  CodArt: string = '';
  Ean: string = '';

  apiMsg! : ApiMsg;
  Conferma: string = '';
  Errore: string = '';

  articolo: IArticoli = {
    codArt: '',
    descrizione: '',
    um: 'PZ',
    codStat: '',
    pzCart: 0,
    pesoNetto: 0,
    prezzo: 0,
    idStatoArt: '1',
    desStatoArt: '',
    dataCreazione: new Date(),
    imageUrl: '',
    iva: {idIva: -1, descrizione: '', aliquota: 0 },
    famAssort: {id : -1, descrizione: ''},
    barcode: []
  };

  Iva: IIva[] = [];
  Cat: ICat[] = [];

  constructor(private route: ActivatedRoute, private articoliService: ArticoliService,
    private router: Router) { }

  ngOnInit(): void {

    this.CodArt =  this.route.snapshot.params['codart'];

    if (this.CodArt) {
      console.log("Selezionato articolo " + this.CodArt);

      this.title = "Modifica Articolo";
      this.isModifica = true;

      this.articoliService.getArticoliByCode(this.CodArt).subscribe({
        next: this.handleResponse.bind(this),
        error: this.handleError.bind(this)
      });
    }
    else {
      this.title = "Creazione Articolo";
      this.isModifica = false;
    }

    this.articoliService.getIva().subscribe(
      response => {
        this.Iva = response;
        console.log(response);
      }
    )

    this.articoliService.getCat().subscribe(
      response => {

        this.Cat = response;
        console.log(response);

      }
    )
  }

  handleResponse(response : any) {
    this.articolo = response;

    this.Ean = (this.articolo.barcode) ? this.articolo.barcode[0].barcode : "";

    console.log(this.articolo);
  }

  handleError(error: any) {
    console.log(error);
  }

  salva = () => {
    console.log(this.articolo);

    this.Conferma = "";
    this.Errore = "";

    if (this.isModifica) {
      this.articoliService.updArticolo(this.articolo).subscribe({
        next: (response) => {
          this.apiMsg = response;
          this.Conferma = this.apiMsg.message;
        },
        error: (error) => {
          this.Errore = error;
        }
      });
    }
    else {
      this.articoliService.insArticolo(this.articolo).subscribe({
        next: (response) => {
          this.apiMsg = response;
          this.Conferma = this.apiMsg.message;
        },
        error: (error) => {
          this.Errore = error;
        }
      });
    }
  }

  abort = () => {

    if (this.isModifica)
      this.router.navigate(['articoli'], {queryParams: {filter : this.CodArt}});
    else
      this.router.navigate(['articoli']);

  }


}
