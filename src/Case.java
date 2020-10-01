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
                    setBackground(Color.LIGHT_GRAY);
                    countCases();
                    counted = true;
                }
                else
                {
                    drawCenterString(gc,caseValue);
                    setBackground(Color.LIGHT_GRAY);
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
            repaint();
            //All cases except cases representing bombes were discovered so it is a win
            if (lclGui.demineur.getGameChamp().getNbMines() == ((lclGui.demineur.getGameChamp().getDimX() * lclGui.demineur.getGameChamp().getDimY()) - lclGui.demineur.getNbDiscoveredCases())) {

                lclGui.blockGame();

                int input = JOptionPane.showConfirmDialog(
                        null,
                        "Congrats ! You won this round, well played !\nWanna play again ?",
                        "Good job my boy !!!",
                        JOptionPane.YES_NO_OPTION);
                if(input == JOptionPane.YES_OPTION){
                    lclGui.newGame(lclGui.demineur.getGameChamp().getNiveauChamp());
                }

                /*if (JOptionPane.showConfirmDialog(
                        null,
                        "Congrats ! You won this round, well played !\nWanna play again ?",
                        "Good job my boy !!!",
                        JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION) {
                    lclGui.newGame(lclGui.demineur.getGameChamp().getNiveauChamp());
                }*/
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabledClick=enabled;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
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

    @Override
    public void mousePressed(MouseEvent e){

}

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


}
