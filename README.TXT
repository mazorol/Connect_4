===========================================================================================
|Udacity Intro to Java Programming Final Project - Connect 4 game-playing agent (MyAgent) |
===========================================================================================

PURPOSE OF PROJECT
==================
The purpose of this project is to create a Connect 4 game-playing agent. All logic for the 
agent is in the class "MyAgent". The logic for "MyAgent" loops through each column to see 
if the slot available (lowest empty slot) meets certain criteria for it to be the best move.

The critera for the best move are iterated in the following order:

1. Middle column (First / second moves)
---------------------------------------
Checks if the middle column, bottom 2 slots are filled. If not, will move on middle column
until both of those slots are filled with either the agent's color or the opponent's.

2. Win Move
-----------
The most important move at any point of the game after at least 3 tokens of the agent's 
color have been played is the win move. The logic in MyAgent for the win move iterates 
through the columns of the board and looks at the slots around the lowest empty slot to 
see if it is a win move. As soon as a win move is found, (moving from column index 0 to the 
number of columns in the board) the agent will move on that column and win the game.

3. Block Move
-------------
The second most important move at any point of the game before 3 tokens of the opponent's 
color have been played is to block the opponent's win move. The same logic for the win move 
is applied to the block move but will look for the opponent's color as opposed to the agent's.

4. Stragtegy Move
-----------------
After there are no win or block moves to be found, it will look at the surrounding slots of 
the current column's lowest empty slot for clusters of tokens of the same color. This is done 
by iterating through every column to find the one with the most 2-in-a-row clusters of the 
agent's color, then 2-in-a-row clusters of the opponent's color, then single tokens of the 
agent's color, then of the opponent's color. If no clusters ar found or if moving on the 
current column will create a win move for the opponent, that column will not be considered
as a valid move.

5. Next Move
-----------------
If none of the above criteria are met, it will iterate through the columns and look for 
2-in-a-row of the agent's color. First column found with the lowest empty index next to a 
2-in-a-row (to create 3-in-a-row), it will move on it. 

6. Last Move
----------------------
Next it iterate through the columns and look for a token of the agent's color. First column
that is found with the lowest empty index next to a token of the agent's color 
(to create 2-in-a-row), it will move on it. 


7. Random Move
--------------
If none of the criteria are met, a random move will be made.


DATE
====
05/20/2015

HOW TO START THIS PROJECT
=========================
Run class "Main" 

AUTHOR
======
Matt Mazorol

USER INSTRUCTIONS
=================
Change board size in the "Main" class
--------------------------------------
Connect4Game game = new Connect4Game(7, 6); // create the game; these sizes can be altered for larger or smaller games

Change agents in the "Main" class. There must be a red player and a yellow player.
-----------------------------------------------------------------------------------
Agent redPlayer = new Agent1(game, true); // create the red player, any subclass of Agent
Agent yellowPlayer = new Agent2(game, false); // create the yellow player, any subclass of Agent