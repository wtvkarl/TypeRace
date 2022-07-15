import javax.swing.JFrame;

public class App {

    private static final int width = 600, height = 400;

    public static void main(String[] args) throws Exception {
        WordDatabase.initializeDatabase();

        JFrame window = new JFrame("TypeRace");
        Screen screen = new Screen(width, height);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(screen);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        screen.startThread();
    }
}
