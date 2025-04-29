import java.util.ArrayList;
import java.lang.StringBuilder;

public class ManagementSystem {
    private String adminPassword; //size >= 8 //1 upper case,1 lower, 1 digit at least
    private String userPassword; //same as admin
    private ArrayList<Room> rooms;
    protected double maxAllowedPower; //?
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
        rooms = new ArrayList<Room>();
        waitingListDay = new ArrayList<Device>();
        waitingListPower = new ArrayList<Device>();
        setAdminPassword(adminPassword);
        setUserPassword(userPassword);
        setMaxAllowedPower(LOW);
        setDayTime();
    }

    public void setAdminPassword(String adminPassword) {
        if(passwordIsValid(adminPassword)) this.adminPassword = adminPassword;
    }

    public void setUserPassword(String userPassword) {
        if(passwordIsValid(userPassword)) this.userPassword = userPassword;
    }
//this should be public and static i guess to work?
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
        for(int i = 0; i < rooms.size(); i++){
            sb.append(rooms.get(i));
            sb.append("\n");
        }
        return sb.toString();
    }

    //search by code
    public String displayDetailsOneRoom(String code){
        for(int i = 0; i < rooms.size(); i++){
            if(rooms.get(i).getCode().equals(code)) return rooms.get(i).toString();
        }
        return null;
    }

    public boolean addRoom(Room r){
        //check if the added room has the same id to prevent code duplicates
        for(Room room : rooms){
            if(r.getCode().equals(r.getCode())) return false;
        }
        rooms.add(r);
        return true;
    }
    //returns false if another room has the same code;

    public int addDevice(Device d, Room r){
        //check if anoother device has the same id in this room or another >> to prevent id duplicates
        //(other methods deal with devices using an id basis)
        for(Room room : rooms){
            for(Device device :room.getDevicesList()){
                if(device.equals(d)) return 2;
            }
        }
        //check if room is present
        for(Room room : rooms){
            if(room.equals(r)){
                room.addDevice(d);
                return 0;
            }
        }
        return 1;
    }
    //return 0 if added correctly
    //return 1 if room is not present
    //return 2 if device id is already used

    public boolean removeRooms(Room r){
        if(!rooms.contains(r)) return false;
        else rooms.remove(r);
        return true;
    }
    //add remove room by code

    public boolean removeDevice(Device d){
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                if(rooms.get(i).getDevicesList().get(j).equals(d)){
                    rooms.get(i).getDevicesList().remove(j);
                    return true;
                }
            }
        }
        return false;
    }
    //add remove device by id; optional

    public void setDayTime(){
        day = true;
        tryToTurnOnDevicesDay();
    }
    //setDaytime is prompted, ask user if he wants to turn on all lights in the house or not
    //use method --turnOnAllLightsInHouse()

    public void setNightTime(){
        day = false;
    }
    //when set night --> checks for running noisy devices --> input user --> off / standy by in waitlist / kept on (ask user)
    //use methods --checkForRuningNoisyDevices() --setNoisyDeviceStatus()

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
    //returns false is device is not found in the room or room is invalid
    //use --checkTurnOnDevice() in main to check for the conditions:
    //if(checkTurnOnDevice(d)) turnOnDevice
    //use a switch for all other conditions

    public int checkTurnOnDevice(Device d){
        if(!(d.getCurrentConsumption() + getTotalPowerConsumption() >= maxAllowedPower)) return 2;
        if(d instanceof Appliance){
            if((((Appliance) d).isNoisy()) && !day) return 1;
        }
        return 0;
    }
    //0 >> no constraints
    //1 >> noisy and night>> can turn on anyway / standby+waiting list / cancel (ask user)
    //2 >> not enough power --> waitlist / cancel (ask user)

    public boolean turnOffDevice(String roomCode, int deviceId){
        //check if roomCode is valid
        if(searchRoomByCode(roomCode) == null) return false;
        //check room if the device is present
        for(int i = 0; i < searchRoomByCode(roomCode).getDevicesList().size(); i++){
            if(searchRoomByCode(roomCode).getDevicesList().get(i).getId() == deviceId){
                turnOffDevice(searchRoomByCode(roomCode).getDevicesList().get(i));
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
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                turnOffDevice(rooms.get(i).getCode(), rooms.get(i).getDevicesList().get(j).getId());
            }
        }
    }

    public Room searchRoomByCode(String code){
        for(int i = 0; i < rooms.size(); i++){
            if(rooms.get(i).getCode().equals(code)) return rooms.get(i);
        }
        return null;
    }

    public Device searchDeviceById(int id){
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                if(rooms.get(i).getDevicesList().get(j).getId() == id) return rooms.get(i).getDevicesList().get(j);
            }
        }
        return null;
    }

    public String displayInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("Time = " + day + "\n");
        sb.append("Max allowed power = " + maxAllowedPower + "\n");
        for(Room room : rooms){
            sb.append("Room code " + room.getCode() + ":\n");
            for(Device d : room.getDevicesList()){
                sb.append(d.toString() +"\n");
            }
        }
        return "";
    }

    //More code

    //when day is set, check the waitlist and turn on the devices if possible, if not, send it to powerWaitList
    private void tryToTurnOnDevicesDay(){
        if(waitingListDay.size() != 0){
            for(int i = 0; i < waitingListDay.size(); i++){
                if(waitingListDay.get(i).getCurrentConsumption() + getTotalPowerConsumption() <= maxAllowedPower){
                    waitingListDay.get(i).turnOn();
                    waitingListDay.remove(waitingListDay.get(i));
                } else {
                    waitingListPower.add(waitingListDay.get(i));
                    waitingListDay.remove(waitingListDay.get(i));
                }
            }
        }
    }

    //normal setter
    public void setMaxAllowedPower(double maxAllowedPower) {
        if(maxAllowedPower == LOW || maxAllowedPower == NORMAL || maxAllowedPower == HIGH)
            this.maxAllowedPower = maxAllowedPower;
    }

    //calculate total consumption between all rooms
    public double getTotalPowerConsumption(){
        double count = 0;
        for(int i = 0; i < rooms.size(); i++){
            count += rooms.get(i).getCurrentConsumption();
        }
        return count;
    }

    //check if the password matches any mode
    public int checkAccess(String s){
        if(s.equals(userPassword)) return USER;
        if(s.equals(adminPassword)) return ADMIN;
        else return NOACCESS;
        //set mode?? boolean\
        //when exit admin mode / user mode --> display main menu;
    }

    //when a device is turned off, check if a new device(s) can be turned on
    private void tryToTurnOnDevicesPower(){
        if(waitingListPower.size() != 0){
            for(int i = 0; i < waitingListPower.size(); i++) {
                if (waitingListPower.get(i).getCurrentConsumption() + getTotalPowerConsumption() <= maxAllowedPower) {
                    waitingListPower.get(i).turnOn();
                    waitingListPower.remove(waitingListPower.get(i));
                }
            }
        }
    }

    public void turnOnAllLightsInHouse(){
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                if(rooms.get(i).getDevicesList().get(j) instanceof Light)
                    rooms.get(i).getDevicesList().get(j).turnOn();
            }
        }
    }

    public boolean checkForRunningNoisyDevices(){
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                if(rooms.get(i).getDevicesList().get(j) instanceof Appliance){
                    if (((Appliance)rooms.get(i).getDevicesList().get(j)).isNoisy()
                    && ((Appliance)rooms.get(i).getDevicesList().get(j)).getStatus() == Device.ON)
                        return true;
                }
            }
        }
        return false;
    }

    public String displayAllRunningDevices() {
        StringBuilder sb = new StringBuilder();
        boolean empty = true;
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = 0; j < rooms.get(i).getDevicesList().size(); j++) {
                if (rooms.get(i).getDevicesList().get(j).getStatus() == Device.ON) {
                    empty = false;
                    sb.append(rooms.get(i).getDevicesList().get(j).toString());
                }
            }
        }
        return (!empty) ? sb.toString() : "No devices On";
    }

    //set the newStatus for all noisy devices only
    public void setNoisyDeviceStatus(int newStatus){
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                if(rooms.get(i).getDevicesList().get(j) instanceof Appliance
                        && ((Appliance) rooms.get(i).getDevicesList().get(j)).isNoisy()){
                    rooms.get(i).getDevicesList().get(j).setStatus(newStatus);
                    //add to waitlist if standby
                    if(newStatus == Device.STANDBY){
                        waitingListDay.add((rooms.get(i).getDevicesList().get(j)));
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
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                if(rooms.get(i).getDevicesList().get(j) instanceof Appliance){
                    if(((Appliance) rooms.get(i).getDevicesList().get(j)).isNoisy()){
                        addDeviceToWaitingListDay(rooms.get(i).getDevicesList().get(j));
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
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                if(rooms.get(i).getDevicesList().get(j).isCritical()){
                    rooms.get(i).getDevicesList().get(j).setStatus(newStatus);
                }
            }
        }
    }

    public void setRoomCriticalDeviceStatus(Room r, int newStatus){
        for(int i = 0; i < r.getDevicesList().size(); i++){
            if(r.getDevicesList().get(i).isCritical()){
                rooms.get(i).getDevicesList().get(i).setStatus(newStatus);
            }
        }
    }

    public boolean checkAllRoomsForCriticalDevice(){
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                if(rooms.get(i).getDevicesList().get(j).isCritical()){
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
}
