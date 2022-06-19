package Network;

import View.FieldRenderParameters;

public interface IView
{
    FieldRenderParameters getFieldInfo();
    boolean gameRunning();
}
