package momime.server.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.map.CoordinateSystemUtils;
import com.ndg.map.coordinates.MapCoordinates2DEx;
import com.ndg.map.coordinates.MapCoordinates3DEx;
import com.ndg.multiplayer.server.session.MultiplayerSessionServerUtils;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.utils.random.RandomUtils;

import jakarta.xml.bind.JAXBException;
import momime.common.MomException;
import momime.common.database.CommonDatabaseConstants;
import momime.common.database.Spell;
import momime.common.database.SpellBookSectionID;
import momime.common.database.UnitCombatSideID;
import momime.common.messages.MapAreaOfCombatTiles;
import momime.common.messages.MemoryUnit;
import momime.common.messages.MomCombatTile;
import momime.common.messages.UnitAddBumpTypeID;
import momime.common.utils.CombatFleeChance;
import momime.common.utils.CombatFleeUtils;
import momime.common.utils.ExpandUnitDetails;
import momime.common.utils.ExpandedUnitDetails;
import momime.common.utils.SpellTargetingUtils;
import momime.common.utils.TargetSpellResult;
import momime.common.utils.UnitUtils;
import momime.server.MomSessionVariables;
import momime.server.fogofwar.FogOfWarMidTurnMultiChanges;
import momime.server.fogofwar.KillUnitActionID;
import momime.server.knowledge.CombatDetails;
import momime.server.utils.CombatMapServerUtils;
import momime.server.utils.UnitAddLocation;
import momime.server.utils.UnitServerUtils;

/**
 * More methods dealing with executing combats
 */
