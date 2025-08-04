package matteo;

import java.util.concurrent.TimeUnit;

public class GestioneInterruzioni implements IInterrompibile {
    private boolean interrompiSuRichiesta = false;
    private boolean interrompiSuTempo = false;
    private long tempoInizio;
    private long timeoutMillis;
    
    public void interrupt() {
        this.interrompiSuRichiesta = true;
    }
    
    @Override
    public void setTimeout(long duration, TimeUnit unit) {
    	timeoutMillis = unit.toMillis(duration);
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
        interrompiSuRichiesta = interrompiSuTempo = false;
    }
    
}