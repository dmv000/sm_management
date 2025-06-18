import java.util.ArrayList;
import java.util.Scanner;

public class DashboardTester {
    public final static Scanner scan = new Scanner(System.in);
    private static ManagementSystem managementSystem;
    private static int currentAccessLevel = 0; // 0 = not logged in; 1 = user login; 2 = admin login

    private static void handleTurnOnDevice(String roomCodeInput, int deviceIdInput, Device targetDevice) {
        int levelOrChoice = 0;
        if(targetDevice instanceof Light){
            Light targetLight = (Light) targetDevice;
            if(!targetLight.isAdjustable()){
                levelOrChoice = 100; // Default for non-adjustable
            } else {
                levelOrChoice = readIntInput("Enter the brightness level (0-100): ");
            }
        } else if (targetDevice instanceof Appliance) {
            Appliance targetAppliance = (Appliance) targetDevice;
            System.out.println("Enter the power level index from the below:");
            int[] powerLevelsArray = targetAppliance.getPowerLevels();
            if (powerLevelsArray == null || powerLevelsArray.length == 0) {
                System.out.println("No power levels defined for this appliance. Cannot turn on.");
                return;
            }
            for(int i = 0; i < powerLevelsArray.length; i++){
                System.out.println(i + ". " + powerLevelsArray[i]);
            }
            levelOrChoice = readIntInput("Select power level index: ");
            if(levelOrChoice >= powerLevelsArray.length) levelOrChoice = powerLevelsArray.length - 1; // Corrected to prevent out of bounds
            if(levelOrChoice < 0) levelOrChoice = 0;
        }

        // Check device turn-on conditions
        switch(managementSystem.checkTurnOnDevice(targetDevice)){
            case 0: // OK
                managementSystem.turnOnDevice(roomCodeInput, deviceIdInput, levelOrChoice);
                System.out.println("Device turned on successfully.");
                break;
            case 1: // Noisy and night
                System.out.println("The device is noisy and it's currently night time.");
                System.out.println("Options:\n1. Turn on anyway\n2. Put on standby (day waiting list)\n3. Cancel operation");
                int noisyNightChoice = readIntInput("Enter your choice: ");
                if (noisyNightChoice == 1) {
                    managementSystem.turnOnDevice(roomCodeInput, deviceIdInput, levelOrChoice);
                    System.out.println("Device turned on successfully.");
                } else if (noisyNightChoice == 2) {
                    managementSystem.addDeviceToWaitingListDay(targetDevice);
                    System.out.println("Device added to day waiting list.");
                } else {
                    System.out.println("Operation cancelled by user.");
                }
                break;
            case 2: // Not enough power
                System.out.println("Not enough power available to turn on this device.");
                System.out.println("Options:\n1. Add to power waiting list\n2. Cancel operation");
                int powerIssueChoice = readIntInput("Enter your choice: ");
                if (powerIssueChoice == 1) {
                    managementSystem.addDeviceToWaitingListPower(targetDevice);
                    System.out.println("Device added to power waiting list.");
                } else {
                    System.out.println("Operation cancelled by user.");
                }
                break;
            default:
                System.out.println("Unknown condition preventing device turn on. Please check system status.");
                break;
        }
    }

    private static void handleTurnOffDevice(Device targetDevice) {
        if(targetDevice.isCritical()){
            System.out.print("Device is critical. Please enter admin password to proceed: ");
            String adminPasswordAttempt = scan.nextLine();
            if(managementSystem.checkAccess(adminPasswordAttempt) == ManagementSystem.ADMIN){
                managementSystem.turnOffDevice(targetDevice);
                System.out.println("Critical device turned off successfully.");
            } else {
                System.out.println("Incorrect admin password. Unable to turn off critical device.");
            }
        } else {
            managementSystem.turnOffDevice(targetDevice);
            System.out.println("Device turned off successfully.");
        }
    }

