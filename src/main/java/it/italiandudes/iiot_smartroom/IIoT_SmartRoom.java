package it.italiandudes.iiot_smartroom;

import it.italiandudes.iiot_smartroom.utils.Defs;
import it.italiandudes.idl.logger.InfoFlags;
import it.italiandudes.idl.logger.Logger;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class IIoT_SmartRoom {

    // Main Method
    public static void main(String[] args) {

        // Setting Charset to UTF-8
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        // Initializing the logger
        try {
            Logger.init();
            Logger.log("Logger initialized!", Defs.LOGGER_CONTEXT);
        } catch (IOException e) {
            Logger.log("An error has occurred during Logger initialization, exit...", Defs.LOGGER_CONTEXT);
            return;
        }

        // Configure the shutdown hooks
        Logger.log("Configuring Shutdown Hooks...", Defs.LOGGER_CONTEXT);
        Runtime.getRuntime().addShutdownHook(new Thread(Logger::close));
        Logger.log("Shutdown Hooks configured!", Defs.LOGGER_CONTEXT);

        // Check Java Version
        Logger.log("Verifying for Java 21...", Defs.LOGGER_CONTEXT);
        if (!SystemUtils.IS_JAVA_21) {
            Logger.log("The current java version is wrong, to run this jar you need Java 21!", new InfoFlags(true, true, true, true), Defs.LOGGER_CONTEXT);
            return;
        }
        Logger.log("Java 21 Verified!", Defs.LOGGER_CONTEXT);

        // Check OS
        Logger.log("Verifying OS...", Defs.LOGGER_CONTEXT);
        Logger.log("OS Name: " + SystemUtils.OS_NAME, Defs.LOGGER_CONTEXT);
        Logger.log("OS Arch: " + SystemUtils.OS_ARCH, Defs.LOGGER_CONTEXT);

        // App Entry Point
    }
}
