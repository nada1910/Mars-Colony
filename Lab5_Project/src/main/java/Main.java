import java.util.List;
import java.util.Map;

public class Main {

    // everything the colony needs, kept here so every scenario can use it
    static Environment env;
    static Colony colony;
    static Simulation sim;

    static Habitat habitat;
    static StorageFacility storage;
    static Laboratory lab;

    static Scientist scientist;
    static Engineer engineer;
    static MedicalOfficer medic;
    static ExplorationRover rover;
    static RepairRobot repairRobot;

    static Equipment drill;
    static MarsSurface surface;

    // creates the objects and builds the colony
    static void setup() {
        env = new Environment();
        surface = new MarsSurface("Mars Surface");

        // Facilites
        habitat = new Habitat(1, "Habitat A", 5, OperationalStatus.OPERATIONAL, 1);
        habitat.addResource(ResourcesType.OXYGEN, 300);

        storage = new StorageFacility(1000, 2, "Storage", 5, OperationalStatus.OPERATIONAL);
        storage.addResource(ResourcesType.OXYGEN, 200);
        storage.addResource(ResourcesType.SPARE_PARTS, 10);
        storage.addResource(ResourcesType.BATTERY_POWER, 50);
        storage.addResource(ResourcesType.MEDICAL_SUPPLIES, 10);

        lab = new Laboratory(3, "Laboratory", 5, OperationalStatus.OPERATIONAL);

        // crew 
        scientist = new Scientist(1, "Dr. Amr", habitat);
        scientist.addSkill("Geology");
        engineer = new Engineer(2, "Eng. Farah", habitat);
        medic = new MedicalOfficer(3, "Dr. Nada", habitat);

        // robots
        rover = new ExplorationRover(1, "Rover-1", habitat, Robot.OPERATIONAL);
        repairRobot = new RepairRobot(2, "Fixer-1", habitat, Robot.OPERATIONAL);

        // the rover carries the equipment the mission needs
        drill = new Equipment("Drill", 1, false);
        rover.carry(drill);

        // put everything in the colony, then start the simulation
        colony = new Colony(env,
                List.of(habitat, storage, lab),
                List.of(scientist, engineer, medic),
                List.of(rover, repairRobot));
        sim = new Simulation(env, colony);
    }

    // makes an exploration mission
    static ExplorationMission makeMission(int id, int duration, Map<ResourcesType, Integer> cost) {
        return new ExplorationMission(id, duration, List.of(Agent.EXPLORE),
                cost, List.of(), "Valles Marineris", List.of(drill));
    }

    static void title(String text) {
        System.out.println();
        System.out.println("========== " + text + " ==========");
    }

    // First: everything is correct so the mission should finish
    static void scenario1() {
        title("SCENARIO 1: Successful Exploration");

        rover.moveTo(surface);   // exploration happens on the surface

        System.out.println("--- Before ---");
        colony.statusReport();

        ExplorationMission m = makeMission(1, 3, Map.of(ResourcesType.BATTERY_POWER, 5));
        colony.submitMission(m, List.of(scientist, rover));

        sim.advanceTime(3);      // the mission lasts 3 time units

        System.out.println("--- After ---");
        colony.statusReport();
    }

    // Second: an engineer can't explore so the mission must fail
    static void scenario2() {
        title("SCENARIO 2: Mission Rejection");

        ExplorationMission m = makeMission(2, 3, Map.of());
        colony.submitMission(m, List.of(engineer, rover));

        System.out.println(">>> Mission cannot be assigned.");
        System.out.println(">>> Reason: " + m.getFailureReason());
    }

    // Third: the mission asks for more battery than we have
    static void scenario3() {
        title("SCENARIO 3: Resource Shortage");

        System.out.println(">>> Battery before: " + colony.getTotal(ResourcesType.BATTERY_POWER));

        ExplorationMission m = makeMission(3, 3, Map.of(ResourcesType.BATTERY_POWER, 500));
        colony.submitMission(m, List.of(scientist, rover));

        System.out.println(">>> Mission cannot be assigned.");
        System.out.println(">>> Reason: " + m.getFailureReason());
        System.out.println(">>> Battery after: " + colony.getTotal(ResourcesType.BATTERY_POWER)
                + " (nothing was used)");
    }

    // Fourth: oxygen leak in Habitat A
    static void scenario4() {
        title("SCENARIO 4: Oxygen Leak");

        // The leak can be repaired
        System.out.println(">>> Oxygen in habitat before: " + habitat.getOxygen());
        System.out.println(">>> Spare parts before: " + colony.getTotal(ResourcesType.SPARE_PARTS));

        OxygenLeak leak = new OxygenLeak(true, habitat, 30, 1,
                EmergencyType.OXYGEN_LEAK, FacilityType.HABITAT, "Leak in Habitat A");
        colony.handleEmergency(leak);

        System.out.println(">>> Oxygen in habitat after: " + habitat.getOxygen() + " (lost 30 once)");
        System.out.println(">>> Spare parts after: " + colony.getTotal(ResourcesType.SPARE_PARTS)
                + " (2 were used)");
        System.out.println(">>> Still leaking? " + habitat.isLeaking());

        // We remove the spare parts so the repair is impossible
        System.out.println();
        System.out.println("--- Same leak but with no spare parts ---");
        storage.consumeResource(ResourcesType.SPARE_PARTS,
                storage.getQuantity(ResourcesType.SPARE_PARTS));

        int before = habitat.getOxygen();
        OxygenLeak leak2 = new OxygenLeak(true, habitat, 30, 2,
                EmergencyType.OXYGEN_LEAK, FacilityType.HABITAT, "Second leak in Habitat A");
        colony.handleEmergency(leak2);

        System.out.println(">>> Oxygen lost: " + (before - habitat.getOxygen())
                + " (30 + 30, unresolved costs double)");
        System.out.println(">>> Reason: " + leak2.getUnresolvedReason());
    }

    // Fifth: dust storm
    static void scenario5() {
        title("SCENARIO 5: Dust Storm");

        // The storm stops a new mission from starting
        env.setCondition(Environment.DUST_STORM);
        System.out.println(">>> Environment: " + env.getCondition());

        ExplorationMission m = makeMission(5, 3, Map.of());
        colony.submitMission(m, List.of(scientist, rover));

        System.out.println(">>> Exploration Mission cannot start.");
        System.out.println(">>> Reason: " + m.getFailureReason());

        // A storm that comes while a mission is already running
        System.out.println();
        System.out.println("--- Storm during a running mission ---");
        env.setCondition(Environment.NORMAL);

        ExplorationMission running = makeMission(6, 5, Map.of());
        colony.submitMission(running, List.of(scientist, rover));
        sim.advanceTime(1);                        // runs normally
        env.setCondition(Environment.DUST_STORM);  // storm arrives
        sim.advanceTime(1);                        // mission fails on this tick

        System.out.println(">>> Running mission failed.");
        System.out.println(">>> Reason: " + running.getFailureReason());
        System.out.println(">>> Rover available again? " + rover.isAvailable());
    }

    public static void main(String[] args) {
        setup();
        scenario1();
        scenario2();
        scenario3();
        scenario4();
        scenario5();
    }
}