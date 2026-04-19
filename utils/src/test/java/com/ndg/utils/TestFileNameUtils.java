package com.ndg.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test script for FileNameUtils class
 */
@ExtendWith(MockitoExtension.class)
public final class TestFileNameUtils
{
	/**
	 * Tests the extractFileExt method
	 */
	@Test
	public final void testExtractFileExt ()
	{
		assertEquals (FileNameUtils.extractFileExt ("Simple file.txt"), ".txt", "Pathless filename with simple extension");
		assertEquals (FileNameUtils.extractFileExt ("Simple file"), "", "Pathless filename with no extension");
		assertEquals (FileNameUtils.extractFileExt ("Simple.file.txt"), ".txt", "Pathless filename with double extension");

		assertEquals (FileNameUtils.extractFileExt
			("C:" + File.separator + "Some path" + File.separator + "Simple file.txt"), ".txt", "Simple path filename with simple extension");
		assertEquals (FileNameUtils.extractFileExt
			("C:" + File.separator + "Some path" + File.separator + "Simple file"), "", "Simple path filename with no extension");
		assertEquals (FileNameUtils.extractFileExt
			("C:" + File.separator + "Some path" + File.separator + "Simple.file.txt"), ".txt", "Simple path filename with double extension");

		assertEquals (FileNameUtils.extractFileExt
			("C:" + File.separator + "Some.path" + File.separator + "Simple file.txt"), ".txt", "Path with extension, filename with simple extension");
		assertEquals (FileNameUtils.extractFileExt
			("C:" + File.separator + "Some.path" + File.separator + "Simple file"), "", "Path with extension, filename with no extension");
		assertEquals (FileNameUtils.extractFileExt
			("C:" + File.separator + "Some.path" + File.separator + "Simple.file.txt"), ".txt", "Path with extension, filename with double extension");
	}

	/**
	 * Tests the changeFileExt method
	 */
	@Test
	public final void testChangeFileExt ()
	{
		assertEquals (FileNameUtils.changeFileExt ("Simple file.txt", ".doc"), "Simple file.doc", "Pathless filename with simple extension");
		assertEquals (FileNameUtils.changeFileExt ("Simple file", ".doc"), "Simple file.doc", "Pathless filename with no extension");
		assertEquals (FileNameUtils.changeFileExt ("Simple.file.txt", ".doc"), "Simple.file.doc", "Pathless filename with double extension");
		assertEquals (FileNameUtils.changeFileExt ("Simple.file.txt", ""), "Simple.file", "Pathless filename - remove double extension");

		assertEquals (FileNameUtils.changeFileExt
			("C:" + File.separator + "Some path" + File.separator + "Simple file.txt", ".doc"), "C:" + File.separator + "Some path" + File.separator + "Simple file.doc",
				"Simple path filename with simple extension");
		assertEquals (FileNameUtils.changeFileExt
			("C:" + File.separator + "Some path" + File.separator + "Simple file", ".doc"), "C:" + File.separator + "Some path" + File.separator + "Simple file.doc",
				"Simple path filename with no extension");
		assertEquals (FileNameUtils.changeFileExt
			("C:" + File.separator + "Some path" + File.separator + "Simple.file.txt", ".doc"), "C:" + File.separator + "Some path" + File.separator + "Simple.file.doc",
				"Simple path filename with double extension");
		assertEquals (FileNameUtils.changeFileExt
			("C:" + File.separator + "Some path" + File.separator + "Simple.file.txt", ""), "C:" + File.separator + "Some path" + File.separator + "Simple.file",
				"Simple path filename - remove double extension");

		assertEquals (FileNameUtils.changeFileExt
			("C:" + File.separator + "Some.path" + File.separator + "Simple file.txt", ".doc"), "C:" + File.separator + "Some.path" + File.separator + "Simple file.doc",
				"Path with extension, filename with simple extension");
		assertEquals (FileNameUtils.changeFileExt
			("C:" + File.separator + "Some.path" + File.separator + "Simple file", ".doc"), "C:" + File.separator + "Some.path" + File.separator + "Simple file.doc",
				"Path with extension, filename with no extension");
		assertEquals (FileNameUtils.changeFileExt
			("C:" + File.separator + "Some.path" + File.separator + "Simple.file.txt", ".doc"), "C:" + File.separator + "Some.path" + File.separator + "Simple.file.doc",
				"Path with extension, filename with double extension");
		assertEquals (FileNameUtils.changeFileExt
			("C:" + File.separator + "Some.path" + File.separator + "Simple.file.txt", ""), "C:" + File.separator + "Some.path" + File.separator + "Simple.file",
				"Path with extension, remove double extension");
	}

	/**
	 * Tests the resolvePathUps method
	 */
	@Test
	public final void testResolvePathUps ()
	{
		assertEquals (FileNameUtils.resolvePathUps
			("abc" + File.separator + "def" + File.separator + "ghi" + File.separator + "jkl" + File.separator + "mno" + File.separator + ".." + File.separator + ".." + File.separator + "pqr", null),
			"abc" + File.separator + "def" + File.separator + "ghi" + File.separator + "pqr", "Resolve path ups one");
		assertEquals (FileNameUtils.resolvePathUps
			("abc" + File.separator + "def" + File.separator + ".." + File.separator + ".." + File.separator + ".." + File.separator + "pqr", null),
			".." + File.separator + "pqr", "Resolve path ups two");
	}
}