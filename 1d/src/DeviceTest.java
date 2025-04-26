public class DeviceTest {
    public static void main(String[] args) {
        System.out.println("HI");
        int []powerlvl = {50,100};
        Appliance A1 = new Appliance(100, "Ac", 750, true,powerlvl, false);
        A1.turnOn(1);
        System.out.println(A1.getCurrentConsumption());
        System.out.println(A1);
        //A1.turnOff();
        System.out.println(A1);

        Light L1 = new Light(150, "Light", 50, false, true);
        L1.turnOn(50);
        System.out.println(L1.getCurrentConsumption());
        System.out.println(L1);
        //L1.turnOff();
        System.out.println(L1);

        Room r1= new Room("L1", "Living Room");
        r1.addDevice(L1);
        r1.addDevice(A1);
        r1.removeDevice(A1);
        System.out.println(r1);
        System.out.println(r1.getCurrentConsumption());

        System.out.println(r1.searchDeviceById(100));
        ManagementSystem M1 = new ManagementSystem("123456Ab","123456Ba");
        M1.addRoom(r1);
        System.out.println(M1.displayInfo());
        System.out.println(M1.displayDetailsOneRoom("L1"));
    }
}
