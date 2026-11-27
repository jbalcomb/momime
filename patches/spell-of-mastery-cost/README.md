# Spell of Mastery research cost goes down as you learn spells

Patch: [`0001-spell-of-mastery-cost.patch`](0001-spell-of-mastery-cost.patch)
Applies to: `momime` repo root (paths are `Common/...`, `Server/...`).

## Report

> the spell of mastery stays at 60k research all the time, this on purpose?

It isn't. IME always used the flat `<researchCost>60000</researchCost>` from the database.

## The rule (MoM 1.31)

Sources: wiki "Spell of Mastery". The starting-spells detail was checked against the original code (ReMoM).

- The base cost is **60,000**.
- **−3,000** if the wizard has **11 or more books in any one realm**.
- **−½ the research cost of every spell acquired during the game**, rounded down. That covers research, spell
  trading or gifts, spells stolen when capturing a wizard's fortress, and treasure from lairs, nodes and towers.
- **The spells a wizard starts the game with do not reduce it.**

## Code changes

### Common

- `MoMIMEMessages.xsd`, `momPersistentPlayerPrivateKnowledge`: new optional `spellOfMasteryResearchCostReduction`
  (int). It is kept separately from the spell's `remainingResearchCost` because of the "lose current research"
  setting. With that setting, switching research resets the old spell to its full cost, and for the Spell of Mastery
  that has to mean the *reduced* cost. A missing value, as in older saved games, means no reduction.
- `MoMIMEServerToClientMessages.xsd`, `updateRemainingResearchCostMessage`: new optional `spellOfMasteryResearchCostReduction`.
  It is only filled in when the Spell of Mastery's cost is reduced, so the client knows the new *total* cost as well as the remaining cost.
- `SpellUtils.getFullResearchCost (spell, priv)` is new: the database cost, less the reduction for the Spell of Mastery,
  clamped at 0. The server and client both use it, so they always agree.
- `CommonDatabaseConstants.PICK_TYPE_ID_SPELL_BOOK = "B"`.

### Client

Without these changes the client would show the reductions as research progress. For example, after a 3,000
reduction it would say "3,000 of 60,000 researched".

- `UpdateRemainingResearchCostMessageImpl` stores the reduction in our private knowledge when the message includes one.
  Joining or loading a game already sends the whole private knowledge, so saved games carry it too.
- `HelpUI` (spell help: total cost, research so far, not started yet), `MagicSlidersUI` (research progress bar) and
  `SpellBookNewUI` (the "would you lose research by switching" check) use `getFullResearchCost` instead of
  `Spell.getResearchCost ()`.

### Server: `ServerSpellCalculations` / `Impl`

| Method | What it does |
|---|---|
| `calculateSpellOfMasteryResearchCost (priv, db)` | `max (0, 60000 − reduction)`: the player's full cost before any research is put in |
| `applyStartingSpellOfMasteryResearchCostReduction (priv, picks, db)` | −3,000 if any spell book pick has quantity ≥ 11 |
| `reduceSpellOfMasteryResearchCost (priv, amount)` | Adds to the stored reduction and lowers the remaining research cost, clamped at 0. Does nothing to the remaining cost if the spell is already known. |
| `spellAcquiredDuringPlay (player, spellID, db)` | `reduce (researchCost / 2)`, skipping the Spell of Mastery itself and spells with no research cost, then `sendSpellOfMasteryResearchCost` |
| `sendSpellOfMasteryResearchCost (player)` | Sends human players `UpdateRemainingResearchCostMessage` with the remaining cost and the total reduction. It is also sent at game start if the 11-book reduction applied. |

### Server: call sites

| Where | When |
|---|---|
| `PlayerMessageProcessingImpl.checkIfCanStartGame` | Game start: the 11-book reduction only. The starting spells (picks, initial choices, Artificer's free spells) are deliberately **not** counted. |
| `ServerResourceCalculationsImpl.progressResearch` | Research completed |
| `DiplomacyProcessingImpl.giveSpellInternal` / `agreeTradeSpells` | Spell gifted, or both sides of a trade |
| `SpellProcessingImpl.stealSpells` | Spells taken when capturing a wizard's fortress |
| `TreasureUtilsImpl.rollTreasureReward` | Spell found as treasure, and free spells from a retort found as treasure (only if not already known) |
| `RequestResearchSpellMessageImpl` | With `LOSE_CURRENT_RESEARCH`, switching away from the Spell of Mastery resets it to the reduced cost, not 60,000. Needs the new `serverSpellCalculations` bean property, which is added in `momime-server-beans.xml`. |

## Tests

`TestServerSpellCalculationsImpl` has 4 new tests:

- `testReduceSpellOfMasteryResearchCost`: reductions part-way through research, repeated reductions adding up,
  the clamp at 0, and already-known spells.
- `testCalculateSpellOfMasteryResearchCost`: no reduction (older saves), a normal reduction, and the clamp.
- `testApplyStartingSpellOfMasteryResearchCostReduction`: 10 + 1 books in different realms gives no reduction,
  and 11 in one realm gives −3,000.
- `testSpellAcquiredDuringPlay`: a research cost of 1001 gives −500, and the message is sent to the human player.
  Free spells and the Spell of Mastery itself give no reduction.

`TestSpellUtilsImpl.testGetFullResearchCost` (Common) covers the reduction, a missing reduction, other spells,
spells with no research cost, and the clamp at 0.

`TestServerResourceCalculationsImpl.testProgressResearch_*` now also checks for the `spellAcquiredDuringPlay` call.

## Notes for review

- **Floor of 0.** A wizard who learns a lot can, in principle, push the cost to 0. The wiki gives no minimum, so the
  cost is clamped at 0. A Spell of Mastery at 0 cost completes on the next turn's research.
- **11 books gained mid-game.** The −3,000 is only checked at game start, from the starting picks. A wizard who
  reaches 11 books in a realm later, through treasure, doesn't get it. Say if you want it applied when the threshold
  is crossed.
- **Someone else's Spell of Mastery** in the help screen still shows the database cost of 60,000. We don't know
  their reduction, and before this change it showed 60,000 for every spell anyway.
