import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyActionListener implements ActionListener {
    public JButton butReset = new JButton("Reset");
    JButton butQuit = new JButton("Quit");
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == butQuit)
        {
            System.exit(0);
        }
        else if(source == butReset)
        {

        }
    }
}
