package matteo;

// Enum delle modalità predefinite usando i bitflags
public enum ConfigurationMode {
    DEFAULT(ConfigurationFlag.MONITOR_ENABLED),
    
    DEBUG(ConfigurationFlag.DEBUG, 
          ConfigurationFlag.MONITOR_ENABLED, 
          ConfigurationFlag.STOP_MESSAGE, 
          ConfigurationFlag.STATE_CHECK),
    
    PERFORMANCE(ConfigurationFlag.MONITOR_ENABLED,
               ConfigurationFlag.SORTED_FRONTIERA,
               ConfigurationFlag.CONDIZIONE_RAFFORZATA,
               ConfigurationFlag.CACHE_ENABLED),
    
    PERFORMANCE_NO_CACHE(ConfigurationFlag.MONITOR_ENABLED,
                        ConfigurationFlag.SORTED_FRONTIERA,
                        ConfigurationFlag.CONDIZIONE_RAFFORZATA),
    
    PERFORMANCE_NO_CONDIZIONE(ConfigurationFlag.MONITOR_ENABLED,
                             ConfigurationFlag.SORTED_FRONTIERA,
                             ConfigurationFlag.CACHE_ENABLED),
    
    PERFORMANCE_NO_CONDIZIONE_NO_CACHE(ConfigurationFlag.MONITOR_ENABLED,
                                      ConfigurationFlag.SORTED_FRONTIERA),
    
    PERFORMANCE_NO_SORTED_FRONTIERA(ConfigurationFlag.MONITOR_ENABLED,
                                  ConfigurationFlag.CONDIZIONE_RAFFORZATA,
                                  ConfigurationFlag.CACHE_ENABLED),
    
    PERFORMANCE_NO_SORTED_FRONTIERA_NO_CACHE(ConfigurationFlag.MONITOR_ENABLED,
                                           ConfigurationFlag.CONDIZIONE_RAFFORZATA);
    
    
    private final int flags;
    
    
    // Constructor che accetta flag variabili
    ConfigurationMode(ConfigurationFlag... enabledFlags) {
        int tempFlags = 0;
        for (ConfigurationFlag flag : enabledFlags) {
            tempFlags |= flag.getValue();
        }
        this.flags = tempFlags;
    }
    
    
    // Verifica se un flag specifico è attivo
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
    
    // Restituisce una nuova modalità con flag aggiuntivi
    public ConfigurationMode withFlags(ConfigurationFlag... additionalFlags) {
        int newFlags = this.flags;
        for (ConfigurationFlag flag : additionalFlags) {
            newFlags |= flag.getValue();
        }
        return fromFlags(newFlags);
    }
    
    // Restituisce una nuova modalità senza i flag specificati
    public ConfigurationMode withoutFlags(ConfigurationFlag... flagsToRemove) {
        int newFlags = this.flags;
        for (ConfigurationFlag flag : flagsToRemove) {
            newFlags &= ~flag.getValue();
        }
        return fromFlags(newFlags);
    }
    
    // Crea una modalità custom da un valore di flag
    /**
     * Crea una modalità custom da un valore di flag. <br>
     * Questo metodo cerca se esiste già una modalità con questi flag e la restituisce. <br>
     * Se non esiste, restituisce la modalità DEFAULT. <br>
     * @param flagValue
     * @return
     */
    private static ConfigurationMode fromFlags(int flagValue) {
        // Cerca se esiste già una modalità con questi flag
        for (ConfigurationMode mode : values()) {
            if (mode.flags == flagValue) {
                return mode;
            }
        }
        // Se non esiste, restituiamo la modalità o DEFAULT
        return DEFAULT;
    }
    
    // Getters per compatibilità con il codice esistente
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
    
    // Restituisce una rappresentazione binaria dei flag (per debug)
    public String toBinaryString() {
        return String.format("%7s", Integer.toBinaryString(flags)).replace(' ', '0');
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
    
    @Override
    public String toString() {
        return name() + " (flags: " + toBinaryString() + ", active: " + getActiveFlags() + ")";
    }
    
    
    public CamminoConfiguration toCamminoConfiguration() {
    	return new CamminoConfiguration(this);
    }
    
}