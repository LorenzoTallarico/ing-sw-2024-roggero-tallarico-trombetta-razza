# Peer-Review 1: UML
Pietro Roggero, Lorenzo Tallarico, Matteo Trombetta, Tommaso Razza | Gruppo GC06  
Valutazione del diagramma UML delle classi del gruppo GC53.

## 1 Lati positivi
Sebbene l'UML sia poco ordinato traspare l'intento generale e il previsto funzionamento del modello.
La strada implementativa è ragionevolmente intuitiva e abbastanza chiara.  
Abbiamo individuato due aspetti positivi in particolare che meritano attenzione.
### 1.1 Update risorse e oggetti in Player Area
Ottima scelta quella di aggiornare delle variabili di conteggio risorse e oggetti per semplificare il controllo dei requisiti di carte oro e/o carte obiettivo, evitando anche possibili calcoli ridondanti. Sul tipo dato utilizzato abbiamo però lasciato una considerazione sotto _(2.1)_.
### 1.2 Parametri adeguati per metodi di pesca delle carte
In Table e GameController i metodi per scegliere le carte dalla mano o dal tavolo utilizzano delle enum come parametri, ciò riduce i rischi di errore dati dall'utilizzo di semplici int e quindi evita problemi di gestione di indici.

## 2 Lati negativi
Leggendo il diagramma e successivamente provando a simulare una partita abbiamo individuato i seguenti punti critici che potrebbero essere migliorati.
### 2.1 Troppe variabili diverse per i contatori in Player Area
Invece di utilizzare sette int diversi per aggiornare ogni volta la disponibilità di risorse e oggetti presenti sul campo di gioco di un player, si potrebbe usare una mappa che utilizza come chiavi la enum _Content_. Ciò renderebbe più semplice attuare modifiche al gioco senza dover riscrivere codice e generalizzare i metodi.
### 2.2 AdjacentPoints array di stringhe
Sosteniamo che l'utilizzo di un array di stringhe non sia la soluzione più adeguata. Innanzitutto l'array ha dimensione fissa, servirebbe una struttura simile come un ArrayList. In secondo luogo una string non è il tipo dato adatto per salvare una posizione, servirebbe creare una classe apposita con all'interno due attributi, un intero per l'ascissa e uno per l'ordinata.
### 2.3 GoalCard presenta ambiguità
Non si capisce come vengono gestite le casistiche delle carte obiettivo. In _GoalObj&Res_ manca la possibilità di identificare l'obiettivo che richiede un set di 3 oggetti diversi. In GoalTopologic non si capisce come vengono identificati gli obiettivi che richiedono pattern diversi sul campo di gioco e come mai viene utilizzata una stringa invece di una enum.
### 2.4 Troppe classi enumerativi superflue o ridondanti
Pensiamo che siano presenti troppe enum, ciò comporta inutili complicazioni e difficoltà di lettura e modificabilità. Rendono l'implementazione poco scalabile.  
Alcuni esempi sono _Placement_ che presenta solo front e back, può benissimo essere rimpiazzata da un semplice boolean, _Numbers_ che può essere sostituita da un semplice int e infine _PlayerTurn_ che può essere rimpiazzata da un semplice indice o da un riferimento al player che sta giocando.

## 3 Confronto tra le architetture
L'idea di base non è troppo differente dalla nostra, ma si nota subito come il loro sistema sia pieno di classi ad hoc, mentre noi abbiamo preso una strada implementativa in cui prevale una generalizzazione di classi e metodi.  
Il loro modello denota un'ereditarietà molto più articolata dalla nostra, ma utilizza un calcolo degli obiettivi più rudimentale senza l'aiuto di alcun pattern.  
In generale le differenze non sono troppe ed entrambe le soluzioni consentono di svolgere tutte le operazioni per il corretto flusso di gioco.
