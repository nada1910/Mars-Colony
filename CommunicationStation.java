/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;


public class CommunicationStation extends Facility {
    private boolean signalDisturbed;

      public CommunicationStation(int id, String name,int capacity,OperationalStatus operationalStatus) {
        super(id, name,FacilityType.COMMUNICATION,capacity, operationalStatus);
        
    }

    public boolean isDisturbed(){
        if(signalDisturbed){
            System.out.println("Signal Is Disturbed");
            return true;
        }
        System.out.println("Signal Is Not Disturbed");
        return false;
    }
    public void disturbSignal(){
        this.signalDisturbed=true;
        System.out.println("Signal Is Disturbed");
    }
    
    public boolean isLinkActive() {
        return isOperational() && !signalDisturbed;
    }
    public void restoreSignal(){
        this.signalDisturbed=false;
        System.out.println("Signal Is Restored");
    }
}

