public class Fire extends Emergency {
    private boolean fireActive;
    private int severity;//1 for low 2 for moderate 3 for high
    private int oxygenConsumption;
    private Laboratory affectedLab;
    private  MedicalEmergency followUpMedical;

    public Fire(boolean fireActive, int severity, int oxygenConsumption,Laboratory affectedLab, int id, EmergencyType type, FacilityType location, String description) {
        super(id, type, location, description);
          if (affectedLab == null) {
            throw new IllegalArgumentException("Invalid Habitat");
        }
        if (severity < 1 || severity > 3) {
            throw new IllegalArgumentException("Severity must be between 1 and 3");
        }
        if (oxygenConsumption < 0) {
            throw new IllegalArgumentException("Invalid oxygen consumption");
        }
        this.affectedLab=affectedLab;
        this.fireActive = fireActive;
        this.severity = severity;
        this.oxygenConsumption = oxygenConsumption;
    }

    public boolean isFireActive() { return fireActive; }
    public int getSeverity() { return severity; }

    public int getOxygenConsumption() {
        return oxygenConsumption;
    }

    public Laboratory getAffectedLab() {
        return affectedLab;
    }
    
    @Override
    public boolean hasRequiredResponders() {
       return !responders().isEmpty();
       
    }
    public boolean extinguishFire(){
        if(hasRequiredResponders()){
           fireActive=false;
           affectedLab.extinguish();
           System.out.println("Fire Extinguished");
           resolveEmergency();
           return true;
        }
        System.out.println("Fire Still Active");
        this.setStatus(EmergencyStatus.UNRESOLVED);
        escalateToMedicalEmergency();

       
        
        return false;
    }
    public void consumeOxygen(){
        if(severity==1){
            affectedLab.consumeResource(ResourcesType.OXYGEN,5);
        }
        else if(severity==2){
            affectedLab.consumeResource(ResourcesType.OXYGEN,10);
        }
        else{
           affectedLab.consumeResource(ResourcesType.OXYGEN,15);
        }
     
        
    }
        private void escalateToMedicalEmergency() {
        if (followUpMedical != null) {
            return;
        }
        CrewMember patient = null;
        for (Agent a : allAgents) {
            if (a instanceof CrewMember) {
                patient = (CrewMember) a;
                break;
            }
        }
        if (patient == null) {
            System.out.println("No crew member available to be injured by the fire");
            return;
        }
        followUpMedical = new MedicalEmergency(1, patient, affectedLab, 3,
                getId() + 1000, EmergencyType.MEDICAL_EMERGENCY, FacilityType.LABORATORY,
                "Patient injured by uncontrolled fire  " );
       
    }


    public void setFire(){
        fireActive=true;
        affectedLab.setOperationalStatus(OperationalStatus.DAMAGED);
        affectedLab.setFire();
   
    }
     public MedicalEmergency getFollowUpMedical() {
        return followUpMedical;
    }

    
        public void tick() {
    if (fireActive && getStatus() != EmergencyStatus.RESOLVED) {
        consumeOxygen();
         escalateToMedicalEmergency();
    }
}
        
    @Override
    public void applyInitialEffect() { setFire(); }

    @Override
    public void resolve() { extinguishFire(); }

    @Override
    public Facility getAffectedFacility() { return affectedLab; }
    
    
}
