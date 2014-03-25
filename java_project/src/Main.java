import java.io.IOException;

public class Main {

	final static String LOCAL_TEST_NO = "01";
	static String dataLocation = "";
	static int timeLimit = 60;
	static boolean local = false;
	
    public static void main(String[] args) throws IOException {
    	
    	// RUN LOCAL (no arguments, or timeLimit given as argument)
    	if (args.length == 1) {
			timeLimit = Integer.parseInt(args[0]);
		}
    	if (args.length <= 1) {
    		local = true;
    		args = new String[]{"basic.utt", "courses.utt", "lecturers.utt", "rooms.utt", "curricula.utt", "relation.utt", "unavailability.utt"};
    		dataLocation = "./TestDataUTT/Test" + LOCAL_TEST_NO + "/";
    	}
    	
    	// Load and show problem
    	localPrint("\n********\nPROBLEM:\n********");
        Problem problemInstance = new Problem(dataLocation, args);
        localPrint(problemInstance);
        
        // Perform search
        localPrint("\n*******\nSEARCH:\n*******");
        localPrint("Running for " + timeLimit + " seconds...");
        long startTime = System.nanoTime();
        long currentTime = System.nanoTime();
        Search search = new SimpleSearch(new Solution(problemInstance), local);
        while ((currentTime - startTime)/1e9 < timeLimit) {
        	search.iterate();
        	currentTime = System.nanoTime();
        }
        
        // Show final solution
        localPrint("\n***************\nFINAL SCHEDULE:\n***************");
        localPrint(search.getBestSolution());
        
        // Output for CodeJudge
        localPrint("\n*****************\nCODEJUDGE OUTPUT:\n*****************");
        System.out.println(search.getBestSolution().codeJudgeOutput());
        System.out.println(search.getBestSolution().getCost());
        
        localPrint("Great Success!");
    }
    
    static private void localPrint(Object obj) {
    	if (local) {
    		System.out.println(obj);
    	}
    }

}
