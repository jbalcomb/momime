# Shared city tiles give half their output

Patch: [`0001-shared-tiles.patch`](0001-shared-tiles.patch)
Applies to: `momime` repo root (paths are `Common/...`, `Client/...`).

```
cd momime
git apply --check /path/to/0001-shared-tiles.patch && git apply /path/to/0001-shared-tiles.patch
```

## Report

> Towns that share tiles do not divide said tiles output by two; same goes for the Terrain Specials on it
> (our Dwarf player was very, very happy).

This was confirmed. `listCityFoodProductionFromTerrainTiles`, `listCityProductionPercentageBonusesFromTerrainTiles` and
`addProductionFromMapFeatures` each counted every tile in the city radius at full value, whether or not another
city's radius also covered it.

## The rule (MoM 1.31)

Source: strategy guide, and the Master of Magic wiki "Town → Shared Tiles" and "Town → Gold".

A tile inside the catchment area of two settlements gives half its normal output to **both**, Terrain Specials
included. Any settlement counts, whoever owns it, including outposts. How the half is rounded depends on the resource:

| Resource | How shared tiles are halved |
|---|---|
| Food (max city size) | Food is counted in ¼ units and **totalled over all shared tiles before rounding**, so sharing loses no food overall. A shared Forest (½) plus a shared Grassland (1½) gives **+1**, not 0. |
| Production % bonus | Halved **per tile**, rounded down. A shared Desert (3%) gives +1%. |
| Minerals / Terrain Specials (gold, power, unit cost reduction, Wild Game food/rations) | Halved **per feature**, rounded down, **after** the Dwarf ×2 and the Miners' Guild +50%. Shared Gems for Dwarves with a Miners' Guild: 5 ×2 = 10, +50% = 15, ÷2 = **7**. Wild Game's +2 food becomes +1. |

## Code changes

### Common

- `CityCalculations.isTileSharedWithAnotherCity (map, tile, cityLocation, sys)` is new. It walks
  `DIRECTIONS_TO_TRAVERSE_CITY_RADIUS` from the tile and looks for a city other than the one at `cityLocation`. The radius is
  symmetric, so this finds every city whose radius covers the tile. It works when there is no city at
  `cityLocation` yet, so the client surveyor and the AI's site evaluation take existing neighbours into account,
  as the original surveyor does.
- The tile type and map feature counting loops now keep **shared and unshared tiles as separate breakdown entries**.
  The internal key for shared entries is `ID + "-shared"`, and unshared keys are unchanged.
- **Production %:** `percentageBonusAllTiles = (bonus / 2) * count` for shared entries.
- **Food:** this is the awkward one. IME keeps food as doubled (½) values, and halving a shared tile again would need
  quarters. Instead, the code keeps a running total of shared doubled food and halves that total, rounding in the
  food production type's own direction (`RE06` = round up). The final halving of the food total also rounds that way,
  and ⌈⌈x⌉/2⌉ = ⌈x/2⌉, so the result is exactly what you'd get by adding up in quarters. Each shared entry is given
  the difference in the halved running total, so the entries add up to the right amount. If there are several shared
  tile types, which entry gets the odd half is arbitrary.
- **Map features:** there's a new field `doubleProductionAmountAfterSharing`, and it is what now gets added to the
  production breakdown. For unshared features it equals `doubleModifiedProductionAmountAllFeatures`, so nothing
  changes. For shared features it is `2 × ⌊modifiedEach / 4⌋ × count`, i.e. each feature's value rounded down to a
  whole number after halving.
- `MoMIMECommonInternal.xsd` has the new `shared` boolean on `cityProductionBreakdownTileType` and
  `cityProductionBreakdownMapFeature`, plus the new `doubleProductionAmountAfterSharing` field. The docs on the halved
  fields are updated.

### Client (display only)

The city screen production breakdown gets three new language entries. They are English only, and other
languages fall back to English as usual.

- `productionFromSharedTiles`: "2 x Grasslands shared with another city, each producing 1½ Food, halved = 1½ Food"
- `percentageBonusFromSharedTiles`: "2 x Desert shared with another city, each gives 3% halved and rounded down = 2% Production bonus"
- `productionFromMapFeatureShared`: an indented line under the map feature, "Shared with another city, halved and rounded down = 7 Gold"

There is also a new replacer variable, `MAP_FEATURE_PRODUCTION_AMOUNT_SHARED`.

### Tests

`TestCityCalculationsImpl` has 4 new tests:

- `testIsTileSharedWithAnotherCity`: covers the radius corners, the other plane, your own city, and a location with no city.
- `testListCityFoodProductionFromTerrainTiles_Shared`: 2 shared Forest and 1 shared Grassland must total 1¼ food.
  That rounds up to a doubled value of 3 for the shared tiles. Rounding each tile separately would give 4.
- `testListCityProductionPercentageBonusesFromTerrainTiles_Shared`: 1 unshared + 2 shared Desert gives 3 + 1 + 1 = 5%.
- `testAddProductionFromMapFeatures_Shared`: Dwarf city with a Miners' Guild. Unshared Gems give 15 gold, shared Gems give 7, and shared Wild Game gives 1 ration.

All existing tests pass unchanged, so a city with no shared tiles calculates exactly as before.

## Notes for review

- **Map used.** As with everything else in city production, the server uses the true map and the client uses its
  fog-of-war memory. A neighbouring city the player hasn't seen yet won't show as sharing on the city screen.
  The server still halves the tile, because it uses the true map.
- **No "½" icon on the city screen's mini-map.** The original draws one on shared tiles. I left that out because it
  needs a graphic. The production breakdown text does say which tiles are shared.
- **Building requirements** (e.g. Sawmill needs a Forest) are unaffected. In the original, a shared tile still lets
  both cities build. `buildingPassesTileTypeRequirements` doesn't use these breakdowns, so it already behaves that way.
- **Gold trade bonus** from river/ocean tiles isn't halved. In the original it isn't part of the catchment output.
