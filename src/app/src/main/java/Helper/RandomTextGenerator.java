package Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Steffen on 25.06.2017.
 */

public class RandomTextGenerator {

    public static String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static String NUMBERS = "1234567890";
    public static String SPECIAL_CHARACTERS = "!+~,.-#()[]?/";

    public static String GenerateText(int length, boolean useLetters, boolean useNumbers, boolean useSpecialCharacters)
    {
        List<Character> list = new ArrayList<Character>();

        if(useLetters)
        {

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
