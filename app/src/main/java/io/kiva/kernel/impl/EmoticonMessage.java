package io.kiva.kernel.impl;
import io.kiva.kernel.R;
import io.kiva.kernel.model.MessageData;
import io.kiva.kernel.model.MessageFrom;
import io.kiva.kernel.model.MessageType;

@SuppressWarnings("unused")
public class EmoticonMessage extends BaseMessage
{
    private static final int[] EMOTICONS = {
            R.drawable.emoji_lovely,
            R.drawable.emoji_pick_up_b,
            R.drawable.emoji_mad,
            R.drawable.emoji_smile,
            R.drawable.emoji_shining_closed_eyes,
            R.drawable.emoji_look_at_you_2,
            R.drawable.emoji_surprised,
            R.drawable.emoji_sad_cry,
            R.drawable.emoji_cat_lovely,
            R.drawable.emoji_happy,
            R.drawable.emoji_unaccepted,
            R.drawable.emoji_shining,
            R.drawable.emoji_hug_flower,
            R.drawable.emoji_hug,
            R.drawable.emoji_eat_lollipop,
            R.drawable.emoji_jeer,
            R.drawable.emoji_shy,
            R.drawable.emoji_no_words,
            R.drawable.emoji_funny_corner,
            R.drawable.emoji_sleep,
            R.drawable.emoji_hug_arms_open,
            R.drawable.emoji_king_sigh,
            R.drawable.emoji_white_eyes,
            R.drawable.emoji_hug_2,
            R.drawable.emoji_look_at_you,
            R.drawable.emoji_get_pierced,
            R.drawable.emoji_scared_suddenly,
            R.drawable.emoji_eat_pill,
            R.drawable.emoji_eat_noodles,
            R.drawable.emoji_funny_no_words,
            R.drawable.emoji_unsatisfied,
            R.drawable.emoji_confused,
            R.drawable.emoji_stare,
            R.drawable.emoji_eat_watermelon,
            R.drawable.emoji_zombie,
            R.drawable.emoji_embarrassed_shy,
    };

    public static final int EMOJI_LOVELY = 0;
    public static final int EMOJI_PICK_UP_B = 1;
    public static final int EMOJI_MAD = 2;
    public static final int EMOJI_SMILE = 3;
    public static final int EMOJI_SHINING_CLOSED_EYES = 4;
    public static final int EMOJI_LOOK_AT_YOU_2 = 5;
    public static final int EMOJI_SURPRISED = 6;
    public static final int EMOJI_SAD_CRY = 7;
    public static final int EMOJI_CAT_LOVELY = 8;
    public static final int EMOJI_HAPPY = 9;
    public static final int EMOJI_UNACCEPTED = 10;
    public static final int EMOJI_SHINING = 11;
    public static final int EMOJI_HUG_FLOWER = 12;
    public static final int EMOJI_HUG = 13;
    public static final int EMOJI_EAT_LOLLIPOP = 14;
    public static final int EMOJI_JEER = 15;
    public static final int EMOJI_SHY = 16;
    public static final int EMOJI_NO_WORDS = 17;
    public static final int EMOJI_FUNNY_CORNER = 18;
    public static final int EMOJI_SLEEP = 19;
    public static final int EMOJI_HUG_ARMS_OPEN = 20;
    public static final int EMOJI_KING_SIGH = 21;
    public static final int EMOJI_WHITE_EYES = 22;
    public static final int EMOJI_HUG_2 = 23;
    public static final int EMOJI_LOOK_AT_YOU = 24;
    public static final int EMOJI_GET_PIERCED = 25;
    public static final int EMOJI_SCARED_SUDDENLY = 26;
    public static final int EMOJI_EAT_PILL = 27;
    public static final int EMOJI_EAT_NOODLES = 28;
    public static final int EMOJI_FUNNY_NO_WORDS = 29;
    public static final int EMOJI_UNSATISFIED = 30;
    public static final int EMOJI_CONFUSED = 31;
    public static final int EMOJI_STARE = 32;
    public static final int EMOJI_EAT_WATERMELON = 33;
    public static final int EMOJI_ZOMBIE = 34;
    public static final int EMOJI_EMBARRASSED_SHY = 35;

    private EmoticonMessageData data;

    public EmoticonMessage(MessageFrom from)
    {
        super(MessageType.TYPE_EMOTICON, from);
    }

    public EmoticonMessage(MessageFrom from, EmoticonMessageData emoticonMessageData) {
        this(from);
        setData(emoticonMessageData);
    }

    public void setData(EmoticonMessageData data)
    {
        this.data = data;
    }

    @Override
    public MessageType getType()
    {
        // TODO: Implement this method
        return MessageType.TYPE_EMOTICON;
    }

    @Override
    public <T extends MessageData> T getData()
    {
        // TODO: Implement this method
        return (T) data;
    }

    public static int getEmoji(int index) {
        if (index >= EMOTICONS.length) {
            return 0;
        }
        return EMOTICONS[index];
    }

    public static int getEmojiCount() {
        return EMOTICONS.length;
    }
}
