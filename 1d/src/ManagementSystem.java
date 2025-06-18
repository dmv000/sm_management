import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

public class ManagementSystem {
    private String adminPassword; //size >= 8 //1 upper case,1 lower, 1 digit at least
    private String userPassword; //same as admin
    private Map<String, Room> rooms;
    private Map<Integer, Device> allDevices;
    private double maxAllowedPower; //?
    //3 power modes
    public final static int LOW = 1000;
    public final static int NORMAL = 4000;
    public final static int HIGH = 10000;
    //3 access Levels
    public final static int NOACCESS = 0;
    public final static int USER = 1;
    public final static int ADMIN = 2;

    private boolean day; //T=Day, F=Night
    private ArrayList<Device> waitingListDay; //standby until day // for noisy
    private ArrayList<Device> waitingListPower; //standby until power allows it

    public ManagementSystem(String adminPassword, String userPassword){
        rooms = new HashMap<>();
        allDevices = new HashMap<>();
        waitingListDay = new ArrayList<Device>();
        waitingListPower = new ArrayList<Device>();
        setAdminPassword(adminPassword);
        setUserPassword(userPassword);
        setMaxAllowedPower(LOW);
        setDayTime();
    }

    public void setAdminPassword(String adminPassword) {
        if (!passwordIsValid(adminPassword)) {
            throw new IllegalArgumentException("Invalid admin password format.");
        }
        this.adminPassword = adminPassword;
    }

    public void setUserPassword(String userPassword) {
        if (!passwordIsValid(userPassword)) {
            throw new IllegalArgumentException("Invalid user password format.");
        }
        this.userPassword = userPassword;
    }

    public static boolean passwordIsValid(String s){
        boolean isUpper = false, isLower = false, isDigit = false;
        if(s.length() < 8) return false;

        for(int i = 0; i < s.length(); i++){
            if(Character.isUpperCase(s.charAt(i))) isUpper = true;
            if(Character.isLowerCase(s.charAt(i))) isLower = true;
            if(Character.isDigit(s.charAt(i))) isDigit = true;
        }
        return isUpper && isLower && isDigit;
    }

    public void changeAdminPassword(String adminPassword){
        if(passwordIsValid(adminPassword)) setAdminPassword(adminPassword);
    }

    public void changeUserPassword(String userPassword){
        if(passwordIsValid(userPassword)) setUserPassword(userPassword);
    }

    public String displaySummaryAllRooms(){
        StringBuilder sb = new StringBuilder();
        for(Room room : rooms.values()){
            sb.append(room.toString()); // Assuming Room has a good toString()
            sb.append("\n");
        }
        return sb.toString();
    }

    //search by code
    public String displayDetailsOneRoom(String code){
        Room room = rooms.get(code);
        return (room != null) ? room.toString() : null;
    }

    public boolean addRoom(Room r){
        //check if the added room has the same id to prevent code duplicates
        if(rooms.containsKey(r.getCode())) return false;
        rooms.put(r.getCode(), r);
        return true;
    }
    //returns false if another room has the same code;

    public void addDevice(Device d, Room r){
        // The Room r parameter is assumed to be a valid, managed room object.
        // The caller is responsible for fetching it using searchRoomByCode first.
        if (r == null) { // Should ideally not happen if caller fetches room correctly
            throw new IllegalArgumentException("Room cannot be null.");
        }
        if (!rooms.containsKey(r.getCode()) || rooms.get(r.getCode()) != r) {
             // Defensive check: Ensure 'r' is the one from our map, or a room with its code exists.
            throw new RoomNotFoundException("Room with code " + r.getCode() + " not found or is not the managed instance.");
        }

        // Check if another device has the same id
        if(allDevices.containsKey(d.getId())) {
            throw new DuplicateDeviceIdException("Device with ID " + d.getId() + " already exists.");
        }

        // If checks pass, add the device to the room's internal list and the global map
        r.addDevice(d);
        allDevices.put(d.getId(), d);
    }

    public boolean removeRooms(Room r){
        // This method can be deprecated or updated.
        // For now, let's assume it might be kept for compatibility or specific use cases.
        // If it's to be removed, DashboardTester would need to change.
        // If kept, it should align with the new Map structure.
        if (r == null || !rooms.containsKey(r.getCode())) return false;
        return removeRoomByCode(r.getCode());
    }

    public boolean removeRoomByCode(String roomCode){
        Room roomToRemove = rooms.get(roomCode);
        if(roomToRemove == null) return false;

        // Remove all devices in this room from the allDevices map
        for(Device device : roomToRemove.getDevicesList()){
            allDevices.remove(device.getId());
        }
        rooms.remove(roomCode);
        return true;
    }

    public boolean removeDevice(Device d){
        // This method can be updated to call removeDeviceById
        if (d == null) return false;
        return removeDeviceById(d.getId());
    }

