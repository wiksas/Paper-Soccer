# Plan prezentacji: Paper Soccer

Czas: od 10 do 15 minut. Główną częścią jest pokaz gry na żywo. Poniższe punkty są do wyboru, można pominąć te zbędne.

## 1. Tytuł

- Paper Soccer (Piłkarzyki na kartce)
- Autorzy, przedmiot, prowadzący

## 2. Czym jest gra

- Cyfrowa wersja szkolnych piłkarzyków na kartce
- Aplikacja desktopowa w Javie, interfejs w Swing
- Tryby: 1 vs 1 na jednym komputerze oraz gra przeciwko komputerowi

## 3. Pokaz na żywo (część główna)

- Start z menu, wybór trybu
- Partia 1 vs 1: ruch, odbicie od bandy, gol
- Gra z komputerem na różnych poziomach trudności
- Pokazanie zakończenia gry przez blokadę

## 4. Zasady w skrócie

- Rysowanie toru piłki po siatce węzłów
- Wejście na zajęty węzeł daje dodatkowy ruch (odbicie)
- Wygrana przez gola lub zablokowanie przeciwnika

## 5. Jak to działa w środku

- Oddzielenie logiki gry od interfejsu
- Wszystkie reguły w jednej klasie (GameLogic), używane też przez komputer

## 6. Przeciwnik komputerowy

- Algorytm minimax, myśli kilka ruchów w przód
- Poziomy trudności to głębokość patrzenia: 2, 4 i 7 ruchów
- Zakłada najlepszą grę przeciwnika i dąży do jego bramki

## 7. Jakość i narzędzia

- 13 testów jednostkowych (JUnit)
- Maven, podział na pakiety, repozytorium Git
- Praca w zespole dwuosobowym

## 8. Podsumowanie

- Co zostało zrobione zgodnie z założeniami
- Co można rozwinąć (gra sieciowa)
