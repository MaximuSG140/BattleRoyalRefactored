package View;

import java.util.ArrayList;

public class FieldRenderParameters
{
    private ArrayList<PlayerRenderParameters> playerInfo = new ArrayList<>();
    private ArrayList<WeaponRenderParameters> weaponInfo = new ArrayList<>();

    public ArrayList<PlayerRenderParameters> getPlayerInfo()
    {
        return playerInfo;
    }

    public ArrayList<WeaponRenderParameters> getWeaponInfo()
    {
        return weaponInfo;
    }

    public void update(String[] args)
    {
        switch (args.length) {
            case 3:
                var weaponName = args[0];
                var weaponPositionX = args[1];
                var weaponPositionY = args[2];
                weaponInfo.add(new WeaponRenderParameters(weaponName, Integer.parseInt(weaponPositionX), Integer.parseInt(weaponPositionY)));
                return;
            case 4:
                var playerName = args[0];
                var playerPositionX = Integer.parseInt(args[1]);
                var playerPositionY = Integer.parseInt(args[2]);
                var playerHealth = Integer.parseInt(args[3]);
                playerInfo.add(new PlayerRenderParameters(playerName, playerPositionX, playerPositionY, playerHealth));
                return;
        }
    }

    public void registerPlayer(PlayerRenderParameters data)
    {
        playerInfo.add(data);
    }

    public void registerWeapon(WeaponRenderParameters data)
    {
        weaponInfo.add(data);
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for(var record : playerInfo)
        {
            result.append(record.toString());
        }
        for(var record : weaponInfo)
        {
            result.append(record.toString());
        }
        return result.toString();
    }
}
