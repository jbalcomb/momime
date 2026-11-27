# Melding over a Magic Spirit always succeeds, like the original

Patch: [`0001-meld-chance.patch`](0001-meld-chance.patch)
Applies to: `momime` repo root (paths are `Server/...`).

## Report

> sometimes my magic spirit failed to take over my brother's node and he had also used a magic spirit (none of us
> play life/guardian spirit). is there a random chance involved?

There is in IME. In 1.31 there isn't.

## IME before

`OverlandMapServerUtilsImpl.attemptToMeldWithNode` rolled `random (attacker + defender) < attacker`, using the
meld skill (`US040`) values. A Magic Spirit has 1 and a Guardian Spirit has 2.

| Attacker → node held by | Magic Spirit | Guardian Spirit |
|---|---|---|
| Magic Spirit | **50%** | 33% |
| Guardian Spirit | 67% | 50% |

## MoM 1.31

Source: wiki "Magic Spirit → Melding", "Guardian Spirit", "Node".

> Melding is always successful if the Node is not controlled by any player, or if it is linked to its owner by a
> Magic Spirit. However, if a rival Wizard controls the Node through a Guardian Spirit, the chance of success is
> only 25%.

| Attacker → node held by | Magic Spirit | Guardian Spirit |
|---|---|---|
| Magic Spirit | 100% | 25% |
| Guardian Spirit | 100% | 25% |

The attacking spirit is always used up, whether it succeeds or not. That was already the case in IME.

## Fix

```java
successful = (defendingStrength <= 1) || (getRandomUtils ().nextInt (MELD_CHANCE_AGAINST_GUARDIAN_SPIRIT) == 0);   // = 4
```

The defending spirit's meld value still comes from the database, so a value of 1 means "no protection" and anything
higher gives the 25% chance. The attacker's meld value no longer matters. Unowned nodes always succeed, as before.
The diplomacy penalty for attempting to take an enemy node is unchanged.

The AI already sends several spirits at well-defended nodes, and the new odds don't change any of that logic.

## Tests

The `TestOverlandMapServerUtilsImpl` tests are updated:

- `testAttemptToMeldWithNode_Successful_HumanAttacking`: node held by a Magic Spirit. The capture succeeds and no
  random number is drawn (`verifyNoInteractions (randomUtils)`).
- `testAttemptToMeldWithNode_Failed_HumanAttacking`: node held by a Guardian Spirit, with `nextInt (4)` returning 1,
  so the capture fails.
