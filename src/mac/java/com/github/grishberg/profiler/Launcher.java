package com.github.grishberg.profiler;

import com.github.grishberg.profiler.common.AppLogger;
import com.github.grishberg.profiler.common.SimpleConsoleLogger;
import com.github.grishberg.profiler.common.settings.JsonSettings;
import com.github.grishberg.profiler.common.settings.SettingsRepository;
import com.github.grishberg.profiler.ui.FramesManager;
import com.github.grishberg.profiler.ui.Main;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.List;

import static com.github.grishberg.profiler.ui.Main.*;

public class Launcher {
    @Nullable
    private static File sPendingFile = null;
    private static final SimpleConsoleLogger log = new SimpleConsoleLogger(APP_FILES_DIR_NAME);
    private static final JsonSettings settings = new JsonSettings(APP_FILES_DIR_NAME, log);
    private static boolean sMainWidowStarted = false;
    private static final FramesManager sFramesManager = new FramesManager();

    static {
        initDefaultSettings(settings, log);

        String osName = System.getProperty("os.name");
        log.i("Current OS: " + osName);
        if (osName.contains("OS X")) {
            log.i("Setup mac open file handler");
            setupMacOpenFileHandler(log, settings);
        }
    }

    public static void main(String[] args) {
        Main app = sFramesManager.createMainFrame(Main.StartMode.DEFAULT, settings, log);
        sMainWidowStarted = true;
        if (sPendingFile != null) {
            app.openTraceFile(sPendingFile);
            sPendingFile = null;
        }

        if (args.length > 0) {
            File f = new File(args[0]);
            if (f.isFile()) {
                app.openTraceFile(f);
            }
        }
    }

    private static void setupMacOpenFileHandler(SimpleConsoleLogger log, JsonSettings settings) {
        log.d("setupMacOpenFileHandler");

        Desktop.getDesktop().setOpenFileHandler(ofe -> {
            log.d("setupMacOpenFileHandler: openFiles: ofe = " + ofe);
            List<File> files = ofe.getFiles();
            if (files != null && files.size() > 0) {
                File file = files.get(0);
                log.d("setupMacOpenFileHandler: openFiles: " + file.getPath());
                if (!sMainWidowStarted) {
                    sPendingFile = file;
                    log.d("setupMacOpenFileHandler: pending file: " + file.getPath());
                    return;
                }
                Main newWindow = sFramesManager.createMainFrame(Main.StartMode.DEFAULT, settings, log);
                newWindow.openTraceFile(file);
            }
        });
    }

    private static void initDefaultSettings(SettingsRepository settings, AppLogger log) {
        String androidHome = System.getenv("ANDROID_HOME");
        log.i("ANDROID_HOME = " + androidHome);
        if (androidHome != null) {
            settings.setStringValue(SETTINGS_ANDROID_HOME, androidHome);
        }

        settings.getBoolValueOrDefault(SETTINGS_SHOW_BOOKMARKS,
                settings.getBoolValueOrDefault(SETTINGS_SHOW_BOOKMARKS, true));
    }
}
