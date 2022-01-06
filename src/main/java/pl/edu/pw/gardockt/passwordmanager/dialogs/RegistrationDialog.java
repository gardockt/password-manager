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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.edu.pw.gardockt.passwordmanager.StringGenerator;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.components.PasswordFieldWithStrength;
import pl.edu.pw.gardockt.passwordmanager.entities.RegistrationData;
import pl.edu.pw.gardockt.passwordmanager.security.PasswordConfiguration;
import pl.edu.pw.gardockt.passwordmanager.security.PasswordStrengthCalculator;
import pl.edu.pw.gardockt.passwordmanager.security.SimplePasswordStrengthCalculator;
import pl.edu.pw.gardockt.passwordmanager.services.UserService;

public class RegistrationDialog extends Dialog {

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    private final PasswordStrengthCalculator calculator = new SimplePasswordStrengthCalculator();

    private final TextField usernameField = new TextField(Strings.USERNAME);
    private final PasswordFieldWithStrength accountPasswordField = new PasswordFieldWithStrength(calculator, Strings.ACCOUNT_PASSWORD);
    private final PasswordField repeatAccountPasswordField = new PasswordField(Strings.REPEAT_ACCOUNT_PASSWORD);
    private final PasswordFieldWithStrength unlockPasswordField = new PasswordFieldWithStrength(calculator, Strings.UNLOCK_PASSWORD);
    private final PasswordField repeatUnlockPasswordField = new PasswordField(Strings.REPEAT_UNLOCK_PASSWORD);
    private final Button registerButton = new Button(Strings.CONFIRM, e -> register());
    private final Button cancelButton = new Button(Strings.CANCEL, e -> close());

    private final String width = "20em";

    private final RegistrationData registrationData = new RegistrationData();
    private final Binder<RegistrationData> binder = new BeanValidationBinder<>(RegistrationData.class);

    public RegistrationDialog(UserService userService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;

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

        binder.forField(usernameField)
                .withValidator(new StringLengthValidator(StringGenerator.getLengthError(4, 32), 4, 32))
                .withValidator(this::isUsernameAvailable, Strings.USERNAME_TAKEN)
                .bind(RegistrationData::getUsername, RegistrationData::setUsername);
        binder.forField(accountPasswordField.getPasswordField())
                .withValidator(new StringLengthValidator(
                        StringGenerator.getLengthError(PasswordConfiguration.MIN_LENGTH, PasswordConfiguration.MAX_LENGTH),
                        PasswordConfiguration.MIN_LENGTH,
                        PasswordConfiguration.MAX_LENGTH
                ))
                .withValidator(pass -> accountPasswordField.getComplexityScore() >= 2, Strings.PASSWORD_TOO_WEAK)
                .bind(RegistrationData::getAccountPassword, RegistrationData::setAccountPassword);
        binder.forField(repeatAccountPasswordField)
                .withValidator(pass -> accountPasswordField.getPasswordField().getValue().equals(pass), Strings.PASSWORDS_NOT_MATCHING)
                .bind(RegistrationData::getAccountPassword, RegistrationData::setAccountPassword);
        binder.forField(unlockPasswordField.getPasswordField())
                .withValidator(new StringLengthValidator(
                        StringGenerator.getLengthError(PasswordConfiguration.MIN_LENGTH, PasswordConfiguration.MAX_LENGTH),
                        PasswordConfiguration.MIN_LENGTH,
                        PasswordConfiguration.MAX_LENGTH
                ))
                .withValidator(pass -> unlockPasswordField.getComplexityScore() >= 2, Strings.PASSWORD_TOO_WEAK)
                .bind(RegistrationData::getUnlockPassword, RegistrationData::setAccountPassword);
        binder.forField(repeatUnlockPasswordField)
                .withValidator(pass -> unlockPasswordField.getPasswordField().getValue().equals(pass), Strings.PASSWORDS_NOT_MATCHING)
                .bind(RegistrationData::getUnlockPassword, RegistrationData::setUnlockPassword);
        binder.bindInstanceFields(this);

        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        registerButton.setWidthFull();
        cancelButton.setWidthFull();
    }

    private boolean isUsernameAvailable(String username) {
        try {
            userDetailsService.loadUserByUsername(username);
            return false;
        } catch (UsernameNotFoundException e) {
            return true;
        }
    }

    private void register() {
        // TODO: add validation?

        try {
            binder.writeBean(registrationData);
            userService.register(registrationData);
            Notification.show("Zarejestrowano pomyślnie");
            close();
        } catch(ValidationException e) {
            Notification.show(Strings.ILLEGAL_FIELD_VALUES_ERROR);
        } catch(Exception e) {
            Notification.show(Strings.ERROR_OCCURRED);
            e.printStackTrace();
        }
    }

}
