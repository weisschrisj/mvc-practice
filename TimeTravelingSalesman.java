import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


/********************************************************************************************
 * 
 *  Model
 *
 ********************************************************************************************/

class ttsModel  
{ 
    private int denominations; 
    private int numberOfPrices; 
    private int[][] prices;
    private int[] conversions;
     
    //setters and getters
    public int getDenominations()  
    { 
        return denominations; 
    } 
     
    public void setDenominations(int denominations)  
    { 
        this.denominations = denominations; 
    } 
     
    public int getNumberOfPrices()  
    { 
        return numberOfPrices; 
    } 
     
    public void setNumberOfPrices(int numberOfPrices)  
    { 
        this.numberOfPrices = numberOfPrices; 
    } 
    
    public int[][] getPrices()
    {
    	return prices;
    }
    
    public void setPrices(int[][] prices)
    {
    	this.prices = prices;
    }
    
    public int[] getConversions()
    {
    	return conversions;
    }
    
    public void setConversions(int[] conversions)
    {
    	this.conversions = conversions;
    }
} 
  

/********************************************************************************************
 * 
 *  View
 *
 ********************************************************************************************/

class ttsView  
{ 
    public void printDataSet(int numberOfDataSets, int[] output) 
    { 
        for (int i=0; i<numberOfDataSets; i++)
        {
        	System.out.println("Data Set " + (i+1) + ":");
        	System.out.println(output[i]);
        }
    } 
} 
  
/********************************************************************************************
 * 
 *  Controller
 *
 ********************************************************************************************/

class ttsController  
{ 
    private ttsModel model; 
    private ttsView view; 
    private int numberOfDataSets;
    private int[] output;
  
    public ttsController(ttsModel model, ttsView view) 
    { 
        this.model = model; 
        this.view = view; 
        getInput();
        updateView();
    } 
    
    private void getInput()
    {
    	//Scanner in = new Scanner(System.in);
    	File inputFile = new File("input.txt");

    	try
    	{    	
    	    System.out.println ("Reading file " + inputFile);
        	Scanner in = new Scanner(inputFile);
    		//get number of data sets
    		//System.out.println("Enter the number of data sets: ");
    		numberOfDataSets = in.nextInt();
    		//check valid input
    		if (numberOfDataSets < 1)
    		{
    			throw new CustomException("Need at least 1 data set");
    		}
    		
    		output = new int[numberOfDataSets];
    		
    		//move to next line
    		in.nextLine();
    		
    		//assemble data sets
    		for (int i=0; i<numberOfDataSets; i++)
    		{
    			getDenominationsAndPrices(in);
    			getConversions(in);
    			getPrices(in);
    			getLargestDifference(i);
    		}
    		
    	}	
    	catch (NumberFormatException | FileNotFoundException | CustomException exception) {
    		System.out.println(exception);
    	}
    			
    }
    
    //get info from line 2 - denominations and number of prices
    private void getDenominationsAndPrices(Scanner in) throws CustomException
    {
    	//read user input
    	//System.out.println("Enter denominations and number of prices: ");
    	String input = in.nextLine();
    	String[] temp = input.split(" ");
    	
    	//check valid input
    	if (temp.length == 2)
    	{
    		//set Denominations
    		int tempDenominations = Integer.valueOf(temp[0]);
    		setModelDenominations(tempDenominations);
    		
    		//check valid input
    		if (getModelDenominations() < 2 || getModelDenominations() > 7)
    		{
    			throw new CustomException("Only denominations >=2 and <=7 are valid");
    		}
    		
    		//set Number of prices
    		int tempNumberOfPrices = Integer.valueOf(temp[1]);
    		setModelNumberOfPrices(tempNumberOfPrices);
    		
    		//check valid input
    		if (getModelNumberOfPrices() < 2 || getModelNumberOfPrices() > 10)
    		{
    			throw new CustomException("Only number of prices >=2 and <=10 are valid");
    		}
    		
    	}
    	else
    	{
    		throw new CustomException("Please enter the number of denominations and number of prices seperated by a space (i.e. '2 2')");
    	}
    }
    
