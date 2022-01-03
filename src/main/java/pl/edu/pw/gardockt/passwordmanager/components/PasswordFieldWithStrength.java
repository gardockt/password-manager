package pl.edu.pw.gardockt.passwordmanager.components;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.value.ValueChangeMode;
import pl.edu.pw.gardockt.passwordmanager.security.PasswordStrengthCalculator;

public class PasswordFieldWithStrength extends FormLayout {

    private final PasswordStrengthCalculator calculator;

    private final PasswordField passwordField;
    private final Label complexityText = new Label();
    private final ProgressBar complexityBar = new ProgressBar();
    private int complexityScore = 0;
    private HasSize[] componentsBySize;

    public PasswordFieldWithStrength(PasswordStrengthCalculator calculator) {
        this.calculator = calculator;
        this.passwordField = new PasswordField();
        configure();
    }

    public PasswordFieldWithStrength(PasswordStrengthCalculator calculator, String label) {
        this.calculator = calculator;
        this.passwordField = new PasswordField(label);
        configure();
    }

    private void configure() {
        componentsBySize = new HasSize[]{passwordField, complexityText, complexityBar};
        for(HasSize c : componentsBySize) {
            c.setWidthFull();
        }

        passwordField.setValueChangeMode(ValueChangeMode.LAZY);
        passwordField.addValueChangeListener(e -> updateComplexity());

        complexityBar.setMin(calculator.getMinStrength());
        complexityBar.setMax(calculator.getMaxStrength());

        add(passwordField, complexityBar, complexityText);
    }

    private void updateComplexity() {
        complexityScore = calculator.getPasswordStrength(passwordField.getValue());
        complexityText.setText(calculator.getStrengthLabel(complexityScore));
        complexityBar.setValue(complexityScore);
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public double getComplexityScore() {
        return complexityScore;
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        for(HasSize c : componentsBySize) {
            c.setWidth(width);
        }
    }

}
