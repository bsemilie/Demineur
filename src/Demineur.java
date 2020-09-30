import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.PAGE_END;
import static javax.swing.SpringLayout.SOUTH;

/**
 * @author emilie
 * @version 2.0
 */

public class Demineur extends JFrame {



    private Champ gameChamp;

    public Champ getGameChamp() {
        return gameChamp;
    }


    public Demineur(){
        gameChamp = new Champ(Common.Niveau.MEDIUM);
        System.out.println(gameChamp + "\n\n");


        GUI gui = new GUI(this);
        ImageIcon img = new ImageIcon(getClass().getResource("minesIcon.png"));
        setIconImage(img.getImage());
        setContentPane(gui);
        setPreferredSize(new Dimension(900,600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        setVisible(true);
    }
    public static void main(String[] args){

       new Demineur();
    }
}
