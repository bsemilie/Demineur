package game.demineur;
import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Client extends Thread{


    private int PlayerId;

    public String PlayerName;
    public Socket socket;
    public DataOutputStream out;
    public DataInputStream in;
    private Demineur demineur;
    private boolean started;
    private boolean replaying;

    /**
     * Constructor
     * @param ipAddress of the server
     * @param port on which server accept connections
     * @param playerName name of the player, Denis is player's name by default + random number
     * @param demineur  actual instance of demineur
     */
    Client(String ipAddress, String port, String playerName, Demineur demineur){
        try {
            this.demineur = demineur;
            socket = new Socket(ipAddress, Integer.parseInt(port));
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            Random r = new Random();
            if (playerName.isEmpty()) {
                this.PlayerName = "Denis" + r.nextInt();
                demineur.getGuiClient().getPseudo().setText(playerName);

            } else {
                this.PlayerName = playerName;

            }
            this.start();
        } catch (IOException e) {
            System.out.println("The ip address " + ipAddress + ":" + port + " is unreachable");
            JOptionPane.showConfirmDialog(
                    null,
                    "The ip address " + ipAddress + ":" + port + " is unreachable",
                    "Error",
                    JOptionPane.DEFAULT_OPTION
            );
        }


    }


    /**
     * This function manages the server's instruction and perform actions bases on these instructions
     */
    @Override
    public void run() {
        try {
            demineur.getGuiClient().createLog();
            out.writeUTF(PlayerName);
            PlayerId = in.readInt();
            demineur.getGuiClient().displayID();

            while (this != null) {
                String input = in.readUTF();
                String[] inputArray = input.split("\\s+");
                String date, playerColor, playerScore;
                int x, y, nbMines, playerId;
                switch (inputArray[0]) {
                    case "start":
                        replaying = false;
                        started = true;
                        date = inputArray[1];
                        String difficulty = inputArray[2];
                        demineur.getGuiClient().appendToPane(date + " - Game started !\n Difficulty: " + difficulty + "\n", Color.BLACK, true);
                        demineur.getGameChamp().setChamp(Common.Niveau.valueOf(difficulty));
                        demineur.getGuiClient().newGame(Common.Niveau.valueOf(difficulty));
                        break;
                    case "eliminated":
                        date = inputArray[1];
                        String playerName = inputArray[2];
                        x = Integer.parseInt(inputArray[3]);
                        y = Integer.parseInt(inputArray[4]);
                        playerColor = inputArray[5];
                        playerScore = inputArray[6];
                        playerId = Integer.parseInt(inputArray[7]);
                        demineur.getGuiClient().appendToPane(date + " - " + playerName + " is eliminated ! His score: " + playerScore + " points\n", new Color(Integer.parseInt(playerColor)), true);
                        demineur.getGuiClient().getTabCase()[x][y].clientRepaint(-1, new Color(Integer.parseInt(playerColor)));
                        if (playerId == demineur.getGuiClient().demineur.getClient().getPlayerId()) {
                            if (JOptionPane.showConfirmDialog(
                                    null,
                                    "Game is over. YOur score: " + playerScore + " points\nDo you want to wait the next game?",
                                    "Game over buddy!",
                                    JOptionPane.YES_NO_OPTION
                            ) == JOptionPane.NO_OPTION) {
                                out.writeUTF("new false");
                                this.close();
                            } else {
                                out.writeUTF("new true");
                                replaying = true;
                            }
                        }
                        break;
                    case "clicked":
                        nbMines = Integer.parseInt(inputArray[1]);
                        x = Integer.parseInt(inputArray[2]);
                        y = Integer.parseInt(inputArray[3]);
                        playerColor = inputArray[4];
                        demineur.getGuiClient().getTabCase()[x][y].clientRepaint(nbMines, new Color(Integer.parseInt(playerColor)));
                        break;
                    case "pause":
                        date = inputArray[1];
                        JOptionPane.showConfirmDialog(
                                null,
                                "Game has been paused by an admin",
                                "Pause",
                                JOptionPane.DEFAULT_OPTION
                        );
                        demineur.getGuiClient().appendToPane(date + " - Game has been paused\n", Color.BLACK, true);
                        break;
                    case "resume":
                        date = inputArray[1];
                        demineur.getGuiClient().appendToPane(date + " - Game has been resumed\n", Color.BLACK, true);
                        break;
                    case "left":
                        date = inputArray[1];
                        playerName = inputArray[2];
                        demineur.getGuiClient().appendToPane(date + " - " + playerName + " has left the game\n", Color.BLACK, true);
                        break;
                    case "end":
                        if (!replaying) {
                            if (JOptionPane.showConfirmDialog(
                                    null,
                                    "Game is over. Do you want to play again ?",
                                    "Game is over !",
                                    JOptionPane.YES_NO_OPTION
                            ) == JOptionPane.YES_OPTION) {
                                out.writeUTF("new true");
                                replaying = true;
                            }
                        }
                        break;
                    case "message":
                        date = inputArray[1];
                        playerColor = inputArray[2];
                        playerName = inputArray[3];
                        StringBuilder message = new StringBuilder();
                        message.append(date);
                        message.append(" - ");
                        message.append(playerName).append(": ");
                        for (int i = 4; i < inputArray.length; i++) {
                            message.append(inputArray[i]).append(" ");
                        }
                        message.append("\n");
                        demineur.getGuiClient().appendToPane(message.toString(), new Color(Integer.parseInt(playerColor)), true);
                        break;
                    default:
                        demineur.getGuiClient().appendToPane(input, Color.BLACK, true);
                }

            }
        } catch (IOException e) {
            demineur.getGuiClient().appendToPane("Connection lost ...\n", Color.red, true);
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "You have lost your connection to the server! DO you want to play in solo mode ?",
                    " Connection lost",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                this.close();
            }
        }
    }

    /**
     * This function closed the thread and start a new game
     */
    private  void close(){
        try{
            in.close();
            out.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        demineur.setClient(null);
        demineur.getGameChamp().setChamp(Common.Niveau.EASY);
        demineur.getGuiClient().newGame(Common.Niveau.EASY);
        demineur.getGuiClient().disableOnlineDisplay();
    }

    /**
     * Send message to server
     * @param message
     */
    void sendMessage(String message){
        try{
            out.writeUTF("message " + message);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * This functions return the name of the player
     * @return
     */
    public String getPlayerName() {
        return PlayerName;
    }

    /**
     * This function return the id of the player
     * @return
     */
    public int getPlayerId() {
        return PlayerId;
    }


    /**
     * This function returns the output data stream related to this thread
     * @return
     */
    DataOutputStream getOut() {
        return out;
    }

    /**
     * This function returns if a game has started
     * @return
     */
    boolean isStarted() {
        return started;
    }

}
