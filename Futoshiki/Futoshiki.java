/**
 *	FutoshikiGame is a class that uses two interlinked 2D arrays, one being a JTextField and the other being a 
 *	JLabel, to create a 7x7 grid of numbers and inequality signs, which is just a 4x4 grid of numbers with some
 *	inequality signs between them all. This grid is then filled completely to create a final grid, and then most 
 *	of the numbers and signs are removed, and the grid is displayed to the user. Once the game is complete, the 
 *	starting state of the grid and the time taken to finish it are used as training data in the nueral network. 
 *  
 *  @author Connor Fitzpatrick
 */

// import statements  
import java.awt.*;
import javax.swing.*; 
import javax.swing.border.*;
import java.util.Random;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class Futoshiki extends JFrame{
	
	
	Network futoshikiNetwork;         //creates the network object for futoshiki
	
	private JTextField[][] gameBoxes;            //creates an attribute of a two dimensional array of text fields
	
	private JLabel[][] inequalitySigns;            //attribute of a two dimnesional JLabel array
	
	private JButton submitGame;                  //creates a button attribute
	
	private JLabel timeLabel;                   //will be used to dislpay instructions to the user
	
	
	
	private Timer timer;                         //timer attributes that will be used for training and neural network
	
	private long startTime;
	private long endTime;
	private long timeDifference;
	
	private Double[] gameAsArray = new Double[8]; 
	
	
	//constructor for the game object, creates the basic grid layout
	public Futoshiki(){                 
		
		System.out.println("Futoshiki.java: Instantiating Network.");
		Network futoshikiNetwork = new Network("Futoshiki", 8);
		System.out.println("Futoshiki.java: Calling Save()");
		futoshikiNetwork.save();


		//gameFrame = new JFrame("Futoshiki Game");          //instantiates the Jframe attribute
		
		setSize(500, 500);     //sets the frame size
		setResizable(false);    //so that the size of the frame can't be changed
		setVisible(false);      //sets the frame to visible
		setLayout(new BorderLayout());     //sets the frames border layout for easier arrangement of aspects later
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //dont do this, it closes ALL frames when the window is closes, including the main menu       //sets the program to close when the 'x' is pressed 
		setLocationRelativeTo(null);                 //will display the frame in the middle of the screen
        
		JPanel spacePanelEast = new JPanel();             //creates a blank panel used for making space	on the left hand side	
		JPanel spacePanelWest = new JPanel();             //creates a blank panel used for making space	on the right hand side			
		
		JPanel timePanel = new JPanel(new GridLayout(2, 1));                   //creates a panel for a timer at the top 			
		JPanel gamePanel = new JPanel (new GridLayout (7, 7, 10, 10));                //creates a panel with a 7x7 grid layout for our game structure, with a 10 pixel gap between each grid
		JPanel submitPanel = new JPanel();                                      //creates a panel for a button 
		
		spacePanelEast.setBackground(Color.WHITE); spacePanelWest.setBackground(Color.WHITE); timePanel.setBackground(Color.WHITE); gamePanel.setBackground(Color.WHITE); submitPanel.setBackground(Color.WHITE);   //sets the background for all panels as white
		
		add(spacePanelEast, BorderLayout.EAST);     //adds the two blank panels to the frame on their respected sides
		add(spacePanelWest, BorderLayout.WEST);
		
		timeLabel = new JLabel("");
		timeLabel.setHorizontalAlignment(JLabel.CENTER); timeLabel.setVerticalAlignment(JLabel.CENTER);
		timePanel.add(timeLabel);
		add(timePanel, BorderLayout.NORTH);          //adds the time panel to the top of the frame.
		
		gameBoxes = new JTextField[7][7];          		//instantiates the gameboxes attribute as the 7x7 text field array
		inequalitySigns = new JLabel[7][7];             //instantiates the inequalitySigns attribute with a 7x7 label array
		
		for (int i = 0; i < 7; i++){                  //for loop for x-axis array
			for (int j = 0; j < 7; j++) {             //for loop for y-axis array
			
				if (i % 2 == 0 && j % 2 == 0){               //add the JTextField to the Panel if it is on a grid with two even indexes
				
					gameBoxes[i][j] = new JTextField(2);                               //creates a text field of size 2 at that array index
					
					gameBoxes[i][j].setHorizontalAlignment(JTextField.CENTER);           //sets the postion of this text field 
					
					gameBoxes[i][j].setFont(gameBoxes[i][j].getFont().deriveFont(20f));     //sets the size of the text
					
					gameBoxes[i][j].setBorder(new LineBorder(Color.BLACK));           //creates a line border for the text field, game grid looks nicer
					
					gamePanel.add(gameBoxes[i][j]);       //adds this text field to the panel
				}
				
				else{
					inequalitySigns[i][j] = new JLabel("");                               //does the same as above but for the JLabels instead
					
					inequalitySigns[i][j].setHorizontalAlignment(JLabel.CENTER);
					
					inequalitySigns[i][j].setFont(inequalitySigns[i][j].getFont().deriveFont(30f));
					
					gamePanel.add(inequalitySigns[i][j]);
					
				}
				
			}
		}
		
		add(gamePanel, BorderLayout.CENTER);     //adds the game panel to the frame in the middle
		
		JFrame loadingScreen = new JFrame();
        loadingScreen.setTitle("Loading...");
        loadingScreen.setSize(500, 500);
        loadingScreen.setLocationRelativeTo(null); // place at centre of screen
        loadingScreen.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // dont let the user close the window, could mess up training algo
        loadingScreen.add(new JLabel("Loading... Please wait."));
        loadingScreen.setVisible(true);
		
		
		futoshikiNetwork.train(10.0);
		
		try {
            Thread.sleep(2000); // Pausing for 2 seconds to give impression of loading
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		
		loadingScreen.setVisible(false);
		
		
		GridFiller(this);                             //calls the grid filler method which fills the grid completely
		
		GameMaker(this);                              //calls the game maker method which empties most of the gird, leaving a puzzle
		
		
		CreateNetworkArray(this);
		
		startTime = System.currentTimeMillis();
		
		timer = new Timer(1000, new ActionListener() {                              //starts a timer that will be displayed on the screen
            @Override
            public void actionPerformed(ActionEvent e) {
                timeDifference = (System.currentTimeMillis() - startTime) / 1000;
                timeLabel.setText(timeDifference + " seconds");
            }
        }); 
		timer.start();                 
		
		JLabel diffLabel = new JLabel();                     //a label to show the difficulty
		diffLabel.setText(futoshikiNetwork.predict(gameAsArray));           //passes the array to the predict method of the network object
		diffLabel.setHorizontalAlignment(JLabel.CENTER); diffLabel.setVerticalAlignment(JLabel.CENTER);   //arranges the text of the label        
		timePanel.add(diffLabel);                                           //adds the label to the panel at the top
		
			
		submitGame = new JButton("SUBMIT");         //instantiates the button attribute with some text
		
		submitGame.addActionListener(e -> buttonPressed(this, futoshikiNetwork));         //lamda expression for action listener of button, calls method
		
		submitPanel.add(submitGame);                //adds the button to the panel for the button 
		
		this.add(submitPanel, BorderLayout.SOUTH);   //adds the button panel to the bottom of the frame.
		
		this.setVisible(true);	                                                          
				
	}
	
	
	public void GridFiller(JFrame gameFrame){
		ArrayList<Integer> numbers = new ArrayList<Integer>();                //stores values 1, 2, 3, 4 to be put in game frame
		
		Random rand = new Random();                                   //object from random class for random integer generation
		 
		for (int i = 0; i < 7; i++){                                        //for loop for x-axis array for adding numbers to the text fields
			
			numbers.clear();
			numbers.add(1); numbers.add(2); numbers.add(3); numbers.add(4);         //adding the numbers to the array list
			
			for (int j = 0; j < 7; j++){                                     //for loop for y-axis array for adding numbers to the text fields
				 
				if (i % 2 == 0 && j % 2 == 0){               //add numbers to the Text Fields if they are even
				
					int num = 10;                              //num is the number which will be added to the text fields
					int index = 0;                                    //index will be the index of the array list that we are trying to get
					boolean duplicate = true;                          //one of two boolean values to show if the current number is a duplicate, this one is for controlling the loop
					while (duplicate == true){  				//only stops looping when number definitely isn't a duplicate
				
						int size = (numbers.size());               //take the current size of the array list 
						
						if (size > 1){                             //if there are more than one elements left in the array list, take a random one by				
							index = rand.nextInt(size);            //generating a random integer from 0 to the array size - 1
							num = numbers.get(index);              //grab the number from the array list at that index
						}
						
						else{                                     //if only one element left in array list, take that one
							num = numbers.get(0);
						}
					
						boolean dup = false;                        //second boolean for duplicate identification, changed inside the loop, so that the while loop can be exitted at the end
					
						for (int a = i-1; a >= 0; a--){  						             //a loop that will loop back across the row, from the current one to the left most one 
			 	
							if (a % 2 == 0){
								
								int prevNuma = Integer.parseInt(gameBoxes[a][j].getText());
								
							    if (num == prevNuma) {                   //and if the current number waiting to be added is already there, set the 
									dup = true;                              //boolean dup as true, if not, keep it as false
								}
									
								                            //boolean dup as true, if not, keep it as false
						    }
							
						}
						
						
						if (dup == false){                              //repeats the while loop if a duplicate is found
							duplicate = false;                                   
							gameBoxes[i][j].setText(String.valueOf(num));      //if no duplicate, adds the number to the current text field
							numbers.remove(index);                             //removes that number from array list so it cannot be used again in that row
						}
						
						if ((size == 1) && (dup == true)){              //if a duplicate us found but we are at the end of the arraylist           //
							duplicate = false;                          //exit the while loop and restart the whole process
							j=0;
							i=0;
							numbers.clear();
							numbers.add(1); numbers.add(2); numbers.add(3); numbers.add(4);         //adding the numbers to the array list
						System.out.println("far");	
						}
							
					}
				
				}
			}
		}
			
		for (int i = 0; i < 7; i++){                                                   //for loop for x-axis for adding inequalities to the labels
			                                                                           //has to be two separate pairs of the same for loops for adding numbers to text fields then
			for (int j = 0; j < 7; j++){                                               //adding inequalities because all of the numbers are needed to be added in order to compare them here
			
				if(( i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)){               //if on a grid with one odd and one even coordinate, for the inequality signs
				
					if (i % 2 != 0 && j % 2 == 0){                                            //if we are even along the x-axis, the text fields will be above/below
						
						int a = i + 1;                                          //i +- 1 gets the number above or below the current label
						int b = i - 1;
						
						int numBelow = Integer.parseInt(gameBoxes[a][j].getText());                 //gets the numbers from the text field/below the current grid
						int numAbove = Integer.parseInt(gameBoxes[b][j].getText());
						
						if (numBelow > numAbove){                                           //if and else statement that adds the right way sign to the label index
							inequalitySigns[i][j].setText("\u02C4");
						}
						else{
							inequalitySigns[i][j].setText("\u02C5");
						}
						
					}
					
					else if(j % 2 != 0 && i % 2 == 0){                 //next 15 lines of code are same as above but for left/right instead
						
						int a = j + 1;
						int b = j - 1;
						
						int numRight = Integer.parseInt(gameBoxes[i][a].getText());
						int numLeft = Integer.parseInt(gameBoxes[i][b].getText());
						
						if (numRight > numLeft){
							inequalitySigns[i][j].setText("<");
						}
						else{
							inequalitySigns[i][j].setText(">");
						}
						
					}
				}
			}
		} 
	
	}
	
	public void GameMaker(JFrame gameFrame){
		
		for (int i = 0; i < 7; i++){                     //start at the first row then make way up them
			
			Random rand = new Random();                   //for getting random numbers
			
			if (i % 2 == 0){                        //if i is even, the row has text fields with numbers on it
				int pos1 = rand.nextInt(6);                  //getting two random numbers that will represent the j cooridnates of the numbers/inequality signs that stay
				int pos2 = rand.nextInt(6);
			
				while (pos1 == pos2){                       //make sure both numbers are different
					pos2 = rand.nextInt(6);
				}
			
				for (int j = 0; j < 7; j++){              //now go across the row
			
					if (j % 2 == 0){                     //if j is even it is a text field with a number
					
						if (j != pos1 && j != pos2){                 //unless the j coordinate is the same as either of the random ones, empty the text field
							gameBoxes[i][j].setText("");
						}
					
					}
				
					else{                                       //same as above but for odd coordiantes with labels 
					
						if (j != pos1 && j != pos2){
							inequalitySigns[i][j].setText("");
						}
					
					}
				}
			}
			
			else{                                            //if i is odd, there is only labels with inequality signs on that row
			 
				int pos = 1;                               //set int value as odd for loop below
				
				while (pos % 2 != 0){                    //on these rows with just labels, the odd j coordinates are empty, so loop makes sure random number is even
					pos = rand.nextInt(6);
				}
				
				for (int j = 0; j < 7; j++){              //go along the row
					
					if (j != pos){                            //any labels at a coordinate that is not the random generated one is emptied
						inequalitySigns[i][j].setText("");
					}
					
				}
				
			}
				
				
				
		}
	}
				
	
	public boolean InputVerifier (JFrame gameFrame){
			
		boolean validInput = true;                             //boolean variable that shows whether all inputted values are valid

		for (int i = 0; i < 7; i++){                               //itterate through both arrays of text fields
			for (int j = 0; j < 7; j++){
				
				if (i % 2 == 0 && j % 2 == 0){               //only check even grid coordinates
				
					try{                                                                        //the below lines will throw and exception if input is not integers
						int inputNum = Integer.parseInt(gameBoxes[i][j].getText());            //if it is a number, see if it is between 1 and 4 inclusive
							
						if (1 > inputNum || inputNum > 4){ 
							validInput = false;                                               //if its not, change the boolean variable
						}
							
					}catch (NumberFormatException nfe){                                    //should catch if not a integer is inputted
						validInput = false;                                               //also changes the boolean variable
					}
				}
			}
		}
		
		return(validInput);                                    //returns the boolean after all the checks
		
	}
	
	
	public boolean NumberAmountChecker (JFrame gameFrame){
	
		boolean validNoAmount = true;                                                //boolean vairable to store if there has been a duplicate of a number
		
		for (int i = 0; i < 7; i++){                                          //three loops, that itterate through every index, to compare it to all indexes below it
			for (int n = 0; n < 7; n++){
				for (int j = n+2 ; j < 7; j++){
					
					if (i % 2 == 0 && n % 2 == 0 && j % 2 == 0){
						
						try{																	//the below lines will throw and exception if input is not integers
							int y = Integer.parseInt(gameBoxes[i][n].getText());              //getting the value in the text fields and turning them into integers				
							int x = Integer.parseInt(gameBoxes[i][j].getText());
						
							if (x == y){
								validNoAmount = false;                             //if two are the same, change the boolean variable
							}
						}catch (NumberFormatException nfe){               //catch the exception, also change the boolean
							validNoAmount = false;
						}
						
					}
					
				}
			}
		}
		
		for (int j = 0; j < 7; j++){                                //three loops, that itterate through every index, to compare it to all indexes to the right of it
			for (int m = 0; m < 7; m++){
				for (int i = m+2; i < 7; i++){
					
					if (i % 2 == 0 && m % 2 == 0 && j % 2 == 0){
						
						try{                                                         //the below lines will throw and exception if input is not integers
							int a = Integer.parseInt(gameBoxes[m][j].getText());       //getting the value in the text fields and turning them into integers	
							int b = Integer.parseInt(gameBoxes[i][j].getText());
						
							if (b == a){
								validNoAmount = false;                              //if two are the same, change the boolean variable
							}
						}catch (NumberFormatException nfe){                            //catch exception, change boolean
							validNoAmount = false;
						}
						
					}
					
				}
			}	
		}
		
		return(validNoAmount);
		
	}
		
	
	public boolean InequalityChecker(JFrame gameFrame){
	
		boolean validSign = true;                         //boolean value to see if there is any signs not satisfied
			
		for (int i = 0; i < 7; i++){                                     //two loops to go through all of the 
			for (int j = 0; j < 7; j++){ 
				
				if(( i % 2 == 0 && j % 2 != 0) || (i % 2 != 0 && j % 2 == 0)){               //if on a grid with one odd and one even coordinate, where the inequality signs will be
			
					if (i % 2 != 0 && j % 2 == 0){                                            //if we are even along the y-axis, the text fields will be above/below the inequality signs
				
						try{ 														//the below lines will throw and exception if input is not integers
							int x = i + 1;                                          //used as coordinates for above and below the current one, which will contain the two numbers that must
							int numx = Integer.parseInt(gameBoxes[x][j].getText());              //take the number from that coordinate and turn into integer
							
							int y = i - 1;                                          
							int numy = Integer.parseInt(gameBoxes[y][j].getText());
							
							String currSign = inequalitySigns[i][j].getText();           //get the inequality sign currently in the label
						
							if (currSign.contains("\u02C5") && numy < numx){                    //if the numbers dont effect the equality sign, change the boolean variable
								validSign = false;
							}
						
							else if(currSign.contains("\u02C4") && numx < numy){                //same as prev if statement
								validSign = false;
							}
							
						}catch (NumberFormatException nfe){                          //catch exception, change boolean
							validSign = false;
						}
						
					}
						
					else if (i % 2 == 0 && j % 2 != 0){                                   //same as above but for numbers to the side of the inequality sign
					
						try{																						
	
							int x = j + 1;
							int numx = Integer.parseInt(gameBoxes[i][x].getText()); 							
							
							int y = j - 1;
							int numy = Integer.parseInt(gameBoxes[i][y].getText());							
						
							String currSign = inequalitySigns[i][j].getText();

							if (currSign.contains("<") && numy > numx){
								validSign = false;
							}
						
							else if (currSign.contains(">") && numx > numy){
								validSign = false;
							}
							
						}catch (NumberFormatException nfe){
							validSign = false;
						}
						
					}
				}
			}
		}
			
		return(validSign);
		
	}
	
	public void buttonPressed (JFrame gameFrame, Network NNInterface){
	
		gameFrame.setVisible(false);
				
		boolean gameState = false;                     //a boolean variable that will be passed through put the methods, and only becomes true when all methods are satisfied
			
			boolean validInputState = InputVerifier(gameFrame);                 //boolean value that stores the result of the InputVerifier method
			
			boolean validNumberAmountState = NumberAmountChecker(gameFrame);    //same as above but for NumberAmountChecker
			
			boolean validInequalityState = InequalityChecker(gameFrame);        //same as above but for InequalityChecker
			
			if (validInputState == false || validNumberAmountState == false || validInequalityState == false){   //if any of the boolean variables come back false, tell user they are wrong
				JOptionPane.showMessageDialog(gameFrame,
                    "The only allowed inputs are 1, 2, 3 or 4, \neach number can only occurs once on every row and colunm, \nand all inequality signs must be satisfied.",
                    "WRONG!",
                    JOptionPane.ERROR_MESSAGE);
				gameFrame.setVisible(true);
			}
			
			else if (validInputState && validNumberAmountState && validInequalityState) {
			                            //if all of the methods return true, then change gameState to true to 																		//satisfy if statement below and finish the                                 
				gameState = true;       //end the game
			}
				
	
		if (gameState == true){                            		
				
			timer.stop();
			double timeDifferenceDouble = (double) timeDifference;
			// timeDifferenceDouble = timeDifferenceDouble / 1000; // i think this is already in seconds ? - huw
			NNInterface.addTrainingData(gameAsArray, timeDifferenceDouble);
			
			NNInterface.save();
			
			JOptionPane.showMessageDialog(gameFrame,
                "Well Done!\nYou completed the Futoshiki puzzle in " + String.valueOf(timeDifference) + " seconds.",                //output window of congratulations to the user
                "CONGRATULATIONS!",
                JOptionPane.INFORMATION_MESSAGE);
			
			processWindowEvent(new WindowEvent(Futoshiki.this, WindowEvent.WINDOW_CLOSING));
		}			
	}


	public void CreateNetworkArray(JFrame gameFrame){
		
		for (int x = 0; x < gameAsArray.length; x++) {           //make all elements in array 0.0
            gameAsArray[x] = 0.0d;
		}
			
		for (int i = 0; i < 7; i+=2){                             //to itterate down the colunm
				
					for (int j = 0; j < 7; j++){            //for each itteration of the colunm, go along the row
						if (j % 2 == 0){                       //if j is even its a number text field
						
							String str = gameBoxes[i][j].getText();         //get the string from the text field
							if (str.isEmpty() == false){                         //if the string isnt empty
								gameAsArray[i] = gameAsArray[i] + 1.0d;            //increment the index of the array
							}
						} 
						
						else{                        //same as above but for j is odd so its a inequa;ity sign label
						
							String str = inequalitySigns[i][j].getText();
							if (str.isEmpty() == false){
								gameAsArray[i+1] = gameAsArray[i+1] + 1.0d;
							}
						}
					}
				
			
		}
	}
							
			
    
	public static void main (String[] args){
		//main method that stars game
		Futoshiki futoshikiPuzzle = new Futoshiki();
		
		
	}
}
