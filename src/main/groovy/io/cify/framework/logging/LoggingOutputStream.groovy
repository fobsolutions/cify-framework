package io.cify.framework.logging

import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.LogManager

/**
 * Created by FOB Solutions
 *
 * This is a class responsible for redirecting system out stream to logger
 */

public class LoggingOutputStream extends OutputStream {

    private static final Logger LOG = LogManager.getLogger(this.class) as Logger
    private static final Marker MARKER = MarkerManager.getMarker('LOGGING OUTPUT STREAM') as Marker

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
                    LOG.trace(MARKER, line);
                    break;
                case LogLevel.DEBUG:
                    LOG.debug(MARKER, line);
                    break;
                case LogLevel.ERROR:
                    LOG.error(MARKER, line);
                    break;
                case LogLevel.INFO:
                    LOG.info(MARKER, line);
                    break;
                case LogLevel.WARN:
                    LOG.warn(MARKER, line);
                    break;
            }
        } else {
            baos.write(b);
        }
    }

}