/*----------------------------------------------------------------------------------------------------------------|
| ConnectFour.java                                                                                                |
|-----------------------------------------------------------------------------------------------------------------|
| Programmer: Lucy Lin                                                                                            |
| Course:     ICS3U                                                                                               |
| Date:       1/31/2021                                                                                           |
|-----------------------------------------------------------------------------------------------------------------|
| This program is a connect four game which allows the player to go against a not very intelligent AI. The board  |
| is represented with numbers for the rows and columns, and X's and O's are used for tokens. The game             |
| automatically checks each turn whether anyone has won. In addition, the program stores the stats (wins, losses, |
| ties) of each player in text files, so that the stats can be accessed at a later time for returning players.    |                                                         
|________________________________________________________________________________________________________________*/

import java.io.*;
import java.util.*;
import java.lang.*;

public class ConnectFour {
   //Declaration of constants for row and column numbers used throughout methods
   static final int ROWS = 6, COLUMNS = 7;
   
   public static void main(String[] args){
      //Declaration of variables and creation of scanners (certain variables are initialized to avoid errors)
      int wins = 0, losses = 0, ties = 0, columnNumber = 1;
      double winPercent, lossPercent;
      String newOrReturning, fileName = "", playerName, again;
      boolean valid1 = false, valid2 = false, valid3 = false, valid4 = false, valid5 = false, valid6 = false, playAgain = false, done;
      final char PLAYER_SYMBOL = 'X', COMPUTER_SYMBOL = 'O';
      char [] [] board = new char [ROWS] [COLUMNS];
      Scanner sc = new Scanner(System.in);
      
      //Prints a brief intro plus the rules to the game
      System.out.println("Welcome to Connect4! Y'know, my friend tried to do this program in one day last year.");
      System.out.println("You know those child prodigies that dream in code? Yeah, he had a nightmare in Java. He also had 69 errors at one point. Nice.");
      System.out.println("aNyWaYs, let's get on with the game. You'll be X's, and the computer opponent will be O's.");
      System.out.println("You probably know the rules already, but here's a refresher: you win by getting 4 in a row, column, or diagonal.");
      System.out.println("That's enough of that for the intro...");
      System.out.println();
      
      //Prompts the user to enter whether they're new or returning
      System.out.println("Are you a new player, or a returning player? Enter 1 for new, and 2 for returning.");
      newOrReturning = sc.nextLine();  //String was used because the programmer was too lazy to catch any exceptions for inputmismatch
      
      //Do-while loop repeatedly accepts input until either 1 or 2 is entered
      do {
         switch (newOrReturning){
            //Creates and opens a new text file for new player and writes all stats as 0
            case "1": 
               System.out.print("Hello, stranger! What's your name? ");
               playerName = sc.nextLine();
               fileName = playerName + ".txt";
               try {
                  BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
                  out.write("0\n0\n0");
                  valid1 = true;
                  out.close();
               }
               catch (IOException e){
                  System.out.println("There was an issue opening or closing your file. Sorry :/ " + e);
               }
               break;
               
            //Opens and reads file of returning player
            case "2":               
               do {
                  System.out.print("Please enter your name: ");
                  playerName = sc.nextLine();
                  fileName = playerName + ".txt";
                  try {
                     BufferedReader in = new BufferedReader(new FileReader(fileName));
                     System.out.println("Hello again, " + playerName + "!");
                     valid2 = true;
                     valid1 = true;
                  }
                  //If the text file can't be found the player is prompted to re-enter their name, or select that they're a new player
                  catch (IOException e){
                     System.out.println("Either something went wrong with opening your file, or you entered your name wrong. Maybe try again?");
                     System.out.println("Alternatively, if you meant to type '1', please input it now. If you want to try inputting your name again, type '2'.");
                     newOrReturning = sc.nextLine();
                     do {
                        switch (newOrReturning){
                           case "1": 
                              valid2 = true;
                              valid6 = true;
                              break;
                           case "2": 
                              valid6 = true;
                              valid2 = false;
                              break;
                           default: 
                              System.out.println("Sorry, that's not a valid option. Please try again.");
                        }
                     } while (valid6 == false);
                     valid1 = false;
                  }
               } while (valid2 == false);
               break;
            default:
            System.out.println("That was not a valid input. Please try again.");
            newOrReturning = sc.nextLine();
         }
      } while (valid1 == false);
      
      //Do while loop repeats the full game until the player chooses to stop
      do {
         //Opens player's stats text file and reads in their stats and stores in variables 
         try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            wins = Integer.parseInt(in.readLine());
            losses = Integer.parseInt(in.readLine());
            ties = Integer.parseInt(in.readLine());
         }
         catch (IOException e){
            System.out.println("Sorry, there was an issue opening or reading your file. " + e);
         }
         
         //Prints stats for player
         System.out.println("You currently have " + wins + " wins, " + losses + " losses, and " + ties + " ties.");
         
         //Calculates win and loss rate, and outputs rates to player
         if (wins != 0 || losses != 0 || ties != 0){
            winPercent = wins * 100.0 / (wins + losses + ties);
            lossPercent = losses * 100.0 / (wins + losses + ties);
         }
         else {
            winPercent = 0;
            lossPercent = 0;
         }
         System.out.println();
         System.out.printf("Your win rate is %.2f%s, and your loss rate is %.2f%s.\n", winPercent, "%", lossPercent, "%");
         System.out.println();
         
         //Fills the board with empty slots to 'reset' at the beginning of each game
         fillBoard(board);
         
         //Do-while loop lets player and AI take turns until someone wins or the board is filled
         done = false;
         do {
            printBoard(board);
            //Do-while loop lets player enter numbers until they enter the number of a not completely filled column
            do {
               //Do-while loop lets player enter input until they enter an integer between 1 and 7
               do {
                  try {
                     System.out.print("Which column would you like to drop the token in? ");
                     columnNumber = sc.nextInt();
                     sc.nextLine();
                     if (columnNumber >=1 && columnNumber <= 7){
                        valid3 = true;
                     }
                     else {
                        System.out.println("Please choose a number between 1 and 7.");
                        valid3 = false;
                     }
                  } 
                  catch (InputMismatchException e){
                     sc.nextLine();
                     System.out.println("Sorry, I don't quite understand. Please input an integer.");
                     valid3 = false;
                  }
               } while (valid3 == false);   
               //dropToken method checks whether the column is full
               if (dropToken(columnNumber, board, PLAYER_SYMBOL) == true){
                  valid4 = true;
               }               
               else {
                  valid4 = false;
                  valid3 = false;
                  System.out.print("Sorry, that column is full. Pick another: ");
               }
            } while (valid4 == false);
            
            //Board is printed again
            printBoard(board);
            System.out.print("Please enter to continue: ");
            sc.nextLine();
            
            //Checks if game is over (if game isn't over, the AI takes its turn)
            if (checkGameOver(board, COMPUTER_SYMBOL, PLAYER_SYMBOL) == 0){
               System.out.println("IT'S THE DUMB AI'S TURN!!");
               //AI drops a token, board is printed, and it's checked whether the game is over
               computerTurn(board, COMPUTER_SYMBOL, PLAYER_SYMBOL);
               printBoard(board);
               if (checkGameOver(board, COMPUTER_SYMBOL, PLAYER_SYMBOL) != 0){
                  done = true;
               }
               System.out.print("Press enter to continue:");
               sc.nextLine();
            } 
            else {
               done = true;
            }            
         } while (done == false);
         
         //Once each game is finished, integer value returned by checkGameOver method is used to determine outcome of game
         if (checkGameOver(board, COMPUTER_SYMBOL, PLAYER_SYMBOL) == 1){
            System.out.println("You won, pogchamp!");
            //Rewrites stats file with new number of wins if player wins
            try {
               BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
               out.write((wins + 1) + "\n" + losses + "\n" + ties);
               out.close();
            }
            catch (IOException e){
               System.out.println(":( Something went wrong with opening or writing the file " + e);
            }
         }
         else if (checkGameOver(board, COMPUTER_SYMBOL, PLAYER_SYMBOL) == 2){
            System.out.println("The compooter won?");
            //Rewrites stats file with new number of losses if computer wins
            try {
               BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
               out.write(wins + "\n" + (losses + 1) + "\n" + ties);
               out.close();
            }
            catch (IOException e){
               System.out.println(":( Something went wrong with opening or writing your file " + e);
            }
         }
         else {
            System.out.println("Tie game, you filled up the entire board.");
            try {
               //Rewrites stats file with new number of ties if game was tied
               BufferedWriter out = new BufferedWriter(new FileWriter(fileName, false));
               out.write(wins + "\n" + losses + "\n" + ties + 1);
               out.close();
            }
            catch (IOException e){
               System.out.println(":( Something went wrong with opening or writing your file " + e);
            }
         }
         System.out.println();
         
         //Asks player if they want to play the game again
         System.out.println("Do you want to play again? (Enter yes or no)");
         valid5 = false;
         
         //While loop repeatedly accepts input until valid input is given
         while (valid5 == false){
            again = sc.nextLine();
            switch (again.toLowerCase()){
               case "yes": 
                  playAgain = true;
                  valid5 = true;
                  break;
               case "no":
                  playAgain = false;
                  valid5 = true;
                  break;
               default:
                  System.out.println("Sorry, I didn't quite get that. Please try again.");
            }
         }
      } while (playAgain == true);
      
