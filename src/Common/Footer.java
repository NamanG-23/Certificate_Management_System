package Common;
import javax.swing.*;
import java.awt.*;

public class Footer {
    public static JPanel getFooterPanel() {

        JPanel footerPanel = new JPanel(new BorderLayout());

        footerPanel.setBackground(new Color(21, 33, 66));
        footerPanel.setPreferredSize(new Dimension(0, 55));

        JLabel projectLabel = new JLabel("Certificate Management System");
        projectLabel.setForeground(Color.WHITE);
        projectLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        projectLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(21, 33, 66));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 15));

        JLabel developerLabel = new JLabel("Developed by: Naman Gupta");
        developerLabel.setForeground(Color.WHITE);
        developerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        developerLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel regLabel = new JLabel("Reg No.: PIET25CS100");
        regLabel.setForeground(Color.WHITE);
        regLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        regLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(developerLabel);
        rightPanel.add(regLabel);

        footerPanel.add(projectLabel, BorderLayout.WEST);
        footerPanel.add(rightPanel, BorderLayout.EAST);

        return footerPanel;
    }
}