    public boolean removeDeviceById(int deviceId){
        Device deviceToRemove = allDevices.get(deviceId);
        if(deviceToRemove == null) return false;

        // Find the room containing this device and remove it from the room's list
        for(Room room : rooms.values()){
            if(room.getDevicesList().contains(deviceToRemove)){
                room.removeDevice(deviceToRemove); // Assumes Room class has removeDevice(Device d)
                break; // Device should only be in one room
            }
        }
        allDevices.remove(deviceId);
        return true;
    }

    public void setDayTime(){
        day = true;
        tryToTurnOnDevicesDay();
        // System.out.println calls removed as per instructions
    }

    public void setNightTime(){
        day = false;
        // Ensure all System.out.println calls are handled by DashboardTester
    }

    public boolean turnOnDevice(String roomCode, int deviceId){
        //check if roomCode is valid
        Room r = searchRoomByCode(roomCode);
        if(r == null) return false;
        //check room if the device is present
        for(int i = 0; i < r.getDevicesList().size(); i++){
            if(r.getDevicesList().get(i).getId() == deviceId){
                r.getDevicesList().get(i).turnOn();
                removeDeviceFromWaitingListPower(r.getDevicesList().get(i));
                removeDeviceFromWaitingListDay(r.getDevicesList().get(i));
                return true;
            }
        }
        return false;
    }

    public boolean turnOnDevice(String roomCode, int deviceId, int level){
        //check if roomCode is valid
        Room r = searchRoomByCode(roomCode);
        if(r == null) return false;
        //check room if the device is present
        for(int i = 0; i < r.getDevicesList().size(); i++){
            if(r.getDevicesList().get(i).getId() == deviceId){
                if(r.getDevicesList().get(i) instanceof Appliance){
                    ((Appliance)r.getDevicesList().get(i)).turnOn(level);
                } else {
                    ((Light)r.getDevicesList().get(i)).turnOn(level);
                }
                removeDeviceFromWaitingListPower(r.getDevicesList().get(i));
                removeDeviceFromWaitingListDay(r.getDevicesList().get(i));
                return true;
            }
        }
        return false;
    }
    //returns false is device is not found in the room or room is invalid
    public int checkTurnOnDevice(Device d){
        if(d.getConsumptionIfOn() + getTotalPowerConsumption() > maxAllowedPower) return 2;
        if(d instanceof Appliance){
            if((((Appliance) d).isNoisy()) && !day) return 1;
        }
        return 0;
    }
    public boolean turnOffDevice(String roomCode, int deviceId){
        Room room = searchRoomByCode(roomCode);
        //check if roomCode is valid
        if(room == null) return false;
        //check room if the device is present
        for(int i = 0; i < room.getDevicesList().size(); i++){
            if(room.getDevicesList().get(i).getId() == deviceId){
                turnOffDevice(room.getDevicesList().get(i));
                return true;
            }
        }
        return false;
    }
    //returns false is device is not found in the room or room is invalid

    public void turnOffDevice(Device d){
        d.turnOff();
        removeDeviceFromWaitingListDay(d);
        removeDeviceFromWaitingListPower(d);
        tryToTurnOnDevicesPower();
    }

    public void shutDownOneRoom(Room r){
        for(int i = 0; i < r.getDevicesList().size(); i++){
            turnOffDevice(r.getCode() ,r.getDevicesList().get(i).getId());
        }
    }

    public void shutDownAllDevices(){
        for(Room room : rooms.values()){
            for(Device device : room.getDevicesList()){
                turnOffDevice(device); // Use the version that takes a Device object
            }
        }
    }

    public Room searchRoomByCode(String code){
        return rooms.get(code);
    }

    public Device searchDeviceById(int id){
        return allDevices.get(id);
    }

