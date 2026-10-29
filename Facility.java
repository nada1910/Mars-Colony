package com.mycompany.nada;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public abstract class Facility implements Location {

    private final int id;
    private String name;
    private final int capacity;
    private final FacilityType type;
    private final List<Equipment> equipments = new ArrayList<>();
    private ArrayList<Resources> resources;
    private List<Agent> occupants = new ArrayList<>();
    private OperationalStatus operationalStatus;

  
       public Facility(int id, String name, FacilityType type, int capacity, OperationalStatus operationalStatus) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid Id");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (type == null) {
            throw new IllegalArgumentException("Invalid Facility Type");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Invalid Capacity");
        }
        if (operationalStatus == null) {
            throw new IllegalArgumentException("Invalid Status");
        }
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.type = type;
        this.operationalStatus = operationalStatus;
    }
    //resources

    public ArrayList<Resources> getResources() {
        System.out.println("Resources: " + this.resources);
        return this.resources;

    }

    public boolean setResources(ArrayList<Resources> resources) {
        if (resources == null) {
            System.out.println("Invalid Resources");
            return false;

        } else {
            this.resources = resources;
            System.out.println("Resources Set Successfuly");
            return true;
        }

    }
     public int getQuantity(ResourcesType type) {
        for (Resources r : resources) {
            if (r.getResource() == type) {
                return r.getQuantity();
            }
        }
        return 0;
    }
      public boolean addResource(ResourcesType type, int amount) {
        if (type == null || amount <= 0) {
            return false;
        }
        for (Resources r : resources) {
            if (r.getResource() == type) {
                return r.add(amount);
            }
        }
        resources.add(new Resources(type, amount));
        return true;
    }

    public boolean consumeResource(ResourcesType type, int amount) {
        if (type == null) {
            return false;
        }
        for (Resources r : resources) {
            if (r.getResource() == type) {
                return r.consume(amount);
            }
        }
        return false;
    }

    //setters and getters
    public int getId() {
        System.out.println("Id: " + this.id);
        return id;
    }

    public FacilityType getType() {
        System.out.println("Facility Type:" + type);
        return type;
    }

    @Override
    public String getLocationName() {
        return name;
    }

    public String getName() {
        System.out.println("Name: " + this.name);
        return name;
    }

    public boolean setName(String name) {
        if (name == null) {
            System.out.println("Invalid Name");
            return false;
        }
        System.out.println("Name Set Successfuly");
        this.name = name;
        return true;
    }

    //Equipment
    public List<Equipment> getEquipments() {
        return Collections.unmodifiableList(equipments);
    }

    public boolean addEquipment(Equipment equipment) {
        if (equipment == null || equipments.contains(equipment)) {
            return false;
        }
        equipments.add(equipment);
        return true;
    }

    public boolean removeEquipment(Equipment equipment) {
        return equipments.remove(equipment);
    }

    public int damageAllEquipment() {
        int count = 0;
        for (Equipment e : equipments) {
            if (!e.isDamaged()) {
                e.damage();
                count++;
            }
        }
        return count;
    }
    //Occupants

    public int getCapacity() {
        System.out.println("Capacity:"+this.capacity);
        return capacity;
    }

    public boolean isFull() {
        return occupants.size() >= capacity;
    }

    public List<Agent> getOccupants() {
        System.out.println("Occupants: " + this.occupants);
        return Collections.unmodifiableList(occupants);
    }

    public void setOccupants(ArrayList<Agent> Occupants) {
        if (Occupants == null) {
            System.out.println("Invalid Occupants");
        } else {
            System.out.println("Occupants Set Successfuly");
            this.occupants = Occupants;
        }
    }

    public boolean addOccupants(Agent o) {
        if (o == null) {
            System.out.println("Invalid Occupants");
            return false;
        } else {
            System.out.println("Occupants Added Successfuly");
            occupants.add(o);
            return true;
        }
    }

    public boolean removeOccupant(Agent agent) {
        return occupants.remove(agent);
    }

   

    //operational status
    public OperationalStatus getOperationalStatus() {
        System.out.println("Status: " + this.operationalStatus);
        return operationalStatus;
    }

    public boolean setOperationalStatus(OperationalStatus operationalStatus) {
        if (operationalStatus == null) {
            System.out.println("Invalid Status");
            return false;

        } else {
            System.out.println("Status Set Successfuly");
            this.operationalStatus = operationalStatus;
            return true;
        }

    }

    public boolean isOperational() {
        if (operationalStatus == OperationalStatus.OPERATIONAL) {
            System.out.println("Facility Is Operational");
            return true;
        }
        System.out.println("Facility Is Not Operational");
        return false;

    }
//time
    public void advanceTime() {
    }
}
