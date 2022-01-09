package pl.edu.pw.gardockt.passwordmanager.dialogs;

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

import javax.crypto.AEADBadTagException;

public class UnlockPasswordDialog extends Dialog {

    EncryptionAlgorithm encryptionAlgorithm;

    private final Password password;

    private final PasswordField unlockPasswordField = new PasswordField();
    private final Button confirmButton = new Button(Strings.CONFIRM, e -> new UnlockThread(getUI().orElseThrow(), this).start());
    private final Button cancelButton = new Button(Strings.CANCEL, e -> close());

    public UnlockPasswordDialog(SecurityConfiguration securityConfiguration, Password password) {
        this.encryptionAlgorithm = securityConfiguration.getEncryptionAlgorithm();
        this.password = password;

        confirmButton.setWidthFull();
        cancelButton.setWidthFull();

        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

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

        public UnlockThread(UI ui, UnlockPasswordDialog unlockPasswordDialog) {
            this.ui = ui;
            this.unlockPasswordDialog = unlockPasswordDialog;
        }

        @Override
        public void run() {
            // TODO: count attempts (use PasswordVerifier?)
            try {
                String decryptedPassword = encryptionAlgorithm.decrypt(password.getPassword(), unlockPasswordField.getValue());
                Password unlockedPassword = password.clone();
                unlockedPassword.setPassword(decryptedPassword);
                ui.access(() -> {
                    new PasswordDialog(unlockedPassword).open();
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
