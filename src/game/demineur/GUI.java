package game.demineur;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
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

    private JLabel portLabel = new JLabel("Port: ");
    private JTextField port = new JTextField("8080");

    private JLabel ipAddressLabel = new JLabel("Ip Address: ");
    private JTextField ipAddress = new JTextField("localhost");
    private JLabel pseudoLabel = new JLabel("Pseudo: ");
    public JTextField pseudo = new JTextField("", 5);

    private JButton connect = new JButton("Connect");

    private JTextPane log = new JTextPane();
    private JTextArea chat = new JTextArea(1,20);
    private JButton sendChat = new JButton("Send");


    public JPanel topPanel = new JPanel();
    public JPanel centerPanel = new JPanel();
    public JPanel westPanel = new JPanel();


    /**
     * Constructor
     * @param demineur instance of demineur running under this GUI
     */
    public GUI(Demineur demineur){
        super(new BorderLayout());

        this.demineur = demineur;




        createTopPanel();
        JPanel bottomPanel = createBottomPanel();
        createCenterPanel();


        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(counter, BorderLayout.WEST);
        createGameMenu();


    }

    /**
     * This function create the top panel for the interface with the level
     */
    private  void createTopPanel() {

        JPanel subTopPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        JLabel title = new JLabel("Welcome to Emilie's Minesweeper");
        subTopPanel.add(title);
        topPanel.add(subTopPanel, BorderLayout.WEST);
        JLabel niveau = new JLabel("Niveau: " + demineur.getGameChamp().getNiveauChamp(), JLabel.CENTER);
        JPanel niveauPanel = new JPanel();
        niveauPanel.add(niveau);
        topPanel.add(niveauPanel, BorderLayout.EAST);
    }


    /**
     * This function creates the menu bar located above the demineur's top panel
     */
    private void createGameMenu()
    {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        Quit.addActionListener(this);
        Quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.META_DOWN_MASK));
        Quit.setToolTipText("The End");

        jMenuBar.add(gameMenu);
        gameMenu.add(Reset);
        Reset.addActionListener(this);
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

    /**
     * This function creates the bottom panel, where a player can start palying online
     * @return
     */
    public JPanel createBottomPanel()
    {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.add(portLabel);
        bottomPanel.add(port);
        bottomPanel.add(ipAddressLabel);
        bottomPanel.add(ipAddress);
        bottomPanel.add(pseudoLabel);
        bottomPanel.add(pseudo);
        bottomPanel.add(connect);
        connect.addActionListener(this);


        return bottomPanel;
    }

    /**
     * This function create the panel where the demineur's grid will be
     */
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


    /**
     * This function creates the multi-player interface, where all messages sent and received will be
     */
    void createLog() {
        JPanel logPanel = new JPanel();
        JPanel chatBar = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

        log.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(log);
        jScrollPane.setPreferredSize(new Dimension(50, 300));
        EmptyBorder emptyBorder = new EmptyBorder(new Insets(10, 10, 10, 10));
        jScrollPane.setBorder(emptyBorder);
        appendToPane("Welcome to the online version of the MineSweeper !\n", Color.red, true);

        DefaultCaret caret = (DefaultCaret) log.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        chatBar.add(chat);
        chatBar.add(sendChat);
        sendChat.addActionListener(this);
        sendChat.setMnemonic(KeyEvent.VK_ENTER);

        logPanel.add(jScrollPane);
        logPanel.add(chatBar);

        add(logPanel, BorderLayout.EAST);
        demineur.pack();
        setVisible(true);
    }

    /**
     * This function starts a new game by resetting attributes
     * @param niveau of the new game
     */
    void newGame(Common.Niveau niveau) {
        demineur.getGameChamp().setLevel(niveau);
        centerPanel.removeAll();
        topPanel.removeAll();
        createTopPanel();
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

    /**
     * This function block the game when a mine is hit
     */
    void blockGame(){
        for (int _i = 0; _i < demineur.getGameChamp().dimX; _i++) {
            for (int _j = 0; _j < demineur.getGameChamp().dimY; _j++) {
                    tabCase[_i][_j].setEnabled(false);
            }
        }
        counter.stop();
    }

    /**
     * Adds a text to the pane "log".
     *
     * @param text    the text that needs to be added to the pane.
     * @param color   the color with which the text must be displayed.
     * @param isAdmin boolean that states if whether or not this message is send by the server, or if it is a
     *                message from another client.
     */
    void appendToPane(String text, Color color, boolean isAdmin) {
        StyledDocument styledDocument = log.getStyledDocument();

        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, color);
        StyleConstants.setBold(attributeSet, isAdmin);
        log.setCharacterAttributes(attributeSet, false);
        log.setCaretPosition(log.getText().length());

        try {
            styledDocument.insertString(styledDocument.getLength(), text, attributeSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function manages all client's actions
     * @param e
     */
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
            JTextField dimX = new JTextField();
            JTextField dimY = new JTextField();
            JTextField nbMines = new JTextField();
            Object[] parameters = {
                    "Dimension X:", dimX,
                    "Dimension Y:", dimY,
                    "Nombre de mines:", nbMines
            };

            int option = JOptionPane.showConfirmDialog(null, parameters, "Custom Level", JOptionPane.OK_CANCEL_OPTION);

            if(option == JOptionPane.OK_OPTION)
            {
                int DimX = Integer.parseInt(dimX.getText());
                int DimY = Integer.parseInt(dimY.getText());
                int mines = Integer.parseInt(nbMines.getText());

                demineur.getGameChamp().customLevel[0] = DimX;
                demineur.getGameChamp().customLevel[1] = DimY;
                demineur.getGameChamp().customLevel[2] = mines;
            }




            newGame(Common.Niveau.CUSTOM);

        }
        else if(Reset.equals(source))
        {
            blockGame();
            newGame(demineur.getGameChamp().getNiveauChamp());
        }

        else if(connect.equals(source))
        {
           demineur.setClient(new Client(ipAddress.getText(), port.getText(),pseudo.getText(), this));
        }else if (sendChat.equals(source)) {
            String message = this.chat.getText();
            this.chat.setText("");
            demineur.getClient().sendMessage(message);
        }
    }

    /**
     * Displays the pseudo and the ID of the player when he successfully connects to a server.
     */
    void displayID() {
        JOptionPane.showConfirmDialog(
                null,
                "You are now connected. Your pseudo is " + demineur.getClient().getPlayerName() + " and your ID is " + demineur.getClient().getPlayerId() + ".",
                "Connected !",
                JOptionPane.DEFAULT_OPTION
        );
        connect.setEnabled(false);
        ipAddress.setEnabled(false);
        port.setEnabled(false);
        pseudo.setEnabled(false);
    }

    public Case[][] getTabCase() {
        return tabCase;
    }
    /**
     * This function allow people to join another game after their previous one finishes.
     */
    void disableOnlineDisplay() {
        chat.setEnabled(false);
        sendChat.setEnabled(false);
        pseudo.setEnabled(true);
        port.setEnabled(true);
        ipAddress.setEnabled(true);
        connect.setEnabled(true);
    }
}
