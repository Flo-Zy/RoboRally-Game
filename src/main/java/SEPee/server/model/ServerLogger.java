package SEPee.server.model;

import SEPee.client.ClientLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.LogRecord;

public class ServerLogger {
    private static Logger logger = Logger.getLogger("ServerLogger");

    static {
        FileHandler fileHandler;
        try {
            // für unique logFile
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String clientLogFile = "ServerLogger_" + timestamp + ".log";

            fileHandler = new FileHandler(System.getProperty("user.dir") + System.getProperty("file.separator") + clientLogFile, true); // um log files anzufügen
            logger.addHandler(fileHandler);

            // Setze benutzerdefinierten Formatter
            fileHandler.setFormatter(new CustomFormatter());

            logger.info("ServerLogger initialized");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception :: ", e);
        }
    }

    static class CustomFormatter extends SimpleFormatter {
        @Override
        public String format(LogRecord record) {
            // nur die Nachricht, kein INFO-Präfix
            return record.getMessage() + System.lineSeparator();
        }
    }

    public static void writeToServerLog(Object object){
        logger.info(String.valueOf(object));
    }

    public static void writeToServerLog(String message, Exception e){
        logger.log(Level.WARNING, message, e);
    }
}
