package pl.edu.pw.gardockt.passwordmanager.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.edu.pw.gardockt.passwordmanager.Strings;
import pl.edu.pw.gardockt.passwordmanager.dialogs.RegistrationDialog;
import pl.edu.pw.gardockt.passwordmanager.services.RegistrationService;

@Route("login")
@PageTitle(LoginView.PAGE_TITLE)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    public static final String PAGE_TITLE = Strings.LOGIN;

    private final LoginForm loginForm = new LoginForm();
    private final Button registerButton = new Button(Strings.REGISTER);

    public LoginView(RegistrationService registrationService, UserDetailsService userDetailsService) {
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        setLoginOverlayStrings();

        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);

        registerButton.addClickListener(e -> new RegistrationDialog(registrationService, userDetailsService).open());

        add(loginForm, registerButton);
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
        form.setTitle(Strings.LOGIN);
        form.setUsername(Strings.USERNAME);
        form.setPassword(Strings.ACCOUNT_PASSWORD);
        form.setSubmit(Strings.LOGIN);
        loginI18n.setForm(form);

        loginForm.setI18n(loginI18n);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true);
        }
    }

}
