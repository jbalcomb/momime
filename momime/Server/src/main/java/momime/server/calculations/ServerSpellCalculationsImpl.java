package momime.server.calculations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.sessionbase.PlayerType;
import com.ndg.utils.random.RandomUtils;

import jakarta.xml.bind.JAXBException;
import momime.common.database.CommonDatabase;
import momime.common.database.CommonDatabaseConstants;
import momime.common.database.Pick;
import momime.common.database.PickType;
import momime.common.database.PickTypeCountContainer;
import momime.common.database.PickTypeGrantsSpells;
import momime.common.database.RecordNotFoundException;
import momime.common.database.Spell;
import momime.common.messages.MomPersistentPlayerPrivateKnowledge;
import momime.common.messages.PlayerPick;
import momime.common.messages.SpellResearchStatus;
import momime.common.messages.SpellResearchStatusID;
import momime.common.messages.servertoclient.UpdateRemainingResearchCostMessage;
import momime.common.utils.SpellUtils;

/**
 * Calculations pertaining to spells that are only used on the server
 */
public final class ServerSpellCalculationsImpl implements ServerSpellCalculations
{
	/** The number of choices we're offered from which to pick a spell to research */
	private final static int SPELL_COUNT_TO_PICK_RESEARCH_FROM = 8;
	
	/** Number of books in any one realm needed to get the Spell of Mastery research cost reduction */
	private final static int SPELL_OF_MASTERY_BOOKS_FOR_REDUCTION = 11;
	
	/** Spell of Mastery research cost reduction for having enough books in any one realm */
	private final static int SPELL_OF_MASTERY_REDUCTION_FROM_BOOKS = 3000;

	/** Spell utils */
	private SpellUtils spellUtils;
	
	/** Random number generator */
	private RandomUtils randomUtils;
	
	/**
	 * For all the spells that we did NOT get for free at the start of the game, decides whether or not they are in our spell book to be available to be researched
	 *
	 * This also marks which spells are 'not in spell book', i.e. spells which aren't in our spell book but are still of a rank so we can get the spell via other means (trading, banishing
	 * or treasure from nodes/lairs/towers), e.g. 3 books gives you 1 very rare spell in your book (researchable) but the other 9 will be 'not in spell book' (they are not 'unavailable')
	 *
	 * @param spells List of spells to update
	 * @param picks List of the picks that we gaining spells from
	 * @param db Lookup lists built over the XML database
	 * @throws RecordNotFoundException If we encounter a pick or other item that we can't find in the cache
	 */
	@Override
	public final void randomizeResearchableSpells (final List<SpellResearchStatus> spells, final List<PlayerPick> picks,
		final CommonDatabase db)
		throws RecordNotFoundException
	{
		final List<String> spellIdsToMakeResearchable = new ArrayList<String> ();
		final List<String> spellIdsToMakeNotInBook = new ArrayList<String> ();

		for (final PlayerPick thisPick : picks)
		{
			final Pick pickRecord = db.findPick (thisPick.getPickID (), "randomizeResearchableSpells");
			final PickType pickTypeRecord = db.findPickType (pickRecord.getPickType (), "randomizeResearchableSpells");

			// Look for an entry for the quantity of this pick that we have - failing to find this is fine,
			// it just means that this number of this pick type doesn't give us anything e.g. its a retort, not a number of books
			PickTypeCountContainer spellCounts = null;
			final Iterator<PickTypeCountContainer> iter = pickTypeRecord.getPickTypeCount ().iterator ();
			while ((spellCounts == null) && (iter.hasNext ()))
			{
				final PickTypeCountContainer thisSpellCounts = iter.next ();
				if (thisSpellCounts.getCount () == thisPick.getQuantity ())
					spellCounts = thisSpellCounts;
			}

			if (spellCounts != null)
				for (final PickTypeGrantsSpells thisRank : spellCounts.getSpellCount ())
				{
					// Take off how many spells of this realm/rank we already know, or are already in our spell book waiting to be researched
					// This should allow this routine to be used again when we gain a new spell book
					int availableCount = thisRank.getSpellsAvailable () -
						getSpellUtils ().getSpellsForRealmRankStatus (spells, thisPick.getPickID (), thisRank.getSpellRank (), SpellResearchStatusID.AVAILABLE, db).size () -
						getSpellUtils ().getSpellsForRealmRankStatus (spells, thisPick.getPickID (), thisRank.getSpellRank (), SpellResearchStatusID.RESEARCHABLE_NOW, db).size () -
						getSpellUtils ().getSpellsForRealmRankStatus (spells, thisPick.getPickID (), thisRank.getSpellRank (), SpellResearchStatusID.RESEARCHABLE, db).size ();

					if (availableCount > 0)
					{
						// List all the spells of this realm/rank that aren't yet in our spell book
						final List<Spell> spellsToChooseFrom = getSpellUtils ().getSpellsNotInBookForRealmAndRank (spells, thisPick.getPickID (), thisRank.getSpellRank (), db);

						// Randomly pick some to make researchable
						while ((availableCount > 0) && (spellsToChooseFrom.size () > 0))
						{
							final Spell chosenSpell = spellsToChooseFrom.get (getRandomUtils ().nextInt (spellsToChooseFrom.size ()));

							spellIdsToMakeResearchable.add (chosenSpell.getSpellID ());
							spellsToChooseFrom.remove (chosenSpell);
							availableCount--;
						}

						// Mark all the rest as gettable-by-other-means
						for (final Spell thisSpell : spellsToChooseFrom)
							spellIdsToMakeNotInBook.add (thisSpell.getSpellID ());
					}
				}
		}

		// Update spell statuses
		if ((spellIdsToMakeResearchable.size () > 0) || (spellIdsToMakeNotInBook.size () > 0))
			for (final SpellResearchStatus researchStatus : spells)
			{
				if (spellIdsToMakeResearchable.contains (researchStatus.getSpellID ()))
					researchStatus.setStatus (SpellResearchStatusID.RESEARCHABLE);

				else if (spellIdsToMakeNotInBook.contains (researchStatus.getSpellID ()))
					researchStatus.setStatus (SpellResearchStatusID.NOT_IN_SPELL_BOOK);
			}
	}

