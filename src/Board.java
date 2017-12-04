/**
 * Board.java
 *
 * Version:
 *     $Id: Board.java,v 1.1 2002/10/22 21:12:52 se362 Exp $
 *
 * Revisions:
 *     $Log: Board.java,v $
 *     Revision 1.1  2002/10/22 21:12:52  se362
 *     Initial creation of case study
 *
 */
import java.util.*;
import java.awt.*;


/**
 *  This class represents the board on which checkers is being played.
 *  The board holds a collection of pieces.
 *
 *  @invariant all variables have valid values
 *
 *  @author
 */
public class Board {

   public Piece pieces[][]; // the pieces that are on the board
   public static int SINGLE = 0;
   public static int KING = 1;


   /**
    * This constructor creates a new board at the beginning of the game
    */

   public Board() {
  
	   // create a array of size 64, generate piece objects and
	   // put them in the correct location in the array
	   // Set the values of numWhites and numBlues to 12 each
	   pieces = new Piece[8][8];

	   // create white pieces
	   for(int y = 0; y < 3; y++ ){
	   		for (int x = 0; x < 8; x+=2){
	   			pieces[y][x + (y % 2)] = new SinglePiece(Color.white);
			}
	   }

       // create black pieces
       for(int y = 5; y < 8; y++ ){
           for (int x = 0; x < 8; x+=2){
               pieces[y][x + (y % 2)] = new SinglePiece(Color.blue);
           }
       }

//	   for(int i = 0; i < 8; i++){
//		   for(int j = 0; j < 8; j++){
//			   if(pieces[i][j] == null)
//				   System.out.print('.' + "\t");
//			   else
//				   System.out.print("X" + "\t");
//		   }
//		   System.out.print("\n");
//	   }

   }

   

   /**
    * Move the piece at the start position to the end position
    * 
    * @param start - current location of the piece
    * @param end - the position where piece is moved
    * 
    * @return -1 if there is a piece in the end position
    */
   public int movePiece(Point start, Point end) {


	   int returnValue = 0;	

	   // check if the end position of the piece is occupied
	   if( occupied( end ) ) {
	   	
	   	   // if it is return -1
	   	   returnValue = -1;


	   // if it is not set a start position in the array to null
	   // put a piece object at the end position in the array
	   // that was at the start positon before
	   } else {
	   	
		   pieces[end.x][end.y] = pieces[start.x][start.y];
		   pieces[start.x][start.y] = null;


	   }

	   return returnValue;

   }

   

   /**
    * This method checks if the space on the board contains a piece
    * 
    * @param space - the space that needs to be checked
    * 
    * @return true or false depending on the situation
    */
   public boolean occupied(Point space) {

	   return occupied(space.x, space.y);
	   
   }

	/**
	 * This method checks if the space on the board contains a piece
	 *
	 * @param x - the x of the space that needs to be checked
	 * @param y - the y of the space that needs to be checked
	 *
	 * @return true or false depending on the situation
	 */
	public boolean occupied(int x, int y) {

		return pieces[x][y] != null;

	}
   
   /**
    * This method removes piece at the position space
    * 
    * @param space - the positon of the piece to be removed
    */
   public void removePiece(Point space) {
   
	   // go to the space position in the array
	   // set it equal to null
	   
	   pieces[ space.x ][ space.y ] = null;

   }
   
   
   /**
    * This method creates a king piece 
    * 
    * @param space - the psotion at which the king piece is created 
    */
   public void kingPiece(Point space) {
   
	   // create a new king piece
	   // go to the space position in the array and place it there
	   // if the position is not ocupied
	   Color color = pieces[space.x][space.y].getColor();
	   Piece piece = new KingPiece( color );
	   pieces[space.x][space.y] = piece;
	   
   }
	/**
	 * This method returns the color of the piece at a certain space
	 *
	 * @param x - the x position of the piece on the board
	 * @param y - the y position of the piece on the board
	 *
	 * @return the color of this piece
	 */
	public Color colorAt(int x, int y)
	{
		return colorAt(new Point(x, y));
	}
   
   /**
    * This method returns the color of the piece at a certain space
    * 
    * @param space - the position of the piece on the board
    * 
    * @return the color of this piece
    */
   public Color colorAt(Point space) {
	   
	   Color returnValue = null;
	   // go to the space position in the array
	   // check if there is a piece at that position
	   // if there is none, return null
	   // else retrun the color of the piece
	   
	   if( occupied( space ) ) {
		   
		   returnValue = pieces[space.x][space.y].getColor();
		   
	   }
   
       return returnValue;
	   
   }
	/**
	 * This method returns the piece at the certain position
	 *
	 * @param x - the x of the space of the piece
	 * @param y - the y of the space of the piece
	 *
	 * @return the piece at that space
	 */
	public Piece getPieceAt(int x, int y)
	{
		return getPieceAt(new Point(x, y));
	}

   /**
    * This method returns the piece at the certain position
    * 
    * @param space - the space of the piece
    * 
    * @return the piece at that space
    */
   public Piece getPieceAt(Point space) {

	   Piece returnValue = new SinglePiece(Color.red);
	   
	   try{
	   	   // check if there is piece at space position
	   	   // if there is none, return null
	   	   // else return the piece at that position
	   
	      if( occupied(  space ) ) {
		   
	   	   	   returnValue = pieces[space.x][space.y];
                           
	   	   }
	   
	   } catch( ArrayIndexOutOfBoundsException e ) {
	   
                          
	   } catch( NullPointerException e ) {
	   	
           
	   }
	   
      return returnValue;
	   
   }
   
   
   
   /**
    * This method returns if there is a piece of color on the board
    * 
    * @param color - the color of the piece
    * 
    * @return true if there is a piece of color left on the board
    *				else return false	
    */
   public boolean hasPieceOf( Color color) {
	   for( int x=0; x<8;x++) {
		   for (int y = 0; y < 8; y++) {
			   if (pieces[x][y] != null && pieces[x][y].getColor() == color) {
				   return true;
			   }
		   }
	   }
      return false;
   }
   

   /**
    * This method returns the size of the board
    * 
    * @return the size of the board, always 64
    */
   public Point sizeOf() {
       return new Point(8, 8);
   }
   
   
   
   /**
    * This method returns a vector containing all blue Pieces
    * 
    * @return blue pieces on the board
    */
   public Vector bluePieces() {
   
      Vector bluePieces = new Vector();

	   for( int x=0; x<8;x++)
	   {
		   for (int y = 0; y < 8; y++)
		   {
			   if (occupied(x, y))
			   {
				   if (pieces[x][y].getColor() == Color.blue)
				   {
					   bluePieces.addElement(pieces[x][y]);
				   }
			   }
		   }
	   }
      
      return bluePieces;
      
  }
 
 
   /**
    * This method returns a vector containing all white Pieces
    * 
    * @return white pieces on the board
    */
  public Vector whitePieces() {

	  Vector whitePieces = new Vector();

	  for( int x=0; x<8;x++)
	  {
		  for (int y = 0; y < 8; y++)
		  {
			  if (occupied(x, y))
			  {
				  if (pieces[x][y].getColor() == Color.blue)
				  {
					  whitePieces.addElement(pieces[x][y]);
				  }
			  }
		  }
	  }

	  return whitePieces;
 }
 
}//Board

