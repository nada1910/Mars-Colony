import java.util.List;
import java.util.Map;
public class ResearchMission extends Mission{
    private Laboratory lab;
    private String requiredSkill;

    private static Laboratory validateLab(Laboratory lab){
        if(lab == null){
            throw new IllegalArgumentException("Invalid laboratory");
        }
        return lab;
    }

    private static String validateSkill(String requiredSkill){
        if(requiredSkill == null || requiredSkill.trim().isEmpty()){
            throw new IllegalArgumentException("Invalid required skill");
        }
        return requiredSkill;
    }

    public ResearchMission(int id, int duration, List<String> requiredCapabilities,
        Map<ResourcesType, Integer> requiredResources, List<Facility> targetFacilities,
        Laboratory lab, String requiredSkill){
        super(id, duration, requiredCapabilities, requiredResources, targetFacilities);
        this.lab = validateLab(lab);
        this.requiredSkill = validateSkill(requiredSkill);
    }

    public Laboratory getLab(){
        return lab;
    }

    public String getRequiredSkill(){
        return requiredSkill;
    }

    @Override
    public boolean isAllowedIn(Environment e) {
    return e != null && lab.isOperational();
    }
}