/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;

public class Laboratory extends Facility {

    private boolean onFire;
 

    public Laboratory(int id, String name, int capacity, OperationalStatus status) {
        super(id, name, FacilityType.LABORATORY, capacity, status);
    }

    public boolean setFire() {
        if (getOperationalStatus() == OperationalStatus.DESTROYED || onFire) {
            System.out.println("Already On Fire");
            return false;
        }
        onFire = true;
        System.out.println("System Is On Fire");
        damageAllEquipment();
        return true;
    }

    public boolean isOnFire() {

        if (onFire) {
            System.out.println("Laboratory On Fire ");
            return true;
        } else {
            System.out.println("Laboratory Not On Fire");
            return false;
        }
    }

    public boolean availability() {
        if (onFire) {
            System.out.println("Laboratory Unavailable Beacuse of A Fire ");
            return false;
        } else {
            System.out.println("Laboratory Available");
            return true;
        }
    }

    public boolean extinguish() {
        if (onFire) {
            onFire = false;
            System.out.println("Laboratory Extinguished");
            setOperationalStatus(OperationalStatus.UNDER_MAINTENANCE);
            return true;
        }
        System.out.println("Laboratory Not On Fire");
        return false;

    }

    public boolean hasUsableEquipment(String equipmentName) {
        for (Equipment e : getEquipments()) {
            if (e.getName().equalsIgnoreCase(equipmentName) && e.isUsable()) {
                return true;
            }
        }
        return false;

    }
    @Override
    public void advanceTime(){
        if(onFire){
            consumeResource(ResourcesType.OXYGEN,3);//use when fire
        }
        else consumeResource(ResourcesType.OXYGEN,1);//normal use
        
    }

}
