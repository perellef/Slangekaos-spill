package src;
import java.util.ArrayList;
import java.awt.Color;
//import javax.swing.text.html.StyleSheet;

import java.lang.Math;

class Slange {


    //private static StyleSheet s = new StyleSheet(); // for farge-overganger

    private Spillekart spillekart;

    private Rute hode;
    private ArrayList<Rute> hale; // forste element er tuppen av halen
    private String retning;
    private String nyRetning;
    private int aatselLevetid = 100;
    private int aatsel = 100;

    private Color startfarge;
    private Color farge;
    private int matSpist = 0;
    private int fart;
    private int trekkTeller = 0;
    private int vokser;

    public Slange(Spillekart spillekart, Rute hode, ArrayList<Rute> hale, String retning, Color startfarge, int fart, int vokser) {
        this.spillekart = spillekart;
        this.hode = hode;
        this.hale = hale;
        this.retning = retning;
        this.nyRetning = retning;
        this.startfarge = startfarge;
        this.farge = startfarge;
        this.fart = fart;
        this.vokser = vokser;
    }

    public void bevegSlange() {

        if (hode == null) {
            aatsel--;
            nyFarge();
            return;
        }

        if (10-trekkTeller>fart) {
            trekkTeller++;
            return;
        }

        trekkTeller = 0;

        int hodeRad = hode.hentRad();
        int hodeKol = hode.hentKol();

        if (nyRetning.equals("opp")) {
            hodeRad--;
        }
        else if (nyRetning.equals("ned")) {
            hodeRad++;
        }
        else if (nyRetning.equals("venstre")) {
            hodeKol--;
        }
        else if (nyRetning.equals("hoyre")) {
            hodeKol++;
        }
        else {
            System.out.println("Ikke definert retning");
            System.exit(0);
        }

        Rute nyttHode = null;
        
        if (spillekart.erInnenforKartet(hodeRad,hodeKol)) { // holder seg i kartet
            
            nyttHode = spillekart.hentRute(hodeRad,hodeKol); // spiser mat    
        
            if (nyttHode.harMat()) { // spiser mat
                vokser++;
                matSpist++;
            }
        }

        oppdaterSlange(nyttHode,vokser>0);

        retning = nyRetning;
    }

    private void oppdaterSlange(Rute nyttHode, boolean forstorres) {    
        
        hale.add(hode);
        hode.minusHode(this);
        hode = nyttHode;
        
        if (forstorres) {
            vokser--;
        }
        else {
            hale.get(0).minusHale(this);
            hale.remove(0);
        }

        oppdaterRuterMedSlange();
    }

    private void oppdaterRuterMedSlange() {
        if (hode!=null) {
            hode.plussHode(this);
        }
        hale.get(hale.size()-1).plussHale(this);
        
    }

    public boolean sjekkOmILive() {

        if (hode==null) {
            return false;
        }
        
        boolean condition1 = (hode.antallHoder()>=2);
        boolean condition2 = (hode.antallHaler()>=1);

        if (condition1 || condition2) {
            hode.minusHode(this);
            hode = null;
            return false;
        }
        return true;
    }

    public void oppdaterRetning() {

        if (10-trekkTeller>fart) {
            return;
        }
        nyRetning = AISlangeOppforsel.finnRetning(spillekart,this);
    }

    public void nyRetning(String onsketRetning) {
        
        if ((retning.equals("opp") || retning.equals("ned")) && (onsketRetning.equals("venstre") || onsketRetning.equals("hoyre"))) {
            nyRetning = onsketRetning;
            return;
        }
        if ((retning.equals("venstre") || retning.equals("hoyre")) && (onsketRetning.equals("opp") || onsketRetning.equals("ned"))) {
            nyRetning = onsketRetning;
            return;
        }
    }

    public Color hentFarge() {
        return farge;
    }

    @Override
    public String toString() {

        String str = "";
            
        if (hode != null) {
            str += "[" + hode.hentRad() + "," + hode.hentKol() + "]";
        }

        for (int i = hale.size()-1; i>=0; i--) {
            Rute haledel = hale.get(i);
            str += "-(" + haledel.hentRad() + "," + haledel.hentKol() + ")";
        }
        return str;
    }

    public int matSpist() {
        return matSpist;
    }

    public int hentFart() {
        return fart;
    }

    public String hentRetning() {
        return retning;
    }

    public Rute hentHode() {
        return hode;
    }

    public boolean forsvunnet() {
        return aatsel <= 0;
    }

    public void fjern() {

        if (hode!=null) {
            hode.minusHode(this);
        }

        for (Rute haledel : hale) {
            haledel.minusHale(this);
        }
    }

    public void nyFarge() {

        Color bakgrunnsfarge = Color.LIGHT_GRAY;

        int r1 = bakgrunnsfarge.getRed();
        int g1 = bakgrunnsfarge.getGreen();
        int b1 = bakgrunnsfarge.getBlue();

        int r2 = startfarge.getRed();
        int g2 = startfarge.getGreen();
        int b2 = startfarge.getBlue();

        double w = 1-((double) aatsel)/aatselLevetid;

        int r = (int) Math.sqrt(r1*r1*w + r2*r2*(1 - w));
        int g = (int) Math.sqrt(g1*g1*w + g2*g2*(1 - w));
        int b = (int) Math.sqrt(b1*b1*w + b2*b2*(1 - w));

        farge = new Color(r,g,b);
    }
}