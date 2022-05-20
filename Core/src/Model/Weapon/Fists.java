package Model.Weapon;

import Model.Pawn;

public class Fists implements IWeapon
{
    @Override
    public void harm(Pawn victim)
    {
        victim.takeDamage(5);
    }
}
