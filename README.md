# MARS-OPS: Facilities, Resources and Emergencies



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

Resources are physically stored inside facilities, but the rules work on the colony as a whole . All of that lives in static methods of `Resources`.

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

## Object relationships

| Relationship | Where | Why |
|---|---|---|
| Inheritance | `Habitat`, `Laboratory`, `StorageFacility`, `PowerStation`, `CommunicationStation` extend `Facility`. `OxygenLeak`, `Fire`, `RoverBreakdown`, `MedicalEmergency` extend `Emergency`. | They are real "is-a" cases and share a lot of code. Overriding `advanceTime()` and `hasRequiredResponders()` is where polymorphism comes in. |
| Realization | `Facility` implements `Location`. | Anything that can hold an agent can be its location. |
| Composition | `Habitat` ◆ `LifeSupportSystem`. `Facility` ◆ `Resources`. | The `LifeSupportSystem` is created in the `Habitat` constructor and is `final`, so it disappears with the habitat. The `Resources` entries are created inside the facility when stock is added and are never passed in from outside. |
| Aggregation | `Facility` ◇ `Equipment`. `Facility` ◇ `Agent` (occupants). | Equipment and people are created elsewhere and passed in. They still exist if the facility is destroyed and can be moved. |
| Association | An `Emergency` uses a `Habitat`, `Laboratory`, `Facility`, `ExplorationRover` or `CrewMember`. `Fire` keeps the `MedicalEmergency` it creates. `PowerStation` uses a target `Facility`. | The objects work together, but neither one owns the other. |

## Design decisions

- **Facilities don't own missions.** The spec says the colony tracks missions, so none of my classes know about missions.
- **Resource logic is static and takes a list of facilities** instead of a Colony object, so it can be used (and tested) before the Colony class exists. The Colony can just call these methods.
- **Facilities are hidden from outside changes where I could.** `getEquipments()` and `getOccupants()` return read-only views, and quantities are checked in the setters.
- **Constructors validate their input** and throw `IllegalArgumentException` for bad ids, names, capacities and quantities.

## Known limitations

- You have to call `getResponders(allAgents)` before `extinguishFire()`, because it uses the list built by that call. `OxygenLeak` does it by itself when the list is empty, and `RoverBreakdown` and `MedicalEmergency` do it by themselves. When the emergency goes through `Colony.handleEmergency`, the colony fills `allAgents` and the responders for you.
- The medical emergency that a fire creates is not handled automatically. It has to be passed to `Colony.handleEmergency` by whoever called `getFollowUpMedical()`.
- `Fire.tick()` is not called by `Colony.tick()`, so a fire only burns oxygen over time if the simulation calls it.




### Scenarios that use my part

**Scenario 3 – Resource shortage.** Put 300 oxygen in a habitat and 200 in a storage facility, then call `Resources.consume(facilities, request)` with 450 (works, storage is emptied first) and then with 600 (rejected, nothing changes).

**Scenario 4 – Emergency handling.** Create a habitat with some oxygen, build an `OxygenLeak` for it, call `setAllAgents(...)` and `getResponders(...)`, then `detect()` and `repairLeak()`. Check the habitat oxygen: 1 × loss if repaired, 2 × loss if there were no responders. Sending the same `OxygenLeak` to `colony.handleEmergency(...)` gives the same result.
