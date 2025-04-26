import java.util.ArrayList;

public class Room {
    String code;
    String description;
    ArrayList<Device> devicesList;

    public Room(String code, String description){
        setCode(code);
        setDescription(description);
        devicesList = new ArrayList<Device>();
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Device> getDevicesList() {
        return devicesList;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDevicesList(ArrayList<Device> devicesList) {
        this.devicesList = devicesList;
    }

    public int getNbLights(){
        int count = 0;
        for(int i = 0; i < devicesList.size(); i++){
            if(devicesList.get(i) instanceof Light) count++;
        }
        return count;
    }

    public int getNbAppliances(){
        int count = 0;
        for(int i = 0; i < devicesList.size(); i++){
            if(devicesList.get(i) instanceof Appliance) count++;
        }
        return count;
    }

    public double getCurrentConsumption(){
        double count = 0;
        for(int i = 0; i < devicesList.size(); i++){
            count += devicesList.get(i).getCurrentConsumption();
        }
        return count;
    }

    public void addDevice(Device d){
        devicesList.add(d);
    }

    public void removeDevice(Device d){
        devicesList.remove(d);
    }

    public Device searchDeviceById(int id){
        for(int i = 0; i < devicesList.size(); i++){
            if(devicesList.get(i).getId() == id)
                return devicesList.get(i);
        }
        return null;
    }

    public String toString(){
        String s = getCode() +"/" +getDescription() +":\n"
                +"Devices: \n";
        for(int i = 0; i < devicesList.size(); i++){
            s += devicesList.get(i).toString() + "\n";
        }
        return s;
    }

    public String toBreifString(){
        int lights = getNbLights();
        int appliances = getNbAppliances();
        return "Total: " + (lights + appliances)
                +", #ofLights: " + lights
                +", #ofAppliances: " + appliances;
    }
}
