package com.ndg.xmleditor.editor;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.xml.namespace.QName;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.transform.JDOMSource;
import org.w3._2001.xmlschema.Import;
import org.w3._2001.xmlschema.OpenAttrs;
import org.w3._2001.xmlschema.TopLevelElement;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import com.ndg.utils.FileNameUtils;
import com.ndg.utils.swing.GridBagConstraintsHorizontalFill;
import com.ndg.utils.swing.GridBagConstraintsNoFill;
import com.ndg.utils.swing.NdgUIUtils;
import com.ndg.utils.swing.actions.MessageDialogAction;
import com.ndg.utils.swing.components.FilenameTextField;
import com.ndg.utils.swing.filefilters.ExtensionFileFilter;
import com.ndg.xmleditor.constants.XmlEditorAppInfo;
import com.ndg.xmleditor.constants.XsdConstants;
import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.doc.ListOfXmlDocumentsImpl;
import com.ndg.xmleditor.doc.XmlDocument;
import com.ndg.xmleditor.schema.SchemaEx;
import com.ndg.xmleditor.schema.XmlDatabaseInfo;
import com.ndg.xmleditor.schema.XmlFilenameType;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Prompt form which asks for the location of the XSD file, handles loading in the XSD file, then asks for the location of each XML file, loads in these XML files,
 * then finally passes this list of XML files to the main editor window and closes itself
 */
public final class AskForXsdAndXmlFileLocations
{
	/** Class logger */
	private final static Log log = LogFactory.getLog (AskForXsdAndXmlFileLocations.class);
	
	/** The title/caption to appear on this form */
	private final static String FORM_TITLE = "Choose paths of XSD and XML database(s) to open";

	/** Mininum width to force the edit boxes, so the form doesn't look all cramped up */
	private final static int EDIT_BOX_MINIMUM_WIDTH = 500;

	/** Typical inset used on this screen layout */
	private final static int INSET = 3;

	/** Special zero inset for layout components */
	private final static int NO_INSET = 0;

	/** The frame/window itself */
	private JFrame frame;

	/** The content pane within the frame */
	private JPanel contentPane;

	/** The label prompting for the filename of the XSD file */
	private JLabel xsdLabel;

	/** Full path to the XSD file */
	private String xsdFilename;
	
	/** The edit box prompting for the location of the XSD file */
	private FilenameTextField xsdEdit;

	/** Action which loads in the chosen XSD file */
	private Action loadXsdAction;

	/** Action which loads in the chosen XML file(s) */
	private Action loadXmlAction;

	/** Listener triggered whenever any of the XML filename(s) are changed */
	private DocumentListener xmlEditDocumentListener;

	/** JDOM XML parser */
	private SAXBuilder parser;

	/** XSD factory */
	private SchemaFactory schemaFactory;

	/** A list of the main XML document being edited, plus any referenced documents */
	private ListOfXmlDocuments xmlDocuments;

	/** Dispatcher to use to open grid and single record displays with */
	private XmlEditorDispatcher dispatcher;

	/** Validator to check that the main doc conforms to the XSD */
	private Validator xsdValidator;

	/** If true, will try to load the XSD from the classpath instead of the filesystem */
	private boolean loadXsdFromClasspath;

	/** Optional resource resolver which can be used to locate referenced XSDs */
	private LSResourceResolver resourceResolver;

	/** UI utils */
	private NdgUIUtils utils;
	
	/** MDI to kick off after completing XSD+XML selection */
	private XmlEditorMain mdiEditor;
	
	/** Unmarshaller for reading XSD files */
	private Unmarshaller xsdUnmarshaller;
	
