package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import matteo.ConfigurationFlag;
import matteo.ConfigurationMode;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Arrays;

@DisplayName("ConfigurationMode Tests")
class TestConfigurationMode{

    @Nested
    @DisplayName("Predefined Modes Tests")
    class PredefinedModesTest {

        @Test
        @DisplayName("DEFAULT mode should only have MONITOR_ENABLED")
        void testDefaultMode() {
            ConfigurationMode mode = ConfigurationMode.DEFAULT;
            
            assertTrue(mode.isMonitorEnabled());
            assertFalse(mode.isDebugEnabled());
            assertFalse(mode.isStopMessageEnabled());
            assertFalse(mode.isStateCheckEnabled());
            assertFalse(mode.isSortedFrontieraEnabled());
            assertFalse(mode.isCondizioneRafforzataEnabled());
            assertFalse(mode.isCacheEnabled());
            
            assertEquals(1, mode.getActiveFlags().size());
            assertTrue(mode.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
        }

        @Test
        @DisplayName("DEBUG mode should have debug, monitor, stop message and state check")
        void testDebugMode() {
            ConfigurationMode mode = ConfigurationMode.DEBUG;
            
            assertTrue(mode.isDebugEnabled());
            assertTrue(mode.isMonitorEnabled());
            assertTrue(mode.isStopMessageEnabled());
            assertTrue(mode.isStateCheckEnabled());
            assertFalse(mode.isSortedFrontieraEnabled());
            assertFalse(mode.isCondizioneRafforzataEnabled());
            assertFalse(mode.isCacheEnabled());
            
            assertEquals(4, mode.getActiveFlags().size());
            assertTrue(mode.hasAllFlags(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.MONITOR_ENABLED,
                ConfigurationFlag.STOP_MESSAGE,
                ConfigurationFlag.STATE_CHECK
            ));
        }

        @Test
        @DisplayName("PERFORMANCE mode should have all performance flags")
        void testPerformanceMode() {
            ConfigurationMode mode = ConfigurationMode.PERFORMANCE;
            
            assertFalse(mode.isDebugEnabled());
            assertTrue(mode.isMonitorEnabled());
            assertFalse(mode.isStopMessageEnabled());
            assertFalse(mode.isStateCheckEnabled());
            assertTrue(mode.isSortedFrontieraEnabled());
            assertTrue(mode.isCondizioneRafforzataEnabled());
            assertTrue(mode.isCacheEnabled());
            
            assertEquals(4, mode.getActiveFlags().size());
            assertTrue(mode.hasAllFlags(
                ConfigurationFlag.MONITOR_ENABLED,
                ConfigurationFlag.SORTED_FRONTIERA,
                ConfigurationFlag.CONDIZIONE_RAFFORZATA,
                ConfigurationFlag.CACHE_ENABLED
            ));
        }

        @Test
        @DisplayName("PERFORMANCE_NO_CACHE should be performance without cache")
        void testPerformanceNoCache() {
            ConfigurationMode mode = ConfigurationMode.PERFORMANCE_NO_CACHE;
            
            assertFalse(mode.isDebugEnabled());
            assertTrue(mode.isMonitorEnabled());
            assertFalse(mode.isStopMessageEnabled());
            assertFalse(mode.isStateCheckEnabled());
            assertTrue(mode.isSortedFrontieraEnabled());
            assertTrue(mode.isCondizioneRafforzataEnabled());
            assertFalse(mode.isCacheEnabled());
            
            assertTrue(mode.hasAllFlags(
                ConfigurationFlag.MONITOR_ENABLED,
                ConfigurationFlag.SORTED_FRONTIERA,
                ConfigurationFlag.CONDIZIONE_RAFFORZATA
            ));
            assertFalse(mode.hasFlag(ConfigurationFlag.CACHE_ENABLED));
        }
    }

    @Nested
    @DisplayName("Flag Operations Tests")
    class FlagOperationsTest {

