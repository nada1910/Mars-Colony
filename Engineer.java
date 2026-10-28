public class Engineer extends CrewMember {
    public Engineer(int id, String name, Location location) {
        super(id, name, location);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null) return false;
        if (mission.requires(Agent.REPAIR))
            return true;
        return false;
    }

    @Override
    public boolean canRespondTo(Emergency emergency) {
        if (emergency == null) return false;
        if (emergency.getType().equals(Emergency.OXYGEN_LEAK)
            || emergency.getType().equals(Emergency.ROVER_BREAKDOWN))
            return true;
        return false;
    }
}