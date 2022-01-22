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
import pl.edu.pw.gardockt.passwordmanager.RegexCheck;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.security.CustomUserDetails;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.EncryptionAlgorithm;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.EncryptionPasswordGenerator;
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

    private boolean unlockInProgress = false;

    public UnlockPasswordDialog(SecurityConfiguration securityConfiguration, DatabaseService databaseService, Password password, PasswordListView passwordListView) {
        this.encryptionAlgorithm = securityConfiguration.getEncryptionAlgorithm();
        this.databaseService = databaseService;
        this.password = password;

        if(password == null ||
           (password.getDescription() == null || !RegexCheck.containsOnlyLegalCharacters(password.getDescription())) ||
           (password.getUsername() != null && !RegexCheck.containsOnlyLegalCharacters(password.getUsername())) ||
           (password.getPassword() == null || !RegexCheck.isBase64(password.getPassword()))) {
            throw new IllegalArgumentException();
        }

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        confirmButton.addClickListener(e -> {
            if(!unlockInProgress) {
                unlockInProgress = true;
                new UnlockThread(getUI().orElseThrow(), this, passwordListView, userDetails.getUsername()).start();
            }
        });

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
        private final String username;

        public UnlockThread(UI ui, UnlockPasswordDialog unlockPasswordDialog, PasswordListView passwordListView, String username) {
            this.ui = ui;
            this.unlockPasswordDialog = unlockPasswordDialog;
            this.passwordListView = passwordListView;
            this.username = username;
        }

        @Override
        public void run() {
            try {
				if(!RegexCheck.isValidPassword(unlockPasswordField.getValue())) {
					throw new AEADBadTagException("Entered password contains illegal characters");
				}

                String decryptedPassword = encryptionAlgorithm.decrypt(
                    password.getPassword(),
                    EncryptionPasswordGenerator.generate(unlockPasswordField.getValue(), username)
                );

                if(!RegexCheck.isValidStoredPassword(decryptedPassword)) {
                    throw new IllegalArgumentException("Decrypted password contains illegal characters");
                }

                // password unlocked successfully
                databaseService.updatePasswordLastAccess(password);
                Password unlockedPassword = password.clone();
                unlockedPassword.setPassword(decryptedPassword);
                ui.access(() -> {
                    try {
                        new PasswordDialog(unlockedPassword).open();
                    } catch (Exception e) {
                        Notification.show(Strings.GENERIC_ERROR);
                        e.printStackTrace();
                    }
                    passwordListView.refreshGrid();
                    unlockPasswordDialog.close();
                });
            } catch (AEADBadTagException e) {
                ui.access(() -> Notification.show(Strings.INCORRECT_UNLOCK_PASSWORD_ERROR));
            } catch (Exception e) {
                ui.access(() -> Notification.show(Strings.GENERIC_ERROR));
                e.printStackTrace();
            } finally {
                unlockInProgress = false;
            }
        }

    }

}
