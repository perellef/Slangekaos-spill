package src;
import java.util.ArrayList;
import java.awt.Color;

class Rute {

    private int rad;
    private int kol;
    private boolean harMat = false;
    private ArrayList<Slange> hoder = new ArrayList<>();
    private ArrayList<Slange> haler = new ArrayList<>();

    public Rute(int rad, int kol) {
        this.rad = rad;
        this.kol = kol;
    }

    public Color hentFarge() {

        if (antallHoder()>=1) {
            return hoder.get(0).hentFarge();
        }
        else if (antallHaler()>=1) {
            return haler.get(0).hentFarge();
        }
        return Color.LIGHT_GRAY;
    }

    public String hentTekst() {
        if (harMat()) {
            return "\uD83C\uDF4E";
        }
        else if (antallHaler()>=1) {
            return "+";
        }
        else if (antallHoder()>=1) {
            return "o";
        }
        return "";
    }

    public void oppdaterMatStatus(boolean matStatus) {
        harMat = matStatus;
    }

    public void plussHode(Slange slange) {
        hoder.add(slange);
    }

    public void minusHode(Slange slange) {
        hoder.remove(slange);
    }

    public void plussHale(Slange slange) {
        haler.add(slange);
    }

    public void minusHale(Slange slange) {
        haler.remove(slange);
    }

    public void settMat() {
        harMat = true;
    }

    public void fjernMat() {
        harMat = false;
    }

    public boolean harMat() {
        return harMat;
    }

    public int antallHoder() {
        return hoder.size();
    }

    public int antallHaler() {
        return haler.size();
    }

    public int hentRad() {
        return rad;
    }

    public int hentKol() {
        return kol;
    }  

    public String toString() {
        return "(" + rad + "," + kol + ")";
    }
}