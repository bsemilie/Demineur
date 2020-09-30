import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JPanel {

    private Demineur demineur;




    /**
     *
     */
    public GUI(Demineur demineur){
        super(new BorderLayout());

        this.demineur = demineur;




        JPanel topPanel = createTopPanel();
        JPanel bottomPanel = createBottomPanel();
        JPanel centerPanel = createCenterPanel();


        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

    }



    public JPanel createTopPanel()
    {
        JPanel topPanel = new JPanel();

        JLabel score = new JLabel("Score: ");
        topPanel.add(score);
        JLabel title = new JLabel("Démineur");
        topPanel.add(title);
        JLabel niveau = new JLabel("Niveau: " + demineur.getGameChamp().getNiveauChamp());
        topPanel.add(niveau);
        return topPanel;
    }

    public JPanel createBottomPanel()
    {
        JPanel bottomPanel = new JPanel();


        MyActionListener actionListener = new MyActionListener();
        bottomPanel.add(actionListener.butReset);
        actionListener.butReset.addActionListener(actionListener);
        actionListener.butQuit.addActionListener(actionListener);
        bottomPanel.add(actionListener.butQuit);
        return bottomPanel;
    }

    public JPanel createCenterPanel()
    {
        JPanel gridContainer = new JPanel();
        JPanel centerPanel = new JPanel();
        GridLayout centerGrid = new GridLayout(demineur.getGameChamp().dimX,demineur.getGameChamp().dimY, 5,5);

        centerPanel.setLayout(centerGrid);

        for (int _i=0; _i<demineur.getGameChamp().dimX;_i++)
        {
            for(int _j=0; _j<demineur.getGameChamp().dimY; _j++)
            {
                if(demineur.getGameChamp().champ[_i][_j] == -1)
                {

                    Case gridCase = new Case("X", _i, _j, this);




                    centerPanel.add(gridCase);
                }
                else
                {

                    Case gridCase = new Case(String.valueOf(demineur.getGameChamp().champ[_i][_j]), _i, _j, this);


                    centerPanel.add(gridCase);
                }

            }
        }

        gridContainer.add(centerPanel);
        return gridContainer;
    }
}
