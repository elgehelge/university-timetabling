Java framework for the Curriculum-based University Course Timetabling optimization problem
==========================================================================================

This code made in the context of a course in metaheuristics at the Danish Technical University.

Description of the problem, the cost function, the input data:
[university_timetabling_description.pdf](https://rawgithub.com/elgehelge/university-timetabling/master/university_timetabling_description.pdf)

Java Doc:
[index.html](https://rawgithub.com/elgehelge/university-timetabling/master/java_project/doc/index.html)

***
Example of search class:
------------------------
```java
public class SimpleSearch extends Search {

	private Solution solution;
	private int countNonimprovements;
	private int maxNonimprovements = 500;
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
    	if (newCost != null && newCost < this.cost) {
    		this.cost = newCost;
    		output("Step " + this.countStep + ": Improving to " + cost);
    	} else {
    		randomAction.revert();
    		this.countNonimprovements += 1;
    	}
    	if (maxNonimprovements < countNonimprovements) {
    		shuffle();
    		this.countNonimprovements = 0;
    	}
    	
	}
	
	public void shuffle() {
		for (int i = 0; i < 1000; i++) {
			Action randomAction = new RandomAction(this.solution);
			Integer newCost = randomAction.execute();
			if (newCost == null) {
				randomAction.revert();
			}
		}
	}
	
	public Solution getBestSolution() {
		return this.solution;
	}
	
}
```

***
Example of output:
------------------
```java

********
PROBLEM:
********
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


*******
SEARCH:
*******
Running for 3 seconds...
Initial cost: 2130
Step 5: Improving to 2114
Step 10: Improving to 2069
Step 12: Improving to 2041
Step 21: Improving to 1968
Step 25: Improving to 1955
Step 26: Improving to 1945
Step 31: Improving to 1919
Step 39: Improving to 1894
Step 64: Improving to 1892
Step 717: Improving to 1882
Step 722: Improving to 1791
Step 723: Improving to 1777
Step 724: Improving to 1766
Step 725: Improving to 1763
Step 727: Improving to 1740
Step 728: Improving to 1729
Step 6713: Improving to 1693
Step 12314: Improving to 1678
Step 12319: Improving to 1665
Step 14550: Improving to 1597
Step 14570: Improving to 1590
Step 85567: Improving to 1589
Step 85568: Improving to 1576
Step 85569: Improving to 1560
Step 85570: Improving to 1536

***************
FINAL SCHEDULE:
***************
Schedule:
	 Day 0  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 	 Day 1  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 	 Day 2  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 	 Day 3  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 	 Day 4  |   P0 |   P1 |   P2 |   P3 |   P4 |   P5 | 
Room 0		|    9 | null |   21 |   26 |   25 |   10 | 		| null | null | null | null | null | null | 		|    9 |   22 | null | null | null | null | 		| null | null | null | null | null |   22 | 		| null |   19 |   18 |   26 | null |   21 | 
Room 1		| null | null | null | null | null | null | 		| null |   15 | null | null | null |   12 | 		|   21 |   14 | null |   10 | null |   23 | 		|   23 |   20 | null | null | null | null | 		|    5 | null |   29 |   12 |   29 |   17 | 
Room 2		| null | null |   15 | null | null |   27 | 		|   12 | null | null | null |   21 | null | 		| null | null |   29 |   24 |   16 |   26 | 		|    0 | null | null |   26 |    5 |   24 | 		| null | null | null |    2 | null |   26 | 
Room 3		| null | null | null |    4 |    8 | null | 		| null |   26 |   29 |    0 |   13 | null | 		|   11 | null | null |   29 | null | null | 		| null |    6 |    1 |   28 |    0 | null | 		|   22 | null | null |    5 | null | null | 
Room 4		|   27 |   15 | null | null | null | null | 		| null | null |   20 | null | null | null | 		|    3 | null |    1 |   15 | null | null | 		|    6 | null | null | null |   18 |   10 | 		| null | null | null | null | null | null | 
Room 5		| null | null | null | null |   22 | null | 		| null | null | null | null |    9 | null | 		| null | null | null | null | null | null | 		|   22 | null |    6 |   18 | null | null | 		|   16 |   11 | null |   10 | null |    1 | 


*****************
CODEJUDGE OUTPUT:
*****************
UNSCHEDULED 89
ROOMCAPACITY 983
ROOMSTABILITY 28
MINIMUMWORKINGDAYS 48
CURRICULUMCOMPACTNESS 62
OBJECTIVE 2265
C0009 0 0 R0000
C0021 0 2 R0000
C0026 0 3 R0000
C0025 0 4 R0000
C0010 0 5 R0000
C0009 2 0 R0000
C0022 2 1 R0000
C0022 3 5 R0000
C0019 4 1 R0000
C0018 4 2 R0000
C0026 4 3 R0000
C0021 4 5 R0000
C0015 1 1 R0001
C0012 1 5 R0001
C0021 2 0 R0001
C0014 2 1 R0001
C0010 2 3 R0001
C0023 2 5 R0001
C0023 3 0 R0001
C0020 3 1 R0001
C0005 4 0 R0001
C0029 4 2 R0001
C0012 4 3 R0001
C0029 4 4 R0001
C0017 4 5 R0001
C0015 0 2 R0002
C0027 0 5 R0002
C0012 1 0 R0002
C0021 1 4 R0002
C0029 2 2 R0002
C0024 2 3 R0002
C0016 2 4 R0002
C0026 2 5 R0002
C0000 3 0 R0002
C0026 3 3 R0002
C0005 3 4 R0002
C0024 3 5 R0002
C0002 4 3 R0002
C0026 4 5 R0002
C0004 0 3 R0003
C0008 0 4 R0003
C0026 1 1 R0003
C0029 1 2 R0003
C0000 1 3 R0003
C0013 1 4 R0003
C0011 2 0 R0003
C0029 2 3 R0003
C0006 3 1 R0003
C0001 3 2 R0003
C0028 3 3 R0003
C0000 3 4 R0003
C0022 4 0 R0003
C0005 4 3 R0003
C0027 0 0 R0004
C0015 0 1 R0004
C0020 1 2 R0004
C0003 2 0 R0004
C0001 2 2 R0004
C0015 2 3 R0004
C0006 3 0 R0004
C0018 3 4 R0004
C0010 3 5 R0004
C0022 0 4 R0005
C0009 1 4 R0005
C0022 3 0 R0005
C0006 3 2 R0005
C0018 3 3 R0005
C0016 4 0 R0005
C0011 4 1 R0005
C0010 4 3 R0005
C0001 4 5 R0005

Great Success!
```