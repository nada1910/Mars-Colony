/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;



public class StorageFacility extends Facility {

    private final int storageCapacity;

   public StorageFacility(int storageCapacity, int id, String name, int capacity, OperationalStatus operationalStatus) {
        super(id, name, FacilityType.STORAGE, capacity, operationalStatus);
        if (storageCapacity < 0) {
            throw new IllegalArgumentException("Invalid storage capacity");
        }
        this.storageCapacity = storageCapacity;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public int getFreeStorage() {
        return storageCapacity - getUsedStorage();
    }

    public int getUsedStorage() {
        int used = 0;
        for (Resources s : getResources()) {
            used += s.getQuantity();
        }
        return used;
    }

    @Override
    public boolean addResource(ResourcesType resource, int amount) {
        if (amount > getFreeStorage()) {
            return false;   // not enough storage space
        }
        return super.addResource(resource, amount);
    }

}
