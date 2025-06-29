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

#### **Panel wyników:**
- Wyświetlanie statystyk (najlepszy czas, średni czas, liczba przejazdów)
- Tabela historii wyników
- Przycisk wylogowania
- Przycisk uruchomienia gry

#### **Komunikacja z API:**
- Wszystkie wywołania do `http://localhost:8080/api/`

#### **Funkcje API:**
```javascript
async function register(username, password) { /* ... */ }
async function login(username, password) { /* ... */ }
async function logout() { /* ... */ }
async function getResults() { /* ... */ }
```

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
