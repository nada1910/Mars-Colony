import java.util.*;

public class Colony {

    private Mission activeMission;
    private Environment e;
    private List<Facility> facilities;
    private List<Mission> missionHistory;
    private List<Emergency> emergencyLog;
    private List<CrewMember> crew;
    private List<Robot> robots;

    public Colony(Environment e, List<Facility> facilities, List<CrewMember> crew, List<Robot> robots) {

        if(e == null){
            throw new IllegalArgumentException("Invalid Environment");
        }

        if(facilities == null || crew == null || robots == null){
            throw new IllegalArgumentException("Invalid Colony Data");
        }

        for(Facility facility : facilities){
            if(facility == null){
                throw new IllegalArgumentException("Invalid Facility");
            }
        }

        for(CrewMember member : crew){
            if(member == null){
                throw new IllegalArgumentException("Invalid Crew Member");
            }
        }

        for(Robot robot : robots){
            if(robot == null){
                throw new IllegalArgumentException("Invalid Robot");
            }
        }

        this.e = e;
        this.facilities = new ArrayList<>(facilities);
        this.crew = new ArrayList<>(crew);
        this.robots = new ArrayList<>(robots);

        this.activeMission = null;
        this.missionHistory = new ArrayList<>();
        this.emergencyLog = new ArrayList<>();
    }

    public void submitMission(Mission m, List<Agent> agents){

    if(m == null){
        throw new IllegalArgumentException("Invalid Mission");
    }

    if(agents == null || agents.isEmpty()){
        m.fail("No agents were assigned to the mission");
        missionHistory.add(m);
        return;
    }

    if(activeMission != null){
        m.fail("There is an active mission");
        missionHistory.add(m);
        return;
    }

    for(Agent agent : agents){

        if(agent == null || !agent.isAvailable()){
            m.fail("Agent is not available");
            missionHistory.add(m);
            return;
        }

        if(!agent.canPerform(m)){
            m.fail("Agent cannot perform this mission");
            missionHistory.add(m);
            return;
        }

        if(!agent.canOperateIn(e)){
            m.fail("Agent cannot operate in the current environment");
            missionHistory.add(m);
            return;
        }

        if(agents.indexOf(agent) != agents.lastIndexOf(agent)){
            m.fail("Duplicate Agent");
            missionHistory.add(m);
            return;
        }
    }

    if(!m.isAllowedIn(e)){
        m.fail("Environment is not suitable");
        missionHistory.add(m);
        return;
    }

    if(m.getRequiredResources() == null){
        throw new IllegalArgumentException("Invalid Required Resources");
    }

    for(Map.Entry<ResourcesType, Integer> resource : m.getRequiredResources().entrySet()){

        if(resource.getKey() == null || resource.getValue() == null || resource.getValue() < 0){
            throw new IllegalArgumentException("Invalid Resource");
        }

        ResourcesType type = resource.getKey();
        int required = resource.getValue();

        if(getTotal(type) < required){
            m.fail("Not enough " + type);
            missionHistory.add(m);
            return;
        }
    }

    consumeResources(m.getRequiredResources());

    for(Agent agent : agents){
        m.assign(agent);
    }

    m.start();
    activeMission = m;
    }
    
    private void checkActiveMission(){

    if(activeMission == null){
        return;
    }

    String reason = null;

    if(!activeMission.isAllowedIn(e)){
        reason = "Environment is not suitable";
    }
    else{
        for(Agent agent : activeMission.getAssignedAgents()){
            if(!agent.isUsable()){
                reason = agent.getName() + " is no longer usable (damaged, low battery or exhausted)";
                break;
            }
        }
    }

    if(reason != null){
        activeMission.fail(reason);
        missionHistory.add(activeMission);
        activeMission = null;
    }
}
    
    public void tick(){

    for(CrewMember member : crew){
        member.tick();
    }
    for(Robot robot : robots){
        robot.tick();
    }

    checkActiveMission();

    if(activeMission == null){
        return;
    }

    activeMission.tick();

    if(activeMission.getState().equals(activeMission.COMPLETED) ||
       activeMission.getState().equals(activeMission.FAILED)){

        missionHistory.add(activeMission);
        activeMission = null;
    }
}

