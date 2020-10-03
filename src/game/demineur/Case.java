package game.demineur;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Case extends JPanel implements MouseListener {

    String caseValue;
    int i;
    int j;
    boolean revealed = false;
    private boolean counted;
    GUI lclGui;
    boolean enabledClick = true;
    private final static int DIM = 25;
    private Color color = Color.lightGray;


    /**
     * Constructor
     * @param caseValue value of the case, if it is a mine or nb of mines around
     * @param i x-coordinate of the case
     * @param j y-coordinate of the case
     * @param gui gui instance related to this case
     */
    public Case(String caseValue, int i, int j, GUI gui){
        this.caseValue= caseValue;
        this.i = i;
        this.j = j;
        this.lclGui = gui;

        setPreferredSize(new Dimension(DIM, DIM)); // taille de la case
        if(!revealed)
        {
            addMouseListener(this);
        }

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
            if(caseValue.equals("X"))
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
                if(caseValue.equals("0"))
                {
                    drawCenterString(gc, "");
                    setBackground(color);
                    countCases();
                    counted = true;
                }
                else
                {
                    drawCenterString(gc,caseValue);
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
        repaint();
    }


    /**
     * Computes the number of clicked cases during a game in the single-player mode. If the number of cases equals
     * the size of the champ minus the number of mines, then the game is won and it offers to start a new one.
     */
    private void countCases(){
        if(!counted) {
            lclGui.demineur.setNbDiscoveredCases(lclGui.demineur.getNbDiscoveredCases() + 1);

            //All cases except cases representing bombes were discovered so it is a win
            if (lclGui.demineur.getGameChamp().getNbMines() == ((lclGui.demineur.getGameChamp().getDimX() * lclGui.demineur.getGameChamp().getDimY()) - lclGui.demineur.getNbDiscoveredCases())) {

                //lclGui.blockGame();
                int response = JOptionPane.showConfirmDialog(
                        lclGui,
                        "Congrats !",
                        "Good job my boy !!!",
                        JOptionPane.YES_NO_OPTION
                );
                if (response == JOptionPane.YES_OPTION) {
                    lclGui.newGame(lclGui.demineur.getGameChamp().getNiveauChamp());
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
        if(lclGui.demineur.getClient() == null){
            if(enabledClick)
            {
                revealed = true;

                lclGui.counter.start();
                repaint();
                if(lclGui.demineur.getGameChamp().champ[i][j]== -1)
                {
                    lclGui.blockGame();
                    if (JOptionPane.showConfirmDialog(
                            null,
                            "You lost ! Would you like to start over ?",
                            "Defeat",
                            JOptionPane.YES_NO_OPTION
                    ) == JOptionPane.YES_OPTION) {
                        lclGui.newGame(lclGui.demineur.getGameChamp().getNiveauChamp());
                    }
                }


            }
        }
       else {
           try{
               if(lclGui.demineur.getClient().isStarted()){
                   lclGui.demineur.getClient().getOut().writeUTF("click " + i + " " + j);
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
    public void mousePressed(MouseEvent e){

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
    void clientRepaint(String caseValue, Color playerColor){
        revealed = true;
        this.caseValue = caseValue;
        color = playerColor;
        repaint();

    }


}
