package Network;

import java.io.IOException;

public interface IController
{
    void send(String line) throws IOException;
}
