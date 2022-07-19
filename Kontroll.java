import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Styrer det hele

class Kontroll {

    private GUI gui;
    private Modell modell;

    public Kontroll(int m, int n) {

        gui = new GUI(this,m,n);
        modell = new Modell(gui,m,n);
        gui.settModell(modell);

        definerSpilletaster();  
    }

    public void start() {
        modell.start();
    }

    public void pause() {
        modell.pause();
    }

    public void exit() {
        modell.exit();
    }

    private void definerSpilletaster() {

        // Tastetrykk til spillet

        JFrame vindu = gui.hentVindu();

        InputMap inputMap = vindu.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = vindu.getRootPane().getActionMap();

        // W, pil opp

        KeyStroke pilOpp = KeyStroke.getKeyStroke("UP");
        KeyStroke W = KeyStroke.getKeyStroke("W");
        Action oppAction = new Opp();
        inputMap.put(pilOpp, "retning opp");
        inputMap.put(W, "retning opp");
        actionMap.put("retning opp", oppAction);

        // S, pil ned

        KeyStroke pilNed = KeyStroke.getKeyStroke("DOWN");
        KeyStroke S = KeyStroke.getKeyStroke("S");
        Action nedAction = new Ned();
        inputMap.put(pilNed, "retning ned");
        inputMap.put(S, "retning ned");
        actionMap.put("retning ned", nedAction);

        // A, pil venstre

        KeyStroke pilVenstre = KeyStroke.getKeyStroke("LEFT");
        KeyStroke A = KeyStroke.getKeyStroke("A");
        Action venstreAction = new Venstre();
        inputMap.put(pilVenstre, "retning venstre");
        inputMap.put(A, "retning venstre");
        actionMap.put("retning venstre", venstreAction);

        // D, pil hoyre
        
        KeyStroke pilHoyre = KeyStroke.getKeyStroke("RIGHT");
        KeyStroke D = KeyStroke.getKeyStroke("D");
        Action hoyreAction = new Hoyre();
        inputMap.put(pilHoyre, "retning hoyre");
        inputMap.put(D, "retning hoyre");
        actionMap.put("retning hoyre", hoyreAction);

        // space

        KeyStroke P = KeyStroke.getKeyStroke("P");
        Action startPauseAction = new P();
        inputMap.put(P, "Start/Pause");
        actionMap.put("Start/Pause", startPauseAction);

        // R

        KeyStroke R = KeyStroke.getKeyStroke("R");
        Action RAction = new R();
        inputMap.put(R, "reset spill");
        actionMap.put("reset spill", RAction);
    }

    class Opp extends AbstractAction {
        protected Opp() {
            super("Opp");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            modell.opp();
        }
    }

    class Ned extends AbstractAction {
        protected Ned() {
            super("Ned");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            modell.ned();
        }
    }

    class Venstre extends AbstractAction {
        protected Venstre() {
            super("Venstre");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            modell.venstre();
        }
    }

    class Hoyre extends AbstractAction {
        protected Hoyre() {
            super("hoyre");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            modell.hoyre();
        }
    }

    class P extends AbstractAction {
        protected P() {
            super("P");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            modell.space();
        }
    }

    class R extends AbstractAction {
        protected R() {
            super("R");
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            modell.reset();
        }
    }
}