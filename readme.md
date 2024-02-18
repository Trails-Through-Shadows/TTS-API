# bugs

- když si getnu cachovane věci co jsou už namapovane pomoci lazy tak se vrátí zacachovaná věc místo lazy

## Lazy load

naopak defaultně fetchovat všechno a kdyžtak ubírat jen bez idček, když jsou id tak defaultně getovat všechno

### fieldy jak se budou loadovat a co vracet

**všechny listy v pagination**
kde to jde lazy load
když si getuju id tak defaultně namapované
v listu když si getuju tak defaultně lazy

# Enpoint seznam

co bude mít co v sobě a co bude na co namapované

- campaigns
    - pagination
    - lazy
    - filter
    - winCondition přeparsovat do jsonu
- effects
    - pagination
- enemies
    - filter
- location
    - lazy load
    - filter
- markets
    - namapovat location a item bez lazy loadu
    - filter
    - domapovat idk actually co všechno

## Fixed věci

- actions/ /{id}
    - cajk vrací idčka
    - je tam namapovaný range for some reason v summon action
- achievements/ + id
    - cajk
    - pagination na /
- background/classes + id
    - lazy load
- background/races + id
    - lazy load
