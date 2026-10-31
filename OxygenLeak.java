/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;

public class OxygenLeak extends Emergency {

    private boolean leak;//true for leak and false after being repaired
    private final Habitat affectedHabitat;
    private final double oxygenLoss;

    public OxygenLeak(boolean leak, Habitat affectedHabitat, double oxygenLoss, int id, EmergencyType type, FacilityType location, String description) {
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
     public void detect() {
        affectedHabitat.setLeaking(true);
        deduceOxygen(oxygenLoss);
        setStatus(EmergencyStatus.RESPONDING);
    }

    public boolean deduceOxygen(double oxygenLoss) {
        if (oxygenLoss <= 0) {
            return false;
        }

        affectedHabitat.reduceOxygen((int)oxygenLoss);
        return true;
    }

    public boolean repairLeak() {
        if (!leak) {
            System.out.println("Oxygen leak already repaired");
            return false;
        }
        if (!hasRequiredResponders()) {
            //second deduction
            deduceOxygen(oxygenLoss);
            this.setStatus(EmergencyStatus.UNRESOLVED);
            System.out.println("Oxygen leak Not repaired ");
            return false;
        }

        leak = false;
        affectedHabitat.stopLeak();
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
