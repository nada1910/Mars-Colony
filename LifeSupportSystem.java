/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;

public class LifeSupportSystem {
    private boolean status=true; //true =working , false= not working
    private int consumptionRate;
    private int airQuality=100;//0 needs filtering 100 dont

    public LifeSupportSystem( boolean status, int consumptionRate) {
        if (consumptionRate < 0) {
            throw new IllegalArgumentException("Invalid Consumption Rate");
        }
        this.status=status;
        this.consumptionRate = consumptionRate;
       
    }

    public boolean isStatus() {
        if (status) {
            System.out.println("Life Support System Working Sucessfuly");
            return status;
        }
        System.out.println("Life Support System Not Working");
        return status;
    }

    public int getAirQuality() {
        return airQuality;
    }

    public int getConsumptionRate() {
        System.out.println("Consumption Rate :"+this.consumptionRate);
        return consumptionRate;
    }

    public boolean setConsumptionRate(int consumptionRate) {
        if (consumptionRate < 0) {
            System.out.println("Invalid Number");
            return false;
        }
        this.consumptionRate = consumptionRate;
        System.out.println("Consumption Rate Chnaged Successfuly");
        return true;
    }

    public boolean increaseConsumptionRate(int rate) {
        if (rate > 0) {
            this.consumptionRate += rate;
            System.out.println("Consumption Rate Increased Sucessfuly ");
            return true;

        }
        System.out.println("Consumption Rate Didn't Change ");
        return false;
    }

    public boolean filterAir() {
        if (!status) {
            System.out.println("System Is Not Working, Could Not Filter Air");
            return false;
        }
        else if(airQuality>50){
        System.out.println("Air Doesn't Need Filtering");
        return false;}
        airQuality=100;
        System.out.println("Air Filtered");
        return true;
    }
    public void restore(){
        this.status=true;
        System.out.println("System Restored Successfuly");
    }
    public void tick() {
    if (status) airQuality = Math.min(100, airQuality + 5);
    else airQuality = Math.max(0, airQuality - 10);
}

}
