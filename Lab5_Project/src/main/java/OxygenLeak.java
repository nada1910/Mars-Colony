import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class OxygenLeak extends Emergency {

    private boolean leak; //true for leak and false after being repaired
    private final Habitat affectedHabitat;
    private final double oxygenLoss;

    public OxygenLeak(boolean leak, Habitat affectedHabitat, double oxygenLoss, int id, EmergencyType type, FacilityType location, String description) {
        super(id, type, location, description);
        if (affectedHabitat == null) {
            throw new IllegalArgumentException("Invalid Habitat");
        }
        if (oxygenLoss <= 0) {
            throw new IllegalArgumentException("Invalid Oxygen Loss");
        }
        this.oxygenLoss = oxygenLoss;
        this.leak = leak;
        this.affectedHabitat = affectedHabitat;

    }

    public boolean isLeak() {
        
        return leak;
    }
     public void detect() {
        affectedHabitat.setLeaking(true);
        deduceOxygen(oxygenLoss);
        setStatus(EmergencyStatus.RESPONDING);
    }

    public boolean deduceOxygen(double oxygenLoss) {
        if (oxygenLoss <= 0) {
            return false;
        }

        affectedHabitat.reduceOxygen((int)oxygenLoss);
        return true;
    }

    public boolean repairLeak() {
        if (!leak) {
            System.out.println("Oxygen leak already repaired");
            return false;
        }
        if (!hasRequiredResponders()) {
            //second deduction
            deduceOxygen(oxygenLoss);
            this.setStatus(EmergencyStatus.UNRESOLVED);
            System.out.println("Oxygen leak Not repaired ");
            return false;
        }

        leak = false;
        affectedHabitat.stopLeak();
        resolveEmergency();
        System.out.println("Oxygen leak repaired successfully");
        return true;
    }

    @Override
    public boolean hasRequiredResponders(){
        List<Agent> team = responders();
        if (team.isEmpty()) {
            team = getResponders(allAgents);
        }
        int humanCount = 0;
        int robotCount = 0;
        for (Agent agent : team) {
            if (agent.isHuman()) {
                humanCount++;
            } else {
                robotCount++;
            }
        }
        return humanCount >= 1 && robotCount >= 1;
    }


    public Habitat getAffectedHabitat() {
        return affectedHabitat;
    }
    
    @Override
    public void applyInitialEffect() { detect(); }

    @Override
    public void applyUnresolvedPenalty() { deduceOxygen(oxygenLoss); }

    @Override
    public void resolve() { repairLeak(); }

    @Override
    public Facility getAffectedFacility() { return affectedHabitat; }

    @Override
    public java.util.Map<ResourcesType, Integer> getRequiredResources() {
        java.util.Map<ResourcesType, Integer> r = new java.util.EnumMap<>(ResourcesType.class);
        r.put(ResourcesType.SPARE_PARTS, 2);
        r.put(ResourcesType.BATTERY_POWER, 5);
        return r;
    }
   
}
