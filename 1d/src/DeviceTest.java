import java.util.Scanner;
public class DeviceTest {
    private static ManagementSystem managementSystem;
    //0 = not logged in; 1 = logged in; 2 = admin log in
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
        do{
            System.out.print("Set the initial User Pass: ");
            userPwd = scan.nextLine();
            if(!ManagementSystem.passwordIsValid(userPwd)){
                System.out.println("Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character.");
            }
        }while(!ManagementSystem.passwordIsValid(userPwd));
        managementSystem = new ManagementSystem(adminPwd, userPwd);

        //Roles admin/user/exit to main menu
        while(true){
            if(role == 0){
                loginMenu(scan);
            }else if(role == 1){
                userMenu(scan);
            }else{
                adminMenu(scan);
            }
        }
    }

    //This prompt the method checkAccess to define admin/user role
    private static void loginMenu(Scanner scan){
        System.out.println("Welcome to your House Management System");
        System.out.println("Please enter the User or Admin password (or x to exit): ");
        String pswd = scan.nextLine();

        if("x".equals(pswd)){
            System.exit(0);
        }
        role = managementSystem.checkAccess(pswd);
        if(role == 0){
            System.out.println("Access Denied, Try again: ");
        }
    }

    private static void userMenu(Scanner scan){
        System.out.println(
                "\n User Menu: \n" +
                        "1. Change My Password\n" +
                        "2. Display Summary of All Rooms\n" +
                        "3. Display Details of One Room\n" +
                        "4. List Standby Devices (Day)\n" +
                        "5. List Standby Devices (Power)\n" +
                        "6. Set Day Mode\n" +
                        "7. Set Night Mode\n" +
                        "8. Turn ON a Device\n" +
                        "9. Turn OFF a Device\n" +
                        "10. Turn ON all Lights\n" +
                        "11. Display System Info\n" +
                        "0. Logout"
        );
        System.out.println("Select action: ");
        int action = scan.nextInt();
        scan.nextLine(); //this to clear the buffer for the next action to be made

        //switch case for a clean dashboard experience in the terminal
        //includes all the methods and their actions


        switch(action){
            case 1:
                System.out.println("Enter your new password:");
                managementSystem.changeUserPassword(scan.nextLine());
                break;
            case 2:
                System.out.println(managementSystem.displaySummaryAllRooms());
                break;
            case 3:
                System.out.println("Enter the room code: ");
                System.out.println(managementSystem.displayDetailsOneRoom(scan.nextLine()));
                break;
            case 4:
                System.out.println(managementSystem.listStandByDayDevices());
                break;
            case 5:
                System.out.println(managementSystem.listStandByPowerDevices());
                break;
            case 6:
                managementSystem.setDayTime();
                break;
            case 7:
                managementSystem.setNightTime();
                break;
            case 8:
                System.out.println("Enter the room code you want to turn on the device in: ");
                String roomC = scan.nextLine();
                System.out.println("Enter the device id you want to turn on: ");
                int devId = scan.nextInt();
                scan.nextLine();
                managementSystem.turnOnDevice(roomC, devId);
                break;
            case 9:
                System.out.println("Enter the room code you want to turn off the device in: ");
                String roomC1 = scan.nextLine();
                System.out.println("Enter the device id you want to turn off: ");
                int devId1 = scan.nextInt();
                scan.nextLine();
                break;
            case 10:
                managementSystem.turnOnAllLightsInHouse();
                break;
            case 11:
                System.out.println(managementSystem.displayInfo());
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
                "\n Admin Menu: \n" +
                        "1. Change My Password\n" +
                        "2. Change User Password\n" +
                        "3. Add Room\n" +
                        "4. Remove Room\n" +
                        "5. Add Device to Room\n" +
                        "6. Remove Device\n" +
                        "7. Display Summary of All Rooms\n" +
                        "8. Display Details of One Room\n" +
                        "9. Set Max Allowed Power\n" +
                        "10. Shut Down Room\n" +
                        "11. Shut Down All Devices\n" +
                        "12. List Standby Devices (Day)\n" +
                        "13. List Standby Devices (Power)\n" +
                        "14. Turn ON a Device\n" +
                        "15. Turn OFF a Device\n" +
                        "16. Turn ON all Lights\n" +
                        "17. Search Room by Code\n" +
                        "18. Search Device by ID\n" +
                        "19. Display System Info\n" +
                        "0. Logout"
        );
        System.out.print("Select action: ");
        int action = scan.nextInt();
        scan.nextLine(); //also to clear the buffer

        switch(action){
            case 1:
                System.out.println("Enter your new admin password");
                managementSystem.changeAdminPassword(scan.nextLine());
                break;
            case 2:
                System.out.println("Enter your new user password");
                managementSystem.changeUserPassword(scan.nextLine());
                break;
            case 3:
                /**
                 * Add Room
                 * maxPower consumption?
                 */
            case 4:
                System.out.println("Enter the room code to remove");
                String code = scan.nextLine();
                Room roomToRemove = managementSystem.searchRoomByCode(code);
                managementSystem.removeRooms(roomToRemove);
                break;
            case 5:
                /**
                 *      Add Device
                 * isCritical
                 * maxPower consumption
                 * Is this device an Appliance or a Light?
                 * if light (adjustable or no)
                 * appliance noisy?
                 */
            case 6:
                System.out.println("Enter the device id to remove");
                int deviceId = scan.nextInt();
                scan.nextLine();
                Device deviceToRemove = managementSystem.searchDeviceById(deviceId);
                managementSystem.removeDevice(deviceToRemove);
                break;
            case 7:
                System.out.println(managementSystem.displaySummaryAllRooms());
                break;
            case 8:
                System.out.println("Enter the room code for details");
                System.out.println(managementSystem.displayDetailsOneRoom(scan.nextLine()));
                break;
            case 9:
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
            case 10:
                System.out.println("Enter room code to shutDown");
                Room targetRoom = managementSystem.searchRoomByCode(scan.nextLine());
                managementSystem.shutDownOneRoom(targetRoom);
                System.out.println("Shut down all devices in room " + targetRoom.getCode());
                break;
            case 11:
                managementSystem.shutDownAllDevices();
                System.out.println("All devices shut down");
                break;
            case 12:
                System.out.println(managementSystem.listStandByDayDevices());
                break;
            case 13:
                System.out.println(managementSystem.listStandByPowerDevices());
                break;
            case 14:
                System.out.println("enter room code and device id respectively to turn on");
                String roomC2 = scan.nextLine();
                int devId2 = scan.nextInt();
                scan.nextLine();
                managementSystem.turnOnDevice(roomC2, devId2);
                break;
            case 15:
                System.out.println("enter room code and device id respectively to turn off");
                String roomC3 = scan.nextLine();
                int devId3 = scan.nextInt();
                scan.nextLine();
                managementSystem.turnOffDevice(roomC3, devId3);
                break;
            case 16:
                managementSystem.turnOnAllLightsInHouse();
                break;
            case 17:
                System.out.println("enter room code to search for it");
                String roomC4 = scan.nextLine();
                System.out.println(managementSystem.searchRoomByCode(roomC4));
                break;
            case 18:
                System.out.println("enter device id to search for it");
                int devId4 = scan.nextInt();
                scan.nextLine();
                System.out.println(managementSystem.searchDeviceById(devId4));
                break;
            case 19:
                System.out.println(managementSystem.displayInfo());
                break;
            case 0:
                role = 0;
                break;
            default:
                System.out.println("Invalid action");
        }

    }

}