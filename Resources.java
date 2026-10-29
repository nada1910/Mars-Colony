/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nada;


import java.util.List;
import java.util.Map;
import java.util.EnumMap;

public class Resources {

    private final ResourcesType resource;
    private int quantity;

    public Resources(ResourcesType resource, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Invalid Quantity");
        }
        if (resource == null) {
            throw new IllegalArgumentException("Invalid Resources");
        }
        this.resource = resource;
        this.quantity = quantity;
    }

    public ResourcesType getResource() {
        return resource;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean setQuantity(int quantity) {
        if (quantity < 0) {
            System.out.println("Quantity Should be A Non Negative Number");
            return false;
        }
        this.quantity = quantity;
        System.out.println("Quantity Set Successfuly");
        return true;
    }

    //add specific resource
    public boolean add(int amount) {
        if (amount <= 0) {
            System.out.println("Invalid Amount");
            return false;
        }
        this.quantity += amount;
        System.out.println("Amount Added Successfuly");
        return true;
    }
    //consume one specific resource
    public boolean consume(int amount) {
        if (amount < 0 || amount > quantity) {
            System.out.println("Can't Consume More, Enter A Valid Amount");
            return false;
        }
        quantity -= amount;
        System.out.println("Amount Consumed Successfuly");
        return true;
    }
    //total of one resource for all facilities
    public static int getColonyTotal(List<Facility> facilities, ResourcesType resource) {
        int total = 0;
        for (Facility f : facilities) {
            if (f.isOperational()) {
                total += f.getQuantity(resource);
            }
        }
        return total;
    }
    //total for every resource type
    public static Map<ResourcesType, Integer> getColonyTotals(List<Facility> facilities) {
        Map<ResourcesType, Integer> totals = new EnumMap<>(ResourcesType.class);
        for (ResourcesType r : ResourcesType.values()) {
            totals.put(r, getColonyTotal(facilities, r));
        }
        return totals;
    }

    public static boolean canSupply(List<Facility> facilities, Map<ResourcesType, Integer> request) {
        if (request == null) {
            return false;
        }
        for (Map.Entry<ResourcesType, Integer> e : request.entrySet()) {
            int needed = e.getValue();
            if (needed <= 0 || getColonyTotal(facilities, e.getKey()) < needed) {
                return false;
            }
        }
        return true;
    }

     public static boolean consume(
            List<Facility> facilities,
            Map<ResourcesType, Integer> request) {

        if (!canSupply(facilities, request)) {
            return false;
        }

        for (ResourcesType r : request.keySet()) {
            int remaining = request.get(r);

            // First use storage facilities, then other facilities
            for (int pass = 0; pass < 2; pass++) {
                for (Facility f : facilities) {

                    boolean storage =
                            f.getType() == FacilityType.STORAGE;

                    if (f.isOperational()
                            && storage == (pass == 0)
                            && remaining > 0) {

                        int amount = Math.min(
                                remaining, f.getQuantity(r));

                        if (amount > 0) {
                            f.consumeResource(r, amount);
                            remaining -= amount;
                        }
                    }
                }
            }
        }

        return true;
    }

}
