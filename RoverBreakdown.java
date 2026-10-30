/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;

public class RoverBreakdown extends Emergency {

    private ExplorationRover explorationRover;//damaged rover
    private int requiredRepairParts;
    private Facility breakdownLocation;
    private boolean repairStatus;//true for repaired flase for not repaired
    private int requiredNumberOfResponder;
    private int availableSpareParts;

    public RoverBreakdown(ExplorationRover explorationRover, int requiredRepairParts, Facility breakdownLocation, int requiredNumberOfResponder,int availableSpareParts, int id, EmergencyType type, FacilityType location, String description) {
        super(id, type, location, description);
        if (breakdownLocation == null) {
            throw new IllegalArgumentException("Invalid Breakdown Location");
        }
        if (explorationRover == null) {
            throw new IllegalArgumentException("Invalid Exploration Rover");
        }
        if (requiredRepairParts < 0) {
            throw new IllegalArgumentException("Invalid Number Of Spare Parts");
        }

        if (requiredNumberOfResponder <= 0) {
            throw new IllegalArgumentException("Invalid Number Of Responders");
        }
        if (availableSpareParts < 0) {
            throw new IllegalArgumentException("Invalid Number Of Spare Parts");
        }
        this.explorationRover = explorationRover;
        this.requiredRepairParts = requiredRepairParts;
        this.breakdownLocation = breakdownLocation;
        this.requiredNumberOfResponder=requiredNumberOfResponder;
    }
    public boolean breakdown(){
        if(this.repairStatus==false){
             System.out.println("Rover Already Broken");
            return false;
        }
         System.out.println("Rover Brokedown");
         this.repairStatus=false;
         stop();//mafroud tw2f mission mesh 3arfa kda sah wala fi haga na2sa
         return true;
    }

    @Override
    public boolean hasRequiredResponders() {
        int responderCount = getResponders(allAgents).size();
        if(responderCount>=this.requiredNumberOfResponder){
            return true;}
        return false;
    }
    public boolean repairRover(){
        if(hasRequiredResponders()&&checkRepairParts()){
            this.repairStatus=true;
            this.resolveEmergency();
            System.out.println("Rover is Repaired Successfuly");
            return true;
        }
        this.setStatus(EmergencyStatus.UNRESOLVED);
        System.out.println("Rover is Not Repaired");
        return false;
    }

    public ExplorationRover getExplorationRover() {
        System.out.println("Rover: "+this.explorationRover);
        return explorationRover;
    }

    public void setExplorationRover(ExplorationRover explorationRover) {
        this.explorationRover = explorationRover;
    }

    public int getRequiredRepairParts() {
        System.out.println("Required Repair Parts: "+this.requiredRepairParts);
        return requiredRepairParts;
    }

    public void setRequiredRepairParts(int requiredRepairParts) {
        this.requiredRepairParts = requiredRepairParts;
    }

    public Facility getBreakdownLocation() {
        System.out.println("Breakdown Location: "+this.breakdownLocation);
        return breakdownLocation;
    }

    public void setBreakdownLocation(Facility breakdownLocation) {
        this.breakdownLocation = breakdownLocation;
    }

    public boolean isRepairStatus() {
        return repairStatus;
    }

    public void setRepairStatus(boolean repairStatus) {
        this.repairStatus = repairStatus;
    }

    public int getRequiredNumberOfResponder() {
        System.out.println("Required Number Of Responders: "+this.requiredNumberOfResponder);
        return requiredNumberOfResponder;
    }

    public void setRequiredNumberOfResponder(int requiredNumberOfResponder) {
        this.requiredNumberOfResponder = requiredNumberOfResponder;
    }

    public int getAvailableSpareParts() {
        System.out.println("Available Spare Parts: "+this.availableSpareParts);
        return availableSpareParts;
    }

    public void setAvailableSpareParts(int availableSpareParts) {
        this.availableSpareParts = availableSpareParts;
    }
    public boolean checkRepairParts(){
        if(availableSpareParts>=requiredRepairParts)
            return true;
        return false;
    }
    
}
