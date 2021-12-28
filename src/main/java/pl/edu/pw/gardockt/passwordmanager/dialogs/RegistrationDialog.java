package pl.edu.pw.gardockt.passwordmanager.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.services.RegistrationService;

public class RegistrationDialog extends Dialog {

    private final RegistrationService registrationService;

    private final TextField usernameField = new TextField(Strings.USERNAME);
    private final PasswordField accountPasswordField = new PasswordField(Strings.ACCOUNT_PASSWORD);
    private final PasswordField repeatAccountPasswordField = new PasswordField(Strings.REPEAT_ACCOUNT_PASSWORD);
    private final PasswordField unlockPasswordField = new PasswordField(Strings.UNLOCK_PASSWORD);
    private final PasswordField repeatUnlockPasswordField = new PasswordField(Strings.REPEAT_UNLOCK_PASSWORD);
    private final Button registerButton = new Button(Strings.CONFIRM);
    private final Button cancelButton = new Button(Strings.CANCEL);

    private final String width = "20em";

    public RegistrationDialog(RegistrationService registrationService) {
        this.registrationService = registrationService;

        configureComponents();

        HorizontalLayout buttonLayout = new HorizontalLayout(registerButton, cancelButton);
        buttonLayout.add(registerButton, cancelButton);

        buttonLayout.setWidth(width);

        H2 title = new H2(Strings.REGISTER);
        title.addClassName("mt-0");

        VerticalLayout layout = new VerticalLayout(
                title,
                usernameField,
                accountPasswordField,
                repeatAccountPasswordField,
                unlockPasswordField,
                repeatUnlockPasswordField,
                buttonLayout
        );
        add(layout);
    }

    private void configureComponents() {
        usernameField.setWidth(width);
        accountPasswordField.setWidth(width);
        repeatAccountPasswordField.setWidth(width);
        unlockPasswordField.setWidth(width);
        repeatUnlockPasswordField.setWidth(width);

        usernameField.addClassName("py-s");
        accountPasswordField.addClassName("py-s");
        repeatAccountPasswordField.addClassName("py-s");
        unlockPasswordField.addClassName("py-s");
        repeatUnlockPasswordField.addClassName("py-s");

        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        registerButton.setWidthFull();
        cancelButton.setWidthFull();

        // TODO: add validation

        registerButton.addClickListener(e -> register());
        cancelButton.addClickListener(e -> close());
    }

    private void register() {
        // TODO: add validation

        try {
            registrationService.register(usernameField.getValue(), accountPasswordField.getValue(), unlockPasswordField.getValue());
            Notification.show("Zarejestrowano pomyślnie");
            close();
        } catch(Exception e) {
            Notification.show(Strings.ERROR_OCCURRED);
            e.printStackTrace();
        }
    }

}
