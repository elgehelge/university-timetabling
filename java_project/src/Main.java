import java.io.IOException;

public class Main {

	final static String LOCAL_TEST_NO = "01";
	static boolean local = false;
	
    public static void main(String[] args) throws IOException {
    	
    	String dataLocation = "";
    	
    	// RUN LOCAL (no arguments given)
    	if (args.length == 0) {
    		local = true;
    		args = new String[]{"basic.utt", "courses.utt", "lecturers.utt", "rooms.utt", "curricula.utt", "relation.utt", "unavailability.utt"};
    		dataLocation = "./TestDataUTT/Test" + LOCAL_TEST_NO + "/";
    	}
    	
        Problem problemInstance = new Problem(dataLocation, args);
        localPrint(problemInstance);
        
        Solution SolutionInstance = new Solution(problemInstance);
        for (int i = 0; i < 10000; i++) {
        	SolutionInstance.insertRandomLecture();
        	SolutionInstance.removeRandomLecture();
        }
        localPrint(SolutionInstance);
        
        System.out.println(SolutionInstance.codeJudgeOutput());
        
        localPrint("Great Success!");    	
    }
    
    static private void localPrint(Object obj) {
    	if (local) {
    		System.out.println(obj);
    	}
    }

}
