/** IPv4 wird als positiver long gespeichert, dessen untere 32 Bits die Adresse enthalten. */
public final class Ipv4Adresse {
    public static final long MAX_WERT = 0xFFFF_FFFFL;
    private final long wert;

    private Ipv4Adresse(long wert) {
        this.wert = wert;
    }

    public static Ipv4Adresse ausWert(long wert) {
        if (wert < 0 || wert > MAX_WERT) {
            throw new IllegalArgumentException("IPv4-Wert muss zwischen 0 und 4294967295 liegen.");
        }
        return new Ipv4Adresse(wert);
    }

    public static Ipv4Adresse parsen(String eingabe) {
        if (eingabe == null) {
            throw new IllegalArgumentException("Eine IPv4-Adresse wird benötigt.");
        }
        String[] oktette = eingabe.trim().split("\\.", -1);
        if (oktette.length != 4) {
            throw new IllegalArgumentException("IPv4 benötigt vier Oktette, z. B. 192.168.1.10.");
        }
        long wert = 0;
        for (String oktett : oktette) {
            if (!oktett.matches("0|[1-9][0-9]{0,2}")) {
                throw new IllegalArgumentException("Oktette müssen Dezimalzahlen ohne Vorzeichen oder führende Nullen sein.");
            }
            int zahl = Integer.parseInt(oktett);
            if (zahl > 255) {
                throw new IllegalArgumentException("Jedes Oktett muss zwischen 0 und 255 liegen.");
            }
            // Acht Bits Platz schaffen und das nächste Oktett mit ODER einsetzen.
            wert = (wert << 8) | zahl;
        }
        return new Ipv4Adresse(wert);
    }

    public long getWert() { return wert; }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        for (int verschiebung = 24; verschiebung >= 0; verschiebung -= 8) {
            if (text.length() > 0) {
                text.append('.');
            }
            text.append((wert >>> verschiebung) & 0xFF);
        }
        return text.toString();
    }

    public String binaer() {
        StringBuilder text = new StringBuilder();
        for (int bit = 31; bit >= 0; bit--) {
            text.append((wert >>> bit) & 1);
            if (bit > 0 && bit % 8 == 0) {
                text.append('.');
            }
        }
        return text.toString();
    }

    @Override
    public boolean equals(Object anderes) {
        if (this == anderes) {
            return true;
        }
        if (!(anderes instanceof Ipv4Adresse)) {
            return false;
        }
        return wert == ((Ipv4Adresse) anderes).wert;
    }

    @Override
    public int hashCode() { return Long.hashCode(wert); }
}
