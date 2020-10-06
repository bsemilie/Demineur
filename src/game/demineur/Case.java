package game.demineur;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * This class represent a case of a minesweeper(demineur).
 * It is a JPanel which uses a MouseListener
 */
public class Case extends JPanel implements MouseListener {

    int i; //x-coordinate of the case
    int j; //y-coordinate of the case
    private boolean revealed = false; //if case was already clicked on or not
    private boolean counted; //if case was counted or not to know when game ends
    private Demineur demineur; //demineur instance where the case is
    private Counter counter; //Time counter which start when a case is first cliked on
    private boolean enabledClick; //Allow case to be clicked on depending if game is stopped
    private final static int DIM = 25; //Dimension of the case
    private Color color = Color.lightGray; //Color to reveal case
    private int value; //Value behind the case


    /**
     * Constructor
     * @param i x-coordinate of the case
     * @param j y-coordinate of the case
     * @param demineur demineur instance related to this case
     */
    public Case(int i, int j, Counter counter, Demineur demineur){

        this.i = i;
        this.j = j;
        this.demineur = demineur;
        this.counter = counter;
        this.enabledClick = true; //Case can be clicked on

        setPreferredSize(new Dimension(DIM, DIM)); // set case size
        addMouseListener(this);
    }

    /**
     * This function is used to repaint a case depending on the context:
     * - if the case is not revealed it is colored blue
     * - if the case is clicked and it is a mine the case displays an image
     * - if it is not a mine, then displays the number of mines around the case
     * @param gc
     */
    @Override
    public void paintComponent(Graphics gc){
        super.paintComponent(gc);
        if(revealed)
        {
            if(value == -1)
            {

                try {
                    BufferedImage image = ImageIO.read(new File("img/Bomb.png"));
                    gc.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                if(value == 0)
                {
                    drawCenterString(gc, ""); //0 is not display, case is empty
                    setBackground(color);
                    countCases();
                    counted = true;
                }
                else
                {
                    drawCenterString(gc,Integer.toString(value));
                    setBackground(color);
                    countCases();
                    counted= true;
                }

            }

        }
        else
        {
            gc.setColor(new Color(100, 100, 255));
            gc.fillRect(1,1,getWidth(), getHeight());
        }


    }

    /**
     * This method is used to draw a string at the center of the case, in a responsive way.
     *
     * @param gc an instance of the Graphic class, which is used in order to draw onto components.
     * @param txt string to draw inside graphic
     */
    private void drawCenterString(Graphics gc, String txt) {
        FontMetrics fm = gc.getFontMetrics();
        int stringWidth = fm.stringWidth(txt);
        int stringAccent = fm.getAscent();
        int xCoordinate = getWidth() / 2 - stringWidth / 2; //x-coordinate use to center text horizontally
        int yCoordinate = getHeight() / 2 + stringAccent / 2; //y-coordinate use to center text vertically
        gc.drawString(txt, xCoordinate, yCoordinate);
    }

    /**
     * This funciton reinitialize the case to its initial state
     */
    public void newGame(){
        revealed = false;
        enabledClick = true;
        counted = false;
        repaint();
    }


    /**
     * Computes the number of clicked cases during a game in the single-player mode. If the number of cases equals
     * the size of the champ minus the number of mines, then the game is won and it offers to start a new one.
     */
    private void countCases(){
        if(!counted && (demineur.getClient() == null)) {
            demineur.setNbDiscoveredCases(demineur.getNbDiscoveredCases() + 1); //New case was discovered

            //All cases except cases representing bombes were discovered so it is a win
            if (demineur.getGameChamp().getNbMines() == ((demineur.getGameChamp().getDimX() * demineur.getGameChamp().getDimY()) - demineur.getNbDiscoveredCases())) {

                demineur.getGuiClient().blockGame();
                //Add the score in the score sheet
                demineur.getScoreRegistering().getScoreLinkedList().add(new Score(demineur.getGuiClient().getCounter().getTime(), demineur.getScoreRegistering().getDateFormat().format(Calendar.getInstance().getTime()), demineur.getGameChamp().getNiveauChamp().name()));
                demineur.getScoreRegistering().write();

                int response = JOptionPane.showConfirmDialog(
                        null,
                        "Congrats !",
                        "Good job my boy !!!",
                        JOptionPane.YES_NO_OPTION
                );
                if (response == JOptionPane.YES_OPTION) {
                    demineur.getGuiClient().newGame();
                }
            }
        }
    }

    /**
     * This functions set a boolean to a certain value to define if the case can be clicked on
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabledClick=enabled;
    }


    /**
     * THis function is called when the mouse perform an action on a case component
     * @param e mouse event
     * */
    @Override
    public void mousePressed(MouseEvent e){
        if(demineur.getClient() == null){
            if(enabledClick)
            {
                counter.start();
                repaint();
                revealed = true;
                value = demineur.getGameChamp().champ[i][j]; //Get value of case
                if(demineur.getGameChamp().champ[i][j]== -1) //Case is a bomb -> defeat
                {
                    demineur.getGuiClient().blockGame();
                    if (JOptionPane.showConfirmDialog(
                            null,
                            "You lost ! Would you like to start over ?",
                            "Defeat",
                            JOptionPane.YES_NO_OPTION
                    ) == JOptionPane.YES_OPTION) {
                        demineur.getGuiClient().newGame(demineur.getGameChamp().getNiveauChamp());
                    }
                }
            }
        }
        else {
            try{
                if(demineur.getClient().isStarted()){
                    demineur.getClient().getOut().writeUTF("click " + i + " " + j);//send instruction that case was clicked
                }
            } catch (IOException exception){
                exception.printStackTrace();

            }
        }
    }

    /**
     *  This function is only used in multi-player mode
     *  It replaces the case value send by the server and send it to each client and repaint the case a the same time
     *  with the color of the client who clicked on it
     * @param caseValue value of the case clicked
     * @param playerColor color of the player who clicked on the case
     */
    void clientRepaint(int caseValue, Color playerColor){
        revealed = true;
        value = caseValue;
        color = playerColor;
        repaint();

    }

    /**
     * Method not used
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /**
     * Method not used
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /**
     * Method not used
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Method not used
     */
    @Override
    public void mouseClicked(MouseEvent e) {


    }



}
