package game.demineur;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author emilie
 * @version 2.0
 */

public class Demineur extends JFrame {



    private Champ gameChamp;
    private GUI GUIclient;
    public Client client = null;
    private int nbDiscoveredCases = 0;
    private ScoreRegistering scoreRegistering = new ScoreRegistering();

    /**
     * Constructor which creates frame where demineur is
     */
    public Demineur(){
        super("Demineur");
        gameChamp = new Champ(Common.Niveau.EASY);
        gameChamp.placeMines();

        GUIclient = new GUI(this);
        ImageIcon img = new ImageIcon(getClass().getResource("minesIcon.png"));
        setIconImage(img.getImage());
        setContentPane(GUIclient);
        setPreferredSize(new Dimension(900,600));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pack();

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                if (JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure?",
                        "Quit",
                        JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION) quit();
            }
        });
    }

    public static void main(String[] args){

       new Demineur();
    }

    /**
     * This function terminates the process of demineur
     */
    void quit() {
        scoreRegistering.write();
        System.out.println("Adios");
        System.exit(0);
    }

    /**
     * THis function returns the number of discovered cases
     * @return
     */
    public int getNbDiscoveredCases() {
        return nbDiscoveredCases;
    }

    /**
     * This function sets the number of discovered cases
     * @param nbDiscoveredCases
     */
    public void setNbDiscoveredCases(int nbDiscoveredCases) {
        this.nbDiscoveredCases = nbDiscoveredCases;
    }

    /**
     * This function sets the client associated to this instance of demineur
     * @param client
     */
    public void setClient (Client client){ this.client = client;};

    /**
     * This function returns the client associated to this instance of demineur
     * @return
     */
    public Client getClient() {
        return client;
    }

    /**
     * This function returns the field associated to the instance of demineur
     * @return
     */
    public Champ getGameChamp() {
        return gameChamp;
    }
    /**
     * THis function returns the score registering instance used in this demineur
     * @return
     */
    ScoreRegistering getScoreRegistering() {
        return scoreRegistering;
    }

    /**
     * This function returns the GUI associated to this instance of demineur
     */
    public GUI getGuiClient(){ return GUIclient;}
}
