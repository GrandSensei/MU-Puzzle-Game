import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class MU_Game extends JPanel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the welcome frame
            JFrame welcomeFrame = new JFrame("Welcome");
            welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeFrame.setSize(800, 500);
            welcomeFrame.setLocationRelativeTo(null); // Center the window on the screen

            // Main Panel for the welcome screen
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel welcomeLabel = new JLabel("Welcome");
            welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 35));
            welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            welcomeLabel.setForeground(Color.BLACK);

            JLabel subtitleLabel = new JLabel("to the MU Puzzle");
            subtitleLabel.setFont(new Font("Times New Roman", Font.BOLD, 35));
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitleLabel.setForeground(Color.BLACK);

            // Panel for the text field
            JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JLabel playerIdLabel = new JLabel("Player ID >> ");
            playerIdLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
            playerIdLabel.setForeground(Color.BLACK);
            JTextField playerIdField = new JTextField(20);
            playerIdField.setFont(new Font("Arial", Font.PLAIN, 16));
            playerIdField.setPreferredSize(new Dimension(200, 30));
            playerIdField.setBackground(Color.WHITE);
            textFieldPanel.add(playerIdLabel);
            textFieldPanel.add(playerIdField);

            // Buttons Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            GradientRoundedButton historyButton = new GradientRoundedButton("History");
            GradientRoundedButton backButton = new GradientRoundedButton("Back");
            GradientRoundedButton continueButton = new GradientRoundedButton("Continue");

            // When Continue is clicked, open the game page and close the welcome screen.
            continueButton.addActionListener(e -> {
                // Create the game frame
                JFrame gameFrame = new JFrame("Game Page");
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameFrame.setSize(800, 500);
                gameFrame.setLocationRelativeTo(null);
                gameFrame.add(new MU_PuzzleGUI());
                gameFrame.setVisible(true);

                // Close the welcome frame
                welcomeFrame.dispose();
            });

            buttonPanel.add(historyButton);
            buttonPanel.add(backButton);
            buttonPanel.add(continueButton);

            // Add components to the main panel
            panel.add(Box.createVerticalGlue());
            panel.add(welcomeLabel);
            panel.add(subtitleLabel);
            panel.add(Box.createVerticalStrut(20)); // Space before text field
            panel.add(textFieldPanel);
            panel.add(Box.createVerticalStrut(20)); // Space before buttons
            panel.add(buttonPanel);
            panel.add(Box.createVerticalGlue());

            welcomeFrame.add(panel);
            welcomeFrame.setVisible(true);
        });
    }

    // Custom rounded button with gradient and animation
    static class GradientRoundedButton extends JButton {
        private Color startColor = new Color(0x2D1E99); // Default background
        private Color endColor = new Color(0x0A54FF);   // Background on hover
        private Color hoverStartColor = new Color(0x0A54FF);
        private Color hoverEndColor = new Color(0x2D1E99);
        private Color pressStartColor = new Color(0x0A54FF); // Color when pressed
        private Color pressEndColor = new Color(0x2D1E99);
        private Color borderColor = new Color(0x1D2B8C); // Border color
        private boolean isHovered = false;
        private boolean isPressed = false;

        public GradientRoundedButton(String text) {
            super(text);
            setFont(new Font("Arial", Font.BOLD, 18));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);

            // Mouse listener for hover and press effects
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });

            // Timer to update the animation smoothly
            Timer timer = new Timer(20, e -> repaint());
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Choose colors based on the current button state
            Color topColor = isPressed ? pressStartColor : (isHovered ? hoverStartColor : startColor);
            Color bottomColor = isPressed ? pressEndColor : (isHovered ? hoverEndColor : endColor);

            // Apply gradient fill
            GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            // Draw the border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);

            // Draw the button text
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 2;
            g2.setColor(getForeground());
            g2.drawString(getText(), x, y);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}



class MU_PuzzleGUI extends JPanel {
    private String currentString = "MI";  // starting string
    private JTextPane display;
    private JButton rule1Button, rule2Button, rule3Button, rule4Button, rule5Button, rule6Button;
    private int selectedIndex = -1;  // stores the index clicked by the user
    private Highlighter highlighter;
    private Highlighter.HighlightPainter painter;

