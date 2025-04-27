public class DeviceTest {
    public static void main(String[] args) {
        System.out.println("HI");
        int []powerlvl = {50,100};
        Appliance A1 = new Appliance(100, "Ac", 750, true,powerlvl, false);
        Appliance A2 = new Appliance(150, "Ac", 700, true,powerlvl, true);
        Appliance A3 = new Appliance(120, "Fan", 100, true,powerlvl, true);


        A1.turnOn(1);

        Light L1 = new Light(150, "Light", 50, false, true);
        Light L2 = new Light(200,"light",100,false,false);
        L1.turnOn(50);
        Room r1= new Room("L1", "Living Room");
        Room r2 = new Room("K1","Kitchen");


        System.out.println("yesyes");
        ManagementSystem M1 = new ManagementSystem("123456Ab","123456Ba");
        M1.addRoom(r1);
        M1.addDevice(A1,r1);
        //M1.addDevice(L1,r1);
        System.out.println(M1.addDevice(L1,r1));
        M1.addRoom(r2);
        M1.addDevice(L1, r2);
        M1.addDevice(A2,r2);
        M1.addDevice(A3,r1);
        M1.addDevice(L2,r1);
        M1.turnOffDevice(A1);
        M1.turnOnDevice("L1",100);
        M1.shutDownAllDevices();
        System.out.println(M1.checkTurnOnDevice(A1));
        M1.turnOnAllLightsInHouse();
        M1.addDeviceToWaitingListDay(L1);
        M1.addDeviceToWaitingListDay(L2);
        M1.addDeviceToWaitingListPower(L1);
        M1.addDeviceToWaitingListPower(A1);
        M1.removeDeviceFromWaitingListDay(L1);
        M1.removeDeviceFromWaitingListPower(L1);
        System.out.println("yyyyyyy");
        System.out.println(M1.listStandByDayDevices());
        System.out.println(M1.listStandByPowerDevices());
        M1.setNoisyDeviceStatus(1);
        M1.turnOnDevice("L1",100);

        A1.turnOn(1);
        System.out.println(M1.displaySummaryAllRooms());

    }
}
