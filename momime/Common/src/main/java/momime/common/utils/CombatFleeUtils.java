package momime.common.utils;

import java.util.List;
import java.util.Map;

import com.ndg.map.coordinates.MapCoordinates3DEx;
import com.ndg.multiplayer.session.PlayerNotFoundException;
import com.ndg.multiplayer.session.PlayerPublicDetails;

import momime.common.MomException;
import momime.common.database.CommonDatabase;
import momime.common.database.RecordNotFoundException;
import momime.common.messages.FogOfWarMemory;

/**
 * Methods for working out the chance of each unit successfully fleeing from combat
 */
public interface CombatFleeUtils
{
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
	public Map<CombatFleeChance, List<ExpandedUnitDetails>> determineChanceOfFleeing
		(final MapCoordinates3DEx combatLocation, final int ourPlayerID, final List<? extends PlayerPublicDetails> players,
		final FogOfWarMemory mem, final CommonDatabase db)
		throws RecordNotFoundException, PlayerNotFoundException, MomException;
}