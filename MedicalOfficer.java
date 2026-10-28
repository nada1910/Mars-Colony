public class MedicalOfficer extends CrewMember {
    public MedicalOfficer(int id, String name, Location location) {
        super(id, name, location);
    }
    
    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null) return false;
        if(mission.requires(Agent.RESCUE))
            return true;
        return false;
        }

    @Override
    public boolean canRespondTo(Emergency emergency) {
        if (emergency == null) return false;
        if(emergency.getType().equals(Emergency.FIRE)
            || emergency.getType().equals(Emergency.MEDICAL))
            return true ;
        return false;
    }
}