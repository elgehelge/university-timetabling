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
	
	public int calcDeltaCostInsert(int room, int day, int period, int courseID) {
		return calcDeltaCost(true, room, day, period, courseID);
	}
	
	public int calcDeltaCostRemove(int room, int day, int period, int courseID) {
		return calcDeltaCost(false, room, day, period, courseID);
	}
	
	private int calcDeltaCost(boolean isInsertOperation, int room, int day, int period, int courseID) {
		Problem.Course course = this.problem.courses.get(courseID);
		
		// Calculate penalty count deltas
		int deltaCountRoomCapacity = deltaCountRoomCapacity(isInsertOperation, room, course);
/*!*/	int deltaCountCurriculumCompactness = deltaCountCurriculumCompactness(isInsertOperation, day, period, course);
		int deltaCountMinWorkDays = deltaCountMinimumWorkingDays(isInsertOperation, day, courseID);
		int deltaCountRoomStability = deltaCountRoomStability(isInsertOperation, room, courseID);
		int deltaCountUnscheduled = deltaCountUnscheduled(isInsertOperation);
		/*!*/// TODO: We should check curriculumcompactness to see how much time i uses.
		
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
	
	private int deltaCountUnscheduled(boolean insert) {
		if (insert) {
			return -1;
		} else {
			return 1;
		}
	}

	private int deltaCountRoomStability(boolean insert, int room, int courseID) {
		int deltaCountRooms = 0;
		if (insert) {
			if (this.coursesRooms[courseID][room] == 0) {
				this.courseNoOfRooms[courseID] += 1;
				if (this.courseNoOfRooms[courseID] > 1) { // We used an extra room
					deltaCountRooms = 1;
				}
			}
			this.coursesRooms[courseID][room] += 1;
		} else {
			if (this.coursesRooms[courseID][room] == 1) {
				this.courseNoOfRooms[courseID] += -1;
				if (this.courseNoOfRooms[courseID] > 0) { // We used a room less
					deltaCountRooms = -1;
				}
			}
			this.coursesRooms[courseID][room] += -1;
		}
		return deltaCountRooms;
	}

	private int deltaCountMinimumWorkingDays(boolean insert, int day, int courseID) {
		int deltaCountDaysBelowMin = 0;
		if (insert) {
			if (this.coursesDays[courseID][day] == 0) {
				this.courseDaysBelowMinimum[courseID] += -1;
				if (this.courseDaysBelowMinimum[courseID] >= 0) { // We improved
					deltaCountDaysBelowMin = -1;
				}
			}
			this.coursesDays[courseID][day] += 1;
		} else {
			if (this.coursesDays[courseID][day] == 1) {
				this.courseDaysBelowMinimum[courseID] += 1;
				if (this.courseDaysBelowMinimum[courseID] > 0) { // We worsened
					deltaCountDaysBelowMin = 1;
				}
			}
			this.coursesDays[courseID][day] += -1;
		}
		return deltaCountDaysBelowMin;
	}

	private int deltaCountCurriculumCompactness(boolean insert, int day, int period, Problem.Course course) {
		int deltaBecomesAlone = 0;
		Iterator<Integer> curricula = course.curricula.iterator();
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
						deltaBecomesAlone += -1;
					}
				}
			}
			if (isAlone) { // No lectures with the same curriculum in either direction
				deltaBecomesAlone += 1;
			}
		}
		if (insert) {
			return deltaBecomesAlone;
		} else {
			return -deltaBecomesAlone;
		}
	}

	private int deltaCountRoomCapacity(boolean insert, int room, Problem.Course course) {
		int studentOverflow = Math.max(0, course.noOfStudents - problem.roomCapacity.get(room));
		if (insert) {
			return studentOverflow;
		} else {
			return -studentOverflow;
		}
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
