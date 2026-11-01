public class Simulation {
    private int currentTime;
    private Environment env;
    private Colony colony;

    public Simulation(Environment env, Colony colony)
    {
        if (env == null || colony == null)
            throw new IllegalArgumentException("Invalid simulation data");
        this.currentTime = 0;
        this.env = env;
        this.colony = colony;
    }

    public void advanceTime(int n) {
        if (n <= 0) {
            System.out.println("Time must advance by at least 1 unit");
            return;
        }
        for (int i = 0; i < n; i++) {
            currentTime++;
            System.out.println("Time unit: " + currentTime);
            colony.tick();
        }
    }

    public int getCurrentTime() {return currentTime;}
    public Environment getEnvironment() {return env;}
    public Colony getColony() {return colony;}
}