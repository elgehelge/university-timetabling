import java.util.Iterator;

/**
 * Calculating the cost of a solution by keeping track of the change in cost (the delta-function).
 * It is assumed that the solution is only changed by inserting or removing lectures from the timetable.
 * 
 * Interact with this class using:
 * - updateCostInsert(int room, int day, int period, int courseID)
 *   > returns total cost after the insert.
 * - updateCostRemove(int room, int day, int period, int courseID)
 *   > returns total cost after the remove.
 *   
 */
public class CostCalculator {
	
	// The constants defined by the optimization problem description
	final static int COST_ROOMCAPACITY			= 1;
	final static int COST_ROOMSTABILITY			= 1;
	final static int COST_CURRICULUMCOMPACTNESS	= 2;
	final static int COST_MINIMUMWORKINGDAYS	= 5;
	final static int COST_UNSCHEDULED			= 10;

	// Bookkeeping on the number of each penalty and the total cost
	public int costTotal;
	private int countUnscheduled;
	private int countRoomCapacity;
	private int countMinimumWorkingDays;
	private int countRoomStability;
	private int countCurriculumCompactness;
	
	// In order to make cost calculation efficient we know which days and rooms each course is allocated to
	private int[][] coursesDays;				// courseID + day		-> no. of lectures this day
	private int[] courseDaysBelowMinimum;		// courseID				-> no. of days below minimum
	private int[][] coursesRooms;				// courseID + room		-> no. of lectures in room
	private int[] courseNoOfRooms;				// courseID				-> no. of distinct rooms

	// References to the problem and the solution which cost we are calculating
	private Problem problem;
	private Solution solution;
	
	/**
	 * Constructor.
	 * @param problem - The problem instance that the solution is a solution for.
	 * @param solution - The solution instance that we are calculating the cost for.
	 */
	public CostCalculator(Problem problem, Solution solution) {
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
		this.costTotal = countUnscheduled * COST_UNSCHEDULED + countMinimumWorkingDays * COST_MINIMUMWORKINGDAYS;
	}
	
	
	// Public methods
	
	/**
	 * Changes the cost calculation accordingly when a lecture is inserted into the timetable.
	 * @param room - The room where the lecture is inserted/removed.
	 * @param day - The day where the lecture is inserted/removed.
	 * @param period - The period where the lecture is inserted/removed.
	 * @param courseID - The course ID of the lecture that is inserted/removed.
	 * @return - The cost after the insert operation.
	 */
	public int updateCostInsert(int room, int day, int period, int courseID) {
		return updateCost(true, room, day, period, courseID);
	}
	
	/**
	 * Changes the cost calculation accordingly when a lecture is inserted into the timetable.
	 * @param room - The room where the lecture is inserted/removed.
	 * @param day - The day where the lecture is inserted/removed.
	 * @param period - The period where the lecture is inserted/removed.
	 * @param courseID - The course ID of the lecture that is inserted/removed.
	 * @return - The cost after the remove operation.
	 */
	public int updateCostRemove(int room, int day, int period, int courseID) {
		return updateCost(false, room, day, period, courseID);
	}
	
	/**
	 * Gives a textual overview of the current state of the cost-function.
	 * This text is used as header when outputting solutions to codejudge.compute.dtu.dk.
	 * @return - A text containing the counts of each penalty as well as the total cost.
	 */
	public String toString() {
		return
			"UNSCHEDULED " + countUnscheduled + "\n" +
			"ROOMCAPACITY " + countRoomCapacity + "\n" +
			"ROOMSTABILITY " + countRoomStability + "\n" +
			"MINIMUMWORKINGDAYS " + countMinimumWorkingDays + "\n" +
			"CURRICULUMCOMPACTNESS " + countCurriculumCompactness + "\n" +
			"OBJECTIVE " + costTotal + "\n";
	}
	
	/**
	 * Updates the internal representation of the variables of the cost function, as well as the total cost.
	 * @param isInsertOperation - Whether the operation is insert (or remove).
	 * @param room - The room where the lecture is inserted/removed.
	 * @param day - The day where the lecture is inserted/removed.
	 * @param period - The period where the lecture is inserted/removed.
	 * @param courseID - The course ID that is inserted/removed.
	 * @return - The total cost.
	 */
	private int updateCost(boolean isInsertOperation, int room, int day, int period, int courseID) {
		Problem.Course course = this.problem.courses.get(courseID);
		
		// Calculate penalty count deltas
		int deltaCountRoomCapacity = deltaCountRoomCapacity(isInsertOperation, room, course);
		int deltaCountCurriculumCompactness = deltaCountCurriculumCompactness(isInsertOperation, day, period, course);
		int deltaCountMinWorkDays = deltaCountMinimumWorkingDays(isInsertOperation, day, courseID);
		int deltaCountRoomStability = deltaCountRoomStability(isInsertOperation, room, courseID);
		int deltaCountUnscheduled = deltaCountUnscheduled(isInsertOperation);
		
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
		this.costTotal += deltaTotalCost;
		
		return costTotal;
	}
	
	/**
	 * Calculates the change in Unscheduled.
	 * @param insert - Whether the operation is insert (or remove).
	 * @return - The change in the number of unscheduled lectures (-1 or 1).
	 */
	private int deltaCountUnscheduled(boolean insert) {
		if (insert) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * Calculates the change in RoomStability.
	 * @param insert - Whether the operation is insert (or remove).
	 * @param room - The room where the lecture is inserted/removed.
	 * @param courseID - The course ID that is inserted/removed.
	 * @return - The number of additional rooms used after the operation (-1, 0 or 1).
	 */
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

	/**
	 * Calculates the change in MinimumWorkingDays.
	 * @param insert - Whether the operation is insert (or remove).
	 * @param day - The day where the lecture is inserted/removed.
	 * @param courseID - The course ID of the lecture that is inserted/removed.
	 * @return - The change in the number of days below minimum (-1, 0 or 1).
	 */
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

	/**
	 * Calculates the change in CurriculumCompactness.
	 * @param insert - Whether the operation is insert (or remove).
	 * @param day - The day where the lecture is inserted/removed.
	 * @param period - The period where the lecture is inserted/removed.
	 * @param course - The course course of the lecture that is inserted/removed.
	 * @return - The change in, for each curriculum, the number of lectures not being adjacent to a course with the same curriculum.
	 */
	private int deltaCountCurriculumCompactness(boolean insert, int day, int period, Problem.Course course) {
		// TODO: This is not a constant time calculation. Check how much time it uses.
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

	/**
	 * Calculates the change in RoomCapacity.
	 * @param insert - Whether the operation is insert (or remove).
	 * @param room - The room where the lecture is inserted/removed.
	 * @param course - The course of the lecture that is inserted/removed.
	 * @return - The change in the number of times a student must sit on the floor.
	 */
	private int deltaCountRoomCapacity(boolean insert, int room, Problem.Course course) {
		int studentOverflow = Math.max(0, course.noOfStudents - problem.roomCapacity.get(room));
		if (insert) {
			return studentOverflow;
		} else {
			return -studentOverflow;
		}
	}
}
