package src;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;

import java.lang.Math;
import java.util.Iterator;
import java.util.List;

class Spillekart {

    private Rute[][] rutenett;
    private ArrayList<Slange> slanger = new ArrayList<>(); // indeks 0: spillerslange

    private Random rand = new Random();
    private ArrayList<Rute> mat = new ArrayList<>();

    private int trekk;

    public Spillekart(int m,int n) {
        rutenett = new Rute[m][n];
        new AISlangeOppforsel(rand);
    }

    public void reset() {

        fjernAltFraKartet();
        lagSpillerSlange();
        utplasserMat();

        trekk = 0;

    }

    public void oppdater() {

        bevegSlanger();
        fjernSpistMat();
        splittDodeMorslanger();
        fjernForsvunnedeSlanger();
        utplasserMat();
        utplasserAIslange();
        endreRetningAIslanger();

        trekk++;
    }

    public boolean sjekkOmILive() {
        return slanger.get(0).sjekkOmILive();
    }

    public void fjernAltFraKartet() {
        
        mat.clear();
        slanger.clear();

        for (int rad = 0; rad<rutenett.length; rad++) {
            for (int kol = 0; kol<rutenett[rad].length; kol++) {
                rutenett[rad][kol] = new Rute(rad,kol);
            }
        }
    }

    private void bevegSlanger() {
        for (int i = 0; i<slanger.size(); i++) {
            slanger.get(i).bevegSlange();
        }
    }

    private void fjernSpistMat() {
        for (int rad = 0; rad<rutenett.length; rad++) {
            for (int kol = 0; kol<rutenett[0].length; kol++) {
                Rute rute = rutenett[rad][kol];
                
                if (rute.antallHoder()>=1) {
                    rute.fjernMat();
                    mat.remove(rute);
                }
            }
        }
    }

    public void utplasserAIslange() {

        if (slanger.size()>100) {
            return;
        }

        int trekkPerNyeSlange;
        if (trekk<=5000) {
            trekkPerNyeSlange = 100-(int) (Math.floor(trekk*8.5/500.0));
        } else {
            trekkPerNyeSlange = 15;        
        }

        if (trekk % trekkPerNyeSlange != 0) { // [% 20, % 100], [0,100s] = [0,5000 trekk] 100-trekk*8/500
            return;
        }

        for (int i = 0; i<10; i++) {

            int rad = rand.nextInt(rutenett.length);
            int kol = rand.nextInt(rutenett[0].length);

            Rute rute = rutenett[rad][kol];

            if (rute.antallHoder()>=1 || rute.antallHaler()>=1 || rute.harMat() || avstandSpiller(rute)<20) {
                continue;
            }

            lagAIslange(rad, kol);
            return;
        }
    }

    public void utplasserMat() {

        for (int i = 0; i<10; i++) {
            
            if (mat.size()>=6) {
                break;
            }

            int rad = rand.nextInt(rutenett.length);
            int kol = rand.nextInt(rutenett[0].length);

            Rute rute = rutenett[rad][kol];

            if (rute.antallHoder()>=1 || rute.antallHaler()>=1) {
                continue;
            }

            mat.add(rute);
            rute.settMat();
        }
    }

    public void lagSpillerSlange() {

        Rute hode = hentRute(18,35);
        Rute haledel = hentRute(19,35);
        Rute haletupp = hentRute(20,35);

        ArrayList<Rute> hale = new ArrayList<>();

        hale.add(haletupp);
        hale.add(haledel);

        Color farge = Color.BLUE;
        int fart = 7;

        Slange spillerSlange = new Slange(this,hode,hale,"opp",farge,fart,0,"spiller");

        hode.plussHode(spillerSlange);
        haledel.plussHale(spillerSlange);
        haletupp.plussHale(spillerSlange);

        slanger.add(spillerSlange);
    }

    public void splittDodeMorslanger() {
        Iterator<Slange> iterator = slanger.iterator(); // iterator tar over for a kunne fjerne elementer mens iterering foregar
        iterator.next();

        List<Slange> nyeSlanger = new ArrayList<>();

        while (iterator.hasNext()) {
            Slange slange = iterator.next();

            if (!slange.sjekkOmILive() && slange.erMorslange()) {
                ArrayList<Slange> barn = splittMorslange(slange);
                if (!barn.isEmpty()) { 
                    nyeSlanger.addAll(barn);
                    iterator.remove();
                }
            }
        }
        slanger.addAll(nyeSlanger);
    }

