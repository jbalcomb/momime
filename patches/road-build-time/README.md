# Road construction time rounds down, like the original

Patch: [`0001-road-build-time.patch`](0001-road-build-time.patch)
Applies to: `momime` repo root (paths are `Server/...`).

## Report

> Even five High Men Engineers take two turns to build a road over some terrains (Forest, Mountains) while it should
> be one according to the wiki.

This was confirmed. It is a rounding difference, not a data problem. The `productionToBuildRoad` values in the 1.31
rules XML already match the wiki's 1-engineer column: Grassland 3, Desert 4, River 5, Forest/Hills/Tundra 6,
Swamp/Mountain/Volcano 8, Sorcery node 4, Nature node 5.

## The rule (MoM 1.31)

Source: Master of Magic wiki "Roads → Construction Times".

> Take the number of turns to build a road in the tile with just one unit of Engineers, divide by the number of
> Engineers working simultaneously, and round down. The result can never be less than 1.

Dwarf Engineers, and Endurance, count double.

IME added each engineer's points every turn and finished when `progress >= needed`. That takes ⌈needed / rate⌉
turns, which rounds **up**. For 5 engineers on Forest that is ⌈6/5⌉ = 2 turns, but the original gives ⌊6/5⌋ = 1.

| Terrain (points) | Engineers | Original | IME before | IME after |
|---|---|---|---|---|
| Forest (6) | 4 | 1 | 2 | 1 |
| Forest (6) | 5 | 1 | 2 | 1 |
| Mountain (8) | 3 | 2 | 3 | 2 |
| Mountain (8) | 5 | 1 | 2 | 1 |
| Desert (4) | 3 | 1 | 2 | 1 |
| River (5) | 2 | 2 | 3 | 2 |

## Code change

`Server/.../process/PlayerMessageProcessingImpl.java`, `progressMultiTurnSpecialOrders`, `BUILD_ROAD` case:

- It now totals the road building rate of **all** live units with a `BUILD_ROAD` order on the tile. That is each unit's
  `US036` skill value, minimum 1, the same as the existing per-unit increment.
- There is a new method, `static boolean isRoadConstructionFinished (progressSoFar, needed, totalRate)`, which returns
  `progressSoFar >= needed || progressSoFar + totalRate > needed`.
  Finishing after ⌊needed / rate⌋ turns means finishing on the first turn *t* where (*t*+1) × rate > needed. With
  accumulated progress that is `progress + rate > needed`. The `>= needed` part keeps the old behaviour when progress
  is already enough, e.g. if engineers left and came back.
- The check runs after each unit adds its points, but it is tested against the whole stack's rate. It can't fire too
  early, because partial progress within a turn is never more than that turn's full progress.

Still the same as before: progress is stored per tile (`roadProductionSoFar`), engineers can join or leave mid-build,
and a finished road cancels every engineer's order on the tile.

### Tests

`TestPlayerMessageProcessingImpl.testIsRoadConstructionFinished` simulates turn-by-turn progress for 1 to 5 engineers
on each terrain row of the wiki table. It checks every cell, plus Dwarf Engineers (rate 2): 1 on Mountain gives 4 turns,
3 give 1 turn.

## Difference from the original, on purpose

In the original, the build time is fixed by the number of engineers present **when the order is given**. That is what
makes the wiki's "leap-frog" exploit work. IME recalculates every turn from the engineers actually working. Stacks
that stay together get exactly the original timings, and the exploit doesn't carry over.
