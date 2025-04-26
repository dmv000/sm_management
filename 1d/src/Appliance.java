public class Appliance extends Device{
    private int[] powerLevels;
    int currentLevel; //def = 0
    boolean noisy;

    public Appliance(int id, String name, double  maxPowerConsumption, int[] powerLevels, boolean noisy){
        this(id, name, maxPowerConsumption, false, powerLevels, noisy);
    }

    public Appliance(int id, String name, double maxPowerConsumption, boolean critical, int[] powerLevels, boolean noisy){
        super(id, name, maxPowerConsumption, critical);
        setPowerLevels(powerLevels);
        setNoisy(noisy);
    }

    public int[] getPowerLevels() {
        return powerLevels;
    }
    public void setPowerLevels(int[] powerLevels) {
        this.powerLevels = powerLevels;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
    public void setCurrentLevel(int currentLevel) {
        if(currentLevel >= 0 && currentLevel < powerLevels.length) this.currentLevel = currentLevel;
        else currentLevel = 0;
    }


    public boolean isNoisy() {
        return noisy;
    }

    public void setNoisy(boolean noisy) {
        this.noisy = noisy;
    }

    //check working again
    public void turnOn() {
        //power level 0?????
        setStatus(ON);
        currentLevel = 0;
    }

    public void turnOn(int level){
        setStatus(ON);
        setCurrentLevel(level);
    }

    public double getCurrentConsumption() {
        return (getStatus() == ON) ? (powerLevels[currentLevel]/100.0) * getMaxPowerConsumption() : 0;

    }

    public String toString(){
        String s = "Appliance{ " + super.toString() + ", power levels = {[";
        for(int i = 0; i < powerLevels.length; i++)
            s += (powerLevels[i] < (powerLevels[powerLevels.length - 1]))? powerLevels[i]+ ", ":powerLevels[i];
        s += "]}, level = " + currentLevel + ", "
                + ((noisy) ? "noisy" : "not noisy");
        return s;
    }
}
