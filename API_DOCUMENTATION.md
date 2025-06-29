# API Documentation - Racing Game Backend

## Base URL
```
http://localhost:8080/api
```

## Endpoints

### 1. Rejestracja użytkownika
**POST** `/register`

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User registered successfully"
}
```

**Response (409 Conflict):**
```json
{
  "success": false,
  "message": "Username already exists"
}
```

### 2. Logowanie
**POST** `/login`

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Logged in successfully"
}
```

**Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid credentials"
}
```

### 3. Pobieranie wyników użytkownika
**GET** `/results`

**Headers:** Wymagana sesja (cookie JSESSIONID)

**Response (200 OK):**
```json
[
  {
    "timeSeconds": 120.5,
    "lapCount": 1,
    "completedAt": "2024-01-15T14:30:00.000Z",
    "formattedTime": "02:00.50"
  },
  {
    "timeSeconds": 115.2,
    "lapCount": 1,
    "completedAt": "2024-01-15T14:25:00.000Z",
    "formattedTime": "01:55.20"
  }
]
```

**Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Not logged in"
}
```

### 4. Zapisywanie wyniku gry
**POST** `/save-result`

**Headers:** Wymagana sesja (cookie JSESSIONID)

**Request Body:**
```json
{
  "timeSeconds": 120.5,
  "lapCount": 1
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Result saved successfully"
}
```

**Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Not logged in"
}
```

### 5. Wylogowanie
**POST** `/logout`

**Headers:** Wymagana sesja (cookie JSESSIONID)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

## CORS Headers
Wszystkie endpointy zwracają następujące nagłówki CORS:
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, OPTIONS
Access-Control-Allow-Headers: Content-Type
```

## Sesje
- Sesje są zarządzane przez Jetty
- Cookie `JSESSIONID` jest automatycznie ustawiane po logowaniu
- Sesje wygasają po zamknięciu przeglądarki lub wylogowaniu

## Przykłady użycia w JavaScript

### Rejestracja
```javascript
const response = await fetch('http://localhost:8080/api/register', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'player1',
    password: 'password123'
  })
});

const data = await response.json();
```

### Logowanie
```javascript
const response = await fetch('http://localhost:8080/api/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    username: 'player1',
    password: 'password123'
  }),
  credentials: 'include' // Ważne dla sesji!
});

const data = await response.json();
```

### Pobieranie wyników
```javascript
const response = await fetch('http://localhost:8080/api/results', {
  method: 'GET',
  credentials: 'include' // Ważne dla sesji!
});

const results = await response.json();
```

### Zapisywanie wyniku
```javascript
const response = await fetch('http://localhost:8080/api/save-result', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  credentials: 'include',
  body: JSON.stringify({
    timeSeconds: 120.5,
    lapCount: 1
  })
});

const data = await response.json();
```

## Błędy HTTP
- **200** - Sukces
- **400** - Błędne żądanie (nieprawidłowe dane)
- **401** - Nieautoryzowany (brak logowania)
- **409** - Konflikt (użytkownik już istnieje)
- **500** - Błąd serwera

## Uwagi
1. Wszystkie hasła są hashowane za pomocą BCrypt
2. Sesje są przechowywane w pamięci serwera
3. API obsługuje CORS dla cross-origin requests
4. Wszystkie endpointy zwracają JSON 