    //get info from line 3 - conversions
    private void getConversions(Scanner in) throws CustomException 
    {
    	int[] conversions = new int[getModelDenominations()];
    	
    	//read user input
    	//System.out.println("Enter currency conversion factors: ");
    	String input = in.nextLine();
    	String[] temp = input.split(" ");
    	
    	//check valid input
    	if (temp.length != (getModelDenominations() - 1))
    	{
    		throw new CustomException("Number of conversions must equal amount of denominations minus one");
    	}
    	else 
    	{
    		//iterate through and set conversions
    		for (int i=0; i<temp.length; i++)
    		{
    			conversions[i] = Integer.valueOf(temp[i]);
    		}
    		
    		setModelConversions(conversions);
    	}
    }
  
    //get info from remaining lines - prices
    private void getPrices(Scanner in) throws CustomException
    {
    	int[][] prices = new int[getModelNumberOfPrices()][getModelDenominations()];
    	
    	for (int i=0; i<getModelNumberOfPrices(); i++)
    	{
    		//read user input
    		//System.out.println("Enter item price: ");
    		String input = in.nextLine();
    		String[] temp = input.split(" ");
    		
    		
    		for (int j=0; j<temp.length; j++)
    		{
    			//check valid input
    			int tempPrice = Integer.valueOf(temp[j]);
    			if (tempPrice < 0)
    			{
    				throw new CustomException("Price can not be negative");
    			}
    			else 
    			{
    				prices[i][j] = Integer.valueOf(temp[j]);
    			}
    		}
    	}
    	
    	setModelPrices(prices);
    }
    
    //determine largest price difference by converting prices into smallest denominations for comparison
    private void getLargestDifference(int dataSet) throws CustomException
    {
    	int min = Integer.MAX_VALUE;
    	int max = Integer.MIN_VALUE;
    	int[] conversions = getModelConversions();
    	int[][] prices = getModelPrices();
    	int convertValue;
    	int total = 0;
    	int priceDifference;
    	
    	//iterate through list of prices
    	for (int i=0; i<getModelNumberOfPrices(); i++)
    	{
    		for (int j=0; j<getModelDenominations(); j++)
    		{
    			//convert prices iterating through to the smallest currency denomination
    			convertValue = prices[i][j];
    			for (int k=j; k < conversions.length - 1; k++)
    			{
    				convertValue *= conversions[k];
    			}
    			total += convertValue;
    			
    			//smallest denomination does not need conversion and can just be added
    			if (j == getModelDenominations())
    			{
    				total += prices[i][j];
    			}
	
    		}
    		
    		//set new min or max price if applicable
    		if (total < min) 
    		{
    			min = total;
    		} 
    		else if (total > max)
    		{
    			max = total;
    		}
    		
    		//reset total for next iteration
    		total = 0;
    	}
    	
    	//calculate largest price difference and set it to the output
    	priceDifference = max - min;
    	output[dataSet] = priceDifference;
    	
    }
    
    //setters and getters
    private void setModelDenominations(int denominations) 
    { 
        model.setDenominations(denominations);         
    } 
  
    private int getModelDenominations() 
    { 
        return model.getDenominations();         
    } 
  
    private void setModelNumberOfPrices(int numberOfPrices) 
    { 
        model.setNumberOfPrices(numberOfPrices);         
    } 
  
    private int getModelNumberOfPrices() 
    { 
        return model.getNumberOfPrices();         
    } 
    
    private void setModelConversions(int[] conversions)
    {
    	model.setConversions(conversions);
    }
  
    private int[] getModelConversions()
    {
    	return model.getConversions();
    }
    
    private void setModelPrices(int[][] prices)
    {
    	model.setPrices(prices);
    }
    
    private int[][] getModelPrices()
    {
    	return model.getPrices();
    }
    
    public void updateView() 
    {                 
        view.printDataSet(numberOfDataSets, output); 
    }     
} 
  
/********************************************************************************************
 * 
 *  Custom exception definer
 *
 ********************************************************************************************/

class CustomException extends Exception 
{
	public CustomException(String message) {
		super(message);
	}
}

/********************************************************************************************
 * 
 *  Main
 *
 ********************************************************************************************/


class TimeTravelingSalesman  
{ 
    public static void main(String[] args)  
    { 
        ttsModel model  = new ttsModel(); 
        ttsView view = new ttsView(); 
        ttsController controller = new ttsController(model, view); 
    } 
} 