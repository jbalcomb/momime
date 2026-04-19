package com.ndg.utils.swing.actions;

import java.awt.event.ActionEvent;

/**
 * Like the ActionListener interface, except that it allows checked exceptions to be thrown.
 */
@FunctionalInterface
public interface CheckedActionListener
{
	/**
	 * @param ev Swing event that triggered this action
	 * @throws Exception If there is a problem
	 */
	public void actionPerformed (final ActionEvent ev) throws Exception;
}