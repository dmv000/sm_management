public class DeviceTest {
    public static void main(String[] args) {
        System.out.println("HI");
        int []powerlvl = {50,100};
        Appliance A1 = new Appliance(100, "Ac", 750, true,powerlvl, false);
        A1.turnOn(1);

        Light L1 = new Light(150, "Light", 50, false, true);
        L1.turnOn(50);
        Room r1= new Room("L1", "Living Room");


        System.out.println("yesyes");
        ManagementSystem M1 = new ManagementSystem("123456Ab","123456Ba");
        M1.addRoom(r1);
        M1.addDevice(A1,r1);
        //M1.addDevice(L1,r1);
        System.out.println(M1.addDevice(L1,r1));
        M1.removeDevice(L1);
        M1.removeRooms(r1);
        System.out.println(M1.displayDetailsOneRoom("L1"));
    }
}