    public String displayInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("Time = " + (day ? "day" : "night") + "\n");
        sb.append("Max allowed power = " + maxAllowedPower + "\n");
        sb.append("Current power consumption = " + getTotalPowerConsumption() +"\n");
        for(Room room : rooms.values()){
            sb.append("Room code " + room.getCode() + ":\n");
            for(Device d : room.getDevicesList()){
                sb.append(d.toString() +"\n");
            }
        }
        return sb.toString();
    }


    //when day is set, check the waitlist and turn on the devices if possible, if not, send it to powerWaitList
    private void tryToTurnOnDevicesDay(){
        java.util.Iterator<Device> iterator = waitingListDay.iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (device.getConsumptionIfOn() + getTotalPowerConsumption() <= maxAllowedPower) {
                device.turnOn(); // Assumes turnOn also sets status correctly
                iterator.remove(); // Remove from waitingListDay
            } else {
                waitingListPower.add(device); // Add to power waiting list
                iterator.remove(); // Remove from waitingListDay
            }
        }
        // Return type changed to void, System.out.println calls removed
    }

    //normal setter
    public void setMaxAllowedPower(double power) {
        if(power == LOW || power == NORMAL || power == HIGH)
            this.maxAllowedPower = power;
        else
            throw new IllegalArgumentException("Invalid power mode specified. Must be LOW, NORMAL, or HIGH.");
    }

    //calculate total consumption between all rooms
    public double getTotalPowerConsumption(){
        double count = 0;
        for(Room room : rooms.values()){
            count += room.getCurrentConsumption();
        }
        return count;
    }

    //check if the password matches any mode
    public int checkAccess(String s){
        if(s.equals(userPassword)) return USER;
        if(s.equals(adminPassword)) return ADMIN;
        else return NOACCESS;
    }

    private void tryToTurnOnDevicesPower(){
        java.util.Iterator<Device> iterator = waitingListPower.iterator();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            if (device.getConsumptionIfOn() + getTotalPowerConsumption() <= maxAllowedPower) {
                device.turnOn(); // Assumes turnOn also sets status correctly
                iterator.remove(); // Remove from waitingListPower
            }
            // If not enough power, it just stays in waitingListPower for the next attempt.
        }
    }

    public void turnOffAllLightsInHouse(){
        for(Room room : rooms.values()){
            for(Device device : room.getDevicesList()){
                if(device instanceof Light)
                    device.turnOff();
            }
        }
    }

    public boolean checkForRunningNoisyDevices(){
        for(Room room : rooms.values()){
            for(Device device : room.getDevicesList()){
                if(device instanceof Appliance){
                    if (((Appliance)device).isNoisy() && device.getStatus() == Device.ON)
                        return true;
                }
            }
        }
        return false;
    }

    public String displayAllRunningDevices() {
        StringBuilder sb = new StringBuilder();
        boolean empty = true;
        for (Room room : rooms.values()) {
            for (Device device : room.getDevicesList()) {
                if (device.getStatus() == Device.ON) {
                    empty = false;
                    sb.append(device.toString() + "\n");
                }
            }
        }
        return (!empty) ? sb.toString() : "No devices On";
    }

    //set the newStatus for all noisy devices only
    public void setNoisyDeviceStatus(int newStatus){
        for(Room room : rooms.values()){
            for(Device device : room.getDevicesList()){
                if(device instanceof Appliance && ((Appliance) device).isNoisy()){
                    device.setStatus(newStatus);
                    if(newStatus == Device.STANDBY){
                        waitingListDay.add(device);
                    }
                }
            }
        }
    }

    //standby methods (add, remove, display)
    public void addDeviceToWaitingListDay(Device d){
        d.setStatus(Device.STANDBY);
        waitingListDay.add(d);
    }

    public void addNoisyDevicesToWaitingListDay(){
        for(Room room : rooms.values()){
            for(Device device : room.getDevicesList()){
                if(device instanceof Appliance){
                    if(((Appliance) device).isNoisy()){
                        addDeviceToWaitingListDay(device);
                    }
                }
            }
        }
    }


    private void removeDeviceFromWaitingListDay(Device d){
        if(waitingListDay.contains(d)) waitingListDay.remove(d);
    }

    public String listStandByDayDevices(){
        StringBuilder sb = new StringBuilder();
        boolean empty = true;
        for(Device i : waitingListDay){
            empty = false;
            sb.append(i.getId() + "\n");
        }
        return (!empty) ? sb.toString() : "No devices on standby";
    }

    public void addDeviceToWaitingListPower(Device d){
        d.setStatus(Device.STANDBY);
        waitingListPower.add(d);
    }

    private void removeDeviceFromWaitingListPower(Device d){
        if(waitingListPower.contains(d)) waitingListPower.remove(d);
    }

    public String listStandByPowerDevices(){
        StringBuilder sb = new StringBuilder();
        boolean empty = true;
        for (Device i : waitingListPower){
            empty = false;
            sb.append(i.getId() + "\n");
        }
        return (!empty) ? sb.toString() : "No devices on standby";
    }

    //critical(setStatus/set)

    public void setAllCriticalDeviceStatus(int newStatus){
        for(Room room : rooms.values()){
            for(Device device : room.getDevicesList()){
                if(device.isCritical()){
                    device.setStatus(newStatus);
                }
            }
        }
    }

    public void setRoomCriticalDeviceStatus(Room r, int newStatus){
        // Assuming r is a valid room object obtained from the rooms map
        if (r != null && rooms.containsKey(r.getCode())) {
            for(Device device : r.getDevicesList()){ // Iterate through devices in the passed room 'r'
                if(device.isCritical()){
                    device.setStatus(newStatus);
                }
            }
        }
    }

    public boolean checkAllRoomsForCriticalDevice(){
        for(Room room : rooms.values()){
            for(Device device : room.getDevicesList()){
                if(device.isCritical()){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkRoomForCriticalDevice(Room r){
        for(int i = 0; i < r.getDevicesList().size(); i++){
            if(r.getDevicesList().get(i).isCritical()){
                return true;
            }
        }
        return false;
    }

    public String searchRoomByDevice(Device otherDevice){
        if (otherDevice == null) return null;
        for(Room room : rooms.values()){
            if(room.getDevicesList().contains(otherDevice)){
                return room.getCode();
            }
        }
        return null;
    }

    public boolean anyLightIsOn(){
        for(Room room : rooms.values()){
            for(Device d : room.getDevicesList()){
                if(d instanceof Light){
                    if(d.getStatus() == Device.ON) return true;
                }
            }
        }
        return false;
    }

    public Collection<Room> getRooms() {
        return rooms.values();
    }
}
