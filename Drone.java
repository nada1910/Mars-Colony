public class Drone extends Robot {

    public Drone(int id, String name, Location location, String status) {
        super(id, name, location, status);
    }

    @Override
    public boolean canPerform(Mission mission) {
        if(mission.requires(Agent.AERIAL_RECON) || mission.requires(Agent.RESCUE))
            return true;
        return false;
    }

    public boolean isControllable(Environment env) {
        if (env == null) return false;
        if(env.allowsCommunication())
            return true;
        return false;
    }
}