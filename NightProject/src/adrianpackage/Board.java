package adrianpackage;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class Board extends JFrame implements KeyListener {

    public static STATE State = STATE.MENU;
    int[] speedsX = {10, 15, 20};
    int backgroundBounce = 0;
    private String path = "C://images/"; //// <---------- Show the path to the images example C://images/
    private PreparedStatement stmt;
    private String TimeString;
    private String Namee;
    private Connection con;
    private String dbType = "mysql"; /// Database type
    private String dbName = "sql7288675"; // Database Name
    private String dbUser = "sql7288675"; // Databse username
    private String dbPassword = "mR2Ay9gYIX"; // Database password
    private String dbPort = "3306"; /// Database port.
   private String dbServer = "sql7.freemysqlhosting.net";  /// <Put your own dbServer
    private boolean endgame = true;
    private boolean magicT = true;
    private boolean magicF = false;
    private int smoothness = 30;
    private int bounces = 1;
    private int[] speedsY = {15, 20, 24};
    private JButton startButton, scoresButton, exitButton, backButton, submit;
    private int counter = 0;
    private int delay = 0;
    private int period = 900;
    private int time = 0;
    private int automaticThreadCount = 0;
    private Container cnt = this.getContentPane();
    private JLabel ball, title, board, GameOver;
    private int width = 800;
    private int height = 600;
    private int ballX = 100;
    private int ballY = 100;
    private int boardX;
    private int boardY = 510;
    private int boardWidth = 150;
    private int ballWidth = 30;
    private int ballHeight = 30;
    private int boardHeight = 25;
    private int speedX = 8;
    private int speedY = 10;

    void create() {
        endgame = false;
        this.setTitle("Random Speed Ball Game");
        this.setSize(width, height);
        this.setLocation(600, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);

        ImageIcon boardImg = new ImageIcon(path + "redboard.png");
        ImageIcon ballImg = new ImageIcon(path + "redball.png");

        board = new JLabel(boardImg);
        ball = new JLabel(ballImg);
        ball.setBounds(ballX, ballY, ballWidth, ballHeight);

        if (State == STATE.GAME) {
            ball.setVisible(true);
        } else if (State == STATE.MENU) {
            ball.setVisible(false);
        }

        board.setBounds(boardX, boardY, boardWidth, boardHeight);
        board.setVisible(true);
        this.add(board);
        this.add(ball);
        this.setMouseListener();

        addKeyListener(this);
        this.setVisible(true);
        this.setFocusable(true);
        ConnectDB();
        // this.getContentPane().setBackground(Color.white);
        repaintMethod();
        if (State == STATE.GAME) {
            automaticUpdates();
        } else if (State == STATE.MENU) {
            start();

        }

    }

    public final void setMouseListener() {
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boardX = e.getX() - 55;
                board.setLocation(boardX, boardY);
                repaintMethod();
                Toolkit.getDefaultToolkit().sync();
            }
        });

    }

    public void movement() {
        if (State == STATE.GAME) {
            ballY = ballY + speedY;
            ballX = ballX + speedX;
            int randomSpeedY = speedsY[(int) (Math.random() * 3)];
            int randomSpeedX = speedsX[(int) (Math.random() * 3)];


            if (ballX >= boardX && ballX <= boardX + boardWidth && ballY + 30 == 510) {
                speedY = (bounces < 3) ? -10 : -randomSpeedY;
                onBounce();
                bounces++;

            } else if (ballX <= 0) {
                speedX = (bounces < 3) ? 8 : randomSpeedX;
                onBounce();

            } else if (ballX + 30 >= 800) {
                speedX = (bounces < 3) ? -8 : -randomSpeedX;
                onBounce();


            } else if (ballY <= 0) {
                speedY = (bounces < 3) ? 10 : randomSpeedY;
                onBounce();

            } else if (ballY > 610) {
                State = STATE.MENU;
                repaintMethod();
                GameOver(true);
                endgame = false;

                JTextField java = new JTextField();
                ImageIcon restrictedImg = new ImageIcon(path + "only15.png");
                JLabel restricted = new JLabel(restrictedImg);

                restricted.setBounds(240, 410, 320, 50);
                this.add(restricted);
                restricted.setVisible(false);
                ImageIcon submitButtonImg = new ImageIcon(path + "submit.png");
                submit = new JButton(submitButtonImg);


                java.setBounds(342, 310, 120, 20);
                submit.setBounds(292, 350, 220, 50);

                this.add(java);
                this.add(submit);
                java.setVisible(true);
                submit.setVisible(true);

                submit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {


                        if (java.getText().length() <= 15) {

                            Namee = java.getText();
                            GameOver.setVisible(false);
                            TimeString = "" + time;
                            addHighscore(Namee, TimeString);
                            visibleZero();
                            create();
                        } else {
                            submit.setIcon(new ImageIcon(path + "submitred.png"));
                            restricted.setVisible(true);
                            repaintMethod();
                            java.setText("");
                        }
                    }
                });


            }
            time++;
            ball.setLocation(ballX, ballY);

        }
    }

    void automaticUpdates() { ///
        if (automaticThreadCount == 0) {
            new Thread(new Runnable() {
                public void run() {

                    Timer t = new Timer(); // J
                    t.scheduleAtFixedRate(
                            new TimerTask() {


                                public void run() {

                                    movement();
                                    repaintMethod();

                                }

                            }, delay, period / smoothness); //


                }

            }).start();
        } else if (automaticThreadCount == 1) {
            /// lai thread nepalaižas vēlreiz no jauna, bet turpina esošo no pirmās reizes.
        }
    }

    void setRandomBackground() {
        Color c = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
        this.getContentPane().setBackground(c);
    }

    void onBounce() {
        backgroundBounce++;
        setRandomBackground();

    }

    public void start() {
        if (State == STATE.MENU) {

            Container startMenu = this.getContentPane();
            startMenu.setLayout(null);
            ImageIcon startButtonImg = new ImageIcon(path + "start2.png");
            ImageIcon scoresButtonImg = new ImageIcon(path + "scores.png");
            ImageIcon exitButtonImg = new ImageIcon(path + "exit.png");
            ImageIcon titleButtonImg = new ImageIcon(path + "title.png");
            ImageIcon backButtonImg = new ImageIcon(path + "back.png");
            title = new JLabel(titleButtonImg);
            startButton = new JButton(startButtonImg);
            scoresButton = new JButton(scoresButtonImg);
            exitButton = new JButton(exitButtonImg);
            backButton = new JButton(backButtonImg);

            startMenu.add(title);
            title.setBounds(0, 60, 800, 130);

            startMenu.add(startButton);
            startButton.setBounds(292, 205, 220, 50);

            startMenu.add(scoresButton);
            scoresButton.setMnemonic(KeyEvent.VK_H);
            scoresButton.setBounds(292, 260, 220, 50);

            startMenu.add(exitButton);
            exitButton.setMnemonic(KeyEvent.VK_E);
            exitButton.setBounds(292, 315, 220, 50);

            startMenu.add(backButton);
            backButton.setMnemonic(KeyEvent.VK_E);
            backButton.setBounds(292, 375, 220, 50);
            backButton.setVisible(false);


            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {


                    if (counter == 0) {
                        State = STATE.GAME;
                        repaintMethod();
                        setVisibleThings();
                        automaticUpdates();
                        counter++;
                        repaintMethod();
                        endgame = true;
                        automaticThreadCount++;

                    } else if (counter >= 1) {
                        magicF = false;
                        magicT = true;
                        setVisibleThings();
                        State = STATE.GAME;
                        automaticUpdates();
                        repaintMethod();
                        automaticThreadCount++;
                    }


                }
            });


        }


        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        scoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                startButton.setVisible(false);
                scoresButton.setVisible(false);
                exitButton.setVisible(false);
                backButton.setVisible(true);
                AddScoreTableToFrame();


            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE && endgame) {

            State = STATE.MENU;
            magicF = true;
            magicT = false;
            setVisibleThings();
            backButton.setVisible(false);
            this.getContentPane().setBackground(Color.WHITE);
            automaticThreadCount++;


        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void setVisibleThings() {
        startButton.setVisible(magicF);
        scoresButton.setVisible(magicF);
        exitButton.setVisible(magicF);
        title.setVisible(magicF);
        ball.setVisible(magicT);
    }

    public void GameOver(boolean a) {
        ImageIcon GameOverImg = new ImageIcon(path + "gameover.png");
        GameOver = new JLabel(GameOverImg);
        this.add(GameOver);
        GameOver.setBounds(200, 140, 400, 150);
        GameOver.setVisible(a);
        this.getContentPane().setBackground(Color.white);
        this.repaintMethod();
    }

    public void visibleZero() {
        dispose();
        this.getContentPane().removeAll();
        revalidate();
        this.repaintMethod();
        counter = 0;
        magicT = true;
        magicF = false;
        backgroundBounce = 0;
        smoothness = 30;
        bounces = 1;
        width = 800;
        height = 600;
        ballX = 100;
        ballY = 100;
        boardY = 510;
        boardWidth = 150;
        ballWidth = 30;
        ballHeight = 30;
        boardHeight = 25;
        speedX = 8;
        speedY = 10;
        counter = 0;
        int[] speedsXnew = {10, 15, 20};
        int[] speedsYnew = {15, 20, 24};
        speedsX = speedsXnew;
        speedsY = speedsYnew;
        time = 0;


    }

    public void repaintMethod() {

        String osName = System.getProperty("os.name");
        if (osName.contains("Linux")) {
            repaint();
            Toolkit.getDefaultToolkit().sync();
        } else {
            repaint();
        }

    }

    public void ConnectDB() {

        try {
            con = DriverManager.getConnection("jdbc:" + dbType + "://" + dbServer + ":" + dbPort + "/" + dbName, dbUser, dbPassword);
            System.out.println("Connected to database successfully!");

        } catch (SQLException ex) {
            System.out.println("Could not connect to database");
        }

    }

    public void AddScoreTableToFrame() {
        DefaultTableModel model = new DefaultTableModel();
        JTable scoreTable = new JTable(model);
        cnt.setLayout(null);
        model.addColumn("Name");
        model.addColumn("Score");
        try {
            PreparedStatement pstm = con.prepareStatement("SELECT * FROM Score ORDER BY `Score`+0 DESC LIMIT 10");
            ResultSet Rs = pstm.executeQuery();
            while (Rs.next()) {
                model.addRow(new Object[]{Rs.getString(1), Rs.getString(2)});
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ((DefaultTableCellRenderer) scoreTable.getDefaultRenderer(Object.class)).setOpaque(false);
        scoreTable.setOpaque(false);
        scoreTable.setShowGrid(false);
        scoreTable.setBounds(292, 200, 220, 160);
        scoreTable.setEnabled(false);
        this.add(scoreTable);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setVisible(true);
                scoresButton.setVisible(true);
                exitButton.setVisible(true);
                backButton.setVisible(false);
                scoreTable.setVisible(false);

            }
        });


    }

    void addHighscore(String Namee, String TimeString) {

        try {
            String query = "INSERT INTO `Score` ( `Name`, `Score`) VALUES ('" + Namee + "','" + TimeString + "')";
            stmt = con.prepareStatement(query);
            stmt.executeUpdate(query);

        } catch (Exception e) {
            System.err.println("Error at Score");

            e.printStackTrace();

        }

    }


    public enum STATE {
        MENU,
        GAME
    }
}




