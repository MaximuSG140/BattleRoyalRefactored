package View;

import Model.Player;

import java.util.HashMap;

public class EndGameData
{
    private HashMap<String, Integer> playerResult = new HashMap<>();

    public void register(String name, int score)
    {
        playerResult.put(name, score);
    }

    public String[] toStrings()
    {
        String[] strings = new String[playerResult.size()];
        var nameSet = playerResult.keySet();
        var iterator = nameSet.iterator();
        for(int i = 1; i <= strings.length; ++i)
        {
            String name = iterator.next();
            strings[i - 1] = Integer.toString(i).concat(" ").concat(name).concat(" ").concat(playerResult.get(name).toString());
        }
        return strings;
    }

}
