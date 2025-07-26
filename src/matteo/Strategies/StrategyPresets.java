package matteo.Strategies;


import matteo.CamminoCache;
import matteo.ConfigurationMode;

/**
 * Classe di utilità che sfrutta direttamente i ConfigurationMode esistenti.
 * Fornisce metodi statici per ottenere facilmente i bundle di strategie
 * 
 */
public class StrategyPresets {
    
    // Factory con cache di default
    private static final StrategyFactory factory = new StrategyFactory();
    
    // Metodi statici che sfruttano i tuoi ConfigurationMode
    public static StrategyBundle debug() {
        return factory.createStrategies(ConfigurationMode.DEBUG);
    }
    
    public static StrategyBundle performance() {
        return factory.createStrategies(ConfigurationMode.PERFORMANCE);
    }
    
    public static StrategyBundle performanceNoCache() {
        return factory.createStrategies(ConfigurationMode.PERFORMANCE_NO_CACHE);
    }
    
    public static StrategyBundle performanceNoCondizione() {
        return factory.createStrategies(ConfigurationMode.PERFORMANCE_NO_CONDIZIONE);
    }
    
    public static StrategyBundle performanceNoCondizioneNoCache() {
        return factory.createStrategies(ConfigurationMode.PERFORMANCE_NO_CONDIZIONE_NO_CACHE);
    }
    
    public static StrategyBundle performanceNoSortedFrontiera() {
        return factory.createStrategies(ConfigurationMode.PERFORMANCE_NO_SORTED_FRONTIERA);
    }
    
    public static StrategyBundle performanceNoSortedFrontieraNoCache() {
        return factory.createStrategies(ConfigurationMode.PERFORMANCE_NO_SORTED_FRONTIERA_NO_CACHE);
    }
    
    public static StrategyBundle defaultMode() {
        return factory.createStrategies(ConfigurationMode.DEFAULT);
    }
    
    // Con cache personalizzata
    public static StrategyBundle debug(CamminoCache cache) {
        return new StrategyFactory(cache).createStrategies(ConfigurationMode.DEBUG);
    }
    
    public static StrategyBundle performance(CamminoCache cache) {
        return new StrategyFactory(cache).createStrategies(ConfigurationMode.PERFORMANCE);
    }
    
    // Metodo generico per mode custom
    public static StrategyBundle fromMode(ConfigurationMode mode) {
        return factory.createStrategies(mode);
    }
    
    public static StrategyBundle fromMode(ConfigurationMode mode, CamminoCache cache) {
        return new StrategyFactory(cache).createStrategies(mode);
    }
}
