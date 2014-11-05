package c5.enums;

/**
 * Identifier types for ".tree" files.
 * 
 * @author li
 * @since 2014
 */
public enum IdentifierType {

	TYPE, CLASS, FREQ, ATT, FORKS, CUT, ELTS, LOW, MID, HIGH;

	/**
	 * Parse string to IdentifierType enum ignoreCase.
	 * 
	 * @param s
	 *            the identifier type flag.
	 * @return The corresponding enum. Return null if not found.
	 */
	public static IdentifierType parse(String s) {
		for (IdentifierType identifierType : IdentifierType.values()) {
			if (identifierType.name().equalsIgnoreCase(s)) {
				return identifierType;
			}
		}
		return null;
	}
}
