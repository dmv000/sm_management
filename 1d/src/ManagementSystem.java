import java.lang.reflect.Array;
import java.util.ArrayList;
import java.lang.StringBuilder;

public class ManagementSystem {
    private String adminPassword; //size >= 8 //1 upper case,1 lower, 1 digit at least
    private String userPassword; //same as admin
    private ArrayList<Room> rooms;
    private double maxAllowedPower;
    //3 power modes
    public final static int LOW = 1000;
    public final static int NORMAL = 4000;
    public final static int HIGH = 10000;

    private boolean day; //T=Day, F=Night
    private ArrayList<Device> waitingListDay; //standby until day // for noisy
    private ArrayList<Device> waitingListPower; //standby until power allows it

    public ManagementSystem(String adminPassword, String userPassword){
        setAdminPassword(adminPassword);
        setUserPassword(userPassword);
        setMaxAllowedPower(LOW);
        setDayTime();
    }

    private void setAdminPassword(String adminPassword) {
        if(passwordIsValid(adminPassword)) this.adminPassword = adminPassword;
    }

    private void setUserPassword(String userPassword) {
        if(passwordIsValid(userPassword)) this.userPassword = userPassword;
    }

    private boolean passwordIsValid(String s){
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

    public void addRoom(Room r){
        rooms.add(r);
    }

    public boolean addDevice(Device d, Room r){
        if(rooms.contains(r)) return false;
        rooms.get(rooms.indexOf(r)).addDevice(d);
        return true;
    }

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
    //when set, ask user if he wants to turn on all lights in the house or not

    public void setNightTime(){
        day = false;
    }
    //when set night --> check for running noisy devices --> off / standy by in waitlist / kept on (ask user)

    public boolean turnOnDevice(String roomCode, int deviceId){
        for(int i = 0; i < searchRoomByCode(roomCode).getDevicesList().size(); i++){
            if(searchRoomByCode(roomCode).getDevicesList().get(i).getId() == deviceId){
                searchRoomByCode(roomCode).getDevicesList().get(i).turnOn();
                return true;
            }
        }
        return false;
    }
    //power constraint + !noisy || day --> on
    //if noisy && night --> warning >> can turn on anyway / standby+waiting list / cancel (ask user)
    //if turn on and power > systemPowerMode --> waitlist / cancel (ask user)

    public boolean turnOffDevice(String roomCode, int deviceId){
        for(int i = 0; i < searchRoomByCode(roomCode).getDevicesList().size(); i++){
            if(searchRoomByCode(roomCode).getDevicesList().get(i).getId() == deviceId){
                searchRoomByCode(roomCode).getDevicesList().get(i).turnOff();
                tryToTurnOnDevicesPower();
                return true;
            }
        }
        return false;
    }
    //if device is critical --> double confirm by entering admin password

    public void shutDownOneRoom(Room r){
        for(int i = 0; i < r.getDevicesList().size(); i++){
            turnOffDevice(r.getCode() ,r.getDevicesList().get(i).getId());
        }
    }

    public void shutDownAllDevices(){
        for(int i = 0; i < rooms.size(); i++){
            for(int j = 0; j < rooms.get(i).getDevicesList().size(); j++){
                turnOffDevice(rooms.get(i).getCode(), rooms.get(i).getDevicesList().get(i).getId());
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
        return "";
    }

    //MYCODE

    //when day is set, check the waitlist and turn on the devices if possible, if not, send it to powerWaitList
    private void tryToTurnOnDevicesDay(){
        if(waitingListDay.size() != 0){
            for(int i = 0; i < waitingListDay.size(); i++){
                if(waitingListDay.get(i).getCurrentConsumption() + getTotalPowerConsumption() < maxAllowedPower){
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
    private double getTotalPowerConsumption(){
        double count = 0;
        for(int i = 0; i < rooms.size(); i++){
            count += rooms.get(i).getCurrentConsumption();
        }
        return count;
    }

    //check if the password matches any mode
    public int CheckAccess(String s){
        if(s.equals(userPassword)) return 1;
        if(s.equals(adminPassword)) return 2;
        else return 0;
        //set mode?? boolean\
        //when exit admin mode / user mode --> display main menu;
    }

    //when a device is turned off, check if a new device(s) can be turned on
    private void tryToTurnOnDevicesPower(){
        if(waitingListPower.size() != 0){
            for(int i = 0; i < waitingListPower.size(); i++) {
                if (waitingListPower.get(i).getCurrentConsumption() + getTotalPowerConsumption() < maxAllowedPower) {
                    waitingListPower.get(i).turnOn();
                    waitingListPower.remove(waitingListPower.get(i));
                }
            }
        }
    }


}
