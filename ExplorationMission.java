package lab5;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class ExplorationMission extends Mission{
    
    private String region;
    private List<Equipment> requiredEquipment;

    public ExplorationMission(int id, int duration,List<String> requiredCapabilities,
                   Map<String, Integer> requiredResources,List<Facility> targetFacilities
                   ,String region,List<Equipment> requiredEquipment){
        super(id,duration,requiredCapabilities,requiredResources,targetFacilities);

        this.region = validateRegion(region);
        this.requiredEquipment = new ArrayList<>(validateEquipment(requiredEquipment));
    }
    private static List<Equipment> validateEquipment(List<Equipment> equipment){
        if(equipment == null){
            throw new IllegalArgumentException("Invalid required equipment");
        }

        for(Equipment e : equipment){
            if(e == null){
                throw new IllegalArgumentException("Invalid equipment");
            }
        }
        return equipment;
    }

    private static String validateRegion(String region){
        if(region == null || region.trim().isEmpty()){
            throw new IllegalArgumentException("Invalid region");
        }

        return region;
    }
    public String getRegion(){
        System.out.println("Region:"+this.region);
        return this.region;
    }

    public void setRegion(String newr){
        if(newr==null|| newr.trim().isEmpty()){
            throw new IllegalArgumentException("Invalid region");
        }
        this.region=newr;
        System.out.println("Region:"+newr);
    }

    public List<Equipment> getRequiredEquipment(){
        System.out.println("Required Equipment:");
        for(Equipment e : requiredEquipment){
            System.out.println("- " + e.getName());
        }
        return java.util.Collections.unmodifiableList(requiredEquipment);
    }

    public boolean isAllowedIn(Environment e){
        if(e == null || !e.allowsSurfaceOperations()){
            System.out.println("The environment is not allowed");
            return false;
        }
        else{ 
            System.out.println("The environment is allowed");
            return true;
        }

    }
}