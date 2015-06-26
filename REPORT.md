
## AIGHT - verslag

Rutger van de Lagemaat<br>
Minor Programmeren 2015a - UvA<br>
26-06-2015<br>

### Introduction
Het idee van de app is simpel: Je bent ergens, en je vindt het leuk als anderen je joinen bij een bepaalde activiteit. 
Je zet je activiteit een 'aight' genaamd, op de kaart door te beschrijven wat je gaat doen in niet meer dan 40 tekens, en stelt daarbij een duur van je aight in ergens tussen de 1 en 90 minuten.

Dit speelt in op het feit dat mensen vaak iets leuks gaan doen waar anderen bij welkom zijn, maar ze niet iedereen  willen bellen/texten of ze meedoen. Zo kent de gemiddelde science park student talloze mensen van gezicht, door ze dagelijks te zien en af en toe te spreken, maar ze hebben geen contactgegevens van elkaar. Aight is een laagdrempelige manier om een activiteit op te zetten. 

Zodra iemand in je buurt een aight heeft aangemaakt, krijg je een notificatie. Op basis van de beschrijving, de gebruikersnaam, de locatie en de duur beslis je of je erbij wilt zijn.

Aight kan op zeer uiteenlopende wijze gebruikt worden, en de keuze is daarin aan de gebruiker. Zo kan Aight gebruikt worden om mensen op de hoogte te stellen van een fenomeeen dat zich bij hun in de buurt voordoet (e.g. "Er komt flink veel rook uit dit gebouw"). Maar ook om mensen op science park uit te nodigen voor een bepaalde activiteit (e.g. "in de pauze balletje trappen, aight?").

Aight is dus echt bedoeld om mensen op korte termijn dingen samen te laten zien/doen/ervaren. Dit is echter niet het enige mogelijke gebruik dat ik voorzie. Doordat je een onbeperkt aantal aights kan maken met verschillende duur, zou je bijvoorbeeld live een speurtoch kunnen maken, die alleen maar op dat moment te volgen is. 
De volledige set aan mogelijke toepassingen is moeilijk te overzien en zal door de gebruikers moeten worden uitgediept.

### Design

Het fundament van de app zijn de zes activities en de twee classes. Daarbij is gebruik gemaakt van Parse voor alle gebruiker-gerelateerde objecten zoals "Events" en "User". Voor het renderen van de kaart en de daarbij behorende objecten is de Google Maps Android API v2 gebruikt.

#### Activities
- **CreateEventActivity.java** CreateEventActivity is een activity ofwel scherm. Hierin kan de gebruiker zijn aight omschrijven, een duur instellen en dit opsturen naar parse. De aight-knop - de naam zegt het al - waarmee de aight wordt gemaakt, brengt de gebruiker naar de MapsActivity
- **LogInActivity.java** LogInActivity is een activity waarin een bestaande gebruiker een username en password kan invullen, dit wordt naar parse gestuurd en als de tokens correct zijn wordt de MapsActivity geladen.
- **LoggedInCheck.java** LoggedInCheck wordt door de launcher gestart, checkt of een gebruiker ingelogd is en verwijst derhalve door naar WelcomeActivity (niet -) of Mapsactivity (wel ingelogd).
- **MapsActivity.jav** MapsActivity is de centrale activity van de app. Periodiek (elke 15 sec) worden de markers waarmee de aights op de kaart staan aangegeven ververst, en wordt de huidige locatie van de gebruiker geupdatet op Parse.
- **SignUpActivity.java** SignUpActivity is zeer vergelijkbaar met loginactivity, maar de gebruiker moet het wachtwoord bevestigen door het tweemaal te typen. De nieuwe gebruiker wordt opgeslagen als User-object op Parse.
- **WelcomeActivity.java** Eenvoudige activity die de gebruiker twee knoppen presenteert: log in en sign up. Wordt aangeroepen als LoggedInCheck geen actieve gebruiker vindt.

#### Classes
- **Application.java** Bij het opstarten, hervatten en in de achtergrond draaien van de app wordt de onCreate method van de Application class aangeroepen. Hierin wordt een verbinding met Parse geinitialiseerd, waarmee wordt voorkomen dat calls aan Parse in andere classes null-objecten worden.
- **MyLocation.java** De MyLocation is een class die kijkt of er een locatieprovider beschikbaar is, welke het beste is (netwerk vs gps bepaalde locatie). Wordt o.a. gebruikt om elke 15 sec de gebruikerlocatie op parse te updaten.

#### Database
In Parse sla ik een aight op als "Event" object. Hierin staat opgeslagen wanneer deze is gemaakt "createdAt", hoe lang het duurt "duration", welke user hem heeft gemaakt "username", een parsegeopoint "location" met daarin de latitude en longitude van de aight, en nog een aantal kolommen die parse automatisch aanmaakt.<br>
Users staan in een User object. Hierin staat het wachtwoord gehashed opgeslagen, de username, de laatst opgeslagen locatie van de user als parsegeopoint en nog een aantal kolommen die parse automatisch aanmaakt.

