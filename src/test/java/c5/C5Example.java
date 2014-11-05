package c5;

import java.io.IOException;

import c5.model.Names;
import c5.model.Node;
import c5.parser.NamesParser;
import c5.parser.TreeParser;

public class C5Example {

	public static void main(String[] args) throws IOException {

		// Find the base path.
		String basePath = C5Example.class.getClassLoader().getResource("")
				.getPath();

		// 1、Construct names
		NamesParser namesAnalyzer = new NamesParser(basePath + "/housing.names");
		Names names = namesAnalyzer.parse();

		// 2、Construct trees
		TreeParser treeParser = new TreeParser(basePath + "/housing.tree",
				names);
		Node[] trees = treeParser.parse();

		// 3、Construct Scoring
		Scoring scoring = new Scoring(trees, names);

		// 4、Do score
		Result result = scoring.score(new String[] { "8.20058", "0.00",
				"18.100", "0", "0.7130", "5.9360", "80.30", "2.7792", "24",
				"666.0", "20.20", "3.50", "16.94", "bottom 80%" });

		System.out.println(String.format("%-15.15s  %.6f", result.getTarget(),
				result.getScore()));
	}

}