package pl.edu.pw.gardockt.passwordmanager.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.gardockt.passwordmanager.Formatter;
import pl.edu.pw.gardockt.passwordmanager.entities.LoginHistory;
import pl.edu.pw.gardockt.passwordmanager.entities.User;
import pl.edu.pw.gardockt.passwordmanager.security.CustomUserDetails;
import pl.edu.pw.gardockt.passwordmanager.services.DatabaseService;

import javax.annotation.security.PermitAll;
import java.sql.Timestamp;

@PageTitle(LoginHistoryView.PAGE_TITLE)
@Route(value = "history", layout = MainLayout.class)
@PermitAll
public class LoginHistoryView extends VerticalLayout {

    public final static String PAGE_TITLE = "Historia logowań";

    private final DatabaseService databaseService;

    private final User user;

    private final Grid<LoginHistory> loginHistoryGrid = new Grid<>(LoginHistory.class);

    public LoginHistoryView(DatabaseService databaseService) {
        this.databaseService = databaseService;

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userDetails.getUser();

        setSizeFull();

        loginHistoryGrid.setSizeFull();
        loginHistoryGrid.removeAllColumns();
        loginHistoryGrid.addColumn("ip").setHeader("IP");
        loginHistoryGrid.addColumn("userAgent").setHeader("User Agent");
        loginHistoryGrid.addColumn("count").setHeader("Ilość logowań");
        loginHistoryGrid.addColumn(lh -> Formatter.formatDate(lh.getLastAccess()))
            .setComparator((pa, pb) -> {
                Timestamp ta = pa.getLastAccess();
                Timestamp tb = pb.getLastAccess();
                if(ta != null && tb != null) {
                    return ta.compareTo(tb);
                } else {
                    return (ta != null ? 1 : 0) - (tb != null ? 1 : 0);
                }
            })
            .setHeader("Ostatnie logowanie");
        loginHistoryGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        refreshGrid();

        add(new Button("Odśwież", e -> refreshGrid()), loginHistoryGrid);
    }

    private void refreshGrid() {
        loginHistoryGrid.setItems(databaseService.getLoginHistoryByUser(user));
    }

}
