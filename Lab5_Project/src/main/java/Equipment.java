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
        return name;
    }

    public boolean setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Invalid Name");
            return false;
        }
        this.name = name;
        return true;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean setQuantity(int quantity) {
        if (quantity < 0) {
            System.out.println("Invalid Quantity");
            return false;
        }
        this.quantity = quantity;
        return true;
    }

    public boolean isDamaged() {
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
    return !damaged && quantity > 0;
}


}
