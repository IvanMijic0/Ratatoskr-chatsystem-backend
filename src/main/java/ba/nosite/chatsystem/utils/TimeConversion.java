package ba.nosite.chatsystem.utils;

public class TimeConversion {
    public static Long convertHourToMs(Long toConvert) {
        if (toConvert != null) {
            return toConvert * 60 * 60 * 1000;
        } else {
            throw new IllegalArgumentException("toConvert cannot be null");
        }
    }
}
