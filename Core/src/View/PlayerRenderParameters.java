package View;

import Model.Player;

public class PlayerRenderParameters
{
    public String name;
    public int x;
    public int y;
    public int hp;
    public PlayerRenderParameters(String name, int x, int y, int hp)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.hp = hp;
    }
    public String toString()
    {
        StringBuilder currentLine = new StringBuilder(name);
        currentLine.append(' ');
        currentLine.append(x);
        currentLine.append(' ');
        currentLine.append(y);
        currentLine.append(' ');
        currentLine.append(hp);
        currentLine.append('\n');
        return currentLine.toString();
    }
}
