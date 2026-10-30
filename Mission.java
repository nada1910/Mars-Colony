package lab5;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public abstract class Mission {

    public static final String FAILED = "FAILED";
    public static final String ASSIGNED = "ASSIGNED";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String COMPLETED = "COMPLETED";

    private int id;
    private int duration;
    private int elapsed;
    private String state;
    private String failureReason;
    private List<Agent> assignedAgents;
    private List<String> requiredCapabilities;
    private Map<String, Integer> requiredResources;
    private List<Facility> targetFacilities;

    public Mission(int id, int duration, List<String> requiredCapabilities,
                   Map<String, Integer> requiredResources,
                   List<Facility> targetFacilities) {
        if(duration<=0){
            throw new IllegalArgumentException("Invalid duration");
        }            
        if(id<=0){
                throw new IllegalArgumentException("Invalid id");
        }
        if(targetFacilities==null){
            throw new IllegalArgumentException("Invalid target facilities");
        }
        if(requiredResources==null){
            throw new IllegalArgumentException("Invalid required resources");
        }
        if(requiredCapabilities==null){
            throw new IllegalArgumentException("Invalid required capabilities");          
        }
        for(String c : requiredCapabilities){
           if(c == null || c.trim().isEmpty()){
        throw new IllegalArgumentException("Invalid capability");
            }
        }
        for(Map.Entry<String, Integer> r : requiredResources.entrySet()){
           if(r.getKey() == null || r.getKey().trim().isEmpty() ||
                r.getValue() == null || r.getValue() < 0){
        throw new IllegalArgumentException("Invalid resource");
            }
        }
        for(Facility f : targetFacilities){
          if(f == null){
        throw new IllegalArgumentException("Invalid facility");
            }
        }
       
        this.id = id;
        this.duration = duration;
        this.elapsed = 0;
        this.state = ASSIGNED;
        this.failureReason = "";
        this.assignedAgents = new ArrayList<>();
        this.requiredCapabilities = new ArrayList<>(requiredCapabilities);
        this.requiredResources = new java.util.HashMap<>(requiredResources);
        this.targetFacilities =  new ArrayList<>(targetFacilities);
    }
    
    public int getId(){ 
        System.out.println("Id:"+this.id);
        return this.id;
    }

    public int getDuration(){
        System.out.println("Duration"+this.duration);
        return this.duration;
    }
    
    public int getElapsed(){
        System.out.println("Elapsed"+this.elapsed);
        return this.elapsed;
    }
    
    public String getState(){
        System.out.println("State:"+this.state);
        return this.state;
    }
  
    public String getFailureReason(){
        System.out.println("Failure reason"+this.failureReason);    
        return this.failureReason;
    }
    
    public List<String> getRequiredCapabilities(){
        System.out.println("Required Capabilities:");
        for(String c : requiredCapabilities){
            System.out.println("- " + c);
        }
        return java.util.Collections.unmodifiableList(requiredCapabilities);
    }

    public List<Agent> getAssignedAgents(){
        System.out.println("Assigned Agents:");
            for(Agent a : assignedAgents){
        System.out.println("- " + a.getName());
    }
        return java.util.Collections.unmodifiableList(assignedAgents);
    }
    
    public Map<String, Integer> getRequiredResources(){
        System.out.println("Required Resources:");
        for(Map.Entry<String, Integer> r : requiredResources.entrySet()){
            System.out.println("- " + r.getKey() + ": " + r.getValue());
        }
        return java.util.Collections.unmodifiableMap(requiredResources);
    }
    
    public List<Facility> getTargetFacilities(){
        System.out.println("Target Facilities:");
            for(Facility f : targetFacilities){
        System.out.println("- " + f.getName());
        }
        return java.util.Collections.unmodifiableList(targetFacilities);
    }

    
    public abstract boolean isAllowedIn(Environment e);

    public void assign(Agent a){
        if(a == null || !ASSIGNED.equals(state) || !a.canPerform(this) || assignedAgents.contains(a)){
            throw new IllegalArgumentException("Invalid failure reason");
            return;
        }
        assignedAgents.add(a);
        a.assignToMission();
    }
    
    public void start(){
        if(!ASSIGNED.equals(state) || assignedAgents.isEmpty()){
            return;
        }    
        state = IN_PROGRESS;
        System.out.println("mission in progress");
    } 
    
    public void tick(){
        if(!IN_PROGRESS.equals(state)){
            return;
        }
        elapsed++;
        if(elapsed >= duration){
            complete();
        }
    }
    
    public void complete(){
        if(!IN_PROGRESS.equals(state)){
            return;
        }
        state = COMPLETED;
        System.out.println("Mission is completed");
        releaseAgents();
    }
    
    public void fail(String reason){
        if (COMPLETED.equals(state) || FAILED.equals(state)) {
            return;
        }
        if(reason == null || reason.trim().isEmpty()){
            throw new IllegalArgumentException("Invalid failure reason");
        }
        System.out.println("The mission failed");
        state = FAILED;
        failureReason = reason;
        releaseAgents();
    }

    public boolean requires(String capability){
        if(capability == null || capability.trim().isEmpty()){
            throw new IllegalArgumentException("Invalid capability");
        }    
        return requiredCapabilities.contains(capability);
    }

    private void releaseAgents() {
        for (Agent a : assignedAgents){
            a.release();
        }

        assignedAgents.clear();
    }
}