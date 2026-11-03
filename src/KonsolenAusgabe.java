import java.io.PrintStream;

final class KonsolenAusgabe {
    private KonsolenAusgabe() { }

    static void anzeigen(Subnetz subnetz, PrintStream ausgabe) {
        ausgabe.println("\n=== Subnetzberechnung ===");
        ausgabe.println("Eingabe:         " + subnetz.getAdresse() + "/" + subnetz.getPraefix());
        ausgabe.println("Netzwerk/CIDR:   " + subnetz.getNetzCidr());
        ausgabe.println("Subnetzmaske:    " + subnetz.getMaske());
        ausgabe.println("Broadcast:      " + (subnetz.hatBroadcast() ? subnetz.getBroadcast() : "entfällt"));
        ausgabe.println("Letzte Adresse: " + subnetz.getLetzteAdresse());
        ausgabe.println("Hostbereich:    " + subnetz.getErsterHost() + " bis " + subnetz.getLetzterHost());
        ausgabe.println("Adressen:       " + subnetz.getAdressanzahl());
        ausgabe.println("Hosts:          " + subnetz.getHostanzahl());
        ausgabe.println("Netz-/Hostbits: " + subnetz.getPraefix() + " / " + (32 - subnetz.getPraefix()));
        ausgabe.println(subnetz.getHinweis());
        ausgabe.println("\nBinärdarstellung (vier Oktette mit je acht Bits):");
        ausgabe.println("IP:             " + subnetz.getAdresse().binaer());
        ausgabe.println("Maske:          " + subnetz.getMaske().binaer());
        ausgabe.println("Netz (IP & M):  " + subnetz.getNetzwerk().binaer());
        ausgabe.println("Letzte Adresse: " + subnetz.getLetzteAdresse().binaer());
        ausgabe.println("Erster Host:    " + subnetz.getErsterHost().binaer());
        ausgabe.println("Letzter Host:   " + subnetz.getLetzterHost().binaer());
        ausgabe.println("Bitoperationen: Netzwerk = IP & Maske; letzte Adresse = Netzwerk | (~Maske & 0xFFFFFFFFL).");
    }
}
