/** Berechnet ein CIDR-Netz ohne Netzwerkzugriffe oder Adressbibliotheken. */
public final class Subnetz {
    private final Ipv4Adresse adresse;
    private final int praefix;
    private final long maske;
    private final long netzwerk;
    private final long letzteAdresse;

    public Subnetz(Ipv4Adresse adresse, int praefix) {
        if (adresse == null) {
            throw new IllegalArgumentException("Eine IPv4-Adresse wird benötigt.");
        }
        if (praefix < 0 || praefix > 32) {
            throw new IllegalArgumentException("Das CIDR-Präfix muss zwischen 0 und 32 liegen.");
        }
        this.adresse = adresse;
        this.praefix = praefix;
        // long erlaubt auch die Verschiebung um 32 Bits für /0; auf 32 Bits begrenzen.
        maske = (Ipv4Adresse.MAX_WERT << (32 - praefix)) & Ipv4Adresse.MAX_WERT;
        netzwerk = adresse.getWert() & maske;
        // ~ invertiert 64 Bits. Die UND-Maske entfernt die oberen 32 Bits wieder.
        long hostmaske = (~maske) & Ipv4Adresse.MAX_WERT;
        letzteAdresse = netzwerk | hostmaske;
    }

    public static Subnetz ausCidr(String eingabe) {
        if (eingabe == null) {
            throw new IllegalArgumentException("Bitte IPv4/Präfix eingeben.");
        }
        String[] teile = eingabe.trim().split("/", -1);
        if (teile.length != 2) {
            throw new IllegalArgumentException("Format: IPv4/Präfix, z. B. 192.168.1.42/24.");
        }
        String praefixText = teile[1].trim();
        if (!praefixText.matches("0|[1-9][0-9]?")) {
            throw new IllegalArgumentException("Das Präfix muss eine Dezimalzahl von 0 bis 32 sein.");
        }
        return new Subnetz(Ipv4Adresse.parsen(teile[0]), Integer.parseInt(praefixText));
    }

    public static Subnetz ausMaske(String adresse, String subnetzmaske) {
        Ipv4Adresse ip = Ipv4Adresse.parsen(adresse);
        long maskenwert = Ipv4Adresse.parsen(subnetzmaske).getWert();
        int praefix = 0;
        boolean nullGefunden = false;
        for (int bit = 31; bit >= 0; bit--) {
            boolean gesetzt = ((maskenwert >>> bit) & 1) == 1;
            if (gesetzt && nullGefunden) {
                throw new IllegalArgumentException("Ungültige Subnetzmaske: Auf die führenden Einsen dürfen nur Nullen folgen.");
            }
            if (gesetzt) {
                praefix++;
            } else {
                nullGefunden = true;
            }
        }
        return new Subnetz(ip, praefix);
    }

    public Ipv4Adresse getAdresse() { return adresse; }
    public int getPraefix() { return praefix; }
    public Ipv4Adresse getMaske() { return Ipv4Adresse.ausWert(maske); }
    public Ipv4Adresse getNetzwerk() { return Ipv4Adresse.ausWert(netzwerk); }
    public Ipv4Adresse getLetzteAdresse() { return Ipv4Adresse.ausWert(letzteAdresse); }
    public boolean hatBroadcast() { return praefix <= 30; }

    public Ipv4Adresse getBroadcast() {
        if (!hatBroadcast()) {
            throw new IllegalStateException("/31 und /32 besitzen hier keine Subnetz-Broadcastadresse.");
        }
        return getLetzteAdresse();
    }

    public long getAdressanzahl() {
        // 1L ist wichtig: Ein int könnte 2^32 nicht darstellen.
        return 1L << (32 - praefix);
    }

    public long getHostanzahl() {
        return praefix >= 31 ? getAdressanzahl() : getAdressanzahl() - 2;
    }

    public Ipv4Adresse getErsterHost() {
        return Ipv4Adresse.ausWert(praefix >= 31 ? netzwerk : netzwerk + 1);
    }

    public Ipv4Adresse getLetzterHost() {
        return Ipv4Adresse.ausWert(praefix >= 31 ? letzteAdresse : letzteAdresse - 1);
    }

    public String getNetzCidr() { return getNetzwerk() + "/" + praefix; }

    public String getHinweis() {
        if (praefix == 31) {
            return "/31: Punkt-zu-Punkt-Verbindung nach RFC 3021; beide Adressen sind Endpunkte, kein Subnetz-Broadcast.";
        }
        if (praefix == 32) {
            return "/32: Einzeladresse (Hostroute); ein Host, kein Subnetz-Broadcast.";
        }
        return "Hostanzahl und Hostbereich sind rechnerisch; Sonder- und reservierte Adressbereiche werden nicht abgezogen.";
    }
}
