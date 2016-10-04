package io.cify.framework.logging

import org.slf4j.Marker
import org.slf4j.MarkerFactory
import groovy.util.logging.Slf4j

/**
 * Created by FOB Solutions
 *
 * This is a class responsible for redirecting system out stream to logger
 */

@Slf4j
public class LoggingOutputStream extends OutputStream {

    private static final Marker MARKER = MarkerFactory.getMarker('LOGGING OUTPUT STREAM') as Marker
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
    private final LogLevel level;

    private LoggingOutputStream(LogLevel level) {
        this.level = level;
    }

    /**
     * Redirect system out stream to logger
     */
    public static void redirectSystemOutAndSystemErrToLogger() {
        System.setOut(new PrintStream(new LoggingOutputStream(LogLevel.INFO)));
        System.setErr(new PrintStream(new LoggingOutputStream(LogLevel.DEBUG)));
    }

    private static enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR,
    }

    @Override
    public void write(int b) {
        if ( b == '\n' ) {
            String line = baos.toString();
            baos.reset();

            switch (level) {
                case LogLevel.TRACE:
                    log.trace(MARKER, line);
                    break;
                case LogLevel.DEBUG:
                    log.debug(MARKER, line);
                    break;
                case LogLevel.ERROR:
                    log.error(MARKER, line);
                    break;
                case LogLevel.INFO:
                    log.info(MARKER, line);
                    break;
                case LogLevel.WARN:
                    log.warn(MARKER, line);
                    break;
            }
        } else {
            baos.write(b);
        }
    }

}