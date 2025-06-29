# Przewodnik implementacji frontendu

## Co musisz zaimplementować w frontendzie

### 1. **Struktura plików**
```
frontend/
├── index.html          # Strona główna
├── style.css           # Style CSS
├── script.js           # Logika JavaScript
└── assets/             # Obrazy, ikony
```

### 2. **Funkcjonalności do zaimplementowania**

#### **Panel logowania:**
- Formularz rejestracji (username, password)
- Formularz logowania (username, password)
- Walidacja danych wejściowych
- Obsługa błędów (użytkownik istnieje, błędne dane)

#### **Panel wyników:**
- Wyświetlanie statystyk (najlepszy czas, średni czas, liczba przejazdów)
- Tabela historii wyników
- Przycisk wylogowania
- Przycisk uruchomienia gry

#### **Komunikacja z API:**
- Wszystkie wywołania do `http://localhost:8080/api/`
- Obsługa sesji (cookies)
- Obsługa błędów HTTP
- Loading states

### 3. **Kluczowe elementy JavaScript**

#### **Zarządzanie stanem:**
```javascript
let currentUser = null;
let isLoggedIn = false;
```

#### **Funkcje API:**
```javascript
async function register(username, password) { /* ... */ }
async function login(username, password) { /* ... */ }
async function logout() { /* ... */ }
async function getResults() { /* ... */ }
async function saveResult(timeSeconds, lapCount) { /* ... */ }
```

#### **Zarządzanie UI:**
```javascript
function showLoginPanel() { /* ... */ }
function showResultsPanel() { /* ... */ }
function displayResults(results) { /* ... */ }
function updateStats(results) { /* ... */ }
```

### 4. **Wymagania techniczne**

#### **CORS:**
- Backend obsługuje CORS dla `*`
- Używaj `credentials: 'include'` w fetch

#### **Sesje:**
- Cookie `JSESSIONID` jest automatycznie zarządzane
- Sprawdzaj status logowania przy ładowaniu strony

#### **Responsywność:**
- Mobile-first design
- Obsługa różnych rozmiarów ekranu

### 5. **Przykładowa struktura HTML**

```html
<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Racing Game - Wyniki</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="container">
        <!-- Panel logowania -->
        <div id="loginPanel" class="panel">
            <!-- Formularze logowania i rejestracji -->
        </div>
        
        <!-- Panel wyników -->
        <div id="resultsPanel" class="panel hidden">
            <!-- Statystyki i tabela wyników -->
        </div>
        
        <!-- Wiadomości -->
        <div id="message" class="message hidden"></div>
    </div>
    
    <script src="script.js"></script>
</body>
</html>
```

### 6. **Style CSS - kluczowe elementy**

#### **Layout:**
- Flexbox/Grid dla responsywności
- Gradient backgrounds
- Card-based design

#### **Komponenty:**
- Formularze z walidacją
- Tabele wyników
- Przyciski z hover effects
- Loading spinners

#### **Responsywność:**
- Media queries dla mobile
- Touch-friendly buttons
- Readable typography

### 7. **Testowanie**

#### **Lokalne testowanie:**
1. Uruchom backend: `mvn exec:java -Dexec.mainClass="Main"`
2. Otwórz frontend w przeglądarce
3. Przetestuj rejestrację i logowanie
4. Sprawdź wyświetlanie wyników

#### **Narzędzia:**
- Developer Tools (Network tab)
- Console dla debugowania
- Postman dla testowania API

### 8. **Rozszerzenia (opcjonalne)**

#### **UX Improvements:**
- Animacje przejść
- Toast notifications
- Skeleton loading
- Dark mode

#### **Funkcjonalności:**
- Filtrowanie wyników
- Sortowanie tabeli
- Export wyników
- Graficzne statystyki

### 9. **Deployment**

#### **Lokalne:**
- Otwórz plik HTML w przeglądarce
- Lub użyj prostego serwera: `python -m http.server 3000`

#### **Produkcja:**
- Hosting statycznych plików (GitHub Pages, Netlify)
- Konfiguracja CORS na backendzie
- HTTPS dla bezpieczeństwa

## Podsumowanie

Backend jest gotowy i udostępnia pełne API. Musisz tylko zaimplementować frontend używając standardowych technologii web (HTML, CSS, JavaScript) i komunikować się z API na `http://localhost:8080/api/`.

Kluczowe punkty:
- ✅ API jest gotowe i udokumentowane
- ✅ CORS jest skonfigurowany
- ✅ Sesje działają automatycznie
- ❌ Frontend musi być zaimplementowany od zera 