	/**
	 * Picks the 8 spells we can choose from to research next
	 *
	 * @param spells List of spells to update
	 * @param db Lookup lists built over the XML database
	 * @throws RecordNotFoundException If there is a spell in the list of research statuses that doesn't exist in the DB
	 */
	@Override
	public final void randomizeSpellsResearchableNow (final List<SpellResearchStatus> spells, final CommonDatabase db)
		throws RecordNotFoundException
	{
		// First find how many 'researchable now' spells we already have - maybe we already have 8 and have nothing to do
		int researchableNow = getSpellUtils ().getSpellsForStatus (spells, SpellResearchStatusID.RESEARCHABLE_NOW, db).size ();
		if (researchableNow < SPELL_COUNT_TO_PICK_RESEARCH_FROM)
		{
			final List<String> spellIdsToMakeResearchableNow = new ArrayList<String> ();

			// Get a list of the spell ranks for which we have spells we can research, and sort it
			// This is because we must research (or put onto the research screen) all common spells before moving up to uncommon, etc
			final List<String> spellRanks = getSpellUtils ().getSpellRanksForStatus (spells, SpellResearchStatusID.RESEARCHABLE, db);
			Collections.sort (spellRanks);

			// Keep going until all are chosen
			while ((researchableNow < SPELL_COUNT_TO_PICK_RESEARCH_FROM) && (spellRanks.size () > 0))
			{
				// Check spells of this rank
				final List<Spell> spellChoices = getSpellUtils ().getSpellsForRankAndStatus (spells, spellRanks.get (0), SpellResearchStatusID.RESEARCHABLE, db);
				while ((researchableNow < SPELL_COUNT_TO_PICK_RESEARCH_FROM) && (spellChoices.size () > 0))
				{
					// Pick a random spell
					final Spell chosenSpell = spellChoices.get (getRandomUtils ().nextInt (spellChoices.size ()));

					spellIdsToMakeResearchableNow.add (chosenSpell.getSpellID ());
					spellChoices.remove (chosenSpell);
					researchableNow++;
				}

				// Finished this spell rank (doesn't matter if we didn't actually use up all this rank because we reached 8 'researchable now' spells, in that case the loop will exit anyway)
				spellRanks.remove (0);
			}

			// Update spell statuses
			if (spellIdsToMakeResearchableNow.size () > 0)
				for (final SpellResearchStatus researchStatus : spells)
					if (spellIdsToMakeResearchableNow.contains (researchStatus.getSpellID ()))
						researchStatus.setStatus (SpellResearchStatusID.RESEARCHABLE_NOW);
		}
	}
	
