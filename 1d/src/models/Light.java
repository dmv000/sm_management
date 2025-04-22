package models;

public class Light extends Device{
    private boolean adjustable;
    int level; //between 0 and 100

    public Light(int id, String name, double maxPowerConsumption){
        this(id, name, maxPowerConsumption, false, false);
    }

    public Light(int id, String name, double maxPowerConsumption, boolean critical){
        this(id, name, maxPowerConsumption, critical, false);
    }

    public Light(int id, String name, double maxPowerConsumption, boolean critical, boolean adjustable){
        setId(id);
        setName(name);
        setMaxPowerConsumption(maxPowerConsumption);
        setCritical(critical);
        setAdjustable(adjustable);
    }

    public boolean isAdjustable() {
        return adjustable;
    }

    public int getLevel() {
        return level;
    }

    public void setAdjustable(boolean adjustable) {
        this.adjustable = adjustable;
    }

    public void setLevel(int level){
        if(adjustable) {
            if(level >= 0 && level <= 100) this.level = level;
        }
    }

    //nonadjustable lights cannot be turned on or off and
    //constantly stay at the same level
    //may add feature for the turn off function to save previous level
    //so that it the methods can be used
    //to be determined later

    public void turnOn(){
        if(adjustable) level = 100;
    }

    public void turnOn(int level){
        if(adjustable) setLevel(level);
    }

    //abstract --> had to add even though not required
    public void turnOff(){
        if(adjustable) level = 0;
    }

    public double getCurrentConsumption(){
        return level * getMaxPowerConsumption();
    }

    public String toString(){
        return "Light{ " + super.toString() + ", "
                +((adjustable) ? "adjustable" : "not adjustable")
                +", level = " + level + "}";
    }
}