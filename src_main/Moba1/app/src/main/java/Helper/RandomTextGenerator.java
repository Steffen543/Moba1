package Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTextGenerator {

    private static String NUMBERS;

    static {
        NUMBERS = "1234567890";
    }

    public static String GenerateText(int length, boolean useLetters, boolean useNumbers, boolean useSpecialCharacters)
    {
        List<Character> list = new ArrayList<>();

        if(useLetters)
        {

            String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            for(Character c : LETTERS.toCharArray())
            {
                list.add(c);
            }
        }

        if(useNumbers)
        {
            for(Character c : NUMBERS.toCharArray())
            {
                list.add(c);
            }
        }

        if(useSpecialCharacters)
        {
            String SPECIAL_CHARACTERS = "!+~,.-#()[]?/";
            for(Character c : SPECIAL_CHARACTERS.toCharArray())
            {
                list.add(c);
            }
        }

        String returnValue = "";

        for(int i = 0; i <= length; i++)
        {
            Random rand = new Random();
            int  nextIndex = rand.nextInt(list.size() - 1) + 1;

            returnValue += list.get(nextIndex);
        }

        return returnValue;


    }

}
