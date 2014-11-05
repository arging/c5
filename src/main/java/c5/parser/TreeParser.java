package c5.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import c5.consts.C5Consts;
import c5.enums.IdentifierType;
import c5.enums.NodeType;
import c5.model.Attribute;
import c5.model.Names;
import c5.model.Node;

/**
 * Parser for parsing ".tree" files.
 * 
 * @author li
 * @since 2014
 */
public class TreeParser {

	private static final String VERSION_IDENTIFIER = "id";

	private static final String ATT = "att=";

	private static final String ELTS = "elts=";

	private static final String ENTRIES = "entries";

	private static final String TYPE = "type";

	private static final String EQUAL_SIGN = "=";

	private static final String QUOTES = "\"";

	private static final String NODE_IDENTIFIER_SPLITTER = "\" ";

	private int lineNum;

	private final String treeFile;

	private Node[] trees;

	private final Names names;

	private BufferedReader reader;

	/**
	 * 
	 * @param treeFile
	 * @param names
	 */
	public TreeParser(String treeFile, Names names) {
		this.treeFile = treeFile;
		this.names = names;
	}

	/**
	 * Parse ".tree" files to Tree structure.
	 * 
	 * @return Multi Trees
	 * @throws IOException
	 */
	public Node[] parse() throws IOException {
		this.reader = new BufferedReader(new FileReader(treeFile));

		try {
			String str = "";
			while ((str = reader.readLine()) != null) {
				lineNum++;
				str = StringUtils.trim(str);

				if (isIngore(str)) {
					continue;
				}

				if (str.startsWith(ATT)) {
					processDiscretes(str);
				}

				if (str.startsWith(ENTRIES)) {
					initialTrees(str);
					break;
				}
			}

			checkAndThrow(trees != null, "File " + treeFile
					+ " doesn't define entries!");

			for (int i = 0; i < trees.length; i++) {
				trees[i] = buildTree();
			}

		} finally {
			reader.close();
		}

		return trees;
	}

	private boolean isIngore(String str) {
		return StringUtils.isBlank(str)
				|| str.trim().startsWith(VERSION_IDENTIFIER);
	}

	private void processDiscretes(String str) {
		int eltsIndex = StringUtils.indexOf(str, ELTS);
		checkAndThrow(eltsIndex > 0);

		String attName = StringUtils.substring(str, ATT.length(), eltsIndex)
				.replaceAll(QUOTES, "").trim();

		Attribute attribute = names.getAttribute(attName);
		checkAndThrow(attribute != null, attName
				+ " can not find in names file. At file " + treeFile + " line "
				+ lineNum);

		String discretesStr = StringUtils.substring(str,
				eltsIndex + ELTS.length());
		checkAndThrow(StringUtils.isNotBlank(discretesStr));

		for (String discrete : StringUtils.split(discretesStr, C5Consts.COMMA)) {
			String standardDiscrete = discrete.replaceAll(QUOTES, "").trim();
			if (StringUtils.isNotBlank(standardDiscrete)) {
				attribute.addDiscrete(standardDiscrete);
			}
		}
	}

	private void initialTrees(String str) {
		int equalSignIndex = str.indexOf(EQUAL_SIGN);
		checkAndThrow(equalSignIndex > 0);

		String treeCountStr = StringUtils.substring(str, equalSignIndex + 2,
				str.length() - 1);
		checkAndThrow(StringUtils.isNotBlank(treeCountStr)
				&& StringUtils.isNumeric(treeCountStr));

		int treeCount = Integer.valueOf(treeCountStr);
		checkAndThrow(treeCount > 0);

		trees = new Node[treeCount];
	}

	private Node buildTree() throws IOException {
		String str;
		while ((str = reader.readLine()) != null) {
			lineNum++;
			if (StringUtils.isBlank(str)) {
				continue;
			}

			Node node = new Node();

			explainTree(str, node);

			if (node.getClassDist().size() > 0) {
				int targetClassIndex = node.getTargetClassIndex();
				node.setErrors(node.getTotalCases()
						- node.getClassDist().get(targetClassIndex));
			}

			if (node.getType() != NodeType.LEAF) {
				for (int i = 1; i <= node.getForks(); i++) {
					node.addBranch(buildTree());
				}
			}

			return node;
		}

		return null;
	}

