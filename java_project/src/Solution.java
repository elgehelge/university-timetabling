import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;


public class Solution {

	final static int COST_ROOMCAPACITY			= 1;
	final static int COST_ROOMSTABILITY			= 1;
	final static int COST_CURRICULUMCOMPACTNESS	= 2;
	final static int COST_MINIMUMWORKINGDAYS	= 5;
	final static int COST_UNSCHEDULED			= 10;

	private int countTotalPenalties;
	private int countUnscheduled;
	private int countRoomCapacity;
	private int countMinimumWorkingDays;
	private int countRoomStability;
	private int countCurriculumCompactness;

	private Problem problem;
	private Integer[][][] timetable;			// room + day + period			-> course (The Timetable)
	private boolean[][][] curriculaTimeslots;	// curriculum + day + period	-> already being thought?
	private boolean[][][] lecturersTimeslots;	// lecturer + day + period		-> already teaching?
	private int[] courseUnscheduled;			// course						-> no. of unscheduled lectures
	private int[][] coursesDays;				// course + day					-> no. of lectures this day
	private int[] courseDaysBelowMinimum;		// course						-> no. of days below minimum
	private int[][] coursesRooms;				// course + room				-> no. of lectures in room
	private int[] courseNoOfRooms;				// course						-> no. of distinct rooms
	
