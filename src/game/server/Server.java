package game.server;


import game.demineur.Champ;
import game.demineur.Client;
import game.demineur.Common;
import game.demineur.GUI;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;

class Server extends Thread {

    private int playerId = 0;
    private ServerSocket serverSocket;
    private HashSet<ClientThread> clientList = new HashSet<>();
    private DateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private GUIserver guiServer;
    private Champ champ;
    private boolean[][] clickedCases;


    Server(int port, GUIserver guiServer) {
        this.guiServer = guiServer;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!(guiServer.isGameStarted())){
            Socket socket;

            try {
                socket = serverSocket.accept();
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                ClientThread clientThread = new ClientThread(socket, in, out, playerId, this);
                out.writeInt(playerId);
                playerId++;
                clientList.add(clientThread);
                guiServer.getLog().append(getDate() + " - A new client is connected : " + clientThread.getPlayerName() + "\n");

                broadcastMessage(getDate() + " - A new client is connected : " + clientThread.getPlayerName() + "\n");
                broadcastMessage("Clients connected :\n");
                for (ClientThread cT : clientList) {
                    broadcastMessage("     " + cT.getPlayerName() + "\n");
                }
                clientThread.start();


            } catch (SocketException ignored){

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * THis function stops the game and broadcast all overall scores to all connected players
     */
    void stopGame() {
        int playerScore;
        broadcastMessage(getDate() + " - Game is finished ! Here are the overall scores:\n");
        for (ClientThread clientThread : clientList) {
            playerScore = clientThread.getOverallScore();
            broadcastMessage("   " + clientThread.getPlayerName() + ": " + playerScore);
            guiServer.getLog().append("   " + clientThread.getPlayerName() + ": " + playerScore);
        }
        broadcastMessage("The server will be closed soon. Thanks for playing with us\n");
        closeServer();
    }

    /**
     * This function closes the client's list and end the program
     */
    void closeServer() {
        clientList.clear();
        System.exit(0);
    }

    /**
     * This function closes the server socket
     */
    void stopSocketServer() {
        try {
            serverSocket.close();
        } catch (SocketException ignored)
        {} catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function broadcasts a string entered in parameter to each player connected to the server
     *
     * @param message message to be sent
     */
    void broadcastMessage(String message) {
        for (ClientThread clientThread : clientList) {
            try {
                clientThread.getOut().writeUTF(message);
            } catch (IOException | NullPointerException ignored) {
            }
        }
    }

    /**
     * This function starts a new game by creating a new field
     *
     * @param level level for the game
     */
    void startGame(String level) {
        for (ClientThread clientThread : clientList) {
            clientThread.startGame();
        }
        broadcastMessage("start" + " " + getDate() + " " + level);
        this.champ = new Champ(Common.Niveau.valueOf(level));
        this.clickedCases = new boolean[champ.getDimX()][champ.getDimY()];
        champ.placeMines();
        champ.setNbMinesCase();
        System.out.println(champ.toString());
    }

    /**
     * This function pauses the game
     */
    void pauseGame() {
        guiServer.getLog().append(getDate() + " - Game has been paused\n");
        broadcastMessage("pause" + " " + getDate());
        for (ClientThread clientThread : clientList) {
            clientThread.setDisabled(true);
        }
    }

    /**
     * This function resumes the game
     */
    void resumeGame() {
        guiServer.getLog().append(getDate() + " - Game has been resumed\n");
        broadcastMessage("resume" + " " + getDate());
        for (ClientThread clientThread : clientList) {
            clientThread.setDisabled(false);
        }
    }

    /**
     * This function send a string to every client to specifiy that one player clicked on a case, and to inform
     * them if it's a mine or not, also sends the color of the player who clicked
     *
     * @param x            x-coordinate of the case clicked
     * @param y            y-coordinate of the case clicked
     * @param clientThread thread linked to the player who clicked
     * @param caseValue    value of the case that was clicked
     */
    void caseWasClicked(int x, int y, ClientThread clientThread, int caseValue) {
        if (caseValue == -1) {
            broadcastMessage("eliminated" + " " + getDate() + " " + clientThread.getPlayerName() + " "+ x + " " + y + " " + clientThread.getPlayerColor().getRGB() + " " + clientThread.getScore() + " " + clientThread.getClientId());
        } else {
            int nbMinesAround = champ.calculNbMines(x, y);
            broadcastMessage("clicked" + " " + nbMinesAround + " " + x + " " + y + " " + clientThread.getPlayerColor().getRGB());
        }
    }

    /**
     * This function check if the number of cases clicked by all players is equals to the total nb of cases minus nb of mines
     * If true then end of the game
     */
    void checkEndGame() {
        int totalScore = 0;
        int nbCases = getClickedCases().length * getClickedCases().length - getChamp().getNbMines();

        for (ClientThread clientThread : clientList) {
            totalScore += clientThread.getScore();
        }

        if (totalScore == nbCases) {
            broadcastMessage("end");
            resetAllScores();
        }
    }

    /**
     * This function resets all scores for each client
     */
    private void resetAllScores() {
        for(ClientThread clientThread: clientList){
            clientThread.resetScore();
        }
    }

    /**
     * This function check if each client does not want to replay and has left
     * If not then reset scores and restart game
     */
    void checkReplay(){
        for(ClientThread clientThread : clientList){
            if((!clientThread.wantsToReplay()) && (!clientThread.hasLeft())){
                return;
            }
        }
        resetAllScores();
        startGame(guiServer.getLevelBox());
    }

    /**
     * This function returns the number of clients in the server
     * @return
     */
    int clientListLength(){return clientList.size();}

    /**
     * THis function return the field associated to the server's game
     * @return
     */
    Champ getChamp() {return champ;}

    /**
     * THis function return the actual date on the server side
     * @return
     */
    String getDate() {
        String date = dateFormat.format(Calendar.getInstance().getTime());
        date = "[" + date + "]";
        return date;
    }

    /**
     * This function returns the array defining which cases have been clicked
     * @return
     */
    boolean[][] getClickedCases(){return clickedCases;}

    /**
     * This function return the GUi linked to the server
     * @return
     */
    GUIserver getGuiServer(){return  guiServer;}

}

class ClientThread extends Thread {

    private Socket sock;
    private DataInputStream in;
    private DataOutputStream out;
    private int clientId;



    private String PlayerName;
    private Server server;
    private Color playerColor = new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
    private int score = 0;
    private int overallScore = 0;
    private boolean disabled = true;
    private boolean wantsToReplay;
    private boolean hasLeft = false;

    /**
     * Constructor
     * @param sock socket linking with server and client
     * @param in datainputstream
     * @param out dataoutputstream
     * @param clientId id of the client
     * @param server server that enables connections
     * @throws IOException
     */
    ClientThread(Socket sock, DataInputStream in, DataOutputStream out, int clientId, Server server) throws IOException {
        this.sock = sock;
        this.in = in;
        this.out = out;
        this.clientId = clientId;
        this.PlayerName = in.readUTF();
        this.server = server;
    }

    /**
     * This function starts the client's thread. Listens to input of data linked to the socket and perform
     * tasks depending on what the client has send
     */
    @Override
    public void run() {
        try {
            while (this != null) {
                String input = in.readUTF();
                String [] inputArray = input.split("\\s+");
                switch(inputArray[0]){
                    case "click":
                        int x = Integer.parseInt(inputArray[1]);
                        int y = Integer.parseInt(inputArray[2]);
                        if(!server.getClickedCases()[x][y] && !disabled){
                            int caseValue = server.getChamp().getBoard()[x][y];
                            if(caseValue != -1)
                            {
                                score+=1;
                            }
                            else{
                                disabled = true;
                                server.getGuiServer().getLog().append(server.getDate() + " - " + PlayerName + " has lost!\n His score: " + score + " points\n");
                            }
                            server.getClickedCases()[x][y] = true;
                            server.caseWasClicked(x,y, this, caseValue);
                            server.checkEndGame();
                        }
                        break;
                    case "new":
                        if(inputArray[1].equals("true")) {
                            server.getGuiServer().getLog().append(server.getDate() + " - " + PlayerName + " wants to play again\n ");
                            wantsToReplay = true;
                        }
                        else {
                            hasLeft = true;
                            stopThread();
                        }
                        server.checkReplay();
                        break;
                    case "message":
                        if(inputArray.length>1){
                            StringBuilder message = new StringBuilder();
                            message.append("message ");
                            message.append(server.getDate()).append(" ");
                            message.append(playerColor.getRGB()).append(" ");
                            message.append(PlayerName).append(" ");
                            for(int i=1; i< inputArray.length; i++){
                                message.append(inputArray[i]).append(" ");
                            }
                            server.broadcastMessage(message.toString());
                        }
                        break;
                }
            }
        } catch (EOFException e) {
            hasLeft = true;
            stopThread();
        } catch (NullPointerException ignored){

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * This function resets the score and add it to the overall score
     */
    void resetScore(){
        overallScore =+ score;
        score = 0;
    }

    /**
     * This function stops the thread
     */
    private  void stopThread(){
        server.broadcastMessage("left" + " " + server.getDate() + " " + PlayerName);
        server.getGuiServer().getLog().append(server.getDate() + " - " + PlayerName + " has left the ship\n");
        try{
            in.close();
            out.close();
            sock.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This function returns the id of the client thread
     * @return
     */
    int getClientId() {return clientId;}

    /**
     * This funciton returns the name of the player associated to the thread
     * @return
     */
    String getPlayerName() {
        return PlayerName;
    }

    /**
     * THis function retuns the color of the player associated to the thread
     * @return
     */
    Color getPlayerColor(){
        return playerColor;
    }

    /**
     * This function returns the output stream of the thread
     * @return
     */
    DataOutputStream getOut(){return out;}

    /**
     * This function allow the player to be or not be enabling or disabling
     * @param disabled
     */
    void setDisabled(boolean disabled){this.disabled = disabled;}

    /**
     * This function return the score of the player for the round
     * @return
     */
    int getScore(){return score;}

    /**
     * This function return the overall score for the entire game
     * @return
     */
    int getOverallScore(){return  overallScore;}

    /**
     * This function returns if the player has lost and wants to replay
     * @return
     */
    boolean wantsToReplay(){return wantsToReplay;}

    /**
     * This function returns if the player as left the game
     * @return
     */
    boolean hasLeft(){return hasLeft;}

    /**
     * This function starts the game be enabling player
     */
    void startGame(){
        disabled = false;
        wantsToReplay = false;
    }
}