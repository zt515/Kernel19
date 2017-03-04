package org.twpp.text.skin;


import android.graphics.Color;

public class LightSkin extends Skin {

    protected static final int JUNGLE_GREEN = Color.rgb(28, 166, 28);
    protected static final int LIGHT_GREY = 0xFFD3D3D3;
    protected static final int LIGHT_BLUE = Color.rgb(53, 137, 204);
    protected static final int OCEAN_BLUE = 0xFF256395;
    protected static final int OFF_BLACK = 0xFF040404;
    protected static final int OFF_WHITE = Color.WHITE;

    protected static Skin INSTANCE = new LightSkin();

    public LightSkin() {
        //前景
        setColor(Colorable.FOREGROUND, OFF_BLACK);
        //背景
        setColor(Colorable.BACKGROUND, OFF_WHITE);
        //选择部分前景
        setColor(Colorable.SELECTION_FOREGROUND, OFF_WHITE);
        //选择部分背景
        setColor(Colorable.SELECTION_BACKGROUND, OCEAN_BLUE);
        //光标前景
        setColor(Colorable.CURSOR_FOREGROUND, OFF_WHITE);
        //光标背景
        setColor(Colorable.CURSOR_BACKGROUND, OFF_BLACK);
        //未聚焦光标颜色
        setColor(Colorable.CURSOR_DISABLED, LIGHT_GREY);
        //行号颜色
        setColor(Colorable.LINE_HIGHLIGHT, Color.rgb(245, 245, 245));
        //注释颜色
        setColor(Colorable.COMMENT, JUNGLE_GREEN);
        //关键词颜色
        setColor(Colorable.KEYWORD, LIGHT_BLUE);
        //
        setColor(Colorable.SYMBOL, Color.rgb(193, 23, 23));
    }


    public static Skin getInstance() {
        return INSTANCE;
    }

}
