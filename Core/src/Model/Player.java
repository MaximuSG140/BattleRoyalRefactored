package Model;


public class Player
{
    private final String name;
    private Pawn controlledPawn = null;
    private int score = 0;

    public Player(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Pawn getControlledPawn()
    {
        return controlledPawn;
    }

    public int getScore()
    {
        return score;
    }

    public void attachPawn(Pawn p)
    {
        controlledPawn = p;
        p.attachToPlayer(this);
    }

    public void removePawn()
    {
        controlledPawn = null;
    }

    public void scoreKill()
    {
        score += 1;
    }

    public boolean hasPawn()
    {
        return controlledPawn != null;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
