# Table of Contents <!-- omit in toc -->
- [Developers](#developers)
- [Description](#description)
- [Usage](#usage)
  - [Barrieren suchen](#barrieren-suchen)
  - [Barriere hinzufügen](#barriere-hinzufügen)

# Developers

* Johannes Gaiser
* Joel Schmid

# Description
Eine Applikation die Menschen mit (oder ohne) Gehbehinderung mitteilen kann, wo es Probleme mit Barrierefreiheit gibt und alternative Wege vorschlagen kann. 

Mit der Applikation kann anhand von Standortinformationen nach 
bereits vorhandenen Barrierefreiheitsproblemen gesucht werden. 
Wenn eine nicht barrierefreie Stelle noch nicht hinterlegt ist, kann 
diese neu hinzugefügt und mit verschiedenen alternativen Wegen 
versehen werden. 

Mush have:

- [x] Basis Anmeldefunktionalität mit Registrierung über E-Mail
- [x] Barriereprobleme festhalten mit genauer Position und Postleitzahl, optional Beschreibung, Bild und 0...n Lösungswege
- [x] Anfragefunktionalität -> Position an Server senden und als Antwort eine Liste an Barrieren zurückgeben
- [x] Suche nach Problemen / Lösungen anhand der Postleitzahl

Should have:

- [x] Suche via Karte
- [ ] Registrierung über Google-Konto
- [x] Konfigurierbarer Suchradius (10m, 25m, 50m, 100m)
- [x] Upvote / Downvote von Barrieren und Lösungen 
- [x] Löschungen beantragen über eine Peer Review (für gelöste Barrieren)

Nice to have:

- [ ] GeoFencing mit Benachrichtung auf Löschungen
- [ ] Kommentare
- [ ] Automatische Postleitzahlerkennung


# Usage

## Barrieren suchen

* App öffnen
* Login mit einem bereits vorhandenen Account oder einen Account anlegen.
  * einmal angemeldet, bleibt der Nutzer auch eingeloggt bzw. kann sich auf Wunsch wieder ausloggen.

* Barrieren können per GPS, Karte oder PLZ gesucht werden.
  * GPS liest die derzeitige Position aus und sucht in dem eingestellten Radius nach Barrieren
  * Auf der Karte kann die gewünschte Position gewählt werden. Der Radius wird auch hier berücksichtigt
  * Barrieren können per PLZ gesucht werden. Hier spielt der Radius keine Rolle.

## Barriere hinzufügen
* Eine Barriere kann erst hinzugefügt werden, sobald in diesem Bereich nach Barrieren gesucht wurde. Vielleicht ist diese Barriere bereits gemeldet. Dann verfasse doch eine Lösung.