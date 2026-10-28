public abstract class CrewMember implements Agent {
    private int id;
    private String name;
    private int health;
    private Location location;     
    private int fatigue;
    private boolean busy;

    public CrewMember(int id, String name, Location location) {
        if (!Validator.validateNumber(id))
            throw new IllegalArgumentException("ID is not valid");
        if (!Validator.validateText(name))
            throw new IllegalArgumentException("Name is not valid");
        if (location == null)
            throw new IllegalArgumentException("Location is not valid");

        this.id = id;
        this.name = name;
        this.location = location;
        this.health = 100;
        this.fatigue = 0;
        this.busy = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public int getHealth() { return health; }
    public int getFatigue() { return fatigue; }
    public boolean isBusy() { return busy; }
    
    public void heal(int amount) 
    { 
        if (amount > 0) 
            this.health = Math.min(100, health + amount); 
    }
    public void injure(int amount)
    { 
        if (amount > 0) 
            health = Math.max(0, health - amount); 
    }

    public boolean moveTo(Location destination) {
        if (destination == null) return false;
        location = destination;
        return true;
    }

    public boolean isUsable() {
        return health >= 50 && fatigue <= 40;
    }

    public boolean isAvailable() {
        return !busy && isUsable();
    }

    public void assignToMission() {
        if (isAvailable()) {
            busy = true;
            System.out.println(name + " is assigned");
        } else {
            System.out.println(name + " cannot be assigned");
        }
    }

    public void release() {
        if (!busy) {
            System.out.println(name + " is already released");
            return;
        }
        busy = false;
    }

    public boolean canPerform(Mission mission) { return false; }
    public boolean canRespondTo(Emergency emergency) { return false; }

    public void tick() {
        if (busy && fatigue < 100) fatigue += 5;
    }

    public void rest() {
        health = 100;
        fatigue = 0;
    }
}