package com.ndg.xmleditor.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.ndg.utils.swing.actions.MessageDialogAction;
import com.ndg.utils.swing.mdi.MDIDesktopPane;
import com.ndg.utils.swing.mdi.WindowMenu;
import com.ndg.xmleditor.doc.ComplexTypeReference;
import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.grid.XmlGridFrame;
import com.ndg.xmleditor.schema.ComplexTypeEx;
import com.ndg.xmleditor.schema.LocalElementEx;
import com.ndg.xmleditor.schema.XmlDatabaseInfo;

/**
 * The main MDI window for the XML editor - controls setting up the menu items and their associated actions
 */
public class XmlEditorMain
{
	/** The title/caption to appear on this form */
	protected final static String FORM_TITLE = "XML Editor";

	/** A list of the main XML document being edited, plus any referenced documents */
	private ListOfXmlDocuments xmlDocuments;

	/** Dispatcher to use to open grid and single record displays with */
	private XmlEditorDispatcher dispatcher;

	/** The frame/window itself */
	private JFrame frame;

	/** The MDI desktop */
	private MDIDesktopPane desktop;

	/** The File menu */
	private JMenu fileMenu;

	/**
	 * Creates the main MDI window for the XML editor
	 * @throws XmlEditorException If there are syntax problems with the XSD
	 */
	public void init ()
		throws XmlEditorException
	{
		// Work out the title
		final XmlDatabaseInfo dbInfo = xmlDocuments.get (0).getXsd ().getDatabaseInfo (xmlDocuments.get (0).getXsd ().getTargetNamespace ());
		final String databaseTitle = (dbInfo != null) ? dbInfo.getTitle () : null;
		final String databaseName = (databaseTitle != null) ? databaseTitle : xmlDocuments.get (0).getXsd ().getTargetNamespace ();
		
		// Initialize the frame
		frame = new JFrame ();
		frame.setTitle (databaseName);
		frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);

		// Save the XML document when the MDI form closes
		frame.addWindowListener (new WindowAdapter ()
		{
			@Override
			public void windowClosed (@SuppressWarnings ("unused") final WindowEvent event)
			{
				// Strip out all referenced XML documents from the special elements at the end of the main XML document, where we put them so we can use the XSD to enforce referential integrity across multiple XML files
				// Write this later once we want to handle multiple XML files

				// Save it
				final Format format = Format.getPrettyFormat ();
				format.setIndent ("\t");

				final XMLOutputter outputter = new XMLOutputter ();
				outputter.setFormat (format);
				try (final FileOutputStream out = new FileOutputStream (xmlDocuments.get (0).getFileNameTextField ().getText ().trim ()))
				{
					outputter.output (xmlDocuments.get (0).getXmlDocument (), out);
				}
				catch (final IOException e)
				{
					e.printStackTrace ();
					JOptionPane.showMessageDialog (null, e.toString (), FORM_TITLE, JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// Initialize the MDI interface
		desktop = new MDIDesktopPane ();

		// Create top level menus
		final JMenuBar menuBar = new JMenuBar ();

		fileMenu = new JMenu ("File");
		menuBar.add (fileMenu);

		final JMenu editMenu = new JMenu ("Edit");
		menuBar.add (editMenu);

		menuBar.add (new WindowMenu (desktop));

		// Exit menu option
		final Action exitAction = new MessageDialogAction ("Exit", (ev) -> frame.dispose ());

		final JMenuItem exitOption = new JMenuItem ();
		exitOption.setAction (exitAction);
		fileMenu.add (exitOption);

		// Add menu options for all the entities defined in the XSD
		for (final LocalElementEx thisEntityElement : xmlDocuments.get (0).getXsd ().getTopLevelTypeDefinition ().getChildEntities (xmlDocuments))
		{
			final ComplexTypeReference thisTypeDefinition = xmlDocuments.findDefinitionOfComplexType (thisEntityElement.getType ());

			// Which menu should we add this new entity on to?
			JMenu addToMenu = editMenu;
			final String subMenu = thisTypeDefinition.getComplexTypeDefinition ().getSubMenu ();
			if (subMenu != null)
			{
				// Does this sub menu already exist?
				addToMenu = null;
				for (final Component thisComponent : editMenu.getPopupMenu ().getComponents ())
				{
					if (thisComponent instanceof JMenu)
					{
						final JMenu thisMenu = (JMenu) thisComponent;
						if (thisMenu.getText ().equals (subMenu))
							addToMenu = thisMenu;
					}
				}

				// If not then add it
				if (addToMenu == null)
				{
					addToMenu = new JMenu (subMenu);
					editMenu.add (addToMenu);
				}
			}

			// Add the menu item
			final EditEntityAction editEntityAction = new EditEntityAction (thisEntityElement, thisTypeDefinition, this);
			final JMenuItem editEntityMenuItem = new JMenuItem ();
			editEntityMenuItem.setAction (editEntityAction);
			addToMenu.add (editEntityMenuItem);

			// Any long description for this entity?
			editEntityMenuItem.setToolTipText (thisTypeDefinition.getComplexTypeDefinition ().getLongDescription ());
		}

		// Final steps before showing frame
		final JScrollPane scrollPane = new JScrollPane ();
		scrollPane.getViewport ().add (desktop);
		frame.getContentPane ().setLayout (new BorderLayout ());
		frame.getContentPane ().add (scrollPane, BorderLayout.CENTER);

		frame.setJMenuBar (menuBar);
		
		// Show the frame
		frame.pack ();
		frame.setVisible (true);
		frame.setExtendedState (Frame.MAXIMIZED_BOTH);
	}

	/**
	 * Exposes functionality to add new windows to the MDI
	 * @param newFrame The new window to add to the MDI
	 */
	public void add (final JInternalFrame newFrame)
	{
		desktop.add (newFrame);
	}

	/**
	 * Finds all grid windows displaying records from the specified type, and forces the grids to reload themselves
	 * @param typeDefinition The top level xsd:complexType definition from the XSD of the type we need to search for
	 */
	public void fireTableDataChangedOnAllGridsShowingEntity (final ComplexTypeEx typeDefinition)
	{
		final JInternalFrame frames [] = desktop.getAllFrames ();
		for (final JInternalFrame thisFrame : frames)
			if (thisFrame instanceof XmlGridFrame)
			{
				final XmlGridFrame thisGridFrame = (XmlGridFrame) thisFrame;
				if (thisGridFrame.getTypeDefinition () == typeDefinition)
					thisGridFrame.fireTableDataChanged ();
			}
	}

	/**
	 * Allows external access to the MDI frame itself (Dialogs need this to know which frame to make themselves modal with respect to)
	 * @return The main MDI frame
	 */
	public final JFrame getFrame ()
	{
		return frame;
	}

	/**
	 * @return A list of the main XML document being edited, plus any referenced documents
	 */
	public final ListOfXmlDocuments getXmlDocuments ()
	{
		return xmlDocuments;
	}

	/**
	 * @param docs A list of the main XML document being edited, plus any referenced documents
	 */
	public final void setXmlDocuments (final ListOfXmlDocuments docs)
	{
		xmlDocuments = docs;
	}
	
	/**
	 * @return Dispatcher to use to open grid and single record displays with
	 */
	public final XmlEditorDispatcher getDispatcher ()
	{
		return dispatcher;
	}

	/**
	 * @param dis Dispatcher to use to open grid and single record displays with
	 */
	public final void setDispatcher (final XmlEditorDispatcher dis)
	{
		dispatcher = dis;
	}
	
	/**
	 * @return The File menu
	 */
	public final JMenu getFileMenu ()
	{
		return fileMenu;
	}
}