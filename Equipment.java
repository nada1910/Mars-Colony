package lab5;
public class Equipment {
    private String name;
    private int quantity;
    private boolean damaged;
    public Equipment(String name,int quantity){
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (quantity<0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        this.name = name;
        this.quantity = quantity;
        this.damaged = false;
    }
    public String getName(){
        System.out.println("Name of equipment"+name);
        return this.name;
    }
    public int getQuantity(){
        System.out.println("Quantity of equipment"+this.quantity);
        return this.quantity;
    }
    public void setName(String nName){
        if (nName == null || nName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid name");
        }
        else{
            this.name = nName;
            System.out.println("Name of equipment"+name);
        }
    }
    public void setQuantity(int newq){
        if(newq<0){
            throw new IllegalArgumentException("Invalid quantity");
        }
        else{
            this.quantity = newq;
            System.out.println("Quantity:"+newq);
        }
    }
    public void damage(){
        damaged = true;
        System.out.println("Equipment Is Damaged");
    }
    public void repair(){
        damaged = false;
        System.out.println("Equipment Is repaired");
    }
    public boolean isUsable(){
        if(damaged == false){
            System.out.println("Equipment is usable");
            return true;
        }
        else{
            System.out.println("Equipment is not usable");
            return false;
        }
    }
    
}
