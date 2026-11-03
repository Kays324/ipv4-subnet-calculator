import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ProjektTest {
    private static int pruefungen;

    public static void main(String[] argumente) {
        Subnetz beispiel = Subnetz.ausCidr("192.168.1.130/26");
        pruefen(beispiel.getNetzCidr().equals("192.168.1.128/26"), "Netzwerk");
        pruefen(beispiel.getMaske().toString().equals("255.255.255.192"), "Maske");
        pruefen(beispiel.getBroadcast().toString().equals("192.168.1.191"), "Broadcast");
        pruefen(beispiel.getErsterHost().toString().equals("192.168.1.129"), "Erster Host");
        pruefen(beispiel.getLetzterHost().toString().equals("192.168.1.190"), "Letzter Host");
        pruefen(beispiel.getAdressanzahl() == 64 && beispiel.getHostanzahl() == 62, "Adressanzahl");
        pruefen(beispiel.getAdresse().binaer().equals("11000000.10101000.00000001.10000010"), "Binaerdarstellung");
        pruefen(Ipv4Adresse.parsen("255.255.255.255").getWert() == 4_294_967_295L, "32-Bit-Grenze");
        Subnetz komplett = Subnetz.ausCidr("192.0.2.1/0");
        pruefen(komplett.getAdressanzahl() == 4_294_967_296L, "Adressanzahl /0");
        pruefen(komplett.getHostanzahl() == 4_294_967_294L, "Hostanzahl /0");
        Subnetz verbindung = Subnetz.ausCidr("192.0.2.11/31");
        pruefen(verbindung.getHostanzahl() == 2 && !verbindung.hatBroadcast(), "Punkt-zu-Punkt-Netz");
        pruefen(verbindung.getErsterHost().toString().equals("192.0.2.10"), "Erster Endpunkt");
        pruefen(verbindung.getLetzterHost().toString().equals("192.0.2.11"), "Zweiter Endpunkt");
        Subnetz einzeladresse = Subnetz.ausCidr("192.0.2.7/32");
        pruefen(einzeladresse.getHostanzahl() == 1 && !einzeladresse.hatBroadcast(), "Hostroute");
        pruefen(einzeladresse.getErsterHost().equals(einzeladresse.getLetzterHost()), "Hostgrenzen /32");

        Random zufall = new Random(73);
        for (int praefix = 0; praefix <= 32; praefix++) {
            for (int durchgang = 0; durchgang < 40; durchgang++) {
                long wert = Integer.toUnsignedLong(zufall.nextInt());
                Subnetz subnetz = new Subnetz(Ipv4Adresse.ausWert(wert), praefix);
                long blockgroesse = (long) Math.pow(2, 32 - praefix);
                long netz = (wert / blockgroesse) * blockgroesse;
                pruefen(subnetz.getNetzwerk().getWert() == netz, "Netzwerk /" + praefix);
                pruefen(subnetz.getLetzteAdresse().getWert() == netz + blockgroesse - 1, "Blockgrenze /" + praefix);
                pruefen(subnetz.getAdressanzahl() == blockgroesse, "Blockgroesse /" + praefix);
                pruefen(subnetz.getMaske().binaer().replace(".", "").equals("1".repeat(praefix) + "0".repeat(32 - praefix)), "Maskenbits");
                pruefen(Subnetz.ausMaske(subnetz.getAdresse().toString(), subnetz.getMaske().toString()).getPraefix() == praefix, "Maskeneingabe");
            }
        }
        for (String adresse : new String[] {null, "", "1.2.3", "256.1.1.1", "01.2.3.4", "-1.2.3.4", "1.2.3.", "localhost", "::1"}) {
            erwarteFehler(() -> Ipv4Adresse.parsen(adresse));
        }
        for (String cidr : new String[] {null, "", "192.0.2.1", "192.0.2.1/-1", "192.0.2.1/33", "192.0.2.1/24/1"}) {
            erwarteFehler(() -> Subnetz.ausCidr(cidr));
        }
        for (String maske : new String[] {"255.0.255.0", "255.255.127.0", "0.0.0.1"}) {
            erwarteFehler(() -> Subnetz.ausMaske("192.0.2.1", maske));
        }

        ByteArrayOutputStream puffer = new ByteArrayOutputStream();
        Main.starten(new ByteArrayInputStream("1\n256.1.1.1/24\n3\n0\n".getBytes(StandardCharsets.UTF_8)),
                new PrintStream(puffer, true, StandardCharsets.UTF_8));
        String ausgabe = puffer.toString(StandardCharsets.UTF_8);
        pruefen(ausgabe.contains("Eingabefehler"), "Fehlerhafte Eingabe im Menue");
        pruefen(ausgabe.contains("192.168.1.128/26"), "Beispiel im Menue");
        pruefen(ausgabe.contains("Auf Wiedersehen!"), "Programmende");
        puffer.reset();
        Main.starten(new ByteArrayInputStream(new byte[0]), new PrintStream(puffer, true, StandardCharsets.UTF_8));
        pruefen(puffer.toString(StandardCharsets.UTF_8).contains("Eingabe beendet"), "EOF");
        System.out.println(pruefungen + " Pruefungen erfolgreich.");
    }

    private static void pruefen(boolean richtig, String beschreibung) {
        if (!richtig) {
            throw new AssertionError(beschreibung);
        }
        pruefungen++;
    }

    private static void erwarteFehler(Runnable aktion) {
        try {
            aktion.run();
        } catch (IllegalArgumentException exception) {
            pruefungen++;
            return;
        }
        throw new AssertionError("Ungueltige Eingabe wurde akzeptiert.");
    }
}
