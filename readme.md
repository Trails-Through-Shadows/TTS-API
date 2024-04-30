# Welcome to Trails Through Shadows API!

This API serves as the main communication point between the database and various clients in the TTS project. It is built with Java and Spring Boot.

## Installation

- Clone the repository
- Run `mvn clean install` to build the project
- Run `mvn spring-boot:run` to start the server
- The server will be running on `localhost:8080`
- The API documentation can be found at `localhost:8080/swagger-ui.html`

## Features

- **CRUD operations** for all entities
- **Pagination, filtering, and sorting** for all entities
- **Lazy loading, caching** for all entities
- **Validation** for checking the integrity of the data, run on every put/post request
- **Session management** for tracking user sessions
- **Game logic** everything related to the game is handled by the API


[//]: # ()
[//]: # (# Old)

[//]: # ()
[//]: # (# bugs)

[//]: # ()
[//]: # (- když si getnu cachovane věci co jsou už namapovane pomoci lazy tak se vrátí zacachovaná věc místo lazy)

[//]: # ()
[//]: # (## Lazy load)

[//]: # ()
[//]: # (naopak defaultně fetchovat všechno a kdyžtak ubírat jen bez idček, když jsou id tak defaultně getovat všechno)

[//]: # ()
[//]: # (### fieldy jak se budou loadovat a co vracet)

[//]: # ()
[//]: # (**všechny listy v pagination**)

[//]: # (kde to jde lazy load)

[//]: # (když si getuju id tak defaultně namapované)

[//]: # (v listu když si getuju tak defaultně lazy)

[//]: # ()
[//]: # (# Enpoint seznam)

[//]: # ()
[//]: # (co bude mít co v sobě a co bude na co namapované)

[//]: # ()
[//]: # (- campaigns)

[//]: # (    - pagination)

[//]: # (    - lazy)

[//]: # (    - filter)

[//]: # (    - winCondition přeparsovat do jsonu)

[//]: # (- effects)

[//]: # (    - pagination)

[//]: # (- enemies)

[//]: # (    - filter)

[//]: # (- location)

[//]: # (    - lazy load)

[//]: # (    - filter)

[//]: # (- markets)

[//]: # (    - namapovat location a item bez lazy loadu)

[//]: # (    - filter)

[//]: # (    - domapovat idk actually co všechno)

[//]: # ()
[//]: # (## Fixed věci)

[//]: # ()
[//]: # (- actions/ /{id})

[//]: # (    - cajk vrací idčka)

[//]: # (    - je tam namapovaný range for some reason v summon action)

[//]: # (- achievements/ + id)

[//]: # (    - cajk)

[//]: # (    - pagination na /)

[//]: # (- background/classes + id)

[//]: # (    - lazy load)

[//]: # (- background/races + id)

[//]: # (    - lazy load)
