import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LightTest {
    private Light adjustableLight;
    private Light nonAdjustableLight;

    @BeforeEach
    void setUp() {
        adjustableLight = new Light(300, "AdjustableTestLight", 100.0, true, true); // ID, Name, MaxPower, Critical, Adjustable
        nonAdjustableLight = new Light(301, "NonAdjustableTestLight", 60.0, false, false); // ID, Name, MaxPower, Critical, Adjustable
    }

    @Test
    void testConstructor_Adjustable() {
        assertNotNull(adjustableLight);
        assertEquals(300, adjustableLight.getId());
        assertEquals("AdjustableTestLight", adjustableLight.getName());
        assertEquals(100.0, adjustableLight.getMaxPowerConsumption());
        assertTrue(adjustableLight.isCritical());
        assertTrue(adjustableLight.isAdjustable());
        assertEquals(100, adjustableLight.getLevel(), "Default level should be 100");
        assertEquals(Device.OFF, adjustableLight.getStatus());
    }

    @Test
    void testConstructor_NonAdjustable() {
        assertNotNull(nonAdjustableLight);
        assertEquals(301, nonAdjustableLight.getId());
        assertEquals("NonAdjustableTestLight", nonAdjustableLight.getName());
        assertEquals(60.0, nonAdjustableLight.getMaxPowerConsumption());
        assertFalse(nonAdjustableLight.isCritical());
        assertFalse(nonAdjustableLight.isAdjustable());
        assertEquals(100, nonAdjustableLight.getLevel(), "Default level should be 100");
        assertEquals(Device.OFF, nonAdjustableLight.getStatus());
    }
    
    @Test
    void testConstructor_Simplified() {
        Light light1 = new Light(302, "SimpleLight1", 75.0); // Uses this(id, name, maxPowerConsumption, false, false);
        assertFalse(light1.isCritical());
        assertFalse(light1.isAdjustable());
        assertEquals(100, light1.getLevel());

        Light light2 = new Light(303, "SimpleLight2", 80.0, true); // Uses this(id, name, maxPowerConsumption, false, adjustable);
        assertFalse(light2.isCritical()); // Critical is hardcoded to false in this constructor
        assertTrue(light2.isAdjustable());
        assertEquals(100, light2.getLevel());
    }


    @Test
    void testSetLevel_Adjustable_Valid() {
        adjustableLight.setLevel(50);
        assertEquals(50, adjustableLight.getLevel());
        adjustableLight.setLevel(0);
        assertEquals(0, adjustableLight.getLevel());
        adjustableLight.setLevel(100);
        assertEquals(100, adjustableLight.getLevel());
    }

    @Test
    void testSetLevel_Adjustable_InvalidTooLow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            adjustableLight.setLevel(-10);
        });
        assertEquals("Adjustable light level must be between 0 and 100.", exception.getMessage());
    }

    @Test
    void testSetLevel_Adjustable_InvalidTooHigh() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            adjustableLight.setLevel(101);
        });
        assertEquals("Adjustable light level must be between 0 and 100.", exception.getMessage());
    }

    @Test
    void testSetLevel_NonAdjustable_Valid() {
        nonAdjustableLight.setLevel(100); // Should always be 100
        assertEquals(100, nonAdjustableLight.getLevel());
    }

    @Test
    void testSetLevel_NonAdjustable_Invalid() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            nonAdjustableLight.setLevel(50);
        });
        assertEquals("Light is not adjustable and level must be 100.", exception.getMessage());
    }

    @Test
    void testTurnOn_NoSpecificLevel_Adjustable() {
        adjustableLight.turnOn();
        assertEquals(Device.ON, adjustableLight.getStatus());
        assertEquals(100, adjustableLight.getLevel(), "Adjustable light should default to 100 when turned on.");
    }

    @Test
    void testTurnOn_NoSpecificLevel_NonAdjustable() {
        nonAdjustableLight.turnOn();
        assertEquals(Device.ON, nonAdjustableLight.getStatus());
        assertEquals(100, nonAdjustableLight.getLevel(), "Non-adjustable light should be 100 when turned on.");
    }

    @Test
    void testTurnOn_SpecificLevel_Adjustable_Valid() {
        adjustableLight.turnOn(75);
        assertEquals(Device.ON, adjustableLight.getStatus());
        assertEquals(75, adjustableLight.getLevel());
    }

    @Test
    void testTurnOn_SpecificLevel_Adjustable_Invalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            adjustableLight.turnOn(150);
        });
        assertEquals("Adjustable light level must be between 0 and 100.", exception.getMessage());
        // Status might be ON or OFF depending on if setLevel is called before or after setStatus in turnOn(level)
        // Current implementation: setStatus(ON) then setLevel(level). So status would be ON.
        assertEquals(Device.ON, adjustableLight.getStatus(), "Status should be ON even if level set fails, as it's set first.");
    }

    @Test
    void testTurnOn_SpecificLevel_NonAdjustable_Invalid() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            nonAdjustableLight.turnOn(50);
        });
        assertEquals("Light is not adjustable and level must be 100.", exception.getMessage());
         assertEquals(Device.ON, nonAdjustableLight.getStatus(), "Status should be ON even if level set fails, as it's set first.");
    }
    
    @Test
    void testTurnOn_SpecificLevel_NonAdjustable_Valid() {
        nonAdjustableLight.turnOn(100); // Setting to 100 should be fine
        assertEquals(Device.ON, nonAdjustableLight.getStatus());
        assertEquals(100, nonAdjustableLight.getLevel());
    }


    @Test
    void testGetCurrentConsumption_Off() {
        adjustableLight.setStatus(Device.OFF);
        assertEquals(0.0, adjustableLight.getCurrentConsumption());
    }

    @Test
    void testGetCurrentConsumption_On_Adjustable() {
        adjustableLight.turnOn(50); // Level 50, MaxPower 100.0
        // Consumption = (level/100.0) * getMaxPowerConsumption() = (50/100.0) * 100.0 = 0.5 * 100.0 = 50.0
        assertEquals(50.0, adjustableLight.getCurrentConsumption());
    }

    @Test
    void testGetCurrentConsumption_On_NonAdjustable() {
        nonAdjustableLight.turnOn(); // Level 100, MaxPower 60.0
        // Consumption = (100/100.0) * 60.0 = 1.0 * 60.0 = 60.0
        assertEquals(60.0, nonAdjustableLight.getCurrentConsumption());
    }
    
    @Test
    void testGetConsumptionIfOn() {
        adjustableLight.setLevel(70); // Set level for "if on" calculation
        // (70/100.0) * 100.0 = 70.0
        assertEquals(70.0, adjustableLight.getConsumptionIfOn());

        nonAdjustableLight.setLevel(100); // Non-adjustable is always 100
        // (100/100.0) * 60.0 = 60.0
        assertEquals(60.0, nonAdjustableLight.getConsumptionIfOn());
    }

    @Test
    void testIsAdjustableAndSetAdjustable() {
        assertTrue(adjustableLight.isAdjustable());
        adjustableLight.setAdjustable(false); // Should this be allowed? Current code allows it.
        assertFalse(adjustableLight.isAdjustable());

        assertFalse(nonAdjustableLight.isAdjustable());
        nonAdjustableLight.setAdjustable(true); // Should this be allowed? Current code allows it.
        assertTrue(nonAdjustableLight.isAdjustable());
    }
}