        @Test
        @DisplayName("hasFlag should work correctly for individual flags")
        void testHasFlag() {
            ConfigurationMode debug = ConfigurationMode.DEBUG;
            
            assertTrue(debug.hasFlag(ConfigurationFlag.DEBUG));
            assertTrue(debug.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertTrue(debug.hasFlag(ConfigurationFlag.STOP_MESSAGE));
            assertTrue(debug.hasFlag(ConfigurationFlag.STATE_CHECK));
            assertFalse(debug.hasFlag(ConfigurationFlag.SORTED_FRONTIERA));
            assertFalse(debug.hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA));
            assertFalse(debug.hasFlag(ConfigurationFlag.CACHE_ENABLED));
        }

        @Test
        @DisplayName("hasAllFlags should work correctly for multiple flags")
        void testHasAllFlags() {
            ConfigurationMode performance = ConfigurationMode.PERFORMANCE;
            
            assertTrue(performance.hasAllFlags(
                ConfigurationFlag.CACHE_ENABLED,
                ConfigurationFlag.SORTED_FRONTIERA
            ));
            
            assertFalse(performance.hasAllFlags(
                ConfigurationFlag.CACHE_ENABLED,
                ConfigurationFlag.DEBUG		//questa non la ha
            ));
            
            assertTrue(performance.hasAllFlags()); // Empty array deve fare return true
        }

        @Test
        @DisplayName("hasAnyFlag should work correctly for multiple flags")
        void testHasAnyFlag() {
            ConfigurationMode debug = ConfigurationMode.DEBUG;
            
            assertTrue(debug.hasAnyFlag(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.CACHE_ENABLED
            ));
            
            assertFalse(debug.hasAnyFlag(
                ConfigurationFlag.CACHE_ENABLED,
                ConfigurationFlag.SORTED_FRONTIERA
            ));
            
            assertFalse(debug.hasAnyFlag()); // Empty array deve fare return false
        }

