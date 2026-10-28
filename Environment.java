public class Environment {
    public static final String NORMAL = "NORMAL";
    public static final String DUST_STORM = "DUST_STORM";
    public static final String COMM_DISRUPTION = "COMM_DISRUPTION";
    
    private String condition = NORMAL;
    
    public String getCondition() {return condition;}
    
    public boolean setCondition(String condition)
    {
        if(NORMAL.equals(condition) || DUST_STORM.equals(condition) || COMM_DISRUPTION.equals(condition))
        {
            this.condition = condition;
            return true;
        }
        return false;
    }
    
    public boolean allowsSurfaceOperations()
    {
        if (condition.equals(DUST_STORM))
            return false;
        return true;
    }

    public boolean allowsCommunication() {
        if (condition.equals(COMM_DISRUPTION))
            return false;
        return true;
}
}
