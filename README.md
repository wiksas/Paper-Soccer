# Paper Soccer ⚽ Piłkarzyki na Kartce

Cyfrowa, desktopowa adaptacja klasycznej gry szkolnej **"Piłkarzyki na kartce"** zrealizowana w języku Java. Projekt posiada nowoczesny interfejs graficzny oraz zaawansowany moduł Sztucznej Inteligencji (AI).

---

## 🌟 Funkcje projektu

- **Dwa tryby rozgrywki:**
  - **Lokalny 1 vs 1 (Hot-Seat):** Rywalizacja dwóch graczy na jednym urządzeniu, poprawna obsługa tur i przeliczanie ścieżek.
  - **Gra z Komputerem (Sztuczna Inteligencja):** Zaawansowany algorytm AI symulujący ruchy, szukający punktowych odbić i unikający ślepych zaułków.
- **Płynna animacja AI:** Ruchy komputera posiadają wbudowane opóźnienie (*cooldown*), dzięki czemu można zaobserwować pełną sekwencję kroków przeciwnika.
- **Dopracowany interfejs graficzny (UI/UX):**
  - Ekologiczna alternatywa dla papieru, angażująca wyobraźnię geometryczną.
  - Estetyczna, zielona murawa boiska i minimalistyczna siatka węzłów.
  - Podświetlenie ostatniego wykonanego ruchu kolorem przypisanym do gracza.
  - Generowanie boiska i poprawna identyfikacja wizualna bramek i tur graczy.
- **Swoboda taktyczna:** Zaimplementowano reguły umożliwiające krzyżowanie linii na ukos.
- **Wykrywanie końca gry:** System automatycznie ogłasza zwycięzcę po strzeleniu gola lub zablokowaniu przeciwnika w ślepym zaułku bez dozwolonych ruchów.

---

## 🛠️ Technologie i narzędzia

- **Język programowania:** Java (17+)
- **Biblioteka GUI:** Java Swing & AWT (`Graphics2D` z antyaliasingiem)
- **Zarządzanie projektem:** Apache Maven
- **Testy automatyczne:** JUnit 5 (Jupiter)

---

## 🚀 Jak uruchomić?

**Opcja 1: Przez Mavena (Zalecane)**
```bash
mvn clean compile exec:java -Dexec.mainClass="PaperSoccerApp"
```

**Opcja 2: Bezpośrednio z terminala**
```bash
javac *.java && java PaperSoccerApp
```

---

## 🧪 Testy

Aby uruchomić testy jednostkowe weryfikujące mechanikę gry, granice boiska oraz walidację ruchów, użyj komendy:
```bash
mvn test
```

---

## 👥 Autorzy (Skład zespołu projektowego)

Zgodnie z Kartą Projektu, role zostały podzielone następująco:

* **Szymon Rafałowski** (CEO, Nr albumu: 155218) – Zarządzanie architekturą projektu, implementacja głównej logiki gry (mechanika tur, detekcja końca gry, moduł AI).
* **Wiktor Sasnal** (COO, Nr albumu: 155307) – Implementacja interfejsu graficznego (UI), podpięcie logiki pod widok, płynne animacje, zarządzanie scenami oraz tworzenie testów jednostkowych.

---
*Projekt akademicki zrealizowany na Wydziale Informatyki i Telekomunikacji Politechniki Krakowskiej.*