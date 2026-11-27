package momime.server.calculations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.sessionbase.PlayerDescription;
import com.ndg.multiplayer.sessionbase.PlayerType;
import com.ndg.utils.random.RandomUtils;

import momime.common.database.CommonDatabase;
import momime.common.database.CommonDatabaseConstants;
import momime.common.database.Pick;
import momime.common.database.Spell;
import momime.common.messages.MomPersistentPlayerPrivateKnowledge;
import momime.common.messages.PlayerPick;
import momime.common.messages.SpellResearchStatus;
import momime.common.messages.SpellResearchStatusID;
import momime.common.messages.servertoclient.UpdateRemainingResearchCostMessage;
import momime.common.utils.SpellUtilsImpl;
import momime.server.DummyServerToClientConnection;
import momime.server.ServerTestData;

/**
 * Tests the ServerSpellCalculations class
 */
@ExtendWith(MockitoExtension.class)
public final class TestServerSpellCalculationsImpl extends ServerTestData
{
	/**
	 * Tests the randomizeResearchableSpells method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testRandomizeResearchableSpells () throws Exception
	{
		final CommonDatabase db = loadServerDatabase ();

		// Every spell is initially unavailbale
		final List<SpellResearchStatus> spells = new ArrayList<SpellResearchStatus> ();
		for (final Spell spell : db.getSpell ())
		{
			final SpellResearchStatus status = new SpellResearchStatus ();
			status.setSpellID (spell.getSpellID ());
			status.setStatus (SpellResearchStatusID.UNAVAILABLE);
			spells.add (status);
		}
		
		// Fix random results - it would be better to put some real values here, not just roll 0s
		final RandomUtils random = mock (RandomUtils.class);
		
		// Set up object to test
		final ServerSpellCalculationsImpl calc = new ServerSpellCalculationsImpl ();
		calc.setSpellUtils (new SpellUtilsImpl ());
		calc.setRandomUtils (random);

		// With only 1 book, we get 3 common and 1 uncommon spell researchable
		// That also means the rest of the common and uncommon spells are obtainable, but rare and very rares aren't
		final List<PlayerPick> picks = new ArrayList<PlayerPick> ();
		final PlayerPick natureBook = new PlayerPick ();
		natureBook.setPickID ("MB04");
		natureBook.setQuantity (1);
		picks.add (natureBook);

		calc.randomizeResearchableSpells (spells, picks, db);

		// Check results
		final List<Integer> reseachableSpellsFirstPass = new ArrayList<Integer> ();
		int researchableCount = 0;
		for (int n = 0; n < 10; n++)
		{
			if (spells.get (n).getStatus ().equals (SpellResearchStatusID.RESEARCHABLE))
			{
				researchableCount++;
				reseachableSpellsFirstPass.add (n);
			}

			else if (!spells.get (n).getStatus ().equals (SpellResearchStatusID.NOT_IN_SPELL_BOOK))
				fail ("Remaining common spells should be marked as not in book");
		}
		assertEquals (3, researchableCount);

		for (int n = 10; n < 20; n++)
		{
			if (spells.get (n).getStatus ().equals (SpellResearchStatusID.RESEARCHABLE))
			{
				researchableCount++;
				reseachableSpellsFirstPass.add (n);
			}

			else if (!spells.get (n).getStatus ().equals (SpellResearchStatusID.NOT_IN_SPELL_BOOK))
				fail ("Remaining uncommon spells should be marked as not in book");
		}
		assertEquals (4, researchableCount);

		for (int n = 20; n < spells.size (); n++)
			assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (n).getStatus ());

		// Research one of those common spells
		spells.get (reseachableSpellsFirstPass.get (0)).setStatus (SpellResearchStatusID.AVAILABLE);

		// Gain an extra book, now we should get 5 common, 2 uncommon, 1 rare
		natureBook.setQuantity (2);
		calc.randomizeResearchableSpells (spells, picks, db);

		// Recheck the counts
		final List<Integer> reseachableSpellsSecondPass = new ArrayList<Integer> ();
		researchableCount = 0;
		for (int n = 0; n < 10; n++)
		{
			// Remember we made 1 available too
			if ((spells.get (n).getStatus ().equals (SpellResearchStatusID.AVAILABLE)) || (spells.get (n).getStatus ().equals (SpellResearchStatusID.RESEARCHABLE)))
			{
				researchableCount++;
				reseachableSpellsSecondPass.add (n);
			}

			else if (!spells.get (n).getStatus ().equals (SpellResearchStatusID.NOT_IN_SPELL_BOOK))
				fail ("Remaining common spells should be marked as not in book");
		}
		assertEquals (5, researchableCount);

		for (int n = 10; n < 20; n++)
		{
			if (spells.get (n).getStatus ().equals (SpellResearchStatusID.RESEARCHABLE))
			{
				researchableCount++;
				reseachableSpellsSecondPass.add (n);
			}

			else if (!spells.get (n).getStatus ().equals (SpellResearchStatusID.NOT_IN_SPELL_BOOK))
				fail ("Remaining uncommon spells should be marked as not in book");
		}
		assertEquals (7, researchableCount);

		for (int n = 20; n < 30; n++)
		{
			if (spells.get (n).getStatus ().equals (SpellResearchStatusID.RESEARCHABLE))
			{
				researchableCount++;
				reseachableSpellsSecondPass.add (n);
			}

			else if (!spells.get (n).getStatus ().equals (SpellResearchStatusID.NOT_IN_SPELL_BOOK))
				fail ("Remaining rare spells should be marked as not in book");
		}
		assertEquals (8, researchableCount);

		for (int n = 30; n < spells.size (); n++)
			assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (n).getStatus ());

		// Check that all the spells that were available the first time were still available the second time,
		// i.e. that we didn't re-randomize the list totally
		for (final Integer spellIndex : reseachableSpellsFirstPass)
			if (!reseachableSpellsSecondPass.contains (spellIndex))
				fail ("Expect spell " + spellIndex + " to still be in second list");
	}

	/**
	 * Tests the randomizeSpellsResearchableNow method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testRandomizeSpellsResearchableNow () throws Exception
	{
		final CommonDatabase db = loadServerDatabase ();

		// Every spell is initially unavailbale
		final List<SpellResearchStatus> spells = new ArrayList<SpellResearchStatus> ();
		for (final Spell spell : db.getSpell ())
		{
			final SpellResearchStatus status = new SpellResearchStatus ();
			status.setSpellID (spell.getSpellID ());
			status.setStatus (SpellResearchStatusID.UNAVAILABLE);
			spells.add (status);
		}

		// Fix random results
		final RandomUtils random = mock (RandomUtils.class);
		
		// Set up object to test
		final ServerSpellCalculationsImpl calc = new ServerSpellCalculationsImpl ();
		calc.setSpellUtils (new SpellUtilsImpl ());
		calc.setRandomUtils (random);
		
		// The key part of this is that it will exhaust lower spell ranks first before moving onto higher ones
		// So lets set 2 spells to already be "researchable", 4 commons and 4 uncommons
		for (int n = 0; n < 2; n++)
			spells.get (n).setStatus (SpellResearchStatusID.RESEARCHABLE_NOW);

		for (int n = 6; n < 14; n++)
			spells.get (n).setStatus (SpellResearchStatusID.RESEARCHABLE);

		calc.randomizeSpellsResearchableNow (spells, db);

		// So should now have our original 2 spells, all 4 commons and 2 out of 4 of the uncommons "researchable now"
		assertEquals (SpellResearchStatusID.RESEARCHABLE_NOW, spells.get (0).getStatus ());
		assertEquals (SpellResearchStatusID.RESEARCHABLE_NOW, spells.get (1).getStatus ());
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (2).getStatus ());
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (3).getStatus ());
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (4).getStatus ());
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (5).getStatus ());
		assertEquals (SpellResearchStatusID.RESEARCHABLE_NOW, spells.get (6).getStatus ());
		assertEquals (SpellResearchStatusID.RESEARCHABLE_NOW, spells.get (7).getStatus ());
		assertEquals (SpellResearchStatusID.RESEARCHABLE_NOW, spells.get (8).getStatus ());
		assertEquals (SpellResearchStatusID.RESEARCHABLE_NOW, spells.get (9).getStatus ());

		// Mocked random number generator will always roll 0s, so we'll get the first 2 uncommons
		assertEquals (SpellResearchStatusID.RESEARCHABLE_NOW, spells.get (10).getStatus ());
		assertEquals (SpellResearchStatusID.RESEARCHABLE_NOW, spells.get (11).getStatus ());
		assertEquals (SpellResearchStatusID.RESEARCHABLE, spells.get (12).getStatus ());
		assertEquals (SpellResearchStatusID.RESEARCHABLE, spells.get (13).getStatus ());

		// Everything higher rank is unavailable yet
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (14).getStatus ());
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (15).getStatus ());
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (16).getStatus ());
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (17).getStatus ());
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (18).getStatus ());
		assertEquals (SpellResearchStatusID.UNAVAILABLE, spells.get (19).getStatus ());
	}

	/**
	 * @param status Research status of the Spell of Mastery
	 * @param remainingResearchCost Remaining research cost of the Spell of Mastery
	 * @return Player knowledge with only the Spell of Mastery in their spell list
	 */
	private final MomPersistentPlayerPrivateKnowledge createSpellOfMasteryKnowledge (final SpellResearchStatusID status, final int remainingResearchCost)
	{
		final SpellResearchStatus som = new SpellResearchStatus ();
		som.setSpellID (CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY);
		som.setStatus (status);
		som.setRemainingResearchCost (remainingResearchCost);
		
		final MomPersistentPlayerPrivateKnowledge priv = new MomPersistentPlayerPrivateKnowledge ();
		priv.getSpellResearchStatus ().add (som);
		return priv;
	}
	
