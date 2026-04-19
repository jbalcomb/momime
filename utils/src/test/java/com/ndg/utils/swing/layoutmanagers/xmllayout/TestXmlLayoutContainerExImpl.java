package com.ndg.utils.swing.layoutmanagers.xmllayout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests the XmlLayoutContainerExImpl class
 */
@ExtendWith(MockitoExtension.class)
public final class TestXmlLayoutContainerExImpl
{
	/**
	 * Tests the findComponent method
	 */
	@Test
	public final void testFindComponent ()
	{
		// Set up a dummy layout
		final XmlLayoutContainerExImpl layout = new XmlLayoutContainerExImpl ();
		for (int n = 1; n <= 3; n++)
		{
			final XmlLayoutComponent comp = new XmlLayoutComponent ();
			comp.setName ("Comp" + n);
			comp.setLeft (n);
			layout.getComponent ().add (comp);
		}
		
		layout.buildMaps ();
		
		// Run tests
		assertEquals (2, layout.findComponent ("Comp2").getLeft ());
		assertNull (layout.findComponent ("Comp4"));
	}
	
	/**
	 * Tests the findComponentAt method
	 */
	@Test
	public final void testFindComponentAt ()
	{
		// Set up a dummy layout
		final XmlLayoutContainerExImpl layout = new XmlLayoutContainerExImpl ();

		final XmlLayoutComponent comp1 = new XmlLayoutComponent ();
		comp1.setName ("Comp1");
		comp1.setLeft (10);
		comp1.setTop (5);
		layout.getComponent ().add (comp1);

		final XmlLayoutComponent comp2 = new XmlLayoutComponent ();
		comp2.setName ("Comp2");
		comp2.setLeft (15);
		comp2.setTop (5);
		comp2.setWidth (10);
		comp2.setHeight (5);
		layout.getComponent ().add (comp2);
		
		// Run tests
		assertSame (comp2, layout.findComponentAt (15, 5));
		assertSame (comp2, layout.findComponentAt (20, 8));
		assertSame (comp2, layout.findComponentAt (24, 9));
		assertNull (layout.findComponentAt (25, 9));
		assertNull (layout.findComponentAt (10, 5));
	}
}