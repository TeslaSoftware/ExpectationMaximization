/*
 * EMAlgorithm
 * This class uses the Expectation Maximization Algorithm to estimate parameters from missing data
 * Class takes 6 arguments for constructor. First one is the path to input file, 
 * remaining 5 are parameters as explained in the comment above constructor
 * 
 * 
 */



import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;


public class EMAlgorithm {
	
	private LinkedList<Person> dataSet;
	private double condParams[][]; //variable to store conditional parameters 
	private double params[]; //variable to store parameter - Probability of Gender
	private double epsilon; //Threshold of convergence
	private double expectedCounts[][][]; //Expected counts for [Gender][Weight][Height]
	private int realRecords, totalRecords; //counts of data records from the input file, real records represents known data only while total records includes real records + augmented records
	
	/*@param
	 * Constructor accepts the following parameters
	 * path -> path to the input file to read from
	 * POM -> Prob(Gender = Male)
	 * POHGM -> Prob(Weight = greater_than_130 | Gender = Male)
	 * POHGF -> Prob(Weight = greater_than_130 | Gender = Female)
	 * POTGM -> Prob(Height = greater_than_55 | Gender = Male)
	 * POTGF -> Prob(Height = greater_than_55 | Gender = Female)
	 * e -> epsilon, this is threshold for convergence 
	 */
	
	//constructor
	EMAlgorithm(String path,double POM, double POHGM, double POHGF, double POTGM, double POTGF, double e ){
		condParams = new double[4][2]; //variable stores conditional parameters as follows [Weight or Height][Gender]
		//where first index as following values 0-weight is lesser than 130, 1 - weight is greater than 130, 2- height is lesser than 55, 3 - height is larger than 55
		params = new double[2]; //params[0] - male, params[1] - female
		expectedCounts = new double[2][2][2]; //Expected counts for [Gender][Weight][Height]
		//setting all the probabilities
		params[0] = POM; // P(Gender = Male)
		params[1] = 1- POM; // P(Gender = Female)
		condParams[1][0] = POHGM; // P(Weight = greater than 130 | Gender = Male)
		condParams[0][0] = 1.0-POHGM; // P(Weight = lesser than 130 | Gender = Male)
		condParams[1][1] = POHGF; // P(Weight = greater than 130 | Gender = Female)
		condParams[0][1] = 1.0 - POHGF; // P(Weight = lesser than 130 | Gender = Female)
		condParams[3][0] = POTGM; // P(Height = greater than 55 | Gender = Male)
		condParams[2][0] = 1.0 - POTGM; // P(Height = lesser than 55 | Gender = Male)
		condParams[3][1] =  POTGF; // P(Height = greater than 55 | Gender = Female)
		condParams[2][1] = 1.0 - POTGF;	// P(Height = lesser than 55 | Gender = Female)
		epsilon = e; //Threshold
		realRecords =0;
		totalRecords = 0;
		//initializing data set and loading data to it
		dataSet = new LinkedList<Person>();
		loadData(path);
	}
	
	//this method runs the EM algorithm - both Expectation and Maximization steps as well as compare log Likelihood after each iteration until convergence to epsilon
	public void run(){
		double currentLogLikelihood, prevLogLikelihood; //variables to keep track of likelihood change to find when EM algorithm converges
		currentLogLikelihood = 0;
		int iteration = 0;
		LinkedList<GraphPoint> graphData = new LinkedList<GraphPoint>();
		//iterate until convergence - difference between log likelihood is smaller than epsilon
		do{
			iteration++;
			prevLogLikelihood = currentLogLikelihood;
			currentLogLikelihood = computeLogLikelihood();		
			//Expecation step
			Estep();
			//Maximization step
			Mstep();			
			System.out.println("\n\n==============================");
			System.out.println("Iteration #" + iteration);
			//add value from each iteration to graphData for displaying when algorithm has completed
			graphData.addLast(new GraphPoint(iteration,currentLogLikelihood ));
			displayParameters(currentLogLikelihood);
		}while (Math.abs(currentLogLikelihood - prevLogLikelihood ) > epsilon);
		//Display data for graph plotting (log likelihood vs iterations for each iteration)
		DisplayGraphStats(graphData);
		
	}
	
	
	
