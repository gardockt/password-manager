package pl.edu.pw.gardockt.passwordmanager.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;

public class PasswordDialog extends Dialog {

    // TODO: copy to clipboard

    private final PasswordField passwordField = new PasswordField(Strings.ACCOUNT_PASSWORD);
    private final Button closeButton = new Button(Strings.CLOSE, e -> close());

    public PasswordDialog(Password password) {
        boolean includeUsername = (password.getUsername() != null && !password.getUsername().isBlank());

        passwordField.setWidthFull();
        passwordField.setReadOnly(true);
        passwordField.setValue(password.getPassword());

        closeButton.setWidthFull();
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeButton.addClickShortcut(Key.ESCAPE);

        H3 title = new H3(password.getDescription());
        title.addClassName("mt-0");

        VerticalLayout layout = new VerticalLayout();
        layout.setMinWidth("20em");

        layout.add(title);

        if(includeUsername) {
            TextField usernameField = new TextField(Strings.USERNAME);
            usernameField.setWidthFull();
            usernameField.setReadOnly(true);
            usernameField.setValue(password.getUsername());
            layout.add(usernameField);
        }

        layout.add(passwordField, closeButton);
        add(layout);
    }

}