    public int getTotal(ResourcesType type)
    {
        if(type == null){
            throw new IllegalArgumentException("Invalid Resource Type");
        }
        return Resources.getColonyTotal(facilities, type);
    }

    public boolean consumeResources(Map<ResourcesType, Integer> resources){

        if(resources == null){
            throw new IllegalArgumentException("Invalid Resources");
        }

        return Resources.consume(facilities, resources);
            }


    public void handleEmergency(Emergency e){

    if(e == null){
        throw new IllegalArgumentException("Invalid Emergency");
    }

    emergencyLog.add(e);
    List<Agent> everyone = new ArrayList<>();
    everyone.addAll(crew);
    everyone.addAll(robots);
    e.setAllAgents(everyone);

    e.applyInitialEffect();
    checkActiveMission();

    String blocked = e.checkPreconditions();
    if(blocked != null){
        e.applyUnresolvedPenalty();
        e.markUnresolved(blocked);
        return;
    }

    List<Agent> responders = new ArrayList<>();

    boolean hasCrew = false;
    boolean hasRobot = false;

    for(CrewMember agent : crew){

        if(agent.isAvailable() && agent.canRespondTo(e)){
            responders.add(agent);
            hasCrew = true;
        }
    }
    for(Robot agent : robots){

        if(agent.isAvailable() && agent.canRespondTo(e)){
            responders.add(agent);
            hasRobot = true;
        }
    }

    String emergencyType = e.getType().toString().toLowerCase();
    boolean oxygenLeak = emergencyType.contains("oxygen") && emergencyType.contains("leak");

    if(responders.isEmpty() || (oxygenLeak && (!hasCrew || !hasRobot))){
        e.applyUnresolvedPenalty();
        if(oxygenLeak && (!hasCrew || !hasRobot)){
            e.markUnresolved("Oxygen leak requires a crew member and a robot");
        }
        else{
            e.markUnresolved("No suitable responders available");
        }
        return;
    }

    if(!consumeResources(e.getRequiredResources())){

        e.applyUnresolvedPenalty();
        e.markUnresolved("Not enough resources");
        return;
    }

    Facility site = e.getAffectedFacility();

    for(Agent agent : responders){
        e.addResponder(agent);
        agent.assignToMission();
        if(site != null){
            agent.moveTo(site);
        }
    }

    e.resolve();

    for(Agent agent : responders){
        agent.release();
    }
}

   public void statusReport(){

    System.out.println("===== REPORT =====");

    System.out.println("Environment: " + e.getCondition());

    if(activeMission != null){
        System.out.println("Active Mission: " + activeMission.getId());
        System.out.println("Mission State: " + activeMission.getState());
    }
    else{
        System.out.println("No Active Mission");
    }

    System.out.println("      FACILITIES      ");

    for(Facility facility : facilities){
        System.out.println(facility.getName() + " : " + facility.isOperational());
    }
    System.out.println("      RESOURCES      ");
    System.out.println("Oxygen: " + getTotal(ResourcesType.OXYGEN));
    System.out.println("Water: " + getTotal(ResourcesType.WATER));
    System.out.println("Food: " + getTotal(ResourcesType.FOOD));
    System.out.println("Fuel: " + getTotal(ResourcesType.FUEL));
    System.out.println("Battery Power: " + getTotal(ResourcesType.BATTERY_POWER));
    System.out.println("Medical Supplies: " + getTotal(ResourcesType.MEDICAL_SUPPLIES));
    System.out.println("Spare Parts: " + getTotal(ResourcesType.SPARE_PARTS));
    System.out.println("      MISSION HISTORY      ");

    for(Mission mission : missionHistory){
        System.out.println("Mission ID: " + mission.getId() +" | State: " + mission.getState());
    }

    System.out.println("      EMERGENCY LOG      ");

    for(Emergency emergency : emergencyLog){
        System.out.println("Emergency: " + emergency.getType() +" | Status: " + emergency.getStatus());
    }
}
}