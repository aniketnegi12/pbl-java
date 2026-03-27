import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


// Rounded panel
class RoundedPanel extends JPanel {
    private final Color bg; private final int r;
    RoundedPanel(Color bg, int r) { this.bg = bg; this.r = r; setOpaque(false); }
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
        g2.dispose(); super.paintComponent(g);
    }
}

//  Rounded button

class RoundedButton extends JButton {
    private final Color n, h, p; private boolean ov, pr;
    RoundedButton(String t, Color n, Color h, Color p, Color fg) {
        super(t); this.n = n; this.h = h; this.p = p;
        setOpaque(false); setContentAreaFilled(false);
        setBorderPainted(false); setFocusPainted(false);
        setFont(new Font("SansSerif", Font.BOLD, 13)); setForeground(fg);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(10, 22, 10, 22));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e)  { ov = true;  repaint(); }
            public void mouseExited(MouseEvent e)   { ov = false; repaint(); }
            public void mousePressed(MouseEvent e)  { pr = true;  repaint(); }
            public void mouseReleased(MouseEvent e) { pr = false; repaint(); }
        });
    }
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(pr ? p : ov ? h : n);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose(); super.paintComponent(g);
    }
}

// Rounded text field 
class StyledField extends JTextField {
    private final String hint; private boolean focused;
    StyledField(String hint) {
        this.hint = hint; setOpaque(false);
        setForeground(new Color(242, 242, 247));
        setCaretColor(new Color(10, 132, 255));
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setSelectionColor(new Color(10, 132, 255, 120));
        setSelectedTextColor(Color.WHITE);
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { focused = true;  repaint(); }
            public void focusLost(FocusEvent e)   { focused = false; repaint(); }
        });
    }
    public boolean isOpaque() { return false; }
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(44, 44, 46));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
        g2.setColor(focused ? new Color(10, 132, 255) : new Color(72, 72, 74));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
        g2.dispose();
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setColor(new Color(142, 142, 147)); g3.setFont(getFont());
            FontMetrics fm = g3.getFontMetrics();
            g3.drawString(hint, 12, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g3.dispose();
        }
        super.paintComponent(g);
    }
}

//  Rounded password field
class StyledPassField extends JPasswordField {
    private final String hint; private boolean focused;
    StyledPassField(String hint) {
        this.hint = hint; setOpaque(false);
        setForeground(new Color(242, 242, 247));
        setCaretColor(new Color(10, 132, 255));
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setSelectionColor(new Color(10, 132, 255, 120));
        setSelectedTextColor(Color.WHITE);
        addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { focused = true;  repaint(); }
            public void focusLost(FocusEvent e)   { focused = false; repaint(); }
        });
    }
    public boolean isOpaque() { return false; }
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(44, 44, 46));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.setStroke(new BasicStroke(focused ? 1.5f : 1f));
        g2.setColor(focused ? new Color(10, 132, 255) : new Color(72, 72, 74));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
        g2.dispose();
        if (getPassword().length == 0 && !isFocusOwner()) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setColor(new Color(142, 142, 147)); g3.setFont(getFont());
            FontMetrics fm = g3.getFontMetrics();
            g3.drawString(hint, 12, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g3.dispose();
        }
        super.paintComponent(g);
    }
}

//  Result card
class ResultCard extends RoundedPanel {
    private final JLabel val;
    ResultCard(String title, Color valColor) {
        super(new Color(44, 44, 46), 14);
        setLayout(new BorderLayout(0, 6)); setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 10));
        t.setForeground(new Color(142, 142, 147));
        val = new JLabel("—");
        val.setFont(new Font("SansSerif", Font.BOLD, 22));
        val.setForeground(valColor);
        add(t, BorderLayout.NORTH); add(val, BorderLayout.CENTER);
    }
    public void setValue(String v) { val.setText(v); repaint(); }
    public String getValue()       { return val.getText(); }
}


class LoginPage extends JPanel {

    static final Color BG1  = new Color(28,  28,  30);
    static final Color BG2  = new Color(44,  44,  46);
    static final Color BLUE = new Color(10,  132, 255);
    static final Color T1   = new Color(242, 242, 247);
    static final Color T2   = new Color(174, 174, 178);
    static final Color T3   = new Color(142, 142, 147);
    static final Color ERR  = new Color(255,  69,  58);

    // Demo credentials
    static final String VALID_USER = "demo";
    static final String VALID_PASS = "demo";

    private StyledField    tfUser;
    private StyledPassField tfPass;
    private JLabel        lblError;
    private Runnable      onSuccess;

