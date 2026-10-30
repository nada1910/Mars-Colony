/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;

public class OxygenLeak extends Emergency {

    private boolean leak;//true for leak and false after being repaired
    private final Habitat affectedHabitat;
    private final int oxygenLoss;

    public OxygenLeak(boolean leak, Habitat affectedHabitat, int oxygenLoss, int id, EmergencyType type, FacilityType location, String description) {
        super(id, type, location, description);
        if (affectedHabitat == null) {
            throw new IllegalArgumentException("Invalid Habitat");
        }
        if (oxygenLoss <= 0) {
            throw new IllegalArgumentException("Invalid Oxygen Loss");
        }
        this.oxygenLoss = oxygenLoss;
        this.leak = leak;
        this.affectedHabitat = affectedHabitat;

    }

    public boolean isLeak() {
        return leak;
    }

    public boolean deduceOxygen(int oxygenLoss) {
        if (oxygenLoss <= 0) {
            return false;
        }

        affectedHabitat.reduceOxygen(oxygenLoss);
        return true;
    }

    public boolean repairLeak() {
        if (!leak || !hasRequiredResponders()) {
            deduceOxygen(10);
           this.setStatus(EmergencyStatus.UNRESOLVED);
           System.out.println("Oxygen leak Not repaired ");
            return false;
        }

        leak = false;
        resolveEmergency();
        System.out.println("Oxygen leak repaired successfully");
        return true;
    }

    @Override
    public boolean hasRequiredResponders() {
        return !responders().isEmpty();
    }

    public Habitat getAffectedHabitat() {
        System.out.println("Affected Habitat: " + this.affectedHabitat);
        return affectedHabitat;
    }

   

}
