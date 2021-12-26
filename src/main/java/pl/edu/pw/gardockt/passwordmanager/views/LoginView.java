package pl.edu.pw.gardockt.passwordmanager.views;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import pl.edu.pw.gardockt.passwordmanager.Strings;

@Route("login")
@PageTitle(LoginView.PAGE_TITLE)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    public static final String PAGE_TITLE = "Zaloguj się";
    private final LoginOverlay loginOverlay = new LoginOverlay();

    public LoginView() {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        setLoginOverlayStrings();

        loginOverlay.setAction("login");
        loginOverlay.setForgotPasswordButtonVisible(false);
        loginOverlay.setOpened(true);
        add(loginOverlay);
    }

    private void setLoginOverlayStrings() {
        LoginI18n loginI18n = LoginI18n.createDefault();

        LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setTitle("Nieprawidłowa nazwa użytkownika lub hasło");
        errorMessage.setMessage("Sprawdź poprawność wprowadzanych danych i spróbuj ponownie");
        loginI18n.setErrorMessage(errorMessage);

        LoginI18n.Header header = new LoginI18n.Header();
        header.setTitle(Strings.APP_TITLE);
        header.setDescription("Zaloguj się, aby kontynuować");
        loginI18n.setHeader(header);

        LoginI18n.Form form = new LoginI18n.Form();
        form.setTitle("Zaloguj się");
        form.setUsername("Nazwa użytkownika");
        form.setPassword("Hasło");
        form.setSubmit("Zaloguj się");
        form.setForgotPassword("Zapomniałem hasła");
        loginI18n.setForm(form);

        loginOverlay.setI18n(loginI18n);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginOverlay.setError(true);
        }
    }

}
