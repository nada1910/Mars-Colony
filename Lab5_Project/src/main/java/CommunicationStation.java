public class CommunicationStation extends Facility {
    private boolean signalDisturbed;

      public CommunicationStation(int id, String name,int capacity,OperationalStatus operationalStatus) {
        super(id, name,FacilityType.COMMUNICATION,capacity, operationalStatus);
        
    }

    public boolean isDisturbed() { return signalDisturbed; }
    
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

