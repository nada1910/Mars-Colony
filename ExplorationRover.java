public class ExplorationRover extends Robot {
    private static final int CAPACITY = 5;
    private Equipment[] cargo = new Equipment[CAPACITY];
    private int cargoCount = 0;
    
    public ExplorationRover(int id, String name, Location location, String status)
    {
        super(id, name, location, status);
    }
    
    public int getCargoCount() { return cargoCount; }
    
    public boolean carry(Equipment equipment)
    {
        if (equipment == null) return false;
        if(cargoCount >= CAPACITY)
        {
            System.out.println(getName() + ": cargo is full");
            return false;
        }
        cargo[cargoCount] = equipment;
        cargoCount++;
        return true;
    }
    
    public boolean hasEquipment(String equipmentName) 
    {
    for (int i = 0; i < cargoCount; i++) {
        if (cargo[i].getName().equals(equipmentName) && cargo[i].isUsable()) {
            return true;
        }
    }
    return false;
    }
    
    @Override
    public boolean canPerform(Mission mission) {
        if(mission.requires(Agent.EXPLORE) || mission.requires(Agent.RESCUE))
            return true;
        return false;
}
    
}
