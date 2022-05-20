package Model;

import Model.Weapon.Fists;
import Model.Weapon.IWeapon;

public class Pawn
{
    private int healthPoints;
    private IWeapon arm = new Fists();
    private Player controller = null;

    public Pawn(int hp)
    {
        healthPoints = hp;
    }

    public void attachToPlayer(Player p)
    {
        controller = p;
    }

    public void takeDamage(int damage)
    {
        healthPoints -= damage;
    }

    public boolean isAlive()
    {
        return healthPoints > 0;
    }

    public void attack(Pawn victim)
    {
        if(!victim.isAlive())
        {
            return;
        }
        arm.harm(victim);
        if(!victim.isAlive())
        {
            controller.scoreKill();
        }
    }

    public void pickUpWeapon(IWeapon weapon)
    {
        if(weapon == null)
        {
            arm = new Fists();
        }
        else
        {
            arm = weapon;
        }
    }

    public IWeapon getWeapon()
    {
        if(arm instanceof Fists)
        {
            return null;
        }
        return arm;
    }

    public Player getController()
    {
        return controller;
    }

    public int getHealthPoints()
    {
        return healthPoints;
    }
}
