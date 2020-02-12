package schulscheduler.main;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import schulscheduler.i18n.Messages;
import schulscheduler.ui.JavaFXHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fängt alle Exceptions ab, die nicht behandelt wurden, und zeigt dem Nutzer in einem Dialog die Fehlermeldung.
 */
public final class GlobalExceptionHandler implements UncaughtExceptionHandler {

    /**
     * Dateiendung für Autosave-Dateien.
     */
    public static final String AUTOSAVE_FILE_EXTENSION = ".crash.autosave";

    /**
     * Die maximale Anzahl an Fehlermeldungs-Dialogen, die geöffnet werden dürfen. Alle weiteren Fehlermeldungen werden
     * einfach ignoriert.
     */
    private static final int MAX_DIALOG_COUNT = 3;

    private static UncaughtExceptionHandler defaultHandler;
    private static GlobalExceptionHandler instance;
    private static AtomicInteger openDialogsCount = new AtomicInteger();

    /**
     * Aktiviert die globale Behandlung von Exceptions.
     */
    public static void initialize() {
        if (instance != null) {
            throw new IllegalStateException("Initialized already!");
        }
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        instance = new GlobalExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(instance);
    }

    /**
     * Mit dieser Methode können andere Programmteile eine Exception direkt an den Handler weitergeben.
     *
     * @param e Die Exception.
     */
    public static void handleException(Throwable e) {
        instance.uncaughtException(Thread.currentThread(), e);
    }

    /**
     * Singleton-Pattern.
     */
    private GlobalExceptionHandler() {
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {

            // TODO: #212 Entfernen, wenn Bug behoben.
            // https://javafx-jira.kenai.com/browse/RT-35338
            // Das ist ein bekannter Bug. Die Exception kann man einfach ignorieren.
            if (e instanceof NullPointerException) {
                StackTraceElement[] stackTrace = e.getStackTrace();
                if (stackTrace.length > 0 && stackTrace[0].getMethodName().equals("handleSelectedCellsListChangeEvent")) {
                    return;
                }
            }

            if (openDialogsCount.get() >= MAX_DIALOG_COUNT) {
                return;
            }

            boolean autoSaved = autoSaveEverything();
            showDialogFor(e, autoSaved);

        } catch (Exception innerException) {
            innerException.printStackTrace();
            if (defaultHandler == null) {
                throw innerException;
            } else {
                defaultHandler.uncaughtException(t, new RuntimeException("Error " + innerException + " when handling:", e));
            }
        }
    }

    /**
     * Zeigt den Dialog zur Behandlung einer Exception an.<br>
     * Um möglichst wenig andere Abhängigkeiten zu haben, wird der Dialog nicht aus FXML geladen, sondern direkt per
     * Java-Code erzeugt.
     *
     * @param e         Die Exception.
     * @param autoSaved Gibt an, ob es gelungen ist, die offenen Dateien zu speichern.
     */
    private static void showDialogFor(Throwable e, boolean autoSaved) {
        if (openDialogsCount.get() >= MAX_DIALOG_COUNT) {
            return;
        }
        Platform.runLater(() -> {
            Messages messages = Messages.getInstance();

            final Stage dialog = new Stage();
            dialog.getIcons().add(JavaFXHelper.WINDOW_ICON);
            dialog.setTitle(messages.getString("exception_dialog_title"));

            String topMessage = messages.getString("exception_dialog_top_text");
            if (autoSaved) {
                topMessage += " ";
                topMessage += messages.getString("exception_dialog_autosaved_message");
            }
            Label topText = new Label(topMessage);
            topText.setPrefWidth(800);
            topText.setWrapText(true);

            String message = exceptionToString(e);
            TextArea errorMessage = new TextArea(message);
            errorMessage.setEditable(false);
            errorMessage.setFont(Font.font("monospaced"));
            errorMessage.setPrefHeight(500);

            Button btnContinue = new Button(messages.getString("exception_dialog_continue"));
            btnContinue.setDefaultButton(true);
            btnContinue.setOnAction(event -> dialog.close());

            Button btnExit = new Button(messages.getString("exception_dialog_program_exit"));
            btnExit.setOnAction(event -> System.exit(1));

            HBox buttonBox = new HBox(3);
            buttonBox.setAlignment(Pos.BASELINE_RIGHT);
            buttonBox.getChildren().setAll(btnExit, btnContinue);

            VBox vbox = new VBox(5);
            vbox.setPadding(new Insets(10));
            vbox.getChildren().setAll(topText, errorMessage, buttonBox);
            VBox.setVgrow(errorMessage, Priority.ALWAYS);
            dialog.setScene(new Scene(vbox));

            dialog.setOnHidden(event -> openDialogsCount.decrementAndGet());
            if (openDialogsCount.get() >= MAX_DIALOG_COUNT) {
                return;
            }
            openDialogsCount.incrementAndGet();
            dialog.show();
        });
    }

    /**
     * Versucht, für alle offenen Dateien eine Sicherung anzulegen.
     *
     * @return True, wenn das für alle offenen Dateien geklappt hat.
     */
    private static boolean autoSaveEverything() {
        // TODO
//        try {
//            File autoSaveFolder = CDIUtils.lookup(WorkspaceManager.class).getAutoSaveFolder();
//            for (WindowInfo<?> windowInfo : CDIUtils.lookup(WindowManager.class).getOpenWindows()) {
//                File autoSaveFile;
//                int counter = 0;
//                do {
//                    counter++;
//                    if (windowInfo.getFile() != null) {
//                        autoSaveFile = new File(autoSaveFolder, windowInfo.getFile().getName() + "." + counter + AUTOSAVE_FILE_EXTENSION);
//                    } else {
//                        autoSaveFile = new File(autoSaveFolder, windowInfo.getDefaultFileName() + windowInfo.getFileExtension() + "." + counter + AUTOSAVE_FILE_EXTENSION);
//                    }
//                } while (autoSaveFile.exists());
//
//                Serialization.xmlWriteFile(windowInfo.getModel(), autoSaveFile);
//            }
//
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
        return true;
    }

    /**
     * @param e Die Exception.
     * @return Eine Darstellung der Exception, die die Fehlermeldung und den gesamten Stack-Trace enthält.
     */
    private static String exceptionToString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        internalExceptionToString(e, printWriter);
        return stringWriter.toString();
    }

    /**
     * @param e      Die Exception.
     * @param writer Der PrintWriter über den die Exception ausgegeben wird.
     */
    private static void internalExceptionToString(Throwable e, PrintWriter writer) {
        e.printStackTrace(writer);
        if (e.getCause() != null) {
            writer.println();
            writer.write("Caused by: ");
            internalExceptionToString(e.getCause(), writer);
        }
    }
}
