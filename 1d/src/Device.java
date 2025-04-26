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
        if (id>=100 && id <= 999) this.id = id;
        else this.id = 0;
    }
    //check that ids are not identical

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
        if(maxPowerConsumption >= 0) this.maxPowerConsumption = maxPowerConsumption;
        else this.maxPowerConsumption = 50;
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
        //add double confirmation somehow
        setStatus(OFF);
    }

    public abstract double getCurrentConsumption();
    //return power if ON and 0 if OFF

    public boolean equals(Device d){
        return id == d.getId();
    }

    public String toString(){
        return "id = " + id + ", name = " + name + ", status: "
                + ((status == OFF) ? "Off" : (status == ON) ? "On" : "Standby")
                + ", maximum power consumption = " + maxPowerConsumption + ", "
                + ((critical) ? "critical" : "not critical");
    }


}