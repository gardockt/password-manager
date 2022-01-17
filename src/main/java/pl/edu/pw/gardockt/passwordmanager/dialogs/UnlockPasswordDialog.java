package pl.edu.pw.gardockt.passwordmanager.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.EncryptionAlgorithm;
import pl.edu.pw.gardockt.passwordmanager.services.DatabaseService;
import pl.edu.pw.gardockt.passwordmanager.views.PasswordListView;

import javax.crypto.AEADBadTagException;

public class UnlockPasswordDialog extends Dialog {

    private final EncryptionAlgorithm encryptionAlgorithm;
    private final DatabaseService databaseService;

    private final Password password;

    private final PasswordField unlockPasswordField = new PasswordField();
    private final Button confirmButton = new Button(Strings.CONFIRM);
    private final Button cancelButton = new Button(Strings.CANCEL, e -> close());

    public UnlockPasswordDialog(SecurityConfiguration securityConfiguration, DatabaseService databaseService, Password password, PasswordListView passwordListView) {
        this.encryptionAlgorithm = securityConfiguration.getEncryptionAlgorithm();
        this.databaseService = databaseService;
        this.password = password;

        confirmButton.addClickListener(e -> new UnlockThread(getUI().orElseThrow(), this, passwordListView).start());

        confirmButton.setWidthFull();
        cancelButton.setWidthFull();

        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        confirmButton.addClickShortcut(Key.ENTER);
        cancelButton.addClickShortcut(Key.ESCAPE);

        unlockPasswordField.setWidthFull();

        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonLayout.setWidthFull();

        H3 title = new H3(password.getDescription());
        title.addClassName("mt-0");

        VerticalLayout layout = new VerticalLayout(
                title,
                new Label("Podaj hasło odblokowujące"),
                unlockPasswordField,
                buttonLayout
        );
        layout.setMinWidth("20em");
        add(layout);
    }

    private class UnlockThread extends Thread {

        private final UI ui;
        private final UnlockPasswordDialog unlockPasswordDialog;
        private final PasswordListView passwordListView;

        public UnlockThread(UI ui, UnlockPasswordDialog unlockPasswordDialog, PasswordListView passwordListView) {
            this.ui = ui;
            this.unlockPasswordDialog = unlockPasswordDialog;
            this.passwordListView = passwordListView;
        }

        @Override
        public void run() {
            try {
                String decryptedPassword = encryptionAlgorithm.decrypt(password.getPassword(), unlockPasswordField.getValue());

                // password unlocked successfully
                databaseService.updatePasswordLastAccess(password);
                Password unlockedPassword = password.clone();
                unlockedPassword.setPassword(decryptedPassword);
                ui.access(() -> {
                    new PasswordDialog(unlockedPassword).open();
                    passwordListView.refreshGrid();
                    unlockPasswordDialog.close();
                });
            } catch (AEADBadTagException e) {
                ui.access(() -> Notification.show(Strings.INCORRECT_UNLOCK_PASSWORD_ERROR));
            } catch (Exception e) {
                ui.access(() -> Notification.show(Strings.GENERIC_ERROR));
                e.printStackTrace();
            }
        }

    }

}
