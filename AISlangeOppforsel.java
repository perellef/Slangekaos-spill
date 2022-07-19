import java.util.Random;
import java.util.ArrayList;

class AISlangeOppforsel {

    static Random rand;

    public AISlangeOppforsel(Random rand) {
        this.rand = rand;
    }

    public static String finnStartRetning(int rad, int kol, int antallRader, int antallKol) {

        double plassOpp = 1.7*rad*(0.6 + rand.nextDouble() * (1.0 - 0.60));
        double plassNed = 1.7*(antallRader-rad-1)*(0.6 + rand.nextDouble() * (1.0 - 0.60));
        double plassVenstre = kol*(0.6 + rand.nextDouble() * (1.0 - 0.60));
        double plassHoyre = (antallKol-kol-1)*(0.6 + rand.nextDouble() * (1.0 - 0.60));

        String retning;
        if (plassOpp>=plassNed && plassOpp>=plassVenstre && plassOpp>=plassHoyre) { // opp storste verdi
            retning = "opp";
        }
        else if (plassNed>=plassVenstre && plassNed>=plassHoyre) { // ned storste verdi
            retning = "ned";
        }
        else if (plassVenstre>=plassHoyre) { // venstre storste verdi
            retning = "venstre";
        }
        else { // hoyre storste verdi
            retning = "hoyre";
        }

        return retning;
    }

    public static String finnRetning(Spillekart spillekart, Slange slange) {

        int strategi = rand.nextInt(3);
        /*
        if (strategi==0) {
            return tilfeldig(spillekart,slange);
        }
        else if (strategi==1) {
            return flukt(spillekart,slange);
        }
        */
        return motMat(spillekart,slange);
    }
    
    private static String motMat(Spillekart spillekart, Slange slange) {

        ArrayList<Rute> mat = spillekart.hentMat();

        int rad = slange.hentHode().hentRad();
        int kol = slange.hentHode().hentKol();

        String retning = slange.hentRetning();

        double matbitRad = mat.get(0).hentRad();
        double matbitKol = mat.get(0).hentKol();

        double avstand1 = (matbitRad-rad)*(matbitRad-rad) + (matbitKol-kol)*(matbitKol-kol);

        int indeks = 0;
        
        for (int i = 1; i<mat.size(); i++) {
            matbitRad = mat.get(i).hentRad();
            matbitKol = mat.get(i).hentKol();
            
            double avstand2 = (matbitRad-rad)*(matbitRad-rad) + (matbitKol-kol)*(matbitKol-kol);

            if (avstand1 > avstand2) {
                avstand1 = avstand2;
                indeks = i;
            }
        }
        
        int radDiff = mat.get(indeks).hentRad() - rad;
        int kolDiff = mat.get(indeks).hentKol() - kol;

        String[] retninger = new String[2]; 

        if (radDiff<=0) {
            retninger[0] = "opp";
        } else {
            retninger[0] = "ned";
        }

        if (kolDiff<=0) {
            retninger[1] = "venstre";
        } else {
            retninger[1] = "hoyre";
        }

        String nyRetning;
        if (retning.equals("hoyre") && retninger[1].equals("venstre")) {
            nyRetning = retninger[0];
        }
        else if (retning.equals("venstre") && retninger[1].equals("hoyre")) {
            nyRetning = retninger[0];
        }
        else if (retning.equals("opp") && retninger[0].equals("ned")) {
            nyRetning = retninger[1];
        }
        else if (retning.equals("ned") && retninger[0].equals("opp")) {
            nyRetning = retninger[1];
        }
        else {

            double vertikalt = (radDiff*radDiff)*rand.nextDouble();
            double horisontalt = (radDiff*radDiff)*rand.nextDouble();

            if (vertikalt>horisontalt) {
                nyRetning = retninger[0];
            }
            else {
                nyRetning = retninger[1];
            }
        }
        
        return vurderFlukt(spillekart,slange,nyRetning);
    }

    private static String vurderFlukt(Spillekart spillekart, Slange slange, String nyRetning) {
        if (spillekart.erFarligRetning(slange,nyRetning)) {
            nyRetning = flukt(spillekart,slange);
        }
        return nyRetning;
    }

    private static String flukt(Spillekart spillekart, Slange slange) { // inneholder feil som gjor at de tar "selvmord" (gar rett i motsatt retning)

        String retning = slange.hentRetning();
        Rute hode = slange.hentHode();

        int rad = hode.hentRad();
        int kol = hode.hentKol();

        double oppFare = 0;
        double nedFare = 0;
        double venstreFare = 0;
        double hoyreFare = 0;

        if (!retning.equals("ned")) {
            for (int i = 1; i<4; i++) { // ned - (sjekker hvor farlig det er a bevege seg i motsatt retning)
                if (spillekart.erFare(rad-i,kol)) {
                    oppFare = i;
                    break;
                }
            }
        }

        if (!retning.equals("opp")) {
            for (int i = 1; i<4; i++) { // opp - (sjekker hvor farlig det er a bevege seg i motsatt retning)
                if (spillekart.erFare(rad-i,kol)) {
                    nedFare = i;
                    break;
                }
            }
        }

        if (!retning.equals("venstre")) {
            for (int i = 1; i<4; i++) { // mot hoyre - (sjekker hvor farlig det er a bevege seg i motsatt retning)
                if (spillekart.erFare(rad,kol+i)) {
                    venstreFare = i;
                    break;
                }
            }
        }

        if (!retning.equals("hoyre")) {
            for (int i = 1; i<4; i++) {
                if (spillekart.erFare(rad,kol-i)) { // mot venstre - (sjekker hvor farlig det er a bevege seg i motsatt retning)
                    hoyreFare = i;
                    break;
                }
            }
        }

        oppFare += rand.nextDouble();
        nedFare += rand.nextDouble();
        venstreFare += rand.nextDouble();
        hoyreFare += rand.nextDouble();
        
        String nyRetning;
        if (oppFare>nedFare && oppFare>venstreFare && oppFare>hoyreFare && !retning.equals("opp")) { // opp storste verdi
            nyRetning = "ned";
        }
        else if (nedFare>venstreFare && nedFare>hoyreFare && !retning.equals("ned")) { // ned storste verdi
            nyRetning = "opp";
        }
        else if (venstreFare>hoyreFare && !retning.equals("hoyre")) { // venstre storste verdi
            nyRetning = "venstre";
        }
        else if (!retning.equals("venstre")) { // hoyre storste verdi
            nyRetning = "hoyre";
        }
        else {
            nyRetning = tilfeldig(spillekart,slange);
        }

        return nyRetning;
    }

    private static String tilfeldig(Spillekart spillekart, Slange slange) {

        String retning = slange.hentRetning();
        
        String[] retninger = new String[3];
        
        int i = 0;
        if (!retning.equals("ned")) {
            retninger[i] = "opp";
            i++;
        }
        if (!retning.equals("opp")) {
            retninger[i] = "ned";
            i++;
        }
        if (!retning.equals("hoyre")) {
            retninger[i] = "venstre";
            i++;
        }
        if (!retning.equals("venstre")) {
            retninger[i] = "hoyre";
        }

        String nyRetning = retninger[rand.nextInt(3)];

        return nyRetning;
    }
}