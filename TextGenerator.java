import java.util.ArrayList;

public class TextGenerator {
    
    public static ArrayList<String> words = new ArrayList<String>();

    public static String generateRandomText(){
        String text = "";
        for(int i = 0; i < 25 ; i++){
            text += WordDatabase.getRandomWord() + " ";
        }
        updateWordList(text);
        return text;
    }

    private static void updateWordList(String str){
        words.clear();
        for(String s : str.split(" ")){
            words.add(s);
        }
    }

}
