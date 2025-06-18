import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collection;

public class ManagementSystemTest {
    private ManagementSystem managementSystem;
    private final String validAdminPass = "Admin123";
    private final String validUserPass = "User1234";

    @BeforeEach
    void setUp() {
        managementSystem = new ManagementSystem(validAdminPass, validUserPass);
    }

    @Test
    void testConstructor_ValidPasswords() {
        assertNotNull(managementSystem);
        // Check default day time
        // assertTrue(managementSystem.isDay()); // Assuming isDay() or similar getter exists or can be inferred
        // Check default power mode
        // assertEquals(ManagementSystem.LOW, managementSystem.getMaxAllowedPower()); // Assuming getter exists
    }

    @Test
    void testConstructor_InvalidAdminPassword() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ManagementSystem("short", validUserPass);
        });
        assertEquals("Invalid admin password format.", exception.getMessage());
    }

    @Test
    void testConstructor_InvalidUserPassword() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ManagementSystem(validAdminPass, "short");
        });
        assertEquals("Invalid user password format.", exception.getMessage());
    }

    @Test
    void testPasswordIsValid_Valid() {
        assertTrue(ManagementSystem.passwordIsValid("ValidPass1"));
    }

    @Test
    void testPasswordIsValid_InvalidTooShort() {
        assertFalse(ManagementSystem.passwordIsValid("Vp1"));
    }

    @Test
    void testPasswordIsValid_InvalidNoUppercase() {
        assertFalse(ManagementSystem.passwordIsValid("validpass1"));
    }

    @Test
    void testPasswordIsValid_InvalidNoLowercase() {
        assertFalse(ManagementSystem.passwordIsValid("VALIDPASS1"));
    }

    @Test
    void testPasswordIsValid_InvalidNoDigit() {
        assertFalse(ManagementSystem.passwordIsValid("ValidPassword"));
    }

    @Test
    void testChangeAdminPassword_Valid() {
        managementSystem.changeAdminPassword("NewAdmin123");
        // Verify by trying to access with new password (indirectly, or if a getter for password hash existed)
        assertEquals(ManagementSystem.ADMIN, managementSystem.checkAccess("NewAdmin123"));
    }
    
    @Test
    void testChangeAdminPassword_Invalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            managementSystem.changeAdminPassword("new");
        });
         // Password should remain unchanged
        assertEquals(ManagementSystem.ADMIN, managementSystem.checkAccess(validAdminPass));
    }


    @Test
    void testChangeUserPassword_Valid() {
        managementSystem.changeUserPassword("NewUser1234");
        assertEquals(ManagementSystem.USER, managementSystem.checkAccess("NewUser1234"));
    }
    
    @Test
    void testChangeUserPassword_Invalid() {
         assertThrows(IllegalArgumentException.class, () -> {
            managementSystem.changeUserPassword("new");
        });
        assertEquals(ManagementSystem.USER, managementSystem.checkAccess(validUserPass));
    }


    @Test
    void testAddRoom_Valid() {
        Room room1 = new Room("R101", "Living Room");
        assertTrue(managementSystem.addRoom(room1));
        assertNotNull(managementSystem.searchRoomByCode("R101"));
    }

    @Test
    void testAddRoom_DuplicateCode() {
        Room room1 = new Room("R101", "Living Room");
        managementSystem.addRoom(room1);
        Room room2 = new Room("R101", "Dining Room");
        assertFalse(managementSystem.addRoom(room2), "Should not allow adding room with duplicate code.");
        assertEquals("Living Room", managementSystem.searchRoomByCode("R101").getDescription(), "Original room should remain.");
    }

    @Test
    void testAddDevice_Valid() {
        Room room1 = new Room("R101", "Living Room");
        managementSystem.addRoom(room1);
        Device device1 = new Light(500, "Test Light", 60.0);
        assertDoesNotThrow(() -> managementSystem.addDevice(device1, room1));
        assertNotNull(managementSystem.searchDeviceById(500));
        assertEquals(device1, room1.searchDeviceById(500));
    }

    @Test
    void testAddDevice_RoomNotFound() {
        Room room1 = new Room("R101", "Living Room"); // Not added to managementSystem yet
        Device device1 = new Light(500, "Test Light", 60.0);
        Exception exception = assertThrows(RoomNotFoundException.class, () -> {
            managementSystem.addDevice(device1, room1);
        });
        assertEquals("Room with code R101 not found or is not the managed instance.", exception.getMessage());
    }

    @Test
    void testAddDevice_DuplicateDeviceId() {
        Room room1 = new Room("R101", "Living Room");
        managementSystem.addRoom(room1);
        Device device1 = new Light(500, "Test Light", 60.0);
        managementSystem.addDevice(device1, room1);

        Room room2 = new Room("R102", "Bedroom");
        managementSystem.addRoom(room2);
        Device device2 = new Appliance(500, "Test Fan", 100.0, new int[]{50,100}, false); // Same ID
        
        Exception exception = assertThrows(DuplicateDeviceIdException.class, () -> {
            managementSystem.addDevice(device2, room2);
        });
        assertEquals("Device with ID 500 already exists.", exception.getMessage());
    }

    @Test
    void testRemoveRoomByCode_Exists() {
        Room room1 = new Room("R101", "Living Room");
        Device device1 = new Light(501, "Test Light", 60.0);
        managementSystem.addRoom(room1);
        managementSystem.addDevice(device1, room1);

        assertTrue(managementSystem.removeRoomByCode("R101"));
        assertNull(managementSystem.searchRoomByCode("R101"));
        assertNull(managementSystem.searchDeviceById(501), "Device in removed room should also be removed from allDevices.");
    }

    @Test
    void testRemoveRoomByCode_NotExists() {
        assertFalse(managementSystem.removeRoomByCode("R404"));
    }
    
    @Test
    void testRemoveRooms_Exists() {
        Room room1 = new Room("R101", "Living Room");
        managementSystem.addRoom(room1);
        assertTrue(managementSystem.removeRooms(room1));
        assertNull(managementSystem.searchRoomByCode("R101"));
    }

    @Test
    void testRemoveDeviceById_Exists() {
        Room room1 = new Room("R101", "Living Room");
        managementSystem.addRoom(room1);
        Device device1 = new Light(502, "Test Light", 60.0);
        managementSystem.addDevice(device1, room1);

        assertTrue(managementSystem.removeDeviceById(502));
        assertNull(managementSystem.searchDeviceById(502));
        assertNull(room1.searchDeviceById(502), "Device should be removed from room's list too.");
    }

    @Test
    void testRemoveDeviceById_NotExists() {
        assertFalse(managementSystem.removeDeviceById(999));
    }
    
    @Test
    void testSearchRoomByCode_Found() {
        Room room1 = new Room("R111", "Office");
        managementSystem.addRoom(room1);
        assertEquals(room1, managementSystem.searchRoomByCode("R111"));
    }

    @Test
    void testSearchRoomByCode_NotFound() {
        assertNull(managementSystem.searchRoomByCode("R404"));
    }

    @Test
    void testSearchDeviceById_Found() {
        Room room1 = new Room("R101", "Living Room");
        managementSystem.addRoom(room1);
        Device device1 = new Light(510, "Desk Lamp", 40.0);
        managementSystem.addDevice(device1, room1);
        assertEquals(device1, managementSystem.searchDeviceById(510));
    }

    @Test
    void testSearchDeviceById_NotFound() {
         assertNull(managementSystem.searchDeviceById(998));
    }

    @Test
    void testGetRooms() {
        Room room1 = new Room("R101", "Living Room");
        Room room2 = new Room("R102", "Kitchen");
        managementSystem.addRoom(room1);
        managementSystem.addRoom(room2);

        Collection<Room> rooms = managementSystem.getRooms();
        assertNotNull(rooms);
        assertEquals(2, rooms.size());
        assertTrue(rooms.contains(room1));
        assertTrue(rooms.contains(room2));
    }
    
    @Test
    void testSetMaxAllowedPower_Valid() {
        managementSystem.setMaxAllowedPower(ManagementSystem.NORMAL);
        // Need a getter for maxAllowedPower or test behaviorally
        // For now, assume it's set. If a getter is added:
        // assertEquals(ManagementSystem.NORMAL, managementSystem.getMaxAllowedPower());
    }

    @Test
    void testSetMaxAllowedPower_Invalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            managementSystem.setMaxAllowedPower(2000); // Not LOW, NORMAL, or HIGH
        });
    }

    // Helper methods
    private Room addTestRoom(String code, String description) {
        Room room = new Room(code, description);
        managementSystem.addRoom(room);
        return room;
    }

    private Light addTestLight(Room room, int id, String name, double maxPower) {
        Light light = new Light(id, name, maxPower);
        managementSystem.addDevice(light, room);
        return light;
    }

    private Appliance addTestAppliance(Room room, int id, String name, double maxPower, int[] levels, boolean noisy) {
        Appliance appliance = new Appliance(id, name, maxPower, levels, noisy);
        managementSystem.addDevice(appliance, room);
        return appliance;
    }


    // Power Management and Device Operation Tests
    @Test
    void testTurnOnDevice_Normal() {
        Room r1 = addTestRoom("R201", "Test Room");
        Light l1 = addTestLight(r1, 601, "TestLight", 50.0);
        
        assertTrue(managementSystem.turnOnDevice("R201", 601), "Should be able to turn on device.");
        assertEquals(Device.ON, l1.getStatus());
    }

    @Test
    void testTurnOnDevice_NoRoom() {
        assertFalse(managementSystem.turnOnDevice("R999", 601), "Turning on device in non-existent room should fail.");
    }
    
    @Test
    void testTurnOnDevice_NoDeviceInRoom() {
        Room r1 = addTestRoom("R202", "Test Room");
        assertFalse(managementSystem.turnOnDevice("R202", 999), "Turning on non-existent device in room should fail.");
    }
    
    @Test
    void testTurnOnDevice_ApplianceLevel() {
        Room r1 = addTestRoom("R203", "Test Room");
        Appliance a1 = addTestAppliance(r1, 602, "TestAppliance", 100.0, new int[]{25,50,100}, false);
        
        assertTrue(managementSystem.turnOnDevice("R203", 602, 1), "Should turn on appliance to specified level.");
        assertEquals(Device.ON, a1.getStatus());
        assertEquals(1, a1.getCurrentLevel());
    }

    @Test
    void testTurnOffDevice_Normal() {
        Room r1 = addTestRoom("R204", "Test Room");
        Light l1 = addTestLight(r1, 603, "TestLight", 50.0);
        managementSystem.turnOnDevice("R204", 603); // Turn it on first
        
        assertTrue(managementSystem.turnOffDevice("R204", 603), "Should be able to turn off device.");
        assertEquals(Device.OFF, l1.getStatus());
    }
    
    @Test
    void testTurnOffDevice_NoRoom() {
         assertFalse(managementSystem.turnOffDevice("R998", 603), "Turning off device in non-existent room should fail.");
    }

    @Test
    void testCheckTurnOnDevice_CanTurnOn() {
        Room r1 = addTestRoom("R205", "Test Room");
        Light l1 = addTestLight(r1, 604, "TestLight", 50.0); // Consumes 50W if on
        managementSystem.setMaxAllowedPower(ManagementSystem.LOW); // LOW is 1000W
        assertEquals(0, managementSystem.checkTurnOnDevice(l1), "Device should be able to turn on.");
    }

    @Test
    void testCheckTurnOnDevice_NoisyAtNight() {
        Room r1 = addTestRoom("R206", "Test Room");
        Appliance noisyApp = addTestAppliance(r1, 605, "Noisy", 100.0, new int[]{100}, true);
        managementSystem.setNightTime(); // Set to night
        managementSystem.setMaxAllowedPower(ManagementSystem.LOW);
        assertEquals(1, managementSystem.checkTurnOnDevice(noisyApp), "Noisy device at night should return 1.");
    }

    @Test
    void testCheckTurnOnDevice_NotEnoughPower() {
        Room r1 = addTestRoom("R207", "Test Room");
        Light l1 = addTestLight(r1, 606, "BigLight", 1500.0);
        managementSystem.setMaxAllowedPower(ManagementSystem.LOW); // LOW is 1000W
        assertEquals(2, managementSystem.checkTurnOnDevice(l1), "Not enough power should return 2.");
    }
    
    @Test
    void testTotalPowerConsumption() {
        Room r1 = addTestRoom("R208", "Room A");
        Light l1 = addTestLight(r1, 607, "LightA", 50.0); // Consumes 50W
        Appliance a1 = addTestAppliance(r1, 608, "AppA", 100.0, new int[]{100}, false); // Consumes 100W at level 0 (100%)
        
        assertEquals(0.0, managementSystem.getTotalPowerConsumption(), "Consumption should be 0 when all off.");
        
        managementSystem.turnOnDevice(r1.getCode(), l1.getId());
        assertEquals(50.0, managementSystem.getTotalPowerConsumption());
        
        managementSystem.turnOnDevice(r1.getCode(), a1.getId(), 0); // Turn on appliance to its 0th power level
        assertEquals(50.0 + 100.0, managementSystem.getTotalPowerConsumption());

        managementSystem.turnOffDevice(r1.getCode(), l1.getId());
        assertEquals(100.0, managementSystem.getTotalPowerConsumption());
    }

    // Waiting List Logic Tests
    @Test
    void testWaitingList_NoisyDeviceAtNight_ThenDay() {
        Room r1 = addTestRoom("R301", "Bedroom");
        Appliance noisyWasher = addTestAppliance(r1, 701, "Washer", 500.0, new int[]{100}, true);
        managementSystem.setMaxAllowedPower(ManagementSystem.NORMAL); // Normal is 4000W, power is not an issue
        
        managementSystem.setNightTime();
        assertEquals(1, managementSystem.checkTurnOnDevice(noisyWasher)); // Noisy at night
        
        // Simulate user choosing to add to waiting list
        managementSystem.addDeviceToWaitingListDay(noisyWasher);
        assertEquals(Device.STANDBY, noisyWasher.getStatus());
        assertTrue(managementSystem.listStandByDayDevices().contains(String.valueOf(noisyWasher.getId())));

        managementSystem.setDayTime(); // This should try to turn on devices from waitingListDay
        assertEquals(Device.ON, noisyWasher.getStatus(), "Device from day waiting list should turn on during day.");
        assertFalse(managementSystem.listStandByDayDevices().contains(String.valueOf(noisyWasher.getId())));
    }

    @Test
    void testWaitingList_PowerConstraint_ThenPowerFreed() {
        Room r1 = addTestRoom("R302", "Kitchen");
        Appliance oven = addTestAppliance(r1, 702, "Oven", 1500.0, new int[]{100}, false);
        Light light = addTestLight(r1, 703, "KitchenLight", 100.0);
        
        managementSystem.setMaxAllowedPower(ManagementSystem.LOW); // 1000W limit
        managementSystem.turnOnDevice(r1.getCode(), light.getId()); // Light uses 100W
        assertEquals(100.0, managementSystem.getTotalPowerConsumption());

        // Try to turn on Oven (1500W), should fail due to power (100 + 1500 > 1000)
        assertEquals(2, managementSystem.checkTurnOnDevice(oven));
        managementSystem.addDeviceToWaitingListPower(oven); // Simulate user adding to power waitlist
        assertEquals(Device.STANDBY, oven.getStatus());
        assertTrue(managementSystem.listStandByPowerDevices().contains(String.valueOf(oven.getId())));
        
        // Turn off light, freeing up 100W. Total consumption becomes 0. Oven should now turn on.
        // tryToTurnOnDevicesPower is called by turnOffDevice
        managementSystem.turnOffDevice(r1.getCode(), light.getId()); 
        assertEquals(Device.ON, oven.getStatus(), "Device from power waiting list should turn on when power is freed.");
        assertEquals(1500.0, managementSystem.getTotalPowerConsumption());
        assertFalse(managementSystem.listStandByPowerDevices().contains(String.valueOf(oven.getId())));
    }
    
    // Day/Night Mode Tests
    @Test
    void testSetDayTime_ProcessesWaitingList() {
        Room r1 = addTestRoom("R303", "Utility");
        Appliance noisyDryer = addTestAppliance(r1, 704, "Dryer", 600.0, new int[]{100}, true);
        managementSystem.setMaxAllowedPower(ManagementSystem.LOW); // 1000W
        managementSystem.setNightTime();
        managementSystem.addDeviceToWaitingListDay(noisyDryer); // Put on day waitlist
        
        managementSystem.setDayTime(); // Action: set to day
        assertEquals(Device.ON, noisyDryer.getStatus());
    }

    @Test
    void testSetNightTime_DayFlagIsSet() {
        managementSystem.setDayTime(); // Ensure it's day
        managementSystem.setNightTime(); // Action: set to night
        // Need a way to check 'day' flag, e.g. via displayInfo or a new getter
        String info = managementSystem.displayInfo();
        assertTrue(info.contains("Time = night"), "System should be in night mode.");
    }
    
    // Other Important Methods
    @Test
    void testShutDownOneRoom() {
        Room r1 = addTestRoom("R401", "Living Room");
        Light l1 = addTestLight(r1, 801, "LR Light 1", 50.0);
        Appliance a1 = addTestAppliance(r1, 802, "LR Fan", 100.0, new int[]{100}, false);
        Room r2 = addTestRoom("R402", "Bedroom");
        Light l2 = addTestLight(r2, 803, "BR Light", 40.0);

        managementSystem.turnOnDevice(r1.getCode(), l1.getId());
        managementSystem.turnOnDevice(r1.getCode(), a1.getId());
        managementSystem.turnOnDevice(r2.getCode(), l2.getId());
        
        managementSystem.shutDownOneRoom(r1);
        
        assertEquals(Device.OFF, l1.getStatus(), "Device in shutdown room should be off.");
        assertEquals(Device.OFF, a1.getStatus(), "Device in shutdown room should be off.");
        assertEquals(Device.ON, l2.getStatus(), "Device in other room should remain on.");
    }

    @Test
    void testShutDownAllDevices() {
        Room r1 = addTestRoom("R403", "Office");
        Light l1 = addTestLight(r1, 804, "Office Light", 50.0);
        Room r2 = addTestRoom("R404", "Garage");
        Appliance a1 = addTestAppliance(r2, 805, "Garage Heater", 1500.0, new int[]{100}, false);

        managementSystem.turnOnDevice(r1.getCode(), l1.getId());
        managementSystem.turnOnDevice(r2.getCode(), a1.getId());
        
        managementSystem.shutDownAllDevices();
        
        assertEquals(Device.OFF, l1.getStatus());
        assertEquals(Device.OFF, a1.getStatus());
    }

    @Test
    void testPasswordChangeImpactOnLogin() {
        managementSystem.changeAdminPassword("NewSecureAdmin1");
        assertEquals(ManagementSystem.NOACCESS, managementSystem.checkAccess(validAdminPass), "Old admin password should no longer grant admin access.");
        assertEquals(ManagementSystem.ADMIN, managementSystem.checkAccess("NewSecureAdmin1"), "New admin password should grant admin access.");

        managementSystem.changeUserPassword("NewSecureUser1");
        assertEquals(ManagementSystem.NOACCESS, managementSystem.checkAccess(validUserPass), "Old user password should no longer grant user access.");
        assertEquals(ManagementSystem.USER, managementSystem.checkAccess("NewSecureUser1"), "New user password should grant user access.");
    }
    
    // Additional tests for displayInfo, displaySummaryAllRooms, listStandByXXXDevices could be added
    // but they are mostly verified through other tests that use their output or side effects.
}