	/**
	 * Initializes the frame
	 * @throws Exception If JVM does not meet the required minimum
	 */
	public final void init () throws Exception
	{
		getUtils ().useNimbusLookAndFeel ();

		// Create other objects
		xmlDocuments = new ListOfXmlDocumentsImpl ();

		// Action to load XSD file
		loadXsdAction = new MessageDialogAction ("Load XSD File(s)", (ev) ->
		{
			loadXSDs ();
			// xmlDocuments.get (0).determineTopLevelElementTypeDefAndSequence ();
			createXmlLabelsAndTextFields ();

			// Only allow loading XSD once - so disable it and the XSD text field
			xsdLabel.setEnabled (false);
			xsdEdit.setEnabled (false);
			loadXsdAction.setEnabled (false);
		});

		loadXsdAction.setEnabled (false);	// Initially we have no XSD Filename, so disabled

		// Action to load XML file(s)
		loadXmlAction = new MessageDialogAction ("Load XML File(s)", (ev) ->
		{
			// Take a backup copy of the main XML document?
			if (xmlDocuments.get (0).getXsd ().isBackupOnOpen ())
			{
				final String inputFilename = xmlDocuments.get (0).getFileNameTextField ().getText ().trim ();
				final File inputFile = new File (inputFilename);
				if (inputFile.exists ())
				{
					// Ensure the "backups" folder exists
					final File inputFolder = inputFile.getParentFile ();
					final File backupFolder = new File (inputFolder, "backups");
					if (!backupFolder.exists ())
						backupFolder.mkdir ();
					
					// Work out the name to give the backup
					final SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");

					final File backupFile = new File (backupFolder,
						FileNameUtils.changeFileExt (inputFile.getName (), "") + " " +
						dateFormat.format (new Date ()) + ".xml");

					// Copy to the backup file
					Files.copy (inputFile.toPath (), backupFile.toPath (), StandardCopyOption.REPLACE_EXISTING);
				}
			}

			// Load in all the chosen XML documents
			final List<Element> copiedDocuments = new ArrayList<Element> ();
			for (int documentNo = 0; documentNo < xmlDocuments.size (); documentNo++)
			{
				final XmlDocument xmlDocument = xmlDocuments.get (documentNo);
				if (xmlDocument.getFileNameTextField () != null)
				{
					final String thisFilename = xmlDocument.getFileNameTextField ().getText ().trim ();

					try (final FileInputStream in = new FileInputStream (thisFilename))
					{
						xmlDocument.setXmlDocument (parser.build (in));
					}
					catch (final FileNotFoundException e)
					{
						// Only allow the main document to be created
						if (documentNo > 0)
							throw new FileNotFoundException (e.getMessage ());
						{
							// Ask whether to create
							if (JOptionPane.showConfirmDialog (null, e.getMessage () + ", create from scratch?", frame.getTitle (), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
								throw new FileNotFoundException (e.getMessage ());
							else
								xmlDocument.setXmlDocument (createNewXmlDocument ());
						}
					}

					// Hack all referenced XML documents into special elements at the start of the main XML document, this way we can use the XSD to enforce referential integrity across multiple XML files
					// Technically this isn't right, it should check the XSD to find the correct place to insert the element - it just assumes it goes at the start
					if (documentNo > 0)
					{
						final Element copiedDocument = xmlDocument.getXml ().clone ();

						// Move the copied element into our own namespace, i.e. change momimesvr:database to momimesvrdatabase
						// Again this is a bit of a fudge, it just removes the : whereas it should look up the proper element definition
						// copiedDocument.setName (copiedDocument.getNamespacePrefix () + copiedDocument.getName ());
						// copiedDocument.setNamespace (null);

						xmlDocuments.get (0).getXml ().addContent (0, copiedDocument);
						copiedDocuments.add (copiedDocument);
					}
				}
			}

			// Validate the merged XML document against the XSD
			final Document mainDocument = xmlDocuments.get (0).getXmlDocument ();
			xsdValidator.validate (new JDOMSource (mainDocument));

			// Remove the inserted XML documents again
			for (final Element copiedDocument : copiedDocuments)
				copiedDocument.detach ();

			// Kick off the main editor frame, passing across the XSD and list of XmlDocuments
			getMdiEditor ().setXmlDocuments (xmlDocuments);
			getMdiEditor ().init ();

			// Close out this prompting frame
			frame.dispose ();
		});

		loadXmlAction.setEnabled (false);	// Initially we have no XML Filename(s), so disabled

		// All the XML Edit boxes use the same DocumentListener, so create it in one place
		xmlEditDocumentListener = new DocumentListener ()
		{
			private void anyUpdate (@SuppressWarnings ("unused") final DocumentEvent e)
			{
				// Ensure that ALL the XML filename edit boxes have a value
				boolean allHaveFilename = true;

				final Iterator<XmlDocument> xmlDocumentsIterator = xmlDocuments.iterator ();
				while ((allHaveFilename) && (xmlDocumentsIterator.hasNext ()))
				{
					final XmlDocument thisXsd = xmlDocumentsIterator.next ();

					// Ignore any for which we don't need an XML document
					if (thisXsd.isXmlFileRequiredForThisXsd ())
					{
						// Text field can be null, since this listener will be triggered as the text on initial
						// text fields is set, before later text fields have been created
						final JTextField thisTextField = thisXsd.getFileNameTextField ();
						if (thisTextField == null)
							allHaveFilename = false;
						else if (thisTextField.getText ().trim ().length () == 0)
							allHaveFilename = false;
					}
				}

				loadXmlAction.setEnabled (allHaveFilename);
			}

			@Override
			public final void insertUpdate (final DocumentEvent e)
			{
				anyUpdate (e);
			}

			@Override
			public final void removeUpdate (final DocumentEvent e)
			{
				anyUpdate (e);
			}

			@Override
			public final void changedUpdate (final DocumentEvent e)
			{
				anyUpdate (e);
			}
		};

		// Initialize the frame
		frame = new JFrame ();
		frame.setTitle (FORM_TITLE);
		frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);

		// Initialize the content pane
		contentPane = new JPanel ();
		contentPane.setLayout (new GridBagLayout ());
		
		// Put in a sly top row to force the minimum width for the edit boxes
		contentPane.add (Box.createRigidArea (new Dimension (EDIT_BOX_MINIMUM_WIDTH, 0)),
			getUtils ().createConstraintsNoFill (1, 0, 1, 1, NO_INSET, GridBagConstraintsNoFill.EAST));

		// XSD Filename
		xsdLabel = new JLabel ();
		xsdLabel.setText ("XSD Filename:");
		contentPane.add (xsdLabel, getUtils ().createConstraintsNoFill (0, 1, 1, 1, INSET, GridBagConstraintsNoFill.WEST));

		xsdEdit = new FilenameTextField ();
		xsdEdit.addChoosableFileFilter (new ExtensionFileFilter ("xsd", "XML Schema Definition files"));
		contentPane.add (xsdEdit, getUtils ().createConstraintsHorizontalFill (1, 1, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));

		// Only allow clicking Load XSD if a filename has been entered
		xsdEdit.getDocument ().addDocumentListener (new DocumentListener ()
		{
			private void anyUpdate (@SuppressWarnings ("unused") final DocumentEvent e)
			{
				loadXsdAction.setEnabled (xsdEdit.getText ().trim ().length () > 0);
			}

			@Override
			public final void insertUpdate (final DocumentEvent e)
			{
				anyUpdate (e);
			}

			@Override
			public final void removeUpdate (final DocumentEvent e)
			{
				anyUpdate (e);
			}

			@Override
			public final void changedUpdate (final DocumentEvent e)
			{
				anyUpdate (e);
			}
		});
		xsdEdit.setText (xsdFilename);

		// Button to load XSD
		final JButton loadXsdButton = new JButton ();
		loadXsdButton.setAction (loadXsdAction);
		contentPane.add (loadXsdButton, getUtils ().createConstraintsNoFill (1, 2, 1, 1, INSET, GridBagConstraintsNoFill.EAST));

		// Show frame
		frame.setContentPane (contentPane);
		frame.pack ();
		frame.setLocationRelativeTo (null);
		frame.setVisible (true);

		// If the XSD filename has been pre-filled in, then go ahead and load it
		if (loadXsdAction.isEnabled ())
			loadXsdAction.actionPerformed (null);
	}

	/**
	 * Allows external access to the XSD text field
	 * @return Full path to the XSD file
	 */
	public final String getXsdFilename ()
	{
		return (xsdEdit == null) ? xsdFilename : xsdEdit.getText ();
	}
	
	/**
	 * Allows external access to the XSD text field
	 * @param value Full path to the XSD file
	 */
	public final void setXsdFilename (final String value)
	{
		xsdFilename = value;
		if (xsdEdit != null)
			xsdEdit.setText (value);
	}

	/**
	 * @return True to to load the XSD from the classpath, False to load from the filesystem
	 */
	public final boolean isLoadXsdFromClasspath ()
	{
		return loadXsdFromClasspath;
	}
	
	/**
	 * @param newValue True to to load the XSD from the classpath, False to load from the filesystem
	 */
	public final void setLoadXsdFromClasspath (final boolean newValue)
	{
		loadXsdFromClasspath = newValue;
	}

	/**
	 * Loads in the main XSD file chosen on the dialog plus any additional XSDs it imports
	 * This builds up the xmlDocuments list
	 * @throws IOException If one of the XSD files cannot be found or read
	 * @throws SAXException If there is problem creating a validator from the main XSD
	 * @throws XmlEditorException If a problem with the XSD prevents us from finding the values
	 * @throws JAXBException If there is a problem reading one of the XSD files
	 */
	private final void loadXSDs ()
		throws IOException, SAXException, XmlEditorException, JAXBException
	{
		// First load the main XSD
		final String mainXsdFilename = xsdEdit.getText ().trim ();
		String fileSeparator;
		final SchemaEx mainXsd;
		if (loadXsdFromClasspath)
		{
			try (final InputStream mainXsdStream = getClass ().getResourceAsStream (mainXsdFilename))
			{
				mainXsd = (SchemaEx) getXsdUnmarshaller ().unmarshal (mainXsdStream);
			}
			
			xsdValidator = getSchemaFactory ().newSchema (getClass ().getResource (mainXsdFilename)).newValidator ();
			fileSeparator = "/";
		}
		else
		{
			try (final InputStream mainXsdStream = new FileInputStream (mainXsdFilename))
			{
				mainXsd = (SchemaEx) getXsdUnmarshaller ().unmarshal (mainXsdStream);
			}

			xsdValidator = getSchemaFactory ().newSchema (new File (mainXsdFilename)).newValidator ();						
			fileSeparator = null;
		}

		// Verify that the main doc has a definition for the DB
		if (mainXsd.getTopLevelElement () == null)
			throw new XmlEditorException ("Main XSD must have exactly one top level element defined");
		
		// Start creating the XmlDocuments list of all the referenced XML files/Schemas, i.e. all the namespaces
		// even if they don't require a corresponding XML file (e.g. Map XSD)
		
		// First add the main XSD
		xmlDocuments.add (new XmlDocument (mainXsd));

		// The deal with each of the import statements
		log.info ("Resolving XSD namespaces...");
		for (final OpenAttrs obj : mainXsd.getIncludeOrImportOrRedefine ())
			if (obj instanceof Import)
			{
				final Import imp = (Import) obj;
				
				// Need to load in this XSD - first see if we've got a resource resolver that can do it for us - if we do, then we don't even care if the schemaLocation attr is present/incorrect
				SchemaEx thisXsd = null;
				if (resourceResolver != null)
				{
					final LSInput xsdInput = resourceResolver.resolveResource (XsdConstants.XML_SCHEMA_XSD_NAMESPACE_URI, imp.getNamespace (), null, null, null);
					if (xsdInput != null)
					{
						log.info ("XSD for namespace URI " + imp.getNamespace () + " found XSD via resource resolver");
						try (final InputStream xsdStream = xsdInput.getByteStream ())
						{
							thisXsd = (SchemaEx) getXsdUnmarshaller ().unmarshal (xsdStream);
						}
					}
				}

				if (thisXsd == null)
				{
					// No resource resolver, or the resource resolver didn't recognize it - so see if the import specifies a schemaLocation, if it doesn't then we're stuffed
					if (imp.getSchemaLocation () == null)
						throw new IOException ("Can't find XSD corresponding to import statement for namespace URI " + imp.getNamespace ());
					
					// Detemine the filename to load
					String thisXsdFilename = new File (mainXsdFilename).getParent () + imp.getSchemaLocation ();
					thisXsdFilename = FileNameUtils.resolvePathUps (thisXsdFilename, fileSeparator);

					// Now load it
					if (loadXsdFromClasspath)
					{
						log.info ("XSD for namespace URI " + imp.getNamespace () + " being loading from classpath " + thisXsdFilename);
						try (final InputStream xsdStream = getClass ().getResourceAsStream (thisXsdFilename))
						{
							thisXsd = (SchemaEx) getXsdUnmarshaller ().unmarshal (xsdStream);
						}
					}
					else
					{
						log.info ("XSD for namespace URI " + imp.getNamespace () + " being loading from filename " + thisXsdFilename);
						try (final InputStream xsdStream = new FileInputStream (thisXsdFilename))
						{
							thisXsd = (SchemaEx) getXsdUnmarshaller ().unmarshal (xsdStream);
						}
					}
				}

				// Add it
				xmlDocuments.add (new XmlDocument (thisXsd));
		}
	}

	/**
	 * Add labels and text fields for all the XML files this XSD refers to.
	 *	XSD will already have been loaded by this point.
	 *
	 * @throws XmlEditorException If the complex type or underlying sequence is not found
	 */
	private final void createXmlLabelsAndTextFields () throws XmlEditorException
	{
		// Create labels and text fields to pick the each of the XML files
		log.info ("Determining which XSDs need to have a corresponding XML file...");

		int layouty = 3;
		for (int xsdIndex = 0; xsdIndex < xmlDocuments.size (); xsdIndex++)
		{
			final XmlDocument xmlDocument = xmlDocuments.get (xsdIndex);

			// Determine if we need an XML document for this XSD
			// e.g. for MoM, the server XSD has an import to the common XSD but there is no actual common XML filename
			// but the language XSD has an import to the server XSD and requires the server XML to be loaded
			if (xsdIndex == 0)
			{
				log.info ("Namespace URI " + xmlDocument.getXsd ().getTargetNamespace () + " needs an XML file because it is the target namespace");
				xmlDocument.setXmlFileRequiredForThisXsd (true);
			}
			else
			{
				// Search under the main XSD sequence for an element ref to this XSD
				final TopLevelElement otherTopLevelElement = xmlDocument.getXsd ().getTopLevelElement ();
				final QName otherTopLevelElementType = (otherTopLevelElement != null) ?
					new QName (xmlDocument.getXsd ().getTargetNamespace (), otherTopLevelElement.getName ()) : null;
				
				if (xmlDocuments.get (0).getXsd ().getTopLevelTypeDefinition ().includesElementRefPlaceholder (otherTopLevelElementType))
				{
					xmlDocument.setXmlFileRequiredForThisXsd (true);
					log.info ("Namespace URI " + xmlDocument.getXsd ().getTargetNamespace () + " has an element ref import placeholder so needs an XML file");
				}
				else
					log.info ("Namespace URI " + xmlDocument.getXsd ().getTargetNamespace () + " doesn't have an element ref import placeholder so doesn't need an XML file");
			}

			if (xmlDocument.isXmlFileRequiredForThisXsd ())
			{
				final XmlDatabaseInfo dbInfo = xmlDocuments.get (0).getXsd ().getDatabaseInfo (xmlDocument.getXsd ().getTargetNamespace ());

				// Work out the text for the label
				// By default, use the namespace URI but can override this in the 'appinfo' section
				final String databaseTitle = (dbInfo != null) ? dbInfo.getTitle () : null;
				final String databaseName = (databaseTitle != null) ? databaseTitle : xmlDocument.getXsd ().getTargetNamespace ();
				
				// This may also suggest a path where the XML file is located
				String databaseXmlFilename = (dbInfo != null) ? dbInfo.getFilename () : null;
				if (databaseXmlFilename != null)
				{
					// Is location relative to the location of the XSD?
					if (dbInfo.getFilenameType () == XmlFilenameType.RELATIVE)
					{
						final File xmlFilename = new File (new File ("."), databaseXmlFilename);	// Extra 'up a directory' strips off the name of the XSD file
						try
						{
							databaseXmlFilename = xmlFilename.getCanonicalPath ();
						}
						catch (final IOException e)
						{
							// If an exception trying to resolve the relative pathname, just use it as-is
						}
					}

					// Does location include user's My Documents folder?
					if (databaseXmlFilename.startsWith (XmlEditorAppInfo.DATABASE_XML_FILENAME_MY_DOCUMENTS_PREFIX))
						databaseXmlFilename = FileSystemView.getFileSystemView ().getDefaultDirectory ().toString () +
							databaseXmlFilename.substring (XmlEditorAppInfo.DATABASE_XML_FILENAME_MY_DOCUMENTS_PREFIX.length ());
				}

				// Create label
				final JLabel xmlLabel = new JLabel ();
				xmlLabel.setText (databaseName + ":");
				contentPane.add (xmlLabel, getUtils ().createConstraintsNoFill (0, layouty, 1, 1, INSET, GridBagConstraintsNoFill.WEST));

				// Create text field
				final FilenameTextField xmlEdit = new FilenameTextField ();
				xmlEdit.addChoosableFileFilter (new ExtensionFileFilter ("xml", "XML files"));
				contentPane.add (xmlEdit, getUtils ().createConstraintsHorizontalFill (1, layouty, 1, 1, INSET, GridBagConstraintsHorizontalFill.CENTRE));
				layouty++;

				// Keep a link to this text field so when any of the text fields change value, we can check them all
				xmlDocument.setFileNameTextField (xmlEdit);

				// Only allow clicking Load XML Files if all filenames have been entered
				xmlEdit.getDocument ().addDocumentListener (xmlEditDocumentListener);

				// Set the text into the text field absolutely last thing, so the DocumentListener will correctly enable or disable the Load XML file(s) button
				xmlEdit.setText (databaseXmlFilename);
			}
		}

		// Button to load XML files
		final JButton loadXmlButton = new JButton ();
		loadXmlButton.setAction (loadXmlAction);
		contentPane.add (loadXmlButton, getUtils ().createConstraintsNoFill (1, layouty, 1, 1, INSET, GridBagConstraintsNoFill.EAST));

		// Force the layout manager to position and size the new controls
		contentPane.revalidate ();
		contentPane.repaint ();
		frame.pack ();
	}

	/**
	 * @return Newly created document with correct namespaces to match
	 * @throws XmlEditorException If there is a problem parsing the XSD to determine the namespace(s) to give the new XML file
	 */
	private final Document createNewXmlDocument ()
		throws XmlEditorException
	{
		// Find the top level xsd:element definition
		final TopLevelElement topLevelElement = xmlDocuments.get (0).getXsd ().getTopLevelElement ();
		
		// Whether or not we know a sensible namespace prefix to use depends on whether the top level element refers to a separate complex type or has one inlined
		final String namespacePrefix = (topLevelElement.getType () != null) ? topLevelElement.getType ().getPrefix () : "ns";

		// Create top level element
		final Element rootElement = new Element (topLevelElement.getName (), namespacePrefix, xmlDocuments.get (0).getXsd ().getTargetNamespace ());

		// Add "schema instance" namespace
		rootElement.addNamespaceDeclaration (XsdConstants.INSTANCE_NAMESPACE);

		return new Document (rootElement);
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
	 * @return JDOM XML parser
	 */
	public final SAXBuilder getParser ()
	{
		return parser;
	}

	/**
	 * @param par JDOM XML parser
	 */
	public final void setParser (final SAXBuilder par)
	{
		parser = par;
	}

	/**
	 * @return XSD factory
	 */
	public final SchemaFactory getSchemaFactory ()
	{
		return schemaFactory;
	}

	/**
	 * @param sf XSD factory
	 */
	public final void setSchemaFactory (final SchemaFactory sf)
	{
		schemaFactory = sf;
	}

	/**
	 * @return Optional resource resolver which can be used to locate referenced XSDs
	 */
	public final LSResourceResolver getResourceResolver ()
	{
		return resourceResolver;
	}

	/**
	 * @param rr Optional resource resolver which can be used to locate referenced XSDs
	 */
	public final void setResourceResolver (final LSResourceResolver rr)
	{
		resourceResolver = rr;
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
	 * @return MDI to kick off after completing XSD+XML selection
	 */
	public final XmlEditorMain getMdiEditor ()
	{
		return mdiEditor;
	}

	/**
	 * @param obj MDI to kick off after completing XSD+XML selection
	 */
	public final void setMdiEditor (final XmlEditorMain obj)
	{
		mdiEditor = obj;
	}

	/**
	 * @return Unmarshaller for reading XSD files
	 */
	public final Unmarshaller getXsdUnmarshaller ()
	{
		return xsdUnmarshaller;
	}

	/**
	 * @param um Unmarshaller for reading XSD files
	 */
	public final void setXsdUnmarshaller (final Unmarshaller um)
	{
		xsdUnmarshaller = um;
	}
}