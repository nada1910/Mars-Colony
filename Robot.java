public abstract class Robot implements Agent {
    public static final String OPERATIONAL = "OPERATIONAL";
    public static final String DAMAGED = "DAMAGED";
    public static final String DESTROYED = "DESTROYED";
    public static final int MIN_BATTERY = 50;

    private int id;
    private String name;
    private int battery;
    private String status;
    private Location location;     
    private boolean busy;

    public Robot(int id, String name, Location location, String status) {
        if (!Validator.validateNumber(id))
            throw new IllegalArgumentException("ID is not valid");
        if (!Validator.validateText(name))
            throw new IllegalArgumentException("Name is not valid");
        if (location == null)
            throw new IllegalArgumentException("Location is not valid");
        if (!isValidStatus(status))
            throw new IllegalArgumentException("Status is not valid");
        this.id = id;
        this.name = name;
        this.location = location;
        this.status = status;
        this.battery = 100;
        this.busy = false;
    }

    private static boolean isValidStatus(String s) {
        return OPERATIONAL.equals(s) || DAMAGED.equals(s) || DESTROYED.equals(s);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public int getBattery() { return battery; }
    public String getStatus() { return status; }
    public boolean isBusy(){return this.busy;}

    public void setStatus(String newStatus) {
        if (isValidStatus(newStatus)) status = newStatus;
    }

    public boolean moveTo(Location destination) {
        if (destination == null) return false;
        location = destination;
        return true;
    }

    public boolean isUsable() {
        return status.equals(OPERATIONAL);
    }

    public boolean isAvailable() {
        return !busy && isUsable() && battery > MIN_BATTERY;
    }

    public void assignToMission() {
        if (isAvailable()) {
            busy = true;
            System.out.println(name + " is assigned");
        } else {
            System.out.println(name + " cannot be assigned: damaged, busy, or low battery");
        }
    }

    public void release() {
        if (!busy) {
            System.out.println(name + " is already released");
            return;
        }
        busy = false;
    }

    public boolean consumeBattery(int n) {
        if (n > 0 && battery >= n) {
            battery -= n;
            return true;
        }
        System.out.println(name + ": battery too low, please recharge");
        return false;
    }

    public void recharge() { battery = 100; }

    public boolean canPerform(Mission mission) { return false; }
    public boolean canRespondTo(Emergency emergency) { return false; }

    public void tick() {
        if (busy) consumeBattery(5);
    }
}