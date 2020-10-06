package game.demineur;

import javax.print.DocFlavor;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 * This class is used to store each score in single-player mode in bestScores.csv file
 */
public class ScoreRegistering {



    private LinkedList<Score> scoreLinkedList= new LinkedList<Score>(); //List of scores
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); //format for date

    /**
     * Constructor
     * Create a linked list with all data inside a file called bestScores.csv
     */
    ScoreRegistering(){
        try{
            BufferedReader file = new BufferedReader(new FileReader("bestScores.csv"));
            String row;
            while((row = file.readLine()) != null){
                String[] data = row.split(";");
                this.scoreLinkedList.add(new Score(data[0], data[1], data[2]));
            }
        }catch(FileNotFoundException e){
            try{
                new BufferedWriter(new FileWriter(new File("bestScores.csv")));
            } catch (IOException exception){
                exception.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This function write inside file bestScores.csv all data contain in the linked list
     */
    void write(){
        try{
            BufferedWriter file = new BufferedWriter(new FileWriter(new File("bestScores.csv")));
            for(Score score: scoreLinkedList){
                file.write(score.getRating() + ";" + score.getDate() + ";" + score.getLevel() + '\n');
            }
            file.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * This function returns the list of score
     * @return
     */
    public LinkedList<Score> getScoreLinkedList() {
        return scoreLinkedList;
    }

    /**
     * This function returns the date format
     * @return
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }
}

/**
 * This class is used to store each score of a player
 */
class Score{
    private String rating; //Score of player which is the value of counter at the end of the game
    private String date; //Date where score was made
    private String level; //Level in which score was made

    /**
     * Constructor
     * @param rating score of the player, nb of cases discovered at end of game
     * @param date data and hour of end of game
     * @param level difficulty of game
     */
    Score(String rating, String date, String level){
        this.rating = rating;
        this.date = date;
        this.level = level;
    }

    /**
     * This function returns the score of the player in this instance
     * @return
     */
    String getRating(){return rating;}

    /**
     * THis function returns the date of this score instance
     * @return
     */
    String getDate(){return date;}

    /**
     * This function returns the level of this score instance
     * @return
     */
    String getLevel(){return level;}

}