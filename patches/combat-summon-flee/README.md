# Fleeing no longer keeps combat summons as overland units

Patch: [`0001-combat-summon-flee.patch`](0001-combat-summon-flee.patch)
Applies to: `momime` repo root (paths are `Server/...`).

## Report

> if you flee with combat summons you gain them as overland unit

This was confirmed. Phantom Warriors, Fire/Earth/Air Elementals, Phantom Beast and the like are meant to last
only for the battle.

## Cause

`CombatHandlingImpl.fleeFromCombat` gets each unit's flee chance from `CombatFleeUtils.determineChanceOfFleeing`
and rolls for it. That list includes combat summons. A summon that makes its roll is taken out of combat by
`setUnitIntoOrTakeUnitOutOfCombat (..., combatLocation = null, ...)`, exactly like a normal unit.

At the end of combat, `CombatProcessingImpl.purgeDeadUnitsAndCombatSummonsFromCombat` removes units with
`wasSummonedInCombat` set, but only those whose `combatLocation` matches the combat. The fled summon no longer
has a combat location, so it is never purged and stays on the overland map for good. It even starts charging upkeep.

## Fix

In `fleeFromCombat`, before the flee rolls, every combat summon on the fleeing side (`wasSummonedInCombat ()`) is
killed with `KillUnitActionID.PERMANENT_DAMAGE`. That is the same action the end-of-combat purge uses for them. The
roll loop then skips summons.

This happens to every summon on the fleeing side, **whatever its flee chance, including `CANNOT_FLEE`**. Otherwise
a player whose real units all escaped could be left stuck in the combat with only summons, unable to leave. The
summons would vanish at the end of combat anyway, so dispelling them when their side retreats changes nothing
else. It is also the closest match to 1.31, where combat summons never reach the overland map.

## Raise Dead / Animate Dead

These were checked as well, and they are **not affected**. They don't create combat summons. `SpellProcessingImpl`
(`KindOfSpell.RAISE_DEAD`) brings the dead unit back, gives it to the caster, adds Undead for Animate Dead, and
sets it alive. `wasSummonedInCombat` stays false, so the unit is a permanent unit whether its side wins, loses or
flees. That is intended: the manual says both spells bring the unit back permanently.

In 1.31, an animated *enemy* unit only belongs to the caster for the battle, then reverts or vanishes, which the
wiki lists as a bug in the original. IME makes the ownership change stick. This patch leaves that alone.

## Tests

`Server/src/test/.../process/TestCombatHandlingImpl.java` is new. `testFleeFromCombat_CombatSummonsDispelled`
sets up an attacker with:

- a regular unit that can easily flee
- a combat summon that can easily flee
- a combat summon that cannot flee

It checks that both summons are killed with `PERMANENT_DAMAGE` and that only the regular unit is taken out of
combat. It also checks that combat then ends with the defender as the winner.

## Not changed: summoning anywhere on the battlefield

> you can combat summon everywhere instead of just your half of the battlefield

This is true. In 1.31 the target tile must be "on the caster's half of the battlefield" (wiki "Summoning Spell →
Combat Summoning Spells"). IME's `CombatSpellClientUtilsImpl` and `SpellQueueingImpl` only check that the tile is
empty and passable. This was left alone on purpose, as a possible IME design choice, and the players like it.
If you want the 1.31 rule, both checks need the same "which half" test. On IME's staggered combat grid, that is
the diagonal between the defender start area around (3,11) and the attacker start area around (7,19).
