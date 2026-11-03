import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Main {
    private Main() { }

    public static void main(String[] argumente) {
        PrintStream ausgabe = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        if (argumente.length == 1 && argumente[0].equals("--demo")) {
            KonsolenAusgabe.anzeigen(Subnetz.ausCidr("192.168.1.130/26"), ausgabe);
        } else if (argumente.length == 0) {
            starten(System.in, ausgabe);
        } else {
            new PrintStream(System.err, true, StandardCharsets.UTF_8)
                    .println("Aufruf: java -jar ipv4-subnet-calculator-1.0.0.jar [--demo]");
        }
    }

    static void starten(InputStream quelle, PrintStream ausgabe) {
        Scanner scanner = new Scanner(quelle, StandardCharsets.UTF_8);
        ausgabe.println("IPv4-Subnetzrechner");
        try {
            while (true) {
                ausgabe.println("\n1 IPv4/CIDR | 2 IPv4 und Subnetzmaske | 3 Beispiel | 0 Beenden");
                String auswahl = lesen(scanner, ausgabe, "Auswahl: ");
                try {
                    switch (auswahl) {
                        case "0" -> { ausgabe.println("Auf Wiedersehen!"); return; }
                        case "1" -> KonsolenAusgabe.anzeigen(Subnetz.ausCidr(lesen(scanner, ausgabe, "IPv4/CIDR: ")), ausgabe);
                        case "2" -> {
                            String adresse = lesen(scanner, ausgabe, "IPv4-Adresse: ");
                            String maske = lesen(scanner, ausgabe, "Subnetzmaske (z. B. 255.255.255.0): ");
                            KonsolenAusgabe.anzeigen(Subnetz.ausMaske(adresse, maske), ausgabe);
                        }
                        case "3" -> KonsolenAusgabe.anzeigen(Subnetz.ausCidr("192.168.1.130/26"), ausgabe);
                        default -> ausgabe.println("Bitte 0, 1, 2 oder 3 wählen.");
                    }
                } catch (IllegalArgumentException exception) {
                    ausgabe.println("Eingabefehler: " + exception.getMessage());
                    ausgabe.println("Bitte erneut einen Menüpunkt wählen.");
                }
            }
        } catch (NoSuchElementException exception) {
            ausgabe.println("\nEingabe beendet. Programm wird geschlossen.");
        }
    }

    private static String lesen(Scanner scanner, PrintStream ausgabe, String frage) {
        ausgabe.print(frage);
        if (!scanner.hasNextLine()) {
            throw new NoSuchElementException("Eingabe beendet.");
        }
        return scanner.nextLine().trim();
    }
}
