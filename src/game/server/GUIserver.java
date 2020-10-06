package game.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GUIserver extends JFrame implements ActionListener {

    private JLabel portLabel = new JLabel("Port: ");
    private JTextField port = new JTextField("8080");
    private JLabel levelLabel = new JLabel("Level: ");
    private JComboBox<String> levelComboBox = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
    private JButton launch = new JButton("Launch");
    private JButton stop = new JButton("Stop");

    private JTextArea log = new JTextArea("Press 'Launch' to accept connections\n", 20, 40);

    private boolean gameStarted = false;
    private Server server;

    private GUIserver(){
        super("Demineur serveur");

        setLayout(new BorderLayout());
        log.setEditable(false);
        log.setEditable(false);

        JScrollPane jScrollPane = new JScrollPane(log);
        JLabel title = new JLabel("Server management");
        add(title, BorderLayout.NORTH);
        add(jScrollPane, BorderLayout.CENTER);

        add(createBottomPane(), BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);


        setVisible(true);
        pack();



    }

    public static void main(String[] args){
        GUIserver guiserver = new GUIserver();
    }

    private JPanel createBottomPane(){
        JPanel bottomPane= new JPanel();

        bottomPane.setLayout(new FlowLayout());
        bottomPane.add(portLabel);
        bottomPane.add(port);
        bottomPane.add(levelLabel);
        bottomPane.add(levelComboBox);
        bottomPane.add(launch);
        launch.addActionListener(this);
        bottomPane.add(stop);
        stop.addActionListener(this);
        stop.setEnabled(false);
        return bottomPane;

    }



    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(launch.equals(source) && launch.getText().equals("Launch"))
        {
            int portNumber = Integer.parseInt(port.getText());
            String ipAddress = null;
            try {
                ipAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
            launch.setText("Start game");
            log.append("Server started.\nIp address: " + ipAddress + ":" + portNumber + "\n");
            pack();
            server = new Server(portNumber, this);
            server.start();
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent event) {
                    if (JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure?",
                            "Quit",
                            JOptionPane.YES_NO_OPTION
                    ) == JOptionPane.YES_OPTION) server.closeServer();
                }
            });
        } else if(launch.equals(source) && launch.getText().equals("Start game")){
            gameStarted = true;
            port.setEnabled(false);
            levelComboBox.setEnabled(false);
            stop.setEnabled(true);
            launch.setText("Pause game");

            //server.stopSocketServer();
            log.append("Entering connections are now blocked.\n Game will begin with " + server.clientListLength() + " players, in " + levelComboBox.getSelectedItem() + " mode\n");
            server.startGame((String) levelComboBox.getSelectedItem());
        } else if(launch.equals(source) && launch.getText().equals("Pause game")){
            server.pauseGame();
            launch.setText("Resume game");
        } else if(launch.equals(source) && launch.getText().equals("Resume game")){
            server.resumeGame();
            launch.setText("Pause game");
        } else if(stop.equals(source)){
            if(JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure ?",
                    "Quit",
                    JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION){
                gameStarted = false;
                server.stopGame();
            }
        }

    }

    /**
     * @return a String containing the level selected by the user.
     */
    String getLevelBox() {
        return (String) levelComboBox.getSelectedItem();
    }

    /**
     * @return the log interface of the GUI.
     */
    JTextArea getLog() {
        return log;
    }

    /**
     * This function returns if gas has started
     * @return
     */
    boolean isGameStarted(){return gameStarted;}

}
