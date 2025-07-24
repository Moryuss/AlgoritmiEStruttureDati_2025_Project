package matteo;

public class CamminoConfiguration {
	// Campi privati con valori di default
	private boolean debug = false;
	private boolean monitorEnabled = true;
	private boolean stopMessage = false;
	private boolean stateCheck = false;
	private boolean sortedFrontiera = false;
	private boolean condizioneRafforzata = false;
	private boolean cacheEnabled = false;

	// Constructor di default (usa i valori sopra)
	public CamminoConfiguration(){};
	
	public CamminoConfiguration(boolean debug, boolean monitorEnabled, boolean stopMessage, 
			boolean stateCheck, boolean sortedFrontiera, 
			boolean condizioneRafforzata, boolean cacheEnabled) {
		this.debug = debug;
		this.monitorEnabled = monitorEnabled;
		this.stopMessage = stopMessage;
		this.stateCheck = stateCheck;
		this.sortedFrontiera = sortedFrontiera;
		this.condizioneRafforzata = condizioneRafforzata;
		this.cacheEnabled = cacheEnabled;
	}
	
	// Metodi di utilità per configurazioni predefinite
    public static CamminoConfiguration createDefault() {
        return new CamminoConfiguration();
    }
    /*
     * Crea una configurazione per la modalità debug.
     */
    public static CamminoConfiguration createDebugMode() {
    	CamminoConfiguration config = new CamminoConfiguration();
        config.setDebugEnabled(true);
        config.setStopMessageEnabled(true);
        config.setStateCheckEnabled(true);
        return config;
    }
    /**
	 * Crea una configurazione per avere la massima performance.
	 */
    public static CamminoConfiguration createPerformanceMode() {
    	CamminoConfiguration config = new CamminoConfiguration();
        config.setCacheEnabled(true);
        config.setSortedFrontieraEnabled(true);
        config.setCondizioneRafforzataEnabled(true);
        return config;
    }
	
	// Getters
    public boolean isDebugEnabled() { return debug; }
    public boolean isMonitorEnabled() { return monitorEnabled; }
    public boolean isStopMessageEnabled() { return stopMessage; }
    public boolean isStateCheckEnabled() { return stateCheck; }
    public boolean isSortedFrontieraEnabled() { return sortedFrontiera; }
    public boolean isConditioneRafforziataEnabled() { return condizioneRafforzata; }
    public boolean isCacheEnabled() { return cacheEnabled; }
    
    // Setters 
    public void setDebugEnabled(boolean debug) { this.debug = debug; }
    public void setMonitorEnabled(boolean monitorEnabled) { this.monitorEnabled = monitorEnabled; }
    public void setStopMessageEnabled(boolean stopMessage) { this.stopMessage = stopMessage; }
    public void setStateCheckEnabled(boolean stateCheck) { this.stateCheck = stateCheck; }
    public void setSortedFrontieraEnabled(boolean sortedFrontier) { this.sortedFrontiera = sortedFrontier; }
    public void setCondizioneRafforzataEnabled(boolean enhancedCondition) { this.condizioneRafforzata = enhancedCondition; }
    public void setCacheEnabled(boolean cacheEnabled) { this.cacheEnabled = cacheEnabled; }
    
    
    @Override
    public String toString() {
        return "PathfindingConfiguration{" +
               "debug=" + debug +
               ", monitorEnabled=" + monitorEnabled +
               ", stopMessage=" + stopMessage +
               ", stateCheck=" + stateCheck +
               ", sortedFrontier=" + sortedFrontiera +
               ", enhancedCondition=" + condizioneRafforzata +
               ", cacheEnabled=" + cacheEnabled +
               '}';
    }
}
