Rubrica Telefonica v1.0
========================

DESCRIZIONE
-----------
Applicazione desktop per la gestione di una rubrica telefonica personale con sistema di
autenticazione utente e cifratura dati.

Ogni utente ha accesso esclusivo alla propria rubrica, con i dati protetti da cifratura
AES-256 basata sulla password personale.


REQUISITI DI SISTEMA
--------------------
- Java 21 o superiore installato e configurato
- Sistema operativo: Windows, macOS, Linux


PRIMO AVVIO
-----------
1. Doppio click su Rubrica.jar (oppure da terminale: java -jar Rubrica.jar)
2. Apparirà la finestra di Login
3. Per registrare un nuovo utente, inserire username e password desiderati e cliccare su "Registra nuovo utente"
Dopo la registrazione, si accederà direttamente all'applicazione senza fare il login.
4. Per accedere con un utente esistente, inserire username e password e cliccare su "Login"

UTILIZZO
--------
Dopo il login, è possibile:
- Aggiungere nuove persone con il bottone "Nuovo"
- Modificare persone esistenti selezionandole e cliccando "Modifica"
- Eliminare persone selezionandole e cliccando "Elimina"

Tutti i dati vengono salvati automaticamente e cifrati.


GESTIONE DATI
-------------
- File utenti: ./utenti.txt (contiene username e password hashate con bcrypt)
- Rubrica: ./informazioni/[username]/ (file cifrati con AES-256-GCM)
- Ogni persona è salvata in un file separato identificato da UUID
- I dati sono cifrati con chiave derivata dalla password dell'utente tramite PBKDF2


MULTI-UTENTE
------------
L'applicazione supporta più utenti sullo stesso computer:
- Ogni utente ha username e password personali
- Ogni utente vede solo la propria rubrica
- I dati di un utente non sono accessibili agli altri utenti
- I file sono cifrati e illeggibili senza la password corretta


SICUREZZA
---------
- Password protette con bcrypt (hash + salt)
- Dati rubrica cifrati con AES-256-GCM
- Chiave di cifratura derivata da password + salt random (PBKDF2)
- I file cifrati sono inaccessibili senza la password corretta

ATTENZIONE: La password non è recuperabile. In caso di smarrimento, i dati cifrati
saranno permanentemente inaccessibili.