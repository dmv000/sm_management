public abstract class Device{
    private int id; //between 100 and 999 default 0--> incorrect setup
    private String name;
    private int status; //0 off //1 on // 2 standby
    private double maxPowerConsumption; //def = 50 //>0
    private boolean critical; // def = false
    //critical devices cannot be shutdown without double confirmation

    public final static int OFF = 0;
    public final static int ON = 1;
    public final static int STANDBY = 2;

    public Device(){
    }
    public Device(int id, String name, double maxPowerConsumption){

        this(id, name, maxPowerConsumption, false);
    }
    public Device(int id, String name, double maxPowerConsumption, boolean critical){
        setId(id);
        setName(name);
        setMaxPowerConsumption(maxPowerConsumption);
        setCritical(critical);
        //status = 0
    }

    public void setId(int id) {
        if (id < 100 || id > 999) {
            throw new IllegalArgumentException("Device ID must be between 100 and 999.");
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }


    public void setStatus(int status) {
        if(status >= OFF && status <= STANDBY) this.status = status;
        else this.status = OFF;
    }
    public int getStatus(){
        return status;
    }


    public void setMaxPowerConsumption(double maxPowerConsumption) {
        if (maxPowerConsumption < 0) {
            throw new IllegalArgumentException("Maximum power consumption cannot be negative.");
        }
        this.maxPowerConsumption = maxPowerConsumption;
    }
    public double getMaxPowerConsumption() {
        return maxPowerConsumption;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }
    public boolean isCritical() {
        return critical;
    }


    public abstract void turnOn();

    public void turnOff(){
        setStatus(OFF);
    }

    public abstract double getCurrentConsumption();
    //return power if ON and 0 if OFF

    public abstract double getConsumptionIfOn();
    //returns power consumption if the device is on

    public boolean equals(Device d){
        return id == d.getId();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id = ").append(id);
        sb.append(", name = ").append(name);
        sb.append(", status: ");
        switch (status) {
            case OFF: sb.append("Off"); break;
            case ON: sb.append("On"); break;
            case STANDBY: sb.append("Standby"); break;
            default: sb.append("Unknown"); break; // Should not happen
        }
        sb.append(", maximum power consumption = ").append(maxPowerConsumption);
        sb.append(", ").append(critical ? "critical" : "not critical");
        return sb.toString();
    }
}