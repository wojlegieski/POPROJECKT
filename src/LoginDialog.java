import javax.swing.*;

public class LoginDialog extends JDialog {
    private boolean succeeded;

    public LoginDialog(JFrame parent) {
        super(parent, "Logowanie", true);
        setContentPane(new LoginPanel(this));
        pack();
        setLocationRelativeTo(parent);
        succeeded = false;
        setSize(800, 600);
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }
}
