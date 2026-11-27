package momime.server.process;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.coordinates.MapCoordinates3DEx;
import com.ndg.multiplayer.server.session.MultiplayerSessionServerUtils;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.sessionbase.PlayerDescription;

import momime.common.database.CommonDatabase;
import momime.common.database.UnitCombatSideID;
import momime.common.messages.FogOfWarMemory;
import momime.common.messages.MemoryUnit;
import momime.common.utils.CombatFleeChance;
import momime.common.utils.CombatFleeUtils;
import momime.common.utils.ExpandedUnitDetails;
import momime.common.utils.UnitUtils;
import momime.server.MomSessionVariables;
import momime.server.fogofwar.FogOfWarMidTurnMultiChanges;
import momime.server.fogofwar.KillUnitActionID;
import momime.server.knowledge.CombatDetails;
import momime.server.messages.MomGeneralServerKnowledge;
import momime.server.worldupdates.WorldUpdates;

/**
 * Tests the CombatHandlingImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestCombatHandlingImpl
{
	/**
	 * @param unitURN Unit URN
	 * @param summonedInCombat Whether the unit was summoned in combat, like Phantom Warriors
	 * @return Mock unit
	 * @throws Exception If there is a problem
	 */
	private final ExpandedUnitDetails createUnit (final int unitURN, final boolean summonedInCombat) throws Exception
	{
		final ExpandedUnitDetails xu = mock (ExpandedUnitDetails.class);
		when (xu.getUnitURN ()).thenReturn (unitURN);
		when (xu.wasSummonedInCombat ()).thenReturn (summonedInCombat);
		return xu;
	}

	/**
	 * Tests the fleeFromCombat method, proving that combat summons are dispelled rather than escaping onto the overland map,
	 * whether they could have fled or not, while regular units flee as normal
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testFleeFromCombat_CombatSummonsDispelled () throws Exception
	{
		// Mock database
		final CommonDatabase db = mock (CommonDatabase.class);

		// Players
		final PlayerDescription attackingPd = new PlayerDescription ();
		attackingPd.setPlayerID (1);
		final PlayerServerDetails attackingPlayer = new PlayerServerDetails (attackingPd, null, null, null, null);

		final PlayerDescription defendingPd = new PlayerDescription ();
		defendingPd.setPlayerID (2);
		final PlayerServerDetails defendingPlayer = new PlayerServerDetails (defendingPd, null, null, null, null);

		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();

		final MultiplayerSessionServerUtils multiplayerSessionServerUtils = mock (MultiplayerSessionServerUtils.class);
		when (multiplayerSessionServerUtils.findPlayerWithID (players, 1, "fleeFromCombat (A)")).thenReturn (attackingPlayer);
		when (multiplayerSessionServerUtils.findPlayerWithID (players, 2, "fleeFromCombat (D)")).thenReturn (defendingPlayer);

		// Combat
		final MapCoordinates3DEx combatLocation = new MapCoordinates3DEx (20, 10, 1);
		final CombatDetails combatDetails = new CombatDetails (1, combatLocation, null, 1, 2, null, null, 0, 0, 0, 0);

		// Session variables
		final FogOfWarMemory trueMap = new FogOfWarMemory ();
		final MomGeneralServerKnowledge gsk = new MomGeneralServerKnowledge ();
		gsk.setTrueMap (trueMap);

		final WorldUpdates wu = mock (WorldUpdates.class);

		final MomSessionVariables mom = mock (MomSessionVariables.class);
		when (mom.getPlayers ()).thenReturn (players);
		when (mom.getServerDB ()).thenReturn (db);
		when (mom.getGeneralServerKnowledge ()).thenReturn (gsk);
		when (mom.getWorldUpdates ()).thenReturn (wu);

		// Attacker's units - a regular unit and a combat summon that can both easily flee, and a combat summon that can't
		final MemoryUnit regularUnit = new MemoryUnit ();
		final ExpandedUnitDetails xuRegular = createUnit (101, false);
		when (xuRegular.getMemoryUnit ()).thenReturn (regularUnit);

		final ExpandedUnitDetails xuFastSummon = createUnit (102, true);
		final ExpandedUnitDetails xuSlowSummon = createUnit (103, true);

		final Map<CombatFleeChance, List<ExpandedUnitDetails>> chances = new HashMap<CombatFleeChance, List<ExpandedUnitDetails>> ();
		chances.put (CombatFleeChance.EASILY_FLEE, Arrays.asList (xuRegular, xuFastSummon));
		chances.put (CombatFleeChance.CANNOT_FLEE, Arrays.asList (xuSlowSummon));

		final CombatFleeUtils combatFleeUtils = mock (CombatFleeUtils.class);
		when (combatFleeUtils.determineChanceOfFleeing (combatLocation, 1, players, trueMap, db)).thenReturn (chances);

		// Nothing left in combat afterwards
		final DamageProcessor damageProcessor = mock (DamageProcessor.class);
		when (damageProcessor.countUnitsInCombat (combatLocation, UnitCombatSideID.ATTACKER, trueMap.getUnit (), db)).thenReturn (0);

		// Set up object to test
		final CombatProcessing combatProcessing = mock (CombatProcessing.class);
		final FogOfWarMidTurnMultiChanges midTurnMulti = mock (FogOfWarMidTurnMultiChanges.class);
		final CombatStartAndEnd combatStartAndEnd = mock (CombatStartAndEnd.class);

		final CombatHandlingImpl handling = new CombatHandlingImpl ();
		handling.setMultiplayerSessionServerUtils (multiplayerSessionServerUtils);
		handling.setCombatFleeUtils (combatFleeUtils);
		handling.setDamageProcessor (damageProcessor);
		handling.setCombatProcessing (combatProcessing);
		handling.setFogOfWarMidTurnMultiChanges (midTurnMulti);
		handling.setCombatStartAndEnd (combatStartAndEnd);
		handling.setUnitUtils (mock (UnitUtils.class));

		// Run method - attacker flees
		handling.fleeFromCombat (combatDetails, 1, mom);

		// Both combat summons are dispelled, even the one that couldn't have fled
		verify (wu).killUnit (102, KillUnitActionID.PERMANENT_DAMAGE);
		verify (wu).killUnit (103, KillUnitActionID.PERMANENT_DAMAGE);
		verify (wu, never ()).killUnit (101, KillUnitActionID.PERMANENT_DAMAGE);

		// Only the regular unit is taken out of combat and keeps going on the overland map
		verify (combatProcessing).setUnitIntoOrTakeUnitOutOfCombat (attackingPlayer, defendingPlayer, regularUnit, combatLocation, null, null, null, null, null, mom);
		verify (midTurnMulti).switchOffSpellsCastInCombatOnUnit (101, mom);
		verify (midTurnMulti, never ()).switchOffSpellsCastInCombatOnUnit (102, mom);
		verify (midTurnMulti, never ()).switchOffSpellsCastInCombatOnUnit (103, mom);

		// Once for the summons, once for the fleeing unit
		verify (wu, times (2)).process (mom);

		// Everything left, so combat is over and the defender won
		verify (combatStartAndEnd).combatEnded (combatDetails, attackingPlayer, defendingPlayer, defendingPlayer, null, mom, false);
	}
}
