import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class testjava extends JFrame{
    public testjava(){

        setLayout(new FlowLayout(FlowLayout.LEFT,10,20));
        this.add(new JLabel("First Name"));
        add(new JTextField(8));
    }

    public static void main(String[] args) {
        testjava frame = new testjava();
        frame.setTitle("ShowFlowLayout");
        frame.setLocationRelativeTo(null);// New since JDK 1.4
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200,200);
        frame.setVisible(true);
    }
}