Er staat een pointer naar het Userobject van de laatst ingelogd user op een installation. De installations worden aangeroepen in cloudcode om te kijken naar welke apparaten een push-notificatie verzonden moet worden.

BIj het aanmaken van de markers wordt een parse functie aangeroepen waarmee de latitude en longitude van twee parsegeopoints wordt vergeleken en er een afstand uitkomt. De query staat afgesteld om binnen een bepaalde afstand markers te zoeken (60km), en binnen een kleinere straal notificaties te sturen (3.5km)

#### Desicions

##### UI
Ik heb de UI zo eenvoudig en intuïtief mogelijk gemaakt. Daarom heb ik de huidige Google maps geprobeerd te imiteren, maar in plaats van een knop om routes te verkrijgen zit er een knop om aights te creëeren.

##### Notifications
De notificaties zijn uiteindelijk helemaal via cloud-code geïmplementeerd. Voordeel hiervan is tegelijkertijd ook het nadeel: ongeacht of iemand de app draait, ontvangt deze een notificatie als er een aight in de buurt wordt gemaakt.



###  Challenges
Challenges is een groot onderwerp. Ik had al een half jaar niet meer naar een java bestand gekeken (ik deed app studio eind 2014) en toen was daar Android Studio. Qua functionaliteit en layout vergelijkbaar met Eclipse, maar niet meteen makkelijk om mee te werken. Dinsdag 9 juni (week 2)  gaf ik de hoop op dat mijn eerste activity vertoond ging worden, en ik bouwde de app opnieuw. Ik had nog programma geschreven dat met een externe database communiceerde, dus met Parse aan de slag gaan was een uitdaging. Gelukkig bleek dat parse hele toegankelijke functies heeft om objecten weg te schrijven en op te halen, en kon medestudent Joram mij hiermee helpen.

Het toevoegen van een externe database in een android project bleek een groot obstakel. Parse toevoegen ging voor mij vrij gemakkelijk omdat Paul daarmee op mij voorliep en de grote moeilijkheden hiervan al was tegengekomen.
Voor het eenvoudig vergelijken van 2 momenten (createdAt van een aight met huidige tijd om te kijken of deze is verlopen) wilde ik de joda-time library toevoegen. Na verschillende versies geprobeerd te hebben en verschillende build en maven errors te hebben gezien over een tijdsbestek van enkele uren heb ik het opgegeven. Het vergelijken van deze datum-objecten bleek uiteindelijk in java zelf ook wel te doen.

Notificaties implementeren had ik in eerste instantie gedaan met een locale aanvraag. Nadeel hiervan was dat een gebruiker de app aan moest hebben staan om deze te ontvangen. Toen ik ben overgeschakeld op cloud-code was een groot deel van mijn code in de MapsActivity overbodig geworden, terwijl ik hier lang aan had gezeten.
Het mooie van cloud-code is dat zodra bij parse een aight wordt weggeschreven, er vanuit parse wordt gekeken naar users in de buurt (binnen 3.5km) en deze een notificatie ontvangen.


### Reflection
De app werkt zonder bugs en heeft alle functionaliteit zoals voorgesteld in mijn MVP. Ik heb er veel aandacht aan besteed om de user interface zo eenvoudig mogelijk te houden en zo veel mogelijk processen op de achtergrond te laten plaatsvinden, waardoor het geduld van de gebruiker niet op de proef wordt gesteld. Daarnaast ben ik ook erg tevreden over de UI + layout en tot in hoeverre dit overeen komt met mijn proposal.

In korte tijd ben ik gegaan van iemand die zich nog wel vaag herinnerd hoe java in elkaar zat, tot een app-ontwikkelaar die parse objecten kan opslaan en queryen, users kan toevoegen aan een app, ik heb een basis van javascript geleerd om cloud-code in te schrijven om push notificaties te implementeren en ik ben zeer tevreden over wat ik in deze korte maar intensieve periode heb geleerd en gemaakt.

De app zou nog verbeterd kunnen worden door bijvoorbeeld een vriendensysteem te implementeren, waarbij de keuze kan worden toegevoegd om danwel publieke danwel friends-only aights te maken. IK zou instellingen kunnen toevoegen waarmee gebruikers zelf de afstand kunnen instellen waarbinnen ze een notificatie willen ontvangen (en óf ze een notificatie willen ontvangen). Ook zou de functie kunnen worden toegevoegd om een toekomstige aight te maken.
Sommige van deze functies wil ik misschien nog toevoegen, maar binnen dit tijdsbestek was dat simpelweg niet haalbaar.
Ik ga de APK in ieder geval aan mijn vrienden geven zodat we aight kunnen gebruiken.

### Sources
 - [https://parse.com/tutorials/anywall-android](AnyWall Parse tutorial)
 - [https://developers.google.com/maps/documentation/android/start](Google Maps Android API v2)
 - [https://github.com/JrSchild/](Joram's brein)
 - [http://stackoverflow.com/questions/3145089/what-is-the-simplest-and-most-robust-way-to-get-the-users-current-location-in-a/3145655#3145655](Stackoverflow et al)
