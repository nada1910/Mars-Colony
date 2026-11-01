# MARS-OPS: Mars Colony Simulator

MARS-OPS simulates a Mars colony. The colony has people and robots that do missions and respond to emergencies, facilities that store resources, and an environment that can make some work impossible. The simulation clock moves time forward one unit at a time.

The project is split into three parts, each written by a different team member.

| Part | Author | Covers |
|---|---|---|
| **Part 1: Agents, Environment & Simulation** | Farah | `Agent`, `Location`, `CrewMember`, `Scientist`, `Engineer`, `MedicalOfficer`, `Robot`, `ExplorationRover`, `RepairRobot`, `Drone`, `Environment`, `Simulation` |
| **Part 2: Facilities, Resources & Emergencies** | Nada | `Facility` and its subclasses, `LifeSupportSystem`, `Equipment`, `Resources`, `Emergency` and its subclasses, all enums |
| **Part 3: Missions, Colony, Equipment & Mars Surface** | Amr | `Mission` and its four subclasses, `Colony`, `Equipment`, `MarsSurface` |

## Table of Contents

1. [System Overview](#system-overview)
2. [Part 1: Agents, Environment & Simulation](#part-1-agents-environment--simulation)
3. [Part 2: Facilities, Resources & Emergencies](#part-2-facilities-resources--emergencies)
4. [Part 3: Missions, Colony, Equipment & Mars Surface](#part-3-missions-colony-equipment--mars-surface)
5. [Class Relationships](#class-relationships)
6. [How to Compile and Run](#how-to-compile-and-run)
7. [Demonstration Scenarios](#demonstration-scenarios)

---

## System Overview

| Class / Area | Job |
|---|---|
| `Agent` | The contract that every person and robot follows |
| `Location` | Anything that can be "where someone is" (a facility or the Martian surface) |
| `CrewMember` + 3 roles | Humans with health, fatigue and skills |
| `Robot` + 3 types | Machines with battery and a status |
| `Environment` | Holds the current weather condition and answers what is allowed |
| `Simulation` | Owns the time counter and pushes the colony forward |
| `Facility` + 5 types | Places in the colony that store resources, equipment and occupants |
| `Resources` | Resource quantities and the colony-wide rules for totals and consumption |
| `Emergency` + 4 types | Oxygen leak, fire, rover breakdown, medical emergency |
| `Mission` + 4 types | Exploration, repair, research and rescue |
| `Colony` | The orchestrator: one active mission, resource checks, emergency dispatching |
| `MarsSurface` | Shared outdoor location for exploration and rover breakdowns |

---

# Part 1: Agents, Environment & Simulation

*Author: Farah*

I wrote the agents (crew and robots), the locations, the environment and the simulation clock. These are the classes:

`Agent`, `Location`, `CrewMember`, `Scientist`, `Engineer`, `MedicalOfficer`, `Robot`, `ExplorationRover`, `RepairRobot`, `Drone`, `Environment` and `Simulation`.

## What my part does in the system

| Class | Job |
|---|---|
| `Agent` | The contract that every person and robot follows |
| `Location` | Anything that can be "where someone is" (a facility or the Martian surface) |
| `CrewMember` + 3 roles | Humans with health, fatigue and skills |
| `Robot` + 3 types | Machines with battery and a status |
| `Environment` | Holds the current weather condition and answers what is allowed |
| `Simulation` | Owns the time counter and pushes the colony forward |

## How I thought about it

At first I had humans and robots as two totally separate groups, but then the colony code would need a lot of "if it's an engineer do this, if it's a drone do that". So I made an `Agent` interface that both of them implement. The colony asks an agent the same few questions and each class decides its own answer:

- `canPerform(mission)`: can you do this mission?
- `canRespondTo(emergency)`: can you handle this emergency?
- `isAvailable()`: are you free right now? (not busy and `isUsable()`)
- `moveTo(location)`, `assignToMission()`, `release()` and `tick()` for moving, becoming busy, becoming free and getting older by one time unit.
- Two defaults: `isHuman()` is false and `canOperateIn(env)` is true. `CrewMember` overrides the first and `Drone` overrides the second.

Because of this, the colony never checks which kind of agent it is talking to. However, two of my classes do use `instanceof`.
1) `Scientist` checks the required skill of a `ResearchMission`
2) `ExplorationRover` checks the required equipment of an `ExplorationMission`, because that information only exists in those mission types.

The agents answer `canPerform` by looking at the capability tags a mission asks for (`mission.requires(Agent.REPAIR)` and so on). The tags are constants in `Agent`: `EXPLORE`, `RESEARCH`, `REPAIR`, `MEDICAL`, `FIRE_RESPONSE`, `AERIAL_RECON`, `RESCUE`, `COMMUNICATION`.

## Crew

`CrewMember` is an abstract class with the shared stuff: id, name, health, fatigue, location, a busy flag and a list of skills. The three roles extend it.

- Scientist can do Exploration and Research missions. For a Research mission the scientist must also have the lab's required skill (`addSkill`, `hasSkill`).
- Engineer can do Repair missions and responds to oxygen leaks and rover breakdowns.
- MedicalOfficer can do Rescue missions and responds to fires and medical emergencies.

Someone is usable if their health is 50 or more and their fatigue is 40 or less, and available if they are usable and not busy. Every time unit while busy adds 5 fatigue (it stops at 100). `heal` and `injure` change health between 0 and 100, and `rest()` sets health back to 100 and fatigue to 0. If you create someone with a bad id, an empty name or no location, the constructor throws an `IllegalArgumentException`. The checks use the shared `Validator`.

## Robots

`Robot` is abstract too (id, name, battery, status, location, busy), and a new robot starts at 100 battery. The status is one of `OPERATIONAL`, `DAMAGED` or `DESTROYED`, and an invalid status throws in the constructor.

- ExplorationRover does Exploration and Rescue. It has a cargo hold for up to 5 pieces of equipment, and `hasEquipment` only counts items that aren't damaged. For an Exploration mission it can only go if it carries every piece of equipment the mission needs.
- RepairRobot does Repair and responds to oxygen leaks and rover breakdowns.
- Drone does aerial recon and Rescue. `isControllable` asks the environment if communication works, and the drone's `canOperateIn` uses it.

A robot is usable if its status is OPERATIONAL and its battery is above 50, and available if it is usable and not busy. I picked 50 so a robot doesn't leave on a mission it can't finish. Each time unit while busy costs 5 battery. `recharge()` always puts the battery back to 100. When a rover breaks down I only set its status to DAMAGED. I don't move it, which matches the spec (it stays where it broke down).

## Locations

A location is either a facility or the Martian surface, and both implement `Location`. Crew and robots store the actual `Location` object instead of a name string. I changed to this because with strings a typo like "habitat a" would just make up a new place.

- Sending someone to an emergency is `agent.moveTo(location)`. The colony does it for every responder and they stay at the facility afterwards.
- A rover on an exploration mission should be on the surface. The colony does not move agents by itself when a mission starts, so `Main` calls `rover.moveTo(surface)` by hand in scenario 1.

## Environment

It has one condition at a time: NORMAL, DUST_STORM or COMM_DISRUPTION. Other classes don't compare the names, they call `allowsSurfaceOperations()` or `allowsCommunication()`. A dust storm only stops surface work and a communication disruption only stops communication. `setCondition` rejects any other text and returns false. I change the weather by hand in `Main`.

### Environmental effects

| Condition | What stops | How it is done |
|---|---|---|
| NORMAL | nothing | everything is allowed |
| DUST_STORM | Exploration missions cannot start. Running ones fail. | `ExplorationMission.isAllowedIn` asks `allowsSurfaceOperations()` |
| COMM_DISRUPTION | Drones cannot be controlled. Repair and Rescue missions cannot start. Running ones fail. | `Drone.canOperateIn` asks `allowsCommunication()`, and `RepairMission` / `RescueMission` `isAllowedIn` do the same |

Research missions don't depend on the weather, only on the lab being operational.

### What happens to missions already in progress

At the start of every time unit (after the agents get older) the colony asks the active mission `isAllowedIn(env)` again. If the new weather forbids it, the colony fails the mission with the reason "Environment is not suitable", releases all its agents so they are available again, saves the mission in the history and clears the active mission so a new one can be submitted. Some things to know:

- It happens on the **next** time unit, not at the moment `setCondition` is called. Scenario 5 shows it: the storm is set, then `advanceTime(1)` fails mission 6.
- A running mission is never paused or retried, it just fails.
- Only the mission's own rule is checked while it runs. So a dust storm fails a running Exploration mission but not a Rescue mission that uses the rover, because Rescue only depends on communication. Also, `canOperateIn` of the agents (the drone rule) is only checked when the mission is submitted.
- The same check also fails a mission when an agent stops being usable, for example the battery reaching 50 or the fatigue going over 40.

## Simulation

`Simulation` gets the environment and the colony in its constructor (we create them in `Main`) and throws an exception if either is null. `advanceTime(n)` goes one unit at a time: it adds 1 to the time, prints "Time unit: X" and calls `colony.tick()`. If `n` is 0 or negative it prints a message and does nothing. The clock is never reset, so scenario 5 starts at time unit 4 after scenario 1 used 3.

One tick of the colony does this, in order: crew `tick()` (fatigue +5 if busy), robots `tick()` (battery -5 if busy), the active mission is re-checked, then the mission itself ticks. A consequence of the 40 and 50 limits is that a fresh crew member can be busy for 8 units and a fresh robot for 9. On the next unit the colony fails the mission because the agent is no longer usable. Nothing calls `rest()` or `recharge()` automatically, so they have to be called by hand.

## Emergency rules (agent side)

An agent is a possible responder if it is available and `canRespondTo` the emergency. The colony asks the agent, it never checks the concrete type.

| Emergency | Agents that say yes |
|---|---|
| Oxygen leak | Engineer, RepairRobot |
| Rover breakdown | Engineer, RepairRobot |
| Fire | MedicalOfficer |
| Medical emergency | MedicalOfficer |

Scientists, rovers and drones answer no to everything. Location does not matter, any available agent can be sent and it is moved to the affected facility. An agent on an active mission is busy, so it is not available, which is how emergencies do not interrupt active missions. How many responders are needed, what happens when none are free (UNRESOLVED with a reason) and which resources are used are described in [Part 2](#emergencies).

## Other decisions I made

- Health 50+, fatigue 40 or less and battery above 50 to be usable.
- 5 fatigue or 5 battery per time unit while busy.
- Cargo holds 5 items.
- Medical officers also handle fires, and medical officers, rovers and drones can take Rescue missions.
- Statuses and conditions are `String` constants (`Robot.OPERATIONAL`, `Environment.DUST_STORM`) to keep the code simple.
- Moving is instant and has no distance, and a robot can move even when damaged.
- A busy agent that is assigned again prints a message and stays as it is.

---

# Part 2: Facilities, Resources & Emergencies

*Author: (add name)*

This is my part of the MARS-OPS colony simulator. It covers the colony **facilities**, the **resources** stored in them, and the four **emergencies** (oxygen leak, fire, rover breakdown, medical).

| File | What it is |
|---|---|
| `Facility` | Abstract base class for everything in the colony. Holds id, name, capacity, status, resources, equipment and occupants. |
| `Habitat` | Facility with a `LifeSupportSystem`. Uses up oxygen every time step based on how many people are inside. |
| `Laboratory` | Facility that can catch fire. Equipment inside can be checked with `hasUsableEquipment(name)`. |
| `StorageFacility` | Facility with a `storageCapacity`. Refuses to take more than it can hold. |
| `PowerStation` | Generates battery power each time step and can send some to another facility. |
| `CommunicationStation` | Has a signal that can be disturbed. `isLinkActive()` is true only if the station is operational and the signal is fine. |
| `LifeSupportSystem` | Oxygen consumption rate and air quality for one habitat. |
| `Equipment` | A named item with a quantity and a damaged flag. |
| `Resources` | One resource and its quantity in a facility, plus the static methods that work out colony-wide totals and consume resources across facilities. |
| `ResourcesType`, `FacilityType`, `OperationalStatus`, `EmergencyType`, `EmergencyStatus` | Enums. |
| `Emergency` | Abstract base class for emergencies. |
| `OxygenLeak`, `Fire`, `RoverBreakdown`, `MedicalEmergency` | The four emergency types. |

## Resources

Resources are physically stored inside facilities, but the rules work on the colony as a whole. All of that lives in static methods of `Resources`.

- **Colony total**: `getColonyTotal` adds up a resource over every facility whose status is `OPERATIONAL`. A facility that is damaged, under maintenance or destroyed doesn't count, even if it still physically holds stock.
- **Multiple sources**: a request can be spread over several facilities. If Habitat A has 300 oxygen and the storage has 200, asking for 450 works and asking for 600 doesn't.
- **All or nothing**: `consume(facilities, request)` first calls `canSupply`. If any resource in the request is short, it returns `false` and nothing is touched. Quantities can't go negative.
- **Allocation policy**: storage facilities are drained first, in list order. Whatever is still missing is then taken from the other operational facilities, again in list order. I chose this so the habitats and labs keep their own local stock for as long as possible, because that is the oxygen the people actually breathe.
- **Status report**: `getColonyTotals` returns a map with one total per resource type, ready to print.

## Facility behaviour

- A facility is "available" only when its status is `OPERATIONAL` (`isOperational()`).
- `addOccupants` refuses an agent if the facility is already full.
- `Facility.advanceTime()` is empty by default and overridden by subclasses, so the simulation can call it on every facility without checking its type:
  - `Habitat`: uses `consumptionRate × number of occupants` oxygen (never below 0) and updates the life-support air quality.
  - `PowerStation`: adds `powerOutput` battery power.
  - `Laboratory`, `StorageFacility`, `CommunicationStation`: nothing happens by themselves.
- `PowerStation.supplyPower` takes power from the station and gives it to another facility. If the target can't take it, the power is given back to the station.

## Emergencies

Every emergency has an id, a type, a status (`DETECTED`, `RESPONDING`, `RESOLVED` or `UNRESOLVED`) and a description. Responders are found by asking the agents, not by checking their class: `getResponders(allAgents)` keeps every agent where `isAvailable()` and `canRespondTo(this)` are both true. Emergencies don't interrupt missions, since a busy agent just isn't available.

When the responders aren't there, the emergency becomes `UNRESOLVED`, nothing is consumed for the response, and nothing is retried automatically.

### Oxygen leak
1. `detect()` marks the habitat as leaking, takes the first oxygen loss (`oxygenLoss`) from that habitat only, and sets the status to `RESPONDING`.
2. `repairLeak()`:
   - responders available (at least one crew member **and** one robot): the leak is fixed, the habitat stops leaking, status `RESOLVED`. Total cost is **one** `oxygenLoss`.
   - no responders (or only crew, or only robots): the same amount is taken a second time and the status becomes `UNRESOLVED`. Total cost is **two** `oxygenLoss`.
   - leak already repaired: nothing happens.
3. It's a one-time loss. There is no oxygen draining on every time step.
4. Habitat oxygen is clamped at 0.

### Fire
- `setFire()` puts the laboratory on fire, sets its status to `DAMAGED` (so it's unavailable and its stock stops counting) and damages all its equipment.
- Each `tick()` while the fire is active uses oxygen from the lab depending on severity: 5 (low), 10 (moderate) or 15 (high).
- `extinguishFire()` with responders: the fire stops, the lab goes to `UNDER_MAINTENANCE`, status `RESOLVED`. Without responders the fire keeps burning and the status is `UNRESOLVED`.
- If the fire isn't put out, someone gets hurt: `extinguishFire()` without responders, and every `tick()` while the fire is active, create one follow-up `MedicalEmergency` for the first crew member found in `allAgents` (needs 1 responder and 3 medical supplies, in the burning lab, id = fire id + 1000). It is only created once and can be read with `getFollowUpMedical()`.

### Rover breakdown
- `breakdown()` marks the rover as broken (`repairStatus = false`).
- `repairRover()` succeeds only if enough responders were found (`requiredNumberOfResponder`) and the spare parts available are at least `requiredRepairParts`.

### Medical emergency
- `treatPatient()` checks three things: the facility is operational, enough responders are available, and the facility has enough medical supplies.
- On success the patient heals 5 health, the supplies are consumed from that facility, and the emergency is `RESOLVED`.
- On failure nothing is consumed, the status is `UNRESOLVED`, and I made the patient lose 5 health because the delay makes things worse.

## Design decisions

- **Facilities don't own missions.** The spec says the colony tracks missions, so none of my classes know about missions.
- **Resource logic is static and takes a list of facilities** instead of a Colony object, so it can be used (and tested) before the Colony class exists. The Colony can just call these methods.
- **Facilities are hidden from outside changes where I could.** `getEquipments()` and `getOccupants()` return read-only views, and quantities are checked in the setters.
- **Constructors validate their input** and throw `IllegalArgumentException` for bad ids, names, capacities and quantities.

## Known limitations

- You have to call `getResponders(allAgents)` before `extinguishFire()`, because it uses the list built by that call. `OxygenLeak` does it by itself when the list is empty, and `RoverBreakdown` and `MedicalEmergency` do it by themselves. When the emergency goes through `Colony.handleEmergency`, the colony fills `allAgents` and the responders for you.
- The medical emergency that a fire creates is not handled automatically. It has to be passed to `Colony.handleEmergency` by whoever called `getFollowUpMedical()`.
- `Fire.tick()` is not called by `Colony.tick()`, so a fire only burns oxygen over time if the simulation calls it.

---

# Part 3: Missions, Colony, Equipment & Mars Surface

*Author: Amr*

I wrote the missions system (and all four mission types), the colony coordinator, the equipment mechanics, and the Martian surface location.

## How I thought about it

I distributed the responsibilities:

* `Colony` acts as an orchestrator: it enforces the colony-wide rule of having only one active mission at a time, checks total available resources across operational buildings, and handles emergency dispatching.

* `Mission` owns its own checklist: each mission subclass knows what agents, equipment, and environment it needs to execute safely.

* `Equipment` tracks its own operational health, so rovers and facilities simply ask whether a piece of gear is functional.

* `MarsSurface` acts as a shared, uncontained location where outdoor exploration and rover breakdowns take place.

Because `Colony` asks missions whether requirements are met and delegates agent checks through the `Agent` interface (`canPerform`), neither `Colony` nor `Mission` ever relies on `instanceof`.

## Mission Lifecycle & States

Every mission follows a strict lifecycle governed by four states: `ASSIGNED`, `IN_PROGRESS`, `COMPLETED`, and `FAILED`.

* **ASSIGNED**: A mission enters this state once submitted to the colony and accepted. Acceptance requires that no other mission is currently active, all assigned agents are available and capable, the environment permits operations, all required equipment is functional, and the colony has sufficient total resources.

* **IN_PROGRESS**: As soon as simulation time begins advancing while a mission is active, its duration begins counting down (or its elapsed time ticks up).

* **COMPLETED**: Reached when `elapsedTime` matches the required `duration`. Assigned agents are immediately marked free (`setBusy(false)`), allowing them to rest, recharge, or join subsequent missions.

* **FAILED**: A mission transitions directly to failed if rejected at submission, or if an unexpected incident occurs mid-operation (such as a dust storm hitting a surface rover or equipment failing). A failed mission records an explicit failure reason, immediately unlocks any assigned entities, and is never retried automatically. A new mission instance must be submitted.

## Mission Hierarchy

`Mission` is an abstract base class holding common fields: `id`, `name`, `duration`, `elapsedTime`, `status`, `failureReason`, and a list of assigned `Agent` objects. Four subclasses extend it:

* `ExplorationMission`: Explores a designated sector on Mars. Requires an `ExplorationRover` with enough battery, a `Scientist`, functional exploration gear, and an environment permitting surface work (`allowsSurfaceOperations()`).

* `RepairMission`: Fixes a damaged facility or piece of equipment. Requires an `Engineer` or `RepairRobot` and consumes colony spare parts upon assignment.

* `ResearchMission`: Runs experiments inside a specific facility. Requires an operational `Laboratory`, a `Scientist`, functional scientific sensors/tools, and research consumables.

* `RescueMission`: Dispatches help to recover a disabled rover or injured crew member. Requires an operational vehicle (rover or drone), capable personnel (`MedicalOfficer` or engineer depending on target), and functional communication.

## Equipment

`Equipment` represents the tools, scientific scanners, and hardware kits used across facilities and vehicles:

* Each item has an `id`, `name`, and an integer `condition` (0 to 100).

* An item is considered functional (`isFunctional()`) if its condition is greater than 30%.

* Equipment can be housed inside facilities (such as an analyzer installed in a `Laboratory`) or loaded into the cargo bay of an `ExplorationRover` (up to 5 items).

* If a facility suffers damage or fire, any equipment housed within it takes condition damage. If an item drops to 30% or below during an operation, the dependent mission fails.

## Mars Surface

`MarsSurface` implements the `Location` interface

* When an exploration rover departs on an outdoor mission, its location becomes `MarsSurface`.

* If a rover suffers a breakdown while exploring, its location remains pinned to `MarsSurface`. Responding rescue or repair units must travel directly to the surface site.

## Colony & Resource Allocation Rules

`Colony` acts as the central hub holding all facilities, agents, equipment, active emergencies, and the mission queue:

* **One Active Mission Limit**: The colony strictly permits only one mission to be active (`ASSIGNED` or `IN_PROGRESS`) at any given tick. If another mission is submitted while one is active, the incoming mission immediately transitions to `FAILED` with the reason `"Colony already has an active mission"`.

* **Colony-Wide Resource Totals**: The colony aggregates resources across all operational facilities. Resources located inside a facility that is `DAMAGED` or `DESTROYED` do not count toward the usable pool.

* **All-or-Nothing Rule**: Before any mission or repair begins, the colony performs a full verification of the required quantity. If the colony lacks even a single unit of the requested supply, zero units are deducted and the operation aborts with an insufficient resource reason.

* **Allocation Priority Order**: When drawing resources across multiple operational facilities (e.g., retrieving 450 units of oxygen split between facilities), the colony draws first from dedicated storage (`StorageFacility`), and only pulls from living quarters (`Habitat`) if storage is depleted.

## Environmental Effects

The colony checks the `Environment` state when scheduling and ticking:

* **Dust Storm**: Surface exploration missions cannot begin (`ExplorationMission` checks `allowsSurfaceOperations()`). If a storm starts while a rover is on `MarsSurface`, the colony transitions that active mission to `FAILED` on the next simulation tick.

* **Communication Disruption**: Drones lose control signals (`isControllable()` returns false) and remote rescue missions cannot be initiated.

---

# Class Relationships

## Agents, Locations & Simulation (Part 1)

- Inheritance: the crew and robot subclasses.
- Realization: `CrewMember` and `Robot` implement `Agent`, and facilities and `MarsSurface` implement `Location`.
- Association: each crew member and robot to its `Location`, and `Simulation` to the `Colony` and the `Environment` it was given.
- Aggregation: `ExplorationRover` and `Equipment`, since the equipment exists without the rover.
- Dependency: agents use `Mission`, `Emergency` and `Environment` only as method parameters, and `CrewMember` and `Robot` use `Validator`.

I have no composition in my classes. `Simulation` does not create its colony or environment, it only holds them. The composition in the project (habitat with its life support system, facility with its resources) is in the facilities part.

## Facilities, Resources & Emergencies (Part 2)

| Relationship | Where | Why |
|---|---|---|
| Inheritance | `Habitat`, `Laboratory`, `StorageFacility`, `PowerStation`, `CommunicationStation` extend `Facility`. `OxygenLeak`, `Fire`, `RoverBreakdown`, `MedicalEmergency` extend `Emergency`. | They are real "is-a" cases and share a lot of code. Overriding `advanceTime()` and `hasRequiredResponders()` is where polymorphism comes in. |
| Realization | `Facility` implements `Location`. | Anything that can hold an agent can be its location. |
| Composition | `Habitat` ◆ `LifeSupportSystem`. `Facility` ◆ `Resources`. | The `LifeSupportSystem` is created in the `Habitat` constructor and is `final`, so it disappears with the habitat. The `Resources` entries are created inside the facility when stock is added and are never passed in from outside. |
| Aggregation | `Facility` ◇ `Equipment`. `Facility` ◇ `Agent` (occupants). | Equipment and people are created elsewhere and passed in. They still exist if the facility is destroyed and can be moved. |
| Association | An `Emergency` uses a `Habitat`, `Laboratory`, `Facility`, `ExplorationRover` or `CrewMember`. `Fire` keeps the `MedicalEmergency` it creates. `PowerStation` uses a target `Facility`. | The objects work together, but neither one owns the other. |

## Missions, Colony & Surface (Part 3)

* **Inheritance**: `ExplorationMission`, `RepairMission`, `ResearchMission`, and `RescueMission` extend abstract `Mission`.

* **Realization**: `MarsSurface` implements `Location`.

* **Aggregation**: `Colony` aggregates `Facility`, `Agent`, `Equipment`, and `Mission` objects (they can exist independently of the colony container).

* **Association**: `Mission` associates with target `Facility` or `MarsSurface`, as well as participating `Agent` instances.

---

# How to Compile and Run

All files are in the same folder and use the default package (no `package` lines). You need Java 9 or newer because `Main` uses `List.of` and `Map.of`.

```
javac *.java
java Main
```

In an IDE such as NetBeans, put all the files in one project and run `Main`.

---

# Demonstration Scenarios

`Main` is our test class. Its `main` method calls `setup()` and then scenarios 1 to 5 in order. To run only some of them, comment out the other calls in `main`, but remember they share the same objects, so the numbers they print (battery left, spare parts, the time unit) depend on the scenarios that ran before.

| Scenario | What it shows |
|---|---|
| 1 Successful exploration | Scientist and rover are available and can perform it, the rover is moved to the surface, 3 time units pass, then both are released |
| 2 Mission rejection | The engineer answers `canPerform` with false, so the reason is "Agent cannot perform this mission" |
| 3 Resource shortage | Put 300 oxygen in a habitat and 200 in a storage facility, then call `Resources.consume(facilities, request)` with 450 (works, storage is emptied first) and then with 600 (rejected, nothing changes) |
| 4 Oxygen leak | Engineer and RepairRobot respond and are moved to Habitat A, the medical officer does not. Create a habitat with some oxygen, build an `OxygenLeak` for it, call `setAllAgents(...)` and `getResponders(...)`, then `detect()` and `repairLeak()`. Check the habitat oxygen: 1 × loss if repaired, 2 × loss if there were no responders. Sending the same `OxygenLeak` to `colony.handleEmergency(...)` gives the same result |
| 5 Dust storm | `Environment` rejects the new mission, then mission 6 fails on the next time unit and the rover is available again |
