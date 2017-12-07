/*
 * CheckerGUI.java
 * 
 * The actual board.
 *
 * Created on January 25, 2002, 2:34 PM
 * 
 * Version
 * $Id: CheckerGUI.java,v 1.1 2002/10/22 21:12:52 se362 Exp $
 * 
 * Revisions
 * $Log: CheckerGUI.java,v $
 * Revision 1.1  2002/10/22 21:12:52  se362
 * Initial creation of case study
 *
 */

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author
 * @version 
 */

public class CheckerGUI extends JFrame implements ActionListener
{

    //the facade for the game

    private static Facade theFacade; //the facade
    private JButton[][] possibleSquares = new JButton[8][8];
    private int timeRemaining;//the time remaining


    private JLabel PlayerOnelabel;
    private JLabel playerTwoLabel;
    private JLabel timeRemainingLabel;
    private JLabel secondsLeftLabel;
    private JButton ResignButton;
    private JButton DrawButton;
    private JLabel warningLabel, whosTurnLabel;

    //the names and time left
    private static String playerOnesName = "", playerTwosName = "", timeLeft = "";

    /**
     * Constructor, creates the GUI and all its components
     *
     * @param facade the facade for the GUI to interact with
     * @param name1  the first players name
     * @param name2  the second players name
     */

    public CheckerGUI(Facade facade, String name1, String name2)
    {

        super("Checkers");

        //long names mess up the way the GUI displays
        //this code shortens the name if it is too long
        String nameOne = "", nameTwo = "";
        if (name1.length() > 7)
        {
            nameOne = name1.substring(0, 7);
        } else
        {
            nameOne = name1;
        }
        if (name2.length() > 7)
        {
            nameTwo = name2.substring(0, 7);
        } else
        {
            nameTwo = name2;
        }

        playerOnesName = nameOne;
        playerTwosName = nameTwo;
        theFacade = facade;
        register();

        initComponents();
        pack();
        update();
        //updateTime();
    }
    
    
    /*
     * This method handles setting up the timer
     */

    private void register()
    {

        try
        {
            theFacade.addActionListener(this);

        } catch (Exception e)
        {

            System.err.println(e.getMessage());

        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form. It initializes the components
     * adds the buttons to the Vecotr of squares and adds
     * an action listener to the components
     */
    private void initComponents()
    {

        this.setResizable(false);

        PlayerOnelabel = new JLabel();
        playerTwoLabel = new JLabel();
        whosTurnLabel = new JLabel();

        warningLabel = new JLabel();
        timeRemainingLabel = new JLabel();
        secondsLeftLabel = new JLabel();

        ResignButton = new JButton();
        ResignButton.addActionListener(this);

        DrawButton = new JButton();
        DrawButton.addActionListener(this);

        //sets the layout and adds listener for closing window
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints1;

        //add window listener
        addWindowListener(new WindowAdapter()
                          {
                              public void windowClosing(WindowEvent evt)
                              {
                                  exitForm(evt);
                              }
                          }
        );

        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                JButton jButton1 = new JButton();
                jButton1.addActionListener(this);
                possibleSquares[y][x] = jButton1;

                jButton1.setPreferredSize(new Dimension(80, 80));
                jButton1.setActionCommand(x + "," + y);
                if (y % 2 == x %2)
                {
                    jButton1.setBackground(Color.blue);
                } else
                {
                    jButton1.setBackground(Color.white);
                }
                gridBagConstraints1 = new java.awt.GridBagConstraints();
                gridBagConstraints1.gridx = x;
                gridBagConstraints1.gridy = y;
                getContentPane().add(jButton1, gridBagConstraints1);
            }
        }


        PlayerOnelabel.setText("Player 1:     " + playerOnesName);
        PlayerOnelabel.setForeground(Color.black);

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 4;
        getContentPane().add(PlayerOnelabel, gridBagConstraints1);

        playerTwoLabel.setText("Player 2:     " + playerTwosName);
        playerTwoLabel.setForeground(Color.black);

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 9;
        gridBagConstraints1.gridwidth = 4;
        getContentPane().add(playerTwoLabel, gridBagConstraints1);

        whosTurnLabel.setText("");
        whosTurnLabel.setForeground(new Color(0, 100, 0));

        gridBagConstraints1.gridx = 8;
        gridBagConstraints1.gridy = 1;
        getContentPane().add(whosTurnLabel, gridBagConstraints1);

        warningLabel.setText("");
        warningLabel.setForeground(Color.red);

        gridBagConstraints1.gridx = 8;
        gridBagConstraints1.gridy = 2;
        getContentPane().add(warningLabel, gridBagConstraints1);

        timeRemainingLabel.setText("Time Remaining:");
        timeRemainingLabel.setForeground(Color.black);

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 8;
        gridBagConstraints1.gridy = 3;
        getContentPane().add(timeRemainingLabel, gridBagConstraints1);

        secondsLeftLabel.setText(timeLeft + " sec.");
        secondsLeftLabel.setForeground(Color.black);

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 8;
        gridBagConstraints1.gridy = 4;
        getContentPane().add(secondsLeftLabel, gridBagConstraints1);

        ResignButton.setActionCommand("resign");
        ResignButton.setText("Resign");

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 8;
        gridBagConstraints1.gridy = 7;
        getContentPane().add(ResignButton, gridBagConstraints1);

        DrawButton.setActionCommand("draw");
        DrawButton.setText("Draw");

        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 8;
        gridBagConstraints1.gridy = 6;
        getContentPane().add(DrawButton, gridBagConstraints1);

    }

