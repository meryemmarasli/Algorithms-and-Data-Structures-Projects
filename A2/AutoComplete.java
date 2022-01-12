import java.util.*;
import java.io.*;

public class AutoComplete{
  private DLBNode root;
  //Add instance variable: you should have at least the tree root

  public AutoComplete(String dictFile) throws java.io.IOException {
    //Initialize the instance variables
    Scanner fileScan = new Scanner(new FileInputStream(dictFile));
    while(fileScan.hasNextLine()){
      StringBuilder word = new StringBuilder(fileScan.nextLine());
      //call the public add method or the private helper method
      add(word);
    }
    fileScan.close();
  }

  /**
   * Part 1: add, increment score, and get score
   */

  //add word to the tree
  public void add(StringBuilder word){
      if (word == null) throw new IllegalArgumentException("calls put() with a null key");
      root = add(root, word, 0);
  }

//private helper function that adds the word to the DLB trie
  private DLBNode add(DLBNode x, StringBuilder word, int pos){
      DLBNode result = x;
      if (result == null){
          result = new DLBNode(word.charAt(pos), 0);
          if(pos < word.length()-1){
            result.child = add(result.child, word, pos+1);
          }else{
            result.isWord = true;
          }
      }else if(result.data == word.charAt(pos)) {
          if(pos < word.length()-1){
            result.child = add(result.child, word, pos+1);
          }else{
            result.isWord = true;
          }
      }else{
        result.sibling = add(result.sibling, word, pos);
      }
      return result;
  }




  //increment the score of word
  public void notifyWordSelected(StringBuilder word){
    DLBNode node  = getNode(root, word.toString(), 0);
    if(node != null){
      node.score++;
    }
  }

  //get the score of word
  public int getScore(StringBuilder word){
    DLBNode node = getNode(root, word.toString(), 0);
    if(node == null) return 0;
    return node.score;

  }

  /**
   * Part 2: retrieve word suggestions in sorted order.
   */

  //retrieve a sorted list of autocomplete words for word. The list should be sorted in descending order based on score.
  public ArrayList<Suggestion> retrieveWords(StringBuilder word){
    //finds all the words that has stringbuilder word as a prefix
    ArrayList<Suggestion> s = new ArrayList<Suggestion>();
    StringBuilder partialSolution = new StringBuilder();
    buildWords(partialSolution, root, word, s);

    Collections.sort(s, Collections.reverseOrder());
    return s;
  }



  // recursive function that builds the words
  private void buildWords(StringBuilder partialSolution, DLBNode n, StringBuilder word, ArrayList<Suggestion> s){
    if(n == null){
      return;
    }

    DLBNode currNode = n;

    while(currNode != null){
      partialSolution.append(currNode.data);
      // checking if word is a valid prefix of partialsolution
      if(isValid(partialSolution.toString(), word.toString())){
        // checking if it is a word
          if(currNode.isWord  && partialSolution.length() >= word.length()){

            s.add(new Suggestion(new StringBuilder(partialSolution.substring(0, partialSolution.length())), currNode.score));
        }

        // recurse to child
        buildWords(partialSolution, currNode.child, word, s);


      }
      partialSolution.deleteCharAt(partialSolution.length()-1);
      //move to sibling if not valid
      currNode = currNode.sibling;
    }
}


  // checks if the word is valid
  private boolean isValid(String partialSolution, String word){
    if(partialSolution.length() < word.length()){
      //partial solution is shorter than target prefix
       if(partialSolution.equals(word.substring(0, partialSolution.length()))){
        return true;
       }
    }else if(partialSolution.length() == word.length()){
        if(partialSolution.equals(word)){
            return true;
        }
    }else{
        if(partialSolution.substring(0, word.length()).equals(word)){
          return true;
        }
      }
      return false;
    }


  /**
   * Helper methods for debugging.
   */

  //Print the subtree after the start string
  public void printTree(String start){
    System.out.println("==================== START: DLB Tree Starting from "+ start + " ====================");
    DLBNode startNode = getNode(root, start, 0);
    if(startNode != null){
      printTree(startNode.child, 0);
    }
    System.out.println("==================== END: DLB Tree Starting from "+ start + " ====================");
  }

  //A helper method for printing the tree
  private void printTree(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
        System.out.println(" (" + node.score + ")");
      printTree(node.child, depth+1);
      printTree(node.sibling, depth);
    }
  }

  //return a pointer to the node at the end of the start string. Called from printTree.
  private DLBNode getNode(DLBNode node, String start, int index){
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = node;
      } else {
          result = getNode(node.sibling, start, index);
      }
    }
    return result;
  }


  //A helper class to hold suggestions. Each suggestion is a (word, score) pair.
  public class Suggestion implements Comparable <Suggestion>  {
    public StringBuilder word;
    public int score;

    public Suggestion(StringBuilder word, int score){
      this.word = word;
      this.score = score;
   }

    public int compareTo(Suggestion s){
      return Integer.compare(this.score, s.score);
    }




  }

  //The node class.
  private class DLBNode{
    private Character data;
    private int score;
    private boolean isWord;
    private DLBNode sibling;
    private DLBNode child;

    private DLBNode(Character data, int score){
        this.data = data;
        this.score = score;
        isWord = false;
        sibling = child = null;
    }
  }
}
