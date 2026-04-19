package com.ndg.xmleditor.grid;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableRowSorter;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Element;
import org.w3._2001.xmlschema.Field;
import org.w3._2001.xmlschema.Selector;

import com.ndg.utils.swing.GridBagConstraintsHorizontalFill;
import com.ndg.utils.swing.GridBagConstraintsNoFill;
import com.ndg.utils.swing.NdgUIUtils;
import com.ndg.utils.swing.actions.MessageDialogAction;
import com.ndg.xmleditor.doc.ComplexTypeReference;
import com.ndg.xmleditor.doc.XmlForeignKeyReference;
import com.ndg.xmleditor.editor.XmlEditorException;
import com.ndg.xmleditor.editor.XmlEditorMain;
import com.ndg.xmleditor.grid.column.AttributeEnumGridColumn;
import com.ndg.xmleditor.grid.column.AttributeForeignKeyGridColumn;
import com.ndg.xmleditor.grid.column.AttributeGridColumn;
import com.ndg.xmleditor.grid.column.DummyGridColumn;
import com.ndg.xmleditor.grid.column.ElementEnumGridColumn;
import com.ndg.xmleditor.grid.column.ElementForeignKeyGridColumn;
import com.ndg.xmleditor.grid.column.ElementGridColumn;
import com.ndg.xmleditor.grid.column.RepeatingElementEnumGridColumn;
import com.ndg.xmleditor.grid.column.RepeatingElementForeignKeyGridColumn;
import com.ndg.xmleditor.grid.column.RepeatingElementGridColumn;
import com.ndg.xmleditor.grid.column.XmlGridColumn;
import com.ndg.xmleditor.schema.AttributeEx;
import com.ndg.xmleditor.schema.FieldEx;
import com.ndg.xmleditor.schema.LocalElementEx;

/**
 * A form with a grid showing one type of record from the XML file
 */