	public Solution(Problem problem) {
		this.problem = problem;
		
		// Start with empty tables:
		this.timetable = new Integer[problem.noOfRooms][problem.noOfDays][problem.periodsPerDay];
		this.curriculaTimeslots = new boolean[problem.noOfCurricula][problem.noOfDays][problem.periodsPerDay];
		this.lecturersTimeslots = new boolean[problem.noOfLecturers][problem.noOfDays][problem.periodsPerDay];
		this.coursesDays = new int[problem.noOfCourses][problem.noOfDays];
		this.coursesRooms = new int[problem.noOfCourses][problem.noOfRooms];
		this.courseNoOfRooms = new int[problem.noOfCourses];
		
		// Initialize Unscheduled & DaysBelowMinimum
		int initialUnscheduled = 0;
		int initialDaysBelowMinimum = 0;
		this.courseUnscheduled = new int[problem.noOfCourses];
		this.courseDaysBelowMinimum = new int[problem.noOfCourses];
		Iterator<Integer> courseIDs = problem.courses.keySet().iterator();
		while (courseIDs.hasNext()) {
			int courseID = courseIDs.next();
			
			Problem.Course course = problem.courses.get(courseID);
			this.courseUnscheduled[courseID] = course.noOfLectures;
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
		this.countTotalPenalties = countUnscheduled * COST_UNSCHEDULED + countMinimumWorkingDays * COST_MINIMUMWORKINGDAYS;
	}
	
	public String toString() {
		String output = "***Schedule***" + "\n";
		for (int d = 0; d < problem.noOfDays; d++) { // loop days
			output += "\t Day " + d + "  | ";
			for (int p = 0; p < problem.periodsPerDay; p++) { // loop periods
				output += String.format("%4s", "P" + p) + " | ";
			}
		}
		output += "\n";
			for (int r = 0; r < problem.noOfRooms; r++) { // loop rooms
				output += "Room " + r;
				for (int d = 0; d < problem.noOfDays; d++) { // loop days
					output += "\t\t| ";
					for (int p = 0; p < problem.periodsPerDay; p++) { // loop periods
						output += String.format("%4d", this.timetable[r][d][p]) + " | ";
					}
				}
				output += "\n";
			}
		return output;
	}
	
	public void addRandomLecture() {
		Random generator = new Random();
		int room = generator.nextInt(problem.noOfRooms);
		int day = generator.nextInt(problem.noOfDays);
		int period = generator.nextInt(problem.periodsPerDay);
		int courseID = generator.nextInt(problem.noOfCourses);
		addLecture(room, day, period, courseID);
	}
	
	private void addLecture(int room, int day, int period, int courseID) {
		if (!isFeasible(room, day, period, courseID)) {
			//return null; TODO: Make return
		} else {
			calcDeltaCost(room, day, period, courseID); // Important that the cost is calculated before the update!
			updateSolution(room, day, period, courseID);
		}
	}
	
	private void calcDeltaCost(int room, int day, int period, int courseID) {
		// Important that the cost is calculated before the update!
		Problem.Course course = problem.courses.get(courseID);
		// RoomCapacity
		int deltaCountRoomCapacity = Math.max(0, course.noOfStudents - problem.roomCapacity.get(room));
		// CurriculumCompactness (Estimate - This is to complex to calculate!!)
		int deltaCountCurriculumCompactness = 1;
		if (isCurriculumCompact(room, day, period, courseID)) {
			deltaCountCurriculumCompactness = 0;
		}
		// MinimumWorkingDays
		int deltaCountMinWorkDays = 0;
		if (this.coursesDays[courseID][day] == 0) {
			this.courseDaysBelowMinimum[courseID] += -1;
			if (this.courseDaysBelowMinimum[courseID] >= 0) { // We improved
				deltaCountMinWorkDays = -1;
			}
		}
		// RoomStability
		int deltaCountRoomStability = 0;
		if (this.coursesRooms[courseID][room] == 0) {
			this.courseNoOfRooms[courseID] += 1;
			if (this.courseNoOfRooms[courseID] > 1) { // We used an extra room
				deltaCountRoomStability = 1;
			}
		}
		// Unscheduled
		int deltaCountUnscheduled = -1;
		// Total
		int deltaTotalCost = deltaCountCurriculumCompactness * COST_CURRICULUMCOMPACTNESS +
						 deltaCountRoomCapacity * COST_ROOMCAPACITY +
						 deltaCountRoomStability * COST_ROOMSTABILITY +
						 deltaCountMinWorkDays * COST_MINIMUMWORKINGDAYS +
						 deltaCountUnscheduled * COST_UNSCHEDULED;
		
		this.countUnscheduled += deltaCountUnscheduled;
		this.countRoomCapacity += deltaCountRoomCapacity;
		this.countRoomStability += deltaCountRoomStability;
		this.countMinimumWorkingDays += deltaCountMinWorkDays;
		this.countCurriculumCompactness += deltaCountCurriculumCompactness;
		this.countTotalPenalties += deltaTotalCost;
	}
	
	private void updateSolution(int room, int day, int period, int courseID) {
		// Important that the cost is calculated before the update!
		Problem.Course course = problem.courses.get(courseID);
		// Register in timetable
		this.timetable[room][day][period] = courseID;
		// Register lecturer
		this.lecturersTimeslots[course.lecturerID][day][period] = true;
		// Register curricula
		Iterator<Integer> curricula = course.curricula.iterator();
		while (curricula.hasNext()) {
			this.curriculaTimeslots[curricula.next()][day][period] = true;
		}
		// Register an extra lecture that day
		this.coursesDays[courseID][day] += 1;
		// Register an extra lecture in that room
		this.coursesRooms[courseID][room] += 1;
		// Register that we scheduled a lecture
		this.courseUnscheduled[courseID] += -1;
	}
	
	private boolean isFeasible(int room, int day, int period, int courseID) {
		Problem.Course course = problem.courses.get(courseID);
		// Is there another course scheduled in the same room and same timeslot?
		if (this.timetable[room][day][period] != null) {
			return false;
		}
		// Is all lectures already scheduled?
		if (this.courseUnscheduled[courseID] == 0) {
			return false;
		}
		// Is the course available?
		if (course.unavailability.contains(problem.calcTimeslotID(day, period))) {
			return false;
		}
		// No courses with the same lecturer at this timeslot?
		if (this.lecturersTimeslots[course.lecturerID][day][period]) {
			return false;
		}
		// No courses with the same curriculum at this timeslot?
		// NB! This is not constant time (but close) since we need to check for each curriculum.
		Iterator<Integer> curricula = course.curricula.iterator();
		while (curricula.hasNext()) {
			if (this.curriculaTimeslots[curricula.next()][day][period]) {
				return false;
			}
		}
		return true;
	}
	
	private void removeRandomLecture(int room, int timeslot) {
		// TODO: Implement
		// TODO: should return delta-cost & courseID
	}
	
	private boolean isCurriculumCompact(int room, int day, int period, int courseID) {
		Iterator<Integer> curricula = problem.courses.get(courseID).curricula.iterator();
		while (curricula.hasNext()) {
			int curriculum = curricula.next();
			for (int adjPeriod : new int[]{period-1, period+1}) {
				if (adjPeriod > 0 && adjPeriod < problem.periodsPerDay) {
					if (this.curriculaTimeslots[curriculum][day][adjPeriod]) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public String codeJudgeOutput() {
		String output = "";
		output += "UNSCHEDULED " + countUnscheduled + "\n";
		output += "ROOMCAPACITY " + countRoomCapacity + "\n";
		output += "ROOMSTABILITY " + countRoomStability + "\n";
		output += "MINIMUMWORKINGDAYS " + countMinimumWorkingDays + "\n";
		output += "CURRICULUMCOMPACTNESS " + countCurriculumCompactness + "\n";
		// TODO: RoomStability
		output += "OBJECTIVE " + countTotalPenalties + "\n";
		for (int r = 0; r < problem.noOfRooms; r++) { // loop rooms
			for (int d = 0; d < problem.noOfDays; d++) { // loop days
				for (int p = 0; p < problem.periodsPerDay; p++) { // loop periods
					if (this.timetable[r][d][p] != null) {
						output += String.format("C%04d %d %d R%04d", this.timetable[r][d][p], d, p, r) + "\n";
					}
				}
			}
		}
		return output;
	}
}
