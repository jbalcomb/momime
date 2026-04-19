package com.ndg.xmleditor.grid;

import javax.swing.JInternalFrame;

import com.ndg.xmleditor.schema.ComplexTypeEx;

/**
 * Special class for grid frames, to enable the MDI to locate them and trigger table updates whenever a particular entity changes
 */
public class XmlGridFrame extends JInternalFrame
{
	/**
	 * The xsd:complexType node of the entity being edited from the XSD
	 */
	private ComplexTypeEx typeDefinition;
	
	/**
	 * The table model displaying the XML data on this frame 
	 */
	private XmlTableModel tableModel;
	
	/**
	 * Creates a special frame for an XML editor grid, to enable the MDI to locate it and trigger table updates whenever a particular entity changes
	 * @param aTitle The title to display on the window
	 * @param aTypeDefinition The xsd:complexType node of the entity being edited from the XSD
	 * @param aTableModel The table model displaying the XML data on this frame 
	 */
	public XmlGridFrame (final String aTitle, final ComplexTypeEx aTypeDefinition, final XmlTableModel aTableModel)
	{
		super (aTitle, true, true, true, true);
		
		typeDefinition = aTypeDefinition;
		tableModel = aTableModel;
	}

	/**
	 * Allows public access to the type definition being displayed
	 * @return The xsd:complexType node of the entity being edited from the XSD
	 */
	public final ComplexTypeEx getTypeDefinition ()
	{
		return typeDefinition;
	}
	
	/**
	 * Causes the displayed table to reload itself 
	 */
	public final void fireTableDataChanged ()
	{
		tableModel.fireTableDataChanged ();
	}
}