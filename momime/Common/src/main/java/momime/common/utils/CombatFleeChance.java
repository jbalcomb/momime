package momime.common.utils;

/**
 * Chance of a unit successfully fleeing from combat
 */
public enum CombatFleeChance
{
	/** Unit has no chance of fleeing */
	CANNOT_FLEE,
	
	/** Unit has a chance of fleeing */
	CHANCE_TO_FLEE,
	
	/** Unit can automatically flee */
	EASILY_FLEE;
}