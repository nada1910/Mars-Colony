# MARS-OPS - my part (Farah)

I wrote the crew, the robots, the environment and the simulation clock. Missions, colony, facilities, resources and emergencies are in Amr's and Nada's parts of the README.

## How I thought about it

At first I had humans and robots as two totally separate groups, but then the colony code would need a lot of "if it's an engineer do this, if it's a drone do that". So I made an `Agent` interface that both of them implement. The colony only asks three things: can you do this mission (`canPerform`), can you handle this emergency (`canRespondTo`), and are you free right now (`isAvailable`). Each class decides its own answer. Because of this I never used `instanceof` in my classes.

## Crew

`CrewMember` is an abstract class with the shared stuff: id, name, health, fatigue, location and a busy flag. The three roles extend it.

- Scientist can do Exploration and Research missions.
- Engineer can do Repair missions and responds to oxygen leaks and rover breakdowns.
- MedicalOfficer can do Rescue missions and responds to fires and medical emergencies.

Someone is available if they're not busy, their health is 50 or more and their fatigue is 40 or less. Every time unit on a mission adds 5 fatigue, and `rest()` sets health back to 100 and fatigue to 0. If you create someone with a bad id, an empty name or no location, the constructor throws an exception.

## Robots

`Robot` is abstract too (id, name, battery, status, location, busy), and a new robot starts at 100 battery.

- ExplorationRover does Exploration and Rescue. It has a cargo hold for up to 5 pieces of equipment, and `hasEquipment` only counts items that aren't damaged.
- RepairRobot does Repair and responds to oxygen leaks and rover breakdowns.
- Drone does aerial recon and Rescue. `isControllable` asks the environment if communication works.

A robot is available if it's not busy, its status is OPERATIONAL and its battery is above 50. I picked 50 so a robot doesn't leave on a mission it can't finish. Each time unit on a mission costs 5 battery, and `recharge()` puts it back to 100. When a rover breaks down I only set its status to DAMAGED. I don't move it, which matches the spec (it stays where it broke down).

## Locations

A location is either a facility or the Martian surface, and both implement `Location`. Crew and robots store the actual `Location` object instead of a name string. I changed to this because with strings a typo like "habitat a" would just make up a new place. Sending someone to an emergency is `agent.moveTo(location)`.

## Environment

It has one condition at a time: NORMAL, DUST_STORM or COMM_DISRUPTION. Other classes don't compare the names, they call `allowsSurfaceOperations()` or `allowsCommunication()`. A dust storm only stops surface work and a communication disruption only stops communication. I change the weather by hand in `Main` so every run of the scenarios gives the same output.

If a dust storm starts while a rover mission is running, the colony fails that mission on the next time unit and saves the reason. A communication disruption means drones can't be controlled and missions that need communication can't start.

## Simulation

`Simulation` creates the environment and the colony. `advanceTime(n)` goes one unit at a time and calls `colony.tick()` each time, and that's what makes batteries drop and fatigue go up. I did it one unit at a time so that advancing 3 units is the same as advancing 1 unit three times, otherwise a mission could fail in the middle and we wouldn't notice. If `n` is 0 or negative it prints a message and does nothing.

## Relationships

- Inheritance: the crew and robot subclasses.
- Realization: `CrewMember` and `Robot` implement `Agent`.
- Association: each crew member and robot to its `Location`.
- Aggregation: `ExplorationRover` and `Equipment`, since the equipment exists without the rover.
- Composition: `Simulation` creates its own `Colony` and `Environment`.

## Assumptions

- Health 50+, fatigue 40 or less, battery above 50 to be available.
- 5 fatigue or 5 battery per time unit while busy.
- Cargo holds 5 items.
- Medical officers also handle fires, and medical officers, rovers and drones can take Rescue missions.


