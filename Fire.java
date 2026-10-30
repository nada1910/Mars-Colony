/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;
public class Fire extends Emergency {
    private boolean fireActive;
    private int severity;//1 for low 2 for moderate 3 for high
    private int oxygenConsumption;
    private Laboratory affectedLab;

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

    public boolean isFireActive() {
        if(fireActive){
            System.out.println("Habitat Is On Fire");
        }else{
             System.out.println("Habitat Is Not On Fire");
        }
  
        return fireActive;
    }

    public int getSeverity() {
        if(severity==1){
            System.out.println("Fire Severity Is Low");
        }
        else if(severity==2){
            System.out.println("Fire Severity Is Moderate ");
        }
        else{
            System.out.println("Fire Severity Is High");
        }
       
        return severity;
    }

    public int getOxygenConsumption() {
        System.out.println("Oxygen Consumption By The Fire:"+this.oxygenConsumption);
        return oxygenConsumption;
    }

    public Laboratory getAffectedLab() {
        System.out.println("Affected Laboratory"+this.affectedLab);
        return affectedLab;
    }
    
    @Override
    public boolean hasRequiredResponders() {
       return !responders().isEmpty();
       
    }
    public boolean extinguishFire(){
        if(hasRequiredResponders()){
           fireActive=false;
           System.out.println("Fire Extinguished");
           resolveEmergency();
           return true;
        }
        System.out.println("Fire Still Active");
        this.setStatus(EmergencyStatus.UNRESOLVED);
        
        
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
    public void setFire(){
        fireActive=true;
        affectedLab.setOperationalStatus(OperationalStatus.DAMAGED);
       affectedLab.damageAllEquipment();
    }
    
        public void tick() {
    if (fireActive && getStatus() != EmergencyStatus.RESOLVED) {
        consumeOxygen();
    }
}
    
    
}
