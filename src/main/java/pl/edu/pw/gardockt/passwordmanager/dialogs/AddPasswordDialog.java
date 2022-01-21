package pl.edu.pw.gardockt.passwordmanager.dialogs;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.gardockt.passwordmanager.StringGenerator;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.components.PasswordFieldWithStrength;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.security.*;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.EncryptionAlgorithm;
import pl.edu.pw.gardockt.passwordmanager.security.encryption.EncryptionPasswordGenerator;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Collectors;

public class AddPasswordDialog extends Dialog {

    private final PasswordEncoder passwordEncoder;
    private final EncryptionAlgorithm encryptionAlgorithm;

    private final Collection<Password> existingPasswords;

    private final TextField descriptionField = new TextField(Strings.PASSWORD_DESCRIPTION);
    private final TextField usernameField = new TextField(Strings.USERNAME);
    private final PasswordFieldWithStrength passwordField = new PasswordFieldWithStrength(new SimplePasswordStrengthCalculator(), Strings.ACCOUNT_PASSWORD);
    private final PasswordField repeatPasswordField = new PasswordField(Strings.REPEAT_ACCOUNT_PASSWORD);
    private final PasswordField unlockPasswordField = new PasswordField(Strings.UNLOCK_PASSWORD);
    private final Button confirmButton = new Button(Strings.CONFIRM, e -> validateAndAddPassword());
    private final Button cancelButton = new Button(Strings.CANCEL, e -> close());

    private final Binder<Password> binder = new BeanValidationBinder<>(Password.class);

    private final Password password = new Password();

    public AddPasswordDialog(SecurityConfiguration securityConfiguration, Collection<Password> existingPasswords) {
        this.passwordEncoder = securityConfiguration.getPasswordEncoder();
        this.encryptionAlgorithm = securityConfiguration.getEncryptionAlgorithm();
        this.existingPasswords = existingPasswords;

        H3 title = new H3(Strings.ADD_PASSWORD);
        title.addClassName("mt-0");

        confirmButton.setWidthFull();
        cancelButton.setWidthFull();

        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        confirmButton.addClickShortcut(Key.ENTER);
        cancelButton.addClickShortcut(Key.ESCAPE);

        descriptionField.setWidthFull();
        usernameField.setWidthFull();
        passwordField.setWidthFull();
        repeatPasswordField.setWidthFull();
        unlockPasswordField.setWidthFull();

        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
        buttonLayout.setWidthFull();

        VerticalLayout layout = new VerticalLayout(title, descriptionField, usernameField, passwordField, repeatPasswordField, unlockPasswordField, buttonLayout);
        layout.setMinWidth("20em");
        add(layout);

        binder.forField(descriptionField)
                .withValidator(new StringLengthValidator(StringGenerator.getLengthError(1, 64), 1, 64))
                .withValidator(text -> !text.isBlank(), Strings.BLANK_STRING_ERROR)
                .withValidator(text -> existingPasswords.stream().noneMatch(password -> password.getDescription().equals(text)), Strings.VALUE_MUST_BE_UNIQUE)
                .bind(Password::getDescription, Password::setDescription);
        binder.forField(usernameField)
                    .withValidator(new StringLengthValidator(StringGenerator.getMaxLengthError(64), 0, 64))
                    .bind(Password::getUsername, Password::setUsername);
        binder.forField(passwordField.getPasswordField())
                .withValidator(new StringLengthValidator(
                    StringGenerator.getLengthError(1, PasswordConfiguration.MAX_LENGTH),
                    1, PasswordConfiguration.MAX_LENGTH))
                .bind(Password::getPassword, Password::setPassword);
        binder.forField(repeatPasswordField)
                .withValidator(text -> passwordField.getPasswordField().getValue().equals(text), Strings.PASSWORDS_NOT_MATCHING)
                .bind(Password::getPassword, Password::setPassword);
        binder.bindInstanceFields(this);
    }

    private void validateAndAddPassword() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            binder.writeBean(password);

            if (!passwordEncoder.matches(unlockPasswordField.getValue(), userDetails.getUser().getUnlockPassword())) {
                throw new BadCredentialsException(Strings.INCORRECT_UNLOCK_PASSWORD_ERROR);
            }

            password.setPassword(encryptionAlgorithm.encrypt(
                password.getPassword(),
                EncryptionPasswordGenerator.generate(unlockPasswordField.getValue(), userDetails.getUsername()),
                generateUniqueIV())
            );
            password.setUser(userDetails.getUser());
            fireEvent(new SavePasswordEvent(this, password));
            close();
        } catch (ValidationException e) {
            Notification.show(Strings.ILLEGAL_FIELD_VALUES_ERROR);
        } catch (BadCredentialsException e) {
            Notification.show(e.getMessage());
        } catch (Exception e) {
            Notification.show(Strings.GENERIC_ERROR);
            e.printStackTrace();
        }
    }

    private byte[] generateUniqueIV() {
        int ivLength = 12; // should be divisible by 3, see below

        Collection<byte[]> existingIVs = existingPasswords.stream().map(
                p -> Base64.getDecoder().decode(p.getPassword().substring(0, ivLength * 4 / 3))
        ).collect(Collectors.toList());

        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[ivLength];
        do {
            random.nextBytes(iv);
        } while (containsIV(existingIVs, iv));
        return iv;
    }

    private boolean containsIV(Collection<byte[]> existingIVs, byte[] iv) {
        return existingIVs.stream().anyMatch(eiv -> Arrays.equals(iv, eiv));
    }

    public static abstract class AddPasswordDialogEvent extends ComponentEvent<AddPasswordDialog> {
        private final Password password;

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
