package org.example;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class HexMazeApp extends JFrame {
    private HexGrid grid;
    private MazeGenerator generator;
    private PathfindingEngine engine;
    private HexCanvas canvas;
    private SoundManager soundManager;
    private JLabel lblStatSteps;
    private JLabel lblStatCost;

    public HexMazeApp() {
        setTitle("Hexagonal Maze - Advanced Pathfinder (OOP)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Initialize Components
        grid = new HexGrid(25, 25);
        canvas = new HexCanvas(grid);
        soundManager = new SoundManager();
        generator = new MazeGenerator(grid);

        // Setup listener untuk stats UI
        PathfindingEngine.StatListener stats = (steps, cost) -> {
            if (lblStatSteps != null) lblStatSteps.setText(String.valueOf(steps));
            if (lblStatCost != null) lblStatCost.setText(String.valueOf(cost));
        };

        // Setup listener untuk partikel (menghubungkan Engine -> Canvas)
        PathfindingEngine.ParticleSpawner particleSpawner = (point, color, amount) -> {
            canvas.addParticles(point, color, amount);
        };

        // Setup repaint callback
        Runnable repainter = () -> canvas.repaint();

        engine = new PathfindingEngine(grid, repainter, stats, particleSpawner, soundManager);
        canvas.setEngine(engine); // Inject engine back to canvas for rendering visited nodes

        // Start Game
        generator.generate(25, 25);
        soundManager.playLoopingBacksound();

        // 2. Add to Frame
        add(canvas, BorderLayout.CENTER);
        add(createSidebar(), BorderLayout.EAST);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(20, 20, 30));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        sidebar.setPreferredSize(new Dimension(260, 0));

        // Title
        JLabel titleLbl = new JLabel("GAME SETTINGS");
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Level Selector
        String[] levels = {"Easy (15x15)", "Medium (25x25)", "Hard (35x35)"};
        ModernComboBox comboLevel = new ModernComboBox(levels);
        comboLevel.setMaximumSize(new Dimension(220, 35));
        comboLevel.setSelectedIndex(1);
        comboLevel.addActionListener(e -> {
            int idx = comboLevel.getSelectedIndex();
            int s = (idx == 0) ? 15 : (idx == 1 ? 25 : 35);

            // Reset logic
            engine.resetVisuals();
            canvas.clearParticles();
            generator.generate(s, s);
            canvas.repaint();
        });

        // Legend
        JPanel legendPanel = new JPanel(new GridLayout(3, 1));
        legendPanel.setBackground(new Color(20, 20, 30));
        legendPanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY), "Terrain Cost", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        legendPanel.setMaximumSize(new Dimension(220, 100));
        legendPanel.add(createLegend("Grass (Cost 1)", GameConstants.COL_GRASS));
        legendPanel.add(createLegend("Mud (Cost 5)", GameConstants.COL_MUD));
        legendPanel.add(createLegend("Water (Cost 10)", GameConstants.COL_WATER));

        // Stats
        JPanel statsPanel = new JPanel(new GridLayout(2, 2));
        statsPanel.setBackground(new Color(20, 20, 30));
        statsPanel.setBorder(new TitledBorder(new LineBorder(Color.CYAN), "Statistics", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, Color.CYAN));
        statsPanel.setMaximumSize(new Dimension(220, 80));
        lblStatSteps = new JLabel("0"); lblStatSteps.setForeground(Color.WHITE);
        lblStatCost = new JLabel("0"); lblStatCost.setForeground(Color.GREEN);
        JLabel t1 = new JLabel("Steps:"); t1.setForeground(Color.LIGHT_GRAY);
        JLabel t2 = new JLabel("Cost:"); t2.setForeground(Color.LIGHT_GRAY);
        statsPanel.add(t1); statsPanel.add(lblStatSteps);
        statsPanel.add(t2); statsPanel.add(lblStatCost);

        // Buttons
        JButton btnGen = new NeonButton("GENERATE MAZE", new Color(100, 100, 100));
        btnGen.addActionListener(e -> {
            engine.resetVisuals();
            canvas.clearParticles();
            generator.generate(grid.getRows(), grid.getCols());
            canvas.repaint();
        });

        sidebar.add(titleLbl); sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(comboLevel); sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(legendPanel); sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(statsPanel); sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(btnGen); sidebar.add(Box.createVerticalStrut(20));

        // Algorithms Buttons
        addAlgoButton(sidebar, "Solve BFS", new Color(0, 150, 200), "BFS");
        addAlgoButton(sidebar, "Solve DFS", new Color(200, 100, 0), "DFS");
        sidebar.add(Box.createVerticalStrut(15));
        addAlgoButton(sidebar, "Solve Dijkstra", new Color(150, 0, 150), "Dijkstra");
        addAlgoButton(sidebar, "Solve A*", new Color(0, 200, 100), "A*");

        return sidebar;
    }

    private void addAlgoButton(JPanel p, String text, Color c, String algo) {
        JButton b = new NeonButton(text, c);
        b.addActionListener(e -> {
            canvas.clearParticles();
            engine.solve(algo);
        });
        p.add(b);
        p.add(Box.createVerticalStrut(5));
    }

    private JPanel createLegend(String text, Color col) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(15, 15));
        box.setBackground(col);
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.LIGHT_GRAY);
        p.add(box); p.add(lbl);
        return p;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new HexMazeApp().setVisible(true));
    }
}