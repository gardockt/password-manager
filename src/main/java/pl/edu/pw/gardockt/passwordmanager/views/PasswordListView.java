package pl.edu.pw.gardockt.passwordmanager.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.dialogs.AddPasswordDialog;
import pl.edu.pw.gardockt.passwordmanager.dialogs.UnlockPasswordDialog;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.security.CustomUserDetails;
import pl.edu.pw.gardockt.passwordmanager.security.PasswordVerifier;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;
import pl.edu.pw.gardockt.passwordmanager.services.DatabaseService;

import javax.annotation.security.PermitAll;

@PageTitle(PasswordListView.PAGE_TITLE)
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class PasswordListView extends VerticalLayout {

    public final static String PAGE_TITLE = "Lista haseł";

    private final SecurityConfiguration securityConfiguration;
    private final PasswordVerifier passwordVerifier;

    private final User user;

    private final DatabaseService databaseService;

    private final Grid<Password> passwordGrid = new Grid<>(Password.class);

    public PasswordListView(SecurityConfiguration securityConfiguration, DatabaseService databaseService, PasswordVerifier passwordVerifier) {
        this.securityConfiguration = securityConfiguration;
        this.databaseService = databaseService;
        this.passwordVerifier = passwordVerifier;

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userDetails.getUser();

        setSizeFull();

        passwordGrid.setSizeFull();
        passwordGrid.removeAllColumns();
        passwordGrid.addColumn("description").setHeader("Opis");
        //passwordGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        passwordGrid.asSingleSelect().addValueChangeListener(e -> {
            if(e.getValue() != null) {
                new UnlockPasswordDialog(securityConfiguration, e.getValue()).open();
                passwordGrid.asSingleSelect().setValue(null);
            }
        });
        refreshGrid();

        add(new Button(Strings.ADD_PASSWORD, e -> openAddPasswordDialog()), passwordGrid);
    }

    private void refreshGrid() {
        passwordGrid.setItems(databaseService.getPasswords(user));
    }

    private void openAddPasswordDialog() {
        AddPasswordDialog dialog = new AddPasswordDialog(securityConfiguration, passwordVerifier);
        dialog.addListener(AddPasswordDialog.SavePasswordEvent.class, this::addPassword);
        dialog.open();
    }

    private void addPassword(AddPasswordDialog.SavePasswordEvent event) {
        try {
            databaseService.addPassword(event.getPassword());
            Notification.show("Hasło dodane pomyślnie");
        } catch (Exception e) {
            Notification.show(Strings.GENERIC_ERROR);
            e.printStackTrace();
        } finally {
            refreshGrid();
        }
    }

}
