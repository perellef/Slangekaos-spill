package src;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

// oppretter GUI-vinduet, og registrerer knappetrykk

class GUI {

    private Kontroll kontroll;
    private Modell modell;

    private JFrame vindu;
    private JPanel panel, hovedmeny, spillekart, grid, knappbokser;
    private JComponent undermeny;
    private JButton start,pause,exit;
    private JLabel score, tid, rekord, besteScore;

    private JLabel[][] rutenett;
    private Rute[][] ruter;

    public GUI(Kontroll kontroll, int m, int n) {
        this.kontroll = kontroll;

        try {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { System.exit(9); }
        
        vindu = new JFrame("Snake");
        vindu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        vindu.add(panel);

        // HOVEDMENY

        hovedmeny = new JPanel();
        panel.add(hovedmeny, BorderLayout.NORTH);

        // spilleinstrukser

        JPanel instrukser = new JPanel();
        instrukser.setLayout(new BorderLayout());
        hovedmeny.add(instrukser);

        JLabel instruksLinje1 = new JLabel("      SPILLEINSTRUKSER:   - Spis epler for a vokse.",SwingConstants.LEFT); 
        JLabel instruksLinje2 = new JLabel("                                             - Hold slangen innenfor spilleomradet.                ",SwingConstants.LEFT); 
        JLabel instruksLinje3 = new JLabel("                                             - Unnga andre slanger for a overleve.",SwingConstants.LEFT); 
        

        instruksLinje1.setFont(new Font(Font.DIALOG,Font.PLAIN,12));
        instruksLinje2.setFont(new Font(Font.DIALOG,Font.PLAIN,12));
        instruksLinje3.setFont(new Font(Font.DIALOG,Font.PLAIN,12));

        instrukser.add(instruksLinje1,BorderLayout.NORTH);
        instrukser.add(instruksLinje2,BorderLayout.CENTER);
        instrukser.add(instruksLinje3,BorderLayout.SOUTH);

        // start-, pause-, og exit-knapper

        knappbokser = new JPanel();
        hovedmeny.add(knappbokser);

        JPanel knappboks1 = new JPanel();
        knappboks1.setLayout(new BorderLayout());
        JPanel knappboks2 = new JPanel();
        knappboks2.setLayout(new BorderLayout());
        JPanel knappboks3 = new JPanel();
        knappboks3.setLayout(new BorderLayout());

        knappbokser.add(knappboks1);
        knappbokser.add(knappboks2);
        knappbokser.add(knappboks3);

        JButton start = new JButton("Start");
        JButton pause = new JButton("Pause");
        JButton exit = new JButton("Exit");

        JLabel startLabel = new JLabel("eller press R",SwingConstants.CENTER);
        JLabel pauseLabel = new JLabel("eller press P",SwingConstants.CENTER);
        JLabel exitLabel = new JLabel("-",SwingConstants.CENTER);

        startLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
        pauseLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
        exitLabel.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
                
        knappboks1.add(start,BorderLayout.NORTH);
        knappboks1.add(startLabel,BorderLayout.SOUTH);
        knappboks2.add(pause,BorderLayout.NORTH);
        knappboks2.add(pauseLabel,BorderLayout.SOUTH);
        knappboks3.add(exit,BorderLayout.NORTH);
        knappboks3.add(exitLabel,BorderLayout.SOUTH);

        class Start implements ActionListener {
            @Override
            public void actionPerformed (ActionEvent e) {
                kontroll.start();
            }
        }

        class Pause implements ActionListener {
            @Override
            public void actionPerformed (ActionEvent e) {
                kontroll.pause();
            }
        }

        class Exit implements ActionListener {
            @Override
            public void actionPerformed (ActionEvent e) {
                kontroll.exit();
            }
        }

        start.addActionListener(new Start());
        pause.addActionListener(new Pause());
        exit.addActionListener(new Exit());

        // info om hvordan styre slangen

        JPanel infobokser = new JPanel();
        infobokser.setLayout(new BorderLayout());
        hovedmeny.add(infobokser);

        JLabel infoboksLinje1 = new JLabel("     Bruk piltastene for                       "); 
        JLabel infoboksLinje2 = new JLabel("     a styre slangen."); 
        

        infoboksLinje1.setFont(new Font(Font.DIALOG,Font.PLAIN,12));
        infoboksLinje2.setFont(new Font(Font.DIALOG,Font.PLAIN,12));

        infobokser.add(infoboksLinje1,BorderLayout.NORTH);
        infobokser.add(infoboksLinje2,BorderLayout.SOUTH);


        // score-oversikt

        JPanel scoreboard = new JPanel();
        scoreboard.setLayout(new BorderLayout());
        hovedmeny.add(scoreboard);

        JPanel venstreScoreboard = new JPanel();
        venstreScoreboard.setLayout(new BorderLayout());
        
        score = new JLabel(" Score:   0");
        besteScore = new JLabel(" Beste score: 0   ");

        score.setBackground(Color.BLACK);
        score.setForeground(new Color(255,255,255));
        score.setOpaque(true);
        score.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,15));