	//Calculating P(Gender = Male | Weight, Height)
	private void Estep(){
		double [][][] thetas= calculateTheta();
		calculateCounts(thetas);
	}
	
	
	//this method calculates theta values using P(Gender = x| Weight = y, Height = z)
	private double[][][] calculateTheta(){
		double [][][] thetas = new double[2][2][2];
		//P(G=x|W=y,H=z)
		for(int x = 0; x <= 1; x ++){
			for (int y =0; y <= 1; y++){
				for(int z = 0; z <= 1; z ++){
					thetas[x][y][z] = (condParams[y][x] * condParams[z+2][x] * params[x])/
							((condParams[y][x] * condParams[z+2][x] * params[x]) + (condParams[y][(x+1) % 2] * condParams[z+2][(x+1) % 2] * params[(x+1) % 2]));

				}
			}					
		}
		
		return thetas;		
	}
	
	//This method calculate counts for each data point
	private void calculateCounts(double [][][] thetas){
		//Reset Expected Counts
		resetCounts();
		//Go through list of data
		Iterator<Person> dataIter = dataSet.iterator();
		while(dataIter.hasNext()){
			Person person = dataIter.next();
			//update newly calculated probability for augmented data (data that was unknown and was augmented)
			if(!person.isDataComplete()){
				double probabilityData = thetas[person.getGender()][person.getWeight()][person.getHeight()];
				person.setProb(probabilityData);
			}
			//add to expected count probability of a given data that is applicable to Person instance which is currently checked
			expectedCounts[person.getGender()][person.getWeight()][person.getHeight()] += person.getProb();
		}	
	}
	
	//this is helper method to reset to 0 each value in 3-dimensional array
	private void resetCounts(){
		for(int x = 0; x <2; x++){
			for(int y = 0; y <2; y++){
				for(int z = 0; z <2 ; z++){
					expectedCounts[x][y][z] = 0;
				}
			}
		}
	}
	
	//Computing maximization of parameters given counts for each probability 
	private void Mstep (){
		//updating parameter Gender
		for( int i = 0; i <2; i++)
		params[i] = (expectedCounts[i][0][0] + expectedCounts[i][0][1] + expectedCounts[i][1][0] + expectedCounts[i][1][1])/ ((double) realRecords);
	
		//updating conditional parameters using estimated counts
		for(int x = 0; x <2 ;x++){
			double countPOX = (expectedCounts[x][0][0] + expectedCounts[x][0][1] + expectedCounts[x][1][0] + expectedCounts[x][1][1]); //Sum of all counts of P(Gender = x)
			condParams[0][x] = (expectedCounts[x][0][0] + expectedCounts[x][0][1]) / countPOX; // #P(Weight = 0 | Gender = x)/#P(Gender = x)
			condParams[1][x] = (expectedCounts[x][1][0] + expectedCounts[x][1][1]) / countPOX; // #P(Weight = 1 | Gender = x)/#P(Gender = x)
			condParams[2][x] = (expectedCounts[x][0][0] + expectedCounts[x][1][0]) / countPOX; // #P(Height = 0 | Gender = x)/#P(Gender = x)
			condParams[3][x] = (expectedCounts[x][0][1] + expectedCounts[x][1][1]) / countPOX; // #P(Height = 1 | Gender = x)/#P(Gender = x)
		}		
	}
	
