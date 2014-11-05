package c5.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import c5.consts.C5Consts;
import c5.enums.AttributeType;
import c5.model.Attribute;
import c5.model.Names;

/**
 * Parser for parsing ".names" file.
 * 
 * @author li
 * @since 2014
 */
public class NamesParser {

	/** label for comment */
	private static final String NOTE_LABEL = "|";

	/** label for target classes */
	private static final String COLON = ":";

	/** label indicates definition ending. */
	private static final String DOT = ".";

	/** records the ".names" file line number */
	private int lineNum;

	/** names file path */
	private final String namesFile;

	/** the analyzing result */
	private final Names names = new Names();

	public NamesParser(String namesFile) {
		this.namesFile = namesFile;
	}

	/**
	 * Parse ".names" file to {@link Names} instance.
	 * 
	 * @return The Names instance.
	 * @throws IOException
	 */
	public Names parse() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(namesFile));
		String str = "";
		try {
			while ((str = reader.readLine()) != null) {
				lineNum++;
				if (isIngore(str)) {
					continue;
				}

				if (isTarget(str)) {
					parseTarget(str);
				} else {
					parseAttribute(str);
				}
			}
		} finally {
			reader.close();
		}

		return names;
	}

	private boolean isIngore(String str) {
		return StringUtils.isBlank(str) || str.trim().startsWith(NOTE_LABEL);
	}

	private boolean isTarget(String str) {
		str = processNote(str);
		return StringUtils.contains(str, DOT)
				&& StringUtils.containsNone(str, COLON);
	}

	private String processNote(String str) {
		int index = str.indexOf(NOTE_LABEL);
		String tmp = index > 0 ? str.substring(0, index) : str;
		return StringUtils.trim(tmp);
	}

	private void parseTarget(String str) {
		str = processNote(str);

		int dotIndex = StringUtils.indexOf(str, DOT);
		checkAndThrow(dotIndex > 0);
		str = StringUtils.substring(str, 0, dotIndex);

		String[] targetClassifies = StringUtils.split(str, C5Consts.COMMA);
		for (String classify : targetClassifies) {
			if (StringUtils.isNotBlank(classify)) {
				names.addTargetClassify(classify.trim());
			}
		}
	}

	private void parseAttribute(String str) {
		str = processNote(str);

		int colonIndex = str.indexOf(COLON);
		int dotIndex = str.indexOf(DOT);
		dotIndex = dotIndex < 0 ? str.length() : dotIndex;

		checkAndThrow(dotIndex > 0 && colonIndex > 0 && dotIndex > colonIndex);

		String attName = StringUtils.trim(str.substring(0, colonIndex));
		String attValue = StringUtils.trim(str.substring(colonIndex + 1,
				dotIndex));

		checkAndThrow(StringUtils.isNotBlank(attName));
		checkAndThrow(StringUtils.isNotBlank(attValue));

		AttributeType attrType = AttributeType.parse(attValue);
		if (attrType == null) {
			attrType = AttributeType.DISCRETE;
		}
		Attribute attribute = new Attribute(attName, attrType);
		parseAttributeValue(attValue, attrType, attribute);

		names.addAttribute(attribute);
	}

	private void parseAttributeValue(String attValue, AttributeType attrType,
			Attribute attribute) {

		if (attrType == AttributeType.DISCRETE) {

			attribute.addDiscrete(C5Consts.NOT_NUMBER);
			if (!StringUtils.startsWithIgnoreCase(attValue,
					AttributeType.DISCRETE.name())) {
				String[] discreteValues = StringUtils.split(attValue,
						C5Consts.COMMA);
				for (String discreteValue : discreteValues) {
					if (StringUtils.isNotBlank(discreteValue)) {
						attribute.addDiscrete(StringUtils.trim(discreteValue));
					}
				}
			}
		}

		if (attrType == AttributeType.LABEL) {
			names.setLableAttIndex(names.getAttrCount());
		}
	}

	private void checkAndThrow(boolean isLegal) {
		if (!isLegal) {
			throw new IllegalArgumentException(
					"Illegal Attribute Definition At Line:" + lineNum
							+ " , File: " + namesFile + ".");
		}
	}
}
