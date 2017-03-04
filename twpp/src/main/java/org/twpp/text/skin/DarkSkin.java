package org.twpp.text.skin;

public class DarkSkin extends LightSkin {

    static DarkSkin INSTANCE = new DarkSkin();

    public DarkSkin() {
        super();
        setColor(Colorable.BACKGROUND,OFF_BLACK);
        setColor(Colorable.FOREGROUND,OFF_WHITE);
        setColor(Colorable.CURSOR_BACKGROUND,OFF_WHITE);
    }

    public static DarkSkin getInstance() {
        return INSTANCE;
    }
}
