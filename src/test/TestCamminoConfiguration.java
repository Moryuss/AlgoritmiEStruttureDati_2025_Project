package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import matteo.ConfigurationFlag;
import matteo.ConfigurationMode;
import matteo.CamminoConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Arrays;

@DisplayName("CamminoConfiguration Tests")
class TestCamminoConfiguration {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTest {

        @Test
        @DisplayName("Default constructor should create DEFAULT configuration")
        void testDefaultConstructor() {
            CamminoConfiguration config = new CamminoConfiguration();
            
            assertTrue(config.isMonitorEnabled());
            assertFalse(config.isDebugEnabled());
            assertFalse(config.isStopMessageEnabled());
            assertFalse(config.isStateCheckEnabled());
            assertFalse(config.isSortedFrontieraEnabled());
            assertFalse(config.isCondizioneRafforzataEnabled());
            assertFalse(config.isCacheEnabled());
            
            assertEquals(1, config.getActiveFlags().size());
            assertTrue(config.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertTrue(config.isPredefined());
            assertEquals(ConfigurationMode.DEFAULT, config.getPredefinedMode());
        }

        @Test
        @DisplayName("Constructor with ConfigurationMode should work correctly")
        void testConfigurationModeConstructor() {
            CamminoConfiguration config = new CamminoConfiguration(ConfigurationMode.DEBUG);
            
            assertTrue(config.isDebugEnabled());
            assertTrue(config.isMonitorEnabled());
//          assertTrue(config.isStopMessageEnabled());
//          assertTrue(config.isStateCheckEnabled());
            assertFalse(config.isSortedFrontieraEnabled());
            assertFalse(config.isCondizioneRafforzataEnabled());
            assertFalse(config.isCacheEnabled());
            
            assertEquals(4, config.getActiveFlags().size());
            assertTrue(config.isPredefined());
            assertEquals(ConfigurationMode.DEBUG, config.getPredefinedMode());
        }

        @Test
        @DisplayName("Constructor with int flags should work correctly")
        void testIntFlagsConstructor() {
            int flags = ConfigurationFlag.DEBUG.getValue() | ConfigurationFlag.MONITOR_ENABLED.getValue();
            CamminoConfiguration config = new CamminoConfiguration(flags);
            
            assertTrue(config.isDebugEnabled());
            assertTrue(config.isMonitorEnabled());
            assertFalse(config.isStopMessageEnabled());
            
            assertEquals(4, config.getActiveFlags().size());
            assertFalse(config.isCustom());
            assertNotNull(config.getPredefinedMode());
        }
    }

    @Nested
    @DisplayName("Factory Methods Tests")
    class FactoryMethodsTest {

        @Test
        @DisplayName("custom() should create custom configuration")
        void testCustomFactory() {
            CamminoConfiguration config = CamminoConfiguration.custom(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.CACHE_ENABLED,
                ConfigurationFlag.MONITOR_ENABLED
            );
            
            assertTrue(config.isDebugEnabled());
            assertTrue(config.isCacheEnabled());
            assertTrue(config.isMonitorEnabled());
            assertFalse(config.isStopMessageEnabled());
            
            assertEquals(5, config.getActiveFlags().size());
            assertTrue(config.isCustom());
            assertNull(config.getPredefinedMode());
        }

        @Test
        @DisplayName("fromFlags() should detect predefined configurations")
        void testFromFlagsPredefined() {
            int debugFlags = ConfigurationFlag.DEBUG.getValue() | 
                           ConfigurationFlag.MONITOR_ENABLED.getValue() |
                           ConfigurationFlag.STOP_MESSAGE.getValue() |
                           ConfigurationFlag.STATE_CHECK.getValue();
            
            CamminoConfiguration config = CamminoConfiguration.fromFlags(debugFlags);
            
            assertTrue(config.isPredefined());
            assertEquals(ConfigurationMode.DEBUG, config.getPredefinedMode());
            assertEquals("DEBUG", config.getConfigurationName());
        }

