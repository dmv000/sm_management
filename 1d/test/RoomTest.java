import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

// Assuming ConcreteDevice, Appliance, and Light classes are accessible for testing Room
// ConcreteDevice from DeviceTest.java can be used if it's in the same package or imported.
// For simplicity, let's assume they are usable here. If not, simple stubs might be needed.

public class RoomTest {
    private Room room;
    private Light light1;
    private Appliance appliance1;

    @BeforeEach
    void setUp() {
        room = new Room("R101", "Living Room");
        // Using concrete implementations for devices
        light1 = new Light(400, "Living Room Light", 60.0, false, true); // id, name, maxPower, critical, adjustable
        appliance1 = new Appliance(401, "Living Room Fan", 100.0, new int[]{0, 50, 100}, false); // id, name, maxPower, powerLevels, noisy
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("R101", room.getCode());
        assertEquals("Living Room", room.getDescription());
        assertNotNull(room.getDevicesList());
        assertTrue(room.getDevicesList().isEmpty());
    }

    @Test
    void testSetters() {
        room.setCode("R102");
        assertEquals("R102", room.getCode());
        room.setDescription("Bedroom");
        assertEquals("Bedroom", room.getDescription());
        
        ArrayList<Device> newDeviceList = new ArrayList<>();
        Light tempLight = new Light(405, "Temp", 10.0);
        newDeviceList.add(tempLight);
        room.setDevicesList(newDeviceList);
        assertEquals(1, room.getDevicesList().size());
        assertTrue(room.getDevicesList().contains(tempLight));
    }

    @Test
    void testAddDevice() {
        room.addDevice(light1);
        assertEquals(1, room.getDevicesList().size());
        assertTrue(room.getDevicesList().contains(light1));

        room.addDevice(appliance1);
        assertEquals(2, room.getDevicesList().size());
        assertTrue(room.getDevicesList().contains(appliance1));
    }

    @Test
    void testRemoveDevice() {
        room.addDevice(light1);
        room.addDevice(appliance1);
        assertEquals(2, room.getDevicesList().size());

        room.removeDevice(light1);
        assertEquals(1, room.getDevicesList().size());
        assertFalse(room.getDevicesList().contains(light1));
        assertTrue(room.getDevicesList().contains(appliance1));

        room.removeDevice(appliance1);
        assertEquals(0, room.getDevicesList().size());
        assertFalse(room.getDevicesList().contains(appliance1));
    }
    
    @Test
    void testRemoveDevice_NotPresent() {
        room.addDevice(light1);
        Light otherLight = new Light(402, "Other Light", 50.0);
        room.removeDevice(otherLight); // Try to remove a device not in the list
        assertEquals(1, room.getDevicesList().size(), "List size should not change if device not present.");
    }


    @Test
    void testSearchDeviceById_Exists() {
        room.addDevice(light1);
        room.addDevice(appliance1);

        Device found = room.searchDeviceById(400);
        assertNotNull(found);
        assertEquals(light1, found);

        found = room.searchDeviceById(401);
        assertNotNull(found);
        assertEquals(appliance1, found);
    }

    @Test
    void testSearchDeviceById_NotExists() {
        room.addDevice(light1);
        Device found = room.searchDeviceById(404); // Non-existent ID
        assertNull(found);
    }
    
    @Test
    void testSearchDeviceById_EmptyList() {
        Device found = room.searchDeviceById(400);
        assertNull(found);
    }


    @Test
    void testGetCurrentConsumption_NoDevices() {
        assertEquals(0.0, room.getCurrentConsumption());
    }

    @Test
    void testGetCurrentConsumption_MultipleDevices_AllOff() {
        room.addDevice(light1); // Off by default
        room.addDevice(appliance1); // Off by default
        assertEquals(0.0, room.getCurrentConsumption());
    }

    @Test
    void testGetCurrentConsumption_MultipleDevices_SomeOn() {
        room.addDevice(light1);
        room.addDevice(appliance1);

        light1.turnOn(50); // 50% of 60W = 30W
        appliance1.turnOn(1); // Level 1 is 50% of 100W = 50W

        assertEquals(30.0 + 50.0, room.getCurrentConsumption(), 0.001);
    }
    
    @Test
    void testGetCurrentConsumption_MultipleDevices_AllOn() {
        room.addDevice(light1);
        room.addDevice(appliance1);

        light1.turnOn(); // 100% of 60W = 60W
        appliance1.turnOn(2); // Level 2 (index for powerLevels {0,50,100}) is 100% of 100W = 100W

        assertEquals(60.0 + 100.0, room.getCurrentConsumption(), 0.001);
    }


    @Test
    void testGetNbLights_And_GetNbAppliances() {
        assertEquals(0, room.getNbLights());
        assertEquals(0, room.getNbAppliances());

        room.addDevice(light1);
        assertEquals(1, room.getNbLights());
        assertEquals(0, room.getNbAppliances());

        room.addDevice(appliance1);
        assertEquals(1, room.getNbLights());
        assertEquals(1, room.getNbAppliances());

        Light light2 = new Light(402, "Another Light", 40.0);
        room.addDevice(light2);
        assertEquals(2, room.getNbLights());
        assertEquals(1, room.getNbAppliances());
        
        // Using a ConcreteDevice instance to test that it's not counted as Light or Appliance
        // Assuming ConcreteDevice is a direct subclass of Device and not Light/Appliance
        // If ConcreteDevice is not available or suitable, this part might need adjustment
        // or a more specific non-Light/non-Appliance Device stub.
        // For now, let's assume ConcreteDevice from DeviceTest is not counted.
        // If DeviceTest.ConcreteDevice is not in the same package, this will not compile.
        // We will skip adding a ConcreteDevice if it's not easily accessible.
        // Instead, we can just rely on the counts of Light and Appliance.

        room.removeDevice(appliance1);
        assertEquals(2, room.getNbLights());
        assertEquals(0, room.getNbAppliances());
    }
}
