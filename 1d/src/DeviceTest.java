public class DeviceTest {
    public static void main(String[] args) {
       // 1. Basic System & Password
       // -------------we should include a way to organize many systems in one.
//>>>>>i think its only one system but will ask monday
       //like a list with ID for each system we select from it
       //example system1 has ID 1 it goes to the location 0 i the array and retrieve it?
        ManagementSystem system1 = new ManagementSystem("Admin123", "User1234");
        System.out.println("admin password validating: " + system1.passwordIsValid("Admin123")); // Valid
        System.out.println("user password validating: " + system1.passwordIsValid("User1234")); // Valid
        System.out.println("invalid password validating: " + system1.passwordIsValid("111")); // Invalid

        // 2. Add Rooms
        Room Livingroom = new Room("Lr101", "Living Room");
        Room Kitchen = new Room("Kch102", "Kitchen");
        Room Bedroom = new Room("Br103", "Bedroom");
        Room Bathroom = new Room("Bthr104", "Bathroom");


        //3. Add Devices
        //add Lights
        Light dimmableLight = new Light(01, "dimm1", 50, false, true);
        Light non_dimmableLight = new Light(02, "nondimm2", 100, false, false);
        Light crit_light = new Light(03, "critLight", 100, true, false);

        // APPLIANCES
        int[] pLevels = {0, 25, 50, 75, 100};
        Appliance tv = new Appliance(04, "SmartTV", 200, pLevels, false);
        Appliance washingMachine = new Appliance(05, "washigMachine", 600, new int[]{0, 75, 100}, true);


        // Add to rooms


        //4. Device On/Off Tests
        // Turn On/Off by room code and device id
        // Dimmable light
        //non-Dimmable light


        // Set light to level


        // Try to switch non-adjustable off (should ignore)


        // Appliance on at level

        //5. Waiting Lists and Noisy Devices
        // At night: noisy device should not be allowed to run

        // Add to day waiting list


        // Simulate day time, should try to turn on noisy waiting devices


        // === 6. Over Power Consumption Shut Off ===
        // Washer ON
        // TV ON
        // Dimmable ON


        // Reduce allowed power to test power waiting list
        // Should not be allowed, check

        // Restore


        //7. Searches and Removals


        //8. Summary and Detail Displays


        //9. Group Operations


        //10. Standby Lists (Final Listing)


        //11. Access Check

    }
}