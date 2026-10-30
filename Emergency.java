/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
abstract class Emergency {
    private final int id;
    private final EmergencyType type;
    private final FacilityType location;
    private final String description;
    private EmergencyStatus status = EmergencyStatus.DETECTED;
    private final List<Agent> responders = new ArrayList<>();
    protected List<Agent> allAgents = new ArrayList<>(); 

    public Emergency(int id, EmergencyType type,FacilityType location,String description) {
        if(id<=0){
            throw new IllegalArgumentException("Invalid Id");
        }
        if(type==null){
            throw new IllegalArgumentException("Invalid Type");
        }
         
        if(location==null){
            throw new IllegalArgumentException("Invalid Location");
        }
        if(description==null){
            throw new IllegalArgumentException("Invalid Description");
        }
        
        
        this.id = id;
        this.type = type;
        this.location = location;
        this.description = description;
    }
    public void handleEmergency(){
        
    }
     public void setAllAgents(List<Agent> agents) {
        this.allAgents = (agents == null) ? new ArrayList<Agent>() : agents;
    }
   
    public boolean resolveEmergency(){
        this.status=EmergencyStatus.RESOLVED;
        return true;
    }
    public List<Agent> responders() {
    return Collections.unmodifiableList(responders);
}
    public int getId() {
        System.out.println("Emergency Id: "+this.id);
        return id;
    }

    public EmergencyType getType() {
        System.out.println("Emergency Id: "+this.type);
        return type;
    }

    public FacilityType getLocation() {
        System.out.println("Emergency Id: "+this.id);
        return location;
    }

    public void setStatus(EmergencyStatus status) {
        this.status = status;
    }

    public String getDescription() {
        System.out.println("Emergency Description: "+this.description);
        return description;
    }

    public EmergencyStatus getStatus() {
        System.out.println("Emergency Status: "+this.status);
        return status;
    }

    public List<Agent> getResponders(List<Agent> allAgents) {
         responders.clear();
        for(Agent agent :allAgents ){
            if(agent.isAvailable() && agent.canRespondTo(this)){
                responders.add(agent);
            }
                
        }
        System.out.println("Emergency Responders: "+this.responders);
        return responders;
    }
    abstract public boolean hasRequiredResponders();
    
}
