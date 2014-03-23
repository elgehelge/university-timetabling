Java framework for the Curriculum-based University Course Timetabling optimization problem
==========================================================================================

This code made in the context of a course in metaheuristics at the Danish Technical University.

Description of the problem, the cost function, the input data:
[university_timetabling_description.pdf](https://rawgithub.com/elgehelge/university-timetabling/master/university_timetabling_description.pdf)

Java Doc:
[index.html](https://rawgithub.com/elgehelge/university-timetabling/master/java_project/doc/index.html)

***
Example of Main.java:
---------------------
```java
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
```

***
Example of output:
------------------
```java
Problem instance:
	No. of courses: 30
	No. of rooms: 6
	No. of days: 5
	Periods per day: 6
	No. of curricula: 14
	No. of constraints: 53
	No. of lectures: 24
	Rooms:	{0=200, 1=100, 2=9, 3=30, 4=20, 5=30}
	Courses:
		{0=Course :
			Course ID: 0
			Lecturer ID: 0
			No. of lectures: 6
			Min. work days: 4
			No. of students: 130
			Curricula: [0, 2]
			Unavailable timeslots: [25, 24, 27, 26, 29, 28]
		, 1=Course :
			Course ID: 1
			Lecturer ID: 1
			No. of lectures: 6
			Min. work days: 4
			No. of students: 75
			Curricula: [0]
			Unavailable timeslots: []
		, 2=Course :
			Course ID: 2
			Lecturer ID: 2
			No. of lectures: 7
			Min. work days: 3
			No. of students: 117
			Curricula: [0, 12]
			Unavailable timeslots: [0, 1, 2, 3, 4, 5]
		, 3=Course :
			Course ID: 3
			Lecturer ID: 3
			No. of lectures: 3
			Min. work days: 3
			No. of students: 75
			Curricula: [0]
			Unavailable timeslots: []
		, 4=Course :
			Course ID: 4
			Lecturer ID: 4
			No. of lectures: 1
			Min. work days: 1
			No. of students: 65
			Curricula: [1]
			Unavailable timeslots: []
		, 5=Course :
			Course ID: 5
			Lecturer ID: 5
			No. of lectures: 8
			Min. work days: 3
			No. of students: 65
			Curricula: [1]
			Unavailable timeslots: []
		, 6=Course :
			Course ID: 6
			Lecturer ID: 6
			No. of lectures: 7
			Min. work days: 3
			No. of students: 65
			Curricula: [1]
			Unavailable timeslots: []
		, 7=Course :
			Course ID: 7
			Lecturer ID: 7
			No. of lectures: 2
			Min. work days: 2
			No. of students: 65
			Curricula: [1]
			Unavailable timeslots: []
		, 8=Course :
			Course ID: 8
			Lecturer ID: 8
			No. of lectures: 4
			Min. work days: 3
			No. of students: 55
			Curricula: [2]
			Unavailable timeslots: [19, 18, 21, 20, 23, 22]
		, 9=Course :
			Course ID: 9
			Lecturer ID: 9
			No. of lectures: 8
			Min. work days: 3
			No. of students: 55
			Curricula: [2]
			Unavailable timeslots: [17, 16, 19, 18, 21, 20, 23, 22, 14, 15]
		, 10=Course :
			Course ID: 10
			Lecturer ID: 10
			No. of lectures: 5
			Min. work days: 4
			No. of students: 55
			Curricula: [2]
			Unavailable timeslots: []
		, 11=Course :
			Course ID: 11
			Lecturer ID: 11
			No. of lectures: 5
			Min. work days: 4
			No. of students: 20
			Curricula: [3]
			Unavailable timeslots: []
		, 12=Course :
			Course ID: 12
			Lecturer ID: 12
			No. of lectures: 5
			Min. work days: 4
			No. of students: 11
			Curricula: [4]
			Unavailable timeslots: []
		, 13=Course :
			Course ID: 13
			Lecturer ID: 13
			No. of lectures: 1
			Min. work days: 1
			No. of students: 31
			Curricula: [3, 4]
			Unavailable timeslots: []
		, 14=Course :
			Course ID: 14
			Lecturer ID: 14
			No. of lectures: 6
			Min. work days: 4
			No. of students: 31
			Curricula: [3, 4]
			Unavailable timeslots: [21, 20, 23, 22, 25, 24, 27, 26, 29, 28]
		, 15=Course :
			Course ID: 15
			Lecturer ID: 15
			No. of lectures: 5
			Min. work days: 4
			No. of students: 2
			Curricula: [6]
			Unavailable timeslots: []
		, 17=Course :
			Course ID: 17
			Lecturer ID: 17
			No. of lectures: 6
			Min. work days: 4
			No. of students: 7
			Curricula: [6, 8]
			Unavailable timeslots: []
		, 16=Course :
			Course ID: 16
			Lecturer ID: 16
			No. of lectures: 5
			Min. work days: 4
			No. of students: 2
			Curricula: [10]
			Unavailable timeslots: []
		, 19=Course :
			Course ID: 19
			Lecturer ID: 19
			No. of lectures: 5
			Min. work days: 4
			No. of students: 10
			Curricula: [7, 13]
			Unavailable timeslots: []
		, 18=Course :
			Course ID: 18
			Lecturer ID: 18
			No. of lectures: 6
			Min. work days: 4
			No. of students: 6
			Curricula: [7]
			Unavailable timeslots: []
		, 21=Course :
			Course ID: 21
			Lecturer ID: 20
			No. of lectures: 6
			Min. work days: 4
			No. of students: 6
			Curricula: [9]
			Unavailable timeslots: []
		, 20=Course :
			Course ID: 20
			Lecturer ID: 20
			No. of lectures: 6
			Min. work days: 4
			No. of students: 8
			Curricula: [9, 10]
			Unavailable timeslots: []
		, 23=Course :
			Course ID: 23
			Lecturer ID: 8
			No. of lectures: 6
			Min. work days: 4
			No. of students: 14
			Curricula: [5, 9, 13]
			Unavailable timeslots: []
		, 22=Course :
			Course ID: 22
			Lecturer ID: 21
			No. of lectures: 6
			Min. work days: 4
			No. of students: 5
			Curricula: [8]
			Unavailable timeslots: []
		, 25=Course :
			Course ID: 25
			Lecturer ID: 23
			No. of lectures: 6
			Min. work days: 4
			No. of students: 9
			Curricula: [10, 11]
			Unavailable timeslots: []
		, 24=Course :
			Course ID: 24
			Lecturer ID: 22
			No. of lectures: 5
			Min. work days: 4
			No. of students: 7
			Curricula: [11]
			Unavailable timeslots: []
		, 27=Course :
			Course ID: 27
			Lecturer ID: 2
			No. of lectures: 6
			Min. work days: 4
			No. of students: 4
			Curricula: [5]
			Unavailable timeslots: []
		, 26=Course :
			Course ID: 26
			Lecturer ID: 7
			No. of lectures: 6
			Min. work days: 4
			No. of students: 7
			Curricula: [11]
			Unavailable timeslots: []
		, 29=Course :
			Course ID: 29
			Lecturer ID: 3
			No. of lectures: 6
			Min. work days: 4
			No. of students: 9
			Curricula: [5, 8]
			Unavailable timeslots: []
		, 28=Course :
			Course ID: 28
			Lecturer ID: 1
			No. of lectures: 6
			Min. work days: 4
			No. of students: 10
			Curricula: [9, 13]
			Unavailable timeslots: [0, 1, 2, 6, 7, 8, 12, 13, 14, 19, 18, 20, 25, 24, 26]
		}

Initial cost: 2130
Step 2: Improving to 2091
Step 3: Improving to 2078
Step 4: Improving to 2067
Step 13: Improving to 2058
Step 14: Improving to 2045
Step 22: Improving to 2035
Step 24: Improving to 2024
Step 25: Improving to 2013
Step 32: Improving to 2010
Step 33: Improving to 1998
Step 35: Improving to 1992
Step 43: Improving to 1971
Step 45: Improving to 1954
Step 47: Improving to 1951
Step 70: Improving to 1928
Step 72: Improving to 1918
Step 75: Improving to 1917
Step 77: Improving to 1915
Step 83: Improving to 1914
Step 84: Improving to 1900
Step 85: Improving to 1888
Step 89: Improving to 1874
Step 94: Improving to 1845
Step 98: Improving to 1837
Step 99: Improving to 1823
Step 100: Improving to 1811
Step 104: Improving to 1801
Step 108: Improving to 1798
Step 203: Improving to 1775
Step 207: Improving to 1750
Step 210: Improving to 1741
Step 211: Improving to 1725
Step 436: Improving to 1724
Step 437: Improving to 1711
Step 443: Improving to 1687
Step 548: Improving to 1683
***Schedule***
	 Day 0  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 	 Day 1  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 	 Day 2  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 	 Day 3  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 	 Day 4  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 
Room 0		| null |   10 |    5 | null | null |   19 | 		| null | null | null |   24 | null |   16 | 		| null | null | null |    1 |   27 |   10 | 		|   25 | null | null | null |    3 | null | 		| null | null | null | null |   25 | null | 
Room 1		| null | null |    8 | null |    0 |   20 | 		|    8 | null | null | null |    0 | null | 		|   14 | null | null | null | null | null | 		|    1 | null | null |   15 | null | null | 		| null | null | null |    3 |   18 | null | 
Room 2		| null | null | null |   29 |   22 | null | 		| null |   12 | null | null | null | null | 		| null |   16 | null |   27 | null |   29 | 		| null | null | null | null | null |    5 | 		| null | null |    8 | null | null | null | 
Room 3		| null | null | null | null | null |    7 | 		|   27 | null |   29 | null | null | null | 		|   25 | null | null | null | null | null | 		| null |   14 |   20 | null |   19 | null | 		| null |   12 | null |   10 | null | null | 
Room 4		|   13 | null |   12 | null | null | null | 		|    3 |   18 | null | null | null | null | 		|   23 | null | null | null | null | null | 		| null |   18 | null |    1 | null | null | 		|   29 | null | null | null | null | null | 
Room 5		| null | null |   15 | null | null | null | 		|   24 | null | null |    9 | null | null | 		| null | null |   10 |   18 | null | null | 		| null | null | null |   10 | null |   21 | 		| null | null |   29 |   25 | null |   24 | 

UNSCHEDULED 104
ROOMCAPACITY 421
ROOMSTABILITY 25
MINIMUMWORKINGDAYS 56
CURRICULUMCOMPACTNESS 56
OBJECTIVE 1878
C0010 0 1 R0000
C0005 0 2 R0000
C0019 0 5 R0000
C0024 1 3 R0000
C0016 1 5 R0000
C0001 2 3 R0000
C0027 2 4 R0000
C0010 2 5 R0000
C0025 3 0 R0000
C0003 3 4 R0000
C0025 4 4 R0000
C0008 0 2 R0001
C0000 0 4 R0001
C0020 0 5 R0001
C0008 1 0 R0001
C0000 1 4 R0001
C0014 2 0 R0001
C0001 3 0 R0001
C0015 3 3 R0001
C0003 4 3 R0001
C0018 4 4 R0001
C0029 0 3 R0002
C0022 0 4 R0002
C0012 1 1 R0002
C0016 2 1 R0002
C0027 2 3 R0002
C0029 2 5 R0002
C0005 3 5 R0002
C0008 4 2 R0002
C0007 0 5 R0003
C0027 1 0 R0003
C0029 1 2 R0003
C0025 2 0 R0003
C0014 3 1 R0003
C0020 3 2 R0003
C0019 3 4 R0003
C0012 4 1 R0003
C0010 4 3 R0003
C0013 0 0 R0004
C0012 0 2 R0004
C0003 1 0 R0004
C0018 1 1 R0004
C0023 2 0 R0004
C0018 3 1 R0004
C0001 3 3 R0004
C0029 4 0 R0004
C0015 0 2 R0005
C0024 1 0 R0005
C0009 1 3 R0005
C0010 2 2 R0005
C0018 2 3 R0005
C0010 3 3 R0005
C0021 3 5 R0005
C0029 4 2 R0005
C0025 4 3 R0005
C0024 4 5 R0005

Great Success!
```