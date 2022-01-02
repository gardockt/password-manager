package pl.edu.pw.gardockt.passwordmanager.dialogs;

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
    private final Button confirmButton = new Button(Strings.CONFIRM, e -> unlock());
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

    private void unlock() {
        // TODO: count attempts
        try {
            String decryptedPassword = encryptionAlgorithm.decrypt(password.getPassword(), unlockPasswordField.getValue());
            Password unlockedPassword = password.clone();
            unlockedPassword.setPassword(decryptedPassword);
            new PasswordDialog(unlockedPassword).open();
            close();
        } catch (AEADBadTagException e) {
            Notification.show(Strings.INCORRECT_PASSWORD_ERROR);
        } catch (Exception e) {
            Notification.show(Strings.GENERIC_ERROR);
            e.printStackTrace();
        }
    }

}
