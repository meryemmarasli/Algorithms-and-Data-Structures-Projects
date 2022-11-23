import java.io.*;
import java.util.*;

public class Crossword
{
	private DictInterface D;
	private char [][] theBoard;
  private int size;
  private char [] alphabet;
	private int[] score;
  private int currLetter = 0;
  private int depth = 0;
	public static void main(String [] args) throws IOException
	{

    new Crossword(args[0], args[1]);
	}

	public Crossword(String a, String b) throws IOException{
		//Read the dictionary
		Scanner fileScan = new Scanner(new FileInputStream(a));
		String st;
		D = new MyDictionary();

		while (fileScan.hasNext())
		{
			st = fileScan.nextLine();
			D.add(st);
		}
		fileScan.close();

		//getting in crossword board

		Scanner inScan = new Scanner(new FileInputStream (b));
	  String s = inScan.nextLine();
    size = Integer.parseInt(s);
    Scanner fReader;

    StringBuilder[] colStr = new StringBuilder[size];
    StringBuilder[] rowStr = new StringBuilder[size];


		theBoard = new char[size][size];

		for (int i = 0; i < size; i++)
		{
			String rowString = inScan.nextLine();
			for (int j = 0; j < rowString.length(); j++)
			{
				theBoard[i][j] = Character.toLowerCase(rowString.charAt(j));
			}
		}
		inScan.close();




		// initializing rowStr and colStr
		for (int i = 0; i < size; i++)
		{
      rowStr[i] = new StringBuilder();
      colStr[i] = new StringBuilder();
 			}



			//taking in the letterpoints file
    Scanner scan = new Scanner(new FileInputStream ("letterpoints.txt"));




    alphabet = new char[26];
		score = new int[26];
    for (int i = 0; i < 26; i++)
    {
        alphabet[i] = Character.toLowerCase(scan.next().charAt(0));
				score[i] = scan.nextInt();



    }
    scan.close();



		//calling initial solve function
					solve(0, 0, rowStr, colStr);




}


		//calculates the score of the board
		private int getScore(StringBuilder[] rowStr){

			int scoreIndex = 0;
			int value = 0;
			for(int i = 0; i < size; i++){
				for(int j = 0; j <rowStr[i].length(); j++)
					if(rowStr[i].charAt(j) != '-'){
						scoreIndex = getScoreIndex(rowStr[i].charAt(j));
						value = value+score[scoreIndex];
				}
			}
			return value;

		}

		//gets corresponding index of score and alphabet
		private int getScoreIndex(char c){
					for(int j = 0; j < 26; j++){
							if(c == alphabet[j]){
								return j;
							}

					}
					return -1;
		}








			private  void solve(int i, int j, StringBuilder[] rowStr, StringBuilder[] colStr ){
		    switch(theBoard[i][j]){
					//checking plus case
		      case '+':
		         for(char c = 'a'; c <= 'z'; c++){
		            if(isValid(c,i,j,rowStr[i],colStr[j])){
		                valid(c,rowStr, colStr, i , j);
		            }
		         }
		        break;
					//checking minus case
		      case '-':
		            valid('-', rowStr, colStr, i, j);
		        break;

					//checking letter case
		      default:
		          if(isValid(theBoard[i][j],i, j, rowStr[i], colStr[j])){
		            valid(theBoard[i][j], rowStr, colStr, i, j);

		          }

		    }

		}