	private double computeLogLikelihood(){
		double result = 0;

		//iterate over the data set
		Iterator <Person> iterP = dataSet.iterator();
		while(iterP.hasNext()){
			Person person = iterP.next();
			int g, w, h;
			g = person.getGender();
			w = person.getWeight();
			h = person.getHeight();
			//for each person check if this is the complete data and use the correct formula for calculating log likelihood
			if(person.isDataComplete()){
				result+=Math.log(condParams[w][g] * condParams[h+2][g] * params[g]);
			}
			else{
				if(g == 0){
					//if data has been augmented then we need to use the law of total probability
					result+=Math.log((condParams[w][0] * condParams[h+2][0] * params[0]) + (condParams[w][1] * condParams[h+2][1] * params[1]));
				}
			}			
		}	
		return result;
	}
	
	private void displayParameters(double logLikelihood){
		DecimalFormat df = new DecimalFormat("#0.00000");
		System.out.println("Current log likelihood: " + logLikelihood);
		System.out.println("==============================");
		System.out.println("Displaying current parameters:");		
		System.out.println("-------------------------");
		System.out.println("|       P(Gender)       |");
		System.out.println("-------------------------");
		System.out.println("|  Male     |   Female  |");
		System.out.println("|  " + df.format(params[0]) +  "  |   " + df.format(params[1]) + " |");
		System.out.println("-------------------------");
		System.out.println();
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println("|                    P(Weight|Gender)          ||         P(Height|Gender)         |");
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println("|         | lesser_than_130 | greater_than_130 || lesser_than_55 | greater_than_55 |");
		System.out.println("| Male:   |    " + df.format(condParams[0][0]) +  "      |       " + df.format(condParams[1][0]) + "    ||     "+ df.format(condParams[2][0]) +  "    |     " + df.format(condParams[3][0]) + "     |");
		System.out.println("| Female: |    " + df.format(condParams[0][1]) +  "      |       " + df.format(condParams[1][1]) + "    ||     "+ df.format(condParams[2][1]) +  "    |     " + df.format(condParams[3][1]) + "     |");
		System.out.println("------------------------------------------------------------------------------------");
		
	}
	

	
	
	
	//This method load the data into dataSet
	private void loadData(String path){
		Scanner reader = null;
		try {
			reader = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//reading header
        String line =  reader.nextLine();
        //reading data line by line
		while (reader.hasNextLine())
	    {
	        //increment real counts
			realRecords++;
			line = reader.nextLine();
	        //split data in the line using tab as delimiter
	        String[] split=line.split("\t");
	        int gender, weight, height;
	        //assign data for weight and height from columns 2 and 3 respectively
        	weight = Integer.parseInt(split[1]);
        	height = Integer.parseInt(split[2]);
	        //if value in first column is known then use it to create a model
	        if(!split[0].equals("-")){
	        	gender = Integer.parseInt(split[0]);
	        	//and add it to dataSet
	        	Person someone = new Person(gender, weight, height, 1, true);
	        	dataSet.add(someone);	
	        	totalRecords++; //increment augumented counts by 1
	           }
	      //else value under first column is unknown then create two variations for it: male and female with appropriate probabilities
	        else{
	        	//and add them both to the dataSet
	        	Person male = new Person(0, weight, height, params[0], false);
	        	dataSet.add(male);
	        	Person female = new Person(1, weight, height, params[1], false);
	        	dataSet.add(female);
	        	totalRecords +=2; //increment augumented counts by 2
	        }
	    }
		reader.close();
		System.out.println("Loading data has been successful");
	}
	
	//This method displays statistics for graph plotting
	private void DisplayGraphStats(LinkedList<GraphPoint> list){
		System.out.println("\n\n==============================");
		System.out.println("Graph points:");
		System.out.println("\nIteration # \t Log Likelihood");
		//remove items one by one from list until it is empty. Display data from each one.
		while(!list.isEmpty()){
			GraphPoint point = list.removeFirst();
			System.out.println(point.getIterNum() + "\t\t" + point.getLogLikelihood());
		}
	}

}
