package c5.enums;

/**
 * Tree node for tree structure.
 * 
 * @author li
 * @since 2014
 */
public enum NodeType {

	LEAF, DISCRETE, CONTINUES, CLUSTERED;

	/**
	 * Parse string to NodeType enum.
	 * 
	 * @param s
	 *            the node type flag.
	 * @return The corresponding enum. Return null if not found.
	 */
	public static NodeType parse(String s) {
		for (NodeType nodeType : NodeType.values()) {
			if (String.valueOf(nodeType.ordinal()).equals(s)) {
				return nodeType;
			}
		}
		return null;
	}
}
