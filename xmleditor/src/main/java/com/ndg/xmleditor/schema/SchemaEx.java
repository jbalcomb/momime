package com.ndg.xmleditor.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3._2001.xmlschema.Annotation;
import org.w3._2001.xmlschema.Appinfo;
import org.w3._2001.xmlschema.OpenAttrs;
import org.w3._2001.xmlschema.Schema;
import org.w3._2001.xmlschema.TopLevelElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ndg.xmleditor.constants.XmlEditorAppInfo;
import com.ndg.xmleditor.doc.ListOfXmlDocuments;
import com.ndg.xmleditor.editor.XmlEditorException;

/**
 * Extends JAXB class to provide normal fields to store the values of all nxe: namespaced attributes
 */
public final class SchemaEx extends Schema
{
	/**
	 * Finds the definition for a named complex type
	 * 
	 * @param complexTypeName The type name excluding the prefix
	 * @return The Element representing the definition of this complex type
	 * @throws XmlEditorException If the complex type is not found
	 */
	public final TopLevelComplexTypeEx findTopLevelComplexType (final String complexTypeName) throws XmlEditorException
	{
		// Find the type definition
		TopLevelComplexTypeEx typeDefinition = null;
		final Iterator<OpenAttrs> iter = getSimpleTypeOrComplexTypeOrGroup ().iterator ();
		while ((typeDefinition == null) && (iter.hasNext ()))
		{
			final OpenAttrs obj = iter.next ();
			if (obj instanceof TopLevelComplexTypeEx)
			{
				final TopLevelComplexTypeEx thisComplexType = (TopLevelComplexTypeEx) obj;
				if (thisComplexType.getName ().equals (complexTypeName))
					typeDefinition = thisComplexType;
			}
		}

		if (typeDefinition == null)
			throw new XmlEditorException ("Cannot find definition in XSD with namespace URI \"" + getTargetNamespace () + "\" for complex type \"" + complexTypeName + "\"");

		return typeDefinition;
	}	
	
	/**
	 * This is a bit of a hack to solve the experienceLevel link from the MoM IME language XSD into the server XSD - it gets confused because
	 * the experienceLevel data is in the server XML, and therefore expects the definition for the experienceLevel complexType to be
	 * in the server XSD, but its in the common XSD due to inheritance.
	 * For a fairly obscure scenario like this, just felt it would be easier to do a search for it rather than trying to walk down the inheritance path and do it "properly".
	 * 
	 * @param complexTypeName The type name excluding the prefix
	 * @param xmlDocuments Other XSDs to try looking for the type under if we fail to find it here
	 * @return The Element representing the definition of this complex type
	 * @throws XmlEditorException If the complex type is not found
	 */
	public final TopLevelComplexTypeEx findTopLevelComplexTypeAndTryOtherSchemas (final String complexTypeName, ListOfXmlDocuments xmlDocuments) throws XmlEditorException
	{
		TopLevelComplexTypeEx referencedComplexType = null;
		try
		{
			// First search for the complex type where we expect it to be
			referencedComplexType = findTopLevelComplexType (complexTypeName);
		}
		catch (final XmlEditorException e)
		{
			// That didn't work, so try searching for it under the other XSDs.
			// Also, search backwards... don't check the language XSD first or we'll just find the experienceLevel complex type in there which isn't what we want.
			int index = xmlDocuments.size () - 1;
			while ((referencedComplexType == null) && (index >= 0))
			{
				try
				{
					referencedComplexType = xmlDocuments.get (index).getXsd ().findTopLevelComplexType (complexTypeName);
				}
				catch (final XmlEditorException e2)
				{
				}
					
				index--;
			}
			
			// If we still never managed to find one, then rethrow the original exception
			if (referencedComplexType == null)
				throw e;
		}
		
		return referencedComplexType;
	}	
	
