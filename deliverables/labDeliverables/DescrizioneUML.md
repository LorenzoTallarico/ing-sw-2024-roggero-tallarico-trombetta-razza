# Descrizione UML del model
Per ogni classe sono stati scritti tutti gli attributi e tutti i metodi, ma nel caso di classi troppo lunghe, non sono elencati tutti o alcuni get e set. Naturalmente i metodi importanti non mancano.
## Game
Classe principale del model, singleton, contiene un arraylist per i giocatori, degli arraylist per le carte in mezzo al tavolo, la chat e altri attributi per capire in che fase del gioco ci troviamo.
I metodi presenti sono quelli per pescare carte dal mazzo, per creare le carte di gioco, per creare le mani dei giocatori all'inizio e per regolare i turni di gioco.
## Player
Un arraylist di carte per quelle che ha in mano, una carta per l'obiettivo segreto, vari attributi per descrivere il player (nome, colore, ...) e la propria area di gioco.
I metodi principali sono quelli per controllare se una carta è piazzabile e poi piazzarla.
## Playground
Questa è l'area di gioco, contiene una matrice di spazi 81x81 su cui potremo piazzare le carte alternando spazi dove si può piazzare una carta a spazi dove non si può (come una scacchiera), le dimensioni sono state scelte considerando tutti i possibili casi di evoluzione del gioco e il fatto che la carta iniziale venga sempre piazzata al centro.
Ci sono 4 indici che delimitano il rettangolo più piccolo dove sono state piazzate delle carte, in tal modo i metodi che operano sul playground evitano di controllare tutte le celle della matrice.
Ci sono due mappe che segnano il numero di oggetti e risorse visibili sul terrneo
Il metodo principale è quello per piazzare una carta in uno spazio, calcola anche i punti per le goldcard e aggiorna angoli di carte adiacenti e mappe.
## Space  
Lo spazio in cui si possono piazzare le carte, può essere libero, occupato o inutilizzabile.
## Card
Classe astratta, padre di tutte e 4 le varie tipologie di carta, gli attributi e i metodi sono abbastanza autoesplicativi, abbiamo un boolean che segna il lato da cui è girata la carta, una risorsa principale e due array di angoli, uno per lato.
## Corner
Un angolo di una carta, può essere libero / inutilizzabile / oggetto / risorsa, negli ultimi due casi ci affidiamo a due ulteriori attributi per identificare l'oggetto o la risorsa.
## ResourceCard
Uguale a Card, con la differenza che non è astratta e quindi ha il costruttore.
## StarterCard
Simile a ResourceCard, ha due attributi aggiunti per le due eventuali risorse principali in più.
## GoldCard
Simile a ResourceCard, ha degli attributi per identificare i requisiti del suo punteggio.
## AchievementCard
Il suo attributo principale è strategy siccome abbiamo usato uno strategy pattern per calcolare i punti dei vari obiettivi.
Il metodo getPoints fa override di quello di card perché chiama execute della strategy.
## Strategy e i vari ConcreteStrategy
Strategy è l'interfaccia con un solo metodo, execute. Le varie ConcreteStrategy lo implementano, ognuna in un modo diverso.
Esso returna un intero che sono i punti ottenuti a fine partita da un determinato achievement.
## Chat e Message
Chat, singleton, è un arraylist di message. Message contiene un testo, un autore e un orario di invio del messaggio.
Chat contiene metodi per ottenere sia l'intera chat che il singolo ultimo messaggio.
