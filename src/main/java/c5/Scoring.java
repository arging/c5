package c5;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import c5.consts.C5Consts;
import c5.context.ContextHolder;
import c5.enums.CaseType;
import c5.model.Attribute;
import c5.model.Case;
import c5.model.Names;
import c5.model.Node;

/**
 * C5 Scoring
 * 
 * @author li
 * @since 2014
 */
public class Scoring {

	private static final int FIRST_BRANCH_INDEX = 0;

	private static final int SECOND_BRANCH_INDEX = 1;

	private static final int THIRD_BRANCH_INDEX = 2;

	private static final double CONTINUES_BRANCH_THRESHOLD = 0.01;

	private static final double APPROACH_ZERO = 1e-4;

	private final Node[] trees;

	private final Names names;

	public Scoring(Node[] trees, Names names) {
		this.trees = trees;
		this.names = names;
	}

	/**
	 * c5 score function.
	 * 
	 * @param dataes
	 *            input variables.
	 * @return scoring result.
	 */
	public Result score(String[] dataes) {
		try {
			ContextHolder.init();
			ContextHolder.get().initClassSum(names.getTargetClassSize());

			checkData(dataes);

			List<Case> caseList = convert2Case(dataes);

			int resultClassIndex = trees.length > 1 ? boostClassify(caseList)
					: treeClassify(caseList, trees[0]);

			Result result = new Result(ContextHolder.get().getLabel(),
					ContextHolder.get().getFact(),
					names.getTargetClass(resultClassIndex), ContextHolder.get()
							.getConfidence());
			return result;
		} finally {
			ContextHolder.clean();
		}
	}

	private void checkData(String[] dataes) {
		checkAndThrow(
				dataes != null && dataes.length >= names.getAttrCount(),
				"the data count of input array must not smaller than "
						+ names.getAttrCount());
	}

	private int boostClassify(List<Case> caseList) {

		double[] votes = new double[names.getTargetClassSize()];
		double total = 0D;
		for (int i = 0; i < trees.length; i++) {
			int bestClassIndex = treeClassify(caseList, trees[i]);
			votes[bestClassIndex] += ContextHolder.get().getConfidence();
			total += ContextHolder.get().getConfidence();
		}

		for (int i = 0; i < names.getTargetClassSize(); i++) {
			ContextHolder.get().setClassSum(i, votes[i] / total);
		}

		return selectClass();
	}

	private int treeClassify(List<Case> caseList, Node tree) {
		ContextHolder.get().resetClassSum();
		searchLeaf(caseList, tree, null, 1D);
		return selectClass();
	}

	private int selectClass() {
		int highestScoreIndex = 0;
		double highestScore = ContextHolder.get()
				.getClassSum(highestScoreIndex);

		for (int i = 0; i < names.getTargetClassSize(); i++) {
			if (ContextHolder.get().getClassSum(i) > highestScore) {
				highestScoreIndex = i;
				highestScore = ContextHolder.get().getClassSum(i);
			}
		}

		ContextHolder.get().setConfidence(highestScoreIndex);
		return highestScoreIndex;
	}

	private void searchLeaf(List<Case> caseList, Node tree, Node parentTree,
			double fraction) {

		switch (tree.getType()) {
		case LEAF:
			dealLeaf(tree, parentTree, fraction);
			break;

		case DISCRETE:
			dealDiscreteNode(caseList, tree, fraction);
			break;

		case CONTINUES:
			dealContinuesNode(caseList, tree, fraction);
			break;

		case CLUSTERED:
			dealClusteredNode(caseList, tree, parentTree, fraction);
			break;

		default:
			throw new IllegalArgumentException("nodeType[" + tree.getType()
					+ "] is not define");
		}
	}

	private void dealClusteredNode(List<Case> caseList, Node tree,
			Node parentTree, double fraction) {

		Case Case = caseList.get(tree.getAttributeIndex());

		if (Case.getType() != CaseType.UNKNOWN) {
			int indexOfSubSet = tree.searchSubset(Case.getDiscrete());

			if (indexOfSubSet >= 0) {
				searchLeaf(caseList, tree.getBranch(indexOfSubSet), tree,
						fraction);
			}

			else {
				dealLeaf(tree, parentTree, fraction);
			}

		} else {
			searchAllBranch(caseList, tree, fraction);
		}
	}

