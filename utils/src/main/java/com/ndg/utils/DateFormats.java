package com.ndg.utils;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Standard date formats used by XML editor and other apps.
 */
public final class DateFormats
{
	/** Date format expected in XML files using xsd:date data type */
	public final static DateTimeFormatter XML_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

	/** Standard date format like 11 JUN 2007 */
	public final static String STANDARD_DATE_FORMAT_STRING = "dd MMM yyyy";

	/** Standard date format like 11 JUN 2007.
	 * This is forced to UK locale to stop dots coming out in (and being expected in parsing) months.
	 * This started being a problem at some point between JDK 11.0.12 and 11.0.16, see
	 * https://bugs.openjdk.org/browse/JDK-8211321?focusedCommentId=14213153&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-14213153
	 */
	public final static DateTimeFormatter STANDARD_DATE_FORMAT = DateTimeFormatter.ofPattern (STANDARD_DATE_FORMAT_STRING, Locale.UK);

	/** Full date and time format showing every value.  Dots in months aren't such an issue here as this is an output-only format - nobody is typing in dates in this format. */
	private final static String FULL_DATE_TIME_FORMAT_STRING = "dd MMM yyyy, HH:mm:ss z";

	/** Full date and time format showing every value */
	public final static DateTimeFormatter FULL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern (FULL_DATE_TIME_FORMAT_STRING);

	/** No dashes date format */
	private final static String NO_DASHES_DATE_FORMAT_STRING = "yyyyMMdd";

	/** No dashes date format */
	public final static DateTimeFormatter NO_DASHES_DATE_FORMAT = DateTimeFormatter.ofPattern (NO_DASHES_DATE_FORMAT_STRING);

	/** Standard time format like 23:47:10 */
	private final static String STANDARD_TIME_FORMAT_STRING = "HH:mm:ss";

	/** Standard time format like 23:47:10 */
	public final static DateTimeFormatter STANDARD_TIME_FORMAT = DateTimeFormatter.ofPattern (STANDARD_TIME_FORMAT_STRING);

	/** No dashes time format */
	private final static String NO_DASHES_TIME_FORMAT_STRING = "HHmmss";

	/** No dashes time format */
	public final static DateTimeFormatter NO_DASHES_TIME_FORMAT = DateTimeFormatter.ofPattern (NO_DASHES_TIME_FORMAT_STRING);

	/**
	 * Prevent instatiation of this class
	 */
	private DateFormats ()
	{
	}
}
