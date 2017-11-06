/*
 * Person.java
 * This data structure stores details about each data record - Person. 
 * It stores its gender, weigh and height as a single binary value as explained below for each variable
 * 
 */
public class Person {
	
	private int gender; // gender - either male or female. 1 - male, 0 - female
	private int weight; //weight - either greater than 130 lbs marked as 1, marked as 0 if lesser than 130
	private int height; //height - marked as 0 for lesser than 55 inch, and marked as 1 for greater than 55 inch
	private double prob; //probability of this data. One indicates this data is certain because it comes from the input file. P(Gender = male)
	private boolean complete; //is data in this instance complete 
	
	//constructor
	public Person (int g, int w, int h, double p, boolean c){
		gender = g;
		weight = w;
		height = h;
		prob = p;
		complete = c;
	}
	
	//accessors
	public int getGender() { return gender; }
	public int getWeight() { return weight; }
	public int getHeight() { return height; }
	public double getProb() { return prob; }
	public boolean isDataComplete() { return complete; }
	
	//mutators
	public void setProb( double p ) { prob = p; }
	
	
}
