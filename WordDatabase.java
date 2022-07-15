import java.io.FileInputStream;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class WordDatabase {
    
    public static String[] words; 

    public static void initializeDatabase(){
        words = new String[1000];
        try{
            FileInputStream reader = new FileInputStream(new File("res/words/1000mostcommonwords.txt"));
            Scanner scanner = new Scanner(reader);

            int index = 0;
            while(scanner.hasNextLine()){
                String text = scanner.nextLine();
                words[index] = filterText(text);
                index++;
            }

            scanner.close();            
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private static String filterText(String text){
        String str = "";
        char[] letters = text.toCharArray();
        for(char c : letters){
            if(Character.isLetter(c)){
                str += c;
            }
        }
        return str;
    }

    public static String getRandomWord(){
        int index = (int)(Math.random() * words.length);
        return words[index];
    }

}
