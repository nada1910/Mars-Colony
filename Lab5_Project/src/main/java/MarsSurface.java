public class MarsSurface implements Location {
    private String name;

    public MarsSurface(String name){
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid name");
        }
        this.name = name;}

    public String getLocationName(){
        return this.name;
    }
}
