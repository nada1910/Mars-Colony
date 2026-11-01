import java.util.List;
import java.util.Map;

public class RepairMission extends Mission{
    private Facility targetFacility;
    private Equipment targetEquipment;

    private static Facility validateFacility(Facility facility){
        if(facility == null){
            throw new IllegalArgumentException("Invalid target facility");
        }
        return facility;
    }

    private static Equipment validateEquipment(Equipment equipment){
        if(equipment == null){
            throw new IllegalArgumentException("Invalid target equipment");
        }
        return equipment;
    }

    public RepairMission(int id, int duration,
        List<String> requiredCapabilities, Map<ResourcesType, Integer> requiredResources,
        List<Facility> targetFacilities, Facility targetFacility, Equipment targetEquipment) {
        super(id, duration, requiredCapabilities, requiredResources, targetFacilities);
        this.targetFacility = validateFacility(targetFacility);
        this.targetEquipment = validateEquipment(targetEquipment);
    }

    public Facility getTargetFacility(){
        return targetFacility;
    }

    public Equipment getTargetEquipment(){
        return targetEquipment;
    }

    @Override
   public boolean isAllowedIn(Environment e) {
    return e != null && e.allowsCommunication();
}
    
}