package pl.edu.pw.gardockt.passwordmanager.views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.gardockt.passwordmanager.dialogs.UnlockPasswordDialog;
import pl.edu.pw.gardockt.passwordmanager.entities.Password;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.security.CustomUserDetails;
import pl.edu.pw.gardockt.passwordmanager.services.DatabaseService;

import javax.annotation.security.PermitAll;

@PageTitle(PasswordListView.PAGE_TITLE)
@Route(value = "passwords", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class PasswordListView extends VerticalLayout {

    public final static String PAGE_TITLE = "Lista haseł";

    private final DatabaseService databaseService;

    private final Grid<Password> passwordGrid = new Grid<>(Password.class);

    public PasswordListView(DatabaseService databaseService) {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        this.databaseService = databaseService;

        setSizeFull();

        passwordGrid.setSizeFull();
        passwordGrid.removeAllColumns();
        passwordGrid.addColumn("description").setHeader("Opis");
        //passwordGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        passwordGrid.asSingleSelect().addValueChangeListener(e -> {
            if(e.getValue() != null) {
                new UnlockPasswordDialog(e.getValue()).open();
                passwordGrid.asSingleSelect().setValue(null);
            }
        });
        passwordGrid.setItems(databaseService.getPasswords(user));

        add(passwordGrid);
    }

}
