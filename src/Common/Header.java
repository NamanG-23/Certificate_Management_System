package Common;
import javax.swing.*;
import java.awt.*;

public class Header {
    public static JPanel getHeaderPanel() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 250, 240));

        ImageIcon logoIcon = new ImageIcon("src/Common/college_logo.png");
        Image scaledImage = logoIcon.getImage().getScaledInstance(
                650, 100,
                Image.SCALE_SMOOTH
        );

        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(logoLabel, BorderLayout.CENTER);

        headerPanel.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 2, 0,
                        Color.LIGHT_GRAY
                )
        );
        return headerPanel;
    }
}