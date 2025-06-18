import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ApplianceTest {
    private Appliance appliance;
    private final int[] defaultPowerLevels = {25, 50, 75, 100};

    @BeforeEach
    void setUp() {
        appliance = new Appliance(200, "TestAppliance", 1000.0, defaultPowerLevels, false);
    }

    @Test
    void testConstructor_ValidParameters() {
        assertNotNull(appliance);
        assertEquals(200, appliance.getId());
        assertEquals("TestAppliance", appliance.getName());
        assertEquals(1000.0, appliance.getMaxPowerConsumption());
        assertArrayEquals(defaultPowerLevels, appliance.getPowerLevels());
        assertFalse(appliance.isNoisy());
        assertEquals(0, appliance.getCurrentLevel()); // Default currentLevel
        assertEquals(Device.OFF, appliance.getStatus()); // Default status
    }
    
    @Test
    void testConstructor_Critical() {
        Appliance criticalAppliance = new Appliance(201, "CriticalApp", 100.0, true, defaultPowerLevels, true);
        assertTrue(criticalAppliance.isCritical());
        assertTrue(criticalAppliance.isNoisy());
    }

    @Test
    void testSetPowerLevels() {
        int[] newPowerLevels = {10, 20, 30};
        appliance.setPowerLevels(newPowerLevels);
        assertArrayEquals(newPowerLevels, appliance.getPowerLevels());
    }

    @Test
    void testSetCurrentLevel_Valid() {
        appliance.turnOn(); // Power levels must be set and device on for setCurrentLevel to be meaningful in some contexts
        appliance.setCurrentLevel(1);
        assertEquals(1, appliance.getCurrentLevel());
    }

    @Test
    void testSetCurrentLevel_InvalidTooLow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appliance.setCurrentLevel(-1);
        });
        assertEquals("Invalid power level. Must be between 0 and " + (defaultPowerLevels.length - 1) + ".", exception.getMessage());
    }

    @Test
    void testSetCurrentLevel_InvalidTooHigh() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appliance.setCurrentLevel(defaultPowerLevels.length);
        });
        assertEquals("Invalid power level. Must be between 0 and " + (defaultPowerLevels.length - 1) + ".", exception.getMessage());
    }

    @Test
    void testSetCurrentLevel_PowerLevelsNull() {
        appliance.setPowerLevels(null);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            appliance.setCurrentLevel(0);
        });
        assertEquals("Power levels not defined for this appliance.", exception.getMessage());
    }
    
    @Test
    void testSetCurrentLevel_PowerLevelsEmpty() {
        appliance.setPowerLevels(new int[]{});
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            appliance.setCurrentLevel(0);
        });
        assertEquals("Power levels not defined for this appliance.", exception.getMessage());
    }


    @Test
    void testTurnOn_NoSpecificLevel() {
        appliance.turnOn();
        assertEquals(Device.ON, appliance.getStatus());
        assertEquals(0, appliance.getCurrentLevel(), "Current level should be default (0) when turned on without a specific level.");
    }
    
    @Test
    void testTurnOn_NoSpecificLevel_NullPowerLevels() {
        Appliance app = new Appliance(202, "TestApp", 100.0, null, false);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            app.turnOn();
        });
        assertEquals("Cannot turn on appliance: power levels not defined.", exception.getMessage());
    }

    @Test
    void testTurnOn_SpecificLevel_Valid() {
        appliance.turnOn(2);
        assertEquals(Device.ON, appliance.getStatus());
        assertEquals(2, appliance.getCurrentLevel());
    }

    @Test
    void testTurnOn_SpecificLevel_Invalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appliance.turnOn(defaultPowerLevels.length); // Try to set an invalid level
        });
         assertEquals("Invalid power level. Must be between 0 and " + (defaultPowerLevels.length - 1) + ".", exception.getMessage());
    }
    
    @Test
    void testTurnOn_SpecificLevel_NullPowerLevels() {
        Appliance app = new Appliance(203, "TestApp", 100.0, null, false);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            app.turnOn(0);
        });
        assertEquals("Power levels not defined for this appliance.", exception.getMessage());
    }


    @Test
    void testGetCurrentConsumption_Off() {
        appliance.setStatus(Device.OFF);
        assertEquals(0.0, appliance.getCurrentConsumption());
    }

    @Test
    void testGetCurrentConsumption_On() {
        appliance.turnOn(1); // Level 1 (50% of 1000.0 = 500.0)
        // Calculation: (powerLevels[currentLevel] / 100.0) * getMaxPowerConsumption()
        // (defaultPowerLevels[1] / 100.0) * 1000.0 = (50 / 100.0) * 1000.0 = 0.5 * 1000.0 = 500.0
        assertEquals(500.0, appliance.getCurrentConsumption());

        appliance.turnOn(3); // Level 3 (100% of 1000.0 = 1000.0)
        // (defaultPowerLevels[3] / 100.0) * 1000.0 = (100 / 100.0) * 1000.0 = 1.0 * 1000.0 = 1000.0
        assertEquals(1000.0, appliance.getCurrentConsumption());
    }
    
    @Test
    void testGetConsumptionIfOn() {
        appliance.setCurrentLevel(1); // Set a level first
        // (defaultPowerLevels[1] / 100.0) * 1000.0 = (50 / 100.0) * 1000.0 = 0.5 * 1000.0 = 500.0
        assertEquals(500.0, appliance.getConsumptionIfOn());
    }
    
    @Test
    void testGetConsumptionIfOn_NullPowerLevels() {
        appliance.setPowerLevels(null);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            appliance.getConsumptionIfOn();
        });
        assertEquals("Power levels not defined for this appliance, cannot calculate consumption if on.", exception.getMessage());
    }


    @Test
    void testIsNoisyAndSetNoisy() {
        assertFalse(appliance.isNoisy()); // Initial value
        appliance.setNoisy(true);
        assertTrue(appliance.isNoisy());
        appliance.setNoisy(false);
        assertFalse(appliance.isNoisy());
    }
}
