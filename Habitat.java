/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;



public class Habitat extends Facility {

    private final LifeSupportSystem lifeSupport;
    private boolean leaking;
    
    

        public Habitat(int id, String name, int capacity, OperationalStatus operationalStatus, int consumptionRate) {
        super(id, name, FacilityType.HABITAT, capacity, operationalStatus);
      
        this.lifeSupport = new LifeSupportSystem(true,consumptionRate);
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
        System.out.println("Oxygen Reduced Successfully");
        return true;}}
    public void oxygenLeak(){
        reduceOxygen(30);
        System.out.println("Oxygen Reduced by 30 Due to A Leak");
        leaking=true;
        
    }
    public boolean isLeaking() {
        return leaking;
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