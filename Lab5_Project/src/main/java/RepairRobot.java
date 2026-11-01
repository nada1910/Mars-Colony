public class RepairRobot extends Robot {
    
    
    public RepairRobot(int id, String name, Location location, String status)
    {
        super(id,name,location,status);
    }
    
    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null) return false;
        if(mission.requires(Agent.REPAIR))
            return true;
        return false;
    }
    
    @Override
    public boolean canRespondTo(Emergency emergency) {
        if (emergency == null) return false;
        if( emergency.getType().equals(EmergencyType.OXYGEN_LEAK)
            || emergency.getType().equals(EmergencyType.ROVER_BREAKDOWN))
            return true;
        return false;
    }
}
