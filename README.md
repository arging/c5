C5
==

####There are only four steps for c5 scoring.

>* Parse your names file.

>* Parse your tree file.

>* Construct your Scoring instance by Names and Trees.

>* Call Scoring score with your input variables.

>>

		NamesParser namesAnalyzer = new NamesParser(namesFile);
		Names names = namesAnalyzer.parse();

	    TreeParser treeParser = new TreeParser(treeFile);
		Node[] trees = treeParser.parse();

		Scoring scoring = new Scoring(trees, names);

		Result result = scoring.score(inputVariables);

Example: https://github.com/arging/C5/blob/master/src/test/java/c5/C5Example.java
