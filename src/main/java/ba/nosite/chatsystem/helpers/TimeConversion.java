package ba.nosite.chatsystem.helpers;

public class TimeConversion {
    public static Long convertToMs(Long toConvert) {
        return toConvert * 60 * 60 * 1000;
    }
}