	/**
	 * Finds the definition for a named simple type
	 * 
	 * @param simpleTypeName The type name excluding the prefix
	 * @return The Element representing the definition of this simple type
	 * @throws XmlEditorException If the simple type is not found
	 */
	public final SimpleTypeEx findTopLevelSimpleType (final String simpleTypeName) throws XmlEditorException
	{
		// Find the type definition
		SimpleTypeEx typeDefinition = null;
		final Iterator<OpenAttrs> iter = getSimpleTypeOrComplexTypeOrGroup ().iterator ();
		while ((typeDefinition == null) && (iter.hasNext ()))
		{
			final OpenAttrs obj = iter.next ();
			if (obj instanceof SimpleTypeEx)
			{
				final SimpleTypeEx thisSimpleType = (SimpleTypeEx) obj;
				if (thisSimpleType.getName ().equals (simpleTypeName))
					typeDefinition = thisSimpleType;
			}
		}

		if (typeDefinition == null)
			throw new XmlEditorException ("Cannot find definition in XSD with namespace URI \"" + getTargetNamespace () + "\" for simple type \"" + simpleTypeName + "\"");

		return typeDefinition;
	}	
	
	/**
	 * @return The top level xsd:element definition from the XSD
	 */
	public final TopLevelElementEx getTopLevelElement ()
	{
		// Find the top level xsd:element definition
		final List<TopLevelElementEx> topLevelElements = new ArrayList<TopLevelElementEx> ();
		for (final OpenAttrs obj : getSimpleTypeOrComplexTypeOrGroup ())
			if (obj instanceof TopLevelElementEx)
				topLevelElements.add ((TopLevelElementEx) obj);
		
		// An XSD can define more than one top level element - but that's not valid for the editor since then it wouldn't know which one it should be dealing with
		// Having 0 top level elements is fine for referenced XSDs, e.g. the Map XSD, just not valid for the main XSD
		return (topLevelElements.size () == 1) ? topLevelElements.get (0) : null;
	}
	
	/**
	 * @return The top level xsd:complexType definition from the XSD
	 * @throws XmlEditorException If the complex type is not found
	 */
	public final ComplexTypeEx getTopLevelTypeDefinition () throws XmlEditorException
	{
		final TopLevelElement topLevelElement = getTopLevelElement ();
		ComplexTypeEx topLevelTypeDefinition = null;
		
		if (topLevelElement != null)
		{
			final QName topLevelType = topLevelElement.getType ();
			if (topLevelType == null)
			{
				// Top level complex type is inlined under the element
				if (topLevelElement.getComplexType () == null)
					throw new XmlEditorException ("Top level element node in XSD with namespace URI \"" + getTargetNamespace () +
						"\" does not refer to a complex type, so expect to be inlined, but didn't find that either");

				topLevelTypeDefinition = (LocalComplexTypeEx) topLevelElement.getComplexType ();
			}
			else
			{
				// Top level complex type is defined separately and referenced
				topLevelTypeDefinition = findTopLevelComplexType (topLevelType.getLocalPart ());
			}
		}
		
		return topLevelTypeDefinition;
	}
	
	/**
	 * @return Annotation node if present, otherwise null
	 */
	final Annotation getAnnotation ()
	{
		Annotation anno = null;
		final Iterator<OpenAttrs> iter = getIncludeOrImportOrRedefine ().iterator ();
		while ((anno == null) && (iter.hasNext ()))
		{
			final OpenAttrs obj = iter.next ();
			if (obj instanceof Annotation)
				anno = (Annotation) obj;
		}
		
		return anno;
	}
	
	/**
	 * @return Annotation > AppInfo structure if present, otherwise null
	 */
	final Appinfo getAppInfo ()
	{
		final Annotation anno = getAnnotation ();
		Appinfo appInfo = null;
		
		if (anno != null)
		{
			final Iterator<Object> iter = anno.getAppinfoOrDocumentation ().iterator ();
			while ((appInfo == null) && (iter.hasNext ()))
			{
				final Object obj = iter.next ();
				if (obj instanceof Appinfo)
					appInfo = (Appinfo) obj;
			}
		}
		
		return appInfo;
	}
	
