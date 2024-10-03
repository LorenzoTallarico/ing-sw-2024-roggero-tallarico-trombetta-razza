![Codex Naturalis Image](./src/main/resources/it/polimi/ingsw/gui/img/misc/title.png?raw=true)
# Prova Finale di Ingegneria del Software 2023 / 2024
Università: Politecnico di Milano <br>
Professore: Gianpaolo Saverio Cugola <br>
Studenti: Pietro Roggero, Lorenzo Tallarico, Matteo Trombetta, Tommaso Razza <br>
# Requisiti del progetto
- Diagrammi UML di alto livello dell'applicazione, che mostrino, con tutti e soli i dettagli utili, il progetto generale dell'applicazione;
- Diagrammi UML di dettaglio che mostrino tutti gli aspetti dell'applicazione (generata dal codice);
- Documentazione del protocollo di comunicazione tra client e server;
- Documenti di peer review (uno relativo alla prima peer review e uno relativo alla seconda);
- codice sorgente dell'implementazione;
- Documentazione Javadoc dell'implementazione (generata dal codice);
- Codice sorgente dei test di unità.
# Funzionalità implementate
✅Regole semplificate  <br>
✅Regole complete  <br>
✅RMI  <br>
✅Socket  <br>
✅CLI  <br>
✅GUI  <br>
✅Chat  <br>
✅Resilienza alle disconnessioni  <br>
❌Persistenza  <br>
❌Partite multiple  <br>
# Indicazioni per l'avvio dell'applicazione
Eseguire da linea di comando prima l'applicazione del server e successivamente quelle dei client mentre si è sotto la stessa rete wifi.
```
java -jar GC06_ClientApp.jar
java -jar GC06_ServerApp.jar
```
# Guida alla documentazione e ai deliverables
Nella cartella [deliverables](deliverables/) sono presenti tutti i file richiesti per requisiti elencati nel secondo paragrafo <br>
- I vari UML sono locati nella cartella [UML Images](<./deliverables/FinalDeliverables/UML Images/>) in formato png o nella cartella [UML Drawio](<./deliverables/FinalDeliverables/UML Drawio/>)
- I Sequence Diagrams per le connessioni [Socket](./deliverables/FinalDeliverables/ConnectionSocket.png) e [RMI](./deliverables/FinalDeliverables/ConnectionRMI.png) sono presenti nella cartella [FinalDeliverables](./deliverables/FinalDeliverables/)
- Lo [screenshot del coverage](./deliverables/FinalDeliverables/Coverage.png) è presente nella cartella [FinalDeliverables](./deliverables/FinalDeliverables/). Il coverage raggiunto dai nostri test è il 100% delle classi e dei metodi che compongono il Model e il Controller. Per quanto riguarda il codice effettivo invece sono state coperte il 93% e 89% delle righe di Model e Controller, le uniche linee non coperte sono quelle che erano troppo invischiate alla funzionalità di resilienza alle connessioni.
- Le cartelle delle peer review, [1](./deliverables/peer-review-1) e [2](./deliverables/peer-review-2), si trovano in [deliverables](./deliverables) e contengono sia i file che noi abbiamo mandato per essere revisionati, sia i file che abbiamo ricevuto e la relativa revisione che abbiamo redatto.
- I file jar dell'applicazione eseguibili da linea di comando, [ClientApp](./deliverables/out/artifacts/GC06_ClientApp_jar/GC06_ClientApp.jar) e [ServerApp](./deliverables/out/artifacts/GC06_ServerApp_jar/GC06_ServerApp.jar), sono presenti nella cartella [artifacts](./deliverables/out/artifacts/).
- Nella cartella [labDeliverables](./deliverables/labDeliverables/) sono presenti alcuni file che ci sono stati richiesti durante i primissimi laboratori.
- La documentazione JavaDoc è invece totalmente contenuta nella cartella [documentation](./deliverables/documentation/) e questo è un link al suo [index](./deliverables/documentation/index.html).
# Disclaimer
Codex Naturalis è un gioco da tavolo sviluppato ed edito da Cranio Creations Srl. I contenuti grafici di questo progetto riconducibili al prodotto editoriale da tavolo sono utilizzati previa approvazione di Cranio Creations Srl a solo scopo didattico. È vietata la distribuzione, la copia o la riproduzione dei contenuti e immagini in qualsiasi forma al di fuori del progetto, così come la redistribuzione e la pubblicazione dei contenuti e immagini a fini diversi da quello sopracitato. È inoltre vietato l'utilizzo commerciale di suddetti contenuti.
