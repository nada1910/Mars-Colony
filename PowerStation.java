/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;

public class PowerStation extends Facility{
    private int powerOutput;
    public PowerStation(int id, String name, int capacity, OperationalStatus status,int powerOutput) {
        super(id, name, FacilityType.POWER_STATION, capacity, status);
        if (powerOutput < 0) {
            throw new IllegalArgumentException("Invalid Power Output");
        }
        this.powerOutput = powerOutput;
    }
    public int getBatteryPower() {
        System.out.println("Battery Power Of Power Station: "+getQuantity(ResourcesType.BATTERY_POWER));
        return getQuantity(ResourcesType.BATTERY_POWER);
    }
    
    public boolean supplyPower(Facility target, int amount) {
        if (target == null || target == this || !target.isOperational() || amount <= 0 ||!isOperational()) {
            return false;
        }
        if (!consumeResource(ResourcesType.BATTERY_POWER, amount)) {
            return false;   // not enough power in this station
        }
        if (!target.addResource(ResourcesType.BATTERY_POWER, amount)) {
            addResource(ResourcesType.BATTERY_POWER, amount); 
            return false;
        }
        return true;
    }
  
    public boolean generate(){
        if (!isOperational()) {
            return false;
        }
        if (powerOutput <= 0) {
            return false;
        }
        return addResource(ResourcesType.BATTERY_POWER, powerOutput);
    }
    @Override
public void advanceTime() {
    generate();
}

}
