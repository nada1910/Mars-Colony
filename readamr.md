# MARS-OPS - my part (Missions, Colony, Equipment & Mars Surface)

I wrote the missions system (and all four mission types), the colony coordinator, the equipment mechanics, and the Martian surface location. 

## How I thought about it

I distributed the responsibilities:

* `Colony` acts as an orchestrator: it enforces the colony-wide rule of having only one active mission at a time, checks total available resources across operational buildings, and handles emergency dispatching.

* `Mission` owns its own checklist: each mission subclass knows what agents, equipment, and environment it needs to execute safely.

* `Equipment` tracks its own operational health, so rovers and facilities simply ask whether a piece of gear is functional.

* `MarsSurface` acts as a shared, uncontained location where outdoor exploration and rover breakdowns take place.

Because `Colony` asks missions whether requirements are met and delegates agent checks through  `Agent` interface (`canPerform`), neither `Colony` nor `Mission` ever relies on `instanceof`.

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

## Relationships

* **Inheritance**: `ExplorationMission`, `RepairMission`, `ResearchMission`, and `RescueMission` extend abstract `Mission`.

* **Realization**: `MarsSurface` implements `Location`.

* **Aggregation**: `Colony` aggregates `Facility`, `Agent`, `Equipment`, and `Mission` objects (they can exist independently of the colony container).

* **Association**: `Mission` associates with target `Facility` or `MarsSurface`, as well as participating `Agent` instances.