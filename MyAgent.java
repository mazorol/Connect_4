import java.util.Random;

public class MyAgent extends Agent {

	Random r;
	int mColumnToMove;
	private boolean mAmIRed;
	private static final int TO_WIN_OR_BLOCK = 3;
	private static final int TO_MOVE_NEXT = 2;
	private static final int TO_MOVE_LAST = 1;

	/**
	 * Constructs a new agent, giving it the game and telling it whether it is Red or Yellow.
	 *
	 * @param game   The game the agent will be playing.
	 * @param iAmRed True if the agent is Red, False if the agent is Yellow.
	 */
	public MyAgent(Connect4Game game, boolean iAmRed) {
		super(game, iAmRed);
		r = new Random();
		mAmIRed = iAmRed;
	}

	/**
	 * The move method is run every time it is this agent's turn in the game. You may assume that
	 * when move() is called, the game has at least one open slot for a token, and the game has not
	 * already been won.
	 *
	 * By the end of the move method, the agent should have placed one token into the game at some
	 * point.
	 *
	 * After the move() method is called, the game engine will check to make sure the move was
	 * valid. A move might be invalid if:
	 * - No token was place into the game.
	 * - More than one token was placed into the game.
	 * - A previous token was removed from the game.
	 * - The color of a previous token was changed.
	 * - There are empty spaces below where the token was placed.
	 *
	 * If an invalid move is made, the game engine will announce it and the game will be ended.
	 *
	 */
	public void move() {
		int middleColumn = (int) myGame.getColumnCount() / 2;
		int middleTwoBottomSlots = myGame.getRowCount() - 2;
		if (!myGame.getColumn(middleColumn).getSlot(middleTwoBottomSlots).getIsFilled()) {
			initialMove();

		} else {
			int columnNumber = getBestColumnToMove();
			if (columnNumber != -1) {
				moveOnColumn(columnNumber);
			} else {
				moveOnRandom();
			}
		}
	}

	/**
	 * Moves on middle column of board
	 */
	private void initialMove() {
		int middleColumn = (int) myGame.getColumnCount() / 2;
		moveOnColumn(middleColumn);
	}

	/**
	 * Determines the best column to move on. For the lowest empty index of the current column it
	 * will first look for a win move, then will look if the opponent has a win move to block,
	 * next look for the best strategy move which is the column with the most clusters of the
	 * agent's color for offense and then opponent's color for defense (first clusters of 2, then 1),
	 * next it will look for 2 tokens in a row of agent's color to create 3-in-a-row, then lastly
	 * look for 1 token of agent's color to make 2-in-a-row
	 *
	 * @return The index number of the best column to move on. If no conditions are met, returns -1
	 */
	private int getBestColumnToMove() {
		int slotNumber;

		// Win move
		for (int columnNumber = 0; columnNumber < myGame.getColumnCount(); columnNumber++) {
			slotNumber = getLowestEmptyIndex(myGame.getColumn(columnNumber));
			if (surroundingTokensCheck(mAmIRed, columnNumber, slotNumber, TO_WIN_OR_BLOCK)) {
				return mColumnToMove;
			}
		}

		// Block move
		for (int columnNumber = 0; columnNumber < myGame.getColumnCount(); columnNumber++) {
			slotNumber = getLowestEmptyIndex(myGame.getColumn(columnNumber));
			if (surroundingTokensCheck(!mAmIRed, columnNumber, slotNumber, TO_WIN_OR_BLOCK)) {
				return mColumnToMove;
			}
		}

		// Strategy move offense (look for column with most connected tokens of 2 of agent color)
		if (getStrategyMove(mAmIRed, TO_MOVE_NEXT) != -1) {
			return mColumnToMove;
		}

		// Strategy move defense (look for column with most connected tokens of 2 of opponent color)
		if (getStrategyMove(!mAmIRed, TO_MOVE_NEXT) != -1) {
			return mColumnToMove;
		}

		// Strategy move offense (look for column with most connected tokens of 1 of agent color)
		if (getStrategyMove(mAmIRed, TO_MOVE_LAST) != -1) {
			return mColumnToMove;
		}

		// Strategy move defense (look for column with most connected tokens of 1 of opponent color)
		if (getStrategyMove(!mAmIRed, TO_MOVE_LAST) != -1) {
			return mColumnToMove;
		}

		// Next move (Make 3 in a row)
		for (int columnNumber = 0; columnNumber < myGame.getColumnCount(); columnNumber++) {
			slotNumber = getLowestEmptyIndex(myGame.getColumn(columnNumber));
			if (surroundingTokensCheck(mAmIRed, columnNumber, slotNumber, TO_MOVE_NEXT)) {
				return mColumnToMove;
			}
		}

		// Last move (make 2 in a row)
		for (int columnNumber = 0; columnNumber < myGame.getColumnCount(); columnNumber++) {
			slotNumber = getLowestEmptyIndex(myGame.getColumn(columnNumber));
			if (surroundingTokensCheck(mAmIRed, columnNumber, slotNumber, TO_MOVE_LAST)) {
				return mColumnToMove;
			}
		}
		return -1;
	}

