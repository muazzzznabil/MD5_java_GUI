import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MD5GUI extends JFrame {
    private JTextField inputField;
    private JTextArea resultArea;
    private JButton hashButton, toggleMode, copyButton; // Made buttons class members
    private JPanel mainPanel, buttonPanel; // Panels are also class members for theme updates

    private boolean darkMode = true;
    private Color bgColor;
    private Color fgColor;
    private Color borderColor;

    public MD5GUI() {
        setTitle("MD5 Hash Generator");
        setSize(500, 350); // Increased height slightly for buttons
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupUI();
        updateTheme(); // Call updateTheme to set initial colors

        setVisible(true);
    }

    private Color getInverseColor(Color c) {
        return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
    }

    private void updateTheme() {
        // Set theme colors based on darkMode flag
        bgColor = darkMode ? new Color(40, 40, 40) : Color.WHITE;
        fgColor = darkMode ? Color.WHITE : Color.BLACK;
        borderColor = fgColor;

        // Update main frame background
        getContentPane().setBackground(bgColor);

        // Update panel backgrounds
        mainPanel.setBackground(bgColor);
        buttonPanel.setBackground(bgColor);

        // Update input field
        inputField.setBackground(bgColor);
        inputField.setForeground(fgColor);
        inputField.setCaretColor(fgColor);
        TitledBorder inputBorder = BorderFactory.createTitledBorder("Enter text");
        inputBorder.setTitleColor(borderColor);
        inputField.setBorder(inputBorder);

        // Update result area
        resultArea.setBackground(bgColor);
        TitledBorder resultBorder = BorderFactory.createTitledBorder("MD5 Hash");
        resultBorder.setTitleColor(borderColor);
        resultArea.setBorder(resultBorder);

        // Update toggle button color to be the inverse of the background
        toggleMode.setBackground(getInverseColor(bgColor));
        toggleMode.setForeground(bgColor); // Set text color to be the background color for contrast

        // Repaint the frame to apply changes
        repaint();
    }

    private void setupUI() {
        Font font = new Font("Consolas", Font.PLAIN, 16);
        // Use BorderLayout for the main frame
        setLayout(new BorderLayout());

        // --- Center Panel for Input and Result ---
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        inputField = new JTextField(30);
        inputField.setFont(font);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        mainPanel.add(inputField, gbc);

        resultArea = new JTextArea(2, 30);
        resultArea.setFont(font);
        resultArea.setEditable(false);
        resultArea.setForeground(new Color(46, 204, 113)); // A nice bright green
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        gbc.gridy = 1;
        mainPanel.add(resultArea, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // --- Bottom Panel for Buttons ---
        buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10)); // 1 row, 3 columns, with gaps
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Add padding

        // Generate MD5 Button (Green)
        hashButton = new JButton("Generate MD5");
        hashButton.setFont(font);
        hashButton.setBackground(new Color(76, 175, 80));
        hashButton.setForeground(Color.WHITE);
        hashButton.setFocusPainted(false);

        // Copy Hash Button (Blue)
        copyButton = new JButton("Copy Hash");
        copyButton.setFont(font);
        copyButton.setBackground(new Color(33, 150, 243));
        copyButton.setForeground(Color.WHITE);
        copyButton.setFocusPainted(false);

        // Toggle Mode Button (Dynamic Color)
        toggleMode = new JButton("Toggle Theme");
        toggleMode.setFont(font);
        toggleMode.setFocusPainted(false);

        buttonPanel.add(hashButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(toggleMode);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        hashButton.addActionListener(e -> {
            String input = inputField.getText();
            resultArea.setText(md5(input));
        });

        copyButton.addActionListener(e -> {
            String hashText = resultArea.getText();
            if (hashText != null && !hashText.isEmpty()) {
                StringSelection stringSelection = new StringSelection(hashText);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        });

        toggleMode.addActionListener(e -> {
            darkMode = !darkMode;
            updateTheme();
        });
    }


    // MD5 Implementation (Unchanged)
    private static final int[] S = {
        7,12,17,22, 7,12,17,22, 7,12,17,22, 7,12,17,22,
        5,9,14,20, 5,9,14,20, 5,9,14,20, 5,9,14,20,
        4,11,16,23, 4,11,16,23, 4,11,16,23, 4,11,16,23,
        6,10,15,21, 6,10,15,21, 6,10,15,21, 6,10,15,21
    };

    private static final int[] K = new int[64];
    static {
        for (int i = 0; i < 64; i++) {
            K[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
        }
    }

    private static final int A0 = 0x67452301;
    private static final int B0 = 0xefcdab89;
    private static final int C0 = 0x98badcfe;
    private static final int D0 = 0x10325476;

    public static String md5(String input) {
        if (input == null) return "";
        byte[] message = input.getBytes(StandardCharsets.UTF_8);
        int messageLenBits = message.length * 8;

        int paddingLength = (56 - (message.length + 1) % 64 + 64) % 64;
        byte[] padded = Arrays.copyOf(message, message.length + 1 + paddingLength + 8);
        padded[message.length] = (byte) 0x80;

        for (int i = 0; i < 8; i++) {
            padded[padded.length - 8 + i] = (byte) ((messageLenBits >>> (8 * i)) & 0xFF);
        }

        int A = A0, B = B0, C = C0, D = D0;

        for (int i = 0; i < padded.length / 64; i++) {
            int[] M = new int[16];
            for (int j = 0; j < 16; j++) {
                int index = i * 64 + j * 4;
                M[j] = ((padded[index] & 0xff)) |
                       ((padded[index + 1] & 0xff) << 8) |
                       ((padded[index + 2] & 0xff) << 16) |
                       ((padded[index + 3] & 0xff) << 24);
            }

            int a = A, b = B, c = C, d = D;

            for (int k = 0; k < 64; k++) {
                int f, g;
                if (k < 16) {
                    f = (b & c) | (~b & d);
                    g = k;
                } else if (k < 32) {
                    f = (d & b) | (~d & c);
                    g = (1 + 5 * k) % 16;
                } else if (k < 48) {
                    f = b ^ c ^ d;
                    g = (5 + 3 * k) % 16;
                } else {
                    f = c ^ (b | ~d);
                    g = (7 * k) % 16;
                }

                int temp = d;
                d = c;
                c = b;
                b = b + Integer.rotateLeft(a + f + K[k] + M[g], S[k]);
                a = temp;
            }

            A += a;
            B += b;
            C += c;
            D += d;
        }

        return toHex(A) + toHex(B) + toHex(C) + toHex(D);
    }

    private static String toHex(int n) {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 4; i++) {
            sb.append(String.format("%02x", (n >>> (i * 8)) & 0xFF));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(MD5GUI::new);
    }
}