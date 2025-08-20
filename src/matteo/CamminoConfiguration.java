package matteo;

/**
 * Classe che rappresenta la configurazione di Cammino.
 * Permette di gestire modalità predefinite e configurazioni personalizzate tramite flag.
 */
public class CamminoConfiguration {

	private final int flags;

	// Constructor default
	public CamminoConfiguration() {
		this.flags = ConfigurationMode.DEFAULT.getFlagsValue();
	}

	// Constructor con modalità predefinita
	public CamminoConfiguration(ConfigurationMode mode) {
		this.flags = mode.getFlagsValue();
	}

	// Constructor  configurazioni custom
	public CamminoConfiguration(int customFlags) {
		this.flags = customFlags;
	}
	
	// Factory method per creare configurazioni personalizzate
	public static CamminoConfiguration custom(ConfigurationFlag... flags) {
		int flagValue = 0;
		for (ConfigurationFlag flag : flags) {
			flagValue |= flag.getValue();
		}
		return new CamminoConfiguration(flagValue);
	}

	   // Factory method per creare da valore int
    public static CamminoConfiguration fromFlags(int flagValue) {
        return new CamminoConfiguration(flagValue);
    }
    /**
     * Aggiunge un flag alla configurazione corrente
     */
    public CamminoConfiguration withFlag(ConfigurationFlag flag) {
        return new CamminoConfiguration(flags | flag.getValue());
    }

    /**
     * Rimuove un flag dalla configurazione corrente
     */
    public CamminoConfiguration withoutFlag(ConfigurationFlag flag) {
        return new CamminoConfiguration(flags & ~flag.getValue());
    }
    /**
     * Aggiunge più flag alla configurazione corrente
     */
    public CamminoConfiguration withFlags(ConfigurationFlag... configFlags) {
        int newFlags = flags;
        for (ConfigurationFlag flag : configFlags) {
            newFlags |= flag.getValue();
        }
        return new CamminoConfiguration(newFlags);
    }
    /**
     * Rimuove più flag dalla configurazione corrente
     */
    public CamminoConfiguration withoutFlags(ConfigurationFlag... configFlags) {
        int newFlags = flags;
        for (ConfigurationFlag flag : configFlags) {
            newFlags &= ~flag.getValue();
        }
        return new CamminoConfiguration(newFlags);
    }
    // Verifica se un flag è attivo
    public boolean hasFlag(ConfigurationFlag flag) {
        return (flags & flag.getValue()) != 0;
    }

    // Verifica se tutti i flag specificati sono attivi
    public boolean hasAllFlags(ConfigurationFlag... flagsToCheck) {
        for (ConfigurationFlag flag : flagsToCheck) {
            if (!hasFlag(flag)) {
                return false;
            }
        }
        return true;
    }
    // Verifica se almeno uno dei flag specificati è attivo
    public boolean hasAnyFlag(ConfigurationFlag... flagsToCheck) {
        for (ConfigurationFlag flag : flagsToCheck) {
            if (hasFlag(flag)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Restituisce la modalità predefinita corrispondente se esiste, altrimenti null
     */
    public ConfigurationMode getPredefinedMode() {
        for (ConfigurationMode mode : ConfigurationMode.values()) {
            if (mode.getFlagsValue() == flags) {
                return mode;
            }
        }
        return null; // È una configurazione custom
    }
    /**
     * Verifica se questa configurazione corrisponde a una modalità predefinita
     */
    public boolean isPredefined() {
        return getPredefinedMode() != null;
    }

    /**
     * Verifica se questa è una configurazione personalizzata
     */
    public boolean isCustom() {
        return !isPredefined();
    }
    // Getters che usano direttamente i flag
    public boolean isDebugEnabled() { 
        return hasFlag(ConfigurationFlag.DEBUG);
    }
    
    public boolean isMonitorEnabled() { 
        return hasFlag(ConfigurationFlag.MONITOR_ENABLED);
    }
    
    public boolean isStopMessageEnabled() { 
        return hasFlag(ConfigurationFlag.STOP_MESSAGE);
    }
    
    public boolean isStateCheckEnabled() { 
        return hasFlag(ConfigurationFlag.STATE_CHECK);
    }
    
    public boolean isSortedFrontieraEnabled() { 
        return hasFlag(ConfigurationFlag.SORTED_FRONTIERA);
    }
    
    public boolean isCondizioneRafforzataEnabled() { 
        return hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA);
    }
    
    public boolean isCacheEnabled() { 
        return hasFlag(ConfigurationFlag.CACHE_ENABLED);
    }
    
    public boolean isSvuotaFrontieraEnabled() { 
        return hasFlag(ConfigurationFlag.SVUOTA_FRONTIERA);
    }
//    // Getters
//    public ConfigurationMode getMode() { 
//        ConfigurationMode predefined = getPredefinedMode();
//        return predefined != null ? predefined : ConfigurationMode.DEFAULT;
//    }

    public int getFlagsValue() {
        return flags;
    }
    
    public CamminoConfiguration toggle(ConfigurationFlag flag) {
    	return new CamminoConfiguration(flags^flag.getValue());
    }
    
    // Restituisce una rappresentazione binaria dei flag (per debug)
    public String toBinaryString() {
        return String.format("%8s", Integer.toBinaryString(flags)).replace(' ', '0');
    }

    // Restituisce la lista dei flag attivi
    public java.util.List<ConfigurationFlag> getActiveFlags() {
        java.util.List<ConfigurationFlag> activeFlags = new java.util.ArrayList<>();
        for (ConfigurationFlag flag : ConfigurationFlag.values()) {
            if (hasFlag(flag)) {
                activeFlags.add(flag);
            }
        }
        return activeFlags;
    }

    /**
     * Restituisce il nome della configurazione (predefinita o CUSTOM)
     */
    public String getConfigurationName() {
        ConfigurationMode predefined = getPredefinedMode();
        return predefined != null ? predefined.name() : "CUSTOM";
    }

    @Override
    public String toString() {
        return  getConfigurationName() + 
               ", flags: " + toBinaryString() + 
               ", active: " + getActiveFlags();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CamminoConfiguration that = (CamminoConfiguration) obj;
        return flags == that.flags;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(flags);
    }
}
