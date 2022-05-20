package View;

public class WeaponRenderParameters
{
    public String type;
    public int x;
    public int y;
    public WeaponRenderParameters(String name, int x, int y)
    {
        type = name;
        this.x = x;
        this.y = y;
    }

    public String toString()
    {
        StringBuilder currentLine = new StringBuilder(type);
        currentLine.append(' ');
        currentLine.append(x);
        currentLine.append(' ');
        currentLine.append(y);
        currentLine.append('\n');
        return currentLine.toString();
    }
}
