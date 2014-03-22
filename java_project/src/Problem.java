import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.HashSet;

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
	
	private StreamTokenizer streamreader; //The streamreader
	
    /**
     * Constructor of the class Problem
     * @param Datafile - The name and path of the file to be loaded
     * @throws java.io.IOException
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
     * Converts day and periode to timeslotID
     * @return timeslotID
     */
    public int calcTimeslotID(int day, int periode) {
    	return day * this.periodsPerDay + periode;
    }
    
	/**
     * Reads information from the basic.utt file
     * @param fileName
     * @throws IOException
     */
	private void readBasic(String DataLocation, String fileName) throws IOException {
		// File structure:
		// Courses Rooms Days Periods_per_day Curricula Constraints Lecturers
		openFile(DataLocation + fileName);
		this.noOfCourses = nextNumber();
		this.noOfRooms = nextNumber();
		this.noOfDays = nextNumber();
		this.periodsPerDay = nextNumber();
		this.noOfCurricula = nextNumber();
		this.noOfConstraints = nextNumber();
		this.noOfLecturers = nextNumber();
	}
	
	/**
     * Reads information from the courses.utt file
     * @param fileName
     * @throws IOException
     */
	private void readCourses(String DataLocation, String fileName) throws IOException {
		// File structure:
		// CourseID LecturerID Number_of_lectures Minimum_working_days Number_of_students
		openFile(DataLocation + fileName);
		boolean reachedEndOfFile = false;
		while(!reachedEndOfFile) {
			int courseID = nextNumber();
			int lecturerID = nextNumber();
			int noOfLectures = nextNumber();
			int minWorkDays = nextNumber();
			int noOfStudents = nextNumber();
			if (courseID != -1) {
				Course currentCourse = new Course(courseID, lecturerID, noOfLectures, minWorkDays, noOfStudents);
				this.courses.put(courseID, currentCourse);
			} else {
				reachedEndOfFile = true;
			}
		}
	}
	
	/**
     * Reads information from the rooms.utt file
     * @param fileName
     * @throws IOException
     */
	private void readRooms(String DataLocation, String fileName) throws IOException {
		// File structure:
		// RoomID Capacity
		openFile(DataLocation + fileName);
		boolean reachedEndOfFile = false;
		while(!reachedEndOfFile) {
			int roomID = nextNumber();
			int capacity = nextNumber();
			if (roomID != -1) {
				this.roomCapacity.put(roomID, capacity);
			} else {
				reachedEndOfFile = true;
			}
		}
	}
	
	/**
     * Reads information from the relation.utt file
     * @param fileName
     * @throws IOException
     */
	private void readRelations(String DataLocation, String fileName) throws IOException {
		// File structure:
		// CurriculumID CourseID
		openFile(DataLocation + fileName);
		boolean reachedEndOfFile = false;
		while(!reachedEndOfFile) {
			int curriculumID = nextNumber();
			int courseID = nextNumber();
			if (curriculumID != -1) {
				this.courses.get(courseID).curricula.add(curriculumID);
			} else {
				reachedEndOfFile = true;
			}
		}
	}
	
	/**
     * Reads information from the unavailability.utt file
     * @param fileName
     * @throws IOException
     */
	private void readUnavailabilities(String DataLocation, String fileName) throws IOException {
		// File structure:
		// Course Day Period
		openFile(DataLocation + fileName);
		boolean reachedEndOfFile = false;
		while(!reachedEndOfFile) {
			int courseID = nextNumber();
			int day = nextNumber();
			int period = nextNumber();
			if (courseID != -1) {
				this.courses.get(courseID).unavailability.add(calcTimeslotID(day, period));
			} else {
				reachedEndOfFile = true;
			}
		}
	}

	/**
     * Tries to open the selected file
     * @param filename
	 * @throws IOException 
     */
    private void openFile(String filename) throws IOException
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
     * Used for loading the solution
     * @return Next number in datafile
     * @throws java.io.IOException
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
    
    /**
     * Returns textual representation of the problem instance
     * @return String
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
    
    public class Course {
    	public int courseID, lecturerID, noOfLectures, minWorkDays, noOfStudents;
    	public HashSet<Integer> curricula = new HashSet<Integer>();			// part of given curriculum?
    	public HashSet<Integer> unavailability = new HashSet<Integer>();	// available?
    	
    	public Course(int courseID, int lecturerID, int noOfLectures, int minWorkDays, int noOfStudents) {
    		this.courseID = courseID;
    		this.lecturerID = lecturerID;
    		this.noOfLectures = noOfLectures;
    		this.minWorkDays = minWorkDays;
    		this.noOfStudents = noOfStudents;
    	}
    	
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