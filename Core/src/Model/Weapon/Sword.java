package Model.Weapon;

import Model.Pawn;

public class Sword implements IWeapon
{

    @Override
    public void harm(Pawn victim)
    {
        victim.takeDamage(25);
    }
}
