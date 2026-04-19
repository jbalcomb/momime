package com.ndg.utils.swing.layoutmanagers.xmllayout;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ndg.utils.XmlConstants;
import com.ndg.utils.swing.NdgUIUtils;
import com.ndg.utils.swing.NdgUIUtilsImpl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

/**
 * Tests the XmlLayoutManager class
 */
@ExtendWith(MockitoExtension.class)
public final class TestXmlLayoutManager
{
	/**
	 * Tests an example frame laid out by XmlLayoutManager
	 * @throws Exception If there is a problem
	 */
	@Test
	public final void testXmlLayoutManager () throws Exception
	{
		// XSD
		final URL xsdResource = getClass ().getResource (XmlLayoutConstants.XML_LAYOUT_XSD_LOCATION);
		assertNotNull (xsdResource, "XML layout XSD could not be found on classpath");

		final SchemaFactory schemaFactory = SchemaFactory.newInstance (XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema = schemaFactory.newSchema (xsdResource);

		final Unmarshaller unmarshaller = JAXBContext.newInstance (XmlLayoutContainer.class).createUnmarshaller ();		
		unmarshaller.setProperty (XmlConstants.OBJECT_FACTORY, new Object [] {new XmlLayoutObjectFactory ()});
		unmarshaller.setSchema (schema);
		
		// XML
		final URL xmlResource = getClass ().getResource ("/TestXmlLayoutManager.xml");
		assertNotNull (xmlResource, "Sample XML layout could not be found on classpath");

		final XmlLayoutContainerEx layout = (XmlLayoutContainerEx) unmarshaller.unmarshal (xmlResource);
		layout.buildMaps ();
		
		// Set look and feel
		final NdgUIUtils utils = new NdgUIUtilsImpl ();
		utils.useNimbusLookAndFeel ();
		
		// Set up content pane
		final JPanel panel = new JPanel ();
		panel.setLayout (new XmlLayoutManager (layout));
		
		panel.add (new JButton ("One"), "ButtonOne");
		panel.add (new JButton ("Two"), "ButtonTwo");
		panel.add (new JButton ("Three"), "ButtonThree");
		panel.add (new JButton ("Four"), "ButtonFour");

		// Set up a dummy frame to display the panel
		final JFrame frame = new JFrame ("testXmlLayoutManager");
		frame.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);
		frame.setContentPane (panel);
		frame.pack ();
		frame.setResizable (false);
		frame.setLocationRelativeTo (null);
		frame.setVisible (true);
		Thread.sleep (5000);
	}
}