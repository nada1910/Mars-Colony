package lab5;
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

        if(m == null || agents == null || agents.isEmpty()){
            throw new IllegalArgumentException("Invalid Mission");
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
        }

        if(!m.isAllowedIn(e)){
            m.fail("Environment is not suitable");
            missionHistory.add(m);
            return;
        }

        if(m.getRequiredResources() == null){
            throw new IllegalArgumentException("Invalid Required Resources");
        }
        if(agents.indexOf(agent) != agents.lastIndexOf(agent)){
                m.fail("Duplicate Agent");
                missionHistory.add(m);
                return;
        }

        for(Map.Entry<String, Integer> resource : m.getRequiredResources().entrySet()){

            if(resource.getKey() == null || resource.getValue() == null || resource.getValue() < 0){
                throw new IllegalArgumentException("Invalid Resource");
            }

            String type = resource.getKey();
            int required = resource.getValue();

            if(getTotal(type) < required){
                m.fail("Not enough " + type);
                missionHistory.add(m);
                return;
            }
        }

        for(Agent agent : agents){
            m.assign(agent);
        }

        m.start();
        activeMission = m;
    }

    public void tick(){

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

    public int getTotal(String type){

        if(type == null){
            throw new IllegalArgumentException("Invalid Resource Type");
        }

        int total = 0;

        for(Facility facility : facilities){

            if(facility.isOperational()){

                Resource resource = facility.getResource(type);

                if(resource != null){
                    total += resource.getQuantity();
                }
            }
        }

        return total;
    }

    public boolean consumeResources(Map<String, Integer> resources){

        if(resources == null){
            throw new IllegalArgumentException("Invalid Resources");
        }

        for(Map.Entry<String, Integer> resource : resources.entrySet()){
            if(resource.getKey() == null || resource.getValue() == null || resource.getValue() < 0){
                throw new IllegalArgumentException("Invalid Resource");
            }
            String type = resource.getKey();
            int required = resource.getValue();
            if(getTotal(type) < required){
                return false;
            }
        }

        for(Map.Entry<String, Integer> resource : resources.entrySet()){
            String type = resource.getKey();
            int required = resource.getValue();
            for(Facility facility : facilities){
                if(facility.isOperational() && required > 0){
                    Resource r = facility.getResource(type);
                    if(r != null){
                        int amount = Math.min(required, r.getQuantity());
                        r.consume(amount);
                        required -= amount;
                    }
                }
            }
        }

        return true;
    }

    public void handleEmergency(Emergency e){

        if(e == null){
            throw new IllegalArgumentException("Invalid Emergency");
        }

        emergencyLog.add(e);
        e.applyInitialEffect();

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
        boolean oxygenLeak = emergencyType.contains("oxygen") &&emergencyType.contains("leak");

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

        for(Agent agent : responders){
            e.getResponders().add(agent);
            agent.assignToMission();
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
        System.out.println("Oxygen: " + getTotal(Resource.OXYGEN));
        System.out.println("Water: " + getTotal(Resource.WATER));
        System.out.println("Food: " + getTotal(Resource.FOOD));
        System.out.println("Fuel: " + getTotal(Resource.FUEL));
        System.out.println("Battery Power: " + getTotal(Resource.BATTERY_POWER));
        System.out.println("Medical Supplies: " + getTotal(Resource.MEDICAL_SUPPLIES));
        System.out.println("Spare Parts: " + getTotal(Resource.SPARE_PARTS));
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