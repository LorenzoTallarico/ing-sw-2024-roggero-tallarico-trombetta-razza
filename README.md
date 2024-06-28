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
Nella cartella [deliveries](./deliveries/) sono presenti tutti i file richiesti per requisiti elencati nel secondo paragrafo <br>
- I vari UML sono locati nella cartella [UML Images](<./deliveries/FinalDeliveries/UML Images/>) in formato png o nella cartella [UML Drawio](<./deliveries/FinalDeliveries/UML Drawio/>)
- I Sequence Diagrams per le connessioni [Socket](./deliveries/FinalDeliveries/ConnectionSocket.png) e [RMI](./deliveries/FinalDeliveries/ConnectionRMI.png) sono presenti nella cartella [FinalDeliveries](./deliveries/FinalDeliveries/)
- Lo [screenshot del coverage](./deliveries/FinalDeliveries/Coverage.png) è presente nella cartella [FinalDeliveries](./deliveries/FinalDeliveries/). Il coverage raggiunto dai nostri test è il 100% delle classi e dei metodi che compongono il Model e il Controller. Per quanto riguarda il codice effettivo invece sono state coperte il 93% e 89% delle righe di Model e Controller, le uniche linee non coperte sono quelle che erano troppo invischiate alla funzionalità di resilienza alle connessioni.
- Le cartelle delle peer review, [1](./deliveries/peer-review-1) e [2](./deliveries/peer-review-2), si trovano in [deliveries](./deliveries) e contengono sia i file che noi abbiamo mandato per essere revisionati, sia i file che abbiamo ricevuto e la relativa revisione che abbiamo redatto.
- I file jar dell'applicazione eseguibili da linea di comando, [ClientApp](./deliveries/out/artifacts/GC06_ClientApp_jar/GC06_ClientApp.jar) e [ServerApp](./deliveries/out/artifacts/GC06_ServerApp_jar/GC06_ServerApp.jar), sono presenti nella cartella [artifacts](./deliveries/out/artifacts/).
- Nella cartella [labDeliveries](./deliveries/labDeliveries/) sono presenti alcuni file che ci sono stati richiesti durante i primissimi laboratori.
- La documentazione JavaDoc è invece totalmente contenuta nella cartella [documentation](./documentation/) e questo è un link al suo [index](./documentation/index.html).
