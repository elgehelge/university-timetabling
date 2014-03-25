import java.util.Random;

/**
 * Represents a random Action i.e. insert/remove a random lecture into a random slot in the timetable.
 *
 */
public class RandomAction implements Action {

	private boolean insert;
	private int room;
	private int day;
	private int period;
	private int courseID;
	private Random randomGenerator;
	private Solution solution;
	
	/*
	 * Constructor.
	 */
	public RandomAction(Solution solution) {
		this.randomGenerator = new Random();
		this.solution = solution;
		this.insert = randomGenerator.nextBoolean();
		this.room = randomGenerator.nextInt(solution.problem.noOfRooms);
		this.day = randomGenerator.nextInt(solution.problem.noOfDays);
		this.period = randomGenerator.nextInt(solution.problem.periodsPerDay);
		this.courseID = randomGenerator.nextInt(solution.problem.noOfCourses);
	}
	
	/*
	 * Tries to execute the random action.
	 * @return - The total cost after the insertion/removal OR null if no change was made.
	 */
	public Integer execute() {
		if (insert) {
			return solution.insertLecture(room, day, period, courseID);
		} else {
			if (this.solution.getCourse(room, day, period) != null) {
				this.courseID = this.solution.getCourse(room, day, period);
			}
			return solution.removeLecture(room, day, period);
		}
	}

	/*
	 * Tries to revert the random action.
	 * @return - The total cost after the insertion/removal OR null if no change was made.
	 */
	public Integer revert() {
		this.insert = !this.insert; // flip
		Integer cost = execute();
		this.insert = !this.insert; // flip back
		return cost;
	}
}
