public class MedicalEmergency extends Emergency {

    private int requiredNumberOfResponder;
    private final CrewMember patient;
    private final Facility affectedFacility;
    private int healthRecovery;
    private boolean treatmentSuccessful;
    private int medicalSuppliesRequired;

    public MedicalEmergency(int requiredNumberOfResponder, CrewMember patient, Facility affectedFacility, int medicalSuppliesRequired, int id, EmergencyType type, FacilityType location, String description) {
        super(id, type, location, description);

        if (patient == null) {
            throw new IllegalArgumentException("Invalid Patient");
        }
        if (requiredNumberOfResponder < 0) {
            throw new IllegalArgumentException("Invalid Number Of Responders");
        }
        if (affectedFacility == null) {
            throw new IllegalArgumentException("Invalid Facility");
        }
        if (medicalSuppliesRequired < 0) {
            throw new IllegalArgumentException("Invalid Medical Supplies Number");
        }
        this.requiredNumberOfResponder = requiredNumberOfResponder;
        this.patient = patient;
        this.affectedFacility = affectedFacility;
        this.medicalSuppliesRequired = medicalSuppliesRequired;
    }

    public int getHealthRecovery() {
        return healthRecovery;
    }

    public void setHealthRecovery(int healthRecovery) {
        this.healthRecovery = healthRecovery;
    }

    public boolean isTreatmentSuccessful() {
        if (treatmentSuccessful) {
            System.out.println("Treatment Successful");
        } else {
            System.out.println("Treatment Not Successful");
        }
        return treatmentSuccessful;
    }

    public void setTreatmentSuccessful(boolean treatmentSuccessful) {
        this.treatmentSuccessful = treatmentSuccessful;
    }

    public int getMedicalSuppliesRequired() {
        return medicalSuppliesRequired;
    }

    public void setMedicalSuppliesRequired(int medicalSuppliesRequired) {
        this.medicalSuppliesRequired = medicalSuppliesRequired;
    }

    public boolean checkMedicalSupplies() {
        if(affectedFacility.getQuantity(ResourcesType.MEDICAL_SUPPLIES)
                >= this.medicalSuppliesRequired){
        
            return true;
        }return false;
    }

    public boolean treatPatient() {
        if (!affectedFacility.isOperational()) {
            treatmentSuccessful = false;
            setStatus(EmergencyStatus.UNRESOLVED);
            System.out.println("Facility is not operational");
            return false;
        }
        if (hasRequiredResponders() && checkMedicalSupplies()) {
            treatmentSuccessful = true;
            resolveEmergency();
            patient.heal(5);
            affectedFacility.consumeResource(ResourcesType.MEDICAL_SUPPLIES, medicalSuppliesRequired);
            System.out.println("Treatment Successful");
            return true;
        } else {
            treatmentSuccessful = false;
            patient.injure(5);
            System.out.println("Treatment Not Successful Because Of Lack of Medical Supplies Or Responders");
            this.setStatus(EmergencyStatus.UNRESOLVED);
            
            return false;
        }
    }

    @Override
    public boolean hasRequiredResponders() {
        int responderCount = getResponders(allAgents).size();
        if (responderCount >= this.requiredNumberOfResponder) {
            
            return true;
        }
        return false;
    }

    public int getRequiredNumberOfResponder() {
        return requiredNumberOfResponder;
    }

    public void setRequiredNumberOfResponder(int requiredNumberOfResponder) {
        this.requiredNumberOfResponder = requiredNumberOfResponder;
    }

    @Override
public String checkPreconditions() {
    return affectedFacility.isOperational() ? null : "Facility is not operational";
}

@Override
public void applyUnresolvedPenalty() {
    treatmentSuccessful = false;
    patient.injure(5);
}

    @Override
    public void resolve() {
        treatmentSuccessful = true;
        healthRecovery = 5;
        patient.heal(healthRecovery);
        resolveEmergency();
    }

    @Override
    public Facility getAffectedFacility() { return affectedFacility; }

    @Override
    public java.util.Map<ResourcesType, Integer> getRequiredResources() {
        java.util.Map<ResourcesType, Integer> r = new java.util.EnumMap<>(ResourcesType.class);
        r.put(ResourcesType.MEDICAL_SUPPLIES, medicalSuppliesRequired);
        return r;
}
    

}
