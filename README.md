# ExpectationMaximization
## Introduction:
In this project I have implemented and analyzed the Expectation Maximization algorithm for small Bayesian Network. The Bayesian Network analyzed has independence structure known as common cause. The Bayesian Network has 3 variables: Gender, Weight and Height and is presented in the graph below.

<img src="Bayesian%20Network%20Graph.png" width ="500">

Each viable has binary form, in other words its either 0 or 1. They are interpreted as follows:
* Gender 	= 0 => Male
* Gender	= 1 => Female
* Weight	= 0 => weight less than 130 lbs
* Weight	= 1 => weight greater than 130 lbs
* Height 	= 0 => height less than 55 inches
* Height 	= 1 => height greater than 55 inches

Threshold for convergence of EM algorithm has been set to 0.001.
Algorithm has been tested on 5 data sets with different percentage of missing data for gender. Each set has been tested on multiple starting points and results have been presented further.

## Methods:
Each data record has been loaded to the data structure Person, which stores its gender, height and weight as well as probability of gender and variable indicating if this person instance is an actual complete data or augmented data point by creating two variations where in one instance Gender=0 and in other one Gender=1. One dimensional array has been used to store parameter P(Gender) called params, where params[0] stands for male and params[1] for female. Moreover, two dimensional array has been used to store conditional probabilities where condParams[Gender][y] and y takes value from 0 to 3, where:
* 0 => Weight = 0
* 1 => Weight = 1
* 2	=> Height = 0
* 3 => Height = 1

To store values of expected counts three dimensional array has been used called expectedCounts[Gender][Weight][Height] where each variable in square brackets is equivalent to its respective value.
Data structure graphPoint stores two variables (iteration number and its respective log likelihood) and all graphPoints are stored in LinkedList. These points are necessary to store for purpose of plotting graph of the likelihood vs number of iterations to demonstrate the convergence of algorithm.

## Instructions:
1. Compile the Main.java class
	javac Main.java
2. Execute the compiled file entering 6 arguments. First argument is input file path. 
  Remaining 5 arguments are parameters as follows
 	* 2nd argument - Prob(Gender = Male)
 	* 3rd argument - Prob(Weight = greater_than_130 | Gender = Male)
 	* 4th argument - Prob(Weight = greater_than_130 | Gender = Female)
	* 5th argument - Prob(Height = greater_than_55 | Gender = Male)
 	* 6th argument - Prob(Height = greater_than_55 | Gender = Female) 
  
  All of argumets 2-6 must have decimal value between 0 and 1. 
