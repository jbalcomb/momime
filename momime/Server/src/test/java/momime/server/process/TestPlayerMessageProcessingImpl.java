package momime.server.process;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.map.coordinates.MapCoordinates3DEx;
import com.ndg.multiplayer.server.session.MultiplayerSessionServerUtils;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.sessionbase.PlayerDescription;
import com.ndg.multiplayer.sessionbase.PlayerType;

import momime.common.database.CommonDatabase;
import momime.common.messages.FogOfWarMemory;
import momime.common.messages.MemoryUnit;
import momime.common.messages.MomGeneralPublicKnowledge;
import momime.common.messages.MomPersistentPlayerPrivateKnowledge;
import momime.common.messages.MomTransientPlayerPrivateKnowledge;
import momime.common.messages.NewTurnMessageData;
import momime.common.messages.PendingMovement;
import momime.common.messages.PendingMovementStep;
import momime.common.messages.TurnSystem;
import momime.common.messages.servertoclient.AddNewTurnMessagesMessage;
import momime.common.messages.servertoclient.SetCurrentPlayerMessage;
import momime.common.messages.servertoclient.StartSimultaneousTurnMessage;
import momime.common.utils.ExpandUnitDetails;
import momime.common.utils.ExpandedUnitDetails;
import momime.common.utils.UnitUtils;
import momime.server.DummyServerToClientConnection;
import momime.server.MomSessionVariables;
import momime.server.fogofwar.FogOfWarMidTurnMultiChanges;
import momime.server.messages.MomGeneralServerKnowledge;

