
package com.mycompany.nada;


public class Equipment {
    
    private String name;
    private int quantity;
    private boolean damaged;

    public Equipment(String name, int quantity, boolean damaged) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Invalid Quantity");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid name");
        }
        this.name = name;
        this.quantity = quantity;
        this.damaged=damaged;
    }

    public String getName() {
        System.out.println("Name :"+this.name);
        return name;
    }

    public boolean setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Invalid Name");
            return false;
        }
        this.name = name;
        System.out.println("Name Set Successfuly");
        return true;
    }

    public int getQuantity() {
        System.out.println("Quantity :"+this.quantity);
        return quantity;
    }

    public boolean setQuantity(int quantity) {
        if (quantity < 0) {
            System.out.println("Invalid Quantity");
            return false;
        }
        this.quantity = quantity;
        System.out.println("Quantity Set Successfuly");
        return true;
    }

    public boolean isDamaged() {
        if(damaged){
            System.out.println("Equipment Is Damaged ");
        }
        System.out.println("Equipment Not Damaged");
        return damaged;
    }


    public void damage() {
        this.damaged = true;
        System.out.println("Equipment Is Damaged");
    }

    public void repair() {
        System.out.println("Equipment Under Repair");
        this.damaged = false;
    }

    public boolean isUsable() {
        if (!this.damaged && this.quantity>0 ) {
            System.out.println("Equipment Is Usable");
            return true;
        } else {
            System.out.println("Equipment Is Not Usable");
            return false;
        }
    }


}