	/**
	 * @param tradeableSpells Spells known by the player who is giving away a spell
	 * @param knownSpells Spells known by the player who is being given a spell
	 * @return List of tradeable spells
	 */
	@Override
	public final List<String> listTradeableSpells (final List<SpellResearchStatus> tradeableSpells, final List<SpellResearchStatus> knownSpells)
	{
		final Set<String> learnableSpellIDs = knownSpells.stream ().filter (s -> (s.getStatus () == SpellResearchStatusID.NOT_IN_SPELL_BOOK) ||
			(s.getStatus () == SpellResearchStatusID.RESEARCHABLE) || (s.getStatus () == SpellResearchStatusID.RESEARCHABLE_NOW)).map (s -> s.getSpellID ()).collect (Collectors.toSet ());

		return tradeableSpells.stream ().filter
			(s -> (s.getStatus () == SpellResearchStatusID.AVAILABLE) && (learnableSpellIDs.contains (s.getSpellID ())) && (!s.getSpellID ().equals (CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY))).map
			(s -> s.getSpellID ()).collect (Collectors.toList ());
	}

	/**
	 * @param spellIDs List of candidate spells
	 * @param count Maximum number of spells to return
	 * @param db Lookup lists built over the XML database
	 * @return List of (count) cheapest spells from the list
	 * @throws RecordNotFoundException If there's a spell in the list that can't be found in the database
	 */
	@Override
	public final List<String> findCheapestSpells (final List<String> spellIDs, final int count, final CommonDatabase db)
		throws RecordNotFoundException
	{
		// Look up the research cost of every spell
		final Map<String, Integer> researchCosts = new HashMap<String, Integer> ();
		final List<String> cheapestSpells = new ArrayList<String> ();
		for (final String spellID : spellIDs)
		{
			final Spell spellDef = db.findSpell (spellID, "findCheapestSpells");
			if ((spellDef.getResearchCost () != null) && (spellDef.getResearchCost () > 0))
			{
				researchCosts.put (spellID, spellDef.getResearchCost ());
				cheapestSpells.add (spellID);
			}
		}
		
		// Sort the list
		cheapestSpells.sort ((s1, s2) -> researchCosts.get (s1) - researchCosts.get (s2));
		
		// Reduce the size of the list if too many spells are listed
		while (cheapestSpells.size () > count)
			cheapestSpells.remove (count);
		
		return cheapestSpells;
	}
	
	/**
	 * @param priv Private knowledge of the player
	 * @param db Lookup lists built over the XML database
	 * @return Full research cost of the Spell of Mastery for this player, i.e. 60,000 less any reductions they've earned, before any research is put towards it
	 * @throws RecordNotFoundException If the Spell of Mastery can't be found in the database
	 */
	@Override
	public final int calculateSpellOfMasteryResearchCost (final MomPersistentPlayerPrivateKnowledge priv, final CommonDatabase db)
		throws RecordNotFoundException
	{
		return getSpellUtils ().getFullResearchCost (db.findSpell (CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY, "calculateSpellOfMasteryResearchCost"), priv);
	}
	
	/**
	 * Applies the reduction to the Spell of Mastery research cost for having 11 or more books in any one realm.
	 * Call once at the start of the game, after the player's picks are chosen.
	 * 
	 * @param priv Private knowledge of the player
	 * @param picks Player's picks
	 * @param db Lookup lists built over the XML database
	 * @throws RecordNotFoundException If one of the picks or the Spell of Mastery can't be found in the database
	 */
	@Override
	public final void applyStartingSpellOfMasteryResearchCostReduction (final MomPersistentPlayerPrivateKnowledge priv, final List<PlayerPick> picks, final CommonDatabase db)
		throws RecordNotFoundException
	{
		boolean enoughBooks = false;
		for (final PlayerPick pick : picks)
			if ((pick.getQuantity () >= SPELL_OF_MASTERY_BOOKS_FOR_REDUCTION) &&
				(CommonDatabaseConstants.PICK_TYPE_ID_SPELL_BOOK.equals (db.findPick (pick.getPickID (), "applyStartingSpellOfMasteryResearchCostReduction").getPickType ())))
				
				enoughBooks = true;
		
		if (enoughBooks)
			reduceSpellOfMasteryResearchCost (priv, SPELL_OF_MASTERY_REDUCTION_FROM_BOOKS);
	}
	