	/**
	 * @return Value of the nxe:backuponopen extension
	 */
	public final boolean isBackupOnOpen ()
	{
		final Appinfo appInfo = getAppInfo ();
		boolean value = false;

		if (appInfo != null)
		{
			Element backupOnOpenElement = null;
			final Iterator<Object> iter = appInfo.getContent ().iterator ();
			while ((backupOnOpenElement == null) && (iter.hasNext ()))
			{
				final Object obj = iter.next ();
				if (obj instanceof Element)
				{
					final Element elem = (Element) obj;
					if ((XmlEditorAppInfo.NAMESPACE_URI.equals (elem.getNamespaceURI ())) &&
						(XmlEditorAppInfo.TAG_VALUE_BACKUP_DATABASE_ON_OPEN.equals (elem.getLocalName ())))
						
						backupOnOpenElement = elem;
				}
			}
			
			if (backupOnOpenElement != null)
				value = XmlEditorAppInfo.VALUE_BACKUP_DATABASE_ON_OPEN_YES.equals (backupOnOpenElement.getTextContent ());
		}
		
		return value;
	}

	/**
	 * Reads the info held about each XML database under the top level <xsd:appinfo>
	 * 
	 * @param namespaceURI NamespaceURI of the XML file we want info on
	 * @return Data structure with info about the requested XML file; or null if no info was specified for it
	 */
	public final XmlDatabaseInfo getDatabaseInfo (final String namespaceURI)
	{
		final Appinfo appInfo = getAppInfo ();
		XmlDatabaseInfo dbInfo = null;
		
		if (appInfo != null)
		{
			Element dbElement = null;
			final Iterator<Object> iter = appInfo.getContent ().iterator ();
			while ((dbElement == null) && (iter.hasNext ()))
			{
				final Object obj = iter.next ();
				if (obj instanceof Element)
				{
					final Element elem = (Element) obj;
					if ((XmlEditorAppInfo.NAMESPACE_URI.equals (elem.getNamespaceURI ())) &&
						(XmlEditorAppInfo.TAG_ENTITY_DATABASE.equals (elem.getLocalName ())) &&
						(namespaceURI.equals (elem.getAttribute (XmlEditorAppInfo.TAG_ATTRIBUTE_DATABASE_NAMESPACE_URI))))
						
						dbElement = elem;
				}
			}
			
			// Found info for this DB?
			if (dbElement != null)
			{
				dbInfo = new XmlDatabaseInfo ();
				
				final NodeList childNodes = dbElement.getChildNodes ();
				for (int nodeNo = 0; nodeNo < childNodes.getLength (); nodeNo++)
				{
					final Node node = childNodes.item (nodeNo);
					if (node instanceof Element)
					{
						final Element elem = (Element) node;
						if (XmlEditorAppInfo.NAMESPACE_URI.equals (elem.getNamespaceURI ()))
						{
							// Check which element it is
							if (XmlEditorAppInfo.TAG_VALUE_DATABASE_TITLE.equals (elem.getLocalName ()))
								dbInfo.setTitle (elem.getTextContent ());

							else if (XmlEditorAppInfo.TAG_VALUE_DATABASE_XML_FILENAME.equals (elem.getLocalName ()))
							{
								dbInfo.setFilename (elem.getTextContent ());
								final String filenameType = elem.getAttribute (XmlEditorAppInfo.TAG_ATTRIBUTE_DATABASE_XML_FILENAME_TYPE);
								if ((filenameType != null) && (!filenameType.equals ("")))
									dbInfo.setFilenameType (XmlFilenameType.valueOf (filenameType.toUpperCase ()));
							}
						}
					}
				}
				
				// Always set the filename type to something valid
				if (dbInfo.getFilenameType () == null)
					dbInfo.setFilenameType (XmlFilenameType.ABSOLUTE);
			}
		}
		
		return dbInfo;
	}
}