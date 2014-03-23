import java.util.Random;

/**
 * Represents a random Action i.e. insert/remove a random lecture into a random slot in the timetable.
 *
 */
public class RandomAction extends Action {

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
	public RandomAction(Problem problem, Solution solution) {
		this.randomGenerator = new Random();
		this.solution = solution;
		this.insert = randomGenerator.nextBoolean();
		this.room = randomGenerator.nextInt(problem.noOfRooms);
		this.day = randomGenerator.nextInt(problem.noOfDays);
		this.period = randomGenerator.nextInt(problem.periodsPerDay);
		this.courseID = randomGenerator.nextInt(problem.noOfCourses);
	}
	
	@Override
	/*
	 * Tries to execute the random action.
	 * @return - The total cost after the insertion/removal OR null if no change was made.
	 */
	public Integer execute() {
		if (insert) {
			return solution.insertLecture(room, day, period, courseID);
		} else {
			return solution.removeLecture(room, day, period);
		}
	}

	@Override
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
