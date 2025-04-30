import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.DoubleToIntFunction;

public class DashboardTester {
    public final static Scanner scan = new Scanner(System.in);
    private static ManagementSystem managementSystem;
    //0 = not logged in; 1 = user login; 2 = admin login
    //(role) is a method in managementsystem class
    private static int role = 0;

    public static void main(String[] args) {
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
            if(userPwd.equals(adminPwd)){
                System.out.println("User password and admin password have to be different!");
            }
        } while (!ManagementSystem.passwordIsValid(userPwd) && !userPwd.equals(adminPwd));
        managementSystem = new ManagementSystem(adminPwd, userPwd);

        //Roles admin/user/exit to main menu
        System.out.println("Welcome to your House Management System");
        while (true) {
            if (role == 0) {
                loginMenu();
            } else if (role == 1) {
                userMenu();
            } else {
                adminMenu();
            }
        }
    }
    //This prompt the method checkAccess to define admin/user role
    private static void loginMenu() {
        System.out.println("Please enter the Contol or Admin password (or x to exit): ");
        String pswd = scan.nextLine();

        if ("x".equals(pswd)) {
            System.exit(0);
        }
        role = managementSystem.checkAccess(pswd);
        if (role == 0) {
            System.out.println("Access Denied, Try again: ");
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

        System.out.print("Select action: ");
        int action = scan.nextInt();
        scan.nextLine(); //this to clear the buffer for the next action to be made
        //switch case for a clean dashboard experience in the terminal
        //includes all the methods and their actions

        switch(action){
            case 1:
                // Check all rooms info
                System.out.println("All Rooms:");
                System.out.println(managementSystem.displaySummaryAllRooms());
                break;
            case 2:
                //Check all devices info
                System.out.println(managementSystem.displayInfo());
                break;
            case 3:
                // Check all running devices
                System.out.println("Running Devices: \n");
                System.out.println(managementSystem.displayAllRunningDevices());
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
                String roomCode = scan.nextLine();
                Room foundRoom = managementSystem.searchRoomByCode(roomCode);
                if(foundRoom != null){
                    System.out.println(foundRoom);
                }else{
                    System.out.println("Room not found.");
                }
                break;
            case 7:
                // Search for a given device
                System.out.print("Enter device id: ");
                int deviceId = scan.nextInt();
                scan.nextLine();
                Device foundDevice = managementSystem.searchDeviceById(deviceId);
                if(foundDevice != null){
                    System.out.println(foundDevice);
                }else{
                    System.out.println("Device not found.");
                }
                break;
            case 8:
                //Turn on / Turn off a device
                System.out.print("Do you want to:\n1. Turn on a device\nother. Turn off a device\n");
                int choice = scan.nextInt();
                System.out.print("Enter the room code: ");
                String rCode = scan.next();
                Room r = managementSystem.searchRoomByCode(rCode);
                if(r == null){
                    System.out.println("Invalid room code");
                    break;
                }
                System.out.print("Enter the device id: ");
                int dId = scan.nextInt();
                Device d = managementSystem.searchDeviceById(dId);
                if(d == null){
                    System.out.println("Invalid device id");
                    break;
                }
                switch(choice){
                    case 1:
                        //light or app
                        int c = 0;
                        if(d instanceof Light){
                            if(!((Light)d).isAdjustable()){
                                c = 100;
                            } else {
                                System.out.print("Enter the brightness level: ");
                                c = scan.nextInt();
                            }
                        } else {
                            System.out.println("Enter the power level from the below:");
                            int[] levels = ((Appliance)d).getPowerLevels();
                            for(int i = 0; i<levels.length; i++){
                                System.out.println(i +". " + levels[i]);
                            }
                            c = scan.nextInt();
                            if(c >= levels.length) c = levels.length-1;
                            if(c < 0) c = 0;
                        }
                        //ON
                        switch(managementSystem.checkTurnOnDevice(d)){
                            case 0:
                                //ok
                                managementSystem.turnOnDevice(rCode, dId, c);
                                System.out.println("Device turned on");
                                break;
                            case 1:
                                //noisy night
                                System.out.println("the device is noisy and it's night");
                                System.out.println("What do you want to do?");
                                System.out.println("1. Turn on anyways");
                                System.out.println("2. Put the device on standby");
                                System.out.println("3. Dont turn on the device");
                                System.out.print("Enter your choice: ");
                                switch(scan.nextInt()){
                                    case 1:
                                        managementSystem.turnOnDevice(rCode, dId, c);
                                        System.out.println("Device turned on");
                                        break;
                                    case 2:
                                        managementSystem.addDeviceToWaitingListDay(d);
                                        System.out.println("Device added to wait list");
                                        break;
                                    case 3:
                                        System.out.println("Cancelled order!");
                                        break;
                                    default:
                                        System.out.println("Invalid option!");
                                        break;
                                }
                                break;
                            case 2:
                                // not enoigh power
                                System.out.println("Not enough power!");
                                System.out.println("What do you want to do?");
                                System.out.println("1. add device to waitlist");
                                System.out.println("2. cancel");
                                System.out.print("Enter your choice: ");
                                switch (scan.nextInt()){
                                    case 1:
                                        managementSystem.addDeviceToWaitingListPower(d);
                                        System.out.println("Device added to waitlist");
                                        break;
                                    case 2:
                                        System.out.println("Cancelled order!");
                                        break;
                                    default:
                                        System.out.println("Invalid option");
                                        break;
                                }
                                break;
                        }
                        break;
                    default:
                        //OFF
                        if(d.isCritical()){
                            System.out.println("Device is critical. Please enter admin password to procceed.");
                            if(ManagementSystem.passwordIsValid(scan.next())){
                                managementSystem.turnOffDevice(d);
                                System.out.println("device is off");
                            } else {
                                System.out.println("Invalid password! Unable to turn off!");
                            }
                            break;
                        } else {
                            managementSystem.turnOffDevice(d);
                            System.out.println("device is off");
                        }
                        break;
                }
                break;

            case 9:
                // Turn off all devices from one specific room
                System.out.println("Enter room code to turn off all devices: ");
                roomCode = scan.nextLine();
                foundRoom = managementSystem.searchRoomByCode(roomCode);
                if(managementSystem.checkRoomForCriticalDevice(foundRoom)){
                    System.out.print("Critical device/s detected in the room. Please enter the admin password to proceed: ");
                    String adminPassword = scan.nextLine();
                    int accessLevel = managementSystem.checkAccess(adminPassword);
                    if (accessLevel == ManagementSystem.ADMIN) {
                        managementSystem.shutDownAllDevices();
                        System.out.println("All devices in the room have been turned off.");
                    } else {
                        managementSystem.shutDownOneRoom(foundRoom);
                        managementSystem.setRoomCriticalDeviceStatus(foundRoom, Device.ON);
                        System.out.println("Incorrect admin password. " +
                                "Only non critical devices in the room have been turned off");
                    }
                }else{
                    managementSystem.shutDownOneRoom(foundRoom);
                }
                break;
            case 10:
                // Turn off all devices in the house
                if(managementSystem.checkAllRoomsForCriticalDevice()) {
                    System.out.print("Critical device/s detected in the house. Please enter the admin password to proceed: ");
                    String adminPassword = scan.nextLine();
                    int accessLevel = managementSystem.checkAccess(adminPassword);
                    if (accessLevel == ManagementSystem.ADMIN) {
                        managementSystem.shutDownAllDevices();
                        System.out.println("All devices in the house have been turned off.");
                    } else {
                        managementSystem.shutDownAllDevices();
                        managementSystem.setAllCriticalDeviceStatus(Device.ON);
                        System.out.println("Incorrect admin password! Only non-critical devices have been turned off.");
                    }
                }else{
                    managementSystem.shutDownAllDevices();
                }
                break;

            case 11:
                // Check current power consumption
                System.out.println("Current power consumption: " + managementSystem.getTotalPowerConsumption());
                break;
            case 12:
                //Set day/night mode
                setDayNightMode();
                break;
            case 13:
                role = 0;
                break;
            default:
                System.out.println("Invalid action");

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
                    System.out.println("Max allowed power is set to: LOW");
                }
                else if(mPower == 2){
                    managementSystem.maxAllowedPower = ManagementSystem.NORMAL;
                    System.out.println("Max allowed power is set to: NORMAL");
                }else{
                    managementSystem.maxAllowedPower = ManagementSystem.HIGH;
                    System.out.println("Max allowed power is set to: HIGH");
                }
                break;
            case 3:
                // Set day time mode
                setDayNightMode();
                break;
            case 4:
                //Add/Delete/Search a room
                System.out.println("1. Add Room\n2. Delete Room\n3. Search Room");
                int actionRoom = scan.nextInt();
                scan.nextLine();
                if(actionRoom == 1){
                    System.out.print("Enter room code: ");
                    String rCode = scan.nextLine();
                    System.out.print("Enter room description: ");
                    String rDesc = scan.nextLine();
                    if(managementSystem.addRoom(new Room(rCode, rDesc))) {
                        System.out.println("Room added.");
                    } else {
                        System.out.println("FFFFFF");
                    }
                }else if (actionRoom == 2){
                    System.out.print("Enter room code to delete: ");
                    String code = scan.nextLine();
                    Room roomToRemove = managementSystem.searchRoomByCode(code);
                    if(roomToRemove != null){
                        managementSystem.removeRooms(roomToRemove);
                        System.out.println("Room removed!");
                    }else{
                        System.out.println("Room not found!");
                    }
                }else if(actionRoom == 3){
                    System.out.print("Enter room code to search: ");
                    String code = scan.nextLine();
                    Room foundRoom = managementSystem.searchRoomByCode(code);
                    if(foundRoom != null){
                        System.out.println("Room found:\n" + foundRoom);
                    }else{
                        System.out.println("Room not found!");
                    }
                }else{
                    System.out.println("Invalid option!");
                }
                break;

            case 5:
                addDeleteSearchDevice();
                break;
            case 6:
                role = 0;
                break;
            default:
                System.out.println("Invalid action");
        }
    }

    public static void setDayNightMode(){
        System.out.print("Set mode 1 to Day or 2 to Night: ");
        int dayNight = scan.nextInt();
        scan.nextLine();
        if(dayNight == 1){
            managementSystem.setDayTime();
            System.out.println("System set to day mode.");
        }else if(dayNight == 2){
            managementSystem.setNightTime();
            if (managementSystem.checkForRunningNoisyDevices()){
                System.out.println("It is night! Noisy devices detected!");
                System.out.println("Choose an option: ");
                System.out.println("1. Keep the devices on anyways");
                System.out.println("2. Add the devices to the day waitlist");
                System.out.println("3. Turn the noisy devices off");
                switch(scan.nextInt()){
                    case 1:
                        System.out.println("Noisy devices kept on");
                        managementSystem.setNightTime();
                        System.out.println("It is night!");
                        break;
                    case 2:
                        managementSystem.addNoisyDevicesToWaitingListDay();
                        System.out.println("Noisy devices added to waitlist");
                        managementSystem.setNightTime();
                        System.out.println("It is night!");
                        break;
                    case 3:
                        managementSystem.setNoisyDeviceStatus(Device.OFF);
                        managementSystem.setNightTime();
                        System.out.println("Noisy devices turned off");
                        break;
                    default:
                        System.out.println("Invalid Option");
                        break;
                }
            }
            System.out.println("System set to night mode.");
        }else {
            System.out.println("Invalid input.");
        }
    }

    public static void addDeleteSearchDevice(){
        //Add/Delete/Search a device
        System.out.println("1. Add Device\n2. Delete Device\n3. Search Device");
        switch (scan.nextInt()){
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
        System.out.print("Enter room id: ");
        String roomId = scan.next();
        Room r = managementSystem.searchRoomByCode(roomId);
        if(r == null) {
            System.out.println("Invalid room");
        } else {
            System.out.println("Do you want to add a light or an appliance?");
            System.out.println("1. light, 2. appliance");
            System.out.print("Enter your choice: ");
            int c = scan.nextInt();
            switch(c) {
                case 1:
                    //light
                    System.out.print("Enter id: ");
                    int id = scan.nextInt();
                    System.out.print("Enter name: ");
                    String name = scan.next();
                    System.out.print("Enter max power consumption: ");
                    double maxPowerConsumption = scan.nextDouble();
                    System.out.print("Is the device critical? (0=false, 1=true)");
                    boolean critical = (scan.nextInt() == 1 ? true : false);
                    System.out.print("Is the device adjustable? (0=false, 1=true)");
                    boolean adjustable = (scan.nextInt() == 1 ? true : false);
                    managementSystem.addDevice(new Light(id, name, maxPowerConsumption, critical, adjustable), r);
                    System.out.println("Added");
                    break;
                case 2:
                    //appliance
                    System.out.print("Enter id: ");
                    int idR = scan.nextInt();
                    System.out.print("Enter name: ");
                    String nameR = scan.next();
                    System.out.print("Enter max power consumption: ");
                    double maxPowerConsumptionR = scan.nextDouble();
                    System.out.print("Is the device critical? (0=false, 1=true)");
                    boolean criticalR = (scan.nextInt() == 1 ? true : false);
                    System.out.println("enter the power levels, one at a time (-1 to stop)");
                    ArrayList<Integer> nums = new ArrayList<Integer>();
                    int n = scan.nextInt();
                    while (n != -1) {
                        if (n >= 0 && n <= 100)
                            nums.add(n);
                        else {
                            System.out.println("Invalid");
                            continue;
                        }
                        n = scan.nextInt();
                    }
                    int[] numsArray = new int[nums.size()];
                    for (int i = 0; i < numsArray.length; i++) {
                        numsArray[i] = nums.get(i);
                    }
                    System.out.print("Is the device noisy? (0=false, 1=true)");
                    boolean noisy = (scan.nextInt() == 1 ? true : false);
                    managementSystem.addDevice(new Appliance(idR, nameR, maxPowerConsumptionR, criticalR, numsArray, noisy), r);
                    System.out.println("Added");
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    public static void deleteDevice(){
        //id
        System.out.print("Enter the id: ");
        int dId = scan.nextInt();
        Device d = managementSystem.searchDeviceById(dId);
        if(d == null){
            System.out.println("device invalid");
        }else managementSystem.removeDevice(d);
    }

    public static void searchDevice(){
        //id
        System.out.print("Enter the id: ");
        int dId = scan.nextInt();
        Device d = managementSystem.searchDeviceById(dId);
        if(d == null){
            System.out.println("device invalid");
        }else {
            System.out.println(d.toString() + " room code: " + managementSystem.searchRoomByDevice((d)));
        }
    }
}
