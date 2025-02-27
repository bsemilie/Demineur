package game.demineur;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * This class is used to count time since a case was clciked on for the first time.
 * The counter is displayed in a JPanel and it is a thread.
 */
public class Counter extends JPanel implements Runnable {

    private Thread processScores; //Thread for counter
    private double cnt; //Time value of counter
    private boolean started = false; //Define if counter has started
    private NumberFormat nf = new DecimalFormat("0.##"); //Format to display counter


    /**
     * Constructor
     * @param width width for JPanel
     * @param height height for JPanel
     */
    Counter(int width, int height){
        setPreferredSize((new Dimension(width,height)));
        cnt = 0.00;
        processScores = new Thread(this);
    }

    /**
     * Function to start counter
     */
    void start(){
        if(!started){
            processScores.start();
            started = true;
        }
    }

    /**
     * Function to reset the counter
     */
    void reset() {
        started = false;
        cnt = 0.00;
        processScores = new Thread(this);
    }

    /**
     * Actions performed while running counter
     */
    @Override
    public void run() {
        while(processScores!=null)
        {
            try {
                Thread.sleep(10);
                cnt += 0.01;
                repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        processScores = new Thread(this);

    }

    /**
     * Stop the thread by setting its reference to null.
     */
    void stop() {
        processScores = null;
    }

    /**
     * This function returns the time on the counter using a format declared above
     * @return
     */
    String getTime() {
        return nf.format(cnt);
    }

    /**
     * Paints the JPanel with the counter
     * @param gc
     */
    @Override
    public void paintComponent(Graphics gc){
        super.paintComponent(gc);
        int xCoordinate = getWidth() / 4 ;
        int yCoordinate = getHeight() / 2;
        gc.drawString("Timer: " + nf.format(cnt), xCoordinate, yCoordinate);

    }
}