      //If player chooses to not play again, a parting message is printed
      System.out.println();
      System.out.println("Good game! :)");
   }
   
   
   /*----------------------------------------------------------------------------------------------------------------|
   | void fillBoard (char [] [] boardArray)                                                                          |
   |-----------------------------------------------------------------------------------------------------------------|
   | returns void - nothing to see here                                                                              |
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and fills it with blank spaces to reset the board.                       |                                                                                                                           
   |________________________________________________________________________________________________________________*/
   
   public static void fillBoard(char [] [] boardArray){
      //Nested for loop fills each position in array with a blank space
      for (int i = 0; i < ROWS; i++){
         for (int j = 0; j < COLUMNS; j++){
            boardArray [i] [j] = ' ';   
         }
      }
   }
   
  
   /*----------------------------------------------------------------------------------------------------------------|
   | void printBoard (char [] [] boardArray)                                                                         |
   |-----------------------------------------------------------------------------------------------------------------|
   | returns void - nothing to see here                                                                              |
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and prints it in a table format, with labels on the top and side for the |
   | row and column numbers. The printed result is a visual representation of the game board.                        |                                                                                                                                                                                                                   
   |________________________________________________________________________________________________________________*/
   
   public static void printBoard(char [] [] boardArray){
      //Prints top labels for column numbers
      System.out.print("   ");
      for (int header = 1; header <= 7; header++){
         System.out.print(header + "  ");
      }
      System.out.println();
      
      //Prints side labels for row numbers + array in table format 
      for (int i = 0; i < 6; i++){
         System.out.print(6 - i + "  ");
         for (int j = 0; j < 7; j++){
            System.out.print(boardArray [i] [j] + "  ");
         }
         System.out.println();
      } 
      System.out.println(); 
   }
   
   
   /*----------------------------------------------------------------------------------------------------------------|
   | void dropToken (int colNum, char [] [] boardArray, char playerSymbol)                                           |
   |-----------------------------------------------------------------------------------------------------------------|
   | returns boolean - whether the column chosen is full or not                                                      |                                                          
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   | int colNum - the integer which indicates which column the player wants to drop a token in                       |
   | char playerSymbol - the char which represents the player's token                                                |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, an integer, and a symbol, and fills the lowest empty row of the array at |
   | that column (which has an index equal to the integer - 1) with the provided symbol. If the specified column was |
   | not previously completely full, true is returned, and if the specified column was previously completely filled, |
   | the array is not changed and false is returned.                                                                 |                                                                                                                           
   |________________________________________________________________________________________________________________*/  

   public static boolean dropToken(int colNum, char [] [] boardArray, char playerSymbol){
      //Declaration of variables
      int rowNum = 5;
      boolean valid = false;
      
      try {
         //While loop goes through each row in the selected column, bottom up, until an empty space is found
         while (boardArray [rowNum] [colNum - 1] != ' '){
            rowNum--;
         }
         boardArray [rowNum] [colNum - 1] = playerSymbol;
         valid = true;
      }
      //If no spaces are empty, the value returned remains false (error is caught)
      catch (ArrayIndexOutOfBoundsException e){
      } 
      return valid;
   }
   

   
   /*----------------------------------------------------------------------------------------------------------------|
   | int checkGameOver(char [] [] boardArray, char compSymbol, char playerSymbol                                     |                                                                                     
   |-----------------------------------------------------------------------------------------------------------------|
   | returns int - a value indicating whether anyone/who won the game                                                |              
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   | char compSymbol - the char used to represent the computer's tokens                                              |
   | char playerSymbol - the char that is used to represent the player's tokens                                      |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and uses other methods in the program to check whether the game is over. |
   | Depending on the values given by the other methods, it returns an int value of 0, 1, 2, or 3. 0 indicates that  |
   | the game is not over, 1 indicates the player won, 2 indicates the computer won, and 3 indicates that the game   |
   | was tied.                                                                                                       |                                                                                                                                                                                                                  
   |________________________________________________________________________________________________________________*/
   
   public static int checkGameOver(char [] [] boardArray, char compSymbol, char playerSymbol){
      //Declaration of variable
      int winner;
      
      //Uses other methods to see if any columns, rows, or diagonals have 4 in a row from player or computer
      if (checkFull(boardArray) == true && checkRows(boardArray, compSymbol, playerSymbol) == 0){
         winner = 3;
      }
      else if (checkRows(boardArray, compSymbol, playerSymbol) == 1){
         winner = 1;
      }
      else if (checkRows(boardArray, compSymbol, playerSymbol) == 2){
         winner = 2;
      }
      else if (checkCol(boardArray, compSymbol, playerSymbol) == 1){
         winner = 1;
      }
      else if (checkCol(boardArray, compSymbol, playerSymbol) == 2){
         winner = 2;
      }
      else if (checkDiag(boardArray, compSymbol, playerSymbol) == 1){
         winner = 1;
      }
      else if (checkDiag(boardArray, compSymbol, playerSymbol) == 2){
         winner = 2;
      }
      else if (checkDiag2(boardArray, compSymbol, playerSymbol) == 1){
         winner = 1;
      }
      else if (checkDiag2(boardArray, compSymbol, playerSymbol) == 2){
         winner = 2;
      }
      else {
         winner = 0;
      }
      return winner;
   }
   
   /*----------------------------------------------------------------------------------------------------------------|
   | boolean checkFull(char [] [] boardArray)                                                                        |                                                                                     
   |-----------------------------------------------------------------------------------------------------------------|
   | returns boolean - indicates whether the board is full                                                           |              
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and checks whether any of the addresses have a value of ' '. If not, the |
   | board is full, and a value of true is returned. If there are any empty spaces, a value of false is returned.    |                                                                                                                                                                                                                 
   |________________________________________________________________________________________________________________*/
   
   public static boolean checkFull(char [] [] boardArray){
      //Declaration of variable
      boolean full = true;
      
      //Nested for loop iterates through each address in the array and checks whether it's filled
      for (int i = 0; i < ROWS; i++){
         for (int j = 0; j < COLUMNS; j++){
            if (boardArray [i] [j] == ' '){
               full = false;
            }
         }
      }
      return full;
   }
   
   
   /*----------------------------------------------------------------------------------------------------------------|
   | int checkRows(char [] [] boardArray, char compSymbol, char playerSymbol)                                        |                                                                                     
   |-----------------------------------------------------------------------------------------------------------------|
   | returns int - a value indicating whether anyone/who got four in a row                                           |              
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   | char compSymbol - the char used to represent the computer's tokens                                              |
   | char playerSymbol - the char that is used to represent the player's tokens                                      |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and checks whether either the player or the computer has 4 in a row.  If |
   | the player has four in a row, 1 is returned. If the computer has four in a row, 2 is returned. If there are no  |
   | rows, 0 is returned.                                                                                            |                                                                                                                                                                                                                  
   |________________________________________________________________________________________________________________*/
   
   public static int checkRows(char [] [] boardArray, char compSymbol, char playerSymbol){
      //Declaration of variables
      int compooterCount, playerCount, anyoneWin = 0;
      
      //First loop iterates through each row
      for (int i = 0; i < ROWS; i++){
      
         //Second loop iterates through the starting value of the columns of each group of 4
         for (int j = 0; j < 4; j++){
         compooterCount = 0;
         playerCount = 0;
         
            //Third loop iterates through each group of 4 and checks the symbol in each position
            for (int k = j; k <= j + 3; k++){
               if (boardArray [i] [k] == compSymbol){
                  compooterCount++;
               }
               else if (boardArray [i] [k] == playerSymbol){
                  playerCount++;
               }
            }
            //If either the computer or player has a 4 in a row in one of the groups, the nested loop stops
            if (compooterCount == 4){
               anyoneWin = 2;
               break;
            }
            else if (playerCount == 4){
               anyoneWin = 1;
               break;
            }
            else {
               anyoneWin = 0;
            }
         }
         if (anyoneWin != 0){
            break;
          }
      }  
      return anyoneWin;   
   }
   
      
   /*----------------------------------------------------------------------------------------------------------------|
   | int checkCol(char [] [] boardArray, char compSymbol, char playerSymbol)                                         |                                                                                     
   |-----------------------------------------------------------------------------------------------------------------|
   | returns int - a value indicating whether anyone/who got four in a column                                        |             
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   | char compSymbol - the char used to represent the computer's tokens                                              |
   | char playerSymbol - the char that is used to represent the player's tokens                                      |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and checks whether either the player or the computer has 4 in a column.  |
   | If the player has 4 in a column, 1 is returned. If the computer has four in a column, 2 is returned. If there   |
   | no four in a columns, 0 is returned.                                                                            |                                                                                                                                                                                                                 
   |________________________________________________________________________________________________________________*/
   
   public static int checkCol(char [] [] boardArray, char compSymbol, char playerSymbol){
   //Declaration of variables
   int playerCount, computerCount, anyoneWin = 0;
   
      //First for loop iterates through each column
      for (int i = 0; i < COLUMNS; i++){
      
         //Second for loop iterates through the starting row index for each group of 4
         for (int j = 0; j < 3; j++){
            playerCount = 0; 
            computerCount = 0;
            
            //Third for loop iterates through each group of four and checks the symbol in each position
            for (int k = j; k < j + 4; k++){
               if (boardArray [k] [i] == compSymbol){
                  computerCount++;
               }
               else if (boardArray [k] [i] == playerSymbol){
                  playerCount++;
               }
            }
            
            //If either the computer or player has a 4 in a column in one of the groups, the nested loop stops 
            if (computerCount == 4){
                anyoneWin = 2;
                break;
             }
             else if (playerCount == 4){
                anyoneWin = 1;
                break;
             }
             else {
                anyoneWin = 0; 
             }
          }
          if (anyoneWin != 0){
            break;
          }
       }
       return anyoneWin;
   }
   
   
   /*----------------------------------------------------------------------------------------------------------------|
   | int checkDiag(char [] [] boardArray, char compSymbol, char playerSymbol)                                        |                                                                                     
   |-----------------------------------------------------------------------------------------------------------------|
   | returns int - a value indicating whether anyone/who got four in a diagonal                                      |             
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   | char compSymbol - the char used to represent the computer's tokens                                              |
   | char playerSymbol - the char that is used to represent the player's tokens                                      |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and checks whether the array has any diagonals going from up to down.    |
   | If the player has a diagonal, 1 is returned. If the computer has a diagonal, 2 is returned. If neither the      |
   | player nor the computer has a diagonal, 0 is returned.                                                          |                                                                                                                                                                                                                
   |________________________________________________________________________________________________________________*/
   
   public static int checkDiag(char [] [] boardArray, char compSymbol, char playerSymbol){
      //Declaration of variables
      int computerCount, playerCount, anyoneWin = 0;
      
      //For loop counts down the row which the diagnoal starts on 
      for (int i = 2; i >= 0; i--){
      
         //For loop counts up the difference between the row index and column index for the first "half" of the digonals, based on i value
         for (int j = 0; j <= i; j++){
            computerCount = 0;
            playerCount = 0;
            
            //For loop iterates through each row index, starting with the i value and symbol counts are incremented as needed
            for (int k = i; k < i + 4; k++){
               if (boardArray [k] [k - j] == compSymbol){   //Difference (j) is subtracted from row index to find column index
                  computerCount++;
               }
               else if (boardArray [k] [k - j] == playerSymbol){
                  playerCount++;
               }
            }
            
            //If either the player or the computer has a diagonal, the loop stops
            if (computerCount == 4){
               anyoneWin = 2;
               break;
            }
            else if (playerCount == 4){
               anyoneWin = 1;
               break;
            }
         }
         if (anyoneWin != 0){
            break;
         }
         
         //For loop counts up difference between column index and row index for second "half" of the digonals, based on i value 
         for (int j = 3 - i; j > 0; j--){
            computerCount = 0;
            playerCount = 0;
            //For loop iterates through each row index, starting with the i value and symbol counts are incremented as needed
            for (int k = i; k < i + 4; k++){
               if (boardArray [k] [k + j] == compSymbol){   //Difference (j) is added to each row index to find the column index
                  computerCount++;
               }
               else if (boardArray [k] [k + j] == playerSymbol){
                  playerCount++;
               }
            }
            //If either the player or the computer has a diagonal, the loop stops
            if (computerCount == 4){
               anyoneWin = 2;
               break;
            }
            else if (playerCount == 4){
               anyoneWin = 1;
               break;
            }
         }
         if (anyoneWin != 0){
            break;
         }
      }
      return anyoneWin;
   }   
   
   
   /*----------------------------------------------------------------------------------------------------------------|
   | int checkDiag2(char [] [] boardArray, char compSymbol, char playerSymbol)                                       |                                                                                     
   |-----------------------------------------------------------------------------------------------------------------|
   | returns int - a value indicating whether anyone/who got four in a diagonal                                      |             
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   | char compSymbol - the char used to represent the computer's tokens                                              |
   | char playerSymbol - the char that is used to represent the player's tokens                                      |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and checks whether the array has any diagonals going from down to up.    |
   | If the player has a diagonal, 1 is returned. If the computer has a diagonal, 2 is returned. If neither the      |
   | player nor the computer has a diagonal, 0 is returned.                                                          |                                                                                                                                                                                                                
   |________________________________________________________________________________________________________________*/  

   public static int checkDiag2(char [] [] boardArray, char compSymbol, char playerSymbol){
      //Declaration of variables
      int computerCount, playerCount, anyoneWin = 0;
      
      //For loop counts down the row which the diagnoal starts on
      for (int i = 5; i >= 3; i--){
         
         //For loop counts down the values for starting col for the first "half" of the digonals, based on i value
         for (int j = 5 - i; j >= 0; j--){
            computerCount = 0;
            playerCount = 0;
            
            //For loop iterates through numbers needed to check the four addresses in the diagonal, variables are incremented as needed
            for (int k = 0; k <= 3; k++){
               if (boardArray [i - k] [j + k] == compSymbol){  //Row decreases by one at a time, col increases
                  computerCount++;
               }
               else if (boardArray [i - k] [j + k] == playerSymbol){
                  playerCount++;
               }   
            }
            
            //If anyone has four in a row, the loop stops
            if (computerCount == 4){
               anyoneWin = 2;
               break;
            }
            else if (playerCount == 4){
               anyoneWin = 1;
               break;
            }
         }
         if (anyoneWin != 0){
            break;
         }
         
         //For loop counts down the values for starting col for the second "half" of the digonals, based on i value
         for (int j = 3; j >= 6 - i; j--){
            computerCount = 0;
            playerCount = 0;
            
            //For loop iterates through numbers needed to check the four addresses in the diagonal, variables are incremented as needed
            for (int k = 0; k <= 3; k++){
               if (boardArray [i - k] [j + k] == compSymbol){  //Row decreases by one at a time, col increases
                  computerCount++;
               }
               else if (boardArray [i - k] [j + k] == playerSymbol){
                  playerCount++;
               }   
            }
            
            //If anyone has a diagonal, the loop stops
            if (computerCount == 4){
               anyoneWin = 2;
               break;
            }
            else if (playerCount == 4){
               anyoneWin = 1;
               break;
            }
         }
         if (anyoneWin != 0){
            break;
         }
      }
      return anyoneWin;
   } 
   
   
   /*----------------------------------------------------------------------------------------------------------------|
   | int computerTurn(char [] [] boardArray, char compSymbol, char playerSymbol)                                     |                                                                                     
   |-----------------------------------------------------------------------------------------------------------------|
   | returns void - nothing to see here                                                                              |             
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   | char compSymbol - the char used to represent the computer's tokens                                              |
   | char playerSymbol - the char that is used to represent the player's tokens                                      |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and uses other methods within the program to determine the best column   |
   | for the computer to drop its token in. It then drops the token. If there is no clear-cut logical choice, a      |
   | random column is selected.                                                                                      |                                                                                                                                                                                                                
   |________________________________________________________________________________________________________________*/ 
   
   public static void computerTurn(char [] [] boardArray, char compSymbol, char playerSymbol){
      //Declaration of variables
      int rowNum, colNum;
      boolean valid = false;
      
      //Blocks off player's columns, or completes its own
      if (computerCheckCol(boardArray, compSymbol, playerSymbol) != -1){
         rowNum = ROWS - 1;
         colNum = computerCheckCol(boardArray, compSymbol, playerSymbol);
       
            while (boardArray [rowNum] [colNum] !=  ' '){
               rowNum--;
            }
            boardArray [rowNum] [colNum] = compSymbol;  
         
      }
      
      //Blocks off player's rows, or completes its own
      else if (computerCheckRows(boardArray, compSymbol, playerSymbol) != -1){
         rowNum = ROWS - 1;
         colNum = computerCheckRows(boardArray, compSymbol, playerSymbol);
            while (boardArray [rowNum] [colNum] !=  ' '){
               rowNum--;
            }
            boardArray [rowNum] [colNum] = compSymbol; 
      }
      
      //Tries to drop a token in a random slot until one is found that isn't full
      else {
         do {
            rowNum = ROWS - 1;
            try {
               colNum = (int) (Math.random() * 7);
               while (boardArray [rowNum] [colNum] !=  ' '){
                  rowNum--;
               }
               valid = true;
               boardArray [rowNum] [colNum] = compSymbol;
            }
            catch (ArrayIndexOutOfBoundsException e) {
               valid = false;
            }
         } while (valid == false);
      }
   }    
   
   /*----------------------------------------------------------------------------------------------------------------|
   | int computerCheckCol(char [] [] boardArray, char computerSymbol, char playerSymbol)                             |                                                                                     
   |-----------------------------------------------------------------------------------------------------------------|
   | returns int - a value indicating which column the computer should pick                                          |             
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   | char compSymbol - the char used to represent the computer's tokens                                              |
   | char playerSymbol - the char that is used to represent the player's tokens                                      |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and checks whether the array has any almost complete columns. If the     |
   | player has 3 consecutive tokens in a column, the computer drops a token in that column to block it off. If the  |
   | computer has 3 consecutive tokens in a column, the player drops a token to complete its own column. If neither  |
   | the player nor computer has 3 consecutive tokens, a value of -1 is returned, indicating that the computer       |
   | should choose a random column.                                                                                  |                                                                                                                                                                                                                
   |________________________________________________________________________________________________________________*/ 
   
   public static int computerCheckCol(char [] [] boardArray, char computerSymbol, char playerSymbol){
      //Declaration of variables
      int colNum, playerCount = 0, computerCount = 0;
      colNum = -1;
      
      //First for loop iterates through each column
      for (int i = 0; i < COLUMNS; i++){
      
         //Second for loop iterates through the starting row index for each group of 3
         for (int j = 5; j > 2; j --){
            playerCount = 0; 
            
            //Third for loop iterates through each group of 3 and checks the symbol in each position, variables are incremented as needed
            for (int k = j; k > j - 3; k--){
               //Second part of if statement ensures that computer isn't stuck dropping in the same column
               if (boardArray [k] [i] == playerSymbol && boardArray [k - 1] [i] != computerSymbol){
                  playerCount++;
               }
               if (boardArray [k] [i] == computerSymbol){
                  computerCount++;
               }
            }
            
            //If either computer or player has 3 in a column, column number is indicated as the current column and loop exits
            if (playerCount == 3 || computerCount == 3){
               colNum = i;
               break;
            }
         }
         if (playerCount == 3 || computerCount == 3){
             break;
         }
      }
      return colNum;
   }
   
   /*----------------------------------------------------------------------------------------------------------------|
   | int computerCheckRows(char [] [] boardArray, char computerSymbol, char playerSymbol)                            |                                                                                     
   |-----------------------------------------------------------------------------------------------------------------|
   | returns int - a value indicating which column the computer should pick                                          |             
   |-----------------------------------------------------------------------------------------------------------------|
   | char [] [] boardArray - this is the 2d array used to store the board                                            |
   | char compSymbol - the char used to represent the computer's tokens                                              |
   | char playerSymbol - the char that is used to represent the player's tokens                                      |
   |-----------------------------------------------------------------------------------------------------------------|
   | This method takes a 2D array of chars, and checks whether the array has any almost complete rows. If the player |
   | has 3 consecutive tokens in a row, the computer drops a token in an appropriate column to block it off. If the  |
   | computer has 3 consecutive tokens in a row, the player drops a token to complete its own row. If neither the    |
   | the player nor computer has 3 consecutive tokens, a value of -1 is returned, indicating that the computer       |
   | should choose a random column.                                                                                  |                                                                                                                                                                                                                
   |________________________________________________________________________________________________________________*/ 

   public static int computerCheckRows(char [] [] boardArray, char computerSymbol, char playerSymbol){
      //Declaration of variables
      int colNum, playerCount = 0, computerCount = 0;
      colNum = -1;
      
      //First loop iterates through each row
      for (int i = 0; i < ROWS; i++){
      
         //Second loop iterates through the starting value of the columns of each group of 3 from left to right
         for (int j = 0; j <= 3; j++){
         computerCount = 0;
         playerCount = 0;
         
            //Third loop iterates through each group of 3 and checks the symbol in each position
            for (int k = j; k <= j + 2; k++){
               if (boardArray [i] [k] == computerSymbol){
                  computerCount++;
               }
               //Second part of if statement ensures computer isn't stuck dropping in the same column
               else if (boardArray [i] [k] == playerSymbol && boardArray [i] [k + 1] != computerSymbol){
                  playerCount++;
               }
            }
            
            //If either the computer or player has a 3 in a row in one of the groups, the nested loop stops
            if (computerCount == 3 || playerCount == 3){
               //Computer doesn't drop in column which would typically be used to block off a row if there is empty space under desired location, meaning a token there would help the player
               if (boardArray [i - 1] [j + 3] != ' ' || i == 5){
                  colNum = j + 3;
               } 
               break;
            }
         }
         
         //Second loop iterates through the starting value of the columns of each group of 3 from right to left
         for (int j = 6; j >= 3; j--){
         computerCount = 0;
         playerCount = 0;
         
            //Third loop iterates through each group of 3 and checks the symbol in each position
            for (int k = j; k >= j - 2; k--){
               if (boardArray [i] [k] == computerSymbol){
                  computerCount++;
               }
               //Second part of if statement ensures computer doesn't keep dropping in same col
               else if (boardArray [i] [k] == playerSymbol  && boardArray [i] [k - 1] != computerSymbol){
                  playerCount++;
               }
            }
            
            //If either the computer or player has a 3 in a row in one of the groups, the nested loop stops
            if (computerCount == 3 || playerCount == 3){
               //Computer doesn't drop in column which would typically be used to block off a row if there is empty space under desired location, meaning a token there would help the player
               if (boardArray [i - 1] [j - 3] != ' ' || i == 5){
                  colNum = j - 3;
               }
               break;
            }
         }
         if (colNum != -1){
            break;
         }
      }  
      return colNum;   
   }
}