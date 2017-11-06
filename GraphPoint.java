/*
 * GraphPoint.java
 * This data structure stores values of nunber of iteration and its respective log likelihood
 * 
 * 
 */
public class GraphPoint {
	private int iterNum;
	private double LogLikelihood;
	
	public GraphPoint(int i, double ll){
		iterNum = i;
		LogLikelihood = ll;
	}
	
	public int getIterNum() { return iterNum;}
	public double getLogLikelihood() {return LogLikelihood;}

}
