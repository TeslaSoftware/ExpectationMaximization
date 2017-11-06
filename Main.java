

/*
 * Main.java
 * This class takes 6 parameters
 * 1st parameter is path to input file
 * remaining 5 parameters are values between 0 and 1 for the following probabilities
 * Prob(Gender = Male)
 * Prob(Weight = greater_than_130 | Gender = Male)
 * Prob(Weight = greater_than_130 | Gender = Female)
 * Prob(Height = greater_than_55 | Gender = Male)
 * Prob(Height = greater_than_55 | Gender = Female) 
 * 
 * 
 */
public class Main {

	public static void main(String[] args) {
		double arg1 = 0, arg2 = 0 , arg3 = 0, arg4 = 0, arg5 = 0;
		try {
			 arg1 = Double.parseDouble(args[1]);
			 arg2 = Double.parseDouble(args[2]);
			 arg3 = Double.parseDouble(args[3]);
			 arg4 = Double.parseDouble(args[4]);
			 arg5 = Double.parseDouble(args[5]);
		}
		catch(IllegalArgumentException e){
			System.out.println("Arguments entered are not numeric");
		}
		if(arg1 <0 || arg1 >1 ||arg2 <0 || arg2 >1 ||arg3 <0 || arg3 >1 ||arg4 <0 || arg4 >1 ||arg5 <0 || arg5 >1 ){
			System.out.println("Arguments entered are not valid. Value must be between 0 and 1 for each argument");
		}
		
		String path = args[0];
		
		EMAlgorithm ema = new EMAlgorithm(path, arg1, arg2, arg3,arg4 , arg5, 0.001);
		ema.run();
		

	}

}
