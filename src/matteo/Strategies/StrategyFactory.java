package matteo.Strategies;


import matteo.CamminoCache;
import matteo.CamminoConfiguration;
import matteo.ConfigurationMode;

/**
 * Factory semplificato che sfrutta direttamente i ConfigurationMode predefiniti.
 * Molto più pulito e diretto - usa le combinazioni che hai già definito!
 */
public class StrategyFactory {
    
    private final CamminoCache cache;
    
    public StrategyFactory() {
        this.cache = new CamminoCache();
    }
    
    public StrategyFactory(CamminoCache cache) {
        this.cache = cache;
    }
    
    /**
     * Crea tutte le strategy da un ConfigurationMode
     */
    public StrategyBundle createStrategies(ConfigurationMode mode) {
        return new StrategyBundle(
            mode.isDebugEnabled() ? new DebugAbilitato() : new DebugDisabilitato(),
            mode.isCacheEnabled() ? new CacheAttiva(cache) : new CacheNull(),
            mode.isCondizioneRafforzataEnabled() ? new CondizioneRafforzata() : new CondizioneNormale(),
            mode.isSortedFrontieraEnabled() ? new FrontieraOrdinata() : new FrontieraNormale()
        );
    }
    
    /**
     * Overload per CamminoConfiguration (che contiene il mode)
     */
    public StrategyBundle createStrategies(CamminoConfiguration configuration) {
        return createStrategies(configuration.getMode());
    }
    
    
    
}