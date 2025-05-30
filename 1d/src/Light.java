public class Light extends Device{
    private boolean adjustable;
    int level; //between 0 and 100 def 100

    public Light(int id, String name, double maxPowerConsumption){
        this(id, name, maxPowerConsumption, false, false);
    }
    public Light(int id, String name, double maxPowerConsumption, boolean adjustable){
        this(id, name, maxPowerConsumption, false, adjustable);
    }
    public Light(int id, String name, double maxPowerConsumption, boolean critical, boolean adjustable){
        super(id, name, maxPowerConsumption, critical);
        setAdjustable(adjustable);
        setLevel(100);

    }

    public void setAdjustable(boolean adjustable) {
        this.adjustable = adjustable;
    }
    public boolean isAdjustable() {
        return adjustable;
    }

    public int getLevel() {
        return level;
    }
    public void setLevel(int level){
        if(adjustable) {
            if(level >= 0 && level <= 100) this.level = level;
            else this.level = 100;
        } else {
            this.level = 100;
        }
    }

    public void turnOn(){
        setStatus(ON);
        if(adjustable) level = 100;
    }
    //this has no usage?
    public void turnOn(int level){
        setStatus(ON);
        if(adjustable) setLevel(level);
    }

    public double getCurrentConsumption() {
        return (getStatus() == ON) ? (level/100.0) * getMaxPowerConsumption() : 0;
    }

    public double getConsumptionIfOn() {
        return (level/100.0) * getMaxPowerConsumption();
    }

    public String toString(){
        return "Light{" + super.toString() + ", "
                +((adjustable) ? "adjustable" : "not adjustable")
                +", level = " + level + "}";
    }
}