# IPv4-Subnetzrechner

Konsolenanwendung in Java zur Berechnung von IPv4-Subnetzen. Eingaben sind als CIDR-Adresse oder als IPv4-Adresse mit punktierter Subnetzmaske möglich. Die Berechnung erfolgt mit Bitoperationen ohne Netzwerkzugriffe.

## Voraussetzungen

- JDK 17 oder neuer
- UTF-8 als Zeichenkodierung

## Starten

```shell
javac -encoding UTF-8 --release 17 -d out src/*.java
java -cp out Main
```

Beispiel ausführen:

```shell
java -cp out Main --demo
```

Einstiegspunkt ist `src/Main.java`. Das Projekt benötigt nur das JDK und keine externen Bibliotheken.

## Funktionen

- CIDR-Präfixe von /0 bis /32
- Validierung punktierter Subnetzmasken
- Netzwerkadresse, Broadcastadresse und Hostbereich
- Anzahl der Adressen und rechnerisch nutzbaren Hosts
- Binärdarstellung von IP, Maske, Netzwerk, oberer Blockgrenze und Hostgrenzen
- Gesonderte Behandlung von /31 und /32

## Beispiel

```text
Eingabe:         192.168.1.130/26
Netzwerk/CIDR:   192.168.1.128/26
Subnetzmaske:    255.255.255.192
Broadcast:      192.168.1.191
Hostbereich:    192.168.1.129 bis 192.168.1.190
Adressen:       64
Hosts:          62

IP:             11000000.10101000.00000001.10000010
Maske:          11111111.11111111.11111111.11000000
Netzwerk:       11000000.10101000.00000001.10000000
```

## Eingaberegeln

IPv4-Adressen bestehen aus vier dezimalen Oktetten zwischen 0 und 255. Vorzeichen, führende Nullen, Hostnamen und IPv6-Adressen werden abgewiesen. Subnetzmasken müssen aus zusammenhängenden führenden Einsen und anschließenden Nullen bestehen.

Ungültige Eingaben führen zu einer Fehlermeldung und der Rückkehr ins Menü. Eingabeende (EOF) beendet die Anwendung ohne Stacktrace.

## Berechnung und Sonderfälle

IPv4-Werte werden als positive `long`-Werte gespeichert. Eine Maske begrenzt die Operationen auf die unteren 32 Bits.

```text
Netzwerk       = IP & Maske
Hostmaske      = ~Maske & 0xFFFFFFFFL
Letzte Adresse = Netzwerk | Hostmaske
Adressanzahl   = 1L << (32 − Präfix)
```

| Präfix | Hostberechnung | Broadcast |
| --- | --- | --- |
| /0–/30 | Adressanzahl minus 2 | obere Blockgrenze |
| /31 | beide Adressen als Punkt-zu-Punkt-Endpunkte | entfällt |
| /32 | eine Einzeladresse / Hostroute | entfällt |

Die /31-Behandlung folgt dem Punkt-zu-Punkt-Modell aus [RFC 3021](https://www.rfc-editor.org/rfc/rfc3021). Die Hostanzahl ist rein rechnerisch: Reservierte und andere Sonderadressbereiche werden nicht abgezogen. Bei /0 ist die obere Blockgrenze `255.255.255.255`, die zugleich die besondere Bedeutung des Limited Broadcast hat.

## Aufbau

Der Quellcode liegt in `src`, die Tests in `tests`.

- `Ipv4Adresse`: unveränderliches Wertobjekt, Parsing und Darstellung
- `Subnetz`: Maskenprüfung und Subnetzberechnung
- `KonsolenAusgabe`: Ergebnisdarstellung
- `Main`: Menü und Eingabeverarbeitung

## Tests

Die Tests sind eigenständige Java-Programme ohne externe Bibliotheken:

```shell
javac -encoding UTF-8 --release 17 -d out src/*.java tests/*.java
java -cp out ProjektTest
```

Bei einer fehlgeschlagenen Prüfung wird ein `AssertionError` ausgelöst. Die Tests prüfen bekannte Ergebnisse, Randfälle und ungültige Eingaben.
