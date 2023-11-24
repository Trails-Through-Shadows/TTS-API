# Requirements form other branches

## Database

**Overview** Overall I need CRUD operations for all tables. Now depends on how I want to do with M:N tables.

- [ ] Generic CRUD function for all tables
    - Idk yet if will be generic for CRUD or just C and another for R and so on ...
- [ ] Some object mapping for

### CRUD Operace u tabulek

---

GET /tabulka | Returns all
GET /tabulka/<id>    | Returns by ID

- ?filter=<filter>   | - Filtr podle možných kriterii např.: <sloupec>='Goblin'
- ?orderBy=<sloupec> | - Seřazení podle kriterii např.: id,name
- ?max=100 | - Limit maximálního počtu entries
- ?skip=100 | - Posun entries v tabulce

POST /tabulka/<id>   | Update by ID
PUT /tabulka | Create, returns new ID
DELETE /tabulka/<id> | Delete by ID

### Schematic

---

GET /location | Returns but truncated

- ?tag="dng1"              | - Vypis podle daného tagu
- ?type="DUNGEON"          | - Vypis podle specifického typu
  GET /location/<id>         | Returns by ID full
- ?include=<includes>      | - Zapíse i dále vnořené sloupce (Pokud chceš na jeden request vypsat úplně vše)
  GET /location/<id>/enemies | Returns all enemies at specific location
- ?part=1 | - Vypis enemies pouze na partu 1

GET pro /location/<id>/enemies..obstacles..doors

## GET Output

---

```json
{
  "pagination": {
    "count": 100,
    "totalEntries": 500,
    "hasMoreItems": true,
    "skipCount": 0,
    "maxItems": 100
  },
  "entries": [
    {
      "id": ...
      "name": ...
    },
    ...
  ]
}
```

### Error Output

---

```json
{
  "error": {
    "statusCode": 400,
    "errorKey": "User Not Found"
    "value": "User with name \"Pajda\" not found!"
    "logId": 1
    // ID erroru uložený někde v error souboru
  }
}
```

### Succ Output

---

```json
{
  "success": {
    "statusCode": 200,
    "successKey": "User Added",
    "value": "User was successfully added!"
  }
}
```