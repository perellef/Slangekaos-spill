package src;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Scanner;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

// Hvordan spillet skal fungere

class Modell {

    private GUI gui;
    private Spillekart spillekart;
    private Runnable cycle;

    private boolean spiller = false;
    private boolean pause = true;
    private Thread telletrad = new Thread(new Telletrad());
    
    private int score;
    private int highscore = 0;
    private File rekordFil;
    private int rekord;

    public Modell(GUI gui, int m, int n) {
        this.gui = gui;
        spillekart = new Spillekart(m,n);
        rekord = lesRekordFraFil();

        gui.settRekord(rekord);

        telletrad.start();
    }

    public void start() {

        if (spiller) {
            return;
        }

        spillekart.reset();
        gui.oppdater(0,0);

        score = 0;

        System.out.println("Nytt spill startet");

        pause = false;
        spiller = true;

    }

    protected class Telletrad implements Runnable {

        @Override
        public void run() {

            cycle = new Runnable() {
                public void run() {                        
                    if (!pause && spiller) {
                        spillekart.oppdater();

                        spiller = spillekart.sjekkOmILive();

                        score = spillekart.scoreSpillerSlange();
                        gui.oppdater(score,spillekart.hentTrekk());

                        if (!spiller) {
                            telletrad.stop();
                            avsluttSpill();
                        }
                    }
                }
            };

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(cycle, 0, 20, TimeUnit.MILLISECONDS); // kjorer cycle hvert 20. ms
        }
    }

    private void avsluttSpill() {
        System.out.println("Score: " + score);
        if (score > highscore) {
            highscore = score;
            System.out.println("Ny high score: " + score);

            gui.endreBesteScore(score);
        }
        if (score > rekord) {
            endreRekord(score);
            gui.settRekord(score);

            System.out.println("Ny rekord: " + score);
        }
    }

    public void exit() {
        System.exit(0);
    }
    public void pause() {
        if (!spiller) {
            return;
        }
        pause = !pause;
    }


    public void opp() {
        spillekart.nyRetningSpillerSlange("opp");
    }

    public void ned() {
        spillekart.nyRetningSpillerSlange("ned");
    }

    public void venstre() {
        spillekart.nyRetningSpillerSlange("venstre");
    }

    public void hoyre() {
        spillekart.nyRetningSpillerSlange("hoyre");
    }
    public void space() {
        if (!spiller) {
            return;
        }
        pause = !pause;
    }

    public void reset() {
        spiller = false;
        pause = true;
        start();
    }

    public Spillekart hentSpillekart() {
        return spillekart;
    }

    private int lesRekordFraFil() {

        String filnavn = "rekord.txt";
        rekordFil = new File(filnavn);
        
        Scanner sc = null;
        try {
            sc = new Scanner(rekordFil);
        } catch (FileNotFoundException e) {
            System.out.println("Feil 32");
            System.exit(0);
        }

        return sc.nextInt();
        
    }

    private void endreRekord(int nyScore) {

        FileWriter skriver = null;

        try {
            skriver = new FileWriter(rekordFil);
        } catch (IOException e) {
            System.out.println("Feil 213");
            System.exit(0);
        }

        try {
            skriver.write(nyScore + "");
        } catch (IOException e) {
            System.out.println("Feil 312");
            System.exit(0);
        } finally {

            try {
                skriver.close();
            } catch (IOException e) {
                System.out.println("Feil 320");
                System.exit(0);
            }
        }
    }
}
