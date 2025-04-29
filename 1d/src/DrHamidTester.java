import java.util.Scanner;

public class DrHamidTester {

    private static ManagementSystem managementSystem;
    //0 = not logged in; 1 = user login; 2 = admin login
    //(role) is a method in managementsystem class
    private static int role = 0;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        //prompt and check the adminn and user password to be true and stores it
        String adminPwd;
        do {
            System.out.print("Set the initial Admin pass: ");
            adminPwd = scan.nextLine();
            if (!ManagementSystem.passwordIsValid(adminPwd)) {
                System.out.println("Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character.");
            }
        } while (!ManagementSystem.passwordIsValid(adminPwd));

        String userPwd;
        do {
            System.out.print("Set the initial User Pass: ");
            userPwd = scan.nextLine();
            if (!ManagementSystem.passwordIsValid(userPwd)) {
                System.out.println("Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character.");
            }
        } while (!ManagementSystem.passwordIsValid(userPwd));
        managementSystem = new ManagementSystem(adminPwd, userPwd);

        //Roles admin/user/exit to main menu
        while (true) {
            if (role == 0) {
                loginMenu(scan);
            } else if (role == 1) {
                userMenu(scan);
            } else {
                adminMenu(scan);
            }
        }
    }

    //This prompt the method checkAccess to define admin/user role
    private static void loginMenu(Scanner scan) {
        System.out.println("Welcome to your House Management System");
        System.out.println("Please enter the User or Admin password (or x to exit): ");
        String pswd = scan.nextLine();

        if ("x".equals(pswd)) {
            System.exit(0);
        }
        role = managementSystem.checkAccess(pswd);
        if (role == 0) {
            System.out.println("Access Denied, Try again: ");
        }
    }

    private static void userMenu(Scanner scan){
        System.out.println(
                "1. Check all rooms info\n" +
                "2. Check all devices info\n" +//under construction
                "3. Check all running devices\n" +//needs implementation
                "4. Check all standby devices in the day waiting list\n" +
                "5. Check all standby devices in the power waiting list\n" +
                "6. Search for a given room\n" +
                "7. Search for a given device\n" +
                "8. Turn on/Turn off a device\n" +
                "9. Turn off all devices from one specific room\n" +//??
                "10. Turn off all devices in the house\n" + //needs implementation
                "11. Check current power consumption\n" +
                "12. Set day/night mode\n" +
                "0. Exit user mode");

        System.out.print("Select action: ");
        int action = scan.nextInt();
        scan.nextLine(); //this to clear the buffer for the next action to be made
        //switch case for a clean dashboard experience in the terminal
        //includes all the methods and their actions

        switch(action){
            case 1:
                // Check all rooms info
                System.out.println(managementSystem.displaySummaryAllRooms());
                break;
            case 2:
                //Check all devices info
                //??
            case 3:
                // Check all running devices
                //if device is running displaySummaryOfDevices
                System.out.println(managementSystem.displaySummaryAllRooms());
                break;
            case 4:
                // Check all standby devices in the day waiting list
                System.out.println("Devices waiting for day time:");
                System.out.println(managementSystem.listStandByDayDevices());
                break;
            case 5:
                // Check all standby devices in the power waiting list
                System.out.println("Devices waiting for power availability:");
                System.out.println(managementSystem.listStandByPowerDevices());
                break;
            case 6:
                // Search for a given room
                System.out.print("Enter room code: ");
                String r3 = scan.nextLine();
                Room foundRoom = managementSystem.searchRoomByCode(r3);
                if (foundRoom != null) {
                    System.out.println(foundRoom);
                } else {
                    System.out.println("Room not found.");
                }
                break;
            case 7:
                // Search for a given device
                System.out.print("Enter device id: ");
                int d1 = scan.nextInt();
                scan.nextLine();
                Device foundDevice = managementSystem.searchDeviceById(d1);
                if (foundDevice != null) {
                    System.out.println(foundDevice);
                } else {
                    System.out.println("Device not found.");
                }
                break;
            case 8:
                 // Turn on/ Turn off a device
                 System.out.print("Enter device id: ");
                 d1 = scan.nextInt();
                 scan.nextLine();
                 foundDevice = managementSystem.searchDeviceById(d1);
                 if (foundDevice != null) {
                 System.out.print("Turn on (1) or off (0): ");
                 int onOff = scan.nextInt();
                 scan.nextLine();
                 if (onOff == 1) foundDevice.turnOn();
                 else foundDevice.turnOff();
                 System.out.println("Device updated: " + foundDevice);
                 } else {
                 System.out.println("Device not found.");
                 }
                 break;

            case 9:
                // Turn off all devices from one specific room
                //AI help
                System.out.print("Enter room code: ");
                String code = scan.nextLine();
                foundRoom = managementSystem.searchRoomByCode(code);
                if (foundRoom != null) {
                    for (Device device : foundRoom.getDevicesList()) {
                        device.turnOff();
                    }
                    System.out.println("All devices in room turned off.");
                } else {
                    System.out.println("Room not found.");
                }
                break;
            case 10:
                // Turn off all devices in the house
                // If device is critical prompt the admin password
                //same as 9 and then another loop to turn off all devices in the house

            case 11:
                // Check current power consumption
                System.out.println("Current power consumption: " + managementSystem.getTotalPowerConsumption());
                break;
            case 12:
                // Set day/night mode
                System.out.print("Set mode (1 = Day, 0 = Night): ");
                int timeMode = scan.nextInt();
                scan.nextLine();
                if (timeMode == 1) managementSystem.setDayTime();
                else managementSystem.setNightTime();
                System.out.println("Mode updated.");
                break;
            case 0:
                role = 0;
                break;
            default:
                System.out.println("Invalid action");

        }
    }

    private static void adminMenu(Scanner scan){
        System.out.println(
                "Admin Menu:\n" +
                "1. Change admin and user passwords\n" +
                "2. Change power mode to one of the three possible modes\n" +
                "3. Set day/time mode\n" +
                "4. Add/Delete/Search a room\n" +
                "5. Add/Delete/Search a device\n" +
                "0. Logout");

        System.out.print("Select action: ");
        int action = scan.nextInt();
        scan.nextLine(); //also to clear the buffer

        switch(action){
            case 1:
                // Change admin and user passwords
                System.out.print("Change (1) Admin or (2) User password? ");
                int type = scan.nextInt();
                scan.nextLine();
                System.out.print("Enter new password: ");
                String newPass = scan.nextLine();
                if (type == 1) {
                    managementSystem.changeAdminPassword(newPass);
                    System.out.println("Admin password changed.");
                } else if (type == 2) {
                    managementSystem.changeUserPassword(newPass);
                    System.out.println("User password changed.");
                } else {
                    System.out.println("Invalid choice.");
                }
                break;
            case 2:
                // Change power mode
                System.out.println("Enter the max allowed power (1 = low, 2= normal, 3 = high)");
                int mPower = scan.nextInt();
                scan.nextLine();
                if(mPower == 1){
                    managementSystem.maxAllowedPower = ManagementSystem.LOW;
                }
                else if(mPower == 2){
                    managementSystem.maxAllowedPower = ManagementSystem.NORMAL;
                }else{
                    managementSystem.maxAllowedPower = ManagementSystem.HIGH;
                }
                break;
            case 3:
                // Set day time mode
                System.out.print("Set mode 1 to Day or 2 to Night: ");
                int dayNight = scan.nextInt();
                scan.nextLine();
                if (dayNight == 1) {
                    managementSystem.setDayTime();
                    System.out.println("System set to day mode.");
                }else if (dayNight == 2) {
                    managementSystem.setNightTime();
                    System.out.println("System set to night mode.");
                }else {
                    System.out.println("Invalid input.");
                }
                break;
            case 4:
                // Add/Delete/Search a room
                //can be made with switch case (Better?)
                System.out.println("1. Add Room\n 2. Delete Room\n 3. Search Room");
                int actionRoom = scan.nextInt();
                scan.nextLine();
                if(actionRoom == 1){
                    System.out.print("Enter room code: ");
                    String rCode = scan.nextLine();
                    System.out.print("Enter room description: ");
                    String rDesc = scan.nextLine();
                    managementSystem.addRoom(new Room(rCode, rDesc));
                    System.out.println("Room added.");
                }else if (actionRoom == 2){
                    System.out.print("Enter room code to delete: ");
                    String code = scan.nextLine();
                    Room roomToRemove = managementSystem.searchRoomByCode(code);
                    if(roomToRemove != null){
                        managementSystem.removeRooms(roomToRemove);
                        System.out.println("Room removed ");
                    }else{
                        System.out.println("Room not found ");
                    }
                }else if(actionRoom == 3){
                    System.out.print("Enter room code to search: ");
                    String code = scan.nextLine();
                    Room foundRoom = managementSystem.searchRoomByCode(code);
                    if(foundRoom != null){
                        System.out.println("Room found:\n" + foundRoom);
                    }else{
                        System.out.println("Room not found.");
                    }
                }else{
                    System.out.println("Invalid option.");
                }
                break;

            case 5:
                // Add/Delete/Search a device
                //can be made with switch case (Better?)
                System.out.println("1. Add Device\n 2. Delete Device\n 3. Search Device");
                int actionDevice = scan.nextInt();
                scan.nextLine();
                if (actionDevice == 1) {
                    //device id
                    System.out.print("Enter device id: ");
                    int deviceId = scan.nextInt();
                    scan.nextLine();
                    //device name
                    System.out.print("Enter device name: ");
                    String deviceName = scan.nextLine();
                    //max power consumption
                    System.out.print("Enter max power consumption: ");
                    double maxPower = scan.nextDouble();
                    scan.nextLine();
                    //isCritical
                    System.out.print("Is the device critical? (true/false): ");
                    boolean criticalInput = scan.nextBoolean();
                    scan.nextLine();
                    //device power levels
                    System.out.print("Enter the power level: ");
                    int singlePowerLevel = scan.nextInt();
                    scan.nextLine();
                    int[] powerLevels = new int[] { singlePowerLevel };
                    //isNoisy
                    System.out.print("Is the device noisy? (true/false): ");
                    boolean noisyInput = scan.nextBoolean();
                    scan.nextLine();

                    System.out.print("Enter room code to add this device: ");
                    String roomCode = scan.nextLine();

                    Room room = managementSystem.searchRoomByCode(roomCode);
                    if (room != null) {
                        Appliance device = new Appliance(deviceId, deviceName, maxPower, noisyInput, powerLevels, criticalInput);
                        int result = managementSystem.addDevice(device, room);
                        if (result == 0) {
                            System.out.println("Device added.");
                        } else if (result == 1) {
                            System.out.println("Room not found.");
                        } else {
                            System.out.println("Duplicate device id.");
                        }
                    } else {
                        System.out.println("Room not found.");
                    }
                } else if (actionDevice == 2) {
                    System.out.print("Enter device id to delete: ");
                    int deviceId = scan.nextInt();
                    scan.nextLine();
                    Device device = managementSystem.searchDeviceById(deviceId);
                    if (device != null) {
                        managementSystem.removeDevice(device);
                        System.out.println("Device deleted.");
                    } else {
                        System.out.println("Device not found.");
                    }
                } else if (actionDevice == 3) {
                    System.out.print("Enter device id to search: ");
                    int deviceId = scan.nextInt();
                    scan.nextLine();
                    Device device = managementSystem.searchDeviceById(deviceId);
                    if (device != null) {
                        System.out.println("Device found:\n" + device);
                    } else {
                        System.out.println("Device not found.");
                    }
                } else {
                    System.out.println("Invalid option.");
                }
                break;

            case 0:
                role = 0;
                break;
            default:
                System.out.println("Invalid action");
        }
    }

}