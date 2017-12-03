/**
 * TestingKernel.java
 *
 * Created on February 8, 2002, 1:09 PM
 *
 * Version:
 *   $Id: TestingKernel.java,v 1.1 2002/10/22 21:12:53 se362 Exp $
 *
 * Revisions:
 *   $Log: TestingKernel.java,v $
 *   Revision 1.1  2002/10/22 21:12:53  se362
 *   Initial creation of case study
 *
 */

// Not all of these are probably necessary.
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

/**
 *  This class is the system's Testing Kernel.  For now it is hard
 *  coded, sequential tests that will be performed.
 *
 *  It tests the system and how it responds to such things as basic
 *  moves/jumps, illegal values, and illegal moves/jumps.
 *
 *  All results will be outputted about pass/fail on the tests.
 *
 * @author
 * @version
 */
public class TestingKernel extends java.lang.Object{
    // The facade that we will manipulate and interact with.
    public Facade testFacade;

    // The driver that this program needs to call
    public Driver theDriver;

    // The board that we will use to check the state of the pieces with.
    public Board  testBoard;

    // Initial values that will be used in this program.
    public int    testTime  = 250;
    public String playerOne = "Joe";
    public String playerTwo = "Bob";

    /**
     * The main method.
     *
     * Create a new instance of the driver and then create an instance
     * of kernel, pass it that driver, and start the program from there.
     *
     * @param args Array of command line arguments.
     */
    public static void main( String args[] ){
        // Create the driver
        Driver sampleDriver = new Driver();

        // Create the instance of the testing kernel.
        // Pass to it as a parameter the driver you just made.
        // This is turn should begin execution of the program.
        TestingKernel tester = new TestingKernel( sampleDriver );
    }

    /**
     * The constructor for this kernel which calls the other methods.
     *
     * @param aDriver The facade to manipulate in this program.
     */
    public TestingKernel( Driver aDriver ){
        //testFacade = aFacade;
        theDriver  = aDriver;
        testFacade = theDriver.getFacade();

        // Call the needed methods.
        setBegin();
        beginTests();
    }

    /**
     * Set the state of the game to initial settings.
     *
     * i.e., a generously timed local game for which to test.
     */
    public void setBegin(){

        try{
            // Set this game to be a local game.
            testFacade.setGameMode( testFacade.LOCALGAME );

            // Create players
            // createPlayer(int num, int type, String name)
            theDriver.createPlayer( 1, Player.LOCALPLAYER, playerOne );
            theDriver.createPlayer( 2, Player.LOCALPLAYER, playerTwo );

            // Set the names for the players.
            testFacade.setPlayerName( 1, playerOne );
            testFacade.setPlayerName( 2, playerTwo );

            // Give a generous time.  At this point, it will allow
            // adequate time for this program to run, but perform a
            // basic test on the timer.
            testFacade.setTimer( testTime, ( testTime/2 ) );

            //Start the game.
            testFacade.startGame();

        }catch( Exception e ){
            System.err.println( e.getMessage() );
        }
    }

    /**
     * Tests the intial values, then call the other methods.
     *
     * Handles the reporting of the results.
     */
    public void beginTests(){
        // Bool to keep track whether or not it passed the current test.
        boolean passedTest = false;

        // Test that it correctly set the initial values correctly.
        // Player One's Name
        passedTest = playerOne.equals( testFacade.getPlayerName( 1 ) );
        report( passedTest, "Sets Player One name", 3 );

        // Player Two's Name
        passedTest = playerTwo.equals( testFacade.getPlayerName( 2 ) );
        report( passedTest, "Sets Player Two name", 3 );

        // The Timer.
        passedTest = ( testTime == testFacade.getTimer() );
        report( passedTest, "Sets Timer", 4 );

        // These are intended to run in succession.  So pieces are assumed
        // to be positioned based on previous tests.  You must take note
        // of that.

        // Check basic moves.
        System.out.println( "Testing Basic Moves...." );
        passedTest = testBasicMoves();
        report( passedTest, "Performs Basic Moves", 3 );

        // Test bounds
        System.out.println( "Testing the Bounds of the board..." );
        passedTest = testBounds();
        report( passedTest, "Checks Bounds", 4 );

        // Test forced jumps
        System.out.println( "Testing Forced Jumps..." );
        passedTest = testForcedJump();
        report( passedTest, "Forces Jumps", 4 );

        // Test multiple jumps
        System.out.println( "Testing that it allows mult. jumps..." );
        passedTest = testMultJumps();
        report( passedTest, "Allows Multiple Jumps", 3 );

        // Test Invalid moves.
        System.out.println( "Testing invalid moves..." );
        passedTest = testInvalidMoves();
        report( passedTest, "Checks Invalid Moves", 3 );

        // Test offering a draw.
        // System.out.println( "Testing that if offers draw..." );
        // passedTest = testDraw();
        // report( passedTest, "Offers Draw", 3 );

        System.out.println( "\n" + "Testing has completed." );
    }

    /**
     * Test basic moves made by the checkers.  This  does not
     * tests jumps or invalid moves.
     *
     * @return Whether or not it passed.
     */
    public boolean testBasicMoves(){return false;}

    /**
     * This method will attempt to make moves with out of bound values
     * to ensure the system detects these without throwing an
     * OutOfBounds Exception.
     *
     * It will also test that the system detects making a
     * move with the same start and end positions.
     *
     * @return Whether or not it passed the test.
     */
    public boolean testBounds(){return false;}

    /**
     * Tests for the following:  A jump is possible.  The player attempts
     * to move elsewhere.  System must not allow move and keep state
     * unchanged.
     *
     * @return Whether or not it passed the test.
     */
    public boolean testForcedJump(){return false;}

    /**
     * When multiple jumps are present, this tests that they are forced
     * and that the correct player keeps control of the game.
     *
     * @return Whether or not it passed the test.
     */
    public boolean testMultJumps(){return false;}

    /**
     * Testing of a variety of invalid moves.
     *
     * @return Whether or not it passed the test.
     */
    public boolean testInvalidMoves(){return false;}

    /**
     * Tests the actions of the GUI and checks to see that both players
     * may offer draws and accept/decline.
     */
    public boolean testDraw(){
        // Boolean for whether or not it passed the test.
        boolean drawTest = true;

        return drawTest;
    }

    /**
     * This method is for reporting.  It outputs the name of the test
     * and a Pass/Fail value along side of the test name.
     *
     * @param passFail Bool on whether or not test was passed.
     * @param name     Name of test.
     * @param tabs     The number of tabs to insert
     */
    public void report( boolean passFail, String name, int tabs ){
        // String representation of whether or not the test was passed.
        String success;

        // Assign a value.
        if( passFail ){
            success = "passed";
        }else{
            success = "FAILED!!!";
        }

        // Report the results.
        System.out.print( name + ":" );

        for( int i = 0; i < tabs; i++ ){
            System.out.print( "\t" );
        }

        System.out.print( success + "\n" + "\n" );
    }

    /**
     * This method is a generic way to put in place
     * an artificial wait into the program.
     * This allows for the program to have a delay without
     * having to implement anything such as Runnable.
     */
    public void simpleWait(){
        for( int i = 0; i < 2500; i++ ){
            // not the best way for a delay
            // but it allows a wait without
            // having to deal with threads.
        }
    }

} // TestingKernel.java