	/**
	 * @return Calculations object to test
	 */
	private final ServerSpellCalculationsImpl createCalc ()
	{
		final ServerSpellCalculationsImpl calc = new ServerSpellCalculationsImpl ();
		calc.setSpellUtils (new SpellUtilsImpl ());
		return calc;
	}
	
	/**
	 * Tests the reduceSpellOfMasteryResearchCost method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testReduceSpellOfMasteryResearchCost () throws Exception
	{
		final ServerSpellCalculationsImpl calc = createCalc ();
		
		// Not researched yet, including part way through researching it
		final MomPersistentPlayerPrivateKnowledge priv = createSpellOfMasteryKnowledge (SpellResearchStatusID.RESEARCHABLE_NOW, 50000);
		assertEquals (49500, calc.reduceSpellOfMasteryResearchCost (priv, 500).intValue ());
		assertEquals (49500, priv.getSpellResearchStatus ().get (0).getRemainingResearchCost ());
		assertEquals (500, priv.getSpellOfMasteryResearchCostReduction ().intValue ());
		
		// Further reductions add up, and remaining cost can't go below zero
		assertEquals (0, calc.reduceSpellOfMasteryResearchCost (priv, 60000).intValue ());
		assertEquals (0, priv.getSpellResearchStatus ().get (0).getRemainingResearchCost ());
		assertEquals (60500, priv.getSpellOfMasteryResearchCostReduction ().intValue ());
		
		// Nothing to reduce if already researched
		final MomPersistentPlayerPrivateKnowledge known = createSpellOfMasteryKnowledge (SpellResearchStatusID.AVAILABLE, 0);
		assertNull (calc.reduceSpellOfMasteryResearchCost (known, 500));
	}
	
	/**
	 * Tests the calculateSpellOfMasteryResearchCost method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testCalculateSpellOfMasteryResearchCost () throws Exception
	{
		final Spell somDef = new Spell ();
		somDef.setSpellID (CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY);
		somDef.setResearchCost (60000);
		
		final CommonDatabase db = mock (CommonDatabase.class);
		when (db.findSpell (CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY, "calculateSpellOfMasteryResearchCost")).thenReturn (somDef);
		
		final ServerSpellCalculationsImpl calc = createCalc ();
		
		// No reduction (e.g. older saved game)
		final MomPersistentPlayerPrivateKnowledge priv = new MomPersistentPlayerPrivateKnowledge ();
		assertEquals (60000, calc.calculateSpellOfMasteryResearchCost (priv, db));
		
		// With reduction
		priv.setSpellOfMasteryResearchCostReduction (3500);
		assertEquals (56500, calc.calculateSpellOfMasteryResearchCost (priv, db));
		
		// Can't go below zero
		priv.setSpellOfMasteryResearchCostReduction (70000);
		assertEquals (0, calc.calculateSpellOfMasteryResearchCost (priv, db));
	}
	
	/**
	 * Tests the applyStartingSpellOfMasteryResearchCostReduction method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testApplyStartingSpellOfMasteryResearchCostReduction () throws Exception
	{
		final Pick deathBook = new Pick ();
		deathBook.setPickType (CommonDatabaseConstants.PICK_TYPE_ID_SPELL_BOOK);
		
		final CommonDatabase db = mock (CommonDatabase.class);
		when (db.findPick ("MB02", "applyStartingSpellOfMasteryResearchCostReduction")).thenReturn (deathBook);
		
		final ServerSpellCalculationsImpl calc = createCalc ();
		
		// 10 + 1 books is not 11 in any one realm; picks with fewer than 11 (including retorts) are never even looked up
		final PlayerPick tenLife = new PlayerPick ();
		tenLife.setPickID ("MB01");
		tenLife.setQuantity (10);
		
		final PlayerPick oneDeath = new PlayerPick ();
		oneDeath.setPickID ("MB02");
		oneDeath.setQuantity (1);
		
		final PlayerPick retort = new PlayerPick ();
		retort.setPickID ("RT01");
		retort.setQuantity (1);
		
		final MomPersistentPlayerPrivateKnowledge priv = createSpellOfMasteryKnowledge (SpellResearchStatusID.RESEARCHABLE, 60000);
		calc.applyStartingSpellOfMasteryResearchCostReduction (priv, List.of (tenLife, oneDeath, retort), db);
		assertEquals (60000, priv.getSpellResearchStatus ().get (0).getRemainingResearchCost ());
		assertNull (priv.getSpellOfMasteryResearchCostReduction ());
		
		// 11 in one realm
		final PlayerPick elevenDeath = new PlayerPick ();
		elevenDeath.setPickID ("MB02");
		elevenDeath.setQuantity (11);
		
		calc.applyStartingSpellOfMasteryResearchCostReduction (priv, List.of (elevenDeath, retort), db);
		assertEquals (57000, priv.getSpellResearchStatus ().get (0).getRemainingResearchCost ());
		assertEquals (3000, priv.getSpellOfMasteryResearchCostReduction ().intValue ());
	}
	
	/**
	 * Tests the spellAcquiredDuringPlay method
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSpellAcquiredDuringPlay () throws Exception
	{
		final Spell spellDef = new Spell ();
		spellDef.setResearchCost (1001);
		
		final Spell freeSpellDef = new Spell ();
		
		final CommonDatabase db = mock (CommonDatabase.class);
		when (db.findSpell ("SP001", "spellAcquiredDuringPlay")).thenReturn (spellDef);
		when (db.findSpell ("SP002", "spellAcquiredDuringPlay")).thenReturn (freeSpellDef);
		
		// Human player
		final PlayerDescription pd = new PlayerDescription ();
		pd.setPlayerType (PlayerType.HUMAN);
		
		final MomPersistentPlayerPrivateKnowledge priv = createSpellOfMasteryKnowledge (SpellResearchStatusID.RESEARCHABLE, 60000);
		final PlayerServerDetails player = new PlayerServerDetails (pd, null, priv, null, null);
		
		final DummyServerToClientConnection msgs = new DummyServerToClientConnection ();
		player.setConnection (msgs);
		
		final ServerSpellCalculationsImpl calc = createCalc ();
		
		// Half the research cost, rounded down
		calc.spellAcquiredDuringPlay (player, "SP001", db);
		assertEquals (59500, priv.getSpellResearchStatus ().get (0).getRemainingResearchCost ());
		assertEquals (500, priv.getSpellOfMasteryResearchCostReduction ().intValue ());
		
		assertEquals (1, msgs.getMessages ().size ());
		final UpdateRemainingResearchCostMessage msg = (UpdateRemainingResearchCostMessage) msgs.getMessages ().get (0);
		assertEquals (CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY, msg.getSpellID ());
		assertEquals (59500, msg.getRemainingResearchCost ());
		assertEquals (500, msg.getSpellOfMasteryResearchCostReduction ().intValue ());
		
		// Spells with no research cost, and the Spell of Mastery itself, make no difference
		calc.spellAcquiredDuringPlay (player, "SP002", db);
		calc.spellAcquiredDuringPlay (player, CommonDatabaseConstants.SPELL_ID_SPELL_OF_MASTERY, db);
		assertEquals (59500, priv.getSpellResearchStatus ().get (0).getRemainingResearchCost ());
		assertEquals (1, msgs.getMessages ().size ());
	}
}
