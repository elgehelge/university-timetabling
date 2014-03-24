
abstract public class Search {

	private boolean shouldOutputInfo;
	
	public Search(boolean outputInfo) {
		this.shouldOutputInfo = outputInfo;
	}
	
	abstract public void iterate();
	
	abstract public Solution getBestSolution();
	
	protected void output(Object object) {
		if (shouldOutputInfo) {
			System.out.println(object);
		}
	}

}
