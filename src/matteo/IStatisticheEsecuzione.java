package matteo;

public interface IStatisticheEsecuzione {

	    public void statsGriglia(int h, int w);
	    
	    
	    public void incrementaCelleFrontiera();
	    
	    public void incrementaIterazioniCondizione();
	    
	    public void aggiungiPrestazione(String prestazione);
	    
	    public void interrompiCalcolo();
	    
	    public String generaRiassunto(ICammino risultato);

}