        @Test
        @DisplayName("withFlags should add flags correctly")
        void testWithFlags() {
            ConfigurationMode original = ConfigurationMode.DEFAULT;
            ConfigurationMode modified = original.withFlags(	
            		
                ConfigurationFlag.STOP_MESSAGE,
                ConfigurationFlag.STATE_CHECK,
                ConfigurationFlag.DEBUG
            );
            //problema con aggiunta, se non ne ho una con quelle flag da il default
            
            // Original should be unchanged
            assertFalse(original.hasFlag(ConfigurationFlag.DEBUG));
            assertFalse(original.hasFlag(ConfigurationFlag.STOP_MESSAGE));
            assertTrue(original.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            
            // Modified should have new flags plus original ones
            assertTrue(modified.hasFlag(ConfigurationFlag.DEBUG));
            assertTrue(modified.hasFlag(ConfigurationFlag.STOP_MESSAGE));
            assertTrue(modified.hasFlag(ConfigurationFlag.STATE_CHECK));
            assertTrue(modified.hasFlag(ConfigurationFlag.MONITOR_ENABLED));

        }

        @Test
        @DisplayName("withoutFlags should remove flags correctly")
        void testWithoutFlags() {
            ConfigurationMode original = ConfigurationMode.PERFORMANCE;
            ConfigurationMode modified = original.withoutFlags(
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
            assertEquals(modified.getActiveFlags(), 
            		ConfigurationMode.PERFORMANCE_SORTED_FRONTIERA.getActiveFlags());
        }
    }

    @Nested
    @DisplayName("Utility Methods Tests")
    class UtilityMethodsTest {

        @Test
        @DisplayName("toBinaryString should return correct binary representation")
        void testToBinaryString() {
            assertEquals("0000010", ConfigurationMode.DEFAULT.toBinaryString());
            assertEquals("0001111", ConfigurationMode.DEBUG.toBinaryString());
            assertEquals("1110010", ConfigurationMode.PERFORMANCE.toBinaryString());
        }

        @Test
        @DisplayName("getActiveFlags should return correct list of active flags")
        void testGetActiveFlags() {
            List<ConfigurationFlag> defaultFlags = ConfigurationMode.DEFAULT.getActiveFlags();
            assertEquals(1, defaultFlags.size());
            assertTrue(defaultFlags.contains(ConfigurationFlag.MONITOR_ENABLED));
            
            List<ConfigurationFlag> debugFlags = ConfigurationMode.DEBUG.getActiveFlags();
            assertEquals(4, debugFlags.size());
            assertTrue(debugFlags.containsAll(Arrays.asList(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.MONITOR_ENABLED,
                ConfigurationFlag.STOP_MESSAGE,
                ConfigurationFlag.STATE_CHECK
            )));
            List<ConfigurationFlag> performanceFlags = ConfigurationMode.PERFORMANCE.getActiveFlags();
            assertEquals(4, performanceFlags.size());
            assertTrue(performanceFlags.containsAll(Arrays.asList(
				ConfigurationFlag.MONITOR_ENABLED,
				ConfigurationFlag.SORTED_FRONTIERA,
				ConfigurationFlag.CONDIZIONE_RAFFORZATA,
				ConfigurationFlag.CACHE_ENABLED
			)));
        }

        @Test
        @DisplayName("getNomeMode should contain mode name and useful info")
        void testNomeMode() {
            String defaultStr = ConfigurationMode.DEFAULT.getModeName();
            assertTrue(defaultStr.contains("DEFAULT"));
            assertTrue(defaultStr.contains("0000010"));
            assertTrue(defaultStr.contains("MONITOR_ENABLED"));
            
        	String debugStr = ConfigurationMode.DEBUG.getModeName();
        				assertTrue(debugStr.contains("DEBUG"));
			assertTrue(debugStr.contains("0001111"));
			assertTrue(debugStr.contains("DEBUG"));
			assertTrue(debugStr.contains("MONITOR_ENABLED"));
			assertTrue(debugStr.contains("STOP_MESSAGE"));
			assertTrue(debugStr.contains("STATE_CHECK"));

			String performanceStr = ConfigurationMode.PERFORMANCE.getModeName();
			assertTrue(performanceStr.contains("PERFORMANCE"));
			assertTrue(performanceStr.contains("1110010"));
			assertTrue(performanceStr.contains("MONITOR_ENABLED"));
			assertTrue(performanceStr.contains("SORTED_FRONTIERA"));
			assertTrue(performanceStr.contains("CONDIZIONE_RAFFORZATA"));
			assertTrue(performanceStr.contains("CACHE_ENABLED"));
        }
    }

    @Nested
    @DisplayName("Boolean Getter Methods Tests")
    class BooleanGetterTest {

        @ParameterizedTest
        @EnumSource(ConfigurationMode.class)
        @DisplayName("Boolean getters should be consistent with hasFlag")
        void testBooleanGettersConsistency(ConfigurationMode mode) {
            assertEquals(mode.hasFlag(ConfigurationFlag.DEBUG), mode.isDebugEnabled());
            assertEquals(mode.hasFlag(ConfigurationFlag.MONITOR_ENABLED), mode.isMonitorEnabled());
            assertEquals(mode.hasFlag(ConfigurationFlag.STOP_MESSAGE), mode.isStopMessageEnabled());
            assertEquals(mode.hasFlag(ConfigurationFlag.STATE_CHECK), mode.isStateCheckEnabled());
            assertEquals(mode.hasFlag(ConfigurationFlag.SORTED_FRONTIERA), mode.isSortedFrontieraEnabled());
            assertEquals(mode.hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA), mode.isCondizioneRafforzataEnabled());
            assertEquals(mode.hasFlag(ConfigurationFlag.CACHE_ENABLED), mode.isCacheEnabled());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTest {

        @Test
        @DisplayName("withFlags with duplicate flags should work correctly")
        void testWithDuplicateFlags() {
            ConfigurationMode mode = ConfigurationMode.DEFAULT.withFlags(
                ConfigurationFlag.DEBUG,
                ConfigurationFlag.DEBUG, // Duplicate
                ConfigurationFlag.STATE_CHECK,
                ConfigurationFlag.STOP_MESSAGE	//diventa modalità debug
            );
            
            assertTrue(mode.hasFlag(ConfigurationFlag.DEBUG));
            assertTrue(mode.hasFlag(ConfigurationFlag.STATE_CHECK));
            assertTrue(mode.hasFlag(ConfigurationFlag.STOP_MESSAGE));
            assertTrue(mode.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertEquals(mode.getActiveFlags(), 
            		ConfigurationMode.DEBUG.getActiveFlags());
        }

        @Test
        @DisplayName("withoutFlags on non-existing flags should not cause errors")
        void testWithoutNonExistingFlags() {
            ConfigurationMode mode = ConfigurationMode.DEFAULT.withoutFlags(
                ConfigurationFlag.DEBUG, // Not present in DEFAULT
                ConfigurationFlag.CACHE_ENABLED // Not present in DEFAULT
            );
            
            // Should still have the original flag
            assertTrue(mode.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertFalse(mode.hasFlag(ConfigurationFlag.DEBUG));
            assertFalse(mode.hasFlag(ConfigurationFlag.CACHE_ENABLED));
        }

        @Test
        @DisplayName("Chain operations should work correctly")
        void testChainOperations() {
            ConfigurationMode result = ConfigurationMode.DEFAULT
                .withFlags(ConfigurationFlag.DEBUG, ConfigurationFlag.MONITOR_ENABLED)
                .withFlags(
                		ConfigurationFlag.CACHE_ENABLED,
                		ConfigurationFlag.CONDIZIONE_RAFFORZATA,
                		ConfigurationFlag.SORTED_FRONTIERA)
                .withoutFlags(ConfigurationFlag.DEBUG);
            
            assertFalse(result.hasFlag(ConfigurationFlag.DEBUG));
            assertTrue(result.hasFlag(ConfigurationFlag.MONITOR_ENABLED));
            assertTrue(result.hasFlag(ConfigurationFlag.CACHE_ENABLED));
            assertTrue(result.hasFlag(ConfigurationFlag.CONDIZIONE_RAFFORZATA));
            assertTrue(result.hasFlag(ConfigurationFlag.SORTED_FRONTIERA));
            assertEquals(4, result.getActiveFlags().size());
            assertEquals(result.getActiveFlags(), 
            		ConfigurationMode.PERFORMANCE.getActiveFlags());
        }

        @Test
        @DisplayName("All performance variants should have MONITOR_ENABLED")
        void testPerformanceVariantsHaveMonitor() {
            ConfigurationMode[] performanceModes = {
                ConfigurationMode.PERFORMANCE,
                ConfigurationMode.PERFORMANCE_NO_CACHE,
                ConfigurationMode.PERFORMANCE_NO_CONDIZIONE_RAFFORZATA,
                ConfigurationMode.PERFORMANCE_SORTED_FRONTIERA,
                ConfigurationMode.PERFORMANCE_NO_SORTED_FRONTIERA,
                ConfigurationMode.PERFORMANCE_CONDIZIONE_RAFFORZATA
            };
            
            for (ConfigurationMode mode : performanceModes) {
                assertTrue(mode.isMonitorEnabled(), 
                    "Mode " + mode.name() + " should have MONITOR_ENABLED");
            }
        }
    }

    @Nested
    @DisplayName("ConfigurationFlag Tests")
    class ConfigurationFlagTest {

        @Test
        @DisplayName("ConfigurationFlag values should be powers of 2")
        void testFlagValuesArePowersOfTwo() {
            ConfigurationFlag[] flags = ConfigurationFlag.values();
            
            for (int i = 0; i < flags.length; i++) {
                int expectedValue = 1 << i; // 2^i
                assertEquals(expectedValue, flags[i].getValue(),
                    "Flag " + flags[i] + " should have value " + expectedValue);
            }
        }

        @Test
        @DisplayName("All flags should have unique values")
        void testFlagValuesAreUnique() {
            ConfigurationFlag[] flags = ConfigurationFlag.values();
            
            for (int i = 0; i < flags.length; i++) {
                for (int j = i + 1; j < flags.length; j++) {
                    assertNotEquals(flags[i].getValue(), flags[j].getValue(),
                        "Flags " + flags[i] + " and " + flags[j] + " should have different values");
                }
            }
        }

        @Test
        @DisplayName("Flag combination should not overlap")
        void testFlagCombinationNoOverlap() {
            // Test that combining any two flags results in a unique value
            ConfigurationFlag flag1 = ConfigurationFlag.DEBUG;
            ConfigurationFlag flag2 = ConfigurationFlag.CACHE_ENABLED;
            
            int combined = flag1.getValue() | flag2.getValue();
            
            assertNotEquals(flag1.getValue(), combined);
            assertNotEquals(flag2.getValue(), combined);
            assertEquals(flag1.getValue() + flag2.getValue(), combined);
        }
    }
}