	/**
	 * Reduces the research cost of the Spell of Mastery for this player, updating both the stored reduction and the remaining research cost.
	 * Doesn't tell the client.
	 * 
	 * @param priv Private knowledge of the player
	 * @param reduction Amount to reduce the research cost by
	 * @return New remaining research cost of the Spell of Mastery; or null if it didn't change, e.g. because the player has already researched it
	 * @throws RecordNotFoundException If the player has no research status for the Spell of Mastery
	 */
	@Override
	public final Integer reduceSpellOfMasteryResearchCost (final MomPersistentPlayerPrivateKnowledge priv, final int reduction)
		throws RecordNotFoundException
	{
		Integer newRemainingResearchCost = null;
		if (reduction > 0)
		{
			priv.setSpellOfMasteryResearchCostReduction (((priv.getSpellOfMasteryResearchCostReduction () == null) ? 0 : priv.getSpellOfMasteryResearchCostReduction ()) + reduction);
			
			final SpellResearchStatus status = getSpellUtils ().findSpellResearchStatus (priv.getSpellResearchStatus (), CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY);
			if ((status != null) && (status.getStatus () != SpellResearchStatusID.AVAILABLE) &&
				(status.getRemainingResearchCost () > 0))
			{
				newRemainingResearchCost = Math.max (0, status.getRemainingResearchCost () - reduction);
				status.setRemainingResearchCost (newRemainingResearchCost);
			}
		}
		
		return newRemainingResearchCost;
	}
	
	/**
	 * Call whenever a player acquires a spell during the game - by research, trading, stealing from a wizard's fortress or as treasure - but NOT for
	 * the spells they start the game with.  In MoM 1.31 this reduces the research cost of the Spell of Mastery by half the acquired spell's research cost.
	 * If this changes the remaining research cost of the Spell of Mastery, human players are sent the new value.
	 * 
	 * @param player Player who acquired the spell
	 * @param spellID Spell they acquired
	 * @param db Lookup lists built over the XML database
	 * @throws RecordNotFoundException If the spell can't be found in the database
	 * @throws JAXBException If there is a problem sending the reply to the client
	 * @throws XMLStreamException If there is a problem sending the reply to the client
	 */
	@Override
	public final void spellAcquiredDuringPlay (final PlayerServerDetails player, final String spellID, final CommonDatabase db)
		throws RecordNotFoundException, JAXBException, XMLStreamException
	{
		if (!CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY.equals (spellID))
		{
			final Integer researchCost = db.findSpell (spellID, "spellAcquiredDuringPlay").getResearchCost ();
			if (researchCost != null)
			{
				final MomPersistentPlayerPrivateKnowledge priv = (MomPersistentPlayerPrivateKnowledge) player.getPersistentPlayerPrivateKnowledge ();
				if (reduceSpellOfMasteryResearchCost (priv, researchCost / 2) != null)
					sendSpellOfMasteryResearchCost (player);
			}
		}
	}
	
	/**
	 * Tells a human player the remaining research cost of the Spell of Mastery and how much it has been reduced by,
	 * so the client can show the right total cost and progress.  Does nothing for AI players or if they've already researched it.
	 * 
	 * @param player Player to send to
	 * @throws RecordNotFoundException If the player has no research status for the Spell of Mastery
	 * @throws JAXBException If there is a problem sending the reply to the client
	 * @throws XMLStreamException If there is a problem sending the reply to the client
	 */
	@Override
	public final void sendSpellOfMasteryResearchCost (final PlayerServerDetails player)
		throws RecordNotFoundException, JAXBException, XMLStreamException
	{
		if (player.getPlayerDescription ().getPlayerType () == PlayerType.HUMAN)
		{
			final MomPersistentPlayerPrivateKnowledge priv = (MomPersistentPlayerPrivateKnowledge) player.getPersistentPlayerPrivateKnowledge ();
			final SpellResearchStatus status = getSpellUtils ().findSpellResearchStatus (priv.getSpellResearchStatus (), CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY);
			if (status.getStatus () != SpellResearchStatusID.AVAILABLE)
			{
				final UpdateRemainingResearchCostMessage msg = new UpdateRemainingResearchCostMessage ();
				msg.setSpellID (CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY);
				msg.setRemainingResearchCost (status.getRemainingResearchCost ());
				msg.setSpellOfMasteryResearchCostReduction (priv.getSpellOfMasteryResearchCostReduction ());
				player.getConnection ().sendMessageToClient (msg);
			}
		}
	}
	
	/**
	 * @return Spell utils
	 */
	public final SpellUtils getSpellUtils ()
	{
		return spellUtils;
	}

	/**
	 * @param utils Spell utils
	 */
	public final void setSpellUtils (final SpellUtils utils)
	{
		spellUtils = utils;
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
}