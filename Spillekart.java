import java.util.Random;
import java.util.ArrayList;
import java.awt.Color;

import java.lang.Math;
import java.util.Iterator;

class Spillekart {

    private Rute[][] rutenett;
    private Slange spillerSlange;
    private ArrayList<Slange> andreSlanger = new ArrayList<>();

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
        finnDodeSlanger();
        fjernForsvunnedeSlanger();
        utplasserMat();
        utplasserAIslange();
        endreRetningAIslanger();

        trekk++;
    }

    public boolean sjekkOmILive() {
        return spillerSlange.sjekkOmILive();
    }

    public void fjernAltFraKartet() {
        
        mat.clear();
        spillerSlange = null; 
        andreSlanger.clear();

        for (int rad = 0; rad<rutenett.length; rad++) {
            for (int kol = 0; kol<rutenett[rad].length; kol++) {
                rutenett[rad][kol] = new Rute(rad,kol);
            }
        }
    }

    private void bevegSlanger() {
        spillerSlange.bevegSlange();
        for (int i = 0; i<andreSlanger.size(); i++) {
            andreSlanger.get(i).bevegSlange();
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

    private void finnDodeSlanger() {
        spillerSlange.sjekkOmILive();
        for (int i = 0; i<andreSlanger.size(); i++) {
            andreSlanger.get(i).sjekkOmILive();
        }
    }

    public void utplasserAIslange() {

        if (andreSlanger.size()>100) {
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

        spillerSlange = new Slange(this,hode,hale,"opp",farge,fart,0);

        hode.plussHode(spillerSlange);
        haledel.plussHale(spillerSlange);
        haletupp.plussHale(spillerSlange);
    }

    public void fjernForsvunnedeSlanger() {

        Iterator<Slange> iterator = andreSlanger.iterator(); // iterator tar over for a kunne fjerne elementer mens iterering foregar
            
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
            maksfart = 10;        
        }

        int fart = rand.nextInt(maksfart)+1; // 3-10, [0, 100s] ->[0,5000 trekk], 3+7/5000

        int storrelse = rand.nextInt(spillerSlange.matSpist()+1)+1;

        Slange aiSlange = new Slange(this,hode,hale,retning,farge,fart,storrelse);

        andreSlanger.add(aiSlange);
    }

    public void endreRetningAIslanger() {
        for (Slange slange : andreSlanger) {
            if (slange.sjekkOmILive()) {
                slange.oppdaterRetning();
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
        spillerSlange.nyRetning(onsketRetning);
    }

    public int scoreSpillerSlange() {
        return spillerSlange.matSpist() * spillerSlange.hentFart();
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

    public double avstandSpiller(Rute rute) {

        int rad1 = rute.hentRad();
        int kol1 = rute.hentKol();

        int rad2 = spillerSlange.hentHode().hentRad();
        int kol2 = spillerSlange.hentHode().hentKol();

        return Math.sqrt((rad1-rad2)*(rad1-rad2)+(kol1-kol2)*(kol1-kol2));
    }

    public int hentTrekk() {
        return trekk;
    }
}