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
                "\n===== USER MENU =====\n" +
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
            case ?;
        }
    }

}