import java.util.Iterator;
import java.util.Random;


public class Solution {

	private Problem problem;
	
	private Integer[][][] timetable;			// room + day + period			-> course (The Timetable)
	private int[] unscheduled;					// course						-> no. of unscheduled lectures
	
	// Bookkeeping of hard constraints
	private boolean[][][] curriculaTimeslots;	// curriculum + day + period	-> already being thought?
	private boolean[][][] lecturersTimeslots;	// lecturer + day + period		-> already teaching?
	
	private CostEstimator costEstimator;
	
	public Solution(Problem problem) {
		this.problem = problem;
		this.costEstimator = new CostEstimator(problem, this);
		
		// Start with empty tables:
		this.timetable = new Integer[problem.noOfRooms][problem.noOfDays][problem.periodsPerDay];
		this.curriculaTimeslots = new boolean[problem.noOfCurricula][problem.noOfDays][problem.periodsPerDay];
		this.lecturersTimeslots = new boolean[problem.noOfLecturers][problem.noOfDays][problem.periodsPerDay];
		
		this.unscheduled = new int[problem.noOfCourses];
		Iterator<Integer> courseIDs = problem.courses.keySet().iterator();
		while (courseIDs.hasNext()) {
			int courseID = courseIDs.next();
			Problem.Course course = problem.courses.get(courseID);
			this.unscheduled[courseID] = course.noOfLectures;
		}
	}
	
	public String toString() {
		String output = "***Schedule***" + "\n";
		for (int d = 0; d < this.problem.noOfDays; d++) { // loop days
			output += "\t Day " + d + "  | ";
			for (int p = 0; p < this.problem.periodsPerDay; p++) { // loop periods
				output += String.format("%4s", "P" + p) + " | ";
			}
		}
		output += "\n";
			for (int r = 0; r < this.problem.noOfRooms; r++) { // loop rooms
				output += "Room " + r;
				for (int d = 0; d < this.problem.noOfDays; d++) { // loop days
					output += "\t\t| ";
					for (int p = 0; p < this.problem.periodsPerDay; p++) { // loop periods
						output += String.format("%4d", this.timetable[r][d][p]) + " | ";
					}
				}
				output += "\n";
			}
		return output;
	}
	
	public void addRandomLecture() {
		Random generator = new Random();
		int room = generator.nextInt(this.problem.noOfRooms);
		int day = generator.nextInt(this.problem.noOfDays);
		int period = generator.nextInt(this.problem.periodsPerDay);
		int courseID = generator.nextInt(this.problem.noOfCourses);
		addLecture(room, day, period, courseID);
	}
	
	private void addLecture(int room, int day, int period, int courseID) {
		if (!isFeasible(room, day, period, courseID)) {
			//return null; TODO: Make return
		} else {
			costEstimator.calcDeltaCost(room, day, period, courseID);
			updateSolution(room, day, period, courseID);
		}
	}
	
	
	private void updateSolution(int room, int day, int period, int courseID) {
		Problem.Course course = this.problem.courses.get(courseID);
		// Register in timetable
		this.timetable[room][day][period] = courseID;
		// Register lecturer
		this.lecturersTimeslots[course.lecturerID][day][period] = true;
		// Register curricula
		Iterator<Integer> curricula = course.curricula.iterator();
		while (curricula.hasNext()) {
			this.curriculaTimeslots[curricula.next()][day][period] = true;
		}
		// Register that we scheduled a lecture
		this.unscheduled[courseID] += -1;
	}
	
	private boolean isFeasible(int room, int day, int period, int courseID) {
		Problem.Course course = this.problem.courses.get(courseID);
		// Is there another course scheduled in the same room and same timeslot?
		if (this.timetable[room][day][period] != null) {
			return false;
		}
		// Is all lectures already scheduled?
		if (this.unscheduled[courseID] == 0) {
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
	
	//private void removeRandomLecture(int room, int timeslot) {
	//	// TODO: Implement
	//	// TODO: should return delta-cost & courseID
	//}
	
	public boolean getCurriculaTimeslots(int curriculum, int day, int adjPeriod) {
		return this.curriculaTimeslots[curriculum][day][adjPeriod];
	}
	
	public String codeJudgeOutput() {
		String output = this.costEstimator.codeJudgeHeader();
		for (int r = 0; r < this.problem.noOfRooms; r++) { // loop rooms
			for (int d = 0; d < this.problem.noOfDays; d++) { // loop days
				for (int p = 0; p < this.problem.periodsPerDay; p++) { // loop periods
					if (this.timetable[r][d][p] != null) {
						output += String.format("C%04d %d %d R%04d", this.timetable[r][d][p], d, p, r) + "\n";
					}
				}
			}
		}
		return output;
	}
}
