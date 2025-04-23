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
            sb.append(rooms.get(i) +"\n");
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
    //add remove device by id;

    public void setDayTime(){
        day = true;
    }
    //(day: remove everythign from dayWaitlist, turn on if possible, put rest in powerWaitList)
    //when set, ask user if he wants to turn on all lights in the house or not

    public void setNightTime(){
        day = false;
    }
    //when set night --> check for running noisy devices --> off / standy by in waitlist / kept on

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
    //if noisy && night --> warning >> can turn off anyway / standby+waiting list / cancel
    //if turn on and power > systemPowerMode --> waitlist / cancel

    public boolean turnOffDevice(String roomCode, int deviceId){
        for(int i = 0; i < searchRoomByCode(roomCode).getDevicesList().size(); i++){
            if(searchRoomByCode(roomCode).getDevicesList().get(i).getId() == deviceId){
                searchRoomByCode(roomCode).getDevicesList().get(i).turnOff();
                return true;
            }
        }
        return false;
    }
    //when turn off --> check for power standby device for first one that can be on without excceeding limit
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

    //add to standby power
    // '' ''' ' day
    //passwordIsAccepted --> admin, user, nothing (0 ,1, 2)
    //boolean mode
    //when exit admin mode / user mode --> display main menu;

}
