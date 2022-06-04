package game;

import javafx.scene.image.Image;

import java.util.HashMap;

import static game.menuController.CELL_SIZE_PIXEL;

public class ImageFactory
{
    static private final HashMap<String, Image> imageByName = new HashMap<>();
    static public Image getImage(String name)
    {
        if(!imageByName.containsKey(name))
        {
            imageByName.put(name, new Image(ImageFactory.class.getResource(name).toString(), CELL_SIZE_PIXEL, CELL_SIZE_PIXEL, false, false));
        }
        return imageByName.get(name);
    }
}
