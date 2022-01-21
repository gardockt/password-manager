package pl.edu.pw.gardockt.passwordmanager.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.gardockt.passwordmanager.ApplicationConfiguration;
import pl.edu.pw.gardockt.passwordmanager.Formatter;
import pl.edu.pw.gardockt.passwordmanager.RegexCheck;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.dialogs.AddPasswordDialog;
import pl.edu.pw.gardockt.passwordmanager.dialogs.MessageDialog;
import pl.edu.pw.gardockt.passwordmanager.dialogs.UnlockPasswordDialog;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.security.CustomUserDetails;
import pl.edu.pw.gardockt.passwordmanager.security.SecurityConfiguration;
import pl.edu.pw.gardockt.passwordmanager.services.DatabaseService;

import javax.annotation.security.PermitAll;
import java.sql.Timestamp;
import java.util.Collection;

@PageTitle(PasswordListView.PAGE_TITLE)
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class PasswordListView extends VerticalLayout {

    public final static String PAGE_TITLE = "Lista haseł";

    private final SecurityConfiguration securityConfiguration;
    private final DatabaseService databaseService;

    private final User user;
    private Collection<Password> passwords;

    private final Grid<Password> passwordGrid = new Grid<>(Password.class);

    public PasswordListView(SecurityConfiguration securityConfiguration, DatabaseService databaseService) {
        this.securityConfiguration = securityConfiguration;
        this.databaseService = databaseService;

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userDetails.getUser();

        if(user.getFailedAttemptsSinceLogin() >= securityConfiguration.failedAttemptsLockCount) {
            new MessageDialog(
                    "Liczba nieudanych prób logowania od ostatniego poprawnego zalogowania: " + user.getFailedAttemptsSinceLogin(),
                    "Ostrzeżenie"
            ).open();
            user.setFailedAttemptsSinceLogin(0);
        }

        setSizeFull();

        passwordGrid.setSizeFull();
        passwordGrid.removeAllColumns();
        passwordGrid.addColumn("description").setHeader("Opis");
        passwordGrid.addColumn(password -> Formatter.formatDate(password.getLastAccess()))
            .setComparator((pa, pb) -> {
                Timestamp ta = pa.getLastAccess();
                Timestamp tb = pb.getLastAccess();
                if(ta != null && tb != null) {
                    return ta.compareTo(tb);
                } else {
                    return (ta != null ? 1 : 0) - (tb != null ? 1 : 0);
                }
            })
            .setHeader("Ostatni dostęp");
        passwordGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        passwordGrid.asSingleSelect().addValueChangeListener(e -> {
            if(e.getValue() != null) {
                try {
                    new UnlockPasswordDialog(securityConfiguration, databaseService, e.getValue(), this).open();
                } catch (Exception ex) {
                    Notification.show(Strings.GENERIC_ERROR);
                    ex.printStackTrace();
                }
                passwordGrid.asSingleSelect().setValue(null);
            }
        });
        refreshGrid();

        add(new Button(Strings.ADD_PASSWORD, e -> openAddPasswordDialog()), passwordGrid);
    }

    public void refreshGrid() {
        passwords = databaseService.getPasswords(user);
        boolean passwordsCorrect = passwords.stream().allMatch(p ->
            p != null &&
            p.getUser().getId().equals(user.getId()) &&
            p.getPassword() != null && RegexCheck.isBase64(p.getPassword()) &&
            p.getDescription() != null && RegexCheck.containsOnlyLegalCharacters(p.getDescription()) &&
            (p.getUsername() == null || RegexCheck.containsOnlyLegalCharacters(p.getUsername()))
        );

        if(passwordsCorrect) {
            passwordGrid.setItems(passwords);
        } else {
            Notification.show(Strings.GENERIC_ERROR);
        }
    }

    private void openAddPasswordDialog() {
        if(passwords.size() >= ApplicationConfiguration.MAX_STORED_PASSWORDS_COUNT) {
            Notification.show("Osiągnięto maksymalną ilość haseł");
            return;
        }

        AddPasswordDialog dialog = new AddPasswordDialog(securityConfiguration, passwords);
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
