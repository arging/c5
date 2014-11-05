package c5.enums;

import c5.consts.C5Consts;

/**
 * Input variable type.
 * 
 * @author li
 * @since 2014
 */
public enum CaseType {

	UNKNOWN, NAN, NORMAL;

	/**
	 * Parse string to CaseType enum.
	 * 
	 * @param s
	 *            the input variable.
	 * @return The corresponding enum. Return null if not found.
	 */
	public static CaseType parse(String s) {
		if (C5Consts.UNKNOWN.equals(s)) {
			return UNKNOWN;
		}
		// "NaN" for Java Double.NaN
		if (C5Consts.NOT_NUMBER.equals(s) || "NaN".equals(s)) {
			return NAN;
		}
		return NORMAL;
	}
}
