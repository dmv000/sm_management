public class Appliance extends Device{
    private int[] powerLevels;
    private int currentLevel; 
    boolean noisy;

    public Appliance(int id, String name, double  maxPowerConsumption, int[] powerLevels, boolean noisy){
        this(id, name, maxPowerConsumption, false, powerLevels, noisy);
    }

    public Appliance(int id, String name, double maxPowerConsumption, boolean critical, int[] powerLevels, boolean noisy){
        super(id, name, maxPowerConsumption, critical);
        setPowerLevels(powerLevels); // Assuming this handles null/empty appropriately or throws.
        setNoisy(noisy);
        // currentLevel is initialized to 0 by default for int instance variables.
        // If powerLevels is empty/null, setCurrentLevel(0) might fail if called here.
        // It's better to set currentLevel when turned on or when a specific level is set.
    }

    public int[] getPowerLevels() {
        return powerLevels;
    }
    public void setPowerLevels(int[] powerLevels) {
        // For now, just assigning.
        this.powerLevels = powerLevels;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
    public void setCurrentLevel(int currentLevel) {
        if (this.powerLevels == null || this.powerLevels.length == 0) {
            throw new IllegalStateException("Power levels not defined for this appliance.");
        }
        if (currentLevel < 0 || currentLevel >= this.powerLevels.length) {
            throw new IllegalArgumentException("Invalid power level. Must be between 0 and " + (this.powerLevels.length - 1) + ".");
        }
        this.currentLevel = currentLevel;
    }


    public boolean isNoisy() {
        return noisy;
    }

    public void setNoisy(boolean noisy) {
        this.noisy = noisy;
    }

    public void turnOn() {
        setStatus(ON);
        if (this.powerLevels == null || this.powerLevels.length == 0) {
            throw new IllegalStateException("Cannot turn on appliance: power levels not defined.");
        }
        // Default to the first power level (index 0) when turned on without a specific level.
        this.currentLevel = 0;
    }

    public void turnOn(int level){
        setStatus(ON);
        setCurrentLevel(level); // This will validate the level against available powerLevels.
    }

    public double getCurrentConsumption() {
        if (getStatus() == ON) {
            if (this.powerLevels == null || this.powerLevels.length == 0) {
                // This case should ideally be prevented by checks in turnOn/setCurrentLevel
                // or by ensuring powerLevels is always initialized.
                // Returning 0 or throwing an exception are options.
                // For consistency with getConsumptionIfOn, let's assume an issue if powerLevels is null/empty here.
                throw new IllegalStateException("Appliance is ON but power levels are not defined.");
            }
            // Ensure currentLevel is valid; it should be if setters/turnOn are used correctly.
            if (this.currentLevel < 0 || this.currentLevel >= this.powerLevels.length) {
                 throw new IllegalStateException("Appliance is ON but current power level is invalid.");
            }
            return (this.powerLevels[this.currentLevel] / 100.0) * getMaxPowerConsumption();
        }
        return 0; // Off
    }

    public double getConsumptionIfOn() {
        if (this.powerLevels == null || this.powerLevels.length == 0) {
            // If there are no power levels, consumption is undefined or could be considered 0.
            // Throwing an exception might be more appropriate if powerLevels are essential.
            throw new IllegalStateException("Power levels not defined for this appliance, cannot calculate consumption if on.");
        }
         // Assume currentLevel is valid for the "if on" scenario, or default to level 0 if not explicitly set.
         // However, currentLevel should be set by turnOn or setCurrentLevel.
         // If we want to predict for a specific level (e.g., default first level):
         // return (this.powerLevels[0] / 100.0) * getMaxPowerConsumption();
         // For now, using the currently set currentLevel:
        if (this.currentLevel < 0 || this.currentLevel >= this.powerLevels.length) {
            // This implies an inconsistent state if the device is meant to be "on" at this level.
            // Or, if it's just a query, we might default to a base level or throw.
            // Let's assume currentLevel should be valid if this method is called meaningfully.
            throw new IllegalStateException("Current power level is invalid for calculating consumption if on.");
        }
        return (this.powerLevels[this.currentLevel]/100.0) * getMaxPowerConsumption();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Appliance{");
        sb.append(super.toString());
        sb.append(", power levels = {");
        if (powerLevels != null && powerLevels.length > 0) {
            sb.append("[");
            for(int i = 0; i < powerLevels.length; i++) {
                sb.append(powerLevels[i]);
                if (i < powerLevels.length - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
        } else {
            sb.append("N/A");
        }
        sb.append("}, level = ").append(currentLevel);
        sb.append(", ").append(noisy ? "noisy" : "not noisy");
        sb.append("}");
        return sb.toString();
    }
}
