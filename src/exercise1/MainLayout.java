package exercise1;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
/**
 * Created with IntelliJ IDEA.
 * User: tomaszlelek
 * Date: 10/28/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainLayout extends JPanel
            implements ActionListener,
            PropertyChangeListener {

        private JProgressBar progressBar;
        private JButton startButton;
        private JButton stopButton;
        private JTextArea taskOutput;
        private int bound = 10;
        ProducerConsumer producerConsumer ;


        public MainLayout() {
            super(new BorderLayout());

            //Create the demo's UI.
            startButton = new JButton("Start");
            startButton.setActionCommand("start");
            startButton.addActionListener(this);
            stopButton = new JButton("Stop");
            stopButton.setActionCommand("stop");
            stopButton.setEnabled(false);
            stopButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    producerConsumer.cancel(true);
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                }
            });


            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);

            taskOutput = new JTextArea(5, 20);
            taskOutput.setMargin(new Insets(5,5,5,5));
            taskOutput.setEditable(false);

            JPanel panel = new JPanel();
            panel.add(startButton);
            panel.add(stopButton);
            panel.add(progressBar);

            add(panel, BorderLayout.PAGE_START);
            add(new JScrollPane(taskOutput), BorderLayout.CENTER);
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));



        }

        /**
         * Invoked when the user presses the start button.
         */
        public void actionPerformed(ActionEvent evt) {
            producerConsumer = new ProducerConsumer(1, 10, bound);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //Instances of javax.swing.SwingWorker are not reusuable, so
            //we create new instances as needed.
            producerConsumer.startProcess();
            producerConsumer.addPropertyChangeListener(this);
            producerConsumer.execute();
        }

        /**
         * Invoked when task's progress property changes.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress" == evt.getPropertyName()) {
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);
                taskOutput.append(String.format(
                        "Completed %d%% of task.\n", producerConsumer.getProgress()));
            }
        }


        /**
         * Create the GUI and show it. As with all GUI code, this must run
         * on the event-dispatching thread.
         */
        private static void createAndShowGUI() {
            //Create and set up the window.
            JFrame frame = new JFrame("ProgressBarDemo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Create and set up the content pane.
            JComponent newContentPane = new MainLayout();
            newContentPane.setOpaque(true); //content panes must be opaque
            frame.setContentPane(newContentPane);

            //Display the window.
            frame.pack();
            frame.setVisible(true);
        }

        public static void main(String[] args) {
            //Schedule a job for the event-dispatching thread:
            //creating and showing this application's GUI.
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
        }
    }


