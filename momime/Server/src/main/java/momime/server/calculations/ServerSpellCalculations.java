package momime.server.calculations;

import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.ndg.multiplayer.server.session.PlayerServerDetails;

import jakarta.xml.bind.JAXBException;
import momime.common.database.CommonDatabase;
import momime.common.database.RecordNotFoundException;
import momime.common.messages.MomPersistentPlayerPrivateKnowledge;
import momime.common.messages.PlayerPick;
import momime.common.messages.SpellResearchStatus;

/**
 * Calculations pertaining to spells that are only used on the server
 */
public interface ServerSpellCalculations
{
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
	public void randomizeResearchableSpells (final List<SpellResearchStatus> spells, final List<PlayerPick> picks,
		final CommonDatabase db)
		throws RecordNotFoundException;

	/**
	 * Picks the 8 spells we can choose from to research next
	 *
	 * @param spells List of spells to update
	 * @param db Lookup lists built over the XML database
	 * @throws RecordNotFoundException If there is a spell in the list of research statuses that doesn't exist in the DB
	 */
	public void randomizeSpellsResearchableNow (final List<SpellResearchStatus> spells, final CommonDatabase db)
		throws RecordNotFoundException;
	
	/**
	 * @param tradeableSpells Spells known by the player who is giving away a spell
	 * @param knownSpells Spells known by the player who is being given a spell
	 * @return List of tradeable spells
	 */
	public List<String> listTradeableSpells (final List<SpellResearchStatus> tradeableSpells, final List<SpellResearchStatus> knownSpells);
	
	/**
	 * @param spellIDs List of candidate spells
	 * @param count Maximum number of spells to return
	 * @param db Lookup lists built over the XML database
	 * @return List of (count) cheapest spells from the list
	 * @throws RecordNotFoundException If there's a spell in the list that can't be found in the database
	 */
	public List<String> findCheapestSpells (final List<String> spellIDs, final int count, final CommonDatabase db)
		throws RecordNotFoundException;

	/**
	 * @param priv Private knowledge of the player
	 * @param db Lookup lists built over the XML database
	 * @return Full research cost of the Spell of Mastery for this player, i.e. 60,000 less any reductions they've earned, before any research is put towards it
	 * @throws RecordNotFoundException If the Spell of Mastery can't be found in the database
	 */
	public int calculateSpellOfMasteryResearchCost (final MomPersistentPlayerPrivateKnowledge priv, final CommonDatabase db)
		throws RecordNotFoundException;
	
	/**
	 * Applies the reduction to the Spell of Mastery research cost for having 11 or more books in any one realm.
	 * Call once at the start of the game, after the player's picks are chosen.
	 * 
	 * @param priv Private knowledge of the player
	 * @param picks Player's picks
	 * @param db Lookup lists built over the XML database
	 * @throws RecordNotFoundException If one of the picks or the Spell of Mastery can't be found in the database
	 */
	public void applyStartingSpellOfMasteryResearchCostReduction (final MomPersistentPlayerPrivateKnowledge priv, final List<PlayerPick> picks, final CommonDatabase db)
		throws RecordNotFoundException;
	
	/**
	 * Reduces the research cost of the Spell of Mastery for this player, updating both the stored reduction and the remaining research cost.
	 * Doesn't tell the client.
	 * 
	 * @param priv Private knowledge of the player
	 * @param reduction Amount to reduce the research cost by
	 * @return New remaining research cost of the Spell of Mastery; or null if it didn't change, e.g. because the player has already researched it
	 * @throws RecordNotFoundException If the player has no research status for the Spell of Mastery
	 */
	public Integer reduceSpellOfMasteryResearchCost (final MomPersistentPlayerPrivateKnowledge priv, final int reduction)
		throws RecordNotFoundException;
	
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
	public void spellAcquiredDuringPlay (final PlayerServerDetails player, final String spellID, final CommonDatabase db)
		throws RecordNotFoundException, JAXBException, XMLStreamException;
	
	/**
	 * Tells a human player the remaining research cost of the Spell of Mastery and how much it has been reduced by,
	 * so the client can show the right total cost and progress.  Does nothing for AI players or if they've already researched it.
	 * 
	 * @param player Player to send to
	 * @throws RecordNotFoundException If the player has no research status for the Spell of Mastery
	 * @throws JAXBException If there is a problem sending the reply to the client
	 * @throws XMLStreamException If there is a problem sending the reply to the client
	 */
	public void sendSpellOfMasteryResearchCost (final PlayerServerDetails player)
		throws RecordNotFoundException, JAXBException, XMLStreamException;
}
