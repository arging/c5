package c5.enums;

/**
 * Attribute type for attributes defined in ".names" file.
 * 
 * @author li
 * @since 2014
 */
public enum AttributeType {

	CONTINUOUS, IGNORE, LABEL, DISCRETE;

	/**
	 * Parse string to AttributeType enum ignoreCase.
	 * 
	 * @param s
	 *            the attribute type flag.
	 * @return The corresponding enum. Return null if not found.
	 */
	public static AttributeType parse(String s) {
		for (AttributeType attrType : AttributeType.values()) {
			if (attrType.name().equalsIgnoreCase(s)) {
				return attrType;
			}
		}
		return null;
	}
}
