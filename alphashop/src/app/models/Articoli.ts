export interface IArticoli {
  codArt: string
  descrizione: string
  um: string
  codStat: string
  pzCart: number
  pesoNetto: number
  prezzo: number
  idStatoArt: string
  desStatoArt: string
  dataCreazione: Date
  imageUrl: string
  famAssort: ICat
  iva: IIva
  barcode: IBarcode[]

}

export interface IIva {

  idIva: number,
  descrizione: string,
  aliquota: number

}

export interface ICat {
  id: number,
  descrizione: string
}

export interface IBarcode {
  barcode: string,
  idTipoArt: string
}
