# PassManager

PassManager intends to be a simple  application for storing passwords associated with a domain and a user-account securely (password-vault). This application is written in JavaFX.

### v0.8
this is an early test version

### Introduction
![Screenshot](/screenshot.png?raw=true "Optional Title")

Die Anwendung soll auf zweierlei Arten einsetzbar sein:
  - Als klassicher Password-Tresor (speicherung von Daten)
  - Als "portabler"- Pass-Generator (keinerlei Datenspeicherung)


 Als "Tresor" legt der Benutzer ein Password für eine Domain und einen dazugehörigen User-account in einer Liste an. Diese Liste lässt sich anschließend ex- und importieren. Diese Liste wird durch das Masterpassword geschützt, nach Entschlüsselung stehen alle Einträge in der Übersicht bereit.

 **HINWEIS:** Eingetragene Daten müssen in der aktuellen Version jedes mal eportiert und beim Neustart der Anwendung vom jeweiligen Speicherort importiert werden, damit sie nicht verloren gehen!


 Als "Generator" leitet die Anwendung aus der der Kombination Domain-Username-Masterpassword ein eindeutiges Password ab. Dies hat den Vorteil, dass keinerlei Benutzerdaten gespeichert werden müssen - die Anwedung dient dabei lediglich als Generator für ein möglichst sicheres (= schwer zu merkendes) Password, welches für die eindeutige Kombination masterpass-domäne-username auch immer dasselbe Ergebnis liefert. Die kleinste Abweichung dieser Inputs führ dabei zu einem völlig anderen Password als Ergebnis. Als Benutzer muss man sich somit nur ein sehr starkes Masterpassword merken und kann dann für jede Domain und einen dazugehörigen Username sein eindeutiges Passwort jedes mal neu generieren lassen. Dabei die exakte Schreibweise für Domain und username sind dabei natürlich zu beachten und man muss sich gut merken, wie man diese setets eingeben möchte, damit auch immer das richtig Password erhält . Beispiel: generiert man sich  ein Pasword für die Domain "google" und den dortigen Useraccount "müller" und schreibt aber bei nächsten Mal bei der Passwordgenerierung bspw. "google.de" und "Müller", so generiert dies ein völlig anderes password als beim Versuch zuvor.
**********

### Prerequisites
**Since we want to achieve a maximum in security  this app needs the Java Cryptography Extension [(JCE)](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html) Unlimited Strength Jurisdiction Policy Files 8 to be installed manually to unlock strong crypto-keylength.**

**Um ein Maximum an Sicherheit zu erreichen benötigt diese Anwendung die manuelle Installation der Java Cryptography Extension [(JCE)](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html) Unlimited Strength Jurisdiction Policy Files 8, um ausreiched lange Schlüssellängen zu ermöglichen.**

***********
#### Funktionsweise

Als **Passowrd-Tresor** dienen die aus der Anwendung exportierten #.db-files. Diese werden mittels eines symmetrischen Hashings passwortbasiert beim Export ver- und beim Import wieder entschlüsselt. Dies wird mittels AES mit Cypher Block Chaining erreicht. Es wird neben einem Initialisierungsvektor für jeden Export ein zufälliges "Salt" von 20 byte Länge generiert und in Verbindung mit dem Masterpassword ein für die symmetrische Verschlüsselung benötigter Schlüssel generiert. Alle datentripel domäne-username-password werden damit verschlüsselt und in einem Zielordner in einer Datei mit der endung db gespeichert. Zusätzlich werden ebenfalls das "Salt" und der Initialisierungsvektor in der Datei abgelegt (diese jedoch unverschlüsselt, da sie für die Entschlüsselung benötigt werden).

Beim Import wird mit der korrekten Eingagabe des Masterpoasswords und dem aus der Datei ausgelesenem "Salt" und InitVektors der symmetrische Schlüssel re-generiert mit welchem die Daten im letzten Schritt entschlüsselt und angezeigt werden.

Die **Password-Generatorfunktion** arbeitet auf folgende Weise:
- Aus der Verknüpfung von domain, dem Username und dem Masterpassword sowie einem "Salt" wird ein Hashwert gebildet. Dies geschieht mittels der PBKDF2+HMAC - Funktion auf Grundlage von SHA512. Für PBKDF2 wird in diesem Fall ein statisches Salt verwendet, um gleiche Ergebnisse bei entsprechenden Funktionsargumenten zu garantieren. Die Anzahl der Iterationen für die PBKDF2 funktion wurde dabei auf 100.000 festgelegt (-> ein regler in einer erweiterten Anwendungsversion ist denkbar)
- Anschließend muss aus dem 512 bit langem Hashwert ein "lesbares" Password abgeleitet werden, damit es beispielsweise für einen e-mail account verwendet werden kann: Dazu wird ein Alphabet definiert (welches hier aus 93 druckbaren ASCII Zeichen besteht), um daraus abhängig vom Hash Zeichen für Zeichen ein Password zusammenzusetzen
 (-> nicht jeder webdienst lässt beliebige Zeichen als password zu, evtl einschränkung in zukünfitger version).

```Java
public static String generatePasswordFromBytes(byte[] hashedBytes, int passwordLength) {

  BigInteger alphabetLen = BigInteger.valueOf(ALPHABET.length());
  String pw = "";
  BigInteger bigHashNumber = new BigInteger(hashedBytes);
  for (int i = 0; i < passwordLength; i++) {

    pw = pw + ALPHABET.charAt(bigHashNumber.mod(alphabetLen).intValue());
    bigHashNumber = bigHashNumber.divide(alphabetLen);
  }
  return pw;
}
```
> Der Hashwert aus masterpass-domain-username (=hashedBytes) wird zunächst als sehr sehr große Zahl aufgefasst. Diese Zahl wird nun modulo der Länge des Alphabetes gerechnet, so dass bei einer Länge des Alphabetes von 93 immer ein wert zwischen 0 und 92 als Ergebnis herauskommt. Dieser Wert dient dazu ein Zeichen aus dem Alphabet auszuwählen. Damit im Folgenden für die Auswahl eines nächsten Zeichens nicht immer dasselbe Ergebnis erscheint, wird die sehr sehr große Zahl durch die Alphabetlänge geteilt und als neuer Ausgangswert gesetzt.

*********************
