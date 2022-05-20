package Exceptions;

public class PlayerDuplicateException extends Exception
{
    final String name;
    public PlayerDuplicateException(String notUniqueName)
    {
        name = notUniqueName;
    }
}
