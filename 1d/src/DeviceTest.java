public class DeviceTest {
    public static void main(String[] args) {
        System.out.println("HI");
        int []powerlvl = {50,100};
        Appliance A1 = new Appliance(100, "Ac", 750, true,powerlvl, false);
        A1.turnOn(1);
        System.out.println(A1.getCurrentConsumption());
        System.out.println(A1);
    }
}
