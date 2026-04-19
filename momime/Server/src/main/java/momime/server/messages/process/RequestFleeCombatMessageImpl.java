package momime.server.messages.process;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ndg.multiplayer.server.session.MultiplayerSessionThread;
import com.ndg.multiplayer.server.session.PlayerServerDetails;
import com.ndg.multiplayer.server.session.PostSessionClientToServerMessage;

import jakarta.xml.bind.JAXBException;
import momime.common.messages.clienttoserver.RequestFleeCombatMessage;
import momime.server.MomSessionVariables;
import momime.server.knowledge.CombatDetails;
import momime.server.process.CombatHandling;
import momime.server.utils.CombatMapServerUtils;

/**
 * Message client sends to server when they want to flee from a combat
 */
public final class RequestFleeCombatMessageImpl extends RequestFleeCombatMessage implements PostSessionClientToServerMessage
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (RequestFleeCombatMessageImpl.class);
	
	/** Methods dealing with combat maps that are only needed on the server */
	private CombatMapServerUtils combatMapServerUtils;	
	
	/** More methods dealing with executing combats */
	private CombatHandling combatHandling;
	
	/**
	 * @param thread Thread for the session this message is for; from the thread, the processor can obtain the list of players, sd, gsk, gpl, etc
	 * @param sender Player who sent the message
	 * @throws JAXBException If there is a problem converting the object into XML
	 * @throws XMLStreamException If there is a problem writing to the XML stream
	 * @throws IOException If there is another kind of problem
	 */
	@Override
	public final void process (final MultiplayerSessionThread thread, final PlayerServerDetails sender)
		throws JAXBException, XMLStreamException, IOException
	{
		final MomSessionVariables mom = (MomSessionVariables) thread;
		
		final CombatDetails combatDetails = getCombatMapServerUtils ().findCombatURN (mom.getCombatDetails (), getCombatURN ());
		
		if (combatDetails == null)
			log.warn (sender.getPlayerDescription ().getPlayerName () + " sent RequestFleeCombatMessage for combat URN " + getCombatURN () + " which does not exist");
		
		else if (!sender.getPlayerDescription ().getPlayerID ().equals (combatDetails.getCombatCurrentPlayerID ()))
			log.warn (sender.getPlayerDescription ().getPlayerName () + " sent RequestFleeCombatMessage when it isn't their turn - ignored");
		
		else
			getCombatHandling ().fleeFromCombat (combatDetails, sender.getPlayerDescription ().getPlayerID (), mom);
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
	 * @return More methods dealing with executing combats
	 */
	public final CombatHandling getCombatHandling ()
	{
		return combatHandling;
	}

	/**
	 * @param h More methods dealing with executing combats
	 */
	public final void setCombatHandling (final CombatHandling h)
	{
		combatHandling = h;
	}
}