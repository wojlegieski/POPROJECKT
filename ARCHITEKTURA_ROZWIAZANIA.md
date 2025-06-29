# ARCHITEKTURA ROZWIĄZANIA

## 1. BACKEND (JAVA)
- BAZA DANYCH: SQLITE (LOKALNA, PROSTA W UŻYCIU)
- WEB SERVER: EMBEDDED JETTY LUB SPRING BOOT
- API: REST ENDPOINTS DO LOGOWANIA I POBIERANIA WYNIKÓW

## 2. FRONTEND (HTML/JAVASCRIPT)
- PROSTA STRONA WEB DO LOGOWANIA I WYŚWIETLANIA WYNIKÓW
- MOŻLIWOŚĆ URUCHOMIENIA LOKALNIE W PRZEGLĄDARCE

---

# ARCHITEKTURA SYSTEMU LOGOWANIA I WYNIKÓW - OPIS KOMPONENTÓW

## BAZA DANYCH (`src/database/`)
- **DatabaseManager.java** – Główna klasa zarządzająca bazą danych SQLite
  - Inicjalizacja tabel (`users`, `results`)
  - Rejestracja i uwierzytelnianie użytkowników
  - Zapisywanie i pobieranie wyników gry
  - Operacje CRUD na bazie danych
- **GameResult.java** – Model danych reprezentujący wynik gry
  - Czas przejazdu, liczba okrążeń, data ukończenia
  - Formatowanie czasu do wyświetlania

## SERWER WEB (`src/web/`)
- **WebServer.java** – Serwer HTTP z Jetty
  - Konfiguracja serwera na porcie 8080
  - Rejestracja servletów API
  - Obsługa plików statycznych (HTML, CSS, JS)
  - Zarządzanie sesjami użytkowników

---

# PRZEPŁYW DANYCH
- **Rejestracja** → script.js → WebServer → DatabaseManager → SQLite
- **Logowanie** → script.js → WebServer → DatabaseManager → Sesja
- **Gra** → Main.java → DatabaseManager → SQLite
- **Wyniki** → WebServer → DatabaseManager → script.js → HTML

# KLUCZOWE INTERFEJSY
- **API REST** – Komunikacja między frontend a backend
- **DatabaseManager** – Abstrakcja dostępu do bazy danych
- **WebServer** – Punkt wejścia dla żądań HTTP
- **Main.java** – Integracja gry z systemem wyników 