import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;


public class Solution {

	final static int PENALTY_ROOMCAPACITY			= 1;
	final static int PENALTY_CURRICULUMCOMPACTNESS	= 2;
	final static int PENALTY_MINIMUMWORKINGDAYS		= 5;
	final static int PENALTY_UNSCHEDULED			= 10;

	private int costTotal;
	private int costUnscheduled;
	private int costRoomCapacity;
	private int costMinimumWorkingDays;
	private int costCurriculumCompactness;

	private Problem problem;
	private Integer[][][] timetable;			// room + day + period			-> course (The Timetable)
	private boolean[][][] curriculaTimeslots;	// curriculum + day + period	-> already being thought?
	private boolean[][][] lecturersTimeslots;	// lecturer + day + period		-> already teaching?
	private int[][] coursesDays;				// course + day					-> no. of lectures this day
	private int[] courseDaysBelowMinimum;		// course						-> no. of days below minimum
	private int[] courseUnscheduled;			// course						-> no. of unscheduled lectures
	
	public Solution(Problem problem) {
		this.problem = problem;
		
		// Start with empty tables:
		this.timetable = new Integer[problem.noOfRooms][problem.noOfDays][problem.periodsPerDay];
		this.curriculaTimeslots = new boolean[problem.noOfCurricula][problem.noOfDays][problem.periodsPerDay];
		this.lecturersTimeslots = new boolean[problem.noOfLecturers][problem.noOfDays][problem.periodsPerDay];
		this.coursesDays = new int[problem.noOfCourses][problem.noOfDays];
		
		// Initialize Unscheduled & DaysBelowMinimum
		int totalUnscheduled = 0;
		int totalDaysBelowMinimum = 0;
		this.courseUnscheduled = new int[problem.noOfCourses];
		this.courseDaysBelowMinimum = new int[problem.noOfCourses];
		Iterator<Integer> courseIDs = problem.courses.keySet().iterator();
		while (courseIDs.hasNext()) {
			int courseID = courseIDs.next();
			Problem.Course course = problem.courses.get(courseID);
			this.courseUnscheduled[courseID] = course.noOfLectures;
			totalUnscheduled += course.noOfLectures;
			this.courseDaysBelowMinimum[courseID] = course.minWorkDays;
			totalDaysBelowMinimum += course.minWorkDays;
		}
		
		// Initialize cost
		this.costCurriculumCompactness = 0;
		this.costRoomCapacity = 0;
		this.costUnscheduled = totalUnscheduled * PENALTY_UNSCHEDULED;
		this.costMinimumWorkingDays = totalDaysBelowMinimum * PENALTY_MINIMUMWORKINGDAYS;
		this.costTotal = costUnscheduled + costMinimumWorkingDays;
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
	
	private void addLecture(int roomID, int day, int period, int courseID) {
		if (!isFeasible(roomID, day, period, courseID)) {
			//return null; TODO: Make return
		} else {
			calcDeltaCost(roomID, day, period, courseID);
			updateSolution(roomID, day, period, courseID);
		}
	}
	
	private void calcDeltaCost(int roomID, int day, int period, int courseID) {
		Problem.Course course = problem.courses.get(courseID);
		// RoomCapacity
		int delta_cost_roomCapacity = Math.max(0, course.noOfStudents - problem.roomCapacity.get(roomID));
		// CurriculumCompactness (TODO: Maybe this one is to complex to calculate?)
		int delta_cost_curriculumCompactness = PENALTY_CURRICULUMCOMPACTNESS;
		Iterator<Integer> curricula = course.curricula.iterator();
		while (curricula.hasNext()) {
			int curriculum = curricula.next();
			for (int adjPeriod : new int[]{period-1, period+1}) {
				if (adjPeriod > 0 && adjPeriod < problem.periodsPerDay) {
					if (this.curriculaTimeslots[curriculum][day][adjPeriod]) {
						delta_cost_curriculumCompactness = 0;
					}
				}
			}
		}
		// MinimumWorkingDays
		int delta_cost_minWorkDays = 0;
		if (this.coursesDays[courseID][day] == 0) {
			this.courseDaysBelowMinimum[courseID] += -1;
			if (this.courseDaysBelowMinimum[courseID] >= 0) { // We improved
				delta_cost_minWorkDays = -PENALTY_MINIMUMWORKINGDAYS;
			}
		}
		// Unscheduled
		int delta_cost_unscheduled = -PENALTY_UNSCHEDULED;
		// Total
		int delta_cost = delta_cost_curriculumCompactness +
						 delta_cost_roomCapacity +
						 delta_cost_minWorkDays +
						 delta_cost_unscheduled;
		
		this.costUnscheduled += delta_cost_unscheduled;
		this.costRoomCapacity += delta_cost_roomCapacity;
		this.costMinimumWorkingDays += delta_cost_minWorkDays;
		this.costCurriculumCompactness += delta_cost_curriculumCompactness;
		this.costTotal += delta_cost;
	}
	
	private void updateSolution(int roomID, int day, int period, int courseID) {
		Problem.Course course = problem.courses.get(courseID);
		// Register in timetable
		this.timetable[roomID][day][period] = courseID;
		// Register lecturer
		this.lecturersTimeslots[course.lecturerID][day][period] = true;
		// Register curricula
		Iterator<Integer> curricula = course.curricula.iterator();
		while (curricula.hasNext()) {
			this.curriculaTimeslots[curricula.next()][day][period] = true;
		}
		// Register an extra lecture that day
		this.coursesDays[courseID][day] += 1;
		// Register that we scheduled a lecture
		this.courseUnscheduled[courseID] += -1;
	}
	
	private boolean isFeasible(int roomID, int day, int period, int courseID) {
		Problem.Course course = problem.courses.get(courseID);
		// Is there another course scheduled in the same room and same timeslot?
		if (this.timetable[roomID][day][period] != null) {
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
	
	public String codeJudgeOutput() {
		String output = "";
		output += "UNSCHEDULED " + costUnscheduled + "\n";
		output += "ROOMCAPACITY " + costRoomCapacity + "\n";
		output += "MINIMUMWORKINGDAYS " + costMinimumWorkingDays + "\n";
		output += "CURRICULUMCOMPACTNESS " + costCurriculumCompactness + "\n";
		// TODO: RoomStability
		output += "OBJECTIVE " + costTotal + "\n";
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
