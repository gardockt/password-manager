package pl.edu.pw.gardockt.passwordmanager.dialogs;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.gardockt.passwordmanager.StringGenerator;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.security.CustomUserDetails;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.AES256GCMEncryptionAlgorithm;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.EncryptionAlgorithm;

public class AddPasswordDialog extends Dialog {

    // TODO: add password generation?

    private final EncryptionAlgorithm encryptionAlgorithm;

    private final TextField descriptionField = new TextField(Strings.PASSWORD_DESCRIPTION);
    private final PasswordField passwordField = new PasswordField(Strings.ACCOUNT_PASSWORD);
    private final PasswordField repeatPasswordField = new PasswordField(Strings.REPEAT_ACCOUNT_PASSWORD);
    private final Button confirmButton = new Button(Strings.CONFIRM, e -> validateAndAddPassword());
    private final Button cancelButton = new Button(Strings.CANCEL, e -> close());

    private final Binder<Password> binder = new BeanValidationBinder<>(Password.class);

    private Password password = new Password();

    public AddPasswordDialog(SecurityConfiguration securityConfiguration) {
        encryptionAlgorithm = securityConfiguration.getEncryptionAlgorithm();

        H3 title = new H3(Strings.ADD_PASSWORD);
        title.addClassName("mt-0");

        confirmButton.setWidthFull();
        cancelButton.setWidthFull();

        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        descriptionField.setWidthFull();
        passwordField.setWidthFull();
        repeatPasswordField.setWidthFull();

        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonLayout.setWidthFull();

        VerticalLayout layout = new VerticalLayout(title, descriptionField, passwordField, repeatPasswordField, buttonLayout);
        layout.setMinWidth("20em");
        add(layout);

        // TODO: add password field validators
        binder.forField(descriptionField)
                .withValidator(new StringLengthValidator(StringGenerator.getLengthError(1, 64), 1, 64))
                .withValidator(text -> !text.isBlank(), Strings.BLANK_STRING_ERROR)
                .bind(Password::getDescription, Password::setDescription);
        binder.forField(passwordField).bind(Password::getPassword, Password::setPassword);
        binder.forField(repeatPasswordField)
                .withValidator(text -> passwordField.getValue().equals(text), Strings.PASSWORDS_NOT_MATCHING_ERROR)
                .bind(Password::getPassword, Password::setPassword);
        binder.bindInstanceFields(this);
    }

    private void validateAndAddPassword() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            binder.writeBean(password);
            password.setPassword(encryptionAlgorithm.encrypt(password.getPassword(), "")); // TODO: FIX PASSWORD!!!
            password.setUser(userDetails.getUser());
            fireEvent(new SavePasswordEvent(this, password));
            close();
        } catch (ValidationException e) {
            Notification.show(Strings.ILLEGAL_FIELD_VALUES_ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            Notification.show(Strings.GENERIC_ERROR);
            e.printStackTrace();
        }
    }

    public static abstract class AddPasswordDialogEvent extends ComponentEvent<AddPasswordDialog> {
        private Password password;

        protected AddPasswordDialogEvent(AddPasswordDialog source, Password password) {
            super(source, false);
            this.password = password;
        }

        public Password getPassword() {
            return password;
        }
    }

    public static class SavePasswordEvent extends AddPasswordDialogEvent {
        SavePasswordEvent(AddPasswordDialog source, Password password) {
            super(source, password);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