public final class CombatHandlingImpl implements CombatHandling
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (CombatHandlingImpl.class);
	
	/** Number of point damage done by vortex doom bolts + lightning bolts */
	private final static int VORTEX_VARIABLE_DAMAGE = 5;
	
	/** Chance of a unit fleeing, if it is the same speed as any enemy.  So chance is 1-in-this-value.  5 would be 20% chance. */
	private final static int CHANCE_TO_FLEE = 5;
	
	/** Methods dealing with combat maps that are only needed on the server */
	private CombatMapServerUtils combatMapServerUtils;
	
	/** Methods that determine whether something is a valid target for a spell */
	private SpellTargetingUtils spellTargetingUtils;
	
	/** Damage processor */
	private DamageProcessor damageProcessor;
	
	/** Unit utils */
	private UnitUtils unitUtils;
	
	/** Coordinate system utils */
	private CoordinateSystemUtils coordinateSystemUtils;
	
	/** Random number generator */
	private RandomUtils randomUtils;
	
	/** expandUnitDetails method */
	private ExpandUnitDetails expandUnitDetails;

	/** Server only helper methods for dealing with players in a session */
	private MultiplayerSessionServerUtils multiplayerSessionServerUtils;
	
	/** Chance of each unit successfully fleeing from combat */
	private CombatFleeUtils combatFleeUtils;
	
	/** Server-only unit utils */
	private UnitServerUtils unitServerUtils;
	
	/** Methods for updating true map + players' memory */
	private FogOfWarMidTurnMultiChanges fogOfWarMidTurnMultiChanges;
	
	/** Combat processing */
	private CombatProcessing combatProcessing;
	
	/** Starting and ending combats */
	private CombatStartAndEnd combatStartAndEnd;
	
	/**
	 * Checks to see if anything special needs to happen when a unit crosses over the border between two combat tiles
	 * 
	 * @param xu Unit that is moving across a border
	 * @param combatLocation Location where the combat is taking place
	 * @param combatMap Combat scenery
	 * @param attackingPlayer The player who attacked to initiate the combat - not necessarily the owner of the 'attacker' unit 
	 * @param defendingPlayer Player who was attacked to initiate the combat - not necessarily the owner of the 'defender' unit
	 * @param moveFrom Cell being moved from
	 * @param moveTo Cell moving into
	 * @param mom Allows accessing server knowledge structures, player list and so on
	 * @return Whether the final unit on one side of combat burned itself to death hence letting the other side win
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 * @throws IOException If there is another kind of problem
	 */
	@Override
	public final boolean crossCombatBorder (final ExpandedUnitDetails xu, final MapCoordinates3DEx combatLocation, final MapAreaOfCombatTiles combatMap,
		final PlayerServerDetails attackingPlayer, final PlayerServerDetails defendingPlayer,
		final MapCoordinates2DEx moveFrom, final MapCoordinates2DEx moveTo, final MomSessionVariables mom)
		throws JAXBException, XMLStreamException, IOException
	{
		boolean combatEnded = false;
		
		// The reason this doesn't look specifically for the border between the from and to tile is mainly for the case where
		// there's no city walls and we move diagonally into the wall of fire from the corners - which case there's no border
		// there but the unit should still get burned - so instead just check if we're outside the walls moving to the inside
		if ((!getCombatMapServerUtils ().isWithinWallOfFire (combatLocation, moveFrom, combatMap,
				mom.getGeneralServerKnowledge ().getTrueMap ().getMaintainedSpell (), mom.getServerDB ())) &&
			(getCombatMapServerUtils ().isWithinWallOfFire (combatLocation, moveTo, combatMap,
				mom.getGeneralServerKnowledge ().getTrueMap ().getMaintainedSpell (), mom.getServerDB ())))
		{
			// Find the spell
			final Spell spellDef = mom.getServerDB ().findSpell (CommonDatabaseConstants.SPELL_ID_WALL_OF_FIRE, "crossCombatBorder");
						
			// Specify 0 for castingPlayerID, since we can hurt ourselves
			if (getSpellTargetingUtils ().isUnitValidTargetForSpell (spellDef, SpellBookSectionID.ATTACK_SPELLS, combatLocation, combatMap, 0, null, null, xu,
				false, mom.getGeneralServerKnowledge ().getTrueMap (), null, mom.getPlayers (), mom.getServerDB ()) == TargetSpellResult.VALID_TARGET)
			{
				final List<ResolveAttackTarget> targetUnits = new ArrayList<ResolveAttackTarget> ();
				targetUnits.add (new ResolveAttackTarget (xu.getMemoryUnit ()));
				
				// castingPlayer (the owner of the wall of fire) has to be the defendingPlayer
				combatEnded = getDamageProcessor ().resolveAttack (null, targetUnits, attackingPlayer, defendingPlayer, null, null, null, null, null, spellDef, null,
					defendingPlayer, combatLocation, true, mom).isCombatEnded ();
			}
		}
		
		return combatEnded;
	}
	
	/**
	 * Checks to see if a Magic Vortex hits any units directly under it or adjacent to it.  It will attack the side who owns it as well.
	 * 
	 * @param vortex The vortex to check damage from
	 * @param attackingPlayer The player who attacked to initiate the combat - not necessarily the owner of the 'attacker' unit 
	 * @param defendingPlayer Player who was attacked to initiate the combat - not necessarily the owner of the 'defender' unit
	 * @param mom Allows accessing server knowledge structures, player list and so on
	 * @return Whether the vortex killed the last unit on one or other side of the combat and ended it or not
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 * @throws IOException If there is another kind of problem
	 */
	@Override
	public final boolean damageFromVortex (final MemoryUnit vortex, final PlayerServerDetails attackingPlayer, final PlayerServerDetails defendingPlayer,
		final MomSessionVariables mom)
		throws JAXBException, XMLStreamException, IOException
	{
		final PlayerServerDetails castingPlayer = getMultiplayerSessionServerUtils ().findPlayerWithID (mom.getPlayers (), vortex.getOwningPlayerID (), "damageFromVortex");
		
		final CombatDetails combatDetails = getCombatMapServerUtils ().findCombatByLocation (mom.getCombatDetails (),
			(MapCoordinates3DEx) vortex.getCombatLocation (), "damageFromVortex");
		
		// Vortexes rack up damage to the city if they are in the 4x4 town area - so check the specific tile where the vortex is
		final List<String> cityTiles = mom.getServerDB ().getCombatTileType ().stream ().filter
			(t -> (t.isInsideCity () != null) && (t.isInsideCity ())).map (t -> t.getCombatTileTypeID ()).collect (Collectors.toList ());
		
		final MomCombatTile combatTile = combatDetails.getCombatMap ().getRow ().get (vortex.getCombatPosition ().getY ()).getCell ().get (vortex.getCombatPosition ().getX ());
		
		if (combatTile.getTileLayer ().stream ().anyMatch (l -> cityTiles.contains (l.getCombatTileTypeID ())))
			combatDetails.setCollateralAccumulator (combatDetails.getCollateralAccumulator () + 5);
		
		// Build a list of all the units being attacked
		final List<ResolveAttackTarget> defenders = new ArrayList<ResolveAttackTarget> ();
		
		// Is there a unit in the same space as the vortex?
		final MemoryUnit doomUnit = getUnitUtils ().findAliveUnitInCombatAt (mom.getGeneralServerKnowledge ().getTrueMap ().getUnit (),
			(MapCoordinates3DEx) vortex.getCombatLocation (), (MapCoordinates2DEx) vortex.getCombatPosition (), mom.getServerDB (), false);
		if (doomUnit != null)
		{
			// Use the Doom Bolt spell definition, which has all the magic realm, damage type etc set correctly, and just override the damage
			final Spell doomBoltSpell = mom.getServerDB ().findSpell (CommonDatabaseConstants.SPELL_ID_DOOM_BOLT, "damageFromVortex");
			final ExpandedUnitDetails xuDoomUnit = getExpandUnitDetails ().expandUnitDetails (doomUnit, null, null, doomBoltSpell.getSpellRealm (),
				mom.getPlayers (), mom.getGeneralServerKnowledge ().getTrueMap (), mom.getServerDB ());

			if (getSpellTargetingUtils ().isUnitValidTargetForSpell (doomBoltSpell, null, (MapCoordinates3DEx) vortex.getCombatLocation (), combatDetails.getCombatMap (),
				0, null, VORTEX_VARIABLE_DAMAGE, xuDoomUnit, false, mom.getGeneralServerKnowledge ().getTrueMap (), null,
				mom.getPlayers (), mom.getServerDB ()) == TargetSpellResult.VALID_TARGET)
			{
				final ResolveAttackTarget doomUnitTarget = new ResolveAttackTarget (doomUnit);
				doomUnitTarget.setSpellOverride (doomBoltSpell);
				defenders.add (doomUnitTarget);
			}
		}
		
		// Are there any units in the 8 tiles adjacent to the vortex?
		Spell lightningBoltSpell = null;
		for (int d = 1; d <= getCoordinateSystemUtils ().getMaxDirection (mom.getSessionDescription ().getCombatMapSize ().getCoordinateSystemType ()); d++)
		{
			final MapCoordinates2DEx coords = new MapCoordinates2DEx ((MapCoordinates2DEx) vortex.getCombatPosition ());
			if (getCoordinateSystemUtils ().move2DCoordinates (mom.getSessionDescription ().getCombatMapSize (), coords, d))
			{
				final MemoryUnit lightningUnit = getUnitUtils ().findAliveUnitInCombatAt (mom.getGeneralServerKnowledge ().getTrueMap ().getUnit (),
					(MapCoordinates3DEx) vortex.getCombatLocation (), coords, mom.getServerDB (), false);
				if ((lightningUnit != null) && (getRandomUtils ().nextInt (3) == 0))
				{
					// Use the Lightning Bolt spell definition, which has all the magic realm, damage type etc set correctly, and just override the damage
					if (lightningBoltSpell == null)
						lightningBoltSpell = mom.getServerDB ().findSpell (CommonDatabaseConstants.SPELL_ID_LIGHTNING_BOLT, "damageFromVortex");
					
					final ExpandedUnitDetails xuLightningUnit = getExpandUnitDetails ().expandUnitDetails (lightningUnit, null, null, lightningBoltSpell.getSpellRealm (),
						mom.getPlayers (), mom.getGeneralServerKnowledge ().getTrueMap (), mom.getServerDB ());

					if (getSpellTargetingUtils ().isUnitValidTargetForSpell (lightningBoltSpell, null, (MapCoordinates3DEx) vortex.getCombatLocation (), combatDetails.getCombatMap (),
						0, null, VORTEX_VARIABLE_DAMAGE, xuLightningUnit, false, mom.getGeneralServerKnowledge ().getTrueMap (), null,
						mom.getPlayers (), mom.getServerDB ()) == TargetSpellResult.VALID_TARGET)
					{
						final ResolveAttackTarget lightningUnitTarget = new ResolveAttackTarget (lightningUnit);
						lightningUnitTarget.setSpellOverride (lightningBoltSpell);
						defenders.add (lightningUnitTarget);
					}
				}
			}
		}
		
		// Any attack to do?
		final boolean combatEnded;
		if (defenders.size () == 0)
			combatEnded = false;
		else
		{
			// Find the spell that summoned the magic vortex, so the damage log says magic vortex attacking, rather than lightning bolt or doom bolt
			final Spell magicVortexSpell = mom.getServerDB ().getSpell ().stream ().filter
				(s -> s.getSummonedUnit ().contains (vortex.getUnitID ())).findAny ().orElse (null);
			
			if (magicVortexSpell == null)
				throw new MomException ("damageFromVortex can't find the spell that summoned the vortex");
			
			combatEnded = getDamageProcessor ().resolveAttack (vortex, defenders, attackingPlayer, defendingPlayer, null, null, null, null, null,
				magicVortexSpell, VORTEX_VARIABLE_DAMAGE, castingPlayer, (MapCoordinates3DEx) vortex.getCombatLocation (), true, mom).isCombatEnded ();
		}
		
		return combatEnded;
	}

	/**
	 * A players units try to flee from combat 
	 * 
	 * @param combatDetails Details about the combat taking place
	 * @param playerID Which player is trying to flee from the combat
	 * @param mom Allows accessing server knowledge structures, player list and so on
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 * @throws IOException If there is another kind of problem
	 */
	@Override
	public final void fleeFromCombat (final CombatDetails combatDetails, final int playerID, final MomSessionVariables mom)
		throws JAXBException, XMLStreamException, IOException
	{
		final PlayerServerDetails attackingPlayer = getMultiplayerSessionServerUtils ().findPlayerWithID (mom.getPlayers (), combatDetails.getAttackingPlayerID (), "fleeFromCombat (A)");
		final PlayerServerDetails defendingPlayer = getMultiplayerSessionServerUtils ().findPlayerWithID (mom.getPlayers (), combatDetails.getDefendingPlayerID (), "fleeFromCombat (D)");
		
		final PlayerServerDetails fleeingPlayer = (combatDetails.getAttackingPlayerID () == playerID) ? attackingPlayer : defendingPlayer;
		final UnitCombatSideID fleeingSide = (combatDetails.getAttackingPlayerID () == playerID) ? UnitCombatSideID.ATTACKER : UnitCombatSideID.DEFENDER;
		
		final Map<CombatFleeChance, List<ExpandedUnitDetails>> chances = getCombatFleeUtils ().determineChanceOfFleeing
			(combatDetails.getCombatLocation (), playerID, mom.getPlayers (), mom.getGeneralServerKnowledge ().getTrueMap (), mom.getServerDB ());
		
		// Combat summons like Phantom Warriors only exist for the duration of the battle, so they never escape to the overland map.
		// Otherwise, taking them out of combat here means purgeDeadUnitsAndCombatSummonsFromCombat no longer finds them at the
		// combat location when the combat ends, and they'd become permanent overland units.  So instead they're simply dispelled.
		// This applies whether they'd have made their roll to flee or not - otherwise a player whose real units all escaped could be left
		// stuck in a combat with only summons that can't leave it.
		// (Units brought back with Raise Dead or Animate Dead are not combat summons - they become permanent units, so can flee like any other.)
		boolean anySummonsDispelled = false;
		for (final List<ExpandedUnitDetails> units : chances.values ())
			for (final ExpandedUnitDetails xu : units)
				if (xu.wasSummonedInCombat ())
				{
					log.debug ("Combat summon unit URN " + xu.getUnitURN () + " is dispelled as its side flees from combat");
					mom.getWorldUpdates ().killUnit (xu.getUnitURN (), KillUnitActionID.PERMANENT_DAMAGE);
					anySummonsDispelled = true;
				}

		if (anySummonsDispelled)
			mom.getWorldUpdates ().process (mom);

		// Make rolls for each unit
		for (final Entry<CombatFleeChance, List<ExpandedUnitDetails>> chance : chances.entrySet ())
			if (chance.getKey () != CombatFleeChance.CANNOT_FLEE)
				for (final ExpandedUnitDetails xu : chance.getValue ())

					// Make roll to flee or not
					if ((!xu.wasSummonedInCombat ()) && ((chance.getKey () == CombatFleeChance.EASILY_FLEE) || (getRandomUtils ().nextInt (CHANCE_TO_FLEE) == 0)))
					{
						boolean unitFlees = true;
						MapCoordinates3DEx unitFleesTo = null;
						log.debug ("Unit URN " + xu.getUnitURN () + " successfully flees from combat");
						
						// If its a defending unit then figure out where it flees to.  If its an attacking unit then it stays where it is, as the attacker doesn't advance until they
						// win a combat.  Effecitively an attacking unit becomes just like a unit in the attacking tile that decided to not be part of the attacking stack.
						if (combatDetails.getDefendingPlayerID () == playerID)
						{
							final UnitAddLocation recallLocation = getUnitServerUtils ().findNearestLocationWhereUnitCanBeMoved (combatDetails.getCombatLocation (), true, xu, mom);
							if (recallLocation.getBumpType () == UnitAddBumpTypeID.NO_ROOM)
								unitFlees = false;
							else
								unitFleesTo = recallLocation.getUnitLocation ();
						}

						if (unitFlees)
						{
							// Take the unit out of combat
							getCombatProcessing ().setUnitIntoOrTakeUnitOutOfCombat (attackingPlayer, defendingPlayer, xu.getMemoryUnit (),
								combatDetails.getCombatLocation (), null, null, null, null, null, mom);
	
							// Its possible this could kill the unit, if it has extra HP from Lionheart cast in combat that we now lose
							getFogOfWarMidTurnMultiChanges ().switchOffSpellsCastInCombatOnUnit (xu.getUnitURN (), mom);
							
							mom.getWorldUpdates ().process (mom);
							
							// If its a defending unit that needs to move and its still alive, the move it 1 square
							if ((getUnitUtils ().findUnitURN (xu.getUnitURN (), mom.getGeneralServerKnowledge ().getTrueMap ().getUnit ()) != null) && (unitFleesTo != null))
								getFogOfWarMidTurnMultiChanges ().moveUnitStackOneCellOnServerAndClients (Arrays.asList (xu.getMemoryUnit ()),
									fleeingPlayer, combatDetails.getCombatLocation (), unitFleesTo, mom);
						}
					}
		
		// If all of our units fled combat, then we lose
		if (getDamageProcessor ().countUnitsInCombat (combatDetails.getCombatLocation (), fleeingSide,
			mom.getGeneralServerKnowledge ().getTrueMap ().getUnit (), mom.getServerDB ()) == 0)
		{
			final PlayerServerDetails winningPlayer = (fleeingPlayer == defendingPlayer) ? attackingPlayer : defendingPlayer;
			
			getCombatStartAndEnd ().combatEnded (combatDetails, attackingPlayer, defendingPlayer, winningPlayer, null, mom, false);
		}
	}
	
	/**
	 * @return Methods dealing with combat maps that are only needed on the server
	 */
	public final CombatMapServerUtils getCombatMapServerUtils ()
	{
		return combatMapServerUtils;
	}

	/**
	 * @param u Methods dealing with combat maps that are only needed on the server
	 */
	public final void setCombatMapServerUtils (final CombatMapServerUtils u)
	{
		combatMapServerUtils = u;
	}

	/**
	 * @return Methods that determine whether something is a valid target for a spell
	 */
	public final SpellTargetingUtils getSpellTargetingUtils ()
	{
		return spellTargetingUtils;
	}

	/**
	 * @param s Methods that determine whether something is a valid target for a spell
	 */
	public final void setSpellTargetingUtils (final SpellTargetingUtils s)
	{
		spellTargetingUtils = s;
	}

	/**
	 * @return Damage processor
	 */
	public final DamageProcessor getDamageProcessor ()
	{
		return damageProcessor;
	}

	/**
	 * @param proc Damage processor
	 */
	public final void setDamageProcessor (final DamageProcessor proc)
	{
		damageProcessor = proc;
	}

	/**
	 * @return Unit utils
	 */
	public final UnitUtils getUnitUtils ()
	{
		return unitUtils;
	}

	/**
	 * @param utils Unit utils
	 */
	public final void setUnitUtils (final UnitUtils utils)
	{
		unitUtils = utils;
	}

	/**
	 * @return Coordinate system utils
	 */
	public final CoordinateSystemUtils getCoordinateSystemUtils ()
	{
		return coordinateSystemUtils;
	}

	/**
	 * @param utils Coordinate system utils
	 */
	public final void setCoordinateSystemUtils (final CoordinateSystemUtils utils)
	{
		coordinateSystemUtils = utils;
	}

	/**
	 * @return Random number generator
	 */
	public final RandomUtils getRandomUtils ()
	{
		return randomUtils;
	}

	/**
	 * @param utils Random number generator
	 */
	public final void setRandomUtils (final RandomUtils utils)
	{
		randomUtils = utils;
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

	/**
	 * @return Server only helper methods for dealing with players in a session
	 */
	public final MultiplayerSessionServerUtils getMultiplayerSessionServerUtils ()
	{
		return multiplayerSessionServerUtils;
	}

	/**
	 * @param obj Server only helper methods for dealing with players in a session
	 */
	public final void setMultiplayerSessionServerUtils (final MultiplayerSessionServerUtils obj)
	{
		multiplayerSessionServerUtils = obj;
	}

	/**
	 * @return Chance of each unit successfully fleeing from combat
	 */
	public final CombatFleeUtils getCombatFleeUtils ()
	{
		return combatFleeUtils;
	}

	/**
	 * @param c Chance of each unit successfully fleeing from combat
	 */
	public final void setCombatFleeUtils (final CombatFleeUtils c)
	{
		combatFleeUtils = c;
	}

	/**
	 * @return Server-only unit utils
	 */
	public final UnitServerUtils getUnitServerUtils ()
	{
		return unitServerUtils;
	}

	/**
	 * @param utils Server-only unit utils
	 */
	public final void setUnitServerUtils (final UnitServerUtils utils)
	{
		unitServerUtils = utils;
	}

	/**
	 * @return Methods for updating true map + players' memory
	 */
	public final FogOfWarMidTurnMultiChanges getFogOfWarMidTurnMultiChanges ()
	{
		return fogOfWarMidTurnMultiChanges;
	}

	/**
	 * @param obj Methods for updating true map + players' memory
	 */
	public final void setFogOfWarMidTurnMultiChanges (final FogOfWarMidTurnMultiChanges obj)
	{
		fogOfWarMidTurnMultiChanges = obj;
	}
	
	/**
	 * @return Combat processing
	 */
	public final CombatProcessing getCombatProcessing ()
	{
		return combatProcessing;
	}

	/**
	 * @param proc Combat processing
	 */
	public final void setCombatProcessing (final CombatProcessing proc)
	{
		combatProcessing = proc;
	}

	/**
	 * @return Starting and ending combats
	 */
	public final CombatStartAndEnd getCombatStartAndEnd ()
	{
		return combatStartAndEnd;
	}

	/**
	 * @param cse Starting and ending combats
	 */
	public final void setCombatStartAndEnd (final CombatStartAndEnd cse)
	{
		combatStartAndEnd = cse;
	}
}