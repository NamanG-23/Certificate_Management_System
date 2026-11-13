package Files;
import Common.*;
import DatabaseConnectivity.dbConnect;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ViewCertificates extends JFrame {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final int EDIT_COL = 5;
    private final Color navyColor = new Color(21, 33, 66);
    private final Color blueColor = new Color(41, 98, 255);
    private final Color blueHover = new Color(25, 75, 210);
    private final Color redColor = new Color(200, 50, 50);
    private final Color redHover = new Color(160, 35, 35);
    private final Color disabledColor = new Color(200, 200, 200);
    private final Color bgColor = new Color(245, 245, 245);
    private final String userName;
    private final List<Certificate> certificates = new ArrayList<>();
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JPanel detailsContent;
    private JScrollPane detailsScroll;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton viewBtn;
    private Certificate selected;

    public ViewCertificates(String userName) {
        this.userName = userName;
        setTitle("Certificate Management System - My Certificates");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(Header.getHeaderPanel(), BorderLayout.NORTH);
        topPanel.add(createTitleBar(), BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(Footer.getFooterPanel(), BorderLayout.SOUTH);
        loadCertificates();
    }

    private JPanel createTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(navyColor);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("My Certificates");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel user = new JLabel("Welcome, " + userName);
        user.setFont(new Font("SansSerif", Font.PLAIN, 14));
        user.setForeground(Color.WHITE);
        bar.add(title, BorderLayout.WEST);
        bar.add(user, BorderLayout.EAST);
        return bar;
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout(15, 12));
        content.setBackground(bgColor);
        content.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        content.add(createTopBar(), BorderLayout.NORTH);
        content.add(createTablePanel(), BorderLayout.CENTER);
        content.add(createDetailsPanel(), BorderLayout.EAST);
        return content;
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        JButton backBtn = createButton("Back to Dashboard", blueColor, blueHover, 185, 34);
        backBtn.addActionListener(e -> {
            dispose();
            new DashboardForm(userName).setVisible(true);
        });
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.setToolTipText("Search by name, issuer, type, category...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        bar.add(backBtn, BorderLayout.WEST);
        bar.add(searchPanel, BorderLayout.EAST);
        return bar;
    }
    private JScrollPane createTablePanel() {
        String[] columns = {"Certificate Name", "Issued By", "Issue Date", "Type", "Category", "Edit"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                return false;
            }
            @Override public Class<?> getColumnClass(int col) {
                return col == 2 ? LocalDate.class : String.class;
            }
        };
        table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(220, 232, 255));
        table.setSelectionForeground(navyColor);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        sorter = new TableRowSorter<>(model);
        sorter.setSortable(EDIT_COL, false);
        table.setRowSorter(sorter);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel,
                                                           boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, value, false, false, row, col);
                setBackground(navyColor);
                setForeground(Color.WHITE);
                setFont(new Font("SansSerif", Font.BOLD, 13));
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                setHorizontalAlignment(col == EDIT_COL ? SwingConstants.CENTER : SwingConstants.LEFT);
                return this;
            }
        });
        CellRenderer cellRenderer = new CellRenderer();
        table.setDefaultRenderer(String.class, cellRenderer);
        table.setDefaultRenderer(LocalDate.class, cellRenderer);
        table.getColumnModel().getColumn(EDIT_COL).setCellRenderer(new DefaultTableCellRenderer() {
            private final Icon pencil = new PencilIcon(blueColor);
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, "", sel, false, row, col);
                setIcon(pencil);
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });
        int[] widths = {160, 115, 95, 95, 95, 45};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        table.getColumnModel().getColumn(EDIT_COL).setMinWidth(45);
        table.getColumnModel().getColumn(EDIT_COL).setMaxWidth(45);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = table.getSelectedRow();
            showDetails(row < 0 ? null : certificates.get(table.convertRowIndexToModel(row)));
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == EDIT_COL) {
                    openEdit(certificates.get(table.convertRowIndexToModel(row)));
                }
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor(Cursor.getPredefinedCursor(
                        row >= 0 && col == EDIT_COL ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(330, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel heading = new JLabel("Certificate Details");
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading.setForeground(navyColor);
        viewBtn = new JButton(new CameraIcon(blueColor));
        viewBtn.setDisabledIcon(new CameraIcon(disabledColor));
        viewBtn.setToolTipText("View certificate image");
        viewBtn.setFocusPainted(false);
        viewBtn.setContentAreaFilled(false);
        viewBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        viewBtn.setPreferredSize(new Dimension(38, 28));
        viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewBtn.addActionListener(e -> openCertificateFile());

        JPanel headingRow = new JPanel(new BorderLayout());
        headingRow.setOpaque(false);
        headingRow.add(heading, BorderLayout.WEST);
        headingRow.add(viewBtn, BorderLayout.EAST);
        detailsContent = new JPanel(new GridBagLayout());
        detailsContent.setBackground(Color.WHITE);
        detailsScroll = new JScrollPane(detailsContent);
        detailsScroll.setBorder(null);
        detailsScroll.getViewport().setBackground(Color.WHITE);
        detailsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        detailsScroll.getVerticalScrollBar().setUnitIncrement(16);
        editBtn = createButton("Edit", blueColor, blueHover, 130, 36);
        editBtn.setIcon(new PencilIcon(Color.WHITE));
        editBtn.setIconTextGap(8);
        editBtn.addActionListener(e -> {
            if (selected != null) openEdit(selected);
        });
        deleteBtn = createButton("Delete", redColor, redHover, 130, 36);
        deleteBtn.addActionListener(e -> handleDelete());
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(headingRow, BorderLayout.NORTH);
        panel.add(detailsScroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createButton(String text, Color normal, Color hover, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, h));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hover);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(normal);
            }
        });
        return btn;
    }

    private void showDetails(Certificate c) {
        selected = c;
        detailsContent.removeAll();
        if (c == null) {
            String msg = certificates.isEmpty()
                    ? "You have not added any certificates yet. Use Add Certificate to create one."
                    : "Select a certificate from the table to see its details.";
            JLabel hint = new JLabel("<html><div style='width:200px'>" + msg + "</div></html>");
            hint.setFont(new Font("SansSerif", Font.ITALIC, 13));
            hint.setForeground(new Color(120, 120, 120));
            GridBagConstraints g = new GridBagConstraints();
            g.gridx = 0; g.gridy = 0;
            g.anchor = GridBagConstraints.NORTHWEST;
            g.insets = new Insets(6, 0, 0, 0);
            detailsContent.add(hint, g);
        } else {
            int y = 0;
            y = addDetail(y, "Name", c.name);
            y = addDetail(y, "Course", c.course);
            y = addDetail(y, "Issued By", c.issuedBy);
            y = addDetail(y, "Cert. ID", c.certId);
            y = addDetail(y, "Issue Date", c.issueDate.format(DATE_FMT));
            y = addDetail(y, "Type", c.type);
            y = addDetail(y, "Category", c.category);
            y = addDetail(y, "Mode", c.mode);
            y = addDetail(y, "Validity", validityText(c.expiryDate));
            y = addDetail(y, "Grade", c.grade);
            addDetail(y, "Remarks", c.remarks);
            GridBagConstraints filler = new GridBagConstraints();
            filler.gridx = 2; filler.gridy = 0;
            filler.weightx = 1.0;
            detailsContent.add(Box.createHorizontalGlue(), filler);
        }
        boolean has = c != null;
        editBtn.setEnabled(has);
        deleteBtn.setEnabled(has);
        viewBtn.setEnabled(has);
        editBtn.setBackground(has ? blueColor : disabledColor);
        deleteBtn.setBackground(has ? redColor : disabledColor);
        detailsContent.revalidate();
        detailsContent.repaint();
        SwingUtilities.invokeLater(() -> detailsScroll.getVerticalScrollBar().setValue(0));
    }

    private int addDetail(int y, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(90, 90, 90));
        l.setPreferredSize(new Dimension(85, l.getPreferredSize().height));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = y;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(5, 0, 5, 8);
        detailsContent.add(l, g);
        String text = (value == null || value.trim().isEmpty()) ? "-" : value;
        JLabel v = new JLabel("<html><div style='width:135px'>" + escapeHtml(text) + "</div></html>");
        v.setFont(new Font("SansSerif", Font.PLAIN, 13));
        v.setForeground(navyColor);
        g = new GridBagConstraints();
        g.gridx = 1; g.gridy = y;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(5, 0, 5, 0);
        detailsContent.add(v, g);
        return y + 1;
    }

    private String validityText(LocalDate expiry) {
        if (expiry == null) return "Lifetime";
        LocalDate today = LocalDate.now();
        if (expiry.isBefore(today)) return "Expired (" + expiry.format(DATE_FMT) + ")";
        if (expiry.isEqual(today)) return "Expires today";
        Period p = Period.between(today, expiry);
        StringBuilder sb = new StringBuilder();
        if (p.getYears() > 0) sb.append(p.getYears()).append(p.getYears() == 1 ? " Year " : " Years ");
        if (p.getMonths() > 0) sb.append(p.getMonths()).append(p.getMonths() == 1 ? " Month " : " Months ");
        if (p.getYears() == 0 && p.getMonths() == 0) sb.append(p.getDays()).append(p.getDays() == 1 ? " Day " : " Days ");
        return sb.toString().trim() + " left (till " + expiry.format(DATE_FMT) + ")";
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }
    private void applyFilter() {
        final String q = searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                return certificates.get(entry.getIdentifier()).searchText.contains(q);
            }
        });
    }
    private void openCertificateFile() {
        if (selected == null) return;
        String path = selected.image;
        File file = (path == null || path.trim().isEmpty()) ? null : new File(path.trim());
        if (file == null || !file.isFile()) {
            JOptionPane.showMessageDialog(this, "Sorry, Certificate Image Does Not Exist.", "View Certificate", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            JOptionPane.showMessageDialog(this,
                    "Opening files is not supported on this system.",
                    "View Certificate", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not open the certificate file.",
                    "View Certificate", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEdit(Certificate c) {
        dispose();
        new EditCertificates(userName, c.scid).setVisible(true);
    }
    private void handleDelete() {
        if (selected == null) return;
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this certificate?\n\n" + selected.name + "\n\nThis action cannot be undone.", "Delete Certificate",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM certificate_details WHERE scid = ? AND user_id = ?";
        try (Connection con = dbConnect.getConnection()) {
            int userId = CertificateFormPanel.findUserId(con, userName);
            if (userId < 0) {
                JOptionPane.showMessageDialog(this, "Session invalid. Please login again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, selected.scid);
                ps.setInt(2, userId);
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Certificate deleted successfully.", "Delete Certificate", JOptionPane.INFORMATION_MESSAGE);
                    loadCertificates();
                } else {
                    JOptionPane.showMessageDialog(this, "Certificate could not be deleted. Please try again.", "Delete Certificate", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Delete Certificate", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCertificates() {
        certificates.clear();
        model.setRowCount(0);
        String sql = "SELECT * FROM certificate_details WHERE user_id = ? ORDER BY issue_date DESC, scid DESC";
        try (Connection con = dbConnect.getConnection()) {
            int userId = CertificateFormPanel.findUserId(con, userName);
            if (userId < 0) return;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Certificate c = new Certificate();
                        c.scid = rs.getInt("scid");
                        c.name = rs.getString("certificate_name");
                        c.issuedBy = rs.getString("issued_by");
                        c.issueDate = rs.getDate("issue_date").toLocalDate();
                        c.type = rs.getString("achievement_type");
                        c.category = rs.getString("category");
                        c.mode = rs.getString("mode");
                        java.sql.Date expiry = rs.getDate("expiry_date");
                        c.expiryDate = expiry == null ? null : expiry.toLocalDate();
                        c.course = rs.getString("course_name");
                        c.certId = rs.getString("certificate_id");
                        c.grade = rs.getString("grade_score");
                        c.remarks = rs.getString("remarks");
                        c.image = rs.getString("certificate_path");
                        c.buildSearchText();
                        certificates.add(c);
                        model.addRow(new Object[]{c.name, c.issuedBy, c.issueDate, c.type, c.category, ""});
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "My Certificates", JOptionPane.ERROR_MESSAGE);
        }
        showDetails(null);
    }

    private static class Certificate {
        int scid;
        String name, issuedBy, type, category, mode;
        String course, certId, grade, remarks, image;
        LocalDate issueDate, expiryDate;
        String searchText;
        void buildSearchText() {
            searchText = (name + " " + issuedBy + " " + issueDate.format(DATE_FMT) + " " + type + " "
                    + category + " " + mode + " " + course + " " + certId + " " + grade)
                    .toLowerCase();
        }
    }
    private static class CellRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
            Object shown = value instanceof LocalDate ? ((LocalDate) value).format(DATE_FMT) : value;
            super.getTableCellRendererComponent(t, shown, sel, false, row, col);
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            return this;
        }
    }

    private static class CameraIcon implements Icon {
        private final Color color;
        CameraIcon(Color color) {
            this.color = color;
        }
        @Override public int getIconWidth() { return 20; }
        @Override public int getIconHeight() { return 16; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            g2.setColor(color);
            g2.fillRoundRect(6, 0, 8, 4, 2, 2);      // top bump
            g2.fillRoundRect(0, 3, 20, 13, 4, 4);    // body
            g2.setColor(Color.WHITE);
            g2.fillOval(6, 5, 8, 8);                 // lens ring
            g2.setColor(color);
            g2.fillOval(8, 7, 4, 4);                 // lens
            g2.dispose();
        }
    }

    private static class PencilIcon implements Icon {
        private final Color color;
        PencilIcon(Color color) {
            this.color = color;
        }
        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g2.drawLine(12, 3, 6, 9);
            java.awt.geom.Path2D.Double tip = new java.awt.geom.Path2D.Double();
            tip.moveTo(4.6, 7.6);
            tip.lineTo(7.4, 10.4);
            tip.lineTo(2.8, 12.2);
            tip.closePath();
            g2.fill(tip);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(7, 14, 15, 14);
            g2.dispose();
        }
    }
}