        venstreScoreboard.add(score,BorderLayout.NORTH);
        venstreScoreboard.add(besteScore,BorderLayout.SOUTH);
        
        rekord = new JLabel("       Rekord: ");
        
        scoreboard.add(venstreScoreboard,BorderLayout.WEST);
        scoreboard.add(rekord,BorderLayout.EAST);
        
        // SPILLEKART

        spillekart = new JPanel();
        spillekart.setLayout(new BorderLayout());
        panel.add(spillekart,BorderLayout.CENTER);  
        
        JPanel venstre = new JPanel();
        JPanel hoyre = new JPanel();

        venstre.setBackground(new Color(65,192,183));
        venstre.setPreferredSize(new Dimension(50,200));
        
        hoyre.setBackground(new Color(65,192,183));
        hoyre.setPreferredSize(new Dimension(50,200));


        spillekart.add(hoyre,BorderLayout.EAST);
        grid = new JPanel();
        spillekart.add(grid,BorderLayout.CENTER);
        spillekart.add(venstre,BorderLayout.WEST);
        
        // grid

        grid.setLayout(new GridLayout(m,n));

        rutenett = new JLabel[m][n];
        
        for (int rad=0; rad<m; rad++) {
            for (int kol=0; kol<n; kol++) { 
                JLabel label = new JLabel(".",SwingConstants.CENTER);

                rutenett[rad][kol] = label;

                label.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,15));
                grid.add(label);
            }
        }
        
        grid.setPreferredSize(new Dimension(200,200));
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        grid.setBorder(border);
    
        // UNDERMENY

        undermeny = new JPanel();
        panel.add(undermeny, BorderLayout.SOUTH);
        
        undermeny.setLayout(new BorderLayout());

        // tid 

        tid = new JLabel("Tid: Ikke startet", SwingConstants.CENTER);
        undermeny.add(tid);

        // signatur

        JLabel signatur = new JLabel("av Per Ellef Aalerud  ");
        signatur.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,11));
        
        undermeny.add(signatur,BorderLayout.EAST);
        
        vindu.pack();
        vindu.setVisible(true);

    }

    public void settModell(Modell modell) {
        this.modell = modell;
        
        ruter = modell.hentSpillekart().hentRutenett();
    }

    public void oppdater(int score, int trekk) {

        tid.setText("Tid: "+trekk/50 + " sekunder");
        this.score.setText(" Score:   " +score);

        int m = ruter.length;
        int n = ruter[0].length;

        for (int rad=0; rad<m; rad++) {
            for (int kol=0; kol<n; kol++) {
                
                Rute rute = ruter[rad][kol];
                JLabel label = rutenett[rad][kol];

                String tekst = rute.hentTekst();

                label.setText(tekst);
                label.setForeground(rute.hentSkriftfarge());
                label.setBackground(rute.hentBakgrunnsfarge());
                label.setOpaque(true);
            }
        }
    }

    public void settRekord(int rekordScore) {
        rekord.setText("       Rekord: " + rekordScore);
    }

    public void endreBesteScore(int score) {
        besteScore.setText(" Beste score: " + score);
    }

    public JFrame hentVindu() {
        return vindu;
    }
}
