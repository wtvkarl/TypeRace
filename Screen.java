import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;

public class Screen extends JPanel implements Runnable, KeyListener{
    
    private int width, height;
    private boolean running;
    private Thread thread;

    private JTextField textField, timeElapsed, wpmDisplay;
    private JTextArea typeLabel, textToType;
    private Font mainFont;
    private Font headingFont;

    private String nextWord;
    private ArrayList<String> words;

    private Timer timer;
    private TimerTask task;

    private double secondsPassed = 0.000;
    private boolean timerStarted; 

    private int numWords;
    private boolean finished;

    public Screen(int w, int h){
        width = w;
        height = h;
        setPreferredSize(new Dimension(width, height));
        setDoubleBuffered(true);
        setLayout(null);
        addKeyListener(this);
        setFocusable(true);

        mainFont = new Font("Sans Serif", Font.BOLD, 20);
        headingFont = new Font("Sans Serif", Font.BOLD, 14);

        timeElapsed = new JTextField();
        timeElapsed.setEditable(false);
        timeElapsed.setHorizontalAlignment(JTextField.CENTER);
        timeElapsed.setSize(new Dimension(100, 20));
        timeElapsed.setLocation(150, 20);
        timeElapsed.setForeground(Color.white);
        timeElapsed.setBackground(Color.darkGray);
        timeElapsed.setFont(headingFont);
        add(timeElapsed);

        typeLabel = new JTextArea("Type to start!");
        typeLabel.setSize(new Dimension(100, 20));
        typeLabel.setLocation(100, 260);
        typeLabel.setForeground(Color.white);
        typeLabel.setBackground(Color.darkGray);
        typeLabel.setFont(headingFont);
        add(typeLabel);

        textField = new JTextField(20);
        textField.setEditable(false);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setSize(new Dimension(400,30));
        textField.setBackground(Color.red);
        textField.setForeground(Color.white);
        textField.setFont(mainFont);
        textField.setLocation(100, 300);

        textToType = new JTextArea(TextGenerator.generateRandomText());
        textToType.setWrapStyleWord(true);
        textToType.setLineWrap(true);
        textToType.setEditable(false);
        textToType.setSize(new Dimension(500, 200));
        textToType.setLocation(50, 50);
        textToType.setFont(mainFont);
        textToType.setForeground(Color.white);
        textToType.setBackground(Color.darkGray);
        add(textToType);

        //keylisteners must be added to ALL components it needs to connect to, not just the container.
        textField.addKeyListener(this);
        add(textField);

        wpmDisplay = new JTextField(20);
        wpmDisplay.setEditable(false);
        wpmDisplay.setHorizontalAlignment(JTextField.CENTER);
        wpmDisplay.setSize(new Dimension(150,20));
        wpmDisplay.setBackground(Color.darkGray);
        wpmDisplay.setForeground(Color.white);
        wpmDisplay.setFont(mainFont);
        wpmDisplay.setLocation(350, 20);
        add(wpmDisplay);

        words = TextGenerator.words;
        nextWord = words.get(0);
        numWords = words.size();

        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run(){
                timerStarted = true;
                secondsPassed += 0.001;
                updateTimerDisplay();
            }
        };
        timer.scheduleAtFixedRate(task, 3000, 1);
    }

    public void startThread(){
        running = true;
        thread = new Thread(this, "main");
        thread.start();
    }

    @Override
    public void run(){
        int targetUpdates = 60;
        double nsPerSecond = 1_000_000_000.0;
        double drawInterval = nsPerSecond/targetUpdates;
        double delta = 0;
        long then = System.nanoTime();
        long now;
        boolean shouldRender = false;
        
        while(running){
            now = System.nanoTime();
            delta += (now - then) / drawInterval;
            then = now;
            
            if(delta >= 1){
                update();
                delta--;
                shouldRender = true;
            }
            
            if(shouldRender){
                repaint();
                shouldRender = false;
            }
            
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.fillRect(0, 0, width, height);
        g2d.setFont(mainFont);
    }

    public void update(){
        if(textField.getText().equals("")){
            textField.setBackground(Color.darkGray);
        }
        if(timerStarted){
           textField.setEditable(true);
        }
        if(finished){
            textField.setText("Press R to reset.");
            timer.cancel();
        }
        else{
            textField.setText("");
        }
        
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == 32){
            //check if the textfield text equals next word
            if(correctWordEntered(textField.getText())){
                textField.setBackground(Color.darkGray);
                textField.setText("");
                words.remove(nextWord);
                updateTextArea(words);

                if(words.size() != 0){
                    nextWord = words.get(0);
                }
                else{
                    finished = true;
                    textToType.setBackground(Color.green);
                    updateWPM();
                }
            }
            else{
                textField.setBackground(Color.pink);
            }
            //update stats accordingly
            //clear textfield
        }
        if(e.getKeyCode() == 82 && finished){
            reset();
            finished = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    private boolean correctWordEntered(String text){
        text = text.replace(" ", "");
        return text.equals(nextWord);
    }

    private void updateTextArea(ArrayList<String> w){
        String str = "";
        for(String s : w){
            str += s + " ";
        }
        textToType.setText(str);
    }

    private void updateTimerDisplay(){
        timeElapsed.setText(String.format("%.3f", secondsPassed));
    }

    private void updateWPM(){
        double finalTime = secondsPassed;
        double wpm = (numWords * 60) / finalTime;
        wpmDisplay.setText(String.format("%.1f WPM", wpm));
    }

    private void reset(){
        secondsPassed = 0.0;
        wpmDisplay.setText("");
        textToType.setText(TextGenerator.generateRandomText());
        words = TextGenerator.words;
        nextWord = words.get(0);
        textField.setText("");
        textToType.setBackground(Color.darkGray);
        timeElapsed.setText("");
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run(){
                timerStarted = true;
                secondsPassed += 0.001;
                updateTimerDisplay();
            }
        };
        timer.scheduleAtFixedRate(task, 3000, 1);
    }

}