	private void explainTree(String str, Node node) {

		checkAndThrow(str.startsWith(TYPE));

		String[] strs = str.split(NODE_IDENTIFIER_SPLITTER);
		checkAndThrow(strs.length > 0);

		for (String strTmp : strs) {

			strTmp = StringUtils.trim(strTmp);
			int equalIndex = StringUtils.indexOf(strTmp, EQUAL_SIGN);
			checkAndThrow(equalIndex > 0);

			String key = StringUtils.substring(strTmp, 0, equalIndex);
			String value = StringUtils.substring(strTmp, equalIndex + 1)
					.replace(QUOTES, "");
			checkAndThrow(StringUtils.isNotBlank(key)
					&& StringUtils.isNotBlank(value));

			IdentifierType identifierEnum = IdentifierType.parse(key);
			checkAndThrow(identifierEnum != null, "Unknow node identifier:"
					+ key);

			switch (identifierEnum) {
			case TYPE:
				NodeType nodeType = NodeType.parse(value);
				checkAndThrow(nodeType != null);
				node.setType(nodeType);
				break;

			case CLASS:
				int targetClassIndex = names.getTargetClassIndex(value);
				checkAndThrow(targetClassIndex >= 0);
				node.setTargetClass(value);
				node.setTargetClassIndex(targetClassIndex);
				break;

			case FREQ:
				String[] freqs = StringUtils.split(value, C5Consts.COMMA);
				int freqCount = 0;
				for (String freq : freqs) {
					if (StringUtils.isBlank(freq)) {
						continue;
					}
					try {
						double doubleFreq = Double.valueOf(freq);
						checkAndThrow(doubleFreq >= 0D);
						node.addClassDist(doubleFreq);
					} catch (Exception ex) {
						checkAndThrow(false);
					}
					freqCount++;
				}
				checkAndThrow(freqCount == names.getTargetClassSize());
				break;

			case ELTS:
				String[] eltses = StringUtils.split(value, C5Consts.COMMA);
				Set<String> subSet = new HashSet<String>();

				for (String discrete : eltses) {
					checkAndThrow(node.getDiscreteIndex(discrete) >= 0
							|| StringUtils
									.equals(C5Consts.NOT_NUMBER, discrete));
					subSet.add(discrete);
				}
				node.addSubSet(subSet);
				break;

			case FORKS:
				checkAndThrow(StringUtils.isNotBlank(value)
						&& StringUtils.isNumeric(value));
				node.setForks(Integer.valueOf(value));
				break;

			case CUT:
				double cut = getDoubleWithThrow(value);
				node.setCut(cut);
				node.setLow(cut);
				node.setHigh(cut);
				node.setMid(cut);
				break;

			case LOW:
				node.setLow(getDoubleWithThrow(value));
				break;

			case MID:
				node.setMid(getDoubleWithThrow(value));
				break;

			case HIGH:
				node.setHigh(getDoubleWithThrow(value));
				break;

			case ATT:
				Attribute attribute = names.getAttribute(value);
				checkAndThrow(attribute != null);
				node.setAttribute(attribute);
				node.setAttributeIndex(names.getArrtibuteIndex(value));
				break;

			default:
				throw new RuntimeException("Never happen.");
			}
		}
	}

	private double getDoubleWithThrow(String str) {
		double value = 0;
		try {
			value = Double.valueOf(str);
		} catch (Exception ex) {
			checkAndThrow(false);
		}
		return value;
	}

	public void checkAndThrow(boolean isLegal, String message) {
		if (!isLegal) {
			throw new IllegalArgumentException(message);
		}
	}

	public void checkAndThrow(boolean isLegal) {
		if (!isLegal) {
			throw new IllegalArgumentException("Illegal Tree File At Line:"
					+ lineNum + ", At File:" + treeFile);
		}
	}
}
