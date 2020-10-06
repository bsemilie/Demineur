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

public class Case extends JPanel implements MouseListener {

    String caseValue;
    int i;
    int j;
    private boolean revealed = false;
    private boolean counted;
    private Demineur demineur;
    private Counter counter;
    private boolean enabledClick;
    private final static int DIM = 25;
    private Color color = Color.lightGray;
    private int value;


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
        this.enabledClick = true;

        setPreferredSize(new Dimension(DIM, DIM)); // taille de la case
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
                    if(image == null)
                    {
                        System.out.println("Image is null");
                    }
                    gc.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                if(value == 0)
                {
                    drawCenterString(gc, "");
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
        int xCoordinate = getWidth() / 2 - stringWidth / 2;
        int yCoordinate = getHeight() / 2 + stringAccent / 2;
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
            demineur.setNbDiscoveredCases(demineur.getNbDiscoveredCases() + 1);

            //All cases except cases representing bombes were discovered so it is a win
            if (demineur.getGameChamp().getNbMines() == ((demineur.getGameChamp().getDimX() * demineur.getGameChamp().getDimY()) - demineur.getNbDiscoveredCases())) {

                demineur.getGuiClient().blockGame();
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
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {


    }

    /**
     * Method not used
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e){
        if(demineur.getClient() == null){
            if(enabledClick)
            {
                counter.start();
                repaint();
                revealed = true;
                value = demineur.getGameChamp().champ[i][j];
                if(demineur.getGameChamp().champ[i][j]== -1)
                {
                    demineur.getGuiClient().blockGame();
                    if (JOptionPane.showConfirmDialog(
                            null,
                            "You lost ! Would you like to start over ?",
                            "Defeat",
                            JOptionPane.YES_NO_OPTION
                    ) == JOptionPane.YES_OPTION) {
                        demineur.getGuiClient().newGame();
                    }
                }
            }
        }
        else {
            try{
                if(demineur.getClient().isStarted()){
                    demineur.getClient().getOut().writeUTF("click " + i + " " + j);
                }
            } catch (IOException exception){
                exception.printStackTrace();

            }
        }
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
     *  This function is only used in multi-player mode
     *  It replaces the case value send by the server and send it to each client and repaint the case a the same time
     *  with the color of the client who clicked on it
     * @param caseValue
     * @param playerColor
     */
    void clientRepaint(int caseValue, Color playerColor){
        revealed = true;
        value = caseValue;
        color = playerColor;
        repaint();

    }


}
