package ShopEase;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AdminLoginDialog extends JDialog {

    // ── Colours (match AdminFrame palette) ──────────────────────────────────
    private static final Color C_DARK    = decode("#0f3460");
    private static final Color C_DARKER  = decode("#16213e");
    private static final Color C_DARKEST = decode("#1a1a2e");
    private static final Color C_ACCENT  = decode("#e94560");
    private static final Color C_TEXT    = Color.WHITE;
    private static final Color C_SUB     = decode("#a8b2d8");
    private static final Color C_INPUT_BG= decode("#0d2137");

    private static Color decode(String hex) {
        try { return Color.decode(hex); } catch (Exception e) { return Color.DARK_GRAY; }
    }

    // ── State ────────────────────────────────────────────────────────────────
    private boolean loginSuccess = false;
    private final String ADMINS_FILE = "data/admins.txt";

    // ── Tab buttons ──────────────────────────────────────────────────────────
    private JButton btnTabLogin, btnTabSignUp;
    private JPanel  cardPanel;
    private CardLayout cards;

    // LOGIN fields
    private JTextField  loginUser;
    private JPasswordField loginPass;

    // SIGN-UP fields
    private JTextField     signUser;
    private JPasswordField signPass, signConfirm;

    // ─────────────────────────────────────────────────────────────────────────
    public AdminLoginDialog(Frame owner) {
        super(owner, "Admin Login", true);
        setSize(510, 520);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // ── Root ─────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_DARKEST);
        setContentPane(root);

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildTabs(),    BorderLayout.CENTER);
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setBackground(C_DARK);
        p.setBorder(new EmptyBorder(22, 0, 18, 0));

        JLabel title = new JLabel("Admin ByBy Money  |  Portal");
        title.setForeground(C_TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        p.add(title);
        return p;
    }

    // ── Tab strip + card panel ────────────────────────────────────────────────
    private JPanel buildTabs() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(C_DARKEST);

        // Tab strip
        JPanel strip = new JPanel(new GridLayout(1, 2));
        strip.setBackground(C_DARKER);

        btnTabLogin  = tabBtn("Login",   true);
        btnTabSignUp = tabBtn("Sign Up", false);

        btnTabLogin .addActionListener(e -> switchTab("LOGIN"));
        btnTabSignUp.addActionListener(e -> switchTab("SIGNUP"));

        strip.add(btnTabLogin);
        strip.add(btnTabSignUp);
        outer.add(strip, BorderLayout.NORTH);

        // Cards
        cards = new CardLayout();
        cardPanel = new JPanel(cards);
        cardPanel.setBackground(C_DARKEST);
        cardPanel.add(buildLoginCard(),  "LOGIN");
        cardPanel.add(buildSignUpCard(), "SIGNUP");
        outer.add(cardPanel, BorderLayout.CENTER);

        return outer;
    }

    private JButton tabBtn(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (active) {
            b.setBackground(C_ACCENT); b.setForeground(C_TEXT);
        } else {
            b.setBackground(C_DARK); b.setForeground(C_SUB);
        }
        b.setPreferredSize(new Dimension(0, 44));
        return b;
    }

    private void switchTab(String card) {
        cards.show(cardPanel, card);
        boolean login = card.equals("LOGIN");
        btnTabLogin .setBackground(login  ? C_ACCENT : C_DARK);
        btnTabLogin .setForeground(login  ? C_TEXT   : C_SUB);
        btnTabSignUp.setBackground(!login ? C_ACCENT : C_DARK);
        btnTabSignUp.setForeground(!login ? C_TEXT   : C_SUB);
    }

    // ── LOGIN card ────────────────────────────────────────────────────────────
    private JPanel buildLoginCard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_DARKEST);
        p.setBorder(new EmptyBorder(30, 50, 30, 50));

        loginUser = inputField();
        loginPass = passField();

        p.add(fieldLabel("Username"));
        p.add(Box.createVerticalStrut(6));
        p.add(loginUser);
        p.add(Box.createVerticalStrut(18));
        p.add(fieldLabel("Password"));
        p.add(Box.createVerticalStrut(6));
        p.add(loginPass);
        p.add(Box.createVerticalStrut(28));
        p.add(accentBtn("Login", e -> doLogin()));

        return p;
    }

    // ── SIGN-UP card ──────────────────────────────────────────────────────────
    private JPanel buildSignUpCard() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_DARKEST);
        p.setBorder(new EmptyBorder(30, 50, 30, 50));

        signUser    = inputField();
        signPass    = passField();
        signConfirm = passField();

        p.add(fieldLabel("Username"));
        p.add(Box.createVerticalStrut(6));
        p.add(signUser);
        p.add(Box.createVerticalStrut(16));

        p.add(fieldLabel("Password"));
        p.add(Box.createVerticalStrut(6));
        p.add(signPass);
        p.add(Box.createVerticalStrut(16));

        p.add(fieldLabel("Confirm Password"));
        p.add(Box.createVerticalStrut(6));
        p.add(signConfirm);
        p.add(Box.createVerticalStrut(26));

        p.add(accentBtn("Create Account", e -> doSignUp()));

        return p;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(C_SUB);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField inputField() {
        JTextField tf = new JTextField();
        styleInput(tf);
        return tf;
    }

    private JPasswordField passField() {
        JPasswordField pf = new JPasswordField();
        styleInput(pf);
        return pf;
    }

    private void styleInput(JTextField tf) {
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBackground(C_INPUT_BG);
        tf.setForeground(C_TEXT);
        tf.setCaretColor(C_TEXT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_ACCENT, 2),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    private JButton accentBtn(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.setBackground(C_ACCENT);
        b.setForeground(C_TEXT);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addActionListener(al);

        // Hover effect
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(C_ACCENT.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(C_ACCENT); }
        });
        return b;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────
    private void doLogin() {
        String user = loginUser.getText().trim();
        String pass = new String(loginPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            error("Please fill in both fields."); return;
        }
        if (checkCredentials(user, pass)) {
            loginSuccess = true;
            dispose();
        } else {
            error("Invalid username or password.");
        }
    }

    private void doSignUp() {
        String user    = signUser.getText().trim();
        String pass    = new String(signPass.getPassword()).trim();
        String confirm = new String(signConfirm.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            error("Please fill in all fields."); return;
        }
        if (!pass.equals(confirm)) {
            error("Passwords do not match."); return;
        }
        if (usernameExists(user)) {
            error("Username already taken."); return;
        }
        saveAdmin(user, pass);
        JOptionPane.showMessageDialog(this,
                "Account created! You can now log in.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        switchTab("LOGIN");
    }

    private boolean checkCredentials(String user, String pass) {
        try {
            File f = new File(ADMINS_FILE);
            if (!f.exists()) return false;
            for (String line : Files.readAllLines(f.toPath())) {
                String[] p = line.split("\\|");
                if (p.length >= 2 && p[0].trim().equals(user) && p[1].trim().equals(pass))
                    return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    private boolean usernameExists(String user) {
        try {
            File f = new File(ADMINS_FILE);
            if (!f.exists()) return false;
            for (String line : Files.readAllLines(f.toPath())) {
                String[] p = line.split("\\|");
                if (p.length >= 1 && p[0].trim().equals(user)) return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    private void saveAdmin(String user, String pass) {
        try {
            new File("data").mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(ADMINS_FILE, true))) {
                pw.println(user + "|" + pass);
            }
        } catch (IOException e) { error("Could not save account: " + e.getMessage()); }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isLoginSuccess() { return loginSuccess; }
}
