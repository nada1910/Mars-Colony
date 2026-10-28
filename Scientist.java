public class Scientist extends CrewMember {
    public Scientist(int id, String name, Location location) {
        super(id, name, location);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null) return false;
        if(mission.requires(Agent.EXPLORE) || mission.requires(Agent.RESEARCH))
            return true;
        return false;
    }
}