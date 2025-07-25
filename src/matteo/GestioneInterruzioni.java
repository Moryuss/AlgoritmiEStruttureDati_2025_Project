package matteo;

public class GestioneInterruzioni {
    private boolean interrompiSuRichiesta = false;
    private boolean interrompiSuTempo = false;
    private long tempoInizio;
    private long timeoutMillis;
    
    public void interrupt() {
        this.interrompiSuRichiesta = true;
    }
    
    public void setTimeout(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        this.tempoInizio = System.currentTimeMillis();
        this.interrompiSuTempo = true;
    }
    
    public void checkInterruzione() throws InterruptedException {
        if (interrompiSuRichiesta) {
            throw new InterruptedException("Interrotto su richiesta");
        }
        
        if (interrompiSuTempo && (System.currentTimeMillis() - tempoInizio) > timeoutMillis) {
            throw new InterruptedException("Timeout raggiunto");
        }
    }
    
    public void reset() {
        interrompiSuRichiesta = false;
        interrompiSuTempo = false;
    }
}