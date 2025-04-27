import java.util.Scanner;
public class DeviceTest {
    private static ManagementSystem managementSystem;
    //0 = not logged in; 1 = logged in; 2 = admin log in
    private static int role = 0;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

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




    }
}