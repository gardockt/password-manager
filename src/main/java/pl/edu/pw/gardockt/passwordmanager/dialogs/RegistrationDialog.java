package pl.edu.pw.gardockt.passwordmanager.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import pl.edu.pw.gardockt.passwordmanager.Strings;

public class RegistrationDialog extends Dialog {

    private final TextField usernameField = new TextField(Strings.USERNAME);
    private final PasswordField passwordField = new PasswordField(Strings.PASSWORD);
    private final PasswordField repeatPasswordField = new PasswordField(Strings.REPEAT_PASSWORD);
    private final Button registerButton = new Button(Strings.CONFIRM);
    private final Button cancelButton = new Button(Strings.CANCEL);

    private final String width = "20em";

    public RegistrationDialog() {
        configureComponents();

        HorizontalLayout buttonLayout = new HorizontalLayout(registerButton, cancelButton);
        buttonLayout.add(registerButton, cancelButton);

        buttonLayout.setWidth(width);

        VerticalLayout layout = new VerticalLayout(
                new H2(Strings.REGISTER),
                usernameField,
                passwordField,
                repeatPasswordField,
                buttonLayout
        );
        add(layout);
    }

    private void configureComponents() {
        usernameField.setWidth(width);
        passwordField.setWidth(width);
        repeatPasswordField.setWidth(width);

        usernameField.addClassName("py-0");
        passwordField.addClassName("py-0");
        repeatPasswordField.addClassName("py-0");

        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        registerButton.setWidthFull();
        cancelButton.setWidthFull();

        // TODO: add register button listener
        cancelButton.addClickListener(e -> close());
    }

}
