import javax.swing.*;
import java.awt.*;
import java.net.Authenticator;
import java.net.URI;
import java.net.http.*;
public class LoginPanel extends JPanel {

    public LoginPanel(JDialog parent) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel label = new JLabel("Login ekran (placeholder)");
        JPasswordField password = new JPasswordField(15);
        JButton button = new JButton("Login");
        button.addActionListener(e -> {
            String pass = new String(password.getPassword());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.google.com"))
                    .header("Content-Type", "application/idk")
                    .POST(HttpRequest.BodyPublishers.ofString(pass))
                    .build();

            if()
                if (parent instanceof LoginDialog) {
                    ((LoginDialog) parent).setSucceeded(true);
                    parent.dispose();
                }

        });
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(button, gbc);
        add(password, gbc);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);


    }


    Authenticator
}