        @Test
        @DisplayName("fromFlags() should create custom for unknown combinations")
        void testFromFlagsCustom() {
            int customFlags = ConfigurationFlag.DEBUG.getValue() | ConfigurationFlag.CACHE_ENABLED.getValue();
            
            CamminoConfiguration config = CamminoConfiguration.fromFlags(customFlags);
            
            assertTrue(config.isCustom());
            assertNull(config.getPredefinedMode());
            assertEquals("CUSTOM", config.getConfigurationName());
        }
    }

    @Nested
    @DisplayName("Predefined Configurations Tests")
    class PredefinedConfigurationsTest {

        @Test
        @DisplayName("PERFORMANCE configuration should have correct flags")
        void testPerformanceConfiguration() {
            CamminoConfiguration config = new CamminoConfiguration(ConfigurationMode.PERFORMANCE);
            
            assertFalse(config.isDebugEnabled());
            assertTrue(config.isMonitorEnabled());
            assertFalse(config.isStopMessageEnabled());
            assertFalse(config.isStateCheckEnabled());
            assertTrue(config.isSortedFrontieraEnabled());
            assertTrue(config.isCondizioneRafforzataEnabled());
            assertTrue(config.isCacheEnabled());
            
            assertEquals(4, config.getActiveFlags().size());
            assertTrue(config.hasAllFlags(
                ConfigurationFlag.MONITOR_ENABLED,
                ConfigurationFlag.SORTED_FRONTIERA,
                ConfigurationFlag.CONDIZIONE_RAFFORZATA,
                ConfigurationFlag.CACHE_ENABLED
            ));
            assertTrue(config.isPredefined());
            assertEquals(ConfigurationMode.PERFORMANCE, config.getPredefinedMode());
        }

        @Test
        @DisplayName("PERFORMANCE_NO_CACHE should be performance without cache")
        void testPerformanceNoCache() {
            CamminoConfiguration config = new CamminoConfiguration(ConfigurationMode.PERFORMANCE_NO_CACHE);
            
            assertFalse(config.isDebugEnabled());
            assertTrue(config.isMonitorEnabled());
            assertFalse(config.isStopMessageEnabled());
            assertFalse(config.isStateCheckEnabled());
            assertTrue(config.isSortedFrontieraEnabled());
            assertTrue(config.isCondizioneRafforzataEnabled());
            assertFalse(config.isCacheEnabled());
            
            assertTrue(config.hasAllFlags(
                ConfigurationFlag.MONITOR_ENABLED,
                ConfigurationFlag.SORTED_FRONTIERA,
                ConfigurationFlag.CONDIZIONE_RAFFORZATA
            ));
            assertFalse(config.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            assertTrue(config.isPredefined());
        }
    }

    @Nested
    @DisplayName("Flag Operations Tests")
    class FlagOperationsTest {

