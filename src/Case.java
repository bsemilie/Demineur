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



    private final static int DIM = 25;


    public Case(String caseValue, int i, int j, GUI gui){
        this.caseValue= caseValue;
        this.i = i;
        this.j = j;

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
                    gc.drawString("", 8, 15);
                    setBackground(Color.LIGHT_GRAY);
                }
                else
                {
                    gc.drawString(caseValue, 8,15);
                    setBackground(Color.LIGHT_GRAY);
                }

            }

        }
        else
        {
            gc.setColor(new Color(100, 100, 255));
            gc.fillRect(1,1,getWidth(), getHeight());
        }


    }

    @Override
    public void mouseClicked(MouseEvent e) {
        revealed = true;
        repaint();
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
