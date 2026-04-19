package momime.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ndg.map.coordinates.MapCoordinates3DEx;
import com.ndg.multiplayer.session.PlayerNotFoundException;
import com.ndg.multiplayer.session.PlayerPublicDetails;

import momime.common.MomException;
import momime.common.database.CommonDatabase;
import momime.common.database.CommonDatabaseConstants;
import momime.common.database.NegatedByUnitID;
import momime.common.database.RecordNotFoundException;
import momime.common.messages.FogOfWarMemory;
import momime.common.messages.MemoryUnit;
import momime.common.messages.UnitStatusID;

/**
 * Methods for working out the chance of each unit successfully fleeing from combat
 */
public final class CombatFleeUtilsImpl implements CombatFleeUtils
{
	/** expandUnitDetails method */
	private ExpandUnitDetails expandUnitDetails;
	
	/**
	 * @param combatLocation Where the combat is taking place
	 * @param ourPlayerID Which player is trying to flee
	 * @param players Players list
	 * @param mem Known overland terrain, units, buildings and so on
	 * @param db Lookup lists built over the XML database
	 * @return Chance of each unit successfully fleeing from combat
	 * @throws RecordNotFoundException If the definition of the unit, a skill or spell or so on cannot be found in the db
	 * @throws PlayerNotFoundException If we cannot find the player who owns the unit
	 * @throws MomException If the calculation logic runs into a situation it doesn't know how to deal with
	 */
	@Override
	public final Map<CombatFleeChance, List<ExpandedUnitDetails>> determineChanceOfFleeing
		(final MapCoordinates3DEx combatLocation, final int ourPlayerID, final List<? extends PlayerPublicDetails> players,
		final FogOfWarMemory mem, final CommonDatabase db)
		throws RecordNotFoundException, PlayerNotFoundException, MomException
	{
		final Set<String> skillsThatAllowEasilyFleeingCombat = db.getUnitSkills ().stream ().filter
			(s -> (s.isAllowsEasilyFleeingCombat () != null) && (s.isAllowsEasilyFleeingCombat ())).map (s -> s.getUnitSkillID ()).collect (Collectors.toSet ());

		final List<String> flightSkills = db.findCombatTileType
			(CommonDatabaseConstants.COMBAT_TILE_TYPE_CLOUD, "determineChanceOfFleeing").getCombatTileTypeRequiresSkill ();
		
		final List<String> skillsThatNegateInvisibility = db.findUnitSkill
			(CommonDatabaseConstants.UNIT_SKILL_IDS_INVISIBILITY.get (0), "determineChanceOfFleeing").getNegatedBySkill ().stream ().filter
				(n -> n.getNegatedByUnitID () == NegatedByUnitID.ENEMY_UNIT).map (n -> n.getNegatedBySkillID ()).collect (Collectors.toList ());
		
		// Find all enemy units
		final List<ExpandedUnitDetails> enemyUnits = new ArrayList<ExpandedUnitDetails> ();
		for (final MemoryUnit mu : mem.getUnit ())
			if ((mu.getOwningPlayerID () != ourPlayerID) && (mu.getStatus () == UnitStatusID.ALIVE) &&
				(mu.getCombatPosition () != null) && (mu.getCombatHeading () != null) && (mu.getCombatSide () != null) &&
				(combatLocation.equals (mu.getCombatLocation ())))
				
				enemyUnits.add (getExpandUnitDetails ().expandUnitDetails (mu, null, null, null, players, mem, db));
		
		// Go through each of our units
		final Map<CombatFleeChance, List<ExpandedUnitDetails>> result = new HashMap<CombatFleeChance, List<ExpandedUnitDetails>> ();
		for (final MemoryUnit mu : mem.getUnit ())
			if ((mu.getOwningPlayerID () == ourPlayerID) && (mu.getStatus () == UnitStatusID.ALIVE) &&
				(mu.getCombatPosition () != null) && (mu.getCombatHeading () != null) && (mu.getCombatSide () != null) &&
				(combatLocation.equals (mu.getCombatLocation ())))
			{
				final ExpandedUnitDetails xu = getExpandUnitDetails ().expandUnitDetails (mu, null, null, null, players, mem, db);

				// Figure out the chance of this unit fleeing 
				final CombatFleeChance chance;
				if (!Collections.disjoint (xu.listBasicSkillIDs (), skillsThatAllowEasilyFleeingCombat))
					chance = CombatFleeChance.EASILY_FLEE;
				else
				{
					// Find the fastest enemy unit that can pursue us
					Integer fastestEnemyUnit = null;
					for (final ExpandedUnitDetails enemyUnit : enemyUnits)
					{
						// If we can't fly, or they can fly  AND  we're not invisible, or they can see invisible
						if (((Collections.disjoint (xu.listBasicSkillIDs (), flightSkills)) || (!Collections.disjoint (enemyUnit.listBasicSkillIDs (), flightSkills))) &&
							((Collections.disjoint (xu.listBasicSkillIDs (), CommonDatabaseConstants.UNIT_SKILL_IDS_INVISIBILITY)) || (!Collections.disjoint (enemyUnit.listBasicSkillIDs (), skillsThatNegateInvisibility))))
						{
							// Is it faster?
							if ((fastestEnemyUnit == null) || (fastestEnemyUnit < enemyUnit.getMovementSpeed ()))
								fastestEnemyUnit = enemyUnit.getMovementSpeed ();
						}
					}
					
					// Are we faster, the same, or slower than the fastest enemy unit that can pursue us?
					if ((fastestEnemyUnit == null) || (fastestEnemyUnit < xu.getMovementSpeed ()))
						chance = CombatFleeChance.EASILY_FLEE;
					else if (fastestEnemyUnit.equals (xu.getMovementSpeed ()))
						chance = CombatFleeChance.CHANCE_TO_FLEE;
					else
						chance = CombatFleeChance.CANNOT_FLEE;
				}
				
				// Add to results
				List<ExpandedUnitDetails> list = result.get (chance);
				if (list == null)
				{
					list = new ArrayList<ExpandedUnitDetails> ();
					result.put (chance, list);
				}
				
				list.add (xu);
			}
		
		return result;
	}
	
	/**
	 * @return expandUnitDetails method
	 */
	public final ExpandUnitDetails getExpandUnitDetails ()
	{
		return expandUnitDetails;
	}

	/**
	 * @param e expandUnitDetails method
	 */
	public final void setExpandUnitDetails (final ExpandUnitDetails e)
	{
		expandUnitDetails = e;
	}
}