    public ArrayList<Slange> splittMorslange(final Slange slange) {
        ArrayList<Slange> barn = new ArrayList<>();
        int antallBarn = (slange.hentHale().size()+1)/3;
        if (antallBarn < 2) {
            return barn;
        }

        for (int i = 0; i < antallBarn; i++) {
            Rute hode = slange.hentHale().get(3*i);
            ArrayList<Rute> hale = new ArrayList<>();
            hale.add(slange.hentHale().get(3*i+1));

            Slange babyslange = new Slange(this,hode,hale,slange.hentRetning(),slange.hentBakgrunnsfarge(),slange.hentFart(),0,"morslange");
            
            barn.add(babyslange);
        }
        slange.fjern();
        return barn;
    }

    public void fjernForsvunnedeSlanger() {
        Iterator<Slange> iterator = slanger.iterator(); // iterator tar over for a kunne fjerne elementer mens iterering foregar
        iterator.next();

        while (iterator.hasNext()) {
            Slange slange = iterator.next();

            if (slange.forsvunnet()) {
                slange.fjern();
                iterator.remove();
            }
        }
    }

    public void lagAIslange(int rad, int kol) {

        Rute hode = hentRute(rad,kol);
        ArrayList<Rute> hale = new ArrayList<>();

        String retning = AISlangeOppforsel.finnStartRetning(rad,kol,rutenett.length,rutenett[0].length);

        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();

        Color farge = new Color(r,g,b);

        int maksfart;
        if (trekk<=5000) {
            maksfart = 3 + (int) Math.floor(trekk*7/5000);
        } else {
            maksfart = 9;        
        }
        int fart = rand.nextInt(maksfart)+1; // 3-10, [0, 100s] ->[0,5000 trekk], 3+7/5000

        String slangetype = velgSlangetype();

        int storrelse = rand.nextInt(slanger.get(0).matSpist()+1)+1;
        if (slangetype == "drapsslange") {
            storrelse = Math.min(slanger.get(0).matSpist()/3+1, rand.nextInt(2,6));
        } else if (slangetype == "fluktslange") {
            storrelse = Math.min(slanger.get(0).matSpist()+1, rand.nextInt(2,6));
        }

        Slange aiSlange = new Slange(this, hode, hale, retning, farge, fart, storrelse, slangetype);
        slanger.add(aiSlange);
    }

    public String velgSlangetype() {
        List<String> options = new ArrayList<>();
        
        options.add("matslange");

        if (trekk > 1000) {options.add("fluktslange");}
        if (trekk > 2000) {options.add("drapsslange");}
        if (trekk > 2750) {options.add("morslange");}
        if (trekk > 3000) {options.add("spøkelsesslange");}

        Random rand = new Random();
        return options.get(rand.nextInt(options.size()));
    }

    public void endreRetningAIslanger() {
        for (int i = 1; i < slanger.size(); i++) {
            if (slanger.get(i).sjekkOmILive()) {
                slanger.get(i).oppdaterRetning();
            }
        }
    }

    public boolean erInnenforKartet(int rad, int kol) {
        return !(rad<0 || kol<0 || rad>=rutenett.length || kol>=rutenett[0].length);
    }

    public Rute[][] hentRutenett() {
        return rutenett;
    }

    public Rute hentRute(int rad, int kol) {
        return rutenett[rad][kol];
    }

    public void nyRetningSpillerSlange(String onsketRetning) {
        slanger.get(0).nyRetning(onsketRetning);
    }

    public int scoreSpillerSlange() {
        return slanger.get(0).matSpist() * slanger.get(0).hentFart();
    }

    public boolean erFare(int rad, int kol) {
        if (!erInnenforKartet(rad,kol)) {
            return true;
        }
        Rute rute = rutenett[rad][kol];

        return rute.antallHoder()>0 || rute.antallHaler()>0;
    }

    public boolean erFarligRetning(Slange slange, String retning) {

        int rad = slange.hentHode().hentRad();
        int kol = slange.hentHode().hentKol();

        if (retning.equals("opp")) {
            rad--;
        }
        else if (retning.equals("ned")) {
            rad++;
        }
        else if (retning.equals("venstre")) {
            kol--;
        }
        else {
            kol++;
        }

        return erFare(rad, kol);
    }

    public ArrayList<Rute> hentMat() {
        return mat;
    }

    public ArrayList<Slange> hentSlanger() {
        return slanger;
    }

    public double avstandSpiller(Rute rute) {

        int rad1 = rute.hentRad();
        int kol1 = rute.hentKol();

        int rad2 = slanger.get(0).hentHode().hentRad();
        int kol2 = slanger.get(0).hentHode().hentKol();

        return Math.sqrt((rad1-rad2)*(rad1-rad2)+(kol1-kol2)*(kol1-kol2));
    }

    public int hentTrekk() {
        return trekk;
    }

    public int antallRader() {
        return rutenett.length;
    }

    public int antallKolonner() {
        return rutenett[0].length;
    }
}