        @Test
        @DisplayName("hasFlag should work correctly for individual flags")
        void testHasFlag() {
            CamminoConfiguration config = new CamminoConfiguration(ConfigurationMode.DEBUG);
            
            assertTrue(config.hasFlag(ConfigurationFlag.DEBUG));
            assertTrue(config.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertFalse(config.hasFlag(ConfigurationFlag.SORTED_FRONTIERA));
            assertFalse(config.hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA));
            assertFalse(config.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            assertFalse(config.hasFlag(ConfigurationFlag.SVUOTA_FRONTIERA));
        }

        @Test
        @DisplayName("hasAllFlags should work correctly for multiple flags")
        void testHasAllFlags() {
            CamminoConfiguration config = new CamminoConfiguration(ConfigurationMode.PERFORMANCE);
            
            assertTrue(config.hasAllFlags(
                ConfigurationFlag.CACHE_ENABLED,
                ConfigurationFlag.SORTED_FRONTIERA
            ));
            
            assertFalse(config.hasAllFlags(
                ConfigurationFlag.CACHE_ENABLED,
                ConfigurationFlag.DEBUG // questa non la ha
            ));
            
            assertTrue(config.hasAllFlags()); // Empty array deve fare return true
        }

        @Test
        @DisplayName("hasAnyFlag should work correctly for multiple flags")
        void testHasAnyFlag() {
            CamminoConfiguration config = new CamminoConfiguration(ConfigurationMode.DEBUG);
            
            assertTrue(config.hasAnyFlag(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.CACHE_ENABLED
            ));
            
            assertFalse(config.hasAnyFlag(
                ConfigurationFlag.CACHE_ENABLED,
                ConfigurationFlag.SORTED_FRONTIERA
            ));
            
            assertFalse(config.hasAnyFlag()); // Empty array deve fare return false
        }

        @Test
        @DisplayName("withFlag should add flags correctly")
        void testWithFlag() {
            CamminoConfiguration original = new CamminoConfiguration(ConfigurationMode.DEFAULT);
            CamminoConfiguration modified = original.withFlags(ConfigurationFlag.SORTED_FRONTIERA, 
            		ConfigurationFlag.SVUOTA_FRONTIERA);
            
            // Original should be unchanged
            assertFalse(original.hasFlag(ConfigurationFlag.SORTED_FRONTIERA));
            assertFalse(original.hasFlag(ConfigurationFlag.SVUOTA_FRONTIERA));
            assertTrue(original.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            
            // Modified should have new flags plus original ones
            assertTrue(modified.hasFlag(ConfigurationFlag.SORTED_FRONTIERA));
            assertTrue(modified.hasFlag(ConfigurationFlag.SVUOTA_FRONTIERA));
            assertTrue(modified.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            
            // Should be custom since DEBUG + MONITOR_ENABLED alone is not a predefined mode
            assertTrue(modified.isCustom());
        }

        @Test
        @DisplayName("withFlags should add multiple flags correctly")
        void testWithFlags() {
            CamminoConfiguration original = new CamminoConfiguration(ConfigurationMode.DEFAULT);
            CamminoConfiguration modified = original.withFlags(
                ConfigurationFlag.STOP_MESSAGE,
                ConfigurationFlag.STATE_CHECK,
                ConfigurationFlag.DEBUG
            );
            
            // Original should be unchanged
            assertFalse(original.hasFlag(ConfigurationFlag.DEBUG));
            assertTrue(original.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            
            // Modified should have new flags plus original ones (becomes DEBUG mode)
            assertTrue(modified.hasFlag(ConfigurationFlag.DEBUG));
            assertTrue(modified.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            
            // Should detect it's the DEBUG predefined mode
            assertTrue(modified.isPredefined());
            assertEquals(ConfigurationMode.DEBUG, modified.getPredefinedMode());
        }

        @Test
        @DisplayName("withoutFlag should remove flags correctly")
        void testWithoutFlag() {
            CamminoConfiguration original = new CamminoConfiguration(ConfigurationMode.PERFORMANCE);
            CamminoConfiguration modified = original.withoutFlag(ConfigurationFlag.CACHE_ENABLED);
            
            // Original should be unchanged
            assertTrue(original.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            
            // Modified should not have removed flag but keep others
            assertFalse(modified.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            assertTrue(modified.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertTrue(modified.hasFlag(ConfigurationFlag.SORTED_FRONTIERA));
            assertTrue(modified.hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA));
        }

        @Test
        @DisplayName("withoutFlags should remove multiple flags correctly")
        void testWithoutFlags() {
            CamminoConfiguration original = new CamminoConfiguration(ConfigurationMode.PERFORMANCE);
            CamminoConfiguration modified = original.withoutFlags(
                ConfigurationFlag.CACHE_ENABLED,
                ConfigurationFlag.CONDIZIONE_RAFFORZATA
            );
            
            // Original should be unchanged
            assertTrue(original.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            assertTrue(original.hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA));
            
            // Modified should not have removed flags but keep others
            assertFalse(modified.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            assertFalse(modified.hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA));
            assertTrue(modified.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertTrue(modified.hasFlag(ConfigurationFlag.SORTED_FRONTIERA));
            
            // Should detect it's PERFORMANCE_SORTED_FRONTIERA
            assertTrue(modified.isPredefined());
            assertEquals(ConfigurationMode.PERFORMANCE_SORTED_FRONTIERA, modified.getPredefinedMode());
        }
    }

    @Nested
    @DisplayName("Utility Methods Tests")
    class UtilityMethodsTest {

        @Test
        @DisplayName("toBinaryString should return correct binary representation")
        void testToBinaryString() {
            assertEquals("00000010", new CamminoConfiguration(ConfigurationMode.DEFAULT).toBinaryString());
            assertEquals("00001111", new CamminoConfiguration(ConfigurationMode.DEBUG).toBinaryString());
            assertEquals("01110010", new CamminoConfiguration(ConfigurationMode.PERFORMANCE).toBinaryString());
            assertEquals("11110010", new CamminoConfiguration(ConfigurationMode.PERFORMANCE_FULL).toBinaryString());
            
        }

        @Test
        @DisplayName("getActiveFlags should return correct list of active flags")
        void testGetActiveFlags() {
            List<ConfigurationFlag> defaultFlags = new CamminoConfiguration(ConfigurationMode.DEFAULT).getActiveFlags();
            assertEquals(1, defaultFlags.size());
            assertTrue(defaultFlags.contains(ConfigurationFlag.MONITOR_ENABLED));
            
            List<ConfigurationFlag> debugFlags = new CamminoConfiguration(ConfigurationMode.DEBUG).getActiveFlags();
            assertEquals(4, debugFlags.size());
            assertTrue(debugFlags.containsAll(Arrays.asList(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.MONITOR_ENABLED,
                ConfigurationFlag.STOP_MESSAGE,
                ConfigurationFlag.STATE_CHECK
            )));
            
            List<ConfigurationFlag> performanceFlags = new CamminoConfiguration(ConfigurationMode.PERFORMANCE).getActiveFlags();
            assertEquals(4, performanceFlags.size());
            assertTrue(performanceFlags.containsAll(Arrays.asList(
                ConfigurationFlag.MONITOR_ENABLED,
                ConfigurationFlag.SORTED_FRONTIERA,
                ConfigurationFlag.CONDIZIONE_RAFFORZATA,
                ConfigurationFlag.CACHE_ENABLED
            )));
        }

        @Test
        @DisplayName("getConfigurationName should return correct names")
        void testGetConfigurationName() {
            assertEquals("DEFAULT", new CamminoConfiguration(ConfigurationMode.DEFAULT).getConfigurationName());
            assertEquals("DEBUG", new CamminoConfiguration(ConfigurationMode.DEBUG).getConfigurationName());
            assertEquals("PERFORMANCE", new CamminoConfiguration(ConfigurationMode.PERFORMANCE).getConfigurationName());
            
            CamminoConfiguration custom = CamminoConfiguration.custom(ConfigurationFlag.DEBUG, ConfigurationFlag.CACHE_ENABLED);
            assertEquals("CUSTOM", custom.getConfigurationName());
        }

        @Test
        @DisplayName("toString should contain configuration name and useful info")
        void testToString() {
            String defaultStr = new CamminoConfiguration(ConfigurationMode.DEFAULT).toString();
            assertTrue(defaultStr.contains("DEFAULT"));
            assertTrue(defaultStr.contains("00000010"));
            assertTrue(defaultStr.contains("MONITOR_ENABLED"));
            
            String debugStr = new CamminoConfiguration(ConfigurationMode.DEBUG).toString();
            assertTrue(debugStr.contains("DEBUG"));
            assertTrue(debugStr.contains("00001111"));
            assertTrue(debugStr.contains("DEBUG"));
            assertTrue(debugStr.contains("MONITOR_ENABLED"));
            assertTrue(debugStr.contains("STOP_MESSAGE"));
            assertTrue(debugStr.contains("STATE_CHECK"));

            String performanceStr = new CamminoConfiguration(ConfigurationMode.PERFORMANCE).toString();
            assertTrue(performanceStr.contains("PERFORMANCE"));
            assertTrue(performanceStr.contains("01110010"));
            assertTrue(performanceStr.contains("MONITOR_ENABLED"));
            assertTrue(performanceStr.contains("SORTED_FRONTIERA"));
            assertTrue(performanceStr.contains("CONDIZIONE_RAFFORZATA"));
            assertTrue(performanceStr.contains("CACHE_ENABLED"));
            
            String customStr = CamminoConfiguration.custom(ConfigurationFlag.DEBUG).toString();
            assertTrue(customStr.contains("CUSTOM"));
            assertTrue(customStr.contains("00001101"));
}

        @Test
        @DisplayName("getFlagsValue should return correct int value")
        void testGetFlagsValue() {
            CamminoConfiguration config = new CamminoConfiguration(ConfigurationMode.DEBUG);
            int expectedValue = ConfigurationFlag.DEBUG.getValue() |
                              ConfigurationFlag.MONITOR_ENABLED.getValue() |
                              ConfigurationFlag.STOP_MESSAGE.getValue() |
                              ConfigurationFlag.STATE_CHECK.getValue();
            
            assertEquals(expectedValue, config.getFlagsValue());
        }
    }

    @Nested
    @DisplayName("Boolean Getter Methods Tests")
    class BooleanGetterTest {

        @ParameterizedTest
        @EnumSource(ConfigurationMode.class)
        @DisplayName("Boolean getters should be consistent with hasFlag")
        void testBooleanGettersConsistency(ConfigurationMode mode) {
            CamminoConfiguration config = new CamminoConfiguration(mode);
            
            assertEquals(config.hasFlag(ConfigurationFlag.DEBUG), config.isDebugEnabled());
            assertEquals(config.hasFlag(ConfigurationFlag.MONITOR_ENABLED), config.isMonitorEnabled());
            assertEquals(config.hasFlag(ConfigurationFlag.STOP_MESSAGE), config.isStopMessageEnabled());
            assertEquals(config.hasFlag(ConfigurationFlag.STATE_CHECK), config.isStateCheckEnabled());
            assertEquals(config.hasFlag(ConfigurationFlag.SORTED_FRONTIERA), config.isSortedFrontieraEnabled());
            assertEquals(config.hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA), config.isCondizioneRafforzataEnabled());
            assertEquals(config.hasFlag(ConfigurationFlag.CACHE_ENABLED), config.isCacheEnabled());
            assertEquals(config.hasFlag(ConfigurationFlag.SVUOTA_FRONTIERA), config.isSvuotaFrontieraEnabled());
        }
    }

    @Nested
    @DisplayName("Custom Configuration Tests")
    class CustomConfigurationTest {

        @Test
        @DisplayName("Custom configuration should be detected correctly")
        void testCustomDetection() {
            CamminoConfiguration custom = CamminoConfiguration.custom(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.CACHE_ENABLED
            );
            
            assertTrue(custom.isCustom());
            assertFalse(custom.isPredefined());
            assertNull(custom.getPredefinedMode());
            assertEquals("CUSTOM", custom.getConfigurationName());
        }

        @Test
        @DisplayName("Custom configuration should support all operations")
        void testCustomOperations() {
            CamminoConfiguration custom = CamminoConfiguration.custom(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.MONITOR_ENABLED
            );
            
            // Test withFlag
            CamminoConfiguration withCache = custom.withFlag(ConfigurationFlag.CACHE_ENABLED);
            assertTrue(withCache.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            assertTrue(withCache.isCustom());
            
            // Test withoutFlag
            CamminoConfiguration withoutDebug = custom.withoutFlag(ConfigurationFlag.DEBUG);
            assertFalse(withoutDebug.hasFlag(ConfigurationFlag.DEBUG));
            assertTrue(withoutDebug.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            
            // Should detect it's now DEFAULT
            assertTrue(withoutDebug.isPredefined());
            assertEquals(ConfigurationMode.DEFAULT, withoutDebug.getPredefinedMode());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTest {

        @Test
        @DisplayName("withFlags with duplicate flags should work correctly")
        void testWithDuplicateFlags() {
            CamminoConfiguration config = new CamminoConfiguration(ConfigurationMode.DEFAULT);
            CamminoConfiguration modified = config.withFlags(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.DEBUG, // Duplicate
                ConfigurationFlag.STATE_CHECK,
                ConfigurationFlag.STOP_MESSAGE
            );
            
            assertTrue(modified.hasFlag(ConfigurationFlag.DEBUG));
//            assertTrue(modified.hasFlag(ConfigurationFlag.STATE_CHECK));
//            assertTrue(modified.hasFlag(ConfigurationFlag.STOP_MESSAGE));
            assertTrue(modified.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            
            // Should detect it's DEBUG mode
            assertTrue(modified.isPredefined());
            assertEquals(ConfigurationMode.DEBUG, modified.getPredefinedMode());
        }

        @Test
        @DisplayName("withoutFlags on non-existing flags should not cause errors")
        void testWithoutNonExistingFlags() {
            CamminoConfiguration config = new CamminoConfiguration(ConfigurationMode.DEFAULT);
            CamminoConfiguration modified = config.withoutFlags(
                ConfigurationFlag.DEBUG, // Not present in DEFAULT
                ConfigurationFlag.CACHE_ENABLED // Not present in DEFAULT
            );
            
            // Should still have the original flag
            assertTrue(modified.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertFalse(modified.hasFlag(ConfigurationFlag.DEBUG));
            assertFalse(modified.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            
            // Should still be DEFAULT
            assertTrue(modified.isPredefined());
            assertEquals(ConfigurationMode.DEFAULT, modified.getPredefinedMode());
        }

        @Test
        @DisplayName("Chain operations should work correctly")
        void testChainOperations() {
            CamminoConfiguration result = new CamminoConfiguration(ConfigurationMode.DEFAULT)
                .withFlags(ConfigurationFlag.DEBUG, ConfigurationFlag.MONITOR_ENABLED)
                .withFlags(
                    ConfigurationFlag.CACHE_ENABLED,
                    ConfigurationFlag.CONDIZIONE_RAFFORZATA,
                    ConfigurationFlag.SORTED_FRONTIERA)
                .withoutFlag(ConfigurationFlag.DEBUG);
            
            assertFalse(result.hasFlag(ConfigurationFlag.DEBUG));
            assertTrue(result.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertTrue(result.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            assertTrue(result.hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA));
            assertTrue(result.hasFlag(ConfigurationFlag.SORTED_FRONTIERA));
            assertEquals(4, result.getActiveFlags().size());
            
            // Should detect it's PERFORMANCE mode
            assertTrue(result.isPredefined());
            assertEquals(ConfigurationMode.PERFORMANCE, result.getPredefinedMode());
        }

        @Test
        @DisplayName("Equality should work correctly")
        void testEquality() {
            CamminoConfiguration config1 = new CamminoConfiguration(ConfigurationMode.DEBUG);
            CamminoConfiguration config2 = new CamminoConfiguration(ConfigurationMode.DEBUG);
            CamminoConfiguration config3 = CamminoConfiguration.fromFlags(
                ConfigurationFlag.DEBUG.getValue() |
                ConfigurationFlag.MONITOR_ENABLED.getValue() |
                ConfigurationFlag.STOP_MESSAGE.getValue() |
                ConfigurationFlag.STATE_CHECK.getValue()
            );
            
            assertEquals(config1, config2);
            assertEquals(config1, config3); // Same flags, even if one is detected as predefined
            assertEquals(config1.hashCode(), config2.hashCode());
            assertEquals(config1.hashCode(), config3.hashCode());
        }

        @Test
        @DisplayName("All performance variants should have MONITOR_ENABLED")
        void testPerformanceVariantsHaveMonitor() {
            ConfigurationMode[] performanceModes = {
                ConfigurationMode.PERFORMANCE,
                ConfigurationMode.PERFORMANCE_CACHE,
                ConfigurationMode.PERFORMANCE_NO_CACHE,
                ConfigurationMode.PERFORMANCE_NO_CONDIZIONE_RAFFORZATA,
                ConfigurationMode.PERFORMANCE_SORTED_FRONTIERA,
                ConfigurationMode.PERFORMANCE_NO_SORTED_FRONTIERA,
                ConfigurationMode.PERFORMANCE_CONDIZIONE_RAFFORZATA,
                ConfigurationMode.PERFORMANCE_SVUOTA_FRONTIERA,
                ConfigurationMode.PERFORMANCE_FULL
            };
            
            for (ConfigurationMode mode : performanceModes) {
                CamminoConfiguration config = new CamminoConfiguration(mode);
                assertTrue(config.isMonitorEnabled(), 
                    "Mode " + mode.name() + " should have MONITOR_ENABLED");
            }
        }
    }
}