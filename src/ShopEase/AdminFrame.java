package ShopEase;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.List;

public class AdminFrame extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────────────
    static final Color C_DARK    = decode("#0f3460");
    static final Color C_DARKER  = decode("#16213e");
    static final Color C_DARKEST = decode("#1a1a2e");
    static final Color C_ACCENT  = decode("#e94560");
    static final Color C_ACCENT2 = decode("#f5a623");
    static final Color C_TEXT    = Color.WHITE;
    static final Color C_SUB     = decode("#a8b2d8");
    static final Color C_PANEL   = decode("#16213e");
    static final Color C_GREEN   = decode("#2ecc71");
    static final Color C_CARD_BG = decode("#1e2a45");

    private static Color decode(String hex) {
        try { return Color.decode(hex); } catch (Exception e) { return Color.DARK_GRAY; }
    }

    // ── State ─────────────────────────────────────────────────────────────────
    private CardLayout   cardLayout;
    private JPanel       centerPanel;
    private boolean      editMode   = false;

    private ProductCatalog catalog;
    private OrderManager   orderManager;

    // Dashboard labels
    private JLabel lblTotalProducts, lblTotalOrders, lblLowStock;

    // Products table
    private DefaultTableModel productsModel;
    private JTable            productsTable;

    // Add-product fields
    private JTextField   tfName, tfPrice, tfStock, tfImageUrl;
    private JComboBox<String> cbCategory;
    private JButton      btnSave;

    // Orders table
    private DefaultTableModel ordersModel;

    // Search widgets
    private JTextField tfSearchId;
    private JLabel     lblSearchImg, lblResName, lblResId, lblResCategory,
            lblResPrice, lblResStock, lblResUrl;
    private JPanel     searchResultCard;

    // ─────────────────────────────────────────────────────────────────────────
    public AdminFrame(ProductCatalog catalog, OrderManager orderManager) {
        super("BeyBye Money");
        this.catalog      = catalog;
        this.orderManager = orderManager;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 730);
        setMinimumSize(new Dimension(950, 620));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_DARKEST);
        setContentPane(root);

        root.add(buildTopBar(),  BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        cardLayout  = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setBackground(C_DARKEST);

        centerPanel.add(buildDashboardPanel(),   "Dashboard");
        centerPanel.add(buildProductsPanel(),    "Products");
        centerPanel.add(buildAddProductPanel(),  "Add Product");
        centerPanel.add(buildSearchPanel(),      "Search");
        centerPanel.add(buildOrdersPanel(),      "Orders");
        centerPanel.add(buildAnalyticsPanel(),   "Analytics");

        root.add(centerPanel, BorderLayout.CENTER);

        refreshDashboard();
        setVisible(true);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, C_DARK, getWidth(), 0, decode("#1a1f5e"));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel("Money Bos");
        titleLbl.setForeground(C_TEXT);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel sub = new JLabel("Management Console — Full Control");
        sub.setForeground(C_ACCENT2);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        left.add(titleLbl);
        left.add(sub);
        p.add(left, BorderLayout.WEST);

        JLabel badge = new JLabel("  ONLINE  ");
        badge.setForeground(C_GREEN);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_GREEN, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        p.add(badge, BorderLayout.EAST);

        return p;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, C_DARKER, 0, getHeight(), C_DARKEST);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(210, 0));
        p.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel navLabel = new JLabel("  NAVIGATION");
        navLabel.setForeground(C_ACCENT2);
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(Box.createVerticalStrut(6));
        p.add(navLabel);
        p.add(Box.createVerticalStrut(10));

        String[][] items = {
            {"[*]", "Dashboard"},
            {"[P]", "Products"},
            {"[+]", "Add Product"},
            {"[?]", "Search"},
            {"[$]", "Orders"},
            {"[~]", "Analytics"}
        };
        for (String[] item : items) {
            p.add(sidebarButton(item[0], item[1]));
            p.add(Box.createVerticalStrut(6));
        }
        p.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("BeyBye Money");
        footer.setForeground(C_ACCENT);
        footer.setFont(new Font("Segoe UI", Font.BOLD, 11));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(footer);
        p.add(Box.createVerticalStrut(8));

        return p;
    }

    private JButton sidebarButton(String icon, String name) {
        JButton b = new JButton(name) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(190, 46));
        b.setPreferredSize(new Dimension(190, 46));
        b.setBackground(C_DARK);
        b.setForeground(C_TEXT);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(C_ACCENT);
                b.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(C_DARK);
                b.setForeground(C_TEXT);
            }
        });
        b.addActionListener(e -> {
            cardLayout.show(centerPanel, name);
            if (name.equals("Dashboard")) refreshDashboard();
            if (name.equals("Products"))  refreshProductsTable();
            if (name.equals("Orders"))    refreshOrdersTable();
            if (name.equals("Search"))    clearSearchPanel();
            if (name.equals("Analytics")) refreshAnalytics();
        });
        return b;
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────
    private JPanel buildDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_DARKEST);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        JLabel title = sectionTitle("Dashboard Overview");
        JLabel sub = new JLabel("Welcome back, Wealthy!!");
        sub.setForeground(C_ACCENT2);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(sub,   BorderLayout.EAST);
        p.add(headerRow, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(24, 0, 0, 0));

        lblTotalProducts = bigLabel("--", C_TEXT);
        lblTotalOrders   = bigLabel("--", C_TEXT);
        lblLowStock      = bigLabel("--", C_ACCENT);

        cards.add(statCard("Total Products",  lblTotalProducts, C_DARK));
        cards.add(statCard("Total Orders",    lblTotalOrders,   C_DARK));
        cards.add(statCard("Low Stock (< 5)", lblLowStock,      C_ACCENT));

        p.add(cards, BorderLayout.CENTER);

        JLabel tip = new JLabel("Tip: Keep stock above 5 units to avoid low-stock alerts!");
        tip.setForeground(C_SUB);
        tip.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        tip.setBorder(new EmptyBorder(20, 0, 0, 0));
        p.add(tip, BorderLayout.SOUTH);

        return p;
    }

    private JPanel statCard(String title, JLabel value, Color borderColor) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_CARD_BG, 0, getHeight(), C_PANEL);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 18, 18);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setForeground(C_SUB);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        value.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(t,     BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(C_TEXT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setBorder(new EmptyBorder(0, 0, 10, 0));
        return l;
    }

    private JLabel bigLabel(String text, Color c) {
        JLabel l = new JLabel(text);
        l.setForeground(c);
        l.setFont(new Font("Segoe UI", Font.BOLD, 42));
        return l;
    }

    // ── Products ──────────────────────────────────────────────────────────────
    private JPanel buildProductsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_DARKEST);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Product Inventory"), BorderLayout.NORTH);

        String[] cols = {"ID","Name","Category","Price","Stock","Edit","Delete"};
        productsModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c >= 5; }
        };

        productsTable = styledTable(productsModel);

        TableColumn editCol   = productsTable.getColumn("Edit");
        TableColumn deleteCol = productsTable.getColumn("Delete");
        editCol  .setCellRenderer(new BtnRenderer("Edit",   C_DARK));
        deleteCol.setCellRenderer(new BtnRenderer("Delete", C_ACCENT));
        editCol  .setCellEditor(new BtnEditor("Edit",   C_DARK,   i -> openEditForm(i)));
        deleteCol.setCellEditor(new BtnEditor("Delete", C_ACCENT, i -> confirmDelete(i)));

        productsTable.getColumnModel().getColumn(5).setMaxWidth(80);
        productsTable.getColumnModel().getColumn(6).setMaxWidth(80);

        JScrollPane sp = new JScrollPane(productsTable);
        sp.getViewport().setBackground(C_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(C_DARK, 1));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // ── Add / Edit Product ────────────────────────────────────────────────────
    private JPanel buildAddProductPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(C_DARKEST);
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));
        outer.add(sectionTitle("Add New Product"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_CARD_BG, 0, getHeight(), C_PANEL);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(28, 28, 28, 28));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(9, 9, 9, 9);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        tfName     = formField();
        tfPrice    = formField();
        tfStock    = formField();
        tfImageUrl = formField();
        cbCategory = new JComboBox<>(new String[]{
                "Electronics","Clothing","Books","Home & Garden","Sports","Other"});
        styleCombo(cbCategory);

        String[] rowLabels = {
                "Product ID", "Product Name", "Category",
                "Price ($)", "Stock Qty", "Image URL"
        };

        JComponent[] inputs = {
                new JLabel("(auto-assigned)") {{ setForeground(C_SUB); setFont(new Font("Segoe UI", Font.ITALIC, 13)); }},
                tfName, cbCategory, tfPrice, tfStock, tfImageUrl
        };

        for (int i = 0; i < inputs.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0.3;
            form.add(formLabel(rowLabels[i]), gc);
            gc.gridx = 1; gc.weightx = 0.7;
            form.add(inputs[i], gc);
        }

        gc.gridx = 1; gc.gridy = inputs.length; gc.weightx = 0;
        btnSave = accentButton("Save Product");
        btnSave.addActionListener(e -> saveProduct());
        form.add(btnSave, gc);

        outer.add(form, BorderLayout.CENTER);
        return outer;
    }

    // ── Search ────────────────────────────────────────────────────────────────
    private JPanel buildSearchPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 16));
        outer.setBackground(C_DARKEST);
        outer.setBorder(new EmptyBorder(24, 24, 24, 24));

        outer.add(sectionTitle("Search Product by ID"), BorderLayout.NORTH);

        // Search bar
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bar.setOpaque(false);

        JLabel idLbl = new JLabel("Product ID:");
        idLbl.setForeground(C_SUB);
        idLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tfSearchId = new JTextField(14);
        styleInputField(tfSearchId);

        JButton btnSearch = accentButton("Search");
        JButton btnClear  = darkButton("Clear");

        JLabel hint = new JLabel("  Enter a product ID and press Search");
        hint.setForeground(C_SUB);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        bar.add(idLbl);
        bar.add(tfSearchId);
        bar.add(btnSearch);
        bar.add(btnClear);
        bar.add(hint);

        // ── Result card ───────────────────────────────────────────────────────
        searchResultCard = new JPanel(new BorderLayout(24, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_CARD_BG, getWidth(), getHeight(), C_PANEL);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(C_ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
            }
        };
        searchResultCard.setOpaque(false);
        searchResultCard.setBorder(new EmptyBorder(24, 24, 24, 24));
        searchResultCard.setVisible(false);

        // ── LEFT: Image — HIGH QUALITY 280x280 ───────────────────────────────
        lblSearchImg = new JLabel("No Image", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                GradientPaint bg = new GradientPaint(0, 0, C_DARKEST, getWidth(), getHeight(), C_DARK);
                g2.setPaint(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setClip(new RoundRectangle2D.Float(2, 2, getWidth()-4, getHeight()-4, 18, 18));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        lblSearchImg.setPreferredSize(new Dimension(280, 280));
        lblSearchImg.setMinimumSize  (new Dimension(280, 280));
        lblSearchImg.setMaximumSize  (new Dimension(280, 280));
        lblSearchImg.setHorizontalAlignment(SwingConstants.CENTER);
        lblSearchImg.setVerticalAlignment  (SwingConstants.CENTER);
        lblSearchImg.setForeground(C_SUB);
        lblSearchImg.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSearchImg.setOpaque(false);
        lblSearchImg.setBorder(BorderFactory.createLineBorder(C_ACCENT, 2));

        JPanel imgWrapper = new JPanel(new GridBagLayout());
        imgWrapper.setOpaque(false);
        imgWrapper.add(lblSearchImg);

        // ── RIGHT: Info panel — NAME FIRST AND BIG ────────────────────────────
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        // Product name — PROMINENT, FIRST
        lblResName = new JLabel(" ");
        lblResName.setForeground(C_TEXT);
        lblResName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblResName.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Colored separator under name
        JSeparator sep = new JSeparator() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, C_ACCENT, getWidth()/2, 0, C_ACCENT2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 3);
                g2.dispose();
            }
        };
        sep.setPreferredSize(new Dimension(200, 3));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 3));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblResId       = resultLbl();
        lblResCategory = resultLbl();
        lblResPrice    = resultLbl();
        lblResPrice.setForeground(C_ACCENT2);
        lblResPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblResStock    = resultLbl();
        lblResStock.setForeground(C_GREEN);
        lblResUrl      = resultLbl();
        lblResUrl.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        info.add(lblResName);
        info.add(Box.createVerticalStrut(4));
        info.add(sep);
        info.add(Box.createVerticalStrut(16));
        info.add(infoRow("ID:",        lblResId));
        info.add(Box.createVerticalStrut(10));
        info.add(infoRow("Category:",  lblResCategory));
        info.add(Box.createVerticalStrut(10));
        info.add(infoRow("Price:",     lblResPrice));
        info.add(Box.createVerticalStrut(10));
        info.add(infoRow("Stock:",     lblResStock));
        info.add(Box.createVerticalStrut(10));
        info.add(infoRow("Image URL:", lblResUrl));

        searchResultCard.add(imgWrapper, BorderLayout.WEST);
        searchResultCard.add(info,       BorderLayout.CENTER);

        btnSearch.addActionListener(e -> performSearch());
        btnClear .addActionListener(e -> clearSearchPanel());
        tfSearchId.addActionListener(e -> performSearch());

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(bar,              BorderLayout.NORTH);
        center.add(searchResultCard, BorderLayout.CENTER);

        outer.add(center, BorderLayout.CENTER);
        return outer;
    }

    private JLabel resultLbl() {
        JLabel l = new JLabel();
        l.setForeground(C_TEXT);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel infoRow(String label, JLabel value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(C_SUB);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setPreferredSize(new Dimension(90, 20));
        row.add(lbl);
        row.add(value);
        return row;
    }

    private void performSearch() {
        String raw = tfSearchId.getText().trim();
        if (raw.isEmpty()) {
            showError("Please enter a Product ID."); return;
        }
        int id;
        try { id = Integer.parseInt(raw); }
        catch (NumberFormatException e) { showError("Product ID must be a number."); return; }

        try {
            Product p = catalog.searchById(id);
            displaySearchResult(p);
        } catch (ProductNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Product with ID [" + id + "] was not found.",
                    "Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void displaySearchResult(Product p) {
        // ── Product name FIRST, prominently ──────────────────────────────────
        lblResName    .setText(p.getName());
        lblResId      .setText(String.valueOf(p.getProductId()));
        lblResCategory.setText(p.getCategory());
        lblResPrice   .setText(String.format("$%.2f", p.getPrice()));
        lblResStock   .setText(p.getStockQty() + " units");
        lblResUrl     .setText(p.getImageUrl());

        lblSearchImg.setIcon(null);
        lblSearchImg.setText("Loading...");

        searchResultCard.setVisible(true);
        searchResultCard.revalidate();
        searchResultCard.repaint();

        String url = p.getImageUrl();
        if (url == null || url.isBlank()) {
            lblSearchImg.setText("No Image URL");
            return;
        }

        // ── HIGH QUALITY async image load with multi-step scaling ─────────────
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            protected ImageIcon doInBackground() throws Exception {
                BufferedImage raw = ImageIO.read(URI.create(url).toURL());
                if (raw == null) return null;

                int targetW = 272, targetH = 272;
                double scaleX = (double) targetW / raw.getWidth();
                double scaleY = (double) targetH / raw.getHeight();
                double scale  = Math.min(scaleX, scaleY);
                int finalW = (int)(raw.getWidth()  * scale);
                int finalH = (int)(raw.getHeight() * scale);

                // Multi-step downscale for much better quality
                BufferedImage current = raw;
                int curW = raw.getWidth();
                int curH = raw.getHeight();

                while (curW > finalW * 2 || curH > finalH * 2) {
                    int stepW = Math.max(curW / 2, finalW);
                    int stepH = Math.max(curH / 2, finalH);
                    BufferedImage step = new BufferedImage(stepW, stepH, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2s = step.createGraphics();
                    g2s.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2s.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);
                    g2s.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2s.drawImage(current, 0, 0, stepW, stepH, null);
                    g2s.dispose();
                    current = step;
                    curW = stepW;
                    curH = stepH;
                }

                // Final pass with rounded clip
                BufferedImage scaled = new BufferedImage(finalW, finalH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = scaled.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                        RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2.setClip(new RoundRectangle2D.Float(0, 0, finalW, finalH, 16, 16));
                g2.drawImage(current, 0, 0, finalW, finalH, null);
                g2.dispose();
                return new ImageIcon(scaled);
            }

            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        lblSearchImg.setIcon(icon);
                        lblSearchImg.setText(null);
                    } else {
                        lblSearchImg.setText("Image Error");
                    }
                } catch (Exception ex) {
                    lblSearchImg.setText("No Image");
                }
                lblSearchImg.revalidate();
                lblSearchImg.repaint();
            }
        };
        worker.execute();
    }

    private void clearSearchPanel() {
        tfSearchId.setText("");
        lblSearchImg.setIcon(null);
        lblSearchImg.setText("No Image");
        searchResultCard.setVisible(false);
        tfSearchId.requestFocus();
    }

    // ── Orders ────────────────────────────────────────────────────────────────
    private JPanel buildOrdersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_DARKEST);
        p.setBorder(new EmptyBorder(24, 24, 24, 24));
        p.add(sectionTitle("Orders"), BorderLayout.NORTH);

        ordersModel = new DefaultTableModel(
                new String[]{"Order ID","Items Count","Total Amount ($)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JScrollPane sp = new JScrollPane(styledTable(ordersModel));
        sp.getViewport().setBackground(C_PANEL);
        sp.setBorder(BorderFactory.createLineBorder(C_DARK, 1));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // ── Refresh helpers ───────────────────────────────────────────────────────
    private void refreshDashboard() {
        List<Product> all  = catalog.getAllProducts();
        long low = all.stream().filter(pr -> pr.getStockQty() < 5).count();
        lblTotalProducts.setText(String.valueOf(all.size()));
        lblTotalOrders  .setText(String.valueOf(orderManager.getOrderHistory().size()));
        lblLowStock     .setText(String.valueOf(low));
    }

    private void refreshProductsTable() {
        productsModel.setRowCount(0);
        for (Product p : catalog.getAllProducts()) {
            productsModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getCategory(),
                    p.getPrice(), p.getStockQty(), "Edit", "Delete"
            });
        }
    }

    private void refreshOrdersTable() {
        ordersModel.setRowCount(0);
        for (Order o : orderManager.getOrderHistory()) {
            ordersModel.addRow(new Object[]{
                    o.getOrderId(), o.getItems().size(), o.getTotalAmount()
            });
        }
    }

    // ── Product CRUD ──────────────────────────────────────────────────────────
    private void openEditForm(int rowIndex) {
        editMode = true;
        btnSave.setText("Update Product");
        cardLayout.show(centerPanel, "Add Product");

        Object id    = productsModel.getValueAt(rowIndex, 0);
        Object name  = productsModel.getValueAt(rowIndex, 1);
        Object cat   = productsModel.getValueAt(rowIndex, 2);
        Object price = productsModel.getValueAt(rowIndex, 3);
        Object stock = productsModel.getValueAt(rowIndex, 4);

        String img = "";
        try {
            Product p = catalog.searchById((int)id);
            img = p.getImageUrl();
        } catch (Exception ignored) {}

        tfName    .setText(name.toString());
        cbCategory.setSelectedItem(cat.toString());
        tfPrice   .setText(price.toString().replace(",","."));
        tfStock   .setText(stock.toString());
        tfImageUrl.setText(img);
        tfName.setEditable(true);
    }

    private void confirmDelete(int rowIndex) {
        Object id   = productsModel.getValueAt(rowIndex, 0);
        Object name = productsModel.getValueAt(rowIndex, 1);
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete product \"" + name + "\" (ID: " + id + ")?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            catalog.removeProduct((int) id);
            FileManager.saveProducts(catalog.getAllProducts(), "data/products.txt");
            refreshProductsTable();
        } catch (ProductNotFoundException e) {
            showError(e.getMessage());
        }
    }

    private void saveProduct() {
        String name     = tfName.getText().trim();
        String category = (String) cbCategory.getSelectedItem();
        String priceStr = tfPrice.getText().trim();
        String stockStr = tfStock.getText().trim();
        String imageUrl = tfImageUrl.getText().trim();

        if (name.isEmpty())    { showError("Product name cannot be empty."); return; }
        double price;
        try { price = Double.parseDouble(priceStr); if (price < 0) throw new Exception(); }
        catch (Exception e) { showError("Price must be a positive number."); return; }
        int stock;
        try { stock = Integer.parseInt(stockStr); if (stock < 0) throw new Exception(); }
        catch (Exception e) { showError("Stock must be a non-negative integer."); return; }

        try {
            if (editMode) {
                int selectedRow = productsTable.getSelectedRow();
                int id = (int) productsModel.getValueAt(selectedRow, 0);
                catalog.removeProduct(id);
                catalog.addProduct(new Product(id, name, category, price, stock, imageUrl));
            } else {
                int newId = catalog.getAllProducts().stream()
                        .mapToInt(Product::getProductId).max().orElse(0) + 1;
                catalog.addProduct(new Product(newId, name, category, price, stock, imageUrl));
            }
            FileManager.saveProducts(catalog.getAllProducts(), "data/products.txt");
            JOptionPane.showMessageDialog(this,
                    "Product " + (editMode ? "updated" : "added") + " successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearAddForm();
            editMode = false;
            btnSave.setText("Save Product");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void clearAddForm() {
        tfName.setText(""); tfPrice.setText(""); tfStock.setText(""); tfImageUrl.setText("");
        cbCategory.setSelectedIndex(0);
    }

    // ── Styled table ──────────────────────────────────────────────────────────
    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? C_PANEL : C_CARD_BG);
                    c.setForeground(C_TEXT);
                }
                return c;
            }
        };
        t.setBackground(C_PANEL);
        t.setForeground(C_TEXT);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setGridColor(decode("#1e2a45"));
        t.setRowHeight(38);
        t.setSelectionBackground(C_ACCENT);
        t.setSelectionForeground(C_TEXT);
        t.setFillsViewportHeight(true);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader h = t.getTableHeader();
        h.setBackground(C_DARK);
        h.setForeground(C_ACCENT2);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 42));
        return t;
    }

    // ── Field / Button builders ───────────────────────────────────────────────
    private JTextField formField() {
        JTextField tf = new JTextField();
        styleInputField(tf);
        return tf;
    }

    private void styleInputField(JTextField tf) {
        tf.setBackground(decode("#0d2137"));
        tf.setForeground(C_TEXT);
        tf.setCaretColor(C_ACCENT2);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_ACCENT, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(decode("#0d2137"));
        cb.setForeground(C_TEXT);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(C_SUB);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private JButton accentButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_ACCENT, getWidth(), getHeight(), decode("#c0392b"));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setForeground(C_TEXT);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        return b;
    }

    private JButton darkButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(C_DARK);
        b.setForeground(C_SUB);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── Analytics ─────────────────────────────────────────────────────────────
    private JPanel analyticsPanel;
    private JPanel barChartPanel;
    private JPanel topProductsPanel;
    private JLabel lblTotalValue, lblAvgPrice, lblLowPct, lblCategoryCount;

    private JPanel buildAnalyticsPanel() {
        analyticsPanel = new JPanel(new BorderLayout(0, 16));
        analyticsPanel.setBackground(C_DARKEST);
        analyticsPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        analyticsPanel.add(sectionTitle("Analytics & Insights"), BorderLayout.NORTH);

        // ── Top summary row ───────────────────────────────────────────────────
        JPanel summaryRow = new JPanel(new GridLayout(1, 4, 14, 0));
        summaryRow.setOpaque(false);

        lblTotalValue    = bigLabel("--", C_ACCENT2);
        lblAvgPrice      = bigLabel("--", C_GREEN);
        lblLowPct        = bigLabel("--", C_ACCENT);
        lblCategoryCount = bigLabel("--", C_SUB);

        summaryRow.add(statCard("Total Inventory Value",   lblTotalValue,    C_ACCENT2));
        summaryRow.add(statCard("Avg Product Price",       lblAvgPrice,      C_GREEN));
        summaryRow.add(statCard("Low Stock %",             lblLowPct,        C_ACCENT));
        summaryRow.add(statCard("Categories",              lblCategoryCount, C_DARK));

        // ── Bottom: bar chart + top products ──────────────────────────────────
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 14, 0));
        bottomRow.setOpaque(false);

        barChartPanel    = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart((Graphics2D) g);
            }
        };
        barChartPanel.setBackground(C_CARD_BG);
        barChartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_DARK, 2),
                new EmptyBorder(16, 16, 16, 16)));
        barChartPanel.setPreferredSize(new Dimension(0, 260));

        topProductsPanel = new JPanel();
        topProductsPanel.setBackground(C_CARD_BG);
        topProductsPanel.setLayout(new BoxLayout(topProductsPanel, BoxLayout.Y_AXIS));
        topProductsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_DARK, 2),
                new EmptyBorder(16, 16, 16, 16)));

        bottomRow.add(barChartPanel);
        bottomRow.add(topProductsPanel);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 14));
        center.setOpaque(false);
        center.add(summaryRow);
        center.add(bottomRow);

        analyticsPanel.add(center, BorderLayout.CENTER);
        return analyticsPanel;
    }

    private void refreshAnalytics() {
        List<Product> all = catalog.getAllProducts();
        if (all.isEmpty()) {
            lblTotalValue.setText("$0");
            lblAvgPrice.setText("$0");
            lblLowPct.setText("0%");
            lblCategoryCount.setText("0");
            topProductsPanel.removeAll();
            barChartPanel.repaint();
            return;
        }

        // ── Summary numbers ───────────────────────────────────────────────────
        double totalValue = all.stream()
                .mapToDouble(p -> p.getPrice() * p.getStockQty()).sum();
        double avgPrice   = all.stream().mapToDouble(Product::getPrice).average().orElse(0);
        long   lowCount   = all.stream().filter(p -> p.getStockQty() < 5).count();
        double lowPct     = (lowCount * 100.0) / all.size();

        java.util.Set<String> cats = new java.util.LinkedHashSet<>();
        for (Product p : all) cats.add(p.getCategory());

        lblTotalValue   .setText(String.format("$%.0f", totalValue));
        lblAvgPrice     .setText(String.format("$%.1f", avgPrice));
        lblLowPct       .setText(String.format("%.0f%%", lowPct));
        lblCategoryCount.setText(String.valueOf(cats.size()));

        // ── Top products panel ────────────────────────────────────────────────
        topProductsPanel.removeAll();

        JLabel topTitle = new JLabel("Top 5 — Highest Value in Stock");
        topTitle.setForeground(C_ACCENT2);
        topTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        topProductsPanel.add(topTitle);
        topProductsPanel.add(Box.createVerticalStrut(10));

        // Sort by price × stock descending
        List<Product> sorted = new java.util.ArrayList<>(all);
        sorted.sort((a, b) -> Double.compare(
                b.getPrice() * b.getStockQty(),
                a.getPrice() * a.getStockQty()));

        Color[] rowColors = {C_ACCENT2, C_GREEN, C_TEXT, C_SUB, C_SUB};
        int limit = Math.min(5, sorted.size());
        for (int i = 0; i < limit; i++) {
            Product p = sorted.get(i);
            double val = p.getPrice() * p.getStockQty();

            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            JLabel rank = new JLabel("#" + (i + 1));
            rank.setForeground(rowColors[i]);
            rank.setFont(new Font("Segoe UI", Font.BOLD, 13));
            rank.setPreferredSize(new Dimension(28, 20));

            JLabel name = new JLabel(p.getName());
            name.setForeground(C_TEXT);
            name.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JLabel valLbl = new JLabel(String.format("$%.0f", val));
            valLbl.setForeground(rowColors[i]);
            valLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

            row.add(rank,   BorderLayout.WEST);
            row.add(name,   BorderLayout.CENTER);
            row.add(valLbl, BorderLayout.EAST);

            topProductsPanel.add(row);
            topProductsPanel.add(Box.createVerticalStrut(8));
        }

        // Low stock warning list
        topProductsPanel.add(Box.createVerticalStrut(6));
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(C_DARK);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        topProductsPanel.add(sep2);
        topProductsPanel.add(Box.createVerticalStrut(8));

        JLabel warnTitle = new JLabel("Low Stock Alerts (< 5 units)");
        warnTitle.setForeground(C_ACCENT);
        warnTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        warnTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        topProductsPanel.add(warnTitle);
        topProductsPanel.add(Box.createVerticalStrut(6));

        List<Product> low = catalog.getLowStockProducts();
        if (low.isEmpty()) {
            JLabel ok = new JLabel("All products well-stocked!");
            ok.setForeground(C_GREEN);
            ok.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            ok.setAlignmentX(Component.LEFT_ALIGNMENT);
            topProductsPanel.add(ok);
        } else {
            for (Product p : low) {
                JLabel lbl = new JLabel("  " + p.getName() + "  —  " + p.getStockQty() + " left");
                lbl.setForeground(C_ACCENT);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                topProductsPanel.add(lbl);
                topProductsPanel.add(Box.createVerticalStrut(4));
            }
        }

        topProductsPanel.revalidate();
        topProductsPanel.repaint();
        barChartPanel.repaint();
    }

    /** Draws a bar chart: products per category, painted directly with Graphics2D */
    private void drawBarChart(Graphics2D g2) {
        List<Product> all = catalog.getAllProducts();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = barChartPanel.getWidth();
        int H = barChartPanel.getHeight();
        int padL = 52, padR = 16, padT = 36, padB = 48;

        // Background
        g2.setColor(C_CARD_BG);
        g2.fillRect(0, 0, W, H);

        // Title
        g2.setColor(C_TEXT);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString("Products per Category", padL, 22);

        if (all.isEmpty()) {
            g2.setColor(C_SUB);
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            g2.drawString("No data available", W / 2 - 50, H / 2);
            return;
        }

        // Count products per category
        java.util.Map<String, Integer> counts = new java.util.LinkedHashMap<>();
        for (Product p : all) {
            counts.merge(p.getCategory(), 1, Integer::sum);
        }

        int maxVal = counts.values().stream().mapToInt(v -> v).max().orElse(1);
        int chartW = W - padL - padR;
        int chartH = H - padT - padB;

        // Y-axis grid lines
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                0, new float[]{4, 4}, 0));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        int gridLines = Math.min(maxVal, 5);
        for (int i = 0; i <= gridLines; i++) {
            int yVal = (int) Math.round((double) maxVal * i / gridLines);
            int yPos = padT + chartH - (int) ((double) chartH * i / gridLines);
            g2.setColor(C_DARK);
            g2.drawLine(padL, yPos, W - padR, yPos);
            g2.setColor(C_SUB);
            g2.drawString(String.valueOf(yVal), padL - 28, yPos + 4);
        }
        g2.setStroke(new BasicStroke(1f));

        // Bars
        Color[] barColors = {
            decode("#e94560"), decode("#f5a623"), decode("#2ecc71"),
            decode("#3498db"), decode("#9b59b6"), decode("#1abc9c")
        };

        String[] categories = counts.keySet().toArray(new String[0]);
        int n         = categories.length;
        int barAreaW  = chartW / Math.max(n, 1);
        int barW      = Math.max(20, Math.min(60, barAreaW - 16));

        for (int i = 0; i < n; i++) {
            int count  = counts.get(categories[i]);
            int barH   = (int) ((double) chartH * count / maxVal);
            int x      = padL + i * barAreaW + (barAreaW - barW) / 2;
            int y      = padT + chartH - barH;

            Color base = barColors[i % barColors.length];

            // Bar gradient
            GradientPaint gp = new GradientPaint(x, y, base.brighter(), x, y + barH, base.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barW, barH, 8, 8);

            // Bar border
            g2.setColor(base.brighter());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, barW, barH, 8, 8);
            g2.setStroke(new BasicStroke(1f));

            // Value label on top of bar
            g2.setColor(C_TEXT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String valStr = String.valueOf(count);
            int    valW   = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, x + (barW - valW) / 2, y - 5);

            // Category label below x-axis
            g2.setColor(C_SUB);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String cat    = categories[i].length() > 9 ? categories[i].substring(0, 8) + "." : categories[i];
            int    catW   = g2.getFontMetrics().stringWidth(cat);
            g2.drawString(cat, x + (barW - catW) / 2, padT + chartH + 18);
        }

        // X axis line
        g2.setColor(C_SUB);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(padL, padT + chartH, W - padR, padT + chartH);
        // Y axis line
        g2.drawLine(padL, padT, padL, padT + chartH);
        g2.setStroke(new BasicStroke(1f));
    }

    // ── Button cell renderer / editor ─────────────────────────────────────────
    static class BtnRenderer extends JButton implements TableCellRenderer {
        private final Color bg;
        BtnRenderer(String label, Color bg) {
            super(label); this.bg = bg;
            setFocusPainted(false); setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            setBackground(bg); return this;
        }
    }

    static class BtnEditor extends DefaultCellEditor {
        private final JButton btn;
        private final java.util.function.IntConsumer action;
        private int currentRow;
        BtnEditor(String label, Color bg, java.util.function.IntConsumer action) {
            super(new JCheckBox());
            this.action = action;
            btn = new JButton(label);
            btn.setBackground(bg); btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false); btn.setBorderPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.addActionListener(e -> {
                fireEditingStopped();
                action.accept(currentRow);
            });
        }
        public Component getTableCellEditorComponent(
                JTable t, Object v, boolean sel, int r, int c) {
            currentRow = r; return btn;
        }
        public Object getCellEditorValue() { return btn.getText(); }
    }
}
