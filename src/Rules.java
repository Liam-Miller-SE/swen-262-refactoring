/**
 * Rules.java
 *
 * Version:
 *    $Id: Rules.java,v 1.1 2002/10/22 21:12:53 se362 Exp $
 *
 * Revisions:
 *    $Log: Rules.java,v $
 *    Revision 1.1  2002/10/22 21:12:53  se362
 *    Initial creation of case study
 *
 */

import java.util.*;
import java.awt.*;

/**
 * This class is used to check the validity
 * of the moves made by the players.  It also 
 * checks to see if the conditions for the end
 * of the game have been met.
 *
 * @author
 * @author
 *
 */
public class Rules {

	private Board theBoard; // The board
	private Driver theDriver; // The driver controlling the program.
	private Move currentMove; // The current move in the game.
	private Point middle;// = 0;  // The space of a piece that gets jumped
	private final int KING = 1; // Constant to represent a king piece.
	private Vector leftWallPieces = new Vector(); // Positions of the left
	// wall spaces.
	private Vector rightWallPieces = new Vector(); // Positions of the right
	// wall spaces.

	/**
	 * The constructor for the Rules class.
	 *
	 * @param board - the checker board.
	 * @param driver - the main driver of the program.
	 */
	public Rules( Board board, Driver driver ) {

		theBoard = board;
		theDriver = driver;
	}

