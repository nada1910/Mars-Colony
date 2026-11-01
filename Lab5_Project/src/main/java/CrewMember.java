import java.util.ArrayList;
import java.util.List;

public abstract class CrewMember implements Agent {
    private int id;
    private String name;
    private int health;
    private Location location;     
    private int fatigue;
    private boolean busy;
    private final List<String> skills = new ArrayList<>();

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
    
    @Override
    public int getId() { return id; }
    @Override
    public String getName() { return name; }
    @Override
    public Location getLocation() { return location; }
    @Override
    public boolean isHuman() { return true; }
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
    
    @Override
    public boolean moveTo(Location destination) {
        if (destination == null) return false;
        location = destination;
        return true;
    }
    
    @Override
    public boolean isUsable() {
        return health >= 50 && fatigue <= 40;
    }

    @Override
    public boolean isAvailable() {
        return !busy && isUsable();
    }

    @Override
    public void assignToMission() {
        if (isAvailable()) {
            busy = true;
            System.out.println(name + " is assigned");
        } else {
            System.out.println(name + " cannot be assigned");
        }
    }

    @Override
    public void release() {
        if (!busy) {
            System.out.println(name + " is already released");
            return;
        }
        busy = false;
    }

    @Override
    public boolean canPerform(Mission mission) { return false; }
    
    @Override
    public boolean canRespondTo(Emergency emergency) { return false; }

    @Override
    public void tick() {
        if (busy && fatigue < 100) fatigue += 5;
    }

    public void rest() {
        health = 100;
        fatigue = 0;
    }
    
    public boolean addSkill(String skill) 
    {
    if (!Validator.validateText(skill) || hasSkill(skill)) return false;
    skills.add(skill.trim());
    return true;
    }
    
    public boolean hasSkill(String skill) 
    {
    if (skill == null) return false;
    // for-each loop.
    for (String s : skills) if (s.equalsIgnoreCase(skill.trim())) return true;
    return false;
    }
    
    public List<String> getSkills() { return new ArrayList<>(skills); }
}