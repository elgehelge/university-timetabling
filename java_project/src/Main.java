import java.io.IOException;

public class Main {

	final static String LOCAL_TEST_NO = "01";
	static int timeLimit = 60;
	static boolean local = false;
	
    public static void main(String[] args) throws IOException {
    	
    	String dataLocation = "";
    	
    	// RUN LOCAL (no arguments, or timeLimit given as argument)
    	if (args.length <= 1) {
    		local = true;
    		args = new String[]{"basic.utt", "courses.utt", "lecturers.utt", "rooms.utt", "curricula.utt", "relation.utt", "unavailability.utt"};
    		dataLocation = "./TestDataUTT/Test" + LOCAL_TEST_NO + "/";
    		if (args.length == 1) {
    			timeLimit = Integer.parseInt(args[0]);
    		}
    	}
    	
        Problem problemInstance = new Problem(dataLocation, args);
        localPrint(problemInstance);
        
        // Random HillClimber-like search (moves on every improvement encountered)
        Solution solutionInstance = new Solution(problemInstance);
        Integer cost = solutionInstance.getCost();
        localPrint("Initial cost: " + cost);
        for (int i = 0; i < 1000000; i++) {
        	Action randomAction = new RandomAction(problemInstance, solutionInstance);
        	Integer newCost = randomAction.execute();
        	if (newCost != null && newCost < cost) {
        		cost = newCost;
        		localPrint("Step " + i + ": Improving to " + cost);
        	} else {
        		randomAction.revert();
        	}
        }
        
        localPrint(solutionInstance);
        
        System.out.println(solutionInstance.codeJudgeOutput());
        
        localPrint("Great Success!");    	
    }
    
    static private void localPrint(Object obj) {
    	if (local) {
    		System.out.println(obj);
    	}
    }

}
