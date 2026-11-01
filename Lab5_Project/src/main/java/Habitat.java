public class Habitat extends Facility {

    private final LifeSupportSystem lifeSupport;//composition
    private boolean leaking;
    
    

        public Habitat(int id, String name, int capacity, OperationalStatus operationalStatus, int consumptionRate) {
        super(id, name, FacilityType.HABITAT, capacity, operationalStatus);
      
        this.lifeSupport = new LifeSupportSystem(true,consumptionRate);//composition
    }

   public int getOxygen() {
        return getQuantity(ResourcesType.OXYGEN);
    }
   public LifeSupportSystem getLifeSupport() {
        return lifeSupport;
    }

    public boolean reduceOxygen(int amount) {
        if (amount < 0 ) {
            System.out.println("Invalid Amount");
            return false;
        }
        if(amount>getOxygen()){
            consumeResource(ResourcesType.OXYGEN, getOxygen());
            System.out.println("Oxygen Amount Is less, Oxygen Set to 0");
            return true;
        }
        else{
           consumeResource(ResourcesType.OXYGEN, amount);
        return true;}}
  
    public boolean isLeaking() {
        return leaking;
    }

    public void setLeaking(boolean leaking) {
        this.leaking = leaking;
    }

    
    public void stopLeak() {
        leaking = false;
    }
    
    @Override
    public void advanceTime(){
        reduceOxygen(lifeSupport.getConsumptionRate() * getOccupants().size());
        lifeSupport.tick();
    }

  

}