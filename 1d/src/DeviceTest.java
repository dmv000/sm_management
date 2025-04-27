import java.util.Scanner;
public class DeviceTest {
    private static ManagementSystem managementSystem;
    //0 = not logged in; 1 = logged in; 2 = admin log in
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

    private static void loginMenu(Scanner scan){
        System.out.println("Welcome to your House Management System: ");
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
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
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
        System.out.print("Select: ");
        int action = scan.nextInt();
        scan.nextLine(); //also to clear the buffer
        switch(action){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 0:
                role = 0;
                break;
            default:
                System.out.println("Invalid action");
        }
    }

}