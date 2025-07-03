import javax.swing.*;
import java.net.http.HttpClient;

public class LoginDialog extends JDialog {
    private boolean succeeded;
    private HttpClient client;

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

    public void setClient(HttpClient client) {this.client = client;}

    public HttpClient getClient() {return client;}
}