import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;

public class LoginPanel extends JPanel {

    public LoginPanel(JDialog parent) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Zaloguj się");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, gbc);
        gbc.gridy++;
        add(new JLabel("Login:"), gbc);
        gbc.gridy++;
        JTextField emailField = new JTextField(20);
        add(emailField, gbc);
        gbc.gridy++;
        add(new JLabel("Hasło:"), gbc);
        gbc.gridy++;
        JPasswordField password = new JPasswordField(20);
        add(password, gbc);
        gbc.gridy++;
        JButton button = new JButton("Login");
        add(button, gbc);
        gbc.gridy++;
        JButton createAccountButton = new JButton("Create Account");
        add(createAccountButton, gbc);
        gbc.gridy++;


        createAccountButton.addActionListener(e ->{
            try {
                Desktop.getDesktop().browse(new URI("https://jkorona.lab.kis.agh.edu.pl/register"));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Nie udało się otworzyć strony rejestracji.");
            }} );
        add(createAccountButton, gbc);

        button.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(password.getPassword());

            String jsonBody = String.format("""
                {
                  "username": "%s",
                  "password": "%s"
                }
            """, email, pass);

            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            HttpClient client = HttpClient.newBuilder()
                    .cookieHandler(cookieManager)
                    .build();


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://jkorona.lab.kis.agh.edu.pl/api/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    if (parent instanceof LoginDialog) {
                        ((LoginDialog) parent).setSucceeded(true);
                        ((LoginDialog) parent).setClient(client);
                        parent.dispose();
                    }
                } else {
                    System.out.println(response);
                    JOptionPane.showMessageDialog(this, "Błąd logowania: " + response.statusCode(),
                            "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Błąd połączenia: " + ex.getMessage(),
                        "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });


    }
}
