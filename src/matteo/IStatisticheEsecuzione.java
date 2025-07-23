package matteo;

import nicolas.ICella2;

public interface IStatisticheEsecuzione {

	    public void saveDimensioniGriglia(int h, int w);
	    
	    public void saveTipoGriglia(int tipo);
	    
	    public void saveOrigine(ICella2 origine);
	    
	    public void saveDestinazione(ICella2 destinazione);
	    
	    public void incrementaCelleFrontiera();
	    
	    public void incrementaIterazioniCondizione();
	    
	    public void aggiungiPrestazione(String prestazione);
	    
	    public void interrompiCalcolo();
	    
	    public String generaRiassunto(ICammino risultato);

		public void incrementaCacheHit();
		
		public void setCache(boolean cahceStatus);
		
		public void setFrontieraStored(boolean frontieraStored);

}