	/**
	 * Checks surrounding vertical, horizontal, and diagonal directions from the
	 * lowest empty slot in current column to determine if there is a win, block,
	 * next, or last move using the counterNumber to compare the number of
	 * surrounding tokens.
	 *
	 * @param amIRed Agent Color
	 * @param columnNumber Current column number
	 * @param slotNumber Current slot number
	 * @param counterNumber Number of tokens to count to
	 * @return True if win move is found
	 */
	private boolean surroundingTokensCheck(boolean amIRed, int columnNumber, int slotNumber, int counterNumber) {
		// Check to make sure not checking out of board bounds
		if (!isSlotOutOfBounds(columnNumber, slotNumber)) {

			if (checkVertical(amIRed, columnNumber, slotNumber, counterNumber)) {
				mColumnToMove = columnNumber;
				return true;
			} else if (checkHorizontal(amIRed, columnNumber, slotNumber, counterNumber)) {
				mColumnToMove = columnNumber;
				return true;
			} else if (checkDiagonalForwardSlash(amIRed, columnNumber, slotNumber, counterNumber)) {
				mColumnToMove = columnNumber;
				return true;
			} else if (checkDiagonalBackSlash(amIRed, columnNumber, slotNumber, counterNumber)) {
				mColumnToMove = columnNumber;
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the best column to move on with the most surrounding clusters of tokens. May be
	 * offensive or defensive depending on the color passed.
	 * Also checks if an opponent win move will result from moving on a column by looking at slot
	 * above the lowest empty index. If a win move for the opponent exists on that slot,
	 * will not move on that column.
	 *
	 * @param amIRed        Agent color
	 * @param counterNumber Number of tokens to count to
	 * @return Column number to move on
	 */
	private int getStrategyMove(boolean amIRed, int counterNumber) {
		int slotNumber;
		int tokensConnectedCount;
		int maxCount = 0;
		for (int columnNumber = 0; columnNumber < myGame.getColumnCount(); columnNumber++) {
			slotNumber = getLowestEmptyIndex(myGame.getColumn(columnNumber));
			tokensConnectedCount = getTokensConnectedCount(amIRed, columnNumber, slotNumber, counterNumber);

			//If opponent has a win move based on agent's move then skip column (slotNumber - 1)
			if (maxCount < tokensConnectedCount && !surroundingTokensCheck(!amIRed, columnNumber, slotNumber - 1, TO_WIN_OR_BLOCK)) {
				maxCount = tokensConnectedCount;
				mColumnToMove = columnNumber;
			} else if (maxCount == 0) {
				mColumnToMove = -1;
			}
		}
		return mColumnToMove;
	}

	/**
	 * Checks vertical, horizontal, and diagonal slots from the lowest empty slot of current
	 * column for clusters of counterNumber of matching color.
	 *
	 * @param amIRed Agent Color
	 * @param columnNumber Current column number
	 * @param slotNumber Current slot number
	 * @param counterNumber Number of tokens to count to
	 * @return Number of clusters of tokens of a color. Returns -1 if no clusters are found.
	 */
	private int getTokensConnectedCount(boolean amIRed, int columnNumber, int slotNumber, int counterNumber) {
		int tokensConnectedCounter = 0;

		//Check to make sure we are checking against available slots in bounds.
		if (!isSlotOutOfBounds(columnNumber, slotNumber)) {

			if (checkVertical(amIRed, columnNumber, slotNumber, counterNumber)) {
				tokensConnectedCounter++;
			}

			if (checkHorizontal(amIRed, columnNumber, slotNumber, counterNumber)) {
				tokensConnectedCounter++;
			}

			if (checkDiagonalForwardSlash(amIRed, columnNumber, slotNumber, counterNumber)) {
				tokensConnectedCounter++;
			}

			if (checkDiagonalBackSlash(amIRed, columnNumber, slotNumber, counterNumber)) {
				tokensConnectedCounter++;
			}

			return tokensConnectedCounter;
		}
		return -1;
	}

	/**
	 * Checks for a vertical move and returns true if a valid vertical move is available.
	 * Counts the number of tokens in the current column below the lowest empty index that
	 * match the agent's color. The number of tokens to count is passed in with counterNumber.
	 * If counterNumber is met, then returns true.
	 *
	 * @param amIRed Agent color
	 * @param columnNumber Current column number
	 * @param slotNumber Current slot number
	 * @param counterNumber Number of tokens to count to
	 * @return true if valid vertical move is available on current column
	 */
	private boolean checkVertical(boolean amIRed, int columnNumber, int slotNumber, int counterNumber) {
		int slotCounter = 0;
		boolean isSameColor;

		// Check slots below
		do {
			slotNumber++;
			if (slotCounter == counterNumber) {
				return true;
			}
			isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
			if (isSameColor) {
				slotCounter++;
			}
		} while (isSameColor);

		return false;
	}

	/**
	 * Checks for a horizontal move and returns true if a valid horizontal move is available.
	 * Counts the number of tokens in the current column first to the left, then to the right
	 * that match the agent's color. The number of tokens to count is passed in with counterNumber.
	 * If counterNumber is met, then returns true.
	 *
	 * @param amIRed Agent color
	 * @param columnNumber Current column number
	 * @param slotNumber Current slot number
	 * @param counterNumber Number of tokens to count to
	 * @return true if valid horizontal move is available on current column
	 */
	private boolean checkHorizontal(boolean amIRed, int columnNumber, int slotNumber, int counterNumber) {
		int slotCounter = 0;
		int originalColumnNumber = columnNumber;
		boolean isSameColor;

		// Check columns to the left first
		do {
			columnNumber--;
			if (slotCounter == counterNumber) {
				return true;
			}
			isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
			if (isSameColor) {
				slotCounter++;
			}
		} while (isSameColor);

		// Start checking columns to right
		columnNumber = originalColumnNumber;
		columnNumber++;
		isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
		while (isSameColor) {
			slotCounter++;
			columnNumber++;
			if (slotCounter == counterNumber) {
				return true;
			}
			isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
		}
		return false;
	}

	/**
	 * Checks for a diagonal forward slash move and returns true if a valid diagonal forward slash
	 * move is available. Counts the number of tokens in the current column first to the lower-left,
	 * then to the upper-right that match the agent's color. The number of tokens to count is passed
	 * in with counterNumber. If counterNumber is met, then returns true.
	 *
	 * @param amIRed Agent color
	 * @param columnNumber Current column number
	 * @param slotNumber Current slot number
	 * @param counterNumber Number of tokens to count to
	 * @return true if diagonal forward slash(/) move is available on current column
	 */
	private boolean checkDiagonalForwardSlash(boolean amIRed, int columnNumber, int slotNumber, int counterNumber) {
		int slotCounter = 0;
		int originalColumnNumber = columnNumber;
		int originalSlotNumber = slotNumber;

		boolean isSameColor;
		// Check lower-left first
		do {
			columnNumber--;
			slotNumber++;
			if (slotCounter == counterNumber) {
				return true;
			}
			isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
			if (isSameColor) {
				slotCounter++;
			}
		} while (isSameColor);

		// Start checking upper-right
		columnNumber = originalColumnNumber;
		slotNumber = originalSlotNumber;
		columnNumber++;
		slotNumber--;
		isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
		while (isSameColor) {
			slotCounter++;
			columnNumber++;
			slotNumber--;
			if (slotCounter == counterNumber) {
				return true;
			}
			isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
		}
		return false;
	}

	/**
	 * Checks for a diagonal back slash move and returns true if a valid diagonal back slash
	 * move is available. Counts the number of tokens in the current column first to the lower-right,
	 * then to the upper-left that match the agent's color. The number of tokens to count is passed
	 * in with counterNumber. If counterNumber is met, then returns true.
	 *
	 * @param amIRed Agent color
	 * @param columnNumber Current column number
	 * @param slotNumber Current slot number
	 * @param counterNumber Number of tokens to count to
	 * @return true if diagonal back slash(\) move is available on current column
	 */
	private boolean checkDiagonalBackSlash(boolean amIRed, int columnNumber, int slotNumber, int counterNumber) {
		int slotCounter = 0;
		int originalColumnNumber = columnNumber;
		int originalSlotNumber = slotNumber;
		// Check lower-right first
		boolean isSameColor;

		do {
			columnNumber++;
			slotNumber++;
			if (slotCounter == counterNumber) {
				return true;
			}
			isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
			if (isSameColor) {
				slotCounter++;
			}
		} while (isSameColor);

		// Start checking upper-left
		columnNumber = originalColumnNumber;
		slotNumber = originalSlotNumber;
		columnNumber--;
		slotNumber--;
		isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
		while (isSameColor) {
			slotCounter++;
			columnNumber--;
			slotNumber--;
			if (slotCounter == counterNumber) {
				return true;
			}
			isSameColor = isMyColor(columnNumber, slotNumber, amIRed);
		}
		return false;
	}

	/**
	 * Returns a random valid move. If your agent doesn't know what to do, making a random move
	 * can allow the game to go on anyway.
	 *
	 * @return a random valid move.
	 */
	public void moveOnRandom() {
		int i = r.nextInt(myGame.getColumnCount());
		while (getLowestEmptyIndex(myGame.getColumn(i)) == -1) {
			i = r.nextInt(myGame.getColumnCount());
		}
		moveOnColumn(i);
	}

	/**
	 * Drops a token into a particular column so that it will fall to the bottom of the column.
	 * If the column is already full, nothing will change.
	 *
	 * @param columnNumber The column into which to drop the token.
	 */
	public void moveOnColumn(int columnNumber)
	{
		int lowestEmptySlotIndex = getLowestEmptyIndex(myGame.getColumn(columnNumber));   // Find the top empty slot in the column
		// If the column is full, lowestEmptySlot will be -1
		if (lowestEmptySlotIndex > -1)  // if the column is not full
		{
			Connect4Slot lowestEmptySlot = myGame.getColumn(columnNumber).getSlot(lowestEmptySlotIndex);  // get the slot in this column at this index
			if (iAmRed) // If the current agent is the Red player...
			{
				lowestEmptySlot.addRed(); // Place a red token into the empty slot
			} else // If the current agent is the Yellow player (not the Red player)...
			{
				lowestEmptySlot.addYellow(); // Place a yellow token into the empty slot
			}
		}
	}

	/**
	 * Returns the index of the top empty slot in a particular column.
	 *
	 * @param column The column to check.
	 * @return the index of the top empty slot in a particular column; -1 if the column is already full.
	 */
	public int getLowestEmptyIndex(Connect4Column column)
	{
		int lowestEmptySlot = -1;
		for (int i = 0; i < column.getRowCount(); i++) {
			if (!column.getSlot(i).getIsFilled()) {
				lowestEmptySlot = i;
			}
		}
		return lowestEmptySlot;
	}

	/**
	 * Checks if slot matches agent's color
	 *
	 * @param column The column to check
	 * @param slot   The slot to check
	 * @param iAmRed Agent color
	 * @return True if the token color matches the agent color
	 */
	private boolean isMyColor(int column, int slot, boolean iAmRed) {
		// Check if slot is out of bounds of the game board
		if (!isSlotOutOfBounds(column, slot)) {
			if (iAmRed) {
				return isRed(column, slot);
			} else {
				return isYellow(column, slot);
			}
		} else {
			return false;
		}
	}

	/**
	 * Checks if the slot is out of bounds of the game board
	 *
	 * @param columnNumber The column to check
	 * @param slotNumber   The slot to check
	 * @return True if the slot is out of game board bounds
	 */
	private boolean isSlotOutOfBounds(int columnNumber, int slotNumber) {
		if (slotNumber < 0 || slotNumber >= myGame.getRowCount() || columnNumber < 0 || columnNumber >= myGame.getColumnCount()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if slot is filled with a token
	 *
	 * @param column The column to check
	 * @param slot   The slot to check
	 * @return True if the slot is filled
	 */
	private boolean isFilled(int column, int slot) {
		return myGame.getColumn(column).getSlot(slot).getIsFilled();
	}

	/**
	 * Checks if token in slot is red
	 *
	 * @param column The column to check
	 * @param slot   The slot to check
	 * @return True if the slot is red
	 */
	private boolean isRed(int column, int slot) {
		return myGame.getColumn(column).getSlot(slot).getIsRed();
	}

	/**
	 * Checks if token in slot is yellow
	 *
	 * @param column The column to check
	 * @param slot   The slot to check
	 * @return True if the slot is yellow
	 */
	private boolean isYellow(int column, int slot) {
		return !isRed(column, slot) && isFilled(column, slot);
	}
	
	/**
	 * Returns the name of this agent.
	 *
	 * @return the agent's name
	 */
	public String getName() {
		return "MyAgent";
	}

}



