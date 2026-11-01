import java.util.List;
import java.util.Map;

public class RescueMission extends Mission {

    public RescueMission(int id, int duration, List<String> requiredCapabilities,
                         Map<ResourcesType, Integer> requiredResources,
                         List<Facility> targetFacilities) {
        super(id, duration, requiredCapabilities, requiredResources, targetFacilities);
    }

    @Override
    public boolean isAllowedIn(Environment e) {
        return e != null && e.allowsCommunication();
    }
}