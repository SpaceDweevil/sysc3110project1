package GameInternal;

import java.util.ArrayList;

public class Game {
	
	//hoo boy
	//hoooooooooooooooooooooooooooooooooooooooooooooooo boy
	//this is gonna take a while isn't it
	private GamePiece[][] board;
	private int boardWidth, boardHeight;
	private ArrayList<String> idList; //Gonna want a list of IDs that have already been made, to avoid duplicate keys.
	
	private GamePiece getObjectAt(int x, int y) {
		return board[x][y];
	}
	
	/**
	 * This is private because why trust anything but Game to make GamePieces?
	 * @param x
	 * @param y
	 * @param piece
	 * @return whether or not it succeeded.
	 */
	private boolean addPiece(int x, int y, GamePiece piece) {
		if(x >= boardWidth || y >= boardHeight) {
			return false;
		}
		if(board[x][y] == null) {
			board[x][y] = piece;
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Not sure how to go about this, but this one is the public-facing addPiece
	 * @param x
	 * @param y
	 * @param piece
	 * @return whether or not it succeeded
	 */
	public boolean addPiece(int x, int y, PieceType piece) {
		switch(piece) {
		case FOX_EW:
			return addPiece(x, y, new Fox(genNewID(piece), x, y, 1, DIRECTION.EAST_WEST));
		case FOX_NS:
			return addPiece(x, y, new Fox(genNewID(piece), x, y, 1, DIRECTION.NORTH_SOUTH));
		case RABBIT:
			return addPiece(x, y, new Rabbit(genNewID(piece), x, y));
		case HILL:
			return addPiece(x, y, new Hill(genNewID(piece), x, y));
		case HOLE:
			return addPiece(x, y, new Hole(genNewID(piece), x, y));
		default:
			return addPiece(x, y, new Mushroom(genNewID(piece), x, y));
			
		}
	}
	/**
	 * Generates a new ID for a given Piece that hasn't been taken.
	 * @param piece the type of piece the ID is being made for.
	 * @return something like "FOX 1" or whatever
	 */
	private String genNewID(PieceType piece) {
		String out = "";
		switch(piece) {
		case FOX_EW: case FOX_NS:
			out += "FOX";
			break;
		case RABBIT:
			out += "RABBIT";
			break;
		case HILL:
			out += "HILL";
			break;
		case HOLE:
			out += "HOLE";
			break;
		case MUSHROOM:
			out += "MUSH";
			break;
		}
		return out;
	}
	/**
         * Checks to see if you can move a piece to a desired location
         * @param piece this is the piece that wants to be moved
         * @param x x coordinate of the final location
         * @param y y coordinate of the final location
         * @return if the piece can move to the given location
         */
        
        public boolean canMove (GamePiece piece, int x, int y){
            
            int currPos; //can only move in one direction at a time,
            int finalPos; // so these values are only either the x or y posistion.
            boolean isVert;
            
            //checks if desired location is out of bounds
            if(x < 0 || y < 0 || x >= boardWidth || y >= boardHeight){
                return false;
            }
            
            // this if is to see if the place they want to move the piece to is inline with where
            //they are either vertically or horizonally and isn't the tile the piece is already on.
            if((piece.getX() != x && piece.getY() != y)||
                    (piece.getX() == x && piece.getY() == y)){
                return false;
            }
            
            // checking if the movement is vert or hori
            if(piece.getX() != x){
                currPos = piece.getX();
                finalPos = x;
                isVert = false;
            } else {
               currPos = piece.getY();
                finalPos = y; 
                isVert = true;
            }
            
            
            // is it a rabbit being moved?
            if(piece instanceof Rabbit){
                // for creating the while loop
                int i;
                int j;
                if(currPos < finalPos){
                    i = currPos;
                    j = finalPos;
                } else {
                    i = finalPos;
                    j = currPos;
                }
                
                // the finalPos and the currPos must have a difference larger then 1
                // remember j is the larger value
                if(j - i < 2){
                    return false;
                }
                
                // goes over each space between the rabbit and the chosen destination
                while(i <= j){
                    // check the space the rabbit is landing on
                    if(finalPos == i){
                        if(getObjectAt(x, y) instanceof ContainerPiece){
                            if(!((ContainerPiece)getObjectAt(x, y)).canEnter()){
                                return false; // if the hole/hill is full
                            }
                            
                        } else if (getObjectAt(x, y) == null){
                            // this means the space is empty
                        } else {
                            return false; // this means the space is occupied
                        }
                    } else if(currPos != i){ // it doesn't need to check its own space
                        if(isVert){
                            if(!(getObjectAt(x, i).canBeJumped()) || getObjectAt(x, i) == null){
                                return false;
                            }
                        } else {
                            if(!(getObjectAt(i, y).canBeJumped()) || getObjectAt(i, y) == null){
                                return false;
                            }
                        }
                    }
                    i ++;
                }
                
                return true;
                
            } else if(piece instanceof Fox) {// is fox being moved?
                
                // need to find out if it is moving head first or tail first
                if(currPos < finalPos){
                    //headfirst = true
                    for (int i = currPos + 1; i <= finalPos; i++){ // for each tile between the fox head and the finalPos, not including the head itself
                        
                        if(isVert){
                            if(!(getObjectAt(x, i) == null)){
                                return false;
                            }
                        } else {
                            if(!(getObjectAt(i, y) == null)){
                                return false;
                            }
                        }
                    }
                    
                    return true;
                } else {
                    //headfirst = false
                    // this means we are moving BACKWARDS
                    //First need to check that the move won't move the tail out of bounds
                    if(finalPos - (((Fox)piece).getLength() - 1) < 0 && !(isVert)){
                        //checking if the final position of the head + (the length of the fox minus the head) is
                        // less then the edge of the board
                        return false;
                    }
                    // for each tile between the fox tail and the finalPos of the tail, not including the tail itself
                    for (int i = (currPos - 1) -(((Fox)piece).getLength() - 1); i >= finalPos - (((Fox)piece).getLength() - 1); i--){ 
                        
                        if(isVert){
                            if(!(getObjectAt(x, i) == null)){
                                return false;
                            }
                        } else {
                            if(!(getObjectAt(i, y) == null)){
                                return false;
                            }
                        }
                    }
                    
                    return true;
                }
                
                
                
            } else { // only enters here if piece is not a movable object (fox/rabbit)
                return false;
            }
        }
        /**
         * This function moves a fox an all of its bits to a desired location
         * if possible
         * @param piece the fox being moved
         * @param x x coordinate of the final location
         * @param y y coordinate of the final location
         * @return if the move was made or not
         */
        public boolean moveFox(Fox piece, int x, int y){
            if(canMove(piece, x, y)){ // can the fox move to where I want to place it
                boolean isVert;
                // is the movement vertical or horizonal
                if(piece.getAxis() == DIRECTION.EAST_WEST){
                    isVert = false;
                } else {
                    isVert = true;
                }
                
                // used the hold the current bit being moved
                FoxBit currBit = piece.getHead();
                
                // go through a move each bit
                for(int i = 0; i < piece.getLength(); i++){
                    if(i == 0){ // moving the head and the fox location
                        piece.setX(x);
                        piece.setY(y);
                        currBit.setX(x);
                        currBit.setY(y);
                        board[x][y] = currBit;
                    } else { // moving the rest of the body
                        if(isVert){
                            currBit.setX(x);
                            currBit.setY(y - i);
                            board[x][y - i] = currBit;
                        } else {
                            currBit.setX(x - i);
                            currBit.setY(y);
                            board[x - i][y] = currBit;
                        }
                    }
                    currBit = currBit.getBehind();
                }
                return true;
            }else{ // the move is not possible
                return false;
            }
        }
            
	/**
	 * basic Game constructor
	 * @param width
	 * @param height
	 */
	public Game(int width, int height) {
		board = new GamePiece[width][height];
		boardWidth = width;
		boardHeight = height;
		idList = new ArrayList<String>();
	}
	
	
}
