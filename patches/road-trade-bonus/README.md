# Gold trade bonus from roads (and Nomads)

Patch: [`0001-road-trade-bonus.patch`](0001-road-trade-bonus.patch)
Applies to: `momime` repo root (paths are `Common/src/...`), against `CityCalculationsImpl.java` as of 1.7.1-rc1 / `develop`.

```
cd momime
git apply --check /path/to/0001-road-trade-bonus.patch
git apply /path/to/0001-road-trade-bonus.patch      # or: patch -p1 < 0001-road-trade-bonus.patch
```

## The problem

`CityCalculationsImpl.calculateGoldTradeBonus` builds the "Road/River/Ocean Gold" bonus from three parts.
The road part was a stub:

```java
// Not yet implemented
gold.setTradePercentageBonusFromRoads (0);
```

So connecting cities by road never gave gold in IME. The client already had the breakdown line for it
(`goldTradeBonusFromRoads`, "...% gold trade bonus from direct road connections to other cities") and the
`tradePercentageBonusFromRoads` field. Both just always got 0.

## What about Nomads?

The Nomad bonus **was already working**. It comes from `<goldTradeBonus>50</goldTradeBonus>` on race `RC08`
in the 1.31 rules XML, and `testCalculateGoldTradeBonus_WithRaceBonus` covers it.
This patch doesn't change that logic. It only moves the `cityData`/`raceID` lookup up a few lines so the road
calculation can use it too. There is a new test of a Nomad city that has a river, roads and the race bonus
together, to check that all three are added and then capped.

Note that the bonus is easy to miss in play. The total is capped at 3% × population (in 1,000s), so a
young Nomad town shows +12% at size 4, not +50%.

## The rule implemented (MoM 1.31)

Sources: Strategy guide p210, and the Master of Magic wiki "Roads → Road/River/Ocean Gold" (including the
worked Honeypool example).

| Source | Bonus |
|---|---|
| Each connected city of the **same** race | +0.5% per 1,000 population |
| Each connected city of a **different** race | +1% per 1,000 population |
| Shore / River / River mouth | +10% / +20% / +30% (unchanged, from tile types) |
| Nomad city | +50% (unchanged, from race) |
| **Cap on the total** | 3% × city population in 1,000s (unchanged) |

"Connected" means:

- A city is connected if a continuous road (8-way adjacency, same plane, wrapping at the map edge) runs from
  this city's tile to its tile.
- The road may pass *through* other cities. City tiles always have a road, so a city in the middle counts and
  the search carries on past it.
- Cities count whoever owns them, including other wizards' cities and neutral cities.
- Normal and enchanted roads count the same.
- Outposts (under 1,000 population) add nothing.
- Same-race half-percents are totalled before rounding down, which matches the wiki's "+1% for every 2
  population of connected same-race cities". Two connected same-race size-3 cities therefore give +3%, not +2%.

Wiki example check: Honeypool (Halfling, size 21) connected to 50 non-Halfling and 10 Halfling population gives
50 × 1% + 10 × 0.5% = 55%. Under the 63% cap, that is what the new code produces.

## Code changes

`Common/.../calculations/CityCalculations.java` and `CityCalculationsImpl.java`:

- New method `int calculateGoldTradeBonusFromRoads (map, cityLocation, cityRaceID, sys)`.
  - It does a breadth-first flood fill from the city along tiles where `terrainData.roadTileTypeID != null`,
    on the city's plane only.
  - It uses `CoordinateSystemUtils.move3DCoordinates`, so map wrapping is handled.
  - It keeps a `boolean[height][width]` visited array, so each tile is looked at once (at most 60×40 tiles).
  - It returns 0 when `cityRaceID == null`, i.e. there is no city at the location. The AI's city-site
    evaluation and the client surveyor call `calculateGoldTradeBonus` for empty tiles, and they keep their
    previous results.
- `calculateGoldTradeBonus` now calls it instead of setting 0.

`Common/src/test/.../TestCityCalculationsImpl.java`, 4 new tests:

- `testCalculateGoldTradeBonusFromRoads`: same and different race, a road passing through a city, an outpost,
  a gap in the road, and a road on the other plane. It runs from both ends to check that the race is relative
  to the city.
- `testCalculateGoldTradeBonusFromRoads_Wrapping`: a road across the x=0 / x=59 edge.
- `testCalculateGoldTradeBonusFromRoads_NoCity`: no city at the location, so the result is 0.
- `testCalculateGoldTradeBonus_WithRoadsAndRaceBonus`: a Nomad city with river 20% + road 4% + race 50%,
  giving a total of 74% (under the size-25 cap of 75%).

No XSD, database, language or client changes were needed.

## Behaviour notes / things a reviewer may want to decide

1. **Which map is used.** The calculation uses whatever map the caller passes in, as the existing river/ocean
   check already did. The server (`ServerResourceCalculationsImpl`, `ServerCityCalculationsImpl`) passes the
   **true map**, so the gold actually paid uses real road connections and current city sizes. The client city
   screen uses the player's fog-of-war memory. If a connected city has grown, shrunk, or been destroyed out of
   sight, the client's breakdown can therefore differ slightly from what the server pays. The server is
   authoritative, and the same kind of mismatch can already happen elsewhere. A rule that only counts cities the
   owner can see would need a different map passed in on the server.
2. **Landmasses.** The wiki says roads built over water by plane-shift tricks do not join cities on different
   landmasses in 1.31. IME follows the road tiles and has no landmass check, so such a bridge *would* connect
   them. This is very much an edge case.
3. **Hard-coded rates.** The 0.5% / 1% rates are in code. Making them data-driven, e.g. `goldTradeBonusRoadsSameRace`
   and `goldTradeBonusRoadsDifferentRace` on the session or database, would need XSD and editor changes. I left
   that out to keep the patch small.
4. **AI.** The AI does not yet value building roads for trade. It already reads the city's real gold production,
   so it will see the extra income, but it won't plan for it.
5. **Recalculation.** Gold is recalculated whenever city production is (start of turn, city screen), so it picks
   up new roads and growing neighbours on its own. Nothing is cached.
