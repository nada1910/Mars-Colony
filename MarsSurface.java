package lab5;

public class MarsSurface implements Location {
    private String name;

    public MarsSurface(String name){
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid name");
        }
        this.name = name;}

    public String getLocationName(){
        System.out.println("Location name"+this.name);
        return this.name;
    }
}
