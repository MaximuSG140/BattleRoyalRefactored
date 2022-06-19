package Util;

import java.util.Objects;

public class Position
{
    private int x;
    private int y;

    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Position left()
    {
        return new Position(x - 1, y);
    }

    public Position right()
    {
        return new Position(x + 1, y);
    }

    public Position up()
    {
        return new Position(x, y - 1);
    }

    public Position down()
    {
        return new Position(x, y + 1);
    }

    public void makeValid(int rightBorder, int bottomBorder)
    {
        if(x < 0)
        {
            x = 0;
        }
        if(y < 0)
        {
            y = 0;
        }
        if(x >= bottomBorder)
        {
            x = bottomBorder - 1;
        }
        if(y >= rightBorder)
        {
            y = rightBorder - 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public Position clone()
    {
        return new Position(x, y);
    }

    public <T>T getItemAt (T[][] field)
    {
        return field[y][x];
    }
    public <T>void setItemAt(T[][] field, T value)
    {
        field[y][x] = value;
    }

    public int calculateDistance(Position other)
    {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
}
