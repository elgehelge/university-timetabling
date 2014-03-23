import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Represents a problem instance.
 * Is used for reading in the problem from the data and looking up this info.
 *
 */
public class Problem {
	
	public int noOfCourses,
			   noOfRooms,
			   noOfDays,
			   periodsPerDay,
			   noOfCurricula,
			   noOfConstraints,
			   noOfLecturers;
	public HashMap<Integer, Course> courses;						// courseID -> course object
	public HashMap<Integer, Integer> roomCapacity;					// roomID -> no. of seats
	
    /**
     * Constructor.
     * @param dataLocation - The path where the problem data files are located.
     * @param args
     * @throws IOException
     */
    public Problem(String dataLocation, String[] args) throws IOException
    {
    	this.courses = new HashMap<Integer, Course>();
    	this.roomCapacity = new HashMap<Integer, Integer>();
    	// Structure of args:
    	// (0)basic.utt (1)courses.utt (2)lecturers.utt (3)rooms.utt (4)curricula.utt (5)relation.utt (6)unavailability.utt (7)60
        readBasic(dataLocation, args[0]);
        readCourses(dataLocation, args[1]);
        readRooms(dataLocation, args[3]);
        readRelations(dataLocation, args[5]);
        readUnavailabilities(dataLocation, args[6]);
    }
    
    /**
     * Converts day and period to timeslotID.
     * @param day
     * @param periode
     * @return - A unique timeslot ID
     */
    public int calcTimeslotID(int day, int periode) {
    	return day * this.periodsPerDay + periode;
    }
    
	/**
	 * Reads information from the basic.utt file.
	 * @param DataLocation
	 * @param fileName
	 * @throws IOException
	 */
	private void readBasic(String DataLocation, String fileName) throws IOException {
		// File structure:
		// Courses Rooms Days Periods_per_day Curricula Constraints Lecturers
		InputReader reader = new InputReader(DataLocation + fileName);
		this.noOfCourses = reader.nextNumber();
		this.noOfRooms = reader.nextNumber();
		this.noOfDays = reader.nextNumber();
		this.periodsPerDay = reader.nextNumber();
		this.noOfCurricula = reader.nextNumber();
		this.noOfConstraints = reader.nextNumber();
		this.noOfLecturers = reader.nextNumber();
	}
	
	/**
	 * Reads information from the courses.utt file.
	 * @param DataLocation
	 * @param fileName
	 * @throws IOException
	 */
	private void readCourses(String DataLocation, String fileName) throws IOException {
		// File structure:
		// CourseID LecturerID Number_of_lectures Minimum_working_days Number_of_students
		InputReader reader = new InputReader(DataLocation + fileName);
		boolean reachedEndOfFile = false;
		while(!reachedEndOfFile) {
			int courseID = reader.nextNumber();
			int lecturerID = reader.nextNumber();
			int noOfLectures = reader.nextNumber();
			int minWorkDays = reader.nextNumber();
			int noOfStudents = reader.nextNumber();
			if (courseID != -1) {
				Course currentCourse = new Course(courseID, lecturerID, noOfLectures, minWorkDays, noOfStudents);
				this.courses.put(courseID, currentCourse);
			} else {
				reachedEndOfFile = true;
			}
		}
	}
	
	/**
	 * Reads information from the rooms.utt file.
	 * @param DataLocation
	 * @param fileName
	 * @throws IOException
	 */
	private void readRooms(String DataLocation, String fileName) throws IOException {
		// File structure:
		// RoomID Capacity
		InputReader reader = new InputReader(DataLocation + fileName);
		boolean reachedEndOfFile = false;
		while(!reachedEndOfFile) {
			int roomID = reader.nextNumber();
			int capacity = reader.nextNumber();
			if (roomID != -1) {
				this.roomCapacity.put(roomID, capacity);
			} else {
				reachedEndOfFile = true;
			}
		}
	}
	
	/**
	 * Reads information from the relation.utt file.
	 * @param DataLocation
	 * @param fileName
	 * @throws IOException
	 */
	private void readRelations(String DataLocation, String fileName) throws IOException {
		// File structure:
		// CurriculumID CourseID
		InputReader reader = new InputReader(DataLocation + fileName);
		boolean reachedEndOfFile = false;
		while(!reachedEndOfFile) {
			int curriculumID = reader.nextNumber();
			int courseID = reader.nextNumber();
			if (curriculumID != -1) {
				this.courses.get(courseID).curricula.add(curriculumID);
			} else {
				reachedEndOfFile = true;
			}
		}
	}
	