    public MU_PuzzleGUI() {
        // Set the panel layout and background color
        setLayout(new BorderLayout());
        setBackground(new Color(145, 202, 243)); // #91CAF3 color

        // Panel to show all the rules at the top
        JPanel rulesPanel = new JPanel(new BorderLayout());
        JTextArea rulesArea = new JTextArea();
        rulesArea.setEditable(false);
        rulesArea.setFont(new Font("Times New Roman", Font.BOLD, 19));
        rulesArea.setText("Make MU by playing around with the String MI, select a character and choose a rule accordingly\n" +
                "Rules:\n" +
                "1. Add U if the string ends with I.\n" +
                "2. Double the substring after M.\n" +
                "3. Replace III with U.\n" +
                "4. Remove UU.\n");
        rulesArea.setRows(5);
        rulesArea.setBackground(new Color(240, 240, 240)); // Light background for rules
        rulesPanel.setBackground(new Color(50, 50, 50));  // Dark background for the rules area
        rulesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        rulesPanel.add(rulesArea, BorderLayout.CENTER);
        add(rulesPanel, BorderLayout.NORTH);

        // Using a JTextPane to allow centered text
        display = new JTextPane();
        display.setFont(new Font("Monospaced", Font.PLAIN, 24));
        display.setEditable(false);
        display.setFocusable(false); // disable focus to avoid caret issues

        // Center the text in the pane:
        StyledDocument doc = display.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        display.setText(currentString);

        // Setup highlighter for the display area
        highlighter = display.getHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

        display.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Convert the mouse click point to a character index in the text pane
                int pos = display.viewToModel(e.getPoint());
                if (pos >= 0 && pos < currentString.length()) {
                    selectedIndex = pos;
                    highlightSelection(pos);
                }
            }
        });
        add(new JScrollPane(display), BorderLayout.CENTER);

        // Panel for rule buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(50, 50, 50)); // Dark background for buttons panel
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Center buttons with spacing

        // Create buttons
        rule1Button = new GradientRoundedButton("Rule 1: Add U");
        rule2Button = new GradientRoundedButton("Rule 2: Double substring");
        rule3Button = new GradientRoundedButton("Rule 3: Replace III with U");
        rule4Button = new GradientRoundedButton("Rule 4: Remove UU");
        rule5Button = new GradientRoundedButton("Rule 5: New Rule");
        rule6Button = new GradientRoundedButton("Rule 6: New Button");

        // Add the buttons to the panel
        buttonPanel.add(rule1Button);
        buttonPanel.add(rule2Button);
        buttonPanel.add(rule3Button);
        buttonPanel.add(rule4Button);
        buttonPanel.add(rule5Button);
        buttonPanel.add(rule6Button);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners for each rule button
        rule1Button.addActionListener(e -> applyRule1());

        rule2Button.addActionListener(e -> {
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(null, "Please select the 'M' position by clicking on the character.");
                return;
            }
            applyRule2(selectedIndex);
        });

        rule3Button.addActionListener(e -> {
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(null, "Please select the starting index of 'III' by clicking on it.");
                return;
            }
            applyRule3(selectedIndex);
        });

        rule4Button.addActionListener(e -> {
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(null, "Please select the starting index of 'UU' by clicking on it.");
                return;
            }
            applyRule4(selectedIndex);
        });

        rule5Button.addActionListener(e -> {
            // Placeholder for Rule 5
            JOptionPane.showMessageDialog(null, "New Rule will be implemented here.");
        });

        rule6Button.addActionListener(e -> {
            // Placeholder for Rule 6
            JOptionPane.showMessageDialog(null, "Rule 6 will be implemented here.");
        });
    }

    // Highlight the selected character in the text pane.
    private void highlightSelection(int pos) {
        highlighter.removeAllHighlights();
        try {
            highlighter.addHighlight(pos, pos + 1, painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // Update the text pane with the current string, re-center the text, and clear any highlights.
    private void updateDisplay() {
        display.setText(currentString);
        // Reapply centered alignment after text update:
        StyledDocument doc = display.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        highlighter.removeAllHighlights();
        selectedIndex = -1;
    }

    // Rule 1: Add U if the string ends with I
    private void applyRule1() {
        if (currentString.endsWith("I")) {
            currentString = currentString + "U";
        } else {
            JOptionPane.showMessageDialog(null, "Error: The string does not end with I.");
        }
        updateDisplay();
    }

    // Rule 2: Double the substring after M (the selected index should be where the M is located)
    private void applyRule2(int pos) {
        if (pos < currentString.length() && currentString.charAt(pos) == 'M') {
            String toDouble = "";
            if (pos + 1 < currentString.length()) {
                toDouble = currentString.substring(pos + 1);
            }
            currentString = currentString + toDouble;
        } else {
            JOptionPane.showMessageDialog(null, "Error: The selected character is not 'M'.");
        }
        updateDisplay();
    }

    // Rule 3: Replace 'III' with 'U' starting at the selected index
    private void applyRule3(int pos) {
        if (pos + 2 < currentString.length() && currentString.substring(pos, pos + 3).equals("III")) {
            currentString = currentString.substring(0, pos) + "U" + currentString.substring(pos + 3);
        } else {
            JOptionPane.showMessageDialog(null, "Error: The substring starting at index " + pos + " is not 'III'.");
        }
        updateDisplay();
    }

    // Rule 4: Remove 'UU' starting at the selected index
    private void applyRule4(int pos) {
        if (pos + 1 < currentString.length() && currentString.substring(pos, pos + 2).equals("UU")) {
            currentString = currentString.substring(0, pos) + currentString.substring(pos + 2);
        } else {
            JOptionPane.showMessageDialog(null, "Error: The substring starting at index " + pos + " is not 'UU'.");
        }
        updateDisplay();
    }

    // Custom Rounded Button with Gradient and Shadow Effect
    static class GradientRoundedButton extends JButton {
        private Color startColor = new Color(45, 30, 153);
        private Color endColor = new Color(45, 30, 153);
        private Color hoverStartColor = new Color(10, 84, 255);
        private Color hoverEndColor = new Color(10, 84, 255);
        private Color pressStartColor = new Color(50, 110, 160);
        private Color pressEndColor = new Color(20, 70, 130);
        private boolean isHovered = false;
        private boolean isPressed = false;

        public GradientRoundedButton(String text) {
            super(text);
            setFont(new Font("Times New Roman", Font.BOLD, 15));
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setBackground(startColor);
            setPreferredSize(new Dimension(180, 35));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setBorderPainted(false);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp;
            if (isPressed) {
                gp = new GradientPaint(0, 0, pressStartColor, 0, getHeight(), pressEndColor);
            } else if (isHovered) {
                gp = new GradientPaint(0, 0, hoverStartColor, 0, getHeight(), hoverEndColor);
            } else {
                gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
            }

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
        }
    }
}
