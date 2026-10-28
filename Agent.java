public interface Agent {
    String EXPLORE = "EXPLORE";
    String RESEARCH = "RESEARCH";
    String REPAIR = "REPAIR";
    String MEDICAL = "MEDICAL";
    String FIRE_RESPONSE = "FIRE_RESPONSE";
    String AERIAL_RECON = "AERIAL_RECON";
    String RESCUE = "RESCUE";
    String COMMUNICATION = "COMMUNICATION";

    int getId();
    String getName();
    Location getLocation();                  
    boolean moveTo(Location destination);    
    boolean isAvailable();
    void assignToMission();
    void release();
    boolean canPerform(Mission mission);
    boolean canRespondTo(Emergency emergency);
    void tick();
}