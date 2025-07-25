package matteo;

public class CamminoConfiguration {
    
	private final ConfigurationMode mode;
    
	
    public CamminoConfiguration() {
        this.mode = ConfigurationMode.DEFAULT;
    }
    
    public CamminoConfiguration(ConfigurationMode mode) {
        this.mode = mode;
    }
    
    
    /**
     * Se trova una modalità con i flag specificati la ritorna.
     * @param flag
     * @return
     */
    public CamminoConfiguration withFlag(ConfigurationFlag flag) {
        return new CamminoConfiguration(mode.withFlags(flag));
    }
    
    public CamminoConfiguration withoutFlag(ConfigurationFlag flag) {
        return new CamminoConfiguration(mode.withoutFlags(flag));
    }
    
    // Getters che delegano al mode
    public boolean isDebugEnabled() { return mode.isDebugEnabled(); }
    public boolean isMonitorEnabled() { return mode.isMonitorEnabled(); }
    public boolean isStopMessageEnabled() { return mode.isStopMessageEnabled(); }
    public boolean isStateCheckEnabled() { return mode.isStateCheckEnabled(); }
    public boolean isSortedFrontieraEnabled() { return mode.isSortedFrontieraEnabled(); }
    public boolean isCondizioneRafforzataEnabled() { return mode.isCondizioneRafforzataEnabled(); }
    public boolean isCacheEnabled() { return mode.isCacheEnabled(); }
    
    public ConfigurationMode getMode() { return mode; }
    
    @Override
    public String toString() {
        return "CamminoConfiguration{mode=" + mode + "}";
    }
}