	/**
	 *  This method checks to see if the move that was just made
	 *  was valid and returns a boolean indicating that.
	 *
	 *  @param move  The move that is to be validated.
	 *
	 *  @return Boolean indicating if it was valid.
	 *
	 *  @pre a player has made a move
	 *  @post the player knows if the move has legal
	 */
	public boolean validateMove( Move move ) {

		currentMove = move;
		boolean retval = false;



			boolean anotherMove = false;  // If there is another move that
			// must be made.
			boolean gameOver = false;     // If the game is over.
			Player player = currentMove.getPlayer(); // Current player.
			Player oppositePlayer = theDriver.getOppositePlayer();
			Point start = currentMove.startLocation(); // Start of piece.
			Point end = currentMove.endLocation();  // Attempted end location
			// of the piece.
			int pieceType = theBoard.getPieceAt(start).getType();// Type of
			// the piece.
			// Contains any possible moves if the piece is on the wall.
			Vector wallMoves = new Vector();
			Vector pieces = new Vector();
			Vector tempVec = new Vector();
			Vector startVec = new Vector();
		ArrayList<Point> possibleJumps = new ArrayList<>();
		try {
			possibleJumps = checkForPossibleJumps(start, pieceType,
					player);
			for(Point p : possibleJumps){
				System.out.println(p.toString());
			}
			return possibleJumps.contains(move.endLocation()) && move.getPlayer() == player;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 *  Used after a move has been completed to check to see
	 *  if the conditions have been met for ending the game.
	 *  a boolean is returned indicating whether or not those
	 *  conditions have been met.
	 *
	 *  @return retval true indicating the game is to end.
	 *
	 *  @pre a capture was successful
	 */
	private boolean checkEndCond() {

		boolean retval = false;

		if ( checkForNoPieces() /*|| checkForNoMoves()*/ ) {
			retval = true;
		}

		return retval;

	}

	/**
	 *  Will check all the pieces of the opposite player
	 *  to see if the pieces that are left can not move.
	 *
	 *  @return retval true if there are no moves to be made.
	 *
	 *  @pre A capture was successful.
	 */
	private boolean checkForNoMoves() {

		boolean retval = false;
		// A vector of any possible jumps that can be made.
		ArrayList<Point> possibleJumps = new ArrayList<>();

		Player player = currentMove.getPlayer(); // Current player.


		// Check all ppieces on the board until a move is found.
		for(int x = 0; x < 8; x++)
		{
			for (int y = 0; y < 8; y++)
			{
				boolean temp = theBoard.occupied(x, y);
				if (temp)
				{
					boolean temp2 = theBoard.getPieceAt(x, y).getColor() !=
							player.getColor();

					// Find pieces of the opposite color.
					if (temp &&
							temp2)
					{
						// If there are moves or jumps possible they will be in
						// their respective vector.
						int type = theBoard.getPieceAt(x, y).getType();
						if (theBoard.getPieceAt(x, y).getColor() ==
								Color.white)
						{
							player = currentMove.getPlayer();
						} else
						{
							player = theDriver.getOppositePlayer();
						}
						possibleJumps = checkForPossibleJumps(new Point(x, y), type, player);

						// If either vector contains a move, drop out of the loop.
						if (!possibleJumps.isEmpty())
						{
							x = 9;
							y = 9;
							break;
						}
					}
				}

			}
		}
		if ( possibleJumps.isEmpty() ) {
			retval = true;
		}
		return retval;

	}

	/**
	 *  Will check if there are any more pieces left for the player
	 *  whose turn it is not.
	 *
	 *  @return true if the other player has no more pieces.
	 *
	 *  @pre A capture was successful.
	 */
	private boolean checkForNoPieces() {

		boolean retval = false;
		Player oppositePlayer = theDriver.getOppositePlayer();

		// If the board does not have any pieces of the opposite player,
		// the current player has captured all pieces and won.
		if ( !theBoard.hasPieceOf( oppositePlayer.getColor() ) ) {
			retval = true;
		}

		return retval;

	}

	/**
	 *  Will check the board for any jumps that are open to the current
	 *  player. If there are any possible jumps the valid end positions
	 *  will be added to the vector.
	 *
	 *  @param piecePosition - start position of piece.
	 *  @param pieceType     - type of piece.
	 *
	 *  @return possibleJumps which contains end positions of possible jumps.
	 */
	private ArrayList<Point> checkForPossibleJumps( Point piecePosition, int pieceType,
										  Player aPlayer ) {
		//blue pieces move 0->8
		//white pieces move 8->0
		System.out.println(aPlayer.getColor());
		ArrayList<Point> endPoints = new ArrayList<>();
		if(theBoard.colorAt(piecePosition) == aPlayer.getColor())
		{
			return null;
		}

		if((pieceType == KING && aPlayer.getColor() == Color.white)|| aPlayer.getColor() == Color.BLUE){
			if(!theBoard.occupied(piecePosition.x - 1, piecePosition.y + 1)){
				endPoints.add(new Point(piecePosition.x - 1, piecePosition.y + 1));
			}
			else if (theBoard.colorAt(piecePosition.x - 1, piecePosition.y + 1) == aPlayer.getColor()
					&& !theBoard.occupied(piecePosition.x - 2, piecePosition.y + 2)){
				endPoints.add(new Point(piecePosition.x - 2, piecePosition.y + 2));
			}
			if(!theBoard.occupied(piecePosition.x + 1, piecePosition.y + 1)){
				endPoints.add(new Point(piecePosition.x + 1, piecePosition.y + 1));
			}
			else if (theBoard.colorAt(piecePosition.x + 1, piecePosition.y + 1) == aPlayer.getColor()
					&& !theBoard.occupied(piecePosition.x + 2, piecePosition.y + 2)){
				endPoints.add(new Point(piecePosition.x + 2, piecePosition.y + 2));
			}
		}
		if((pieceType == KING && aPlayer.getColor() == Color.blue) || aPlayer.getColor() == Color.WHITE){
			if(!theBoard.occupied(piecePosition.x - 1, piecePosition.y - 1)){
				endPoints.add(new Point(piecePosition.x - 1, piecePosition.y -1));
			}
			else if (theBoard.colorAt(piecePosition.x - 1, piecePosition.y - 1) == aPlayer.getColor()
					&& !theBoard.occupied(piecePosition.x - 2, piecePosition.y - 2)){
				endPoints.add(new Point(piecePosition.x - 2, piecePosition.y - 2));
			}
			if(!theBoard.occupied(piecePosition.x + 1, piecePosition.y - 1)){
				endPoints.add(new Point(piecePosition.x + 1, piecePosition.y - 1));
			}
			else if (theBoard.colorAt(piecePosition.x + 1, piecePosition.y - 1) == aPlayer.getColor()
					&& !theBoard.occupied(piecePosition.x + 2, piecePosition.y - 2)){
				endPoints.add(new Point(piecePosition.x + 2, piecePosition.y - 2));
			}
		}
		return endPoints;
	}


	/*
     * Validate a regular move.
     *
     * @param piecePosition - the starting piece position.
     * @param end - the end piece position.
     *
     * @return true if the move is valid.
     */
	private boolean validateMove( Point piecePosition, Point end,
								  Player aPlayer ) {

		boolean retval = false;
		Player player = aPlayer;
		if(end.x >= 8 || end.y >= 8 || end.x < 0 || end.y < 0){
			return false;
		}

		// If piece is a king
		if ( theBoard.getPieceAt( piecePosition ).getType() == theBoard.SINGLE ) {
			//Check if piece is on wall. If it is it's movement is restricted.
			// Check if end position is valid.
			if((player.getColor() == Color.white && end.y >= piecePosition.y) || (player.getColor() == Color.blue && end.y <= piecePosition.y)){
				return false;
			}
		} // end if (piece is king).
		if ( (end.y == piecePosition.y + 1 || end.y == piecePosition.y -1 ) && (end.x == piecePosition.x - 1 || end.x == piecePosition.x + 1 )) {
			return true;
		}

		return false;

	}


	/*
     * Will check if a single piece needs to be kinged.
     *
     * @param end - the piece position.
     * @param pieceType - the type of piece it is.
     *
     * @return true if the piece needs to be kinged.
     */
	private boolean checkForMakeKing( Point end, int pieceType ) {

		boolean retval = false;

		try {
			if ( pieceType == Board.SINGLE ) {
				if ( currentMove.getPlayer().getColor() == Color.white ) {
					if ( end.y == 0 ) {
						theBoard.kingPiece( end );
						retval = true;
					}
				} else {
					if ( end.y == 7) {
						theBoard.kingPiece( end );
						retval = true;
					}
				}

			} // if single

		} catch ( Exception e ) {}

		return retval;

	}

}//Rules.java