    /**
     * Exit the Application
     *
     * @param evt the window event
     */
    private void exitForm(java.awt.event.WindowEvent evt)
    {
        theFacade.pressQuit();

    }

    /**
     * Takes care of input from users, handles any actions performed
     *
     * @param e the event that has occured
     */

    public void actionPerformed(ActionEvent e)
    {

        try
        {
            //if a square gets clicked
            if (Character.isDigit(e.getActionCommand().charAt(0)))
            {
                System.out.println(e.getActionCommand());
                //call selectSpace with the button pressed
                theFacade.selectSpace(new Point(Integer.parseInt(e.getActionCommand().split(",")[0]), Integer.parseInt(e.getActionCommand().split(",")[1])));

                //if draw is pressed
            } else if (e.getActionCommand().equals("draw"))
            {
                //does sequence of events for a draw
                theFacade.pressDraw();

                //if resign is pressed
            } else if (e.getActionCommand().equals("resign"))
            {
                //does sequence of events for a resign
                theFacade.pressQuit();

                //if the source came from the facade
            } else if (e.getSource().equals(theFacade))
            {

                //if its a player switch event
                if ((e.getActionCommand()).equals(theFacade.playerSwitch))
                {
                    //set a new time
                    timeRemaining = theFacade.getTimer();
                    //if it is an update event
                } else if ((e.getActionCommand()).equals(theFacade.update))
                {
                    //update the GUI
                    update();
                } else
                {
                    throw new Exception("unknown message from facade");
                }
            }
            //catch various Exceptions
        } catch (NumberFormatException excep)
        {
            System.err.println(
                    "GUI exception: Error converting a string to a number");
        } catch (NullPointerException exception)
        {
            System.err.println("GUI exception: Null pointerException "
                    + exception.getMessage());
            exception.printStackTrace();
        } catch (Exception except)
        {
            System.err.println("GUI exception: other: "
                    + except.getMessage());
            except.printStackTrace();
        }

    }


    /**
     * Updates the GUI reading the pieces in the board
     * Puts pieces in correct spaces, updates whos turn it is
     *
     */

