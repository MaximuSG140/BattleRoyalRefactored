package Network;

import java.io.IOException;
import java.net.Socket;

public class PlayerController implements IController
{
    private Socket currentServer;
    private String player;

    public PlayerController(Socket server, String player)
    {
        this.currentServer = server;
        this.player = player;
    }

    @Override
    public void send(String line) throws IOException
    {
        currentServer.getOutputStream().write(player.getBytes());
        currentServer.getOutputStream().write(' ');
        currentServer.getOutputStream().write(line.getBytes());
        currentServer.getOutputStream().flush();
    }
}