			//checking if rowStr + letter and colStr + letter is valid
			private boolean isValid(char c, int i, int j, StringBuilder rowStr, StringBuilder colStr){

		/*need to check for each case:
		      in total should at least be 6 different cases;


		*/
		      boolean result = false;
		      rowStr.append(c);
		      colStr.append(c);


					// if the length() is greater than board size, return
		      if(rowStr.length() > size || colStr.length() > size){
		        rowStr.deleteCharAt(rowStr.length()-1);
		        colStr.deleteCharAt(colStr.length()-1);
		        return false;
		      }
		    //Note: prefix == 1, word == 2 prefix&word == 3
				int rowStart = 0;
				int rowEnd = rowStr.length()-1;
				int colStart = 0;
				int colEnd = colStr.length()-1;
				boolean colCheckWord = false;
				boolean rowCheckWord= false;
				//handling minus cases
				if(c == '-'){
					rowEnd = rowStr.length() -2;
					rowCheckWord = true;
					colEnd = colStr.length()-2;
					colCheckWord = true;
				}else if(contains('-', rowStr)){
					//row
					rowStart = minusIndex(rowStr) + 1;

				}else if(contains('-', colStr)){
					colStart = minusIndex(colStr) + 1;

				}
				//getting searchPrefix values for row and col
				int r1 = D.searchPrefix(rowStr, rowStart, rowEnd); // 2 or 3
				int r2 = D.searchPrefix(colStr, colStart, colEnd); //2 or 3

				//checking if at last part of the board
		    if(i == size -1 && j == size -1){
		        // both need to be a word


		        if((r1 == 2 || r1 == 3) && (r2 == 2 || r2 == 3)){
		          result = true;
		        }

				//checking if at last col middle rows
		    }else if(i >= 0 && i < size-1 && j == size-1){
		      //colStr[j] === prefix
		      //rowStr[i] == word
					int value1 = 1;

					if(colCheckWord == true){
						value1 = 2;
					}
		      if((r1 == 2 || r1 == 3) && (r2 == value1 || r2 == 3)){
		        result = true;
		      }

					//checking if at middle of the board
		    }else if(i>= 0 && i < size-1 && j >= 0 && j <size-1){
		      //both prefix
					int colvalue1 = 1;
					int rowValue1 = 1;
					if(colCheckWord == true){
						colvalue1 = 2;
					}
					if(rowCheckWord == true){
						rowValue1 = 2;
					}

		      if((r1 == rowValue1 || r1 == 3) && (r2 == colvalue1|| r2 == 3)){
		        result = true;
		      }
					//checking if at middle col end row
		    }else if(j>= 0 && j <size-1 && i == size-1){
		      //colStr[j] == word
		      //rowStr[i]  == prefix
					int value1 = 1;
					if(rowCheckWord == true){
						value1 = 2;
					}

		      if((r1 == value1 || r1 == 3) && (r2 == 2 || r2 == 3)){
		          result= true;

		     }


			 }

			 //deleting char
		   rowStr.deleteCharAt(rowStr.length()-1);
		   colStr.deleteCharAt(colStr.length()-1);
		   return result;
		}


		//private boolean contains(StringBuilder s, )
		//returns index of the minus
		private int minusIndex(StringBuilder s){
		  for(int i = 0; i < s.length(); i++){
		    if(s.charAt(i) == '-'){
		    	return i;
		    }
			}
		    return -1;
		}

		//check to see if stringbuilder contains a char
			private boolean contains(char c, StringBuilder s){
				for(int i = 0; i < s.length(); i++){
					if(c == s.charAt(i)){
						return true;
					}

				}
				return false;

			}



			//executes if the isValid works (essentially appends letter on to rowStr and colStr) and moves onto next step depending on position of coordinates
		private void valid(char c, StringBuilder[] rowStr, StringBuilder[] colStr, int i , int j ){
		    rowStr[i].append(c);
		    colStr[j].append(c);
		    if(i == size -1 && j == size -1){
		      printSolution(rowStr);
					int score = getScore(rowStr);
					System.out.println("Score: " + score);
		      System.exit(0);
		    }else{
		      int nextI = 0;
		      int nextJ = 0;
		      if(j == (size-1)){
		        nextJ = 0;
		        nextI  = i +1;
		      }else{
		        nextJ = j+1;
		        nextI = i;
		      }


		      solve(nextI, nextJ, rowStr, colStr);
		    }
		    rowStr[i].deleteCharAt(rowStr[i].length()-1);
		    colStr[j].deleteCharAt(colStr[j].length()-1);

		}


		//prints solution
		  private void printSolution(StringBuilder[] rowStr){
		      for( int i = 0; i < size; i++){
		        System.out.println(rowStr[i]);
		      }
		  }





}
