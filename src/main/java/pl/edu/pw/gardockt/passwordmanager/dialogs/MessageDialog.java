package pl.edu.pw.gardockt.passwordmanager.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import pl.edu.pw.gardockt.passwordmanager.Strings;

public class MessageDialog extends Dialog {

    public MessageDialog(String message, String title) {
        setMaxWidth("30em");

        H3 titleObject = new H3(title);
        titleObject.addClassName("mt-0");

        Button closeButton = new Button(Strings.CLOSE, e -> close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeButton.addClickShortcut(Key.ENTER);
        closeButton.addClickShortcut(Key.ESCAPE);

        add(new VerticalLayout(
                titleObject,
                new Label(message),
                closeButton
        ));
    }

}
