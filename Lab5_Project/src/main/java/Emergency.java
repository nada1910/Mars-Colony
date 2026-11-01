import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;

public abstract class Emergency {
    private final int id;
    private final EmergencyType type;
    private final FacilityType location;
    private final String description;
    private EmergencyStatus status=EmergencyStatus.DETECTED;
    private final List<Agent> responders = new ArrayList<>();
    protected List<Agent> allAgents = new ArrayList<>(); 
    private String unresolvedReason = "";

    public Emergency(int id, EmergencyType type,FacilityType location,String description) {
        if(id<=0){
            throw new IllegalArgumentException("Invalid Id");
        }
        if(type==null){
            throw new IllegalArgumentException("Invalid Type");
        }
         
        if(location==null){
            throw new IllegalArgumentException("Invalid Location");
        }
        if(description==null){
            throw new IllegalArgumentException("Invalid Description");
        }
        
        
        this.id = id;
        this.type = type;
        this.location = location;
        this.description = description;
    }
    public void handleEmergency(){
        
    }
     public void setAllAgents(List<Agent> agents) {
        this.allAgents = (agents == null) ? new ArrayList<Agent>() : agents;
    }
   
    public boolean resolveEmergency(){
        this.status=EmergencyStatus.RESOLVED;
        return true;
    }
    public List<Agent> responders() {
    return Collections.unmodifiableList(responders);
}
    public int getId() {
        return id;
    }

    public EmergencyType getType() {
        return type;
    }

    public FacilityType getLocation() {
        return location;
    }

    public void setStatus(EmergencyStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public EmergencyStatus getStatus() {
        return status;
    }

    public List<Agent> getResponders(List<Agent> allAgents) {
         responders.clear();
        for(Agent agent :allAgents ){
            if(agent.isAvailable() && agent.canRespondTo(this)){
                responders.add(agent);
            }
                
        }
        return responders;
    }
    abstract public boolean hasRequiredResponders();
    
    public void applyInitialEffect() { }
    public void applyUnresolvedPenalty() { }
    public String checkPreconditions() { return null; }
    public Facility getAffectedFacility() { return null; }
    public Map<ResourcesType,Integer> getRequiredResources() { return new EnumMap<ResourcesType,Integer>(ResourcesType.class); }
    public void addResponder(Agent a) { if (a != null) responders.add(a); }
    public void resolve() { resolveEmergency(); }
    public void markUnresolved(String reason)
    {
        unresolvedReason = reason;
        status = EmergencyStatus.UNRESOLVED;
        System.out.println("Emergency unresolved. Reason: " + reason);
    }
    public String getUnresolvedReason() { return unresolvedReason; }
    
}
