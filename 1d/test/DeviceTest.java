import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Abstract class for Device, so we need a concrete implementation for testing
class ConcreteDevice extends Device {
    public ConcreteDevice(int id, String name, double maxPowerConsumption, boolean critical) {
        super(id, name, maxPowerConsumption, critical);
    }

    public ConcreteDevice(int id, String name, double maxPowerConsumption) {
        super(id, name, maxPowerConsumption);
    }
    
    public ConcreteDevice() {
        super(); // Default constructor for basic tests
    }

    @Override
    public void turnOn() {
        setStatus(Device.ON);
    }

    @Override
    public double getCurrentConsumption() {
        if (getStatus() == Device.ON) {
            return getMaxPowerConsumption() * 0.5; // Example consumption
        }
        return 0;
    }

    @Override
    public double getConsumptionIfOn() {
        return getMaxPowerConsumption() * 0.5; // Example consumption
    }
}

public class DeviceTest {
    private ConcreteDevice device;

    @BeforeEach
    void setUp() {
        // Initialize with valid default values for most tests
        device = new ConcreteDevice(100, "TestDevice", 50.0, false);
    }

    @Test
    void testSetId_Valid() {
        device.setId(150);
        assertEquals(150, device.getId());
        device.setId(100);
        assertEquals(100, device.getId());
        device.setId(999);
        assertEquals(999, device.getId());
    }

    @Test
    void testSetId_InvalidTooLow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            device.setId(99);
        });
        assertEquals("Device ID must be between 100 and 999.", exception.getMessage());
    }

    @Test
    void testSetId_InvalidTooHigh() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            device.setId(1000);
        });
        assertEquals("Device ID must be between 100 and 999.", exception.getMessage());
    }
    
    @Test
    void testConstructorWithId_InvalidTooLow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ConcreteDevice(99, "Test", 10.0);
        });
        assertEquals("Device ID must be between 100 and 999.", exception.getMessage());
    }

    @Test
    void testConstructorWithId_InvalidTooHigh() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ConcreteDevice(1000, "Test", 10.0);
        });
        assertEquals("Device ID must be between 100 and 999.", exception.getMessage());
    }


    @Test
    void testSetMaxPowerConsumption_Valid() {
        device.setMaxPowerConsumption(75.0);
        assertEquals(75.0, device.getMaxPowerConsumption());
        device.setMaxPowerConsumption(0.0);
        assertEquals(0.0, device.getMaxPowerConsumption());
    }

    @Test
    void testSetMaxPowerConsumption_InvalidNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            device.setMaxPowerConsumption(-10.0);
        });
        assertEquals("Maximum power consumption cannot be negative.", exception.getMessage());
    }
    
    @Test
    void testConstructorWithMaxPower_InvalidNegative() {
         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ConcreteDevice(101, "Test", -10.0);
        });
        assertEquals("Maximum power consumption cannot be negative.", exception.getMessage());
    }

    @Test
    void testSetStatus_Valid() {
        device.setStatus(Device.ON);
        assertEquals(Device.ON, device.getStatus());
        device.setStatus(Device.OFF);
        assertEquals(Device.OFF, device.getStatus());
        device.setStatus(Device.STANDBY);
        assertEquals(Device.STANDBY, device.getStatus());
    }

    @Test
    void testSetStatus_Invalid() {
        // Current implementation defaults to OFF for invalid status
        device.setStatus(5); // An invalid status value
        assertEquals(Device.OFF, device.getStatus(), "Status should default to OFF for invalid input.");
        device.setStatus(-1); // Another invalid status value
        assertEquals(Device.OFF, device.getStatus(), "Status should default to OFF for invalid input.");
    }

    @Test
    void testTurnOff() {
        device.setStatus(Device.ON); // Ensure it's on first
        device.turnOff();
        assertEquals(Device.OFF, device.getStatus());
    }

    @Test
    void testDefaultConstructorAndSetters() {
        Device defaultDevice = new ConcreteDevice();
        // Test default ID (which will fail if setId is not called with valid value)
        // Default constructor in Device does not set an ID, setId will throw if ID is 0.
        // So, we must set a valid ID first.
        assertDoesNotThrow(() -> defaultDevice.setId(200));
        assertEquals(200, defaultDevice.getId());

        defaultDevice.setName("Default Name");
        assertEquals("Default Name", defaultDevice.getName());

        assertDoesNotThrow(() -> defaultDevice.setMaxPowerConsumption(10.0));
        assertEquals(10.0, defaultDevice.getMaxPowerConsumption());
        
        defaultDevice.setCritical(true);
        assertTrue(defaultDevice.isCritical());

        defaultDevice.setStatus(Device.STANDBY);
        assertEquals(Device.STANDBY, defaultDevice.getStatus());
    }
    
    @Test
    void testGetters() {
        assertEquals(100, device.getId());
        assertEquals("TestDevice", device.getName());
        assertEquals(50.0, device.getMaxPowerConsumption());
        assertFalse(device.isCritical());
        assertEquals(Device.OFF, device.getStatus()); // Default status
    }

    @Test
    void testEquals() {
        ConcreteDevice sameIdDevice = new ConcreteDevice(100, "AnotherDevice", 60.0);
        ConcreteDevice differentIdDevice = new ConcreteDevice(101, "TestDevice", 50.0);
        ConcreteDevice nullDevice = null;

        assertTrue(device.equals(sameIdDevice), "Devices with the same ID should be equal.");
        assertFalse(device.equals(differentIdDevice), "Devices with different IDs should not be equal.");
        assertFalse(device.equals(nullDevice), "Device should not be equal to null."); // Though .equals should handle null
        assertFalse(device.equals(new Object()), "Device should not be equal to an object of a different type.");
    }
}