	/**
	 * Reads information from the unavailability.utt file.
	 * @param DataLocation
	 * @param fileName
	 * @throws IOException
	 */
	private void readUnavailabilities(String DataLocation, String fileName) throws IOException {
		// File structure:
		// Course Day Period
		InputReader reader = new InputReader(DataLocation + fileName);
		boolean reachedEndOfFile = false;
		while(!reachedEndOfFile) {
			int courseID = reader.nextNumber();
			int day = reader.nextNumber();
			int period = reader.nextNumber();
			if (courseID != -1) {
				this.courses.get(courseID).unavailability.add(calcTimeslotID(day, period));
			} else {
				reachedEndOfFile = true;
			}
		}
	}
	
	/**
	 * Creates a textual representation of the problem instance.
	 * @return A textual representation of the problem instance.
	 */
    public String toString() {
    	String description =
    		"No. of courses: " + this.noOfCourses + "\n" +
    		"No. of rooms: " + this.noOfRooms + "\n" +
    		"No. of days: " + this.noOfDays + "\n" +
    		"Periods per day: " + this.periodsPerDay + "\n" +
    		"No. of curricula: " + this.noOfCurricula + "\n" +
    		"No. of constraints: " + this.noOfConstraints + "\n" +
    		"No. of lectures: " + this.noOfLecturers + "\n" +
    		"Rooms:" + "\t" + this.roomCapacity + "\n" +
    		"Courses:" + "\n" + this.courses.toString().replaceAll("(?m)^", "\t");
    	return
    		"Problem instance:" +
    		"\n" +
    		description.replaceAll("(?m)^", "\t") + // indenting description
    		"\n";
    }

    /**
     * Helper class for easy reading input files.
     * @author The methods are authored Martin Lundberg-Jensen.
     *
     */
	public class InputReader {
		
		private StreamTokenizer streamreader; // stream reader
		
		/**
	     * Constructor.
	     * Tries to open the selected file.
		 * @param filename
		 * @throws IOException
		 */
	    public InputReader(String filename) throws IOException
	    {
	        try{ 
	        	BufferedReader temp = new BufferedReader(new FileReader(filename));
	        	temp.readLine(); // Removes first line in the file
	        	streamreader = new StreamTokenizer(temp);
	        	
	        	}
	        catch(FileNotFoundException e)
		    {
			System.err.println("File \"" + filename + "\" not found");
			System.exit(0);
		    }
	    }
	
	    /**
	     * Returns the next number in the data file.
	     * @return
	     * @throws IOException
	     */
	    private int nextNumber() throws IOException {
	    	try{ streamreader.nextToken();
	    	} catch(IOException e) {
				System.err.println("Error: Failed to get next number. Is the file open?");
				System.exit(-1);
	    	}
	    	if(streamreader.ttype == StreamTokenizer.TT_EOF) {
	    		return -1;
	    	}
			if(streamreader.ttype != StreamTokenizer.TT_NUMBER){
				//Takes the substring from L0001, to be 0001, so we have an ID
				String temp = streamreader.toString().substring(7, 11);
				return Integer.parseInt(temp);
			}
			return (int) streamreader.nval;
		}
	}
    
    /**
     * Represents a course instance.
     * This is a data structure used for storing course info.
     *
     */
    public class Course {
    	public int courseID, lecturerID, noOfLectures, minWorkDays, noOfStudents;
    	public HashSet<Integer> curricula = new HashSet<Integer>();			// part of given curriculum?
    	public HashSet<Integer> unavailability = new HashSet<Integer>();	// available?
    	
    	/**
    	 * Constructor.
    	 * @param courseID
    	 * @param lecturerID
    	 * @param noOfLectures
    	 * @param minWorkDays
    	 * @param noOfStudents
    	 */
    	public Course(int courseID, int lecturerID, int noOfLectures, int minWorkDays, int noOfStudents) {
    		this.courseID = courseID;
    		this.lecturerID = lecturerID;
    		this.noOfLectures = noOfLectures;
    		this.minWorkDays = minWorkDays;
    		this.noOfStudents = noOfStudents;
    	}
    	
        /**
         * Creates a textual representation of the course instance.
         * @return A textual representation of the course instance.
         */
    	public String toString() {
        	String description =
        		"Course ID: " + this.courseID + "\n" +
        		"Lecturer ID: " + this.lecturerID + "\n" +
        		"No. of lectures: " + this.noOfLectures + "\n" +
        		"Min. work days: " + this.minWorkDays + "\n" +
        		"No. of students: " + this.noOfStudents + "\n" +
        		"Curricula: " + curricula + "\n" +
        		"Unavailable timeslots: " + unavailability;
        	return
        		"Course :" + "\n" +
        		description.replaceAll("(?m)^", "\t") + // indenting description
        		"\n";
    	}
    	
    }
}