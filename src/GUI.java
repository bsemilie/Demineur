import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class GUI extends JPanel implements ActionListener {

    public Demineur demineur;

    public Case[][] tabCase;

    public Counter counter = new Counter(200, 20);

    private JMenuItem Reset = new JMenuItem("Reset", KeyEvent.VK_R);
    private JMenuItem Quit = new JMenuItem("Quit", KeyEvent.VK_Q);
    private JMenuItem mEasy = new JMenuItem("Easy", KeyEvent.VK_E);
    private JMenuItem mMedium = new JMenuItem("Medium", KeyEvent.VK_M);
    private JMenuItem mHard = new JMenuItem("Hard", KeyEvent.VK_H);
    private JMenuItem mCustom = new JMenuItem("Custom", KeyEvent.VK_C);


    public JPanel centerPanel = new JPanel();
    public JPanel westPanel = new JPanel();




    /**
     *
     */
    public GUI(Demineur demineur){
        super(new BorderLayout());

        this.demineur = demineur;




        JPanel topPanel = createTopPanel();
        JPanel bottomPanel = createBottomPanel();
        createCenterPanel();
        createWestPanel();


        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(counter, BorderLayout.EAST);
        createGameMenu();
        add(westPanel, BorderLayout.WEST);

    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        JLabel title = new JLabel("Welcome to Emilie's Minesweeper");
        topPanel.add(title);
        return(topPanel);
    }


    private void createGameMenu()
    {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        Quit.addActionListener(this);
        Quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.META_DOWN_MASK));
        Quit.setToolTipText("The End");

        jMenuBar.add(gameMenu);
        gameMenu.add(Quit);

        //Menu level
        JMenu menuLevel = new JMenu("Level");

        jMenuBar.add(menuLevel);
        menuLevel.add(mEasy);
        mEasy.addActionListener(this);
        menuLevel.add(mMedium);
        mMedium.addActionListener(this);
        menuLevel.add(mHard);
        mHard.addActionListener(this);
        menuLevel.add(mCustom);
        mCustom.addActionListener(this);

        menuLevel.add(Reset);
        Reset.addActionListener(this);
        Reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.META_DOWN_MASK));

        //Make some space
        jMenuBar.add(Box.createGlue());

        //Menu help
        JMenu menuHelp = new JMenu("Help");
        JMenuItem mAbout = new JMenuItem("About this");

        jMenuBar.add(menuHelp);
        menuHelp.add(mAbout);

        demineur.setJMenuBar(jMenuBar);


    }

    public JPanel createBottomPanel()
    {
        JPanel bottomPanel = new JPanel();

        return bottomPanel;
    }

    public void createCenterPanel()
    {

        GridLayout centerGrid = new GridLayout(demineur.getGameChamp().dimX,demineur.getGameChamp().dimY, 5,5);
        tabCase= new Case[demineur.getGameChamp().dimX][demineur.getGameChamp().dimY];

        centerPanel.setPreferredSize(new Dimension(500, 500));
        centerPanel.setLayout(centerGrid);

        for (int _i=0; _i<demineur.getGameChamp().dimX;_i++)
        {
            for(int _j=0; _j<demineur.getGameChamp().dimY; _j++)
            {
                if(demineur.getGameChamp().champ[_i][_j] == -1)
                {
                    tabCase[_i][_j] = new Case("X", _i, _j, this);
                    centerPanel.add(tabCase[_i][_j]);
                }
                else
                {
                    tabCase[_i][_j] = new Case(String.valueOf(demineur.getGameChamp().champ[_i][_j]), _i, _j, this);
                    centerPanel.add(tabCase[_i][_j]);
                }
            }
        }

    }

    public void createWestPanel(){
        JLabel niveau = new JLabel("Niveau: " + demineur.getGameChamp().getNiveauChamp(), JLabel.CENTER);
        westPanel.setLayout(new BorderLayout());
        westPanel.add(niveau, BorderLayout.CENTER);

    }

    void newGame(Common.Niveau niveau) {
        demineur.getGameChamp().setLevel(niveau);
        centerPanel.removeAll();
        westPanel.removeAll();
        createWestPanel();
        createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        add(westPanel, BorderLayout.WEST);

        for (int _i = 0; _i < demineur.getGameChamp().dimX; _i++) {
            for (int _j = 0; _j < demineur.getGameChamp().dimY; _j++) {
                tabCase[_i][_j].newGame();
            }
        }
        centerPanel.revalidate();
        centerPanel.repaint();
        counter.reset();
        demineur.pack();
        System.out.println(demineur.getGameChamp().toString());
    }

    void blockGame(){
        for (int _i = 0; _i < demineur.getGameChamp().dimX; _i++) {
            for (int _j = 0; _j < demineur.getGameChamp().dimY; _j++) {
                    tabCase[_i][_j].setEnabled(false);
            }
        }
        counter.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (Quit.equals(source)) {
            if (JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure?",
                    "Quit",
                    JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION)
                demineur.quit();
        }
        else if(mEasy.equals(source))
        {
            blockGame();
            newGame(Common.Niveau.EASY);
        }
        else if(mMedium.equals(source))
        {
            blockGame();
            newGame(Common.Niveau.MEDIUM);
        }
        else if(mHard.equals(source))
        {
            blockGame();
            newGame(Common.Niveau.HARD);
        }
        else if(mCustom.equals(source))
        {
            String customParameters = JOptionPane.showInputDialog(null, "Enter the parameters for the custom level, first the X dimension, then Y dimension, then number of mines", "Custom");
            System.out.println(customParameters);

            String[] arrayCustomParameters = customParameters.split(",");

            int dimX = Integer.parseInt(arrayCustomParameters[0]);
            int dimY = Integer.parseInt(arrayCustomParameters[1]);
            int mines = Integer.parseInt(arrayCustomParameters[2]);

           demineur.getGameChamp().customLevel[0] = dimX;
           demineur.getGameChamp().customLevel[1] = dimY;
           demineur.getGameChamp().customLevel[2] = mines;

            newGame(Common.Niveau.CUSTOM);

        }
        else if(Reset.equals(source))
        {
            blockGame();
            newGame(demineur.getGameChamp().getNiveauChamp());
        }
    }
}
