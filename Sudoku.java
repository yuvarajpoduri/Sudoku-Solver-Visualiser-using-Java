package yuva.java;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Sudoku extends JFrame {
    private JTextField[][] cells;
    private int[][] board = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    private JLabel timerLabel;
    private JLabel speedLabel;
    private int elapsedTime;
    private Timer timer;
    private int delay = 1000;  

    public Sudoku() {
        cells = new JTextField[9][9];
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(9, 9));
        gridPanel.setBackground(Color.WHITE);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                cells[row][col].setForeground(Color.BLACK);
                cells[row][col].setBackground(Color.WHITE);
                setCellBorder(row, col);
                if (board[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(board[row][col]));
                    cells[row][col].setEditable(false);
                }
                gridPanel.add(cells[row][col]);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(2, 3));

        JButton solveButton = new JButton("Solve");
        solveButton.setBackground(Color.GREEN);
        solveButton.setForeground(Color.WHITE);
        solveButton.setFont(new Font("Arial", Font.BOLD, 20));
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer != null) {
                    timer.stop();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        solveSudoku();
                    }
                }).start();
            }
        });
        controlPanel.add(solveButton);

        timerLabel = new JLabel("Time: 0s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        controlPanel.add(timerLabel);

        speedLabel = new JLabel("Speed: " + (1000 / delay) + " steps/s");
        speedLabel.setFont(new Font("Arial", Font.BOLD, 20));
        speedLabel.setHorizontalAlignment(JLabel.CENTER);
        controlPanel.add(speedLabel);

        JButton increaseSpeedButton = new JButton("+");
        increaseSpeedButton.setFont(new Font("Arial", Font.BOLD, 20));
        increaseSpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (delay > 100) {
                    delay -= 100;
                    speedLabel.setText("Speed: " + (1000 / delay) + " steps/s");
                }
            }
        });
        controlPanel.add(increaseSpeedButton);

        JButton decreaseSpeedButton = new JButton("-");
        decreaseSpeedButton.setFont(new Font("Arial", Font.BOLD, 20));
        decreaseSpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delay += 100;
                speedLabel.setText("Speed: " + (1000 / delay) + " steps/s");
            }
        });
        controlPanel.add(decreaseSpeedButton);

        add(controlPanel, BorderLayout.SOUTH);

        elapsedTime = 0;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timerLabel.setText("Time: " + elapsedTime + "s");
            }
        });
        timer.start();

        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setCellBorder(int row, int col) {
        int top = (row % 3 == 0) ? 2 : 1;
        int left = (col % 3 == 0) ? 2 : 1;
        int bottom = (row == 8) ? 2 : 1;
        int right = (col == 8) ? 2 : 1;
        cells[row][col].setBorder(new LineBorder(Color.BLACK, 1));
        cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
    }

    private void solveSudoku() {
        if (solve()) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    cells[row][col].setText(String.valueOf(board[row][col]));
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No solution exists!");
        }
    }

    private boolean solve() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(row, col, num)) {
                            board[row][col] = num;
                            highlightCell(row, col, Color.GREEN, num);
                            try {
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (solve()) {
                                return true;
                            } else {
                                board[row][col] = 0;
                                highlightCell(row, col, Color.RED, 0);
                                try {
                                    Thread.sleep(delay);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                highlightCell(row, col, Color.WHITE, 0);
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private void highlightCell(int row, int col, Color color, int num) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                cells[row][col].setBackground(color);
                if (num != 0) {
                    cells[row][col].setText(String.valueOf(num));
                } else {
                    cells[row][col].setText("");
                }
            }
        });
    }

    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num || 
                board[row - row % 3 + i / 3][col - col % 3 + i % 3] == num) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        new Heaps();
    }
}