/**
 * Tests the PlayerMessageProcessingImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestPlayerMessageProcessingImpl
{
	/**
	 * Tests the sendNewTurnMessages method, sending messages mid-turn
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSendNewTurnMessages_MidTurn () throws Exception
	{
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		
		// Human player
		final PlayerDescription pd1 = new PlayerDescription ();
		pd1.setPlayerID (2);
		pd1.setPlayerType (PlayerType.HUMAN);
		
		final MomTransientPlayerPrivateKnowledge trans1 = new MomTransientPlayerPrivateKnowledge ();
		final NewTurnMessageData player1message1 = new NewTurnMessageData ();		// These get used as-is, so no need to put real values in here
		final NewTurnMessageData player1message2 = new NewTurnMessageData ();
		trans1.getNewTurnMessage ().add (player1message1);
		trans1.getNewTurnMessage ().add (player1message2);
		
		final PlayerServerDetails player1 = new PlayerServerDetails (pd1, null, null, null, trans1);
		
		final DummyServerToClientConnection msgs1 = new DummyServerToClientConnection ();
		player1.setConnection (msgs1);
		players.add (player1);
		
		// Computer player
		final PlayerDescription pd2 = new PlayerDescription ();
		pd2.setPlayerID (-1);
		pd2.setPlayerType (PlayerType.AI);
		
		final MomTransientPlayerPrivateKnowledge trans2 = new MomTransientPlayerPrivateKnowledge ();
		final NewTurnMessageData player2message1 = new NewTurnMessageData ();
		final NewTurnMessageData player2message2 = new NewTurnMessageData ();
		trans2.getNewTurnMessage ().add (player2message1);
		trans2.getNewTurnMessage ().add (player2message2);
		
		final PlayerServerDetails player2 = new PlayerServerDetails (pd2, null, null, null, trans2);
		players.add (player2);
		
		// Human player
		final PlayerDescription pd3 = new PlayerDescription ();
		pd3.setPlayerID (7);
		pd3.setPlayerType (PlayerType.HUMAN);
		
		final MomTransientPlayerPrivateKnowledge trans3 = new MomTransientPlayerPrivateKnowledge ();
		final NewTurnMessageData player3message1 = new NewTurnMessageData ();
		final NewTurnMessageData player3message2 = new NewTurnMessageData ();
		trans3.getNewTurnMessage ().add (player3message1);
		trans3.getNewTurnMessage ().add (player3message2);
		
		final PlayerServerDetails player3 = new PlayerServerDetails (pd3, null, null, null, trans3);
		
		final DummyServerToClientConnection msgs3 = new DummyServerToClientConnection ();
		player3.setConnection (msgs3);
		players.add (player3);

		// Set up test object
		final PlayerMessageProcessingImpl proc = new PlayerMessageProcessingImpl ();
		
		// Run test
		proc.sendNewTurnMessages (null, players, null);
		
		// Check player1's messages
		assertEquals (1, msgs1.getMessages ().size ());
		
		final AddNewTurnMessagesMessage msg1 = (AddNewTurnMessagesMessage) msgs1.getMessages ().get (0);
		assertFalse (msg1.isExpireMessages ());
		assertEquals (2, msg1.getMessage ().size ());
		assertSame (player1message1, msg1.getMessage ().get (0));
		assertSame (player1message2, msg1.getMessage ().get (1));

		// Check player3's messages
		assertEquals (1, msgs3.getMessages ().size ());
		
		final AddNewTurnMessagesMessage msg3 = (AddNewTurnMessagesMessage) msgs3.getMessages ().get (0);
		assertFalse (msg3.isExpireMessages ());
		assertEquals (2, msg3.getMessage ().size ());
		assertSame (player3message1, msg3.getMessage ().get (0));
		assertSame (player3message2, msg3.getMessage ().get (1));
	}

	/**
	 * Tests the sendNewTurnMessages method, at the start of a simultaneous turn
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSendNewTurnMessages_SimultaneousTurns () throws Exception
	{
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		
		// Human player
		final PlayerDescription pd1 = new PlayerDescription ();
		pd1.setPlayerID (2);
		pd1.setPlayerType (PlayerType.HUMAN);
		
		final MomTransientPlayerPrivateKnowledge trans1 = new MomTransientPlayerPrivateKnowledge ();
		final NewTurnMessageData player1message1 = new NewTurnMessageData ();		// These get used as-is, so no need to put real values in here
		final NewTurnMessageData player1message2 = new NewTurnMessageData ();
		trans1.getNewTurnMessage ().add (player1message1);
		trans1.getNewTurnMessage ().add (player1message2);
		
		final PlayerServerDetails player1 = new PlayerServerDetails (pd1, null, null, null, trans1);
		
		final DummyServerToClientConnection msgs1 = new DummyServerToClientConnection ();
		player1.setConnection (msgs1);
		players.add (player1);
		
		// Computer player
		final PlayerDescription pd2 = new PlayerDescription ();
		pd2.setPlayerID (-1);
		pd2.setPlayerType (PlayerType.AI);
		
		final MomTransientPlayerPrivateKnowledge trans2 = new MomTransientPlayerPrivateKnowledge ();
		final NewTurnMessageData player2message1 = new NewTurnMessageData ();
		final NewTurnMessageData player2message2 = new NewTurnMessageData ();
		trans2.getNewTurnMessage ().add (player2message1);
		trans2.getNewTurnMessage ().add (player2message2);
		
		final PlayerServerDetails player2 = new PlayerServerDetails (pd2, null, null, null, trans2);
		players.add (player2);
		
		// Human player
		final PlayerDescription pd3 = new PlayerDescription ();
		pd3.setPlayerID (7);
		pd3.setPlayerType (PlayerType.HUMAN);
		
		final MomTransientPlayerPrivateKnowledge trans3 = new MomTransientPlayerPrivateKnowledge ();
		final NewTurnMessageData player3message1 = new NewTurnMessageData ();
		final NewTurnMessageData player3message2 = new NewTurnMessageData ();
		trans3.getNewTurnMessage ().add (player3message1);
		trans3.getNewTurnMessage ().add (player3message2);
		
		final PlayerServerDetails player3 = new PlayerServerDetails (pd3, null, null, null, trans3);
		
		final DummyServerToClientConnection msgs3 = new DummyServerToClientConnection ();
		player3.setConnection (msgs3);
		players.add (player3);
		
		// Turn/Current player
		final MomGeneralPublicKnowledge gpk = new MomGeneralPublicKnowledge ();
		gpk.setTurnNumber (5);

		// Set up test object
		final PlayerMessageProcessingImpl proc = new PlayerMessageProcessingImpl ();
		
		// Run test
		proc.sendNewTurnMessages (gpk, players, TurnSystem.SIMULTANEOUS);
		
		// Check player1's messages
		assertEquals (1, msgs1.getMessages ().size ());
		
		final StartSimultaneousTurnMessage msg1 = (StartSimultaneousTurnMessage) msgs1.getMessages ().get (0);
		assertTrue (msg1.isExpireMessages ());
		assertEquals (5, msg1.getTurnNumber ());
		assertEquals (2, msg1.getMessage ().size ());
		assertSame (player1message1, msg1.getMessage ().get (0));
		assertSame (player1message2, msg1.getMessage ().get (1));

		// Check player3's messages
		assertEquals (1, msgs3.getMessages ().size ());
		
		final StartSimultaneousTurnMessage msg3 = (StartSimultaneousTurnMessage) msgs3.getMessages ().get (0);
		assertTrue (msg3.isExpireMessages ());
		assertEquals (5, msg3.getTurnNumber ());
		assertEquals (2, msg3.getMessage ().size ());
		assertSame (player3message1, msg3.getMessage ().get (0));
		assertSame (player3message2, msg3.getMessage ().get (1));
	}

	/**
	 * Tests the sendNewTurnMessages method, at the start of a simultaneous turn
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testSendNewTurnMessages_SingleTurns () throws Exception
	{
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		
		// Human player
		final PlayerDescription pd1 = new PlayerDescription ();
		pd1.setPlayerID (2);
		pd1.setPlayerType (PlayerType.HUMAN);
		
		final MomTransientPlayerPrivateKnowledge trans1 = new MomTransientPlayerPrivateKnowledge ();
		final NewTurnMessageData player1message1 = new NewTurnMessageData ();		// These get used as-is, so no need to put real values in here
		final NewTurnMessageData player1message2 = new NewTurnMessageData ();
		trans1.getNewTurnMessage ().add (player1message1);
		trans1.getNewTurnMessage ().add (player1message2);
		
		final PlayerServerDetails player1 = new PlayerServerDetails (pd1, null, null, null, trans1);
		
		final DummyServerToClientConnection msgs1 = new DummyServerToClientConnection ();
		player1.setConnection (msgs1);
		players.add (player1);
		
		// Computer player
		final PlayerDescription pd2 = new PlayerDescription ();
		pd2.setPlayerID (-1);
		pd2.setPlayerType (PlayerType.AI);
		
		final MomTransientPlayerPrivateKnowledge trans2 = new MomTransientPlayerPrivateKnowledge ();
		final NewTurnMessageData player2message1 = new NewTurnMessageData ();
		final NewTurnMessageData player2message2 = new NewTurnMessageData ();
		trans2.getNewTurnMessage ().add (player2message1);
		trans2.getNewTurnMessage ().add (player2message2);
		
		final PlayerServerDetails player2 = new PlayerServerDetails (pd2, null, null, null, trans2);
		players.add (player2);
		
		// Human player
		final PlayerDescription pd3 = new PlayerDescription ();
		pd3.setPlayerID (7);
		pd3.setPlayerType (PlayerType.HUMAN);
		
		final MomTransientPlayerPrivateKnowledge trans3 = new MomTransientPlayerPrivateKnowledge ();
		final NewTurnMessageData player3message1 = new NewTurnMessageData ();
		final NewTurnMessageData player3message2 = new NewTurnMessageData ();
		trans3.getNewTurnMessage ().add (player3message1);
		trans3.getNewTurnMessage ().add (player3message2);
		
		final PlayerServerDetails player3 = new PlayerServerDetails (pd3, null, null, null, trans3);
		
		final DummyServerToClientConnection msgs3 = new DummyServerToClientConnection ();
		player3.setConnection (msgs3);
		players.add (player3);
		
		// Turn/Current player
		final MomGeneralPublicKnowledge gpk = new MomGeneralPublicKnowledge ();
		gpk.setTurnNumber (5);
		gpk.setCurrentPlayerID (7);
		
		// Set up test object
		final PlayerMessageProcessingImpl proc = new PlayerMessageProcessingImpl ();
		
		// Run test
		proc.sendNewTurnMessages (gpk, players, TurnSystem.ONE_PLAYER_AT_A_TIME);
		
		// Check player1's messages
		assertEquals (1, msgs1.getMessages ().size ());
		
		final SetCurrentPlayerMessage msg1 = (SetCurrentPlayerMessage) msgs1.getMessages ().get (0);
		assertFalse (msg1.isExpireMessages ());
		assertEquals (5, msg1.getTurnNumber ());
		assertEquals (7, msg1.getCurrentPlayerID ());
		assertEquals (0, msg1.getMessage ().size ());

		// Check player3's messages
		assertEquals (1, msgs3.getMessages ().size ());
		
		final SetCurrentPlayerMessage msg3 = (SetCurrentPlayerMessage) msgs3.getMessages ().get (0);
		assertTrue (msg3.isExpireMessages ());
		assertEquals (5, msg3.getTurnNumber ());
		assertEquals (7, msg3.getCurrentPlayerID ());
		assertEquals (2, msg3.getMessage ().size ());
		assertSame (player3message1, msg3.getMessage ().get (0));
		assertSame (player3message2, msg3.getMessage ().get (1));
	}
	
	/**
	 * Tests the continueMovement method for all players (i.e. as in a simultaneous turns game)
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testContinueMovement_AllPlayers () throws Exception
	{
		// Mock database
		final CommonDatabase db = mock (CommonDatabase.class);
		
		// General server knowledge
		final FogOfWarMemory trueMap = new FogOfWarMemory ();
		
		final MomGeneralServerKnowledge gsk = new MomGeneralServerKnowledge ();
		gsk.setTrueMap (trueMap);
		
		// Players
		final MomPersistentPlayerPrivateKnowledge priv1 = new MomPersistentPlayerPrivateKnowledge ();
		final PlayerServerDetails player1 = new PlayerServerDetails (null, null, priv1, null, null);

		final MomPersistentPlayerPrivateKnowledge priv2 = new MomPersistentPlayerPrivateKnowledge ();
		final PlayerServerDetails player2 = new PlayerServerDetails (null, null, priv2, null, null);
		
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		players.add (player1);
		players.add (player2);	
		
		// Units
		final UnitUtils unitUtils = mock (UnitUtils.class);
		
		final MemoryUnit unit1 = new MemoryUnit ();
		final MemoryUnit unit2 = new MemoryUnit ();
		when (unitUtils.findUnitURN (1, trueMap.getUnit (), "continueMovement")).thenReturn (unit1);
		when (unitUtils.findUnitURN (2, trueMap.getUnit (), "continueMovement")).thenReturn (unit2);
		
		final ExpandUnitDetails expand = mock (ExpandUnitDetails.class);
		final ExpandedUnitDetails xu1 = mock (ExpandedUnitDetails.class);
		final ExpandedUnitDetails xu2 = mock (ExpandedUnitDetails.class);
		when (expand.expandUnitDetails (unit1, null, null, null, players, trueMap, db)).thenReturn (xu1);
		when (expand.expandUnitDetails (unit2, null, null, null, players, trueMap, db)).thenReturn (xu2);
		
		final List<ExpandedUnitDetails> unitStack1 = new ArrayList<ExpandedUnitDetails> ();
		unitStack1.add (xu1);
		
		final List<ExpandedUnitDetails> unitStack2 = new ArrayList<ExpandedUnitDetails> ();
		unitStack2.add (xu2);
		
		// Pending moves
		final PendingMovementStep move1step1 = new PendingMovementStep ();
		move1step1.setMoveFrom (new MapCoordinates3DEx (1, 10, 1));
		move1step1.setMoveTo (new MapCoordinates3DEx (2, 10, 1));
		move1step1.setDirection (3);
		
		final PendingMovement move1 = new PendingMovement ();
		move1.setMoveFrom (new MapCoordinates3DEx (1, 10, 1));
		move1.setMoveTo (new MapCoordinates3DEx (2, 10, 1));
		move1.getPath ().add (move1step1);
		move1.getUnitURN ().add (1);

		final PendingMovementStep move2step1 = new PendingMovementStep ();
		move2step1.setMoveFrom (new MapCoordinates3DEx (4, 10, 1));
		move2step1.setMoveTo (new MapCoordinates3DEx (3, 10, 1));
		move2step1.setDirection (7);
		
		final PendingMovement move2 = new PendingMovement ();
		move2.setMoveFrom (new MapCoordinates3DEx (4, 10, 1));
		move2.setMoveTo (new MapCoordinates3DEx (3, 10, 1));
		move2.getPath ().add (move2step1);
		move2.getUnitURN ().add (2);
		
		priv1.getPendingMovement ().add (move1);
		priv2.getPendingMovement ().add (move2);
		
		// Session variables
		final MomSessionVariables mom = mock (MomSessionVariables.class);
		when (mom.getPlayers ()).thenReturn (players);
		when (mom.getGeneralServerKnowledge ()).thenReturn (gsk);
		when (mom.getServerDB ()).thenReturn (db);
		
		// Set up test object
		final FogOfWarMidTurnMultiChanges midTurn = mock (FogOfWarMidTurnMultiChanges.class);
		
		final PlayerMessageProcessingImpl proc = new PlayerMessageProcessingImpl ();
		proc.setUnitUtils (unitUtils);
		proc.setFogOfWarMidTurnMultiChanges (midTurn);
		proc.setExpandUnitDetails (expand);
		
		// Run method
		proc.continueMovement (0, mom);
		
		// Check results
		verify (midTurn).moveUnitStack (unitStack1, player1, false, (MapCoordinates3DEx) move1.getMoveFrom (), (MapCoordinates3DEx) move1.getMoveTo (), false, mom);
		verify (midTurn).moveUnitStack (unitStack2, player2, false, (MapCoordinates3DEx) move2.getMoveFrom (), (MapCoordinates3DEx) move2.getMoveTo (), false, mom);
		
		verifyNoMoreInteractions (midTurn);
	}

	/**
	 * Tests the continueMovement method for a single players (i.e. as in a one-at-a-time turns game)
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testContinueMovement_OnePlayers () throws Exception
	{
		// Mock database
		final CommonDatabase db = mock (CommonDatabase.class);
		
		// General server knowledge
		final FogOfWarMemory trueMap = new FogOfWarMemory ();
		
		final MomGeneralServerKnowledge gsk = new MomGeneralServerKnowledge ();
		gsk.setTrueMap (trueMap);
		
		// Players
		final MomPersistentPlayerPrivateKnowledge priv1 = new MomPersistentPlayerPrivateKnowledge ();
		final PlayerDescription pd1 = new PlayerDescription ();
		pd1.setPlayerID (1);
		
		final PlayerServerDetails player1 = new PlayerServerDetails (pd1, null, priv1, null, null);

		final MomPersistentPlayerPrivateKnowledge priv2 = new MomPersistentPlayerPrivateKnowledge ();
		final PlayerDescription pd2 = new PlayerDescription ();
		pd2.setPlayerID (2);
		
		final PlayerServerDetails player2 = new PlayerServerDetails (pd2, null, priv2, null, null);
		
		final List<PlayerServerDetails> players = new ArrayList<PlayerServerDetails> ();
		players.add (player1);
		players.add (player2);	
		
		// Session utils
		final MultiplayerSessionServerUtils multiplayerSessionServerUtils = mock (MultiplayerSessionServerUtils.class);
		when (multiplayerSessionServerUtils.findPlayerWithID (players, pd2.getPlayerID (), "continueMovement")).thenReturn (player2);
		
		// Units
		final UnitUtils unitUtils = mock (UnitUtils.class);
		
		final MemoryUnit unit2 = new MemoryUnit ();
		when (unitUtils.findUnitURN (2, trueMap.getUnit (), "continueMovement")).thenReturn (unit2);
		
		final ExpandUnitDetails expand = mock (ExpandUnitDetails.class);
		final ExpandedUnitDetails xu1 = mock (ExpandedUnitDetails.class);
		final ExpandedUnitDetails xu2 = mock (ExpandedUnitDetails.class);
		when (expand.expandUnitDetails (unit2, null, null, null, players, trueMap, db)).thenReturn (xu2);
		
		final List<ExpandedUnitDetails> unitStack1 = new ArrayList<ExpandedUnitDetails> ();
		unitStack1.add (xu1);
		
		final List<ExpandedUnitDetails> unitStack2 = new ArrayList<ExpandedUnitDetails> ();
		unitStack2.add (xu2);
		
		// Pending moves
		final PendingMovementStep move1step1 = new PendingMovementStep ();
		move1step1.setMoveFrom (new MapCoordinates3DEx (1, 10, 1));
		move1step1.setMoveTo (new MapCoordinates3DEx (2, 10, 1));
		move1step1.setDirection (3);
		
		final PendingMovement move1 = new PendingMovement ();
		move1.setMoveFrom (new MapCoordinates3DEx (1, 10, 1));
		move1.setMoveTo (new MapCoordinates3DEx (2, 10, 1));
		move1.getPath ().add (move1step1);
		move1.getUnitURN ().add (1);

		final PendingMovementStep move2step1 = new PendingMovementStep ();
		move2step1.setMoveFrom (new MapCoordinates3DEx (4, 10, 1));
		move2step1.setMoveTo (new MapCoordinates3DEx (3, 10, 1));
		move2step1.setDirection (7);
		
		final PendingMovement move2 = new PendingMovement ();
		move2.setMoveFrom (new MapCoordinates3DEx (4, 10, 1));
		move2.setMoveTo (new MapCoordinates3DEx (3, 10, 1));
		move2.getPath ().add (move2step1);
		move2.getUnitURN ().add (2);
		
		priv1.getPendingMovement ().add (move1);
		priv2.getPendingMovement ().add (move2);
		
		// Session variables
		final MomSessionVariables mom = mock (MomSessionVariables.class);
		when (mom.getPlayers ()).thenReturn (players);
		when (mom.getGeneralServerKnowledge ()).thenReturn (gsk);
		when (mom.getServerDB ()).thenReturn (db);
		
		// Set up test object
		final FogOfWarMidTurnMultiChanges midTurn = mock (FogOfWarMidTurnMultiChanges.class);
		
		final PlayerMessageProcessingImpl proc = new PlayerMessageProcessingImpl ();
		proc.setUnitUtils (unitUtils);
		proc.setFogOfWarMidTurnMultiChanges (midTurn);
		proc.setMultiplayerSessionServerUtils (multiplayerSessionServerUtils);
		proc.setExpandUnitDetails (expand);
		
		// Run method
		proc.continueMovement (2, mom);
		
		// Check results
		verify (midTurn).moveUnitStack (unitStack2, player2, false, (MapCoordinates3DEx) move2.getMoveFrom (), (MapCoordinates3DEx) move2.getMoveTo (), false, mom);
		
		verifyNoMoreInteractions (midTurn);
	}

	/**
	 * Works out how many turns it takes to build a road, by repeatedly adding on the build rate until isRoadConstructionFinished says we're done
	 * @param productionToBuildRoad Road building points needed for this tile type
	 * @param totalRoadBuildingRate Total road building points per turn of all engineers working on this tile
	 * @return Number of turns taken
	 */
	private final int turnsToBuildRoad (final int productionToBuildRoad, final int totalRoadBuildingRate)
	{
		int turns = 0;
		int progress = 0;
		do
		{
			turns++;
			progress = progress + totalRoadBuildingRate;
		} while (!PlayerMessageProcessingImpl.isRoadConstructionFinished (progress, productionToBuildRoad, totalRoadBuildingRate));
		
		return turns;
	}
	
	/**
	 * Tests the isRoadConstructionFinished method matches the table of turns to build roads on the wiki ("Roads - Construction Times"),
	 * which is the time for 1 engineer divided by the number of engineers, rounded down, minimum 1
	 */
	@Test
	public final void testIsRoadConstructionFinished ()
	{
		// Each row is the points needed (= turns for 1 engineer), then the expected number of turns for 1, 2, 3, 4 and 5 engineers
		final int [] [] wikiTable =
		{
			{3, 3, 1, 1, 1, 1},		// Grassland
			{4, 4, 2, 1, 1, 1},		// Desert, Sorcery node
			{5, 5, 2, 1, 1, 1},		// River, River mouth, Nature node
			{6, 6, 3, 2, 1, 1},		// Forest, Tundra, Hills
			{8, 8, 4, 2, 2, 1}		// Swamp, Mountain, Volcano, Chaos node
		};
		
		for (final int [] row : wikiTable)
			for (int engineers = 1; engineers <= 5; engineers++)
				assertEquals (row [engineers], turnsToBuildRoad (row [0], engineers), "Points needed " + row [0] + " with " + engineers + " engineers");
		
		// Dwarf engineers have rate 2, so 1 Dwarf Engineer over Mountains = 8 / 2 = 4 turns, and 3 Dwarf Engineers = 8 / 6 = 1 turn
		assertEquals (4, turnsToBuildRoad (8, 2));
		assertEquals (1, turnsToBuildRoad (8, 6));
		
		// Already have enough points (e.g. engineers moved away and came back) is always finished
		assertTrue (PlayerMessageProcessingImpl.isRoadConstructionFinished (6, 6, 0));
		assertFalse (PlayerMessageProcessingImpl.isRoadConstructionFinished (2, 6, 1));
	}
}
