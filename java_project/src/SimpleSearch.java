
public class SimpleSearch extends Search {

	private Solution solution;
	private int countNonimprovements;
	private int maxNonimprovements = 100000;
	private Integer cost;
	private int countStep;
	
	public SimpleSearch(Solution initialSolution, boolean outputInfo) {
		super(outputInfo);
		this.solution = initialSolution;
		this.cost = initialSolution.getCost();
		output("Initial cost: " + this.cost);
	}
	
	public void iterate() {
		this.countStep++;
		Action randomAction = new RandomAction(this.solution);
    	Integer newCost = randomAction.execute();
    	if (newCost != null) {
    		if (newCost < this.cost) {
	    		this.cost = newCost;
	    		output("Step " + this.countStep + ": Improving to " + this.cost);
	    	} else {
	    		randomAction.revert();
	    		this.countNonimprovements += 1;
	    	}
    	} else {
    	}
    	if (maxNonimprovements < countNonimprovements) {
    		shuffle();
    		this.countNonimprovements = 0;
    	}
	}
	
	public void shuffle() {
		for (int i = 0; i < 10; i++) {
			Action randomAction = new RandomAction(this.solution);
			Integer newCost = randomAction.execute();
			if (newCost == null) {
				randomAction.revert();
			}
			this.cost = solution.getCost();
		}
	}
	
	public Solution getBestSolution() {
		return this.solution;
	}
	
}