public class XmlEditorGrid
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (XmlEditorGrid.class);

	/** Special zero inset for layout components */
	protected final static int NO_INSET = 0;
	
	/** Typical inset used on this screen layout */
	protected final static int INSET = 2;
	
	/** The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type */
	private LocalElementEx entityElement;

	/** The xsd:complexType node of the entity being edited from the XSD */
	private ComplexTypeReference typeDefinition;

	/** If this is a child entity, this value holds the parent record; if this is a top level entity, this value will be null */
	private Element parentRecord;

	/** The container where we should look for records */
	private Element container;

	/** Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity */
	private LocalElementEx [] parentEntityElements;

	/** Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity */
	private ComplexTypeReference [] parentTypeDefinitions;

	/** The main MDI window for the editor */
	private XmlEditorMain mdiEditor;

	/** The XML tag/name of this entity */
	private String entityTag;

	/** The frame/window itself */
	private XmlGridFrame frame;

	/** The left hand pane containing the parent regions and the grid */
	private JPanel parentsAndGridPanel;
	
	/** Y value of the next component added to the parentsAndGridPanel GridBagLayout */
	private int parentsAndGridPanelY;

	/** The right hand pane containing all the buttons */
	private JPanel buttonPanel;
	
	/** Y value of the next component added to the buttonPanel GridBagLayout */
	private int buttonPanelY;

	/** The table containing all the data */
	private JTable table;

	/** The table model which maps the XML data into grid cells */
	private XmlTableModel tableModel;

	/** Sort and filter interface for the table */
	private TableRowSorter<XmlTableModel> tableSorter;

	/** Action for bringing up the detail display to edit the currently selected record */
	private Action editAction;

	/** Action for bringing up the detail display to copy the currently selected record */
	private Action copyAction;

	/** Action for deleting the currently selected record */
	private Action deleteAction;

	/** Action for moving the currently selected record with respect to the other records in the XML file */
	private Action moveUpAction;

	/** Action for moving the currently selected record with respect to the other records in the XML file */
	private Action moveDownAction;

	/** List of all the drill down actions, so we can enable/disable them as rows are selected/deselected */
	private List<DrillDownAction> drillDownActions;
	
	/** UI utils */
	private NdgUIUtils utils;
	
	/**
	 * Creates a new form with a grid showing one type of record from the XML file
	 * @throws XmlEditorException If there a syntax problem parsing the XSD
	 * @throws IOException If there is a problem loading the button images
	 */
	public void init ()
		throws XmlEditorException, IOException
	{
		// The entity tag is needed over and over, so look it up once only
		entityTag = entityElement.getName ();

		// Find where the records will be held
		if (parentRecord == null)
			container = mdiEditor.getXmlDocuments ().get (0).getXml ();
		else
			container = parentRecord;

		// Create the table model
		final List<XmlGridColumn> columns = buildColumnsList ();
		tableModel = new XmlTableModel (entityElement, container, columns, mdiEditor);

		// Create the sorter/filterer
		tableSorter = new TableRowSorter<XmlTableModel> (tableModel);
		tableModel.setTableSorter (tableSorter);

		// Set up any specialised comparators
		for (int columnNo = 0; columnNo < columns.size (); columnNo++)
		{
			final Comparator<String> thisComparator = columns.get (columnNo).checkForSpecialisedComparator ();
			if (thisComparator != null)
				tableSorter.setComparator (columnNo, thisComparator);
		}

		// Initialize the frame
		// If we're drilling down into a complex type, use its defined plural name (e.g. if a bank account has an entity named transaction as a child, then put "transactions")
		// If we're drilling down into a simple type, get the plural name from the name of the element (e.g. an animation has a field named "frame", so put "frames")
		frame = new XmlGridFrame ((typeDefinition != null) ? typeDefinition.getComplexTypeDefinition ().getPluralName () : getEntityElement ().getName () + "s",
			(typeDefinition == null) ? null : typeDefinition.getComplexTypeDefinition (), tableModel);
		frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);

		// Initialize the content pane - the contentPane is just a 2x1 gridBagLayout, with the left cell containing all the parents and grids, and the right cell containing the buttons.
		// The two cells then each contain their own GridBayLayout.  We have to do it like this because we can't predict how many buttons high each parent panel should
		// be to set their spany correctly, if we did everything in one GridBagLayout.
		final JPanel contentPane = new JPanel ();
		contentPane.setLayout (new GridBagLayout ());

		// Create a panel containing the parent regions and the grid
		parentsAndGridPanel = new JPanel ();
		parentsAndGridPanel.setLayout (new GridBagLayout ());

		final GridBagConstraints parentsAndGridConstraints = getUtils ().createConstraintsBothFill (0, 0, 1, 1, NO_INSET);
		parentsAndGridConstraints.weightx = 1;
		parentsAndGridConstraints.weighty = 1;
		contentPane.add (parentsAndGridPanel, parentsAndGridConstraints);

		// Create areas on the form showing the values of the whole chain of parent records that led us here
		parentsAndGridPanelY = 0;
		createParentRegions ();

		// Create the grid
		table = new JTable (tableModel, new XmlColumnModel (columns));
		table.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);		// So it actually pays attention to the preferred widths
		table.setAutoCreateColumnsFromModel (true);	// Without this, no TableColumns are added to the column model
		table.setRowSelectionAllowed (true);
		table.setColumnSelectionAllowed (false);
		table.setRowSorter (tableSorter);

		// Put the grid into a scrolling area
		final GridBagConstraints gridConstraints = getUtils ().createConstraintsBothFill (0, parentsAndGridPanelY, getGridSpanX (), 1, INSET);
		gridConstraints.weightx = 1;
		gridConstraints.weighty = getGridWeightY ();
		
		final JScrollPane scrollPane = new JScrollPane (table);
		parentsAndGridPanel.add (scrollPane, gridConstraints);
		parentsAndGridPanelY++;

		// Leave a gap between the grid and buttons

		// Create a panel containing the buttons
		buttonPanel = new JPanel ();
		buttonPanel.setLayout (new GridBagLayout ());

		contentPane.add (buttonPanel, getUtils ().createConstraintsNoFill (1, 0, 1, 1, NO_INSET, GridBagConstraintsNoFill.NORTH));

		// Close action
		final Action closeAction = new MessageDialogAction ("Close", new ImageIcon (getUtils ().loadImage ("/com.ndg.xmleditor.icons/Close.gif")),
			(ev) -> frame.dispose ());

		// Reload action
		final Action reloadAction = new MessageDialogAction ("Reload", new ImageIcon (getUtils ().loadImage ("/com.ndg.xmleditor.icons/Reload.gif")),
			(ev) -> tableModel.fireTableDataChanged ());

		// Edit action
		editAction = new MessageDialogAction ("Edit", new ImageIcon (getUtils ().loadImage ("/com.ndg.xmleditor.icons/Edit.gif")), (ev) ->
		{
			final List<Element> records = container.getChildren (entityTag);
			for (final int rowIndex : table.getSelectedRows ())
			{
				final Element recordToEdit = records.get (table.convertRowIndexToModel (rowIndex));
				editRecord (recordToEdit, false, this);
			}
		});

		// New action
		final Action newAction = new MessageDialogAction ("New", new ImageIcon (getUtils ().loadImage ("/com.ndg.xmleditor.icons/New.gif")),
			(ev) -> editRecord (null, false, this));

		// Copy action
		copyAction = new MessageDialogAction ("Copy", new ImageIcon (getUtils ().loadImage ("/com.ndg.xmleditor.icons/Copy.gif")), (ev) ->
		{
			final List<Element> records = container.getChildren (entityTag);
			for (final int rowIndex : table.getSelectedRows ())
			{
				final Element recordToEdit = records.get (table.convertRowIndexToModel (rowIndex));
				editRecord (recordToEdit, true, this);
			}
		});

		// Delete action
		deleteAction = new MessageDialogAction ("Delete", new ImageIcon (getUtils ().loadImage ("/com.ndg.xmleditor.icons/Delete.gif")), (ev) ->
		{
			// Ask for confirmation
			final int [] selectedRows = table.getSelectedRows ();
			String msg;
			if (selectedRows.length == 1)
				msg = "Are you sure you want to delete this " + typeDefinition.getComplexTypeDefinition ().getSingularName () + "?";
			else
				msg = "Are you sure you want to delete these " + selectedRows.length + " " + typeDefinition.getComplexTypeDefinition ().getPluralName () + "?";

			if (JOptionPane.showConfirmDialog (null, msg, frame.getTitle (), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{
				// Locate all the elements first, otherwise as we start deleting the first few, it then becomes impossible to locate the later rows to delete
				final List<Element> records = container.getChildren (entityTag);

				final Element [] recordsToDelete = new Element [selectedRows.length];
				for (int selectedRowNo = 0; selectedRowNo < selectedRows.length; selectedRowNo++)
					recordsToDelete [selectedRowNo] = records.get (table.convertRowIndexToModel (selectedRows [selectedRowNo]));

				// Now can just delete them all
				for (final Element recordToDelete : recordsToDelete)
					recordToDelete.detach ();

				// Need this, otherwise the bank editor with its custom sheet filter bombs out when fireTableDataChanged calls BalanceColumn.recalculateBalances ()
				// since tableSorter.getViewRowCount () still returns the old number of records from before the row was deleted; calling sort () forces it to refresh itself
				tableSorter.sort ();

				// Remove the row on screen
				tableModel.fireTableDataChanged ();
			}
		});

		// Move up action
		moveUpAction = new MessageDialogAction ("Move", new ImageIcon (getUtils ().loadImage ("/com.ndg.xmleditor.icons/MoveUp.gif")), (ev) ->
		{
			// First put all the elements we're moving into a list - otherwise as we start moving them, it becomes impossible to locate them
			final List<Element> records = container.getChildren (entityTag);

			final List<Element> recordsToMove = new ArrayList<Element> ();
			Element firstRecord = null;
			int firstRowNumber = 0;
			for (final int rowIndex : table.getSelectedRows ())
			{
				final Element thisRecord = records.get (table.convertRowIndexToModel (rowIndex));
				recordsToMove.add (thisRecord);

				if (firstRecord == null)
				{
					firstRecord = thisRecord;
					firstRowNumber = rowIndex;
				}
			}

			// Find the index into the container's list of the first record
			// Since this is the first record we're removing, elements under the container prior to this will be unaffected by the remove lower down
			int insertionPoint = container.indexOf (firstRecord);

			// Step back until we find another element - if its this record type then we'll insert in front of it
			// If we find a record of another type, then the "move up" wasn't really valid, so we've already gone too far
			boolean keepGoing = (insertionPoint > 0);
			while (keepGoing)
			{
				insertionPoint--;
				if (container.getContent (insertionPoint) instanceof Element)
				{
					final Element thisElement = (Element) container.getContent (insertionPoint);
					if (thisElement.getName ().equals (entityTag))
						keepGoing = false;
					else
					{
						// Found a different entity - also stop, but we've actually already gone back too far
						keepGoing = false;
						insertionPoint++;
					}
				}
				else
					keepGoing = (insertionPoint > 0);
			}

			// Then remove them all from their current position in the container
			for (final Element recordToDetach : recordsToMove)
				recordToDetach.detach ();

			// Add them into the new location
			container.addContent (insertionPoint, recordsToMove);
			tableModel.fireTableDataChanged ();

			// Re-select the rows
			table.setRowSelectionInterval (firstRowNumber - 1, firstRowNumber + recordsToMove.size () - 2);
		});

		// Move down action
		moveDownAction = new MessageDialogAction ("Move", new ImageIcon (getUtils ().loadImage ("/com.ndg.xmleditor.icons/MoveDown.gif")), (ev) ->
		{
			// First put all the elements we're moving into a list - otherwise as we start moving them, it becomes impossible to locate them
			final List<Element> records = container.getChildren (entityTag);

			final List<Element> recordsToMove = new ArrayList<Element> ();
			Element lastRecord = null;
			int lastRowNumber = 0;
			for (final int rowIndex : table.getSelectedRows ())
			{
				final Element thisRecord = records.get (table.convertRowIndexToModel (rowIndex));
				recordsToMove.add (thisRecord);

				lastRecord = thisRecord;
				lastRowNumber = rowIndex;
			}

			// Find the index into the container's list of the last record
			int insertionPoint = container.indexOf (lastRecord);

			// Then remove them all from their current position in the container
			for (final Element recordToDetach : recordsToMove)
				recordToDetach.detach ();

			// All the removed elements would have been positioned on or before insertionPoint, so now we've
			// removed them, to keep pointing at the same place we need to adjust the insertion point
			insertionPoint = insertionPoint - recordsToMove.size ();

			// Step forward until we find another element - if its this record type then we'll insert after it (i.e. at its location + 1)
			// If we find a record of another type, then we insert directly before it (i.e. at its location)
			boolean keepGoing = (insertionPoint + 1 < container.getContentSize ());
			while (keepGoing)
			{
				insertionPoint++;
				if (container.getContent (insertionPoint) instanceof Element)
				{
					final Element thisElement = (Element) container.getContent (insertionPoint);
					if (thisElement.getName ().equals (entityTag))
					{
						keepGoing = false;
						insertionPoint++;
					}
					else
					{
						// Found a different entity - insert just before it
						keepGoing = false;
					}
				}
				else
					keepGoing = (insertionPoint + 1 < container.getContentSize ());
			}

			// Add them into the new location
			container.addContent (insertionPoint, recordsToMove);
			tableModel.fireTableDataChanged ();

			// Re-select the rows
			table.setRowSelectionInterval (lastRowNumber - recordsToMove.size () + 2, lastRowNumber + 1);
		});

		// Create the buttons - most of them are two wide, to accomodate the move up/down buttons being on the same row
		buttonPanel.add (new JButton (closeAction),		getUtils ().createConstraintsHorizontalFill (0, 0, 2, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		buttonPanel.add (new JButton (reloadAction),	getUtils ().createConstraintsHorizontalFill (0, 1, 2, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		buttonPanel.add (new JButton (editAction),		getUtils ().createConstraintsHorizontalFill (0, 2, 2, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		buttonPanel.add (new JButton (newAction),		getUtils ().createConstraintsHorizontalFill (0, 3, 2, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		buttonPanel.add (new JButton (copyAction),		getUtils ().createConstraintsHorizontalFill (0, 4, 2, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		buttonPanel.add (new JButton (deleteAction),	getUtils ().createConstraintsHorizontalFill (0, 5, 2, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
		
		final GridBagConstraints moveUpConstraints = getUtils ().createConstraintsHorizontalFill (0, 6, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE);
		moveUpConstraints.weightx = 0.5;		// Make the up/down buttons the same width
		buttonPanel.add (new JButton (moveUpAction), moveUpConstraints);
		
		final GridBagConstraints moveDownConstraints = getUtils ().createConstraintsHorizontalFill (1, 6, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE);
		moveDownConstraints.weightx = 0.5;
		buttonPanel.add (new JButton (moveDownAction), moveDownConstraints);

		// Create buttons for drilling down into child entities
		buttonPanelY = 7;
		drillDownActions = new ArrayList<DrillDownAction> ();
		createDrillDownButtons ();

		// Allow double clicks on the grid to edit
		table.addMouseListener (new MouseAdapter ()
		{
			@Override
		    public void mouseClicked (final MouseEvent e)
		    {
		    	if ((e.getClickCount () == 2) && (e.getButton () == MouseEvent.BUTTON1))
		    		editAction.actionPerformed (null);
		    }
		});

		// Many of the buttons should only be enabled when a valid row is selected
		table.getSelectionModel ().addListSelectionListener (e ->
		{
			boolean enabled = (table.getSelectedRowCount () > 0);
			editAction.setEnabled (enabled);
			copyAction.setEnabled (enabled);
			deleteAction.setEnabled (enabled);

			for (final DrillDownAction thisDrillDownAction : drillDownActions)
				thisDrillDownAction.setEnabled (enabled);

			// Additionally, move up and down only make sense if no column heading has been clicked on to sort the table
			// And we also need more rows than are selected, so we have some rows to move relative to
			enabled = (table.getSelectedRowCount () > 0) && (table.getSelectedRowCount () < table.getRowCount ()) && (tableSorter.getSortKeys ().size () == 0);
			moveUpAction.setEnabled (enabled);
			moveDownAction.setEnabled (enabled);
		});

		// To set the state of the move up and down buttons correctly, we also need to listen for changes in the sort order
		tableSorter.addRowSorterListener (e ->
		{
			final boolean enabled = (table.getSelectedRowCount () > 0) && (table.getSelectedRowCount () < table.getRowCount ()) && (tableSorter.getSortKeys ().size () == 0);
			moveUpAction.setEnabled (enabled);
			moveDownAction.setEnabled (enabled);
		});

		// The actions above, which require a valid row to be selected, must be initially disabled
		editAction.setEnabled (false);
		copyAction.setEnabled (false);
		deleteAction.setEnabled (false);
		moveUpAction.setEnabled (false);
		moveDownAction.setEnabled (false);

		// Show frame
		frame.setContentPane (contentPane);
		frame.pack ();
		mdiEditor.add (frame);
		frame.setVisible (true);
	}

	/**
	 * @return The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 */
	public final LocalElementEx getEntityElement ()
	{
		return entityElement;
	}

	/**
	 * @param elem The xsd:element node of the entity being edited from the XSD, i.e. for a top level entity, this will be a the entry under the xsd:sequence under the database complex type
	 */
	public final void setEntityElement (final LocalElementEx elem)
	{
		entityElement = elem;
	}
	
	/**
	 * @return The xsd:complexType node of the entity being edited from the XSD
	 */
	public final ComplexTypeReference getTypeDefinition ()
	{
		return typeDefinition;
	}

	/**
	 * @param def The xsd:complexType node of the entity being edited from the XSD
	 */
	public final void setTypeDefinition (final ComplexTypeReference def)
	{
		typeDefinition = def;
	}
	
	/**
	 * @return If this is a child entity, returns the parent record; if this is a top level entity, returns null
	 */
	public final Element getParentRecord ()
	{
		return parentRecord;
	}

	/**
	 * @param rec If this is a child entity, this value holds the parent record; if this is a top level entity, this value will be null
	 */
	public final void setParentRecord (final Element rec)
	{
		parentRecord = rec;
	}
	
	/**
	 * @return Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 */
	public final LocalElementEx [] getParentEntityElements ()
	{
		return parentEntityElements;
	}

	/**
	 * @param elems Array of xsd:element entries that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 */
	public final void setParentEntityElements (final LocalElementEx [] elems)
	{
		parentEntityElements = elems;
	}
	
	/**
	 * @return Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 */
	public final ComplexTypeReference [] getParentTypeDefinitions ()
	{
		return parentTypeDefinitions;
	}
	
	/**
	 * @param defs Array of type definitions that have been drilled down to to reach here - earliest parent first in the list, immediate parent to entity last in the list - or null if this is a top level entity
	 */
	public final void setParentTypeDefinitions (final ComplexTypeReference [] defs)
	{
		parentTypeDefinitions = defs;
	}
	
	/**
	 * @return The main MDI window for the editor
	 */
	public final XmlEditorMain getMdiEditor ()
	{
		return mdiEditor;
	}

	/**
	 * @param mdi The main MDI window for the editor
	 */
	public final void setMdiEditor (final XmlEditorMain mdi)
	{
		mdiEditor = mdi;
	}
	
	/**
	 * @return The XML tag/name of this entity
	 */
	public String getEntityTag ()
	{
		return entityTag;
	}

	/**
	 * @return The container where we should look for records
	 */
	public Element getContainer ()
	{
		return container;
	}

	/**
	 * @return The left hand pane containing the parent regions and the grid
	 */
	public JPanel getParentsAndGridPanel ()
	{
		return parentsAndGridPanel;
	}

	/**
	 * @return The table containing all the data
	 */
	public final JTable getTable ()
	{
		return table;
	}

	/**
	 * @return The table model which maps the XML data into grid cells
	 */
	public XmlTableModel getTableModel ()
	{
		return tableModel;
	}

	/**
	 * @return Sort and filter interface for the table
	 */
	public TableRowSorter<XmlTableModel> getTableSorter ()
	{
		return tableSorter;
	}

	/**
	 * @return The right hand pane containing all the buttons
	 */
	protected JPanel getButtonPanel ()
	{
		return buttonPanel;
	}

	/**
	 * Creates areas on the form showing the values of the whole chain of parent records that led us here
	 * @throws XmlEditorException If there a syntax problem parsing the XSD
	 * @throws IOException If there is a problem with missing prototype beans
	 */
	protected void createParentRegions ()
		throws XmlEditorException, IOException
	{
		if (parentEntityElements != null)
		{
			// Build up an array of all the parent, grandparent, etc. records, with the earliest parent first in the list, immediate parent to entity last in the list
			final Element [] parentRecords = new Element [parentEntityElements.length];
			Element thisParent = parentRecord;
			for (int index = parentRecords.length - 1; index >= 0; index--)
			{
				parentRecords [index] = thisParent;
				thisParent = (Element) thisParent.getParent ();
			}

			// Now create regions for them all, earliest first
			for (int index = 0; index < parentRecords.length; index++)
			{
				// Set up the list of parent definitions appropriate for this level
				// We also need the parent of this parent (to search for foreign keys)
				LocalElementEx [] thisParentEntityElements;
				Element parentOfParent;

				if (index == 0)
				{
					thisParentEntityElements = null;
					parentOfParent = null;
				}
				else
				{
					thisParentEntityElements = new LocalElementEx [index];
					for (int copyParentEntityElements = 0; copyParentEntityElements < index; copyParentEntityElements++)
						thisParentEntityElements [copyParentEntityElements] = parentEntityElements [copyParentEntityElements];

					parentOfParent = parentRecords [index - 1];
				}

				// Create the panel, with a label top left showing the entity name of each parent record
				final JPanel thisParentPanel = getMdiEditor ().getDispatcher ().createRecordPanel (parentEntityElements [index], parentTypeDefinitions [index],
					parentRecords [index], parentOfParent, thisParentEntityElements, false).getPanel ();

				// This sets up a triple border!  Etched, title and gap
				thisParentPanel.setBorder (BorderFactory.createCompoundBorder (BorderFactory.createTitledBorder
					(BorderFactory.createEtchedBorder (EtchedBorder.LOWERED),
					parentTypeDefinitions [index].getComplexTypeDefinition ().getSingularName (), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.BELOW_TOP, thisParentPanel.getFont ().deriveFont (Font.BOLD)),
					BorderFactory.createEmptyBorder (INSET, INSET, INSET, INSET)));

				parentsAndGridPanel.add (thisParentPanel, getUtils ().createConstraintsNoFill
					(0, parentsAndGridPanelY, 1, getParentRegionSpanY (), INSET, GridBagConstraintsNoFill.WEST));
				parentsAndGridPanelY = parentsAndGridPanelY + getParentRegionSpanY ();
			}
		}
	}

	/**
	 * Builds the list of columns to display in the grid
	 * @return The list of columns to display in the grid
	 * @throws XmlEditorException If there a syntax problem parsing the XSD
	 */
	protected List<XmlGridColumn> buildColumnsList ()
		throws XmlEditorException
	{
		final List<XmlGridColumn> columns = new ArrayList<XmlGridColumn> ();

		// Build complete inheritance path for this type
		final List<ComplexTypeReference> inheritancePath = mdiEditor.getXmlDocuments ().determineInheritancePath (typeDefinition);
		for (final ComplexTypeReference thisTypeDefinition : inheritancePath)
			if (thisTypeDefinition == null)
			{
				// Drilling down into a 0..unbounded list of a simple type, so there is no complex type def in which to look for attributes or elements.
				// So in this case we just add a dummy column to represent the repeating simple type.
				log.debug ("buildColumnsList adding repeating single type " + getEntityElement ().getName () + " of type " + getEntityElement ().getType ()); 

				if (getEntityElement ().isShowInGrid ())
					columns.add (new RepeatingElementGridColumn (getEntityElement (), mdiEditor.getXmlDocuments ()));

				// To check if this is a foreign key, we need to wind back the last entry of parentEntityElements and treat this as the entityElement to pass in.
				// NB. Although the editor supports repeating single field foreign keys, the XML Schema implementation itself doesn't really :(
				// If you try to set this up and then use the XSD to validate an XML with repeating foregin key fields, it complains the xpath doesn't resolve to a unique field.
				final XmlForeignKeyReference foreignKeyReference = mdiEditor.getXmlDocuments ().checkIfFieldIsForeignKey
					(Arrays.copyOf (parentEntityElements, parentEntityElements.length - 1),
					parentEntityElements [parentEntityElements.length - 1], (Element) parentRecord.getParent (), entityElement.getName ());

				if (foreignKeyReference != null)
					addForeignKeyColumns (entityElement, foreignKeyReference, columns);
				
				// Check if this unbounded list is an enum
				if (entityElement.isShowReferencedColumns ())
				{
					final String [] [] enumerations = mdiEditor.getXmlDocuments ().findEnumerationsForType (getEntityElement ().getType (), false);
					if ((enumerations != null) && (enumerations.length == 2))
						columns.add (new RepeatingElementEnumGridColumn (entityElement, mdiEditor.getXmlDocuments (), enumerations));
				}
			}	
			else
			{
				// Attributes
				for (final AttributeEx thisAttribute : thisTypeDefinition.getComplexTypeDefinition ().getAttributes ())
				{
					log.debug ("buildColumnsList adding attribute " + thisAttribute.getName () + " of type " + thisAttribute.getType () + " from complex type " +
						thisTypeDefinition.getComplexTypeDefinition ().getName () + " in XSD " + thisTypeDefinition.getXmlDocument ().getXsd ().getTargetNamespace ()); 
					
					// Found an attribute to add
					if (thisAttribute.isShowInGrid ())
						columns.add (new AttributeGridColumn (thisTypeDefinition.getComplexTypeDefinition (), mdiEditor.getXmlDocuments (), thisAttribute));
	
					// Check if this attribute is a foreign key
					final String fieldTag = thisAttribute.getName ();
					final XmlForeignKeyReference foreignKeyReference = mdiEditor.getXmlDocuments ().checkIfFieldIsForeignKey
						(parentEntityElements, entityElement, parentRecord, "@" + fieldTag);
	
					if (foreignKeyReference != null)
						addForeignKeyColumns (thisAttribute, foreignKeyReference, columns);
	
					// Check if this attribute is an enum
					if (thisAttribute.isShowReferencedColumns ())
					{
						final String [] [] enumerations = mdiEditor.getXmlDocuments ().findEnumerationsForType (thisAttribute.getType (), false);
						if ((enumerations != null) && (enumerations.length == 2))
							columns.add (new AttributeEnumGridColumn (thisTypeDefinition.getComplexTypeDefinition (), mdiEditor.getXmlDocuments (), enumerations, thisAttribute));
					}
				}
	
				// Elements
				for (final LocalElementEx thisElement : thisTypeDefinition.getComplexTypeDefinition ().getElements (mdiEditor.getXmlDocuments ()))
				{
					log.debug ("buildColumnsList adding element " + thisElement.getName () + " of type " + thisElement.getType () + " from complex type " +
						thisTypeDefinition.getComplexTypeDefinition ().getName () + " in XSD " + thisTypeDefinition.getXmlDocument ().getXsd ().getTargetNamespace ()); 
	
					// Found an element to add
					if (thisElement.isShowInGrid ())
						columns.add (new ElementGridColumn (thisTypeDefinition.getComplexTypeDefinition (), mdiEditor.getXmlDocuments (), thisElement));
	
					// Check if this element is a foreign key
					final String fieldTag = thisElement.getName ();
					final XmlForeignKeyReference foreignKeyReference = mdiEditor.getXmlDocuments ().checkIfFieldIsForeignKey
						(parentEntityElements, entityElement, parentRecord, fieldTag);
	
					if (foreignKeyReference != null)
						addForeignKeyColumns (thisElement, foreignKeyReference, columns);
	
					// Check if this element is an enum
					if (thisElement.isShowReferencedColumns ())
					{
						final String [] [] enumerations = mdiEditor.getXmlDocuments ().findEnumerationsForType (thisElement.getType (), false);
						if ((enumerations != null) && (enumerations.length == 2))
							columns.add (new ElementEnumGridColumn (thisTypeDefinition.getComplexTypeDefinition (), mdiEditor.getXmlDocuments (), enumerations, thisElement));
					}
				}
			}
		
		// If we got no columns at all, just add a dummy column that says "Record" so the row is at least visible in the grid
		if (columns.size () == 0)
			columns.add (new DummyGridColumn ());

		return columns;
	}

	/**
	 * Given a known foreign key, where we've already found the primary key it refers to, adds columns to show each of the referenced fields
	 * @param sourceField The source foreign key column where the reference is coming from - can be either an xsd:element or xsd:attribute node
	 * @param foreignKeyReference Details of the foreign key reference we are creating columns for
	 * @param columns The list to add the foreign key columns to
	 * @throws XmlEditorException If the complex type referred to is not found
	 */
	private void addForeignKeyColumns (final FieldEx sourceField, final XmlForeignKeyReference foreignKeyReference, final List<XmlGridColumn> columns)
		throws XmlEditorException
	{
		// Get the selector & field it refers to
		// Ensure its a PK based on only a single field - multi-key primary keys are only supported as constraints, they can't be used as foreign key targets
		final List<Field> fields = foreignKeyReference.getPrimaryKey ().getField ();
		final Selector selector = foreignKeyReference.getPrimaryKey ().getSelector ();

		if ((selector != null) && (fields.size () == 1))
		{
			final Field field = fields.get (0);
			final String selectorText = selector.getXpath ();
			final String fieldText = field.getXpath ();

			// Find the definition of the referred-to entity
			// Is this right?  Its making a (possibly invalid) assumption that the element name (from the xpath) matches the complex type name?
			final ComplexTypeReference refersToEntity = new ComplexTypeReference (foreignKeyReference.getReferencedDocument (),
				foreignKeyReference.getReferencedDocument ().getXsd ().findTopLevelComplexTypeAndTryOtherSchemas (selectorText, mdiEditor.getXmlDocuments ()));
			
			// Find the primary key on the referenced entity
			// Remember to strip the @ off the field name
			final AttributeEx refersToEntityPrimaryKey = mdiEditor.getXmlDocuments ().findAttributeForComplexTypeOrParent (refersToEntity, fieldText.substring (1));

			// Then display all the referenced entity's attributes
			final List<LocalElementEx> refersToFields = mdiEditor.getXmlDocuments ().getReferencedFieldList (sourceField, refersToEntity);
			for (final LocalElementEx thisRefersToField : refersToFields)
			{
				log.debug ("addForeignKeyColumns adding a column for element " + thisRefersToField);
				
				final XmlGridColumn newColumn;

				if (typeDefinition == null)
					newColumn = new RepeatingElementForeignKeyGridColumn (null, mdiEditor.getXmlDocuments (),
						refersToEntityPrimaryKey, thisRefersToField, refersToEntity.getComplexTypeDefinition (), foreignKeyReference.getContainer (), (LocalElementEx) sourceField);
				
				else if (sourceField instanceof LocalElementEx)
					newColumn = new ElementForeignKeyGridColumn (typeDefinition.getComplexTypeDefinition (), mdiEditor.getXmlDocuments (),
						refersToEntityPrimaryKey, thisRefersToField, refersToEntity.getComplexTypeDefinition (), foreignKeyReference.getContainer (), (LocalElementEx) sourceField);
				else
					newColumn = new AttributeForeignKeyGridColumn (typeDefinition.getComplexTypeDefinition (), mdiEditor.getXmlDocuments (),
						refersToEntityPrimaryKey, thisRefersToField, refersToEntity.getComplexTypeDefinition (), foreignKeyReference.getContainer (), (AttributeEx) sourceField);

				columns.add (newColumn);
			}
		}
	}

	/**
	 * Searches for child entities of this entity, and creates a drill down button for each
	 * @throws XmlEditorException If the definition for one of the drill down entities cannot be found
	 */
	private final void createDrillDownButtons ()
		throws XmlEditorException
	{
		// Add this entity to the array of parents
		LocalElementEx [] parentEntityElementsPlusThisEntity;
		ComplexTypeReference [] parentTypeDefinitionsPlusThisEntity;
		if (parentTypeDefinitions == null)
		{
			parentEntityElementsPlusThisEntity = new LocalElementEx [1];
			parentTypeDefinitionsPlusThisEntity = new ComplexTypeReference [1];
		}
		else
		{
			parentEntityElementsPlusThisEntity = new LocalElementEx [parentTypeDefinitions.length + 1];
			parentTypeDefinitionsPlusThisEntity = new ComplexTypeReference [parentTypeDefinitions.length + 1];

			for (int parentNo = 0; parentNo < parentTypeDefinitions.length; parentNo++)
			{
				parentEntityElementsPlusThisEntity [parentNo] = parentEntityElements [parentNo];
				parentTypeDefinitionsPlusThisEntity [parentNo] = parentTypeDefinitions [parentNo];
			}
		}

		parentEntityElementsPlusThisEntity [parentEntityElementsPlusThisEntity.length - 1] = entityElement;
		parentTypeDefinitionsPlusThisEntity [parentTypeDefinitionsPlusThisEntity.length - 1] = typeDefinition;

		// Look for unbounded elements on this entity and all parents
		final List<ComplexTypeReference> inheritancePath = mdiEditor.getXmlDocuments ().determineInheritancePath (typeDefinition);
		for (final ComplexTypeReference thisTypeDefinition : inheritancePath)
			if (thisTypeDefinition != null)
				for (final LocalElementEx childEntityElement : thisTypeDefinition.getComplexTypeDefinition ().getChildEntities (mdiEditor.getXmlDocuments ()))
				{
					// Found a child entity
					final QName thisType = childEntityElement.getType ();
					ComplexTypeReference childTypeDefinition = null;
					try
					{
						childTypeDefinition = mdiEditor.getXmlDocuments ().findDefinitionOfComplexType (thisType);
					}
					catch (final XmlEditorException e)
					{
						// Ignore, it might be a 0..unbounded simple type, so still create a drill down button for it to make it look like a child entity
					}
	
					final DrillDownAction childAction = new DrillDownAction (entityElement, childEntityElement, childTypeDefinition,
						container, table, parentEntityElementsPlusThisEntity, parentTypeDefinitionsPlusThisEntity, mdiEditor);
					drillDownActions.add (childAction);
					childAction.setEnabled (false);	// No row is initially selected
	
					final JButton childEntityButton = new JButton (childAction);
					buttonPanel.add (childEntityButton, getUtils ().createConstraintsHorizontalFill (0, buttonPanelY, 2, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
					buttonPanelY++;
	
					// Any long description for this entity?
					// Unfortunately these tooltips ignore carriage returns, even though they're read correctly from the XSD
					if (childTypeDefinition != null)
						childEntityButton.setToolTipText (childTypeDefinition.getComplexTypeDefinition ().getLongDescription ());
				}
	}

	/**
	 * Brings up the single record display to edit the specified record
	 * Purpose of it being declared here is to save the long param declarations in the code, and to trap exceptions
	 * If need to create a specialised detail form, the proper place to do so is by overriding the dispatcher, not here
	 * @param recordToEdit Record to edit, or null if adding a new record
	 * @param recordBeingCopied True if we're copying the specified record rather than editing it
	 * @param grid Grid UI tat this record is being added/edited from
	 */
	public void editRecord (final Element recordToEdit, final boolean recordBeingCopied, final XmlEditorGrid grid)
	{
		try
		{
			mdiEditor.getDispatcher ().showRecord (entityElement, typeDefinition,
				mdiEditor, recordToEdit, recordBeingCopied,
				parentRecord, parentEntityElements, parentTypeDefinitions, grid);
		}
		catch (final Exception e)
		{
			e.printStackTrace ();
			JOptionPane.showMessageDialog (null, e.toString (), frame.getTitle (), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * @param insertionPoint Default determined insertion point when adding a new record, to add the record at the bottom
	 * @return Adjusted insertion point if we want to add the record elsewhere
	 */
	public int adjustInsertionPoint (final int insertionPoint)
	{
		return insertionPoint;
	}
	
	/**
	 * @return GridBagLayout weighty value to assign to the main grid on the form - if adding more grid, may need to reassign this down to make the grids resize nicely
	 */
	public double getGridWeightY ()
	{
		return 1;
	}
	
	/**
	 * @return GridBagLayout spanx value to assign to the main grid of the form
	 */
	public int getGridSpanX ()
	{
		return 1;
	}
	
	/**
	 * @return GridBagLayout spany value to assign to the parent panel
	 */
	public int getParentRegionSpanY ()
	{
		return 1;
	}

	/**
	 * @return UI utils
	 */
	public final NdgUIUtils getUtils ()
	{
		return utils;
	}

	/**
	 * @param obj UI utils
	 */
	public final void setUtils (final NdgUIUtils obj)
	{
		utils = obj;
	}

	/**
	 * @return Y value of the next component added to the parentsAndGridPanel GridBagLayout
	 */
	public final int getParentsAndGridPanelY ()
	{
		return parentsAndGridPanelY;
	}

	/**
	 * @param y Y value of the next component added to the parentsAndGridPanel GridBagLayout
	 */
	public final void setParentsAndGridPanelY (final int y)
	{
		parentsAndGridPanelY = y;
	}
	
	/**
	 * @return Y value of the next component added to the buttonPanel GridBagLayout
	 */
	public final int getButtonPanelY ()
	{
		return buttonPanelY;
	}

	/**
	 * @param y Y value of the next component added to the buttonPanel GridBagLayout
	 */
	public final void setButtonPanelY (final int y)
	{
		buttonPanelY = y;
	}
	
	/**
	 * @return The frame/window itself
	 */
	public final XmlGridFrame getFrame ()
	{
		return frame;
	}
}