    public static void main(String[] args) {
        // Prompt for and validate initial admin and user passwords
        String adminPasswordInput;
        do {
            System.out.print("Set the initial Admin password: ");
            adminPasswordInput = scan.nextLine();
            if (!ManagementSystem.passwordIsValid(adminPasswordInput)) {
                System.out.println("Admin password must be at least 8 characters long and include an uppercase letter, a lowercase letter, and a digit.");
            }
        } while (!ManagementSystem.passwordIsValid(adminPasswordInput));

        String userPasswordInput;
        do {
            System.out.print("Set the initial User password: ");
            userPasswordInput = scan.nextLine();
            boolean isValidFormat = ManagementSystem.passwordIsValid(userPasswordInput);
            boolean isDifferentFromAdmin = !userPasswordInput.equals(adminPasswordInput);

            if (!isValidFormat) {
                System.out.println("User password must be at least 8 characters long and include an uppercase letter, a lowercase letter, and a digit.");
            }
            if (isValidFormat && !isDifferentFromAdmin) {
                System.out.println("User password must be different from the Admin password.");
            }
        } while (!ManagementSystem.passwordIsValid(userPasswordInput) || userPasswordInput.equals(adminPasswordInput));
        try {
            managementSystem = new ManagementSystem(adminPasswordInput, userPasswordInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Error initializing management system: " + e.getMessage());
            return; // Exit if initialization fails due to invalid passwords (though loops should prevent this)
        }

        System.out.println("Welcome to your House Management System");
        while (true) { // Main application loop
            if (currentAccessLevel == 0) {
                loginMenu();
            } else if (currentAccessLevel == 1) {
                userMenu();
            } else {
                adminMenu();
            }
        }
    }

    private static int readIntInput(String prompt) {
        System.out.print(prompt);
        while (!scan.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            System.out.print(prompt);
            scan.nextLine(); // Consume invalid input
        }
        int input = scan.nextInt();
        scan.nextLine(); // Consume the newline character after the integer
        return input;
    }

    private static void loginMenu() {
        System.out.println("Please enter the Control or Admin password (or 'x' to exit): ");
        String passwordAttempt = scan.nextLine();

        if ("x".equalsIgnoreCase(passwordAttempt)) {
            System.out.println("Exiting system. Goodbye!");
            System.exit(0);
        }
        currentAccessLevel = managementSystem.checkAccess(passwordAttempt);
        if (currentAccessLevel == ManagementSystem.NOACCESS) {
            System.out.println("Access Denied. Please try again.");
        }
    }

    private static void userMenu(){
        System.out.println(
                "Control Menu:\n" +
                "1. Check all rooms info\n" +
                "2. Check all devices info\n" +
                "3. Check all running devices\n" +
                "4. Check all standby devices in the day waiting list\n" +
                "5. Check all standby devices in the power waiting list\n" +
                "6. Search for a given room\n" +
                "7. Search for a given device\n" +
                "8. Turn on/Turn off a device\n" +
                "9. Turn off all devices from one specific room\n" +
                "10. Turn off all devices in the house\n" +
                "11. Check current power consumption\n" +
                "12. Set day/night mode\n" +
                "13. Exit control mode");

        int action = readIntInput("Select action: ");
        // Main menu switch
        switch(action){
            case 1:
                System.out.println("All Rooms:");
                System.out.println("All Rooms Summary:\n" + managementSystem.displaySummaryAllRooms());
                break;
            case 2:
                System.out.println("All Devices Detailed Info:\n" + managementSystem.displayInfo());
                break;
            case 3:
                System.out.println("Currently Running Devices:\n" + managementSystem.displayAllRunningDevices());
                break;
            case 4:
                System.out.println("Devices on Standby (Waiting for Day Time):\n" + managementSystem.listStandByDayDevices());
                break;
            case 5:
                System.out.println("Devices on Standby (Waiting for Power Availability):\n" + managementSystem.listStandByPowerDevices());
                break;
            case 6:
                System.out.print("Enter room code to search: ");
                String roomCodeToSearch = scan.nextLine();
                Room foundRoomDetails = managementSystem.searchRoomByCode(roomCodeToSearch);
                if(foundRoomDetails != null){
                    System.out.println("Room Details:\n" + foundRoomDetails);
                }else{
                    System.out.println("Room with code '" + roomCodeToSearch + "' not found.");
                }
                break;
            case 7:
                int deviceIdToSearch = readIntInput("Enter device ID to search: ");
                Device foundDeviceDetails = managementSystem.searchDeviceById(deviceIdToSearch);
                if(foundDeviceDetails != null){
                    System.out.println("Device Details:\n" + foundDeviceDetails);
                    String roomLocation = managementSystem.searchRoomByDevice(foundDeviceDetails);
                    if (roomLocation != null) {
                        System.out.println("Located in room: " + roomLocation);
                    }
                }else{
                    System.out.println("Device with ID " + deviceIdToSearch + " not found.");
                }
                break;
            case 8:
                int turnOnOffChoice = readIntInput("Device Operations:\n1. Turn ON a device\n2. Turn OFF a device\nSelect choice: ");
                if (turnOnOffChoice != 1 && turnOnOffChoice != 2) {
                    System.out.println("Invalid choice. Please select 1 or 2.");
                    break;
                }

                System.out.print("Enter the room code where the device is located: ");
                String roomCodeForDeviceOp = scan.nextLine();
                Room targetRoomForDeviceOp = managementSystem.searchRoomByCode(roomCodeForDeviceOp);

                if(targetRoomForDeviceOp == null){
                    System.out.println("Room with code '" + roomCodeForDeviceOp + "' not found. Operation cancelled.");
                    break;
                }
                int deviceIdForOp = readIntInput("Enter the device ID: ");
                Device targetDeviceForOp = managementSystem.searchDeviceById(deviceIdForOp);

                if(targetDeviceForOp == null){
                    System.out.println("Device with ID " + deviceIdForOp + " not found. Operation cancelled.");
                    break;
                }

                if (!targetRoomForDeviceOp.getDevicesList().contains(targetDeviceForOp)) {
                    System.out.println("Device with ID " + deviceIdForOp + " is not in room '" + roomCodeForDeviceOp + "'. Operation cancelled.");
                    break;
                }

                if (turnOnOffChoice == 1) {
                    handleTurnOnDevice(roomCodeForDeviceOp, deviceIdForOp, targetDeviceForOp);
                } else { // turnOnOffChoice == 2
                    handleTurnOffDevice(targetDeviceForOp);
                }
                break;
            case 9:
                System.out.print("Enter room code to shut down all its devices: ");
                String roomToShutDown = scan.nextLine();
                Room roomForShutdown = managementSystem.searchRoomByCode(roomToShutDown);
                if (roomForShutdown == null) {
                    System.out.println("Room with code '" + roomToShutDown + "' not found.");
                    break;
                }
                if(managementSystem.checkRoomForCriticalDevice(roomForShutdown)){
                    System.out.print("Critical device(s) detected in room '" + roomToShutDown + "'. Admin password required to shut down all devices: ");
                    String adminPasswordAttempt = scan.nextLine();
                    if (managementSystem.checkAccess(adminPasswordAttempt) == ManagementSystem.ADMIN) {
                        managementSystem.shutDownOneRoom(roomForShutdown);
                        System.out.println("All devices in room '" + roomToShutDown + "' have been turned off.");
                    } else {
                        System.out.println("Incorrect admin password. Only non-critical devices in room '" + roomToShutDown + "' will be turned off.");
                        for (Device dev : roomForShutdown.getDevicesList()) {
                            if (!dev.isCritical()) {
                                managementSystem.turnOffDevice(dev);
                            }
                        }
                        System.out.println("Non-critical devices in room '" + roomForShutdown.getCode() + "' turned off.");
                    }
                }else{
                    managementSystem.shutDownOneRoom(roomForShutdown);
                    System.out.println("All devices in room '" + roomToShutDown + "' turned off successfully.");
                }
                break;
            case 10:
                if(managementSystem.checkAllRoomsForCriticalDevice()) {
                    System.out.print("Critical device(s) detected in the house. Admin password required to shut down all devices: ");
                    String adminPasswordAttempt = scan.nextLine();
                    if (managementSystem.checkAccess(adminPasswordAttempt) == ManagementSystem.ADMIN) {
                        managementSystem.shutDownAllDevices();
                        System.out.println("All devices in the house have been turned off.");
                    } else {
                        System.out.println("Incorrect admin password. Only non-critical devices in the house will be turned off.");
                        for (Room room : managementSystem.getRooms()) {
                            for (Device dev : room.getDevicesList()) {
                                if (!dev.isCritical()) {
                                    managementSystem.turnOffDevice(dev);
                                }
                            }
                        }
                        System.out.println("All non-critical devices in the house turned off.");
                    }
                }else{
                    managementSystem.shutDownAllDevices();
                    System.out.println("All devices in the house turned off successfully.");
                }
                break;

            case 11:
                System.out.println("Current total power consumption: " + managementSystem.getTotalPowerConsumption() + "W");
                break;
            case 12:
                setDayNightMode();
                break;
            case 13:
                currentAccessLevel = ManagementSystem.NOACCESS;
                System.out.println("Exited control mode.");
                break;
            default:
                System.out.println("Invalid action. Please try again.");

        }
    }

    private static void adminMenu(){
        System.out.println(
                "Admin Menu:\n" +
                "1. Change admin and user passwords\n" +
                "2. Change power mode to one of the three possible modes\n" +
                "3. Set day/time mode\n" +
                "4. Add/Delete/Search a room\n" +
                "5. Add/Delete/Search a device\n" +
                "6. Exit Admin Mode");

        int action = readIntInput("Select action: ");

        switch(action){
            case 1:
                int passwordTypeChoice = readIntInput("Change (1) Admin or (2) User password? ");
                System.out.print("Enter new password: ");
                String newPassword = scan.nextLine();
                if (passwordTypeChoice == 1) {
                    try {
                        managementSystem.changeAdminPassword(newPassword);
                        System.out.println("Admin password changed.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                } else if (passwordTypeChoice == 2) {
                    try {
                        managementSystem.changeUserPassword(newPassword);
                        System.out.println("User password changed.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                } else {
                    System.out.println("Invalid choice.");
                }
                break;
            case 2:
                // Change power mode
                // Change power mode
                int powerModeInput = readIntInput("Enter the max allowed power (1 = LOW, 2 = NORMAL, 3 = HIGH):");
                double targetPower = 0;
                String modeString = "";

                switch (powerModeInput) {
                    case 1:
                        targetPower = ManagementSystem.LOW;
                        modeString = "LOW";
                        break;
                    case 2:
                        targetPower = ManagementSystem.NORMAL;
                        modeString = "NORMAL";
                        break;
                    case 3:
                        targetPower = ManagementSystem.HIGH;
                        modeString = "HIGH";
                        break;
                    default:
                        System.out.println("Invalid choice. Power mode not changed.");
                        break; // Break from switch, then from case
                }
                if (targetPower != 0) { // If a valid mode was chosen
                    try {
                        managementSystem.setMaxAllowedPower(targetPower);
                        System.out.println("Max allowed power set to: " + modeString);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error setting power mode: " + e.getMessage());
                    }
                }
                break;
            case 3:
                setDayNightMode();
                break;
            case 4:
                int roomActionChoice = readIntInput("Room Operations:\n1. Add Room\n2. Delete Room\n3. Search Room\nSelect choice: ");
                String roomCodeInput; // Declare here for wider scope if needed across choices
                switch (roomActionChoice) {
                    case 1: // Add Room
                        System.out.print("Enter new room code: ");
                        roomCodeInput = scan.nextLine();
                        System.out.print("Enter room description: ");
                        String roomDescriptionInput = scan.nextLine();
                        if(managementSystem.addRoom(new Room(roomCodeInput, roomDescriptionInput))) {
                            System.out.println("Room added.");
                        } else {
                            System.out.println("Room code already exists or is invalid.");
                        }
                        break;
                    case 2: // Delete Room
                        System.out.print("Enter room code to delete: ");
                        String roomCodeToDelete = scan.nextLine(); // Changed variable name for clarity
                        if(managementSystem.removeRoomByCode(roomCodeToDelete)) {
                            System.out.println("Room '" + roomCodeToDelete + "' removed successfully!");
                        } else {
                            System.out.println("Room with code '" + roomCodeToDelete + "' not found or could not be removed.");
                        }
                        break;
                    case 3: // Search Room
                        System.out.print("Enter room code to search: ");
                        String roomCodeToSearch = scan.nextLine(); 
                        Room foundRoom = managementSystem.searchRoomByCode(roomCodeToSearch);
                        if(foundRoom != null){
                            System.out.println("Room found:\n" + foundRoom);
                        }else{
                            System.out.println("Room with code '" + roomCodeToSearch + "' not found.");
                        }
                        break;
                    default:
                        System.out.println("Invalid room operation choice. Please try again.");
                        break;
                }
                break;
            case 5:
                // Add/Delete/Search a device operations
                addDeleteSearchDevice();
                break;
            case 6:
                currentAccessLevel = ManagementSystem.NOACCESS; // Log out
                System.out.println("Exited Admin Mode."); // Added feedback for exiting admin mode
                break;
            default:
                System.out.println("Invalid action. Please try again.");
        }
    }

    public static void setDayNightMode(){
        int dayNightChoice = readIntInput("Set mode:\n1. Day\n2. Night\nEnter choice: ");
        if(dayNightChoice == 1){ // Day mode
            managementSystem.setDayTime();
            System.out.println("System set to Day Mode.");
            if(managementSystem.anyLightIsOn()){
                System.out.println("Lights are currently on.");
                if(readIntInput("Turn off all lights? (1 = Yes, Other = No): ") == 1) {
                    managementSystem.turnOffAllLightsInHouse();
                    System.out.println("All lights turned off.");
                } else {
                    System.out.println("Lights kept on.");
                }
            }
        }else if(dayNightChoice == 2){ // Night mode
            managementSystem.setNightTime();
            System.out.println("System set to Night Mode.");
            if (managementSystem.checkForRunningNoisyDevices()){
                System.out.println("Noisy devices are currently running.");
                System.out.println("Options:\n1. Keep them on\n2. Add to day waiting list (standby)\n3. Turn them off");
                int noisyDeviceAction = readIntInput("Enter your choice: ");
                switch(noisyDeviceAction){
                    case 1:
                        System.out.println("Noisy devices kept on.");
                        break;
                    case 2:
                        managementSystem.addNoisyDevicesToWaitingListDay();
                        System.out.println("Noisy devices added to day waiting list (standby).");
                        break;
                    case 3:
                        managementSystem.setNoisyDeviceStatus(Device.OFF);
                        System.out.println("Noisy devices turned off.");
                        break;
                    default:
                        System.out.println("Invalid option for noisy devices.");
                        break;
                }
            }
        }else {
            System.out.println("Invalid day/night mode choice.");
        }
    }

    public static void addDeleteSearchDevice(){
        // Device management submenu
        switch (readIntInput("Device Operations:\n1. Add Device\n2. Delete Device\n3. Search Device\nSelect choice: ")){
            case 1:
                addDevice();
                break;
            case 2:
                deleteDevice();
                break;
            case 3:
                searchDevice();
                break;
            default:
                System.out.println("Invalid input");
                break;
        }
    }

    public static void addDevice(){
        System.out.print("Enter room code to add device to: ");
        String roomCodeInput = scan.nextLine(); // Changed from scan.next()
        Room targetRoom = managementSystem.searchRoomByCode(roomCodeInput);
        if(targetRoom == null) {
            System.out.println("Room not found.");
        } else {
            int deviceTypeChoice = readIntInput("Add:\n1. Light\n2. Appliance\nEnter choice: ");
            switch(deviceTypeChoice) {
                case 1: // Add Light
                    int lightId = readIntInput("Enter light ID: ");
                    System.out.print("Enter light name: ");
                    String lightName = scan.nextLine();
                    System.out.print("Enter max power consumption for light: ");
                    double lightMaxPower = Double.parseDouble(scan.nextLine()); // Using Double.parseDouble
                    boolean isCriticalLight = (readIntInput("Is the light critical? (1=Yes, 0=No): ") == 1);
                    boolean isAdjustableLight = (readIntInput("Is the light adjustable? (1=Yes, 0=No): ") == 1);
                    try {
                        managementSystem.addDevice(new Light(lightId, lightName, lightMaxPower, isCriticalLight, isAdjustableLight), targetRoom);
                        System.out.println("Light added successfully.");
                    } catch (RoomNotFoundException | DuplicateDeviceIdException | IllegalArgumentException e) { // Catching more specific exceptions
                        System.out.println("Error adding light: " + e.getMessage());
                    }
                    break;
                case 2: // Add Appliance
                    int applianceId = readIntInput("Enter appliance ID: ");
                    System.out.print("Enter appliance name: ");
                    String applianceName = scan.nextLine();
                    System.out.print("Enter max power consumption for appliance: ");
                    double applianceMaxPower = Double.parseDouble(scan.nextLine());
                    boolean isCriticalAppliance = (readIntInput("Is the appliance critical? (1=Yes, 0=No): ") == 1);

                    ArrayList<Integer> powerLevelsList = new ArrayList<>();
                    System.out.println("Enter power levels (percentage, 0-100), one per line. Type -1 to finish:");
                    int powerLevelInput;
                    while ((powerLevelInput = readIntInput("")) != -1) {
                        if (powerLevelInput >= 0 && powerLevelInput <= 100) {
                            powerLevelsList.add(powerLevelInput);
                        } else {
                            System.out.println("Invalid power level. Must be between 0 and 100.");
                        }
                    }
                    int[] powerLevelsArray = powerLevelsList.stream().mapToInt(Integer::intValue).toArray();

                    boolean isNoisyAppliance = (readIntInput("Is the appliance noisy? (1=Yes, 0=No): ") == 1);
                    try {
                        managementSystem.addDevice(new Appliance(applianceId, applianceName, applianceMaxPower, isCriticalAppliance, powerLevelsArray, isNoisyAppliance), targetRoom);
                        System.out.println("Appliance added successfully.");
                    } catch (RoomNotFoundException | DuplicateDeviceIdException | IllegalArgumentException e) {
                        System.out.println("Error adding appliance: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Invalid device type choice.");
                    break;
            }
        }
    }

    public static void deleteDevice(){
        int deviceIdToDelete = readIntInput("Enter ID of the device to delete: ");
        if(managementSystem.removeDeviceById(deviceIdToDelete)){
            System.out.println("Device with ID " + deviceIdToDelete + " removed successfully.");
        } else {
            System.out.println("Device with ID " + deviceIdToDelete + " not found or could not be removed.");
        }
    }

    public static void searchDevice(){
        int deviceIdToSearch = readIntInput("Enter ID of the device to search: ");
        Device foundDevice = managementSystem.searchDeviceById(deviceIdToSearch);
        if(foundDevice == null){
            System.out.println("Device not found.");
        }else {
            System.out.println("Found device: " + foundDevice.toString());
            String roomCode = managementSystem.searchRoomByDevice(foundDevice);
            if (roomCode != null) {
                System.out.println("Located in room: " + roomCode);
            } else {
                // This case should ideally not happen if allDevices and room device lists are consistent
                System.out.println("Device is in the system but not currently assigned to a room.");
            }
        }
    }
}
