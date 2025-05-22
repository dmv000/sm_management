public class Light extends Device{
    private boolean adjustable;
    private int level; //between 0 and 100 def 100

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
    public void setLevel(int level) {
        if (!adjustable && level != 100) {
            throw new IllegalStateException("Light is not adjustable and level must be 100.");
        }
        if (adjustable && (level < 0 || level > 100)) {
            throw new IllegalArgumentException("Adjustable light level must be between 0 and 100.");
        }
        this.level = level;
    }

    public void turnOn(){
        setStatus(ON);
        // When turning on, default to level 100 if adjustable, otherwise it's already 100 (or should be)
        // No need to explicitly set level here if setLevel handles the logic correctly.
        // If it's adjustable, the user might want to set a specific level afterwards.
        // If not adjustable, it should always be 100.
        // Consider if a specific level should be set on turnOn or if it should retain its previous level if adjustable.
        // For now, let's assume it defaults to 100 when turned on,
        // or more accurately, it uses the level set by setLevel.
        // The existing logic `if(adjustable) level = 100;` implies a reset to 100 if adjustable.
        // If it's not adjustable, it should be 100 anyway.
        if (adjustable) {
            this.level = 100; // Default to full brightness when turned on
        } else {
            this.level = 100; // Non-adjustable is always 100
        }
    }

    public void turnOn(int level){
        setStatus(ON);
        setLevel(level); // This will use the updated setLevel logic
    }

    @Override
    public double getCurrentConsumption() {
        return (getStatus() == ON) ? (level/100.0) * getMaxPowerConsumption() : 0;
    }

    public double getConsumptionIfOn() {
        return (level/100.0) * getMaxPowerConsumption();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Light{");
        sb.append(super.toString());
        sb.append(", ").append(adjustable ? "adjustable" : "not adjustable");
        sb.append(", level = ").append(level);
        sb.append("}");
        return sb.toString();
    }
}