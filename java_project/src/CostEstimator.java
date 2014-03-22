import java.util.Iterator;


public class CostEstimator {
	
	final static int COST_ROOMCAPACITY			= 1;
	final static int COST_ROOMSTABILITY			= 1;
	final static int COST_CURRICULUMCOMPACTNESS	= 2;
	final static int COST_MINIMUMWORKINGDAYS	= 5;
	final static int COST_UNSCHEDULED			= 10;

	private int costTotalPenalties;
	private int countUnscheduled;
	private int countRoomCapacity;
	private int countMinimumWorkingDays;
	private int countRoomStability;
	private int countCurriculumCompactness;
	
	private int[][] coursesDays;				// courseID + day				-> no. of lectures this day
	private int[] courseDaysBelowMinimum;		// courseID						-> no. of days below minimum
	private int[][] coursesRooms;				// courseID + room				-> no. of lectures in room
	private int[] courseNoOfRooms;				// courseID						-> no. of distinct rooms

	private Problem problem;
	private Solution solution;
	
	public CostEstimator(Problem problem, Solution solution) {
		this.problem = problem;
		this.solution = solution;
		
		this.coursesDays = new int[problem.noOfCourses][problem.noOfDays];
		this.coursesRooms = new int[problem.noOfCourses][problem.noOfRooms];
		this.courseNoOfRooms = new int[problem.noOfCourses];
		
		// Initialize Unscheduled & DaysBelowMinimum
		int initialUnscheduled = 0;
		int initialDaysBelowMinimum = 0;
		this.courseDaysBelowMinimum = new int[problem.noOfCourses];
		Iterator<Integer> courseIDs = problem.courses.keySet().iterator();
		while (courseIDs.hasNext()) {
			int courseID = courseIDs.next();
			Problem.Course course = problem.courses.get(courseID);
			initialUnscheduled += course.noOfLectures;
			this.courseDaysBelowMinimum[courseID] = course.minWorkDays;
			initialDaysBelowMinimum += course.minWorkDays;
		}
		
		// Initialize cost
		this.countCurriculumCompactness = 0;
		this.countRoomCapacity = 0;
		this.countRoomStability = 0;
		this.countUnscheduled = initialUnscheduled;
		this.countMinimumWorkingDays = initialDaysBelowMinimum;
		this.costTotalPenalties = countUnscheduled * COST_UNSCHEDULED + countMinimumWorkingDays * COST_MINIMUMWORKINGDAYS;
	}
	
	public int calcDeltaCostAdd(int room, int day, int period, int courseID) {
		Problem.Course course = this.problem.courses.get(courseID);
		
		// RoomCapacity
		int deltaCountRoomCapacity = Math.max(0, course.noOfStudents - problem.roomCapacity.get(room));
		
		// CurriculumCompactness
		int deltaCountCurriculumCompactness = 0;
		Iterator<Integer> curricula = problem.courses.get(courseID).curricula.iterator();
		while (curricula.hasNext()) { // for each curriculum
			int curriculum = curricula.next();
			boolean isAlone = true; // Assuming that curriculum is alone
			for (int shift : new int[]{-1, 1}) {
				int adjacentPeriod = period + shift;
				if (this.solution.isCurriculaTaught(curriculum, day, adjacentPeriod)) {
					// Curriculum taught by adjacent period
					isAlone = false;
					int adjacentAdjacentPeriod = adjacentPeriod + shift;
					if (!this.solution.isCurriculaTaught(curriculum, day, adjacentAdjacentPeriod)) {
						// Curriculum is NOT also taught by 2nd adjacent period
						deltaCountCurriculumCompactness += -1;
					}
				}
			}
			if (isAlone) { // No lectures with the same curriculum in either direction
				deltaCountCurriculumCompactness += 1;
			}
		}
		
		// MinimumWorkingDays
		int deltaCountMinWorkDays = 0;
		if (this.coursesDays[courseID][day] == 0) {
			this.courseDaysBelowMinimum[courseID] += -1;
			if (this.courseDaysBelowMinimum[courseID] >= 0) { // We improved
				deltaCountMinWorkDays = -1;
			}
		}
		this.coursesDays[courseID][day] += 1;
		
		// RoomStability
		int deltaCountRoomStability = 0;
		if (this.coursesRooms[courseID][room] == 0) {
			this.courseNoOfRooms[courseID] += 1;
			if (this.courseNoOfRooms[courseID] > 1) { // We used an extra room
				deltaCountRoomStability = 1;
			}
		}
		this.coursesRooms[courseID][room] += 1;
		
		// Unscheduled
		int deltaCountUnscheduled = -1;
		
		// Total
		int deltaTotalCost = deltaCountCurriculumCompactness * COST_CURRICULUMCOMPACTNESS +
						 deltaCountRoomCapacity * COST_ROOMCAPACITY +
						 deltaCountRoomStability * COST_ROOMSTABILITY +
						 deltaCountMinWorkDays * COST_MINIMUMWORKINGDAYS +
						 deltaCountUnscheduled * COST_UNSCHEDULED;
		
		// Update counts
		this.countUnscheduled += deltaCountUnscheduled;
		this.countRoomCapacity += deltaCountRoomCapacity;
		this.countRoomStability += deltaCountRoomStability;
		this.countMinimumWorkingDays += deltaCountMinWorkDays;
		this.countCurriculumCompactness += deltaCountCurriculumCompactness;
		this.costTotalPenalties += deltaTotalCost;
		
		return costTotalPenalties;
	}
	
	public String codeJudgeHeader() {
		return
			"UNSCHEDULED " + countUnscheduled + "\n" +
			"ROOMCAPACITY " + countRoomCapacity + "\n" +
			"ROOMSTABILITY " + countRoomStability + "\n" +
			"MINIMUMWORKINGDAYS " + countMinimumWorkingDays + "\n" +
			"CURRICULUMCOMPACTNESS " + countCurriculumCompactness + "\n" +
			"OBJECTIVE " + costTotalPenalties + "\n";
	}
}