	private void dealContinuesNode(List<Case> caseList, Node tree,
			double fraction) {
		Case Case = caseList.get(tree.getAttributeIndex());

		switch (Case.getType()) {
		case UNKNOWN:
			searchAllBranch(caseList, tree, fraction);
			break;

		case NAN:
			searchLeaf(caseList, tree.getBranch(FIRST_BRANCH_INDEX), tree,
					fraction);
			break;

		default:
			double br2 = interpolate(tree, Case);

			double br3 = 1 - br2;

			double newFrac2 = fraction * br2;
			double newFrac3 = fraction * br3;

			if (newFrac2 >= CONTINUES_BRANCH_THRESHOLD) {
				searchLeaf(caseList, tree.getBranch(SECOND_BRANCH_INDEX), tree,
						newFrac2);
			}

			if (newFrac3 >= CONTINUES_BRANCH_THRESHOLD) {
				searchLeaf(caseList, tree.getBranch(THIRD_BRANCH_INDEX), tree,
						newFrac3);
			}
			break;
		}
	}

	public double interpolate(Node tree, Case Case) {

		double val = Case.getContinuous();

		if (val <= tree.getLow()) {
			return 1.0;
		}

		if (val >= tree.getHigh()) {
			return 0.0;
		}

		if (val <= tree.getMid()) {
			return 1 - 0.5 * (val - tree.getLow())
					/ (tree.getMid() - tree.getLow() + 1E-6);
		}

		return 0.5 - 0.5 * (val - tree.getMid())
				/ (tree.getHigh() - tree.getMid() + 1E-6);
	}

	private void dealDiscreteNode(List<Case> caseList, Node tree,
			double fraction) {
		Case Case = caseList.get(tree.getAttributeIndex());
		int forkIndex = tree.getDiscreteIndex(Case.getDiscrete());

		if (forkIndex >= 0) {
			searchLeaf(caseList, tree.getBranch(forkIndex), tree, fraction);
		} else {
			searchAllBranch(caseList, tree, fraction);
		}
	}

	private void dealLeaf(Node tree, Node parentTree, double fraction) {
		if (tree.getTotalCases() < APPROACH_ZERO) {
			tree = parentTree;
		}
		for (int i = 0; i < names.getTargetClassSize(); i++) {
			ContextHolder.get().addClassSum(i,
					fraction * tree.getClassDist(i) / tree.getTotalCases());
		}
	}

	private void searchAllBranch(List<Case> caseList, Node tree, double fraction) {
		for (Node branch : tree.getBranches()) {
			if (branch.getTotalCases() > APPROACH_ZERO) {
				searchLeaf(
						caseList,
						branch,
						tree,
						fraction * branch.getTotalCases()
								/ tree.getTotalCases());
			}
		}
	}

	private List<Case> convert2Case(String[] dataArray) {

		List<Attribute> attributes = names.getAttributes();
		List<Case> caseList = new ArrayList<Case>();

		processFactclass(dataArray, attributes);

		for (int i = 0; i < attributes.size(); i++) {
			String data = dataArray[i];
			checkAndThrow(StringUtils.isNotBlank(data),
					"Illegal Argument In Data Array Index Of " + i);

			if (names.getLableAttIndex() == i) {
				ContextHolder.get().setLabel(data);
			}

			switch (CaseType.parse(data)) {
			case UNKNOWN:
				caseList.add(new Case(CaseType.UNKNOWN));
				break;

			case NAN:
				caseList.add(new Case(CaseType.NAN, C5Consts.NOT_NUMBER));
				break;

			case NORMAL:
				convertNormalCase(data, attributes.get(i), caseList);
				break;

			default:
				break;
			}
		}

		return caseList;
	}

	private void processFactclass(String[] dataArray, List<Attribute> attributes) {

		if (dataArray.length > attributes.size()) {
			String fact = dataArray[attributes.size()];
			fact = names.getTargetClassIndex(fact) < 0 ? C5Consts.UNKNOWN
					: fact;
			ContextHolder.get().setFact(fact);
		} else {
			ContextHolder.get().setFact(C5Consts.UNKNOWN);
		}
	}

	private void convertNormalCase(String data, Attribute attribute,
			List<Case> caseList) {
		switch (attribute.getType()) {
		case CONTINUOUS:
			try {
				caseList.add(new Case(CaseType.NORMAL, Double.valueOf(data)));
			} catch (NumberFormatException ex) {
				checkAndThrow("Illegal Continuous Value For Attribute "
						+ attribute.getName() + ", value= " + data);
			}
			break;

		case DISCRETE:
			checkAndThrow(
					attribute.getDiscreteIndex(data) >= 0,
					"Illegal Discrete Value For Attribute "
							+ attribute.getName() + ",value=" + data);
			caseList.add(new Case(CaseType.NORMAL, data));
			break;

		default:
			caseList.add(new Case(CaseType.NORMAL, data));
			break;
		}
	}

	private void checkAndThrow(String message) {
		checkAndThrow(false, message);
	}

	private void checkAndThrow(boolean isLegal, String message) {
		if (!isLegal) {
			throw new IllegalArgumentException(message);
		}
	}
}