    LoginPage(Runnable onSuccess) {
        this.onSuccess = onSuccess;
        setOpaque(false);
        setLayout(new GridBagLayout()); // center the card
        add(buildCard());
    }

    protected void paintComponent(Graphics g) {
        g.setColor(BG1); g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    private JPanel buildCard() {
        RoundedPanel card = new RoundedPanel(BG2, 18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 40, 36, 40));
        card.setPreferredSize(new Dimension(360, 420));

        // Logo circle
        JPanel logo = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLUE); g2.fillOval(0, 0, 56, 56);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String s = "₹";
                g2.drawString(s, (56 - fm.stringWidth(s)) / 2, (56 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        logo.setOpaque(false); logo.setPreferredSize(new Dimension(56, 56));
        logo.setMaximumSize(new Dimension(56, 56)); logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel title = new JLabel("AI Pricing System");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(T1); title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(T3); sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Error label
        lblError = new JLabel(" ");
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblError.setForeground(ERR); lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        tfUser = new StyledField("Username");
        tfPass = new StyledPassField("Password");
        tfUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tfPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // User label
        JLabel uLabel = new JLabel("Username");
        uLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        uLabel.setForeground(T2); uLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pLabel = new JLabel("Password");
        pLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pLabel.setForeground(T2); pLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button
        RoundedButton btnLogin = new RoundedButton("Sign In",
                BLUE, new Color(0, 122, 255), new Color(0, 100, 210), Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> tryLogin());

        // Enter key on password
        tfPass.addActionListener(e -> tryLogin());
        tfUser.addActionListener(e -> tfPass.requestFocus());

        // Hint
        JLabel hint = new JLabel("Demo: demo / demo");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(T3); hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Build card
        card.add(logo);
        card.add(Box.createVerticalStrut(18));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(sub);
        card.add(Box.createVerticalStrut(24));
        card.add(uLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(tfUser);
        card.add(Box.createVerticalStrut(14));
        card.add(pLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(tfPass);
        card.add(Box.createVerticalStrut(6));
        card.add(lblError);
        card.add(Box.createVerticalStrut(10));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(16));
        card.add(hint);

        return card;
    }

    private void tryLogin() {
        String user = tfUser.getText().trim();
        String pass = new String(tfPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Please enter username and password.");
            return;
        }
        if (user.equals(VALID_USER) && pass.equals(VALID_PASS)) {
            lblError.setText(" ");
            onSuccess.run(); // switch to main dashboard
        } else {
            lblError.setText("Incorrect username or password.");
            tfPass.setText("");
            tfPass.requestFocus();
        }
    }
}


class DashboardPage extends JPanel {

    static final Color BG1   = new Color(28,  28,  30);
    static final Color BG2   = new Color(44,  44,  46);
    static final Color BG3   = new Color(58,  58,  60);
    static final Color BLUE  = new Color(10,  132, 255);
    static final Color GREEN = new Color(48,  209,  88);
    static final Color AMBER = new Color(255, 159,  10);
    static final Color T1    = new Color(242, 242, 247);
    static final Color T2    = new Color(174, 174, 178);
    static final Color T3    = new Color(142, 142, 147);

    private StyledField        tfName, tfCost, tfCompetitors, tfMargin;
    private JComboBox<String> cbDemand, cbSeason;
    private ResultCard        cardPrice, cardProfit, cardPct, cardSat;
    private JLabel            lblEngine, lblInsight;

    DashboardPage() {
        setOpaque(false);
        setLayout(new BorderLayout());
        add(titleBar(), BorderLayout.NORTH);
        add(body(),     BorderLayout.CENTER);
        add(footer(),   BorderLayout.SOUTH);
    }

    protected void paintComponent(Graphics g) {
        g.setColor(BG1); g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    // Title bar
    private JPanel titleBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false); p.setBorder(new EmptyBorder(22, 28, 0, 28));

        JLabel title = new JLabel("Product Pricing");
        title.setFont(new Font("SansSerif", Font.BOLD, 26)); title.setForeground(T1);

        JLabel sub = new JLabel("AI-Enabled Online Product Price & Profit Extension System");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12)); sub.setForeground(T3);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 3));
        left.setOpaque(false); left.add(title); left.add(sub);

        JLabel badge = new JLabel("● AI READY");
        badge.setFont(new Font("SansSerif", Font.BOLD, 11)); badge.setForeground(GREEN);

        p.add(left, BorderLayout.WEST); p.add(badge, BorderLayout.EAST);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false); wrap.add(p, BorderLayout.CENTER);
        JSeparator sep = new JSeparator(); sep.setForeground(BG3);
        sep.setBorder(new EmptyBorder(14, 0, 0, 0));
        wrap.add(sep, BorderLayout.SOUTH);
        return wrap;
    }

    // Body
    private JPanel body() {
        JPanel p = new JPanel(new GridLayout(1, 2, 14, 0));
        p.setOpaque(false); p.setBorder(new EmptyBorder(14, 28, 10, 28));
        p.add(inputPanel()); p.add(outputPanel());
        return p;
    }

    // Input panel
    private JPanel inputPanel() {
        RoundedPanel card = new RoundedPanel(BG2, 16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        tfName        = new StyledField("e.g. Nike Shoes");
        tfCost        = new StyledField("e.g. 800");
        tfCompetitors = new StyledField("e.g. 1100, 1200, 1050");
        tfMargin      = new StyledField("e.g. 20");
        cbDemand      = combo(new String[]{"HIGH", "VERY_HIGH", "MEDIUM", "LOW", "VERY_LOW"});
        cbSeason      = combo(new String[]{"NORMAL", "PEAK", "OFF"});

        card.add(sectionLabel("PRODUCT DETAILS"));
        card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Product Name",      tfName));        card.add(vgap());
        card.add(fieldRow("Cost Price (₹)",    tfCost));        card.add(vgap());
        card.add(fieldRow("Competitor Prices", tfCompetitors)); card.add(vgap());
        card.add(fieldRow("Profit Margin %",   tfMargin));      card.add(vgap());
        card.add(fieldRow("Demand Level",      cbDemand));      card.add(vgap());
        card.add(fieldRow("Season",            cbSeason));
        card.add(Box.createVerticalStrut(18));

        RoundedButton btnCalc = new RoundedButton("Calculate Price",
                BLUE, new Color(0, 122, 255), new Color(0, 100, 210), Color.WHITE);
        btnCalc.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCalc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnCalc.addActionListener(e -> onCalculate());
        card.add(btnCalc);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false); wrap.add(card, BorderLayout.CENTER);
        return wrap;
    }

    // Output panel 
    private JPanel outputPanel() {
        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setOpaque(false);

        JPanel grid = new JPanel(new GridLayout(2, 2, 10, 10));
        grid.setOpaque(false);
        cardPrice  = new ResultCard("SUGGESTED PRICE", GREEN);
        cardProfit = new ResultCard("EXPECTED PROFIT",  BLUE);
        cardPct    = new ResultCard("PROFIT %",         T1);
        cardSat    = new ResultCard("SATISFACTION",     AMBER);
        grid.add(cardPrice); grid.add(cardProfit);
        grid.add(cardPct);   grid.add(cardSat);

        RoundedPanel insCard = new RoundedPanel(BG2, 14);
        insCard.setLayout(new BorderLayout(0, 8));
        insCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel insTitle = new JLabel("AI INSIGHT");
        insTitle.setFont(new Font("SansSerif", Font.BOLD, 10)); insTitle.setForeground(T3);

        lblInsight = new JLabel("<html><body style='width:250px'>Run a calculation to see insight.</body></html>");
        lblInsight.setFont(new Font("SansSerif", Font.PLAIN, 13)); lblInsight.setForeground(T2);

        lblEngine = new JLabel("Engine: —");
        lblEngine.setFont(new Font("SansSerif", Font.PLAIN, 11)); lblEngine.setForeground(T3);

        insCard.add(insTitle,   BorderLayout.NORTH);
        insCard.add(lblInsight, BorderLayout.CENTER);
        insCard.add(lblEngine,  BorderLayout.SOUTH);

        wrap.add(grid,    BorderLayout.CENTER);
        wrap.add(insCard, BorderLayout.SOUTH);
        return wrap;
    }

    //Footer
    private JPanel footer() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        p.setOpaque(false); p.setBorder(new EmptyBorder(0, 28, 6, 28));

        RoundedButton btnClear  = new RoundedButton("Clear",       BG3, new Color(72,72,74), BG2, T2);
        RoundedButton btnReport = new RoundedButton("Save Report", BG3, new Color(72,72,74), BG2, BLUE);
        btnClear.addActionListener(e -> onClear());
        btnReport.addActionListener(e -> onSaveReport());
        p.add(btnClear); p.add(btnReport);
        return p;
    }

    private void onCalculate() {
        // Validate inputs only
        if (tfName.getText().trim().isEmpty())       { err("Product name is required.");          return; }
        if (tfCost.getText().trim().isEmpty())        { err("Cost price is required.");            return; }
        if (tfCompetitors.getText().trim().isEmpty()) { err("Enter at least one competitor price."); return; }
        if (tfMargin.getText().trim().isEmpty())      { err("Profit margin is required.");         return; }

        try { Double.parseDouble(tfCost.getText().trim()); }
        catch (Exception e) { err("Enter a valid cost price.");    return; }
        try { Double.parseDouble(tfMargin.getText().trim()); }
        catch (Exception e) { err("Enter a valid profit margin."); return; }
        try {
            for (String p : tfCompetitors.getText().split(","))
                Double.parseDouble(p.trim());
        } catch (Exception e) { err("Competitor prices format: 1100, 1200, 1050"); return; }

        displayResult("—", "—", "—", "—", "—", "Submit inputs to get a price recommendation.");
    }

    public void displayResult(String price, String profit, String pct,
                               String sat, String engine, String insight) {
        cardPrice.setValue(price); cardProfit.setValue(profit);
        cardPct.setValue(pct);     cardSat.setValue(sat);
        lblEngine.setText("Engine: " + engine);
        lblInsight.setText("<html><body style='width:250px'>" + insight + "</body></html>");
        repaint();
    }

    private void onClear() {
        tfName.setText(""); tfCost.setText("");
        tfCompetitors.setText(""); tfMargin.setText("");
        cbDemand.setSelectedIndex(0); cbSeason.setSelectedIndex(0);
        cardPrice.setValue("—"); cardProfit.setValue("—");
        cardPct.setValue("—");   cardSat.setValue("—");
        lblEngine.setText("Engine: —");
        lblInsight.setText("<html><body style='width:250px'>Run a calculation to see insight.</body></html>");
        repaint();
    }

    private void onSaveReport() {
        if (cardPrice.getValue().equals("—")) { err("Run a calculation first."); return; }
        String fn = "PricingReport_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(fn))) {
            pw.println("══════════════════════════════════");
            pw.println("  AI PRICING REPORT");
            pw.println("  " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            pw.println("══════════════════════════════════");
            pw.println("Product         : " + tfName.getText());
            pw.println("Cost Price      : ₹" + tfCost.getText());
            pw.println("Competitor Prices: " + tfCompetitors.getText());
            pw.println("Profit Margin   : " + tfMargin.getText() + "%");
            pw.println("Demand Level    : " + cbDemand.getSelectedItem());
            pw.println("Season          : " + cbSeason.getSelectedItem());
            pw.println("──────────────────────────────────");
            pw.println("Suggested Price : " + cardPrice.getValue());
            pw.println("Expected Profit : " + cardProfit.getValue());
            pw.println("Profit %        : " + cardPct.getValue());
            pw.println("Satisfaction    : " + cardSat.getValue());
            pw.println("Engine Used     : " + lblEngine.getText());
            pw.println("──────────────────────────────────");
            pw.println("Insight: " + lblInsight.getText().replaceAll("<[^>]+>", ""));
            pw.println("══════════════════════════════════");
            JOptionPane.showMessageDialog(null, "Saved as: " + fn, "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) { err("Save failed: " + e.getMessage()); }
    }

    //Helpers
    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 10)); l.setForeground(T3);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }
    private JPanel fieldRow(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12)); l.setForeground(T2);
        field.setPreferredSize(new Dimension(0, 36));
        p.add(l, BorderLayout.NORTH); p.add(field, BorderLayout.CENTER); return p;
    }
    private JComboBox<String> combo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(BG2); cb.setForeground(T1);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v,
                    int i, boolean sel, boolean focus) {
                super.getListCellRendererComponent(l, v, i, sel, focus);
                setBackground(sel ? BLUE : BG2); setForeground(T1);
                setBorder(new EmptyBorder(6, 12, 6, 12));
                setFont(new Font("SansSerif", Font.PLAIN, 13)); return this;
            }
        }); return cb;
    }
    private Component vgap() { return Box.createVerticalStrut(9); }
    private void err(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
public class Member4Module extends JFrame {

    private final CardLayout   cards = new CardLayout();
    private final JPanel       root  = new JPanel(cards);

    public Member4Module() {
        setTitle("AI Pricing System");
        setSize(800, 620);
        setMinimumSize(new Dimension(720, 560));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        root.setOpaque(false);

        // Create pages
        LoginPage     loginPage     = new LoginPage(this::showDashboard);
        DashboardPage dashboardPage = new DashboardPage();

        root.add(loginPage,     "LOGIN");
        root.add(dashboardPage, "DASHBOARD");

        cards.show(root, "LOGIN");

        setContentPane(root);
        setVisible(true);
    }

    private void showDashboard() {
        cards.show(root, "DASHBOARD");
        setSize(800, 620); // ensure size fits dashboard
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(Member4Module::new);
    }
}
