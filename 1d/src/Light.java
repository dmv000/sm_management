public class Light extends Device{
    private boolean adjustable;
    int level; //between 0 and 100 def 100

    public Light(int id, String name, double maxPowerConsumption, boolean adjustableInput, int[] powerLevels, boolean criticalInput){
        this(id, name, maxPowerConsumption, false, false);
    }
    public Light(int id, String name, double maxPowerConsumption, boolean critical){
        this(id, name, maxPowerConsumption, critical, false);
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
        }
    }

    //nonadjustable lights cannot be turned on or off and

    // -----this might need to have sort of an @override or @overload method----
    //----for the set level of the adjustable light, non_adjustable lights, and the default one----

    //constantly stay at the same level
    //may add feature for the turn off function to save previous level
    //so that it the methods can be used
    //to be determined later

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

    public String toString(){
        return "Light{" + super.toString() + ", "
                +((adjustable) ? "adjustable" : "not adjustable")
                +", level = " + level + "}";
    }
}