    private void update()
    {


        if (checkEndConditions())
        {

            theFacade.showEndGame(" ");
        }
        //the board to read information from
        Board board = theFacade.stateOfBoard();

        //a temp button to work with
        JButton temp = new JButton();

        //go through the board
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {



                // if there is a piece there
                if (board.occupied(x, y))
                {

                    //check to see if color is blue
                    if (board.colorAt(x, y) == Color.blue)
                    {

                        //if there is a  single piece there
                        if ((board.getPieceAt(x, y)).getType() == board.SINGLE)
                        {

                            //show a blue single piece in that spot board
                            temp = (JButton) possibleSquares[y][x];

                            //get the picture from the web
                            temp.setIcon(new ImageIcon("src/BlueSingle.gif"));
                            try
                            {

                                possibleSquares[y][x] = temp;

                            } catch (Exception e)
                            {
                                System.out.println(e.getMessage());
                            }

                            //if there is a kinged piece there
                        } else if ((board.getPieceAt(x, y)).getType() == board.KING)
                        {

                            //show a blue king piece in that spot board
                            temp = (JButton) possibleSquares[y][x];

                            //get the picture from the web
                            temp.setIcon(new ImageIcon("src/BlueKing.gif"));
                            try
                            {
                                possibleSquares[y][x] = temp;
                            } catch (Exception e)
                            {
                            }

                        }
                        //check to see if the color is white
                    } else if (board.colorAt(x, y) == Color.white)
                    {

                        //if there is a single piece there
                        if ((board.getPieceAt(x, y)).getType() == board.SINGLE)
                        {

                            //show a blue single piece in that spot board
                            temp = (JButton) possibleSquares[y][x];


                            //get the picture from the web
                            temp.setIcon(new ImageIcon("src/WhiteSingle.gif"));
                            try
                            {


                                possibleSquares[y][x] = temp;

                                //                           temp.setIcon(
                                //                                  new ImageIcon(new URL("file:src/WhiteSingle.bmp")));
                            } catch (Exception e)
                            {
                            }

                            //if there is a kinged piece there
                        } else if ((board.getPieceAt(x, y)).getType() == board.KING)
                        {

                            //show a blue king piece in that spot board
                            temp = (JButton) possibleSquares[y][x];

                            //get the picture from the web
                            temp.setIcon(new ImageIcon("src/WhiteKing.gif"));
                            try
                            {

                                possibleSquares[y][x] = temp;

                            } catch (Exception e)
                            {
                            }

                        }
                        //if there isnt a piece there
                    }
                } else
                {
                    //show no picture
                    temp = (JButton) possibleSquares[y][x];
                    temp.setIcon(null);

                }


            }
        }

        //this code updates whos turn it is
        if (theFacade.whosTurn() == 2)
        {
            playerTwoLabel.setForeground(Color.red);
            PlayerOnelabel.setForeground(Color.black);
            whosTurnLabel.setText(playerTwosName + "'s turn ");
        } else if (theFacade.whosTurn() == 1)
        {
            PlayerOnelabel.setForeground(Color.red);
            playerTwoLabel.setForeground(Color.black);
            whosTurnLabel.setText(playerOnesName + "'s turn");
        }
    }

    /**
     * Update the timer
     */

    public void updateTime()
    {

        if (theFacade.getTimer() > 0)
        {

            // if the time has run out but not in warning time yet
            // display warning and count warning time
            if (timeRemaining <= 0 && (warningLabel.getText()).equals(""))
            {
                timeRemaining = theFacade.getTimerWarning();
                warningLabel.setText("Time is running out!!!");

                // if the time has run out and it was in warning time quit game
            } else if (timeRemaining <= 0 &&
                    !(warningLabel.getText()).equals(""))
            {

                theFacade.pressQuit();

            } else
            {

                timeRemaining--;

            }

            secondsLeftLabel.setText(timeRemaining + " sec.");

        } else
        {
            secondsLeftLabel.setText("*****");
        }
    }

    /**
     * Checks the ending condotions for the game
     * see if there a no pieces left
     *
     * @return the return value for the method
     * true if the game should end
     * false if game needs to continue
     */

    public boolean checkEndConditions()
    {
        //the return value
        boolean retVal = false;
        try
        {
            //the number of each piece left
            int whitesGone = 0, bluesGone = 0;

            //the board to work with
            Board temp = theFacade.stateOfBoard();

            //go through all the spots on the board
            for (int x = 0; x < 8; x++)
            {
                for (int y = 0; y < 8; y++)
                {
                    //if there is a piece there
                    if (temp.occupied(x, y))
                    {
                        //if its a blue piece there
                        if ((temp.getPieceAt(x, y)).getColor() == Color.blue)
                        {
                            // increment number of blues
                            bluesGone++;
                            //if the piece is white
                        } else if ((temp.getPieceAt(x, y)).getColor()
                                == Color.white)
                        {
                            //increment number of whites
                            whitesGone++;
                        }
                    }
                }
            }//end of for loop

            //if either of the number are 0
            if (whitesGone == 0 || bluesGone == 0)
            {
                retVal = true;
            }

        } catch (Exception e)
        {

            System.err.println(e.getMessage());

        }
        return retVal;

    }//checkEndConditions

}//checkerGUI.java
