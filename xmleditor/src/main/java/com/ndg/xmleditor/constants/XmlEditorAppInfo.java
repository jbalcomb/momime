package com.ndg.xmleditor.constants;

/**
 * Tags used under xsd:appinfo structure for XSD files used by the XML Editor
 */
public class XmlEditorAppInfo
{
	/** Full URI of the nxe: namespace */
	public final static String NAMESPACE_URI = "http://www.ndg.com/xmleditor/1.0.0";

	/**
	 * Specifies whether a backup copy of the main database should be taken before it is opened
	 */
	public final static String TAG_VALUE_BACKUP_DATABASE_ON_OPEN = "backuponopen";

	/**
	 * Value indicating that a backup copy of the main database should be taken before it is opened
	 * @see #TAG_VALUE_BACKUP_DATABASE_ON_OPEN
	 */
	public final static String VALUE_BACKUP_DATABASE_ON_OPEN_YES = "Y";

	/** Container specifying details about each database/XML file */
	public final static String TAG_ENTITY_DATABASE = "database";

	/** Specifies which namespace URI this database entry is giving details about */
	public final static String TAG_ATTRIBUTE_DATABASE_NAMESPACE_URI = "namespaceURI";

	/** The name/title of this database/XML file (used for example when asking for the location of the XML file) */
	public final static String TAG_VALUE_DATABASE_TITLE = "title";

	/** The default location of the XML file for this namespace */
	public final static String TAG_VALUE_DATABASE_XML_FILENAME = "xmlfilename";

	/** Prefix for XML files located in user's My Documents folder */
	public final static String DATABASE_XML_FILENAME_MY_DOCUMENTS_PREFIX = "%MY_DOCUMENTS%";

	/**
	 * Whether the location given is a full path or relative to the location of the XSD file
	 * @see #TAG_VALUE_DATABASE_XML_FILENAME
	 */
	public final static String TAG_ATTRIBUTE_DATABASE_XML_FILENAME_TYPE = "type";

	/** Singular description of an entity, used as the caption when editing a record from this entity */
	public final static String TAG_ATTRIBUTE_COMPLEX_TYPE_NAME_SINGULAR = "singular";

	/** Plural description of an entity, used as the text on the menu option if a top level entity, or on the >> button if a child entity */
	public final static String TAG_ATTRIBUTE_COMPLEX_TYPE_NAME_PLURAL = "plural";

	/**
	 * For top level entities, specifies if the option for the entity will appear on a submenu
	 * If ommitted, the option will appear directly under the edit menu
	 */
	public final static String TAG_ATTRIBUTE_COMPLEX_TYPE_APPEARS_ON_SUB_MENU = "submenu";

	// Fields (values common to both Attributes and Elements)

	/** Display label for an attribute */
	public final static String TAG_ATTRIBUTE_FIELD_LABEL = "label";

	/** The width used to display this attribute in grids */
	public final static String TAG_ATTRIBUTE_FIELD_GRID_WIDTH = "gridwidth";

	/**
	 * Controls whether this column appears in grids or not.
	 * Setting it to false does not prevent fields from the referenced entity from being included
	 * in the grid if this field is a foreign key.
	 */
	public final static String TAG_ATTRIBUTE_SHOW_FIELD_MAIN_COLUMN_IN_GRID = "showmaincolumn";

	/**
	 * Can be used to switch off display of referenced fields altogether
	 */
	public final static String TAG_ATTRIBUTE_SHOW_FIELD_REFERENCED_COLUMNS_IN_GRID = "showreferencedcolumns";

	/**
	 * Value to stop columns from appearing in the grids
	 * @see #TAG_ATTRIBUTE_SHOW_FIELD_MAIN_COLUMN_IN_GRID
	 */
	public final static String VALUE_SHOW_MAIN_COLUMN_IN_GRID_FALSE = "false";

	/**
	 * By default, foreign key references will display all elements from the referenced entity.
	 * Can instead include a list of these tags under annotation/appinfo under the element,
	 * to specifically list out only the fields from the referenced entity that we want to include.
	 */
	public final static String TAG_ELEMENT_ELEMENT_SHOW_REFERENCED_FIELD = "showreferencedfield";

	/**
	 * XSDs don't support FK references from child entities to other child entities, for example
	 * the language entry/category combination in the MoM IME language XML file must match up
	 * with a corresponding language entry/category combination in the server XML file.
	 * But we still need to define the FK - so to stop it throwing out incorrect validation errors,
	 * we stop the validation from working by prefixing the selector xpath by this value
	 */
	public final static String KEY_REF_SELECTOR_XPATH_PREFIX_MULTI_LEVEL_FK_TO_SECONDARY_DB = "multilevelFKtosecondaryDB/";

	/**
	 * Prevent instatiation of this class
	 */
	private XmlEditorAppInfo ()
	{
	}
}