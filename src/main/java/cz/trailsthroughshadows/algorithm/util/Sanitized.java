package cz.trailsthroughshadows.algorithm.util;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.ParameterizedMessage;

@Log4j2
public class Sanitized {
    /**
     * Formats a message using the given format string and arguments.
     * <p>
     *     This method is a wrapper around {@link ParameterizedMessage#getFormattedMessage()}.
     *     It is used to format messages in a safe way, preventing log injection attacks.
     * </p>
     *
     * @param format the format string, using '{}' as a placeholder
     * @param args   the arguments to be substituted into the format string
     *
     * @return the formatted message
     */
    public static String format(String format, Object... args) {
        // if args are null or empty array, return the format string
        if (args == null || args.length == 0) {
            return format;
        }

        return new ParameterizedMessage(format, args).getFormattedMessage();
    }
}
