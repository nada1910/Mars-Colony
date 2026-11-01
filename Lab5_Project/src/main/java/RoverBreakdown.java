public class RoverBreakdown extends Emergency {

    private ExplorationRover explorationRover;//damaged rover
    private int requiredRepairParts;
    private Facility breakdownLocation;
    private boolean repairStatus=true;//true for repaired flase for not repaired
    private int requiredNumberOfResponder;
    private int availableSpareParts;

    public RoverBreakdown(ExplorationRover explorationRover, int requiredRepairParts, Facility breakdownLocation, int requiredNumberOfResponder,int availableSpareParts, int id, EmergencyType type, FacilityType location, String description) {
        super(id, type, location, description);
        if (breakdownLocation == null) {
            throw new IllegalArgumentException("Invalid Breakdown Location");
        }
        if (explorationRover == null) {
            throw new IllegalArgumentException("Invalid Exploration Rover");
        }
        if (requiredRepairParts < 0) {
            throw new IllegalArgumentException("Invalid Number Of Spare Parts");
        }

        if (requiredNumberOfResponder <= 0) {
            throw new IllegalArgumentException("Invalid Number Of Responders");
        }
        if (availableSpareParts < 0) {
            throw new IllegalArgumentException("Invalid Number Of Spare Parts");
        }
        this.explorationRover = explorationRover;
        this.requiredRepairParts = requiredRepairParts;
        this.breakdownLocation = breakdownLocation;
        this.requiredNumberOfResponder=requiredNumberOfResponder;
        this.availableSpareParts=availableSpareParts;
        
    }
    public boolean breakdown(){
        if(this.repairStatus==false){
             System.out.println("Rover Already Broken");
            return false;
        }
         System.out.println("Rover Brokedown");
         this.repairStatus=false;
         explorationRover.setStatus(Robot.DAMAGED);
         return true;
    }

    @Override
    public boolean hasRequiredResponders() {
        int responderCount = getResponders(allAgents).size();
        if(responderCount>=this.requiredNumberOfResponder){
            return true;}
        return false;
    }
    public boolean repairRover(){
        if(hasRequiredResponders()&&checkRepairParts()){
            this.repairStatus=true;
            this.resolveEmergency();
            System.out.println("Rover is Repaired Successfuly");
            return true;
        }
        this.setStatus(EmergencyStatus.UNRESOLVED);
        System.out.println("Rover is Not Repaired");
        return false;
    }

    public ExplorationRover getExplorationRover() {
        return explorationRover;
    }

    public void setExplorationRover(ExplorationRover explorationRover) {
        this.explorationRover = explorationRover;
    }

    public int getRequiredRepairParts() {
        return requiredRepairParts;
    }

    public void setRequiredRepairParts(int requiredRepairParts) {
        this.requiredRepairParts = requiredRepairParts;
    }

    public Facility getBreakdownLocation() {
        return breakdownLocation;
    }

    public void setBreakdownLocation(Facility breakdownLocation) {
        this.breakdownLocation = breakdownLocation;
    }

    public boolean isRepairStatus() {
        return repairStatus;
    }

    public void setRepairStatus(boolean repairStatus) {
        this.repairStatus = repairStatus;
    }

    public int getRequiredNumberOfResponder() {
        return requiredNumberOfResponder;
    }

    public void setRequiredNumberOfResponder(int requiredNumberOfResponder) {
        this.requiredNumberOfResponder = requiredNumberOfResponder;
    }

    public int getAvailableSpareParts() {
        return availableSpareParts;
    }

    public void setAvailableSpareParts(int availableSpareParts) {
        this.availableSpareParts = availableSpareParts;
    }
    public boolean checkRepairParts(){
        if(availableSpareParts>=requiredRepairParts)
            return true;
        return false;
    }
    
    @Override
    public void applyInitialEffect() { breakdown(); }

    @Override
    public void resolve() {
        repairStatus = true;
        explorationRover.setStatus(Robot.OPERATIONAL);
        resolveEmergency();
    }

    @Override
    public Facility getAffectedFacility() { return breakdownLocation; }

    @Override
    public java.util.Map<ResourcesType, Integer> getRequiredResources() {
        java.util.Map<ResourcesType, Integer> r = new java.util.EnumMap<>(ResourcesType.class);
        r.put(ResourcesType.SPARE_PARTS, requiredRepairParts);
        return r;
    }
}
