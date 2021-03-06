package Model;

import Model.Weapon.IWeapon;
import Model.Weapon.Knife;
import Model.Weapon.Sword;
import Util.Position;
import View.FieldRenderParameters;
import View.PlayerRenderParameters;
import View.WeaponRenderParameters;

import java.util.HashMap;
import java.util.Random;

public class Field
{
    public static final int FIELD_SIZE = 20;
    public static final int MAX_WEAPON_COUNT = 8;
    private int weaponCount = 0;
    private final HashMap<Pawn, Position> pawnPosition = new HashMap<>();
    private final IWeapon[][] cellsDrop = new IWeapon[FIELD_SIZE][FIELD_SIZE];

    public void movePawn(Pawn pawn, Direction d)
    {
        synchronized (pawnPosition)
        {
            var pawnPosition = this.pawnPosition.get(pawn);
            Position newPosition;
            Pawn attackedPawn;
            switch (d)
            {
                case UP:
                    newPosition = pawnPosition.up();
                    attackedPawn = checkoutCell(newPosition);
                    if(attackedPawn != null)
                    {
                        pawn.attack(attackedPawn);
                        break;
                    }
                    pawnPosition = newPosition;
                    pawnPosition.makeValid(FIELD_SIZE, FIELD_SIZE);
                    break;
                case DOWN:
                    newPosition = pawnPosition.down();
                    attackedPawn = checkoutCell(newPosition);
                    if(attackedPawn != null)
                    {
                        pawn.attack(attackedPawn);
                        break;
                    }
                    pawnPosition = newPosition;
                    pawnPosition.makeValid(FIELD_SIZE, FIELD_SIZE);
                    break;
                case LEFT:
                    newPosition = pawnPosition.left();
                    attackedPawn = checkoutCell(newPosition);
                    if(attackedPawn != null)
                    {
                        pawn.attack(attackedPawn);
                        break;
                    }
                    pawnPosition = newPosition;
                    pawnPosition.makeValid(FIELD_SIZE, FIELD_SIZE);
                    break;
                case RIGHT:
                    newPosition = pawnPosition.right();
                    attackedPawn = checkoutCell(newPosition);
                    if(attackedPawn != null)
                    {
                        pawn.attack(attackedPawn);
                        break;
                    }
                    pawnPosition = newPosition;
                    pawnPosition.makeValid(FIELD_SIZE, FIELD_SIZE);
                    break;
            }
            this.pawnPosition.put(pawn, pawnPosition);
        }
    }

    public void placePawn(Pawn p, Position position)
    {
        synchronized (pawnPosition)
        {
            pawnPosition.put(p, position);
        }
    }

    public void removePawn(Pawn p)
    {
        var droppedArm = p.getWeapon();
        synchronized (pawnPosition)
        {
            var position = pawnPosition.get(p);
            synchronized (cellsDrop)
            {
                if(position.getItemAt(cellsDrop) == null)
                {
                    position.setItemAt(cellsDrop, droppedArm);
                }
            }
            pawnPosition.remove(p);
        }
    }

    public void pickWeapon(Pawn p)
    {
        var droppedArm = p.getWeapon();
        synchronized (pawnPosition)
        {
            var position = pawnPosition.get(p);
            synchronized (cellsDrop)
            {
                p.pickUpWeapon(position.getItemAt(cellsDrop));
                position.setItemAt(cellsDrop, droppedArm);
            }
        }
    }

    public void generateWeapon(Random rng)
    {
        if(weaponCount >= MAX_WEAPON_COUNT)
        {
            return;
        }
        int x = rng.nextInt(FIELD_SIZE);
        int y = rng.nextInt(FIELD_SIZE);
        synchronized (cellsDrop)
        {
            if(cellsDrop[y][x] == null)
            {
                cellsDrop[y][x] = rng.nextBoolean() ? new Knife() : new Sword();
                weaponCount++;
            }
        }
    }

    public FieldRenderParameters getRenderInfo()
    {
        var info = new FieldRenderParameters();
        for(int y = 0; y < FIELD_SIZE; ++y)
        {
            for(int x = 0; x < FIELD_SIZE; ++x)
            {
                synchronized (cellsDrop)
                {
                    if(cellsDrop[y][x] != null)
                    {
                        info.registerWeapon(new WeaponRenderParameters(cellsDrop[y][x].getClass().getName(), x, y));
                    }
                }
            }
        }
        synchronized (pawnPosition)
        {
            for (var pawn : pawnPosition.keySet())
            {
                info.registerPlayer(new PlayerRenderParameters(pawn.getController().getName(),
                        pawnPosition.get(pawn).getX(),
                        pawnPosition.get(pawn).getY(),
                        pawn.getHealthPoints()));
            }
        }
        return info;
    }

    public Pawn checkoutCell(Position position)
    {
        synchronized (pawnPosition)
        {
            var pawns = pawnPosition.keySet();
            var iterator = pawns.iterator();
            while(iterator.hasNext())
            {
                var pawn = iterator.next();
                if(pawnPosition.get(pawn).equals(position))
                {
                    return pawn;
                }
            }
        }
        return null;
    }
}
