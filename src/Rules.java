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
	//private int adjacentSpots[] = { -9, -7, 7, 9 }; // An array of adjacent
	// spots to check.
	//private int secondSpots[] = { -18, -14, 14, 18 }; // An array of spots
	// adj. to adjacentSpots.
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

		try {

			boolean anotherMove = false;  // If there is another move that
			// must be made.
			boolean gameOver = false;     // If the game is over.
			Player player = currentMove.getPlayer(); // Current player.
			Player oppositePlayer = theDriver.getOppositePlayer();
			Point start = currentMove.startLocation(); // Start of piece.
			Point end = currentMove.endLocation();  // Attempted end location
			// of the piece.
			int pieceType = theBoard.getPieceAt( start ).getType();// Type of
			// the piece.
			// Contains any possible moves if the piece is on the wall.
			Vector wallMoves = new Vector();
			Vector pieces = new Vector();
			Vector tempVec = new Vector();
			Vector startVec = new Vector();
			//Vector possibleJumps = new Vector();
			Vector possibleJumps = checkForPossibleJumps( start, pieceType,
					player );
			// Check all pieces for jumps.
			//if ( player.getColor() == Color.white ) {
			//pieces = theBoard.whitePieces();
			//} else {
			//pieces = theBoard.bluePieces();
			//}

			// For each piece of the current color, see if there are forced jumps.
			for(int x = 0; x < theBoard.sizeOf().x; x++){
				for (int y = 0; y < theBoard.sizeOf().y; y++)
				{
					if (theBoard.occupied(x,y))
					{
						if (theBoard.getPieceAt(x, y).getColor() == player.getColor())
						{
							tempVec = checkForPossibleJumps(new Point(x,y), pieceType, player);
							if (!tempVec.isEmpty())
							{
								startVec.addElement(new Point(x,y));
								possibleJumps.addAll(tempVec);
							}
						}
					}
				}
			}


			// Only proceed if player is trying to move one of his own pieces

			if ( !theBoard.colorAt( start ).equals(
					theDriver.getOppositePlayer().getColor() ) ) {
				// If there is a possible jump it must be made so the end
				// position must match one of the possible jumps.
				if ( startVec.contains(  start  ) ) {
					possibleJumps = checkForPossibleJumps( start, pieceType,
							player );
					if ( possibleJumps.contains( end ) ) {
						// Move the piece
						theBoard.movePiece( start, end );
						// Remove the jumped piece
						theBoard.removePiece( middle );
						middle = null;
						// And if there is a possible multiple jump.
						anotherMove = checkForPossibleJumps( end, pieceType,
								player ).size() > 0;
						// If there is another jump to make, next turn will be
						// current player's and he must move last piece moved.
						if ( anotherMove ) {
							theDriver.endTurn( player, end );
						}
						// Otherwise, next turn should be the opposite player's.
						else {
							theDriver.endTurn( oppositePlayer, null );
						}
						retval = true;
					} // There is no required jump.
				}
				// Otherwise the player is free to make any move that is legal.
				else if ( possibleJumps.isEmpty() ) {

					// If the piece starts on a wall and it's end position is
					// valid then the move is legal.
		    /*TODO remove
		    if ( leftWallPieces.contains( start ) ||
			 rightWallPieces.contains( new Integer( start ) ) ) {
			wallMoves.addAll( wallPieceMoves( start, false,
							  pieceType, player ) );
			if ( wallMoves.contains( new Integer( end ) ) ) {
			    retval = true;
			}
		    }*/

					// If the end position is not occupied then validate move.
					if ( !theBoard.occupied( end ) ) {
						retval = validateMove( start, end, player );
					}

					// If move was OK check end conditions. If game was not won,
					// end turn.
					if ( retval ) {

						// If a move was made, see if the piece should be kinged
						checkForMakeKing( end, pieceType );
						gameOver = checkEndCond();
						if ( gameOver ) {
							theDriver.endGame( player.getName() +
									" won the game." );
						} else {
							theBoard.movePiece( start, end );
							theDriver.endTurn( oppositePlayer, null );
						}

					}
				} // Move is either valid or not.
			} // end if piece on start space is the correct color

			// If the move was not valid, tell the player.
			if ( !retval ) {
				theDriver.endTurn( player, null );
			}
			checkForMakeKing(end, pieceType);
		} catch ( Exception e ) { }

		return retval;

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
		Vector possibleJumps = new Vector();

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
	private Vector checkForPossibleJumps( Point piecePosition, int pieceType,
										  Player aPlayer ) {
		//blue pieces move 0->8
		//white pieces move 8->0
		Vector<Point> endPoints = new Vector<>();
		if(pieceType == KING || aPlayer.getColor() == Color.WHITE){
			if(!theBoard.occupied(piecePosition.x - 1, piecePosition.y - 1)){
				endPoints.add(new Point(piecePosition.x - 1, piecePosition.y - 1));
			}
			else if (theBoard.colorAt(piecePosition.x - 1, piecePosition.y - 1) != aPlayer.getColor()
					&& !theBoard.occupied(piecePosition.x - 2, piecePosition.y - 2)){
				endPoints.add(new Point(piecePosition.x - 2, piecePosition.y - 2));
			}
			if(!theBoard.occupied(piecePosition.x + 1, piecePosition.y - 1)){
				endPoints.add(new Point(piecePosition.x + 1, piecePosition.y - 1));
			}
			else if (theBoard.colorAt(piecePosition.x + 1, piecePosition.y - 1) != aPlayer.getColor()
					&& !theBoard.occupied(piecePosition.x + 2, piecePosition.y - 2)){
				endPoints.add(new Point(piecePosition.x + 2, piecePosition.y - 2));
			}
		}
		if(pieceType == KING || aPlayer.getColor() == Color.BLACK){
			if(!theBoard.occupied(piecePosition.x - 1, piecePosition.y + 1)){
				endPoints.add(new Point(piecePosition.x - 1, piecePosition.y + 1));
			}
			else if (theBoard.colorAt(piecePosition.x - 1, piecePosition.y + 1) != aPlayer.getColor()
					&& !theBoard.occupied(piecePosition.x - 2, piecePosition.y + 2)){
				endPoints.add(new Point(piecePosition.x - 2, piecePosition.y + 2));
			}
			if(!theBoard.occupied(piecePosition.x + 1, piecePosition.y + 1)){
				endPoints.add(new Point(piecePosition.x + 1, piecePosition.y + 1));
			}
			else if (theBoard.colorAt(piecePosition.x + 1, piecePosition.y + 1) != aPlayer.getColor()
					&& !theBoard.occupied(piecePosition.x + 2, piecePosition.y + 2)){
				endPoints.add(new Point(piecePosition.x + 2, piecePosition.y + 2));
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
     *  Check for regular (non-jumping) moves and add them to a Vector.
     *
     *  @param piecePosition - the beginning position of the piece.
     *  @param pieceType     - type of piece.
     *
     *  @return Vector of possible end positions for the piece.
     */

    /*private Vector checkForPossibleMoves( int piecePosition, int pieceType,
					  Player aPlayer ) {


	Vector possibleMoves = new Vector();
	boolean adjacentSpace = false;
	Player player = aPlayer;

	// Get available moves if the piece is on a wall.
	possibleMoves.addAll( wallPieceMoves( piecePosition, false,
					      pieceType, player ) );

	// If the piece is a king.
  	if ( pieceType == theBoard.KING ) {
  	    // Check to see if piece is adjacent to piece of opposite color.
  	    // If there are, add possible end locations to vector.
  	    for ( int i = 0; i <= adjacentSpots.length; i++ ) {
  		adjacentSpace = theBoard.occupied( piecePosition
  						   + adjacentSpots[i] );
  		if ( !adjacentSpace ) {
  		    possibleMoves.addElement( new Integer( piecePosition +
  							   secondSpots[i] ) );
  		}
  	    } // end for

  	} // end if (the piece is not a king).

  	// If the player's color is white it can only move up.
  	else if ( theBoard.getPieceAt( piecePosition ).getColor()
  		  == Color.white ) {
  	    for ( int j = 0; j <= 1; j++ ) {
  		adjacentSpace = theBoard.occupied( piecePosition
  						   + adjacentSpots[j] );
  		// If the adjacent spot is empty.
  		if ( !adjacentSpace ) {
  		    possibleMoves.addElement( new Integer( piecePosition
  							   + secondSpots[j] ) );
  		}
  		adjacentSpace = false;
  	    } // end for
  	}
  	// else the color is blue and can only move down.
  	else {
  	    for ( int k = 2; k <= adjacentSpots.length; k++ ) {
 		// Check to see if there are empty spots adjacent to piece.
  		adjacentSpace = theBoard.occupied( piecePosition
  						   + adjacentSpots[k] );
  		// If there is an empty adjacent spot, add it to the
  		// vector of possible moves.
  		if ( !adjacentSpace ) {
  		    possibleMoves.addElement( new Integer( piecePosition
  							   + secondSpots[k] ) );
  		}
  	    } // end for
  	} // end else

	return possibleMoves;

    }


    /*
     *  This method will check the available moves for pieces if the piece
     *  is on the left or right walls.
     *
     *  @param piecePosition - the starting position of the piece.
     *  @param jump          - true if the piece is making a jump.
     *  @param pieceType     - type of piece.
     *
     *  @return moves, a vector of end positions for the piece.
     */
    /*private Vector wallPieceMoves( int piecePosition, boolean jump,
				   int pieceType , Player aPlayer ) {

	Vector moves = new Vector();

        try{

	    boolean adjacentSpace = false;
	    Piece aPiece;
	    Player player = aPlayer;
	    boolean endSpace = false;

	    if ( pieceType == theBoard.KING ) {
		if ( leftWallPieces.contains( new Integer( piecePosition ) ) ) {
		    if ( jump ) {
			adjacentSpace = theBoard.occupied( piecePosition - 7 );
			aPiece = theBoard.getPieceAt( piecePosition - 7 );
			if ( adjacentSpace && ( aPiece.getColor() !=
						player.getColor() ) ) {
			    // Check the space diagonal to the piece to see
			    // if it is empty.
			    endSpace = theBoard.occupied( piecePosition - 14 );
			    // If the space is empty, there is a multiple
			    // jump that must be made.
			    if ( !endSpace ) {
				moves.addElement( new Integer( piecePosition
							       - 14 ) );
			    }
			}
			adjacentSpace = theBoard.occupied( piecePosition + 9 );
			aPiece = theBoard.getPieceAt( piecePosition + 9 );
			if ( adjacentSpace && ( aPiece.getColor() !=
						player.getColor() ) ) {
			    // Check the space diagonal to the piece to see if
			    // it is empty.
			    endSpace = theBoard.occupied( piecePosition + 18 );
			    // If the space is empty, there is a multiple
			    // jump that must be made.
			    if ( !endSpace ) {
				moves.addElement(new Integer( piecePosition
							      + 18 ) );
			    }
			}
		    }
		    // Otherwise check for a regular move, not a jump.
		    else {
			adjacentSpace = theBoard.occupied( piecePosition - 7 );
			if ( !adjacentSpace ) {
			    moves.addElement( new Integer( piecePosition - 7 ) );
			}
			adjacentSpace = theBoard.occupied( piecePosition + 9 );
			if ( !adjacentSpace ) {
			    moves.addElement( new Integer( piecePosition + 9 ) );
			}
		    }
		}
		// If the piece is on the right wall.
                if ( rightWallPieces.contains( new Integer( piecePosition ) ) ) {
	            if ( jump ) {
			adjacentSpace = theBoard.occupied( piecePosition - 9 );
			aPiece = theBoard.getPieceAt( piecePosition - 9 );
			if ( adjacentSpace && ( aPiece.getColor() !=
						player.getColor() ) ) {
			    // Check the space diagonal to the piece to
			    // see if it is empty.
			    endSpace = theBoard.occupied( piecePosition - 18 );
			    // If the space is empty, there is a multiple
			    // jump that must be made.
			    if ( !endSpace ) {
				moves.addElement( new Integer( piecePosition
							       - 18 ) );
			    }
			}
			adjacentSpace = theBoard.occupied( piecePosition + 7 );
			aPiece = theBoard.getPieceAt( piecePosition + 7 );
			if ( adjacentSpace && ( aPiece.getColor() !=
						player.getColor() ) ) {
			    // Check the space diagonal to the piece to see
			    // if it is empty.
			    endSpace = theBoard.occupied( piecePosition + 14 );
			    // If the space is empty, there is a multiple
			    // jump that must be made.
			    if ( !endSpace ) {
				moves.addElement( new Integer( piecePosition
							       + 14 ) );
			    }
			}
                    }
		    else {
                        // Otherwise check for a regular move, not a jump.
			adjacentSpace = theBoard.occupied( piecePosition - 9 );
			if ( !adjacentSpace ) {
			    moves.addElement( new Integer( piecePosition - 9 ) );
			}
			adjacentSpace = theBoard.occupied( piecePosition + 7 );
			if ( !adjacentSpace ) {
			    moves.addElement( new Integer( piecePosition + 7 ) );
			}
                    }

	        } // end if the piece is on the right wall.
	    }

	    // The piece is not a king.  If its color is white...
	    else if ( player.getColor() == Color.white ) {
		if ( leftWallPieces.contains( new Integer( piecePosition ) ) ) {
		    if ( jump ) {
			adjacentSpace = theBoard.occupied( piecePosition - 7 );
			aPiece = theBoard.getPieceAt( piecePosition - 7 );
			if ( adjacentSpace && ( aPiece.getColor() !=
						player.getColor() ) ) {
			    // Check space diagonal to piece to see if
			    // it is empty.
			    endSpace = theBoard.occupied( piecePosition - 14 );
			    // If the space is empty, there is a multiple
			    // jump that must be made.
			    if ( !endSpace ) {
				moves.addElement( new Integer( piecePosition
							       - 14 ) );
			    }
			}
		    }
		    // Otherwise check for a regular move, not a jump.
		    else {
			adjacentSpace = theBoard.occupied( piecePosition - 7 );
			if ( !adjacentSpace ) {
			    moves.addElement( new Integer( piecePosition - 7 ) );
			}
		    }
		}
		if ( rightWallPieces.contains( new Integer( piecePosition ) ) ) {
		    if ( jump ) {
			adjacentSpace = theBoard.occupied( piecePosition - 9 );
			aPiece = theBoard.getPieceAt( piecePosition - 9 );
			if ( adjacentSpace && ( aPiece.getColor() !=
						player.getColor() ) ) {
			    // Check the space diagonal to the piece to see
			    // if it is empty.
			    endSpace = theBoard.occupied( piecePosition - 18 );
			    // If the space is empty, there is a multiple
			    // jump that must be made.
			    if ( !endSpace ) {
				moves.addElement( new Integer( piecePosition
							       - 18 ) );
			    }
			}
                        // Regular move, not a jump.
		    } else {
			adjacentSpace = theBoard.occupied( piecePosition - 9 );
			if ( !adjacentSpace ) {
			    moves.addElement( new Integer( piecePosition - 9 ) );
			}
		    }
		} // end if the piece is on the right wall.
	    } // end if the piece is white.
	    // The piece must be blue.
	    else {
		if ( leftWallPieces.contains( new Integer( piecePosition ) ) ) {
		    if ( jump ) {
			adjacentSpace = theBoard.occupied( piecePosition + 9 );
			aPiece = theBoard.getPieceAt( piecePosition + 9 );
			if ( adjacentSpace && ( aPiece.getColor() !=
						player.getColor() ) ) {
			    // Check the space diagonal to the piece to
			    // see if it is empty.
			    endSpace = theBoard.occupied( piecePosition + 18 );
			    // If the space is empty, there is a
			    // multiple jump that must be made.
			    if ( !endSpace ) {
				moves.addElement( new Integer( piecePosition
							       + 18 ) );
			    }
			}
		    }
		    // Otherwise check for a regular move, not a jump.
		    else {
			adjacentSpace = theBoard.occupied(  piecePosition + 9 );
			if ( !adjacentSpace ) {
			    moves.addElement( new Integer( piecePosition + 9 ) );
			}
		    }
		}
		if ( rightWallPieces.contains( new Integer( piecePosition ) ) ) {
		    if ( jump ) {
			adjacentSpace = theBoard.occupied( piecePosition + 7 );
			aPiece = theBoard.getPieceAt( piecePosition + 7 );
			if ( adjacentSpace && ( aPiece.getColor() !=
						player.getColor() ) ) {
			    // Check the space diagonal to the piece to see
			    // if it is empty.
			    endSpace = theBoard.occupied( piecePosition + 14 );
			    // If the space is empty, there is a multiple
			    // jump that must be made.
			    if ( !endSpace ) {
				moves.addElement( new Integer( piecePosition
							       + 14 ) );
			    }
			}
		    } else {
			adjacentSpace = theBoard.occupied( piecePosition + 7 );
			if ( !adjacentSpace ) {
			    moves.addElement( new Integer( piecePosition + 7 ) );
			}
		    }
		} // end if the piece is on the right wall.
	    }

        } catch( Exception e ) {}

        return moves;

    }*/

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
