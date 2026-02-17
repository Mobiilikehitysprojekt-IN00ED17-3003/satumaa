# ✨ Satumaa – Missä teknologia kohtaa tarinat

**Satumaa** on moderni, **MVVM-arkkitehtuurilla** ja **Kotlinilla** toteutettu mobiilisovellus, joka on rakennettu **Jetpack Composen** deklaratiiviseen maailmaan. Se on suunniteltu modulaariseksi elämysalustaksi, joka tässä versiossa yhdistää generatiivisen tekoälyn, reaaliaikaisen paikkatietoviestinnän ja lisätyn todellisuuden elementit saumattomaksi kokonaisuudeksi.

---

### 🚀 Tekninen ydin ja arkkitehtuuri
Sovelluksen selkärankana toimii kattava **Firebase-ekosysteemi**, joka toimii turvallisena siltana laitteen ja pilvipalveluiden välillä. Modernia ja kevennettyä Clean Architecture -mallia noudattaen ratkaisu koostuu seuraavista osa-alueista:

* **Cloud Functions & Gemini AI:** Sovelluslogiikka on eriytetty pilvifunktioihin, jotka toimivat turvallisena yhdyskäytävänä generatiiviselle tekoälylle, piilottaen API-avaimet ja monimutkaisen logiikan päätelaitteelta.
* **Firestore & Autentikointi:** Reaaliaikainen NoSQL-tietokanta ja Google-kirjautuminen takaavat, että käyttäjän luomat sadut ja kirjeet pysyvät tallessa ja synkronoituna laitteiden välillä.
* **App Check -tietoturva:** Rajapinnat on suojattu Play Integrity -tarkistuksilla, varmistaen että vain aito sovellus voi kommunikoida backendin kanssa.
* **Offline First -periaate:** Paikallinen **Room-tietokanta** toimii sovelluksen ensisijaisena tietolähteenä, taaten nopean käyttökokemuksen myös ilman verkkoyhteyttä.
* **Hilt & WorkManager:** Modernit Android-kirjastot huolehtivat koodin modulaarisuudesta ja taustatehtävien luotettavasta suorituksesta.

---

### 📖 Satujen uusi sukupolvi
Satumaa antaa vallan lukijalle. **Gemini AI** -integraation avulla käyttäjät voivat generoida täysin uniikkeja, personoituja satuja pelkkien avainsanojen perusteella. Tekoäly on valjastettu toimimaan turvallisena virtuaalisena satukirjailijana, joka luo **kokijan nimeen** ja valittuihin teemoihin pohjautuvia tarinoita hetkessä.

---

### 🌟 Visio alustasta: Joustava pohja uusille ideoille
Satumaa on rakennettu skaalautuvaksi ekosysteemiksi. Nyt toteutettu Joulupukki-moduuli on **ensimmäinen esimerkki (Proof of Concept)** siitä, miten sovellus voi laajentua perinteisen lukemisen ulkopuolelle. Tekninen runko mahdollistaa monipuolisen jatkokehityksen:

* **Vaihtuvat teemamaailmat:** Alusta tukee uusien sisältökokonaisuuksien lisäämistä – oli kyseessä sitten pääsiäisseikkailu tai avaruusmatka, uusi sisältö voidaan tuoda sovellukseen joustavasti.
* **Pelillisyys ja oppiminen:** Sovelluksen rakenne mahdollistaa erilaisten vuorovaikutteisten elementtien, kuten tehtävien tai minipelien, integroimisen osaksi tarinankerrontaa.
* **Kehittyvä arkkitehtuuri:** "Nykyinen selkeään vastuunjakoon perustuva arkkitehtuuri varmistaa koodin ylläpidettävyyden, luoden perustan modulaariselle rakenteelle. Sovellus on suunniteltu siten, että sisällön kasvaessa voidaan siirtyä kerrosarkkitehtuurista (Layer-based) kohti ominaisuuspohjaista monimoduuliarkkitehtuuria (Feature-based Multi-module). Tässä visiossa yhteinen logiikka ja data eriytetään jaettuihin Core-ytimiin, joiden päälle uudet teemat ja ominaisuudet rakentuvat itsenäisinä palasina. Tämä rakenne estää koodin monistumisen ja pitää kehityksen ketteränä myös satojen uusien ominaisuuksien mittakaavassa.

---

### 🎅 Toteutettu esimerkki: Yhteys Joulupukkiin
Ensimmäisessä toteutetussa teemassa halusimme esitellä teknistä osaamistamme yhdistämällä useita vaativia mobiiliteknologioita:

* **Reaaliaikainen seuranta:** Kirjeen matka Korvatunturille visualisoidaan **OSMdroid-kartalla** hyödyntäen GPS-paikannusta.
* **Lisätty todellisuus (AR):** Vastauskirjeen etsintä hyödyntää puhelimen kiihtyvyysantureita, suuntavektoreita ja **CameraX-rajapintaa**.
* **Älykkäät notifikaatiot:** Sovelluksen sisäiset tarkkailijat reagoivat saapuneisiin vastauksiin ja ilmoittavat niistä käyttäjälle juuri oikealla hetkellä.

---

### 📊 Analyyttinen ote luovuuteen
Satumaa ei ainoastaan luo tarinoita, vaan se myös analysoi niitä. Sisäänrakennettu **laskentayksikkö (StatsMathEngine)** seuraa sanavaraston kehitystä lineaarisen regression avulla ja pisteyttää tarinoiden jännitystason uniikilla seikkailuindeksillä, tarjoten visualisoitua tietoa harrastuksen edistymisestä.

**Satumaa on enemmän kuin sovellus – se on silta perinteisen tarinankerronnan ja tulevaisuuden teknologian välillä.**

## 📋 Sisällysluettelo

1.  [Projektin perustiedot](#-1-projektin-perustiedot)
2.  [Pakolliset palautukset / Dokumentit](#-2-pakolliset-palautukset--documents)
3.  [Scrum & GitHub Projects](#-3-scrum--github-projects--näyttö)
4.  [Projekti-ominaisuudet](#%EF%B8%8F-4-projekti-ominaisuudet)
    * [4.1 HTTP / API -toteutus (Cloud Functions)](#41-http--api--toteutus-firebase-cloud-functions)
    * [4.2 Paikallinen tallennus (Room)](#42-paikallinen-tallennus-room-database)
    * [4.3 Offline First](#43-offline-välimuistikäytös-offline-first)
    * [4.4 Autentikointi & Tietoturva](#44-autentikointi-firebase-google-sign-in-ja-tietoturva-app-check)
    * [4.5 Notifikaatiot](#45-notifikaatiot-paikalliset-ilmoitukset)
    * [4.6 Kartta ja paikkatieto](#46-kartta-ja-paikkatieto-openstreetmap--sijaintipalvelut)
    * [4.7 & 4.8 Sensorit, Kamera ja AR](#47--48-sensorit-kamera-ja-ar-kokemus)
    * [4.9 Taustatyöt (WorkManager)](#49-taustatyö-ja-elinkaarikestävyys-workmanager)
    * [4.10 Generatiivinen tekoäly (Gemini AI)](#410-vapaa-vaativa-ominaisuus-generatiivinen-tekoäly-gemini-ai)
5.  [UI/UX & Navigaatio](#-5-projekti--uiux--navigaatio)
6.  [Arkkitehtuuri & Koodin laatu](#-6-projekti--arkkitehtuuri--koodin-laatu)
    * [6.1 Kerrosjako (Pragmatic Clean Arch)](#61-selkeä-kerrosjako-mvvm--clean-architecture)
    * [6.2 Datavirta (UDF)](#62-state-hallinta-ja-datavirta-unidirectional-data-flow)
    * [6.3 Rakenne ja Kaaviot](#63-luettava-koodi-ja-rakenne)
7.  [Advanced Mobile: Secure AI](#-7-advanced-mobile--osuus-secure-ai-with-firebase-cloud-functions)
8.  [Bonukset ja Viimeistely](#-8-bonukset-ja-viimeistely)


<img width="405" height="899" alt="Näyttökuva 2026-02-10 182314" src="https://github.com/user-attachments/assets/3d1f2b17-9783-4cca-b210-2059690708f0" /> <img width="405" height="899" alt="Näyttökuva 2026-02-10 214315" src="https://github.com/user-attachments/assets/451c3c07-a900-4d6b-8204-2cb30771833c" />

___
📖 1. Projektin perustiedot
-
| Kenttä | Selitys |
|-----------|--------|
| Kurssi / Toteutus | Mobiilikehitysprojekti IN00ED17-3003  |
| Tiimin nimi | Ryhmä 18 |
| Jäsenet (nimet) | Joonas Väyrynen, Antero Muunoja, Miro Hovi |
| Teknologia (Android Compose / React Native / muu)  | Android Jetpack Compose |
| Sovelluksen nimi | Satumaa |
| GitHub-repo (julkinen URL)  | https://github.com/Mobiilikehitysprojekt-IN00ED17-3003/satumaa  |
| GitHub Projects (URL)  | https://github.com/orgs/Mobiilikehitysprojekt-IN00ED17-3003/projects/1  |
| Lyhyt kuvaus ideasta (2–4 riviä)  | Luo monipuolisia ja uniikkeja satuja tekoälyn generoimana avainsanojen avulla. Suosikkisadut voi tallentaa omaan kirjastoon myöhempiä lukuhetkiä varten. Käyttäjä voi myös kirjoittaa ja lähettää kirjeen Joulupukille. Kirjeen matkaa voi seurata reaaliajassa OpenStreetMap-kartalla. Kun käyttäjä saa ilmoituksen vastauksesta, vastauskirjeen avaaminen vaatii pienen tehtävän ratkaisemista tai vastauskirjeen etsimistä ympäristöstä puhelimen kameran avulla. |

___
📝 2. Pakolliset palautukset / Documents
-
Tästä osiosta löytyvät linkit projektin virallisiin dokumentteihin ja esityksiin:  
2.1 UI-suunnitelma: [Linkki dokumenttiin]  
2.2 Työajanseuranta: [Linkki seurantaan]  
2.3 Pisteytyslomake: [Linkki lomakkeeseen]  
2.4 Loppuesittelyvideo: [Linkki videoon]  
2.5 Advanced Mobile -video: [Linkki videoon]  
2.6 Scrum-muistio: [Linkki muistioon]

___
🎯 3. Scrum & GitHub Projects -näyttö
-
Projektinhallinta on toteutettu GitHub Projects -työkalulla: https://github.com/orgs/Mobiilikehitysprojekt-IN00ED17-3003/projects/1

3.1 Sprintit nimettynä: Projektissa on käytetty selkeitä sprinttejä.
<img width="1602" height="852" alt="Näyttökuva 2026-02-10 180654 (2)" src="https://github.com/user-attachments/assets/89265889-78a6-4df2-a31a-626947a31a03" />


3.2 Tehtävät pilkottu: Backlog on jaettu pieniin, hallittaviin tehtäviin.
<img width="1602" height="852" alt="Näyttökuva 2026-02-10 180654 backlog" src="https://github.com/user-attachments/assets/98ed56ea-2523-487e-a916-03d9832d9a5b" />


3.3 Tehtävissä assigneet: Jokaisella tehtävällä on nimetty vastuuhenkilö.
<img width="1602" height="852" alt="Näyttökuva 2026-02-10 180654 assignees" src="https://github.com/user-attachments/assets/3ac99391-9204-46b9-9879-16a67ebf1dd0" />


3.4 Kanban elää: GitHub Projects Kanban-taulua on päivitetty reaaliajassa kehityksen aikana.
<img width="1433" height="1060" alt="Näyttökuva 2026-02-11 163246" src="https://github.com/user-attachments/assets/c4e52d60-79ba-4ef9-a784-ebaa3bc62462" />


3.5 Sprint Planning: Suunnittelupalaverit on pidetty ennen jokaista Review-vaihetta.  
Löytyy palaverimuistiosta.

3.6 Scrum Review -osallistumiset: Ryhmä on osallistunut säännöllisiin katselmointiin.  
Löytyy palaverimuistiosta.
___
⚙️ 4. Projekti-ominaisuudet
-
### 4.1 HTTP / API -toteutus (Firebase Cloud Functions)
---
Sovellus ei käytä perinteistä REST-rajapintaa, vaan **Firebase Callable Cloud Functions** -mekanismia (`HttpsCallable`). Tämä on HTTP-pyyntö, joka on kiedottu Firebasen turvalliseen SDK:hon.

**Toteutuksen yksityiskohdat:**
* **API-kutsu:** Android lähettää parametrit (lapsen nimi, teema, tyyli) JSON-muodossa Cloud Functionille (`generateStory`).
* **Haku/Suodatus:** Käyttäjän syötteet toimivat hakuparametreina tekoälylle.
* **Detalji:** Palvelin palauttaa generoidun sadun (otsikko, sisältö) JSON-objektina.
* **Lataus ja Virhetilat:**
    * Repository tarkistaa verkkoyhteyden ennen kutsua.
    * ViewModel asettaa tilaksi `Loading` kutsun ajaksi (UI näyttää spinnerin).
    * Mahdolliset virheet (verkko, API-virhe, timeout) pyydystetään `Result`-kääreeseen ja näytetään käyttäjälle `Error`-tilana.

**Kooditodisteet:**

* **HTTP-kutsu ja JSON-parsinta:**
    * Tiedosto: `data/remote/functions/StoryFunctionsSource.kt`
    * Funktio: `generateStory`
    * Kuvaus: Rakentaa HashMapin, kutsuu `.call(data)` ja odottaa vastausta (`.await()`). Parsii vastauksen `Map<*, *>` -> `Story` -olioksi.
    * Rivit StoryFunctionsSource.kt: rivit n. 25–60 (generateStory-funktio)
    * <img width="746" height="142" alt="image" src="https://github.com/user-attachments/assets/74b13cd8-069f-4e9c-8359-974e8fd8f2b9" />


* **Lataus- ja virhetilojen hallinta (State Management):**
    * Tiedosto: `ui/viewmodel/story/StoryViewModel.kt`
    * Funktio: `generateStory`
    * Kuvaus: Asettaa `_uiState.value = Loading`. Käsittelee vastauksen: `onSuccess` -> `Success(story)` tai `onFailure` -> `Error(message)`.
    * Rivit StoryViewModel.kt: rivit n. 40–60 (tilanhallinta)

* **Verkkoyhteyden tarkistus:**
    * Tiedosto: `data/repository/StoryRepositoryImpl.kt`
    * Funktio: `generateStoryPreview`
    * Kuvaus: Estää turhan HTTP-kutsun, jos `!isOnline()`.

### 4.2 Paikallinen tallennus (Room Database)
Sovellus käyttää **Room**-kirjastoa datan pysyvään tallennukseen laitteelle. Tämä mahdollistaa satujen selaamisen ja tallentamisen strukturoidussa SQL-tietokannassa. Arkkitehtuuri noudattaa **Repository-mallia**, jossa tietokanta on eriytetty sovelluksen käyttöliittymästä.

**Toteutuksen yksityiskohdat:**
* **DAO (Data Access Object):** Määrittelee SQL-kyselyt (`SELECT`, `INSERT`) ja palauttaa datan reaktiivisena `Flow`-virtana.
* **Repository-integraatio:** Repository injektoi DAO:n ja kutsuu sen metodeja, piilottaen tietokantatoteutuksen ViewModelilta.
* **Data Mapping:** Repository muuntaa tietokannan `StoryEntity`-oliot sovelluksen sisäisiksi `Story`-domain-olioiksi (`toDomainModel`), jolloin tietokantariippuvuus ei vuoda UI-tasolle.

**Kooditodisteet:**

* **Tietokannan konfiguraatio (Database):**
    * **Tiedosto:** `data/local/SatumaaDatabase.kt`
    * **Luokka:** `SatumaaDatabase`
    * **Kuvaus:** Määrittelee `@Database`-annotaatiolla sovelluksen SQL-tietokannan, siihen kuuluvat taulut (`StoryEntity`, `LetterEntity`, `LetterLocalStateEntity`) sekä versionumeron. Toimii keskitettynä pisteenä DAO-rajapinnoille.
    * **Rivit:** 22-30

* **Tietokantakyselyt ja reaktiivisuus (DAO):**
    * **Tiedosto:** `data/local/dao/StoryDao.kt`
    * **Funktio:** `getAllStories`, `insertStory`
    * **Kuvaus:** Määrittelee `@Query`-annotaatiolla SQL-lauseet ja palauttaa `Flow<List<StoryEntity>>`, jolloin UI päivittyy automaattisesti kannan muuttuessa.
    * **Rivit:** 29-30
    * <img width="746" height="57" alt="image" src="https://github.com/user-attachments/assets/d4abeb76-cdc6-4608-8909-5e73a7fad22d" />

* **Repositoryn integraatio ja datamuunnos:**
    * **Tiedosto:** `data/repository/StoryRepositoryImpl.kt`
    * **Funktio:** `getStories`
    * **Kuvaus:** Hakee datan DAO:lta ja muuntaa (map) sen: `storyDao.getAllStories().map { it.toDomainModel() }`.
    * **Rivit:** 44-48 (`getStories`-toteutus)
    * <img width="641" height="140" alt="image" src="https://github.com/user-attachments/assets/506fc3aa-af62-47e3-a7d8-d3e73c018364" />

* **Tallennusoperaatio:**
    * **Tiedosto:** `data/repository/StoryRepositoryImpl.kt`
    * **Funktio:** `saveStory`
    * **Kuvaus:** Tallentaa uuden sadun kantaan kutsumalla `storyDao.insertStory`.
    * **Rivit:** 96-108 (`saveStory`-toteutus)
    * <img width="718" height="55" alt="image" src="https://github.com/user-attachments/assets/e4d33aa0-0910-43cc-bed2-c65d4b5988c6" />


### 4.3 Offline-/välimuistikäytös (Offline First)
---
Sovellus on rakennettu **Offline First** -periaatteella. Tämä tarkoittaa, että käyttöliittymän "Single Source of Truth" on aina paikallinen Room-tietokanta, mikä takaa nopean toiminnan ja offline-selailun.

**Toteutuksen yksityiskohdat:**
* **Välitön data:** Kun sovellus avataan, sisältö ladataan heti laitteen muistista (`Flow`-virta).
* **Tallennuslogiikka (Cloud & Local Sync):**
    * **Sadut (Cloud-First):** Kun käyttäjä tallentaa sadun, se lähetetään ensin Firestore-pilvitietokantaan. Onnistuneen tallennuksen jälkeen satu tallennetaan samalla ID:llä paikalliseen Room-kantaan. Tämä varmistaa, että offline-versio on identtinen pilviversion kanssa.
    * **Kirjeet (Automaattinen):** Kirjeet tallentuvat automaattisesti paikalliseen välimuistiin heti generoinnin onnistuttua. Käyttäjän ei tarvitse erikseen tallentaa niitä, mikä estää datan katoamisen verkkokatkoksen tai sovelluksen sulkemisen sattuessa.
* **Verkkotilan tarkistus:** Raskaat operaatiot (generointi/pilvitallennus) tarkistavat verkkoyhteyden (`isOnline()`) ennen suoritusta.

**Kooditodisteet:**

* **Offline-haku (Single Source of Truth):**
    * **Tiedosto:** `data/repository/StoryRepositoryImpl.kt`
    * **Funktio:** `getStories`
    * **Kuvaus:** Hakee datan vain lokaalista kannasta, ei koskaan suoraan verkosta UI-kerroksessa.
    * **Rivit:** 44–48
    * <img width="674" height="135" alt="image" src="https://github.com/user-attachments/assets/b8d50bb1-72b2-4ea1-a6fa-9ce074e7cbc5" />


* **Satujen tallennus (Cloud -> Local):**
    * **Tiedosto:** `data/repository/StoryRepositoryImpl.kt`
    * **Funktio:** `saveStory`
    * **Kuvaus:** Tallentaa ensin pilveen ja käyttää palautettua ID:tä paikalliseen tallennukseen.
    * **Rivit:** 94–108
    * <img width="781" height="346" alt="image" src="https://github.com/user-attachments/assets/d124b1d5-248c-457c-842c-7fb89f3aa158" />


* **Kirjeiden automaattitallennus (Send & Save):**
    * **Tiedosto:** `data/repository/LetterRepositoryImpl.kt`
    * **Funktio:** `sendLetter`
    * **Kuvaus:** Kun kirje on lähetetty pilveen (`functionsSource.sendLetter`), se tallennetaan välittömästi myös paikalliseen Room-tietokantaan.
    * **Rivit:** n. 115-140 (sendLetter-toteutus)
    * <img width="702" height="57" alt="image" src="https://github.com/user-attachments/assets/bd77bc47-9a98-4595-82b2-88c72bb52bc2" />


### 4.4 Autentikointi (Firebase Google Sign-In) ja tietoturva (App Check)
---
Sovellus käyttää **Firebase Authentication** -palvelua ja **Firebase App Check** -suojausta varmistaakseen käyttäjien tunnistamisen ja sovelluksen eheyden. Menetelmänä on **Google Sign-In**, joka tarjoaa turvallisen tavan kirjautua sisään.

**Toteutuksen yksityiskohdat:**
* **Google OAuth:** Sovellus pyytää käyttäjältä ID-tokenin Googlen kirjautumispalvelusta ja välittää sen Firebaselle validointia varten.
* **Firebase App Check:** Sovelluksessa on käytössä App Check (Play Integrity), joka varmistaa, että backend-pyynnöt tulevat vain aidosta ja muokkaamattomasta sovellusversiosta. Tämä estää API-rajapintojen väärinkäytön sovelluksen ulkopuolelta.
* **Käyttäjäkohtainen näkymä:** Jokaisella käyttäjällä on oma `UID` (Unique ID), jonka perusteella sovellus suodattaa vain kyseisen käyttäjän omat tiedot Firestoresta.
* **Auto-login:** `AuthViewModel` tarkistaa sovelluksen käynnistyessä, onko käyttäjä jo kirjautunut (`currentUser != null`), jolloin istunto jatkuu saumattomasti.

**Kooditodisteet:**

* **Kirjautumislogiikan tekninen toteutus:**
    * **Tiedosto:** `data/repository/AuthRepositoryImpl.kt`
    * **Funktio:** `signInWithGoogle`
    * **Kuvaus:** Muuntaa ID-tokenin Firebase-credentialiksi ja suorittaa varsinaisen kirjautumisen asynkronisesti.
    * **Rivit:** 24–44
    * <img width="988" height="479" alt="image" src="https://github.com/user-attachments/assets/536d7526-6b75-4450-97cc-a518a07d8139" />

* **Google-kirjautumisen käsittely:**
    * **Tiedosto:** `ui/screens/auth/LoginScreen.kt`
    * **Kuvaus:** Alustaa `GoogleSignInClient`-asiakkaan ja käsittelee kirjautumisen tuloksen `ActivityResultLauncherilla`.
    * **Rivit:** 65-101
    
* **Kirjautumislogiikka ja tokenin välitys:**
    * **Tiedosto:** `viewmodel/auth/AuthViewModel.kt`
    * **Funktio:** `signInWithGoogle`
    * **Kuvaus:** Välittää ID-tokenin repositorylle ja päivittää UI-tilan (`Loading` -> `Success/Error`).
    * **Rivit:** 72–85
    * <img width="1038" height="376" alt="image" src="https://github.com/user-attachments/assets/194a492a-609c-4d9b-83da-f229166634fb" />


* **Automaattinen kirjautuminen (Auto-login):**
    * **Tiedosto:** `viewmodel/auth/AuthViewModel.kt`
    * **Lohko:** `init`-metodi
    * **Kuvaus:** Tarkistaa olemassa olevan session heti sovelluksen käynnistyessä.
    * **Rivit:** 42–46

### 4.5 Notifikaatiot (Paikalliset ilmoitukset)
---
Sovellus käyttää paikallisia ilmoituksia (Local Notifications) informoidakseen käyttäjää, kun Joulupukin vastaus kirjeeseen on "saapunut" perille simuloidun matka-ajan jälkeen.

**Toteutuksen yksityiskohdat:**
* **Reaktiivinen tarkkailija:** `ReplyNotificationWatcher` seuraa tietokannan tilaa taustalla. Kun kirjeen tila muuttuu muotoon `replied` ja simuloitu odotusaika on kulunut, sovellus liipaisee ilmoituksen.
* **Notification Channel:** Sovellus luo Android 8.0+ vaatimusten mukaisen ilmoituskanavan (`santa_replies`) heti käynnistyksessä `SatumaaApplication`-luokassa.
* **Deep Linking:** Ilmoitusta klikkaamalla sovellus osaa avata suoraan kyseisen kirjeen. Tämä on toteutettu välittämällä `letterId` Intentin extrana `MainActivitylle`, joka navigoi oikeaan ruutuun.
* **Lupien hallinta:** Sovellus huomioi Android 13+ vaatiman `POST_NOTIFICATIONS`-luvan ja pyytää sen dynaamisesti `MainActivityn` käynnistyessä.



**Kooditodisteet:**

* **Ilmoituksen luonti ja näyttäminen:**
    * **Tiedosto:** `notifications/NotificationHelper.kt`
    * **Funktio:** `showSantaReplyNotification`
    * **Kuvaus:** Rakentaa `NotificationCompat.Builderilla` ilmoituksen, asettaa sille uniikin ID:n ja `PendingIntentin` navigointia varten.
    * **Rivit:** 56–99
    * <img width="877" height="186" alt="image" src="https://github.com/user-attachments/assets/c4fd5b1f-abd6-4525-ab39-81253751f7b5" />


* **Taustaseuranta (Watcher):**
    * **Tiedosto:** `notifications/ReplyNotificationWatcher.kt`
    * **Kuvaus:** Tarkkailee `letterRepository.getLetters()` -virtaa. Sisältää logiikan, jolla estetään tuplailmoitukset ja vanhojen viestien tulva käynnistyksessä (`primed`-muuttuja).
    * **Rivit:** 70–138

* **Ilmoituksesta navigointi (Deep Link):**
    * **Tiedosto:** `MainActivity.kt`
    * **Funktiot:** `handleIntent` ja `onNewIntent`
    * **Kuvaus:** Poimii kirjeen ID:n intentistä ja päivittää `launchedLetterId`-tilan, jolloin Compose-UI navigoi oikeaan kirjeeseen.
    * **Rivit:** 77–98

### 4.6 Kartta ja paikkatieto (OpenStreetMap & Sijaintipalvelut)
---
Sovellus visualisoi kirjeen "matkan" käyttäjän sijainnista Korvatunturille reaaliaikaisella karttanäkymällä. Tämä toteutetaan yhdistämällä laitteen GPS-paikannus ja **osmdroid**-karttakirjasto.

**Toteutuksen yksityiskohdat:**
* **OpenStreetMap (osmdroid):** Sovellus käyttää avoimen lähdekoodin `osmdroid`-kirjastoa karttapohjan esittämiseen. Karttanäkymä on upotettu Jetpack Composeen käyttämällä `AndroidView`-komponenttia.
* **Fused Location Provider:** Käyttäjän tarkka sijainti haetaan Googlen suosittelemalla `FusedLocationProviderClient`-rajapinnalla, joka yhdistää GPS- ja verkkotiedot energiatehokkaasti.
* **Reaaliaikainen animaatio:** `LetterMapScreen` laskee kirjeen etenemisen (0.0 – 1.0) vertaamalla nykyhetkeä kirjeen lähetysaikaan ja simuloituun saapumisaikaan. Tämä `progress`-arvo ohjaa tontun liikettä kartalla 60fps päivitysnopeudella.
* **Geometrinen laskenta:** Etäisyys (km) lasketaan dynaamisesti käyttäjän koordinaattien ja Korvatunturin välillä käyttämällä Androidin `Location.distanceBetween`-metodia.



**Kooditodisteet:**

* **Sijainnin haku (Repository):**
    * **Tiedosto:** `data/repository/LocationRepositoryImpl.kt`
    * **Funktio:** `getSingleLocation`
    * **Kuvaus:** Hakee laitteen sijainnin asynkronisesti hyödyntäen Kotlin Coroutines -laajennuksia (`.await()`).
    * **Rivit:** 63-71
    * <img width="1095" height="163" alt="image" src="https://github.com/user-attachments/assets/258f3a33-4550-4f0d-8c06-6400fb4096f8" />


* **Karttanäkymän logiikka ja animaatio:**
    * **Tiedosto:** `ui/screens/letter/LetterMapScreen.kt`
    * **Kuvaus:** Hallinnoi kartan elinkaarta, animaation edistymistä (`LaunchedEffect`) ja etäisyyden laskemista.
    * **Rivit:** 32-107

* **Karttakirjaston alustus:**
    * **Tiedosto:** `SatumaaApplication.kt`
    * **Kuvaus:** Asettaa pakollisen `userAgentValue`-asetuksen `osmdroid`-kirjastolle sovelluksen käynnistyessä.
    * **Rivit:** 58


### 4.7 & 4.8 Sensorit, kamera ja AR-kokemus
---
Sovelluksen "Etsi vastaus" -osio on teknisesti vaativin käyttöliittymäkomponentti. Se yhdistää laitteen kameran, useat asentoanturit ja reaaliaikaisen animaation yhdeksi AR-tyyliseksi (Augmented Reality) kokemukseksi, jossa digitaalinen kirje upotetaan osaksi fyysistä ympäristöä.

#### 4.7 Sensorit ja laiteominaisuudet
Sensorit mahdollistavat digitaalisen sisällön paikantamisen ja reagoimisen laitteen fyysisiin liikkeisiin:
* **Suunta-anturi (Rotation Vector):** `HeadingSensor.kt` lukee puhelimen asentoa avaruudessa. Sen avulla sovellus tietää, mihin suuntaan käyttäjä on kääntynyt, ja osaa näyttää "piilotetun" kirjeen vain silloin, kun puhelin osoittaa oikeaan suuntaan.
* **Kallistusanturi (Accelerometer):** `TiltSensor.kt` tunnistaa, kuinka paljon puhelinta kallistetaan eri suuntiin. Tätä tietoa käytetään luomaan kirjeelle hauska "leijunta-efekti": kun liikutat puhelinta, kirje heilahtaa mukana, mikä saa sen näyttämään siltä kuin se kelluisi oikeasti ilmassa.



#### 4.8 Kamera ja AR-näkymä
Kamera toimii AR-näkymän visuaalisena pohjana, luoden ikkunan todellisuuteen:
* **CameraX-integraatio:** `LetterCameraScreen.kt` hyödyntää Androidin modernia `CameraX`-kirjastoa. Kameran kuva näytetään sovelluksen taustalla reaaliajassa, ja sen päälle piirretään kaikki sovelluksen omat elementit.
* **Dynaaminen Overlay ja Animaatio:** `CameraOverlay.kt` yhdistää sensorien tiedot ja kameranäkymän. Se liikuttaa kirjettä ruudulla juuri oikeassa tahdissa puhelimen liikkeiden kanssa ja näyttää "kuuma–kylmä" -mittarin, joka opastaa käyttäjää etsinnässä.



**Kooditodisteet ja tekninen kuvaus:**

* **Sensorien hallinta (Asennon ja suunnan tunnistus):**
    * **Tiedosto:** `TiltSensor.kt` (Rivit 20–63)
    * <img width="865" height="116" alt="image" src="https://github.com/user-attachments/assets/f94c759d-6f00-415d-9991-d554c74d0b49" />

    * **Tiedosto:** `HeadingSensor.kt` (Rivit 14–62)
    * **Kuvaus:** Sensorien avulla seurataan puhelimen kallistusta ja katsesuuntaa. `TiltSensor` huolehtii liikkeen tasaisuudesta ja `HeadingSensor` muuntaa anturidatan ymmärrettäviksi asteiksi (0–360°), joiden perusteella kirjeen paikka määritetään.

* **Kameran sidonta ja AR-logiikka (Visuaalinen yhdistäminen):**
    * **Tiedosto:** `LetterCameraScreen.kt` (Rivit 32–130)
    * **Tiedosto:** `CameraOverlay.kt` (Rivit 22–65)
    * <img width="668" height="266" alt="image" src="https://github.com/user-attachments/assets/d79f4bcf-f594-40ef-b57d-fdcfc5f1255b" />

    * **Kuvaus:** Kameran esikatselu on sidottu osaksi sovelluksen elinkaarta, jotta se käynnistyy ja sammuu oikeaoppisesti. `CameraOverlay` laskee sensoridatan perusteella kirjeelle tarkan paikan ruudulla ja päivittää sitä jatkuvasti puhelimen liikkuessa.
      
### 4.9 Taustatyö ja elinkaarikestävyys (WorkManager)
---
Sovellus hyödyntää Androidin **WorkManager**-kirjastoa taustatehtävien suorittamiseen. Tämä takaa, että operaatiot (kuten poistot) suoritetaan loppuun asti, vaikka sovellus suljettaisiin tai verkkoyhteys katkeasi kesken suorituksen.

**Toteutuksen yksityiskohdat:**
* **Garantie of Execution:** Käyttämällä `CoroutineWorkeriä` varmistetaan, että poistopyynnöt pilveen (Firestore) menevät perille luotettavasti taustalla.
* **Retry-logiikka (Exponential Backoff):** Kaikissa workereissa on toteutettu automaattinen uudelleenyritys (`Result.retry()`), jos verkkovirhe tapahtuu. Järjestelmä yrittää operaatiota uudelleen optimaalisella viiveellä.
* **Hilt-integraatio:** Workereissa käytetään `@HiltWorker`-injektiota, mikä mahdollistaa repository- ja tietokantaluokkien käytön taustatyön sisällä siististi.
* **Offline-sync:** `SyncStoriesWorker` on valmisteltu pitämään paikallinen tietokanta ajan tasalla pilven kanssa automaattisesti taustalla, mikä parantaa offline-käyttökokemusta.



**Kooditodisteet:**

* **Varmistettu poisto (DeleteLetterWorker):**
    * **Tiedosto:** `workers/DeleteLetterWorker.kt`
    * **Kuvaus:** Varmistaa, että kirje poistetaan pilvestä, vaikka verkko pätkisi poistohetkellä. Hyödyntää `LetterFirestoreSource`-luokkaa.
    * **Rivit:** 19–45
    * <img width="978" height="446" alt="image" src="https://github.com/user-attachments/assets/e35071e3-a144-43f8-a129-83769a63dfcf" />
    * <img width="1299" height="86" alt="image" src="https://github.com/user-attachments/assets/658da238-94af-414b-bdcc-41aa0c6d6d9e" />
    * <img width="1365" height="127" alt="image" src="https://github.com/user-attachments/assets/529cffa4-0a3a-491f-b95c-ec4d0bed5328" />


* **Datan synkronointi (SyncStoriesWorker):**
    * **Tiedosto:** `workers/SyncStoriesWorker.kt`
    * **Kuvaus:** Hakee uusimmat sadut Firestoresta ja päivittää ne paikalliseen Room-kantaan.
    * **Rivit:** 23–46

* **WorkManagerin alustus (Hilt Configuration):**
    * **Tiedosto:** `SatumaaApplication.kt`
    * **Kuvaus:** Sovellusluokka on konfiguroitu käyttämään `HiltWorkerFactorya`, jotta workerit voivat käyttää injektoituja riippuvuuksia.
    * **Rivit:** 28–34

### 4.10 Vapaa vaativa ominaisuus: Generatiivinen tekoäly (Gemini AI)
Sovelluksen erikoisominaisuus on personoitujen satujen luominen käyttämällä Googlen **Gemini 2.0 Flash Lite** -mallia. Ominaisuus on toteutettu integraationa, jossa mobiilisovellus ohjaa tekoälyä tuottamaan lapselle sopivaa, laadukasta ja annettuihin aiheisiin pohjautuvaa sisältöä.

**Toteutuksen yksityiskohdat:**

* **Backend-pohjainen AI-ohjaus:** Kommunikaatio tekoälyn kanssa on eristetty omaan backend-kerrokseensa. Tämä mahdollistaa monimutkaisten kehotteiden (prompt) rakentamisen ja mallin konfiguroinnin turvallisesti, ilman että laitteen resursseja kuormitetaan tai API-avaimia altistetaan.
* **Älykäs personointi:** Backend-logiikka yhdistää Android-sovelluksesta tulevat parametrit (lapsen nimi, pituus, teemat) ja muuntaa ne tekoälylle optimoiduksi ohjeistukseksi.
* **Konfiguroitu luovuus ja turvallisuus:** Tekoälymalli on ohjeistettu järjestelmätasolla (System Instruction) toimimaan lastenkirjailijana. Kommunikaatiokerrokseen on integroitu tarkat `SafetySettings`-rajat, jotka suodattavat pois lapsille sopimattoman sisällön jo generointivaiheessa.

**Kooditodisteet:**

* **Tekoälyn ohjeistus ja mallin valinta:**
    * **Tiedosto:** `aiConfig.ts`
    * **Kuvaus:** Määrittää käytettävän Gemini-mallin, turva-asetukset sekä järjestelmäohjeet (System Instruction), jotka ohjaavat tekoälyn tyyliä.
    * **Rivit:** 1–21
    * <img width="1088" height="182" alt="image" src="https://github.com/user-attachments/assets/7250df5e-c7d3-4d7a-b30e-eadab089d97d" />

     

* **Sadun generointi (Prompt Engineering):**
    * **Tiedosto:** `generateStory.ts`
    * **Funktio:** `generateStory`
    * **Kuvaus:** Rakentaa dynaamisen kehotteen (prompt) käyttäjän syötteiden perusteella ja pyytää tekoälyltä vastauksen valmiiksi jäsennellyssä JSON-muodossa.
    * <img width="679" height="27" alt="image" src="https://github.com/user-attachments/assets/27026294-593e-4941-a94e-331a3fe041f6" />

    


___
## 🖼 5. Projekti – UI/UX & navigaatio

Sovelluksen käyttöliittymä on toteutettu **100% Jetpack Composella**, mikä mahdollistaa modernin, reaktiivisen ja yhtenäisen käyttökokemuksen. Suunnittelun keskiössä on ollut lapsiystävällisyys, selkeys ja maaginen tunnelma.

### 5.1 Näkymät + navigaatio
Navigaatio pohjautuu **Jetpack Navigation Compose** -kirjastoon, joka hallinnoi siirtymiä saumattomasti ns. *Single Activity* -arkkitehtuurin sisällä. Sovellus koostuu kolmesta pääkokonaisuudesta:

1.  **Kirjasto (Koti):** Selkeä listanäkymä tallennetuista saduista. Jokainen satu on esitetty "korttina", joka näyttää otsikon, teeman ja luontipäivämäärän.
2.  **Luo uusi satu:** Wizard-tyyppinen näkymä (`StoryScreen`), jossa käyttäjä valitsee parametrit (nimi, teema, avainsanat) ja käynnistää generoinnin.
3.  **Joulupukki & Profiili:** Erikoisnäkymät, jotka sisältävät interaktiivisia elementtejä kuten kartan (OSMdroid), AR-kameran ja tilastograafit.

**Navigaatiorakenne:**
* Päänäkymien välillä liikutaan **Bottom Navigation Bar** -alapalkin avulla (Koti / Luo / Profiili).
* Syvemmät tasot (esim. yksittäisen sadun luku tai karttanäkymä) avautuvat koko ruudun näkyminä, joista pääsee takaisin yläpalkin "Back"-painikkeella tai Androidin eleohjauksella.

### 📱 Kuvakaappaukset sovelluksesta

| | |
|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/774017c1-e219-4ad6-a55c-4a7ded004b6e" width="250" alt="Login"> <br> **1. Kirjautuminen** | <img src="https://github.com/user-attachments/assets/5ac07d34-d825-4dbe-b6bc-cda9a9bb0881" width="250" alt="Onboarding"> <br> **2. Seikkailijan nimi** |
| <img src="https://github.com/user-attachments/assets/050824a1-745d-4ddb-8684-a3a1a7b18812" width="250" alt="Menu"> <br> **3. Menu** | <img src="https://github.com/user-attachments/assets/6911ed7d-a724-477e-9715-38b6460c37fb" width="250" alt="Create Story"> <br> **4. Iltasadun luonti** |
| <img src="https://github.com/user-attachments/assets/33d32e62-2112-4a4b-a710-b774f9fab2f4" width="250" alt="Story Result"> <br> **5. Satu** | <img src="https://github.com/user-attachments/assets/c9b981ce-702d-4ef4-b8e3-5f39ebd24f37" width="250" alt="Saved Stories"> <br> **6. Tallennetut sadut** |
| <img src="https://github.com/user-attachments/assets/359ca496-b6f0-495b-8a8d-b5bc445e3616" width="250" alt="Letter Input"> <br> **7. Kirje joulupukille** | <img src="https://github.com/user-attachments/assets/b9d92fdb-dbfe-4c50-ae31-826502a3d133" width="250" alt="Santa Received"> <br> **8. Pukki sai kirjeesi** |
| <img src="https://github.com/user-attachments/assets/c4f8e0bb-8603-40a4-b146-0c9c317e9de6" width="250" alt="Math Puzzle"> <br> **9. Kirjeen avaus pulmatehtävällä** | <img src="https://github.com/user-attachments/assets/c8f39f70-1942-40ac-a4a0-9471b2eb5575" width="250" alt="AR Camera"> <br> **10. Kirjeen avaus AR-kameralla** |
| <img src="https://github.com/user-attachments/assets/e9fc68c5-8e28-4fcd-a8e3-1cdd64b6fade" width="250" alt="Santa Reply"> <br> **11. Pukin vastaus** | |




### 5.2 UI johdonmukainen (Material3 & Theming)
Sovellus noudattaa **Material Design 3** -ohjeistusta, mutta se on kustomoitu tukemaan *Satumaa*-brändiä (`SatumaaTheme`).

* **Teema ja Värit:** Käytämme keskitettyä teemaa, joka määrittelee sovelluksen ilmeen (`ui/theme/Theme.kt`). Väripaletti on maanläheinen ja rauhoittava (mm. *Forest Green, Terracotta, Sky Blue, StorybookPaper*), mikä tukee sadunkerronnan tunnelmaa ja luettavuutta.
* **Komponentit:** Olemme rakentaneet uudelleenkäytettäviä UI-komponentteja (kuten `StoryCard`, `MagicWordInput`, `AppButton`), jotta ulkoasu pysyy yhtenäisenä läpi sovelluksen ja koodin ylläpito on helpompaa.
* **Typografia:** Tekstit on hierarkisoitu selkeästi (Otsikko, Leipäteksti, Caption), mikä parantaa luettavuutta pienilläkin näytöillä.

### 5.3 Käytettävyys: State-Driven UI
Käyttöliittymä on täysin tilapohjainen (State-Driven). UI reagoi automaattisesti ViewModelin tarjoamaan `UiState`-tilaan (esim. `StoryUiState`):

* **Lataustilat (Loading):** Kun tekoäly generoi satua tai dataa haetaan pilvestä, käyttäjälle näytetään selkeä `CircularProgressIndicator` tai latausanimaatio. UI ei koskaan "jäädy".
* **Virhetilat (Error):** Verkkovirheet tai API-ongelmat näytetään käyttäjälle ymmärrettävinä virheilmoituksina (`ErrorView`), joissa on "Yritä uudelleen" -toiminto.
* **Onnistuminen (Success):** Kun operaatio valmistuu, sisältö animoidaan näkyviin (`AnimatedVisibility`), mikä tekee kokemuksesta sulavan.
* **Validoinnit:** Syötekentät validoidaan reaaliajassa, ja toimintopainikkeet aktivoituvat vasta, kun tarvittavat tiedot on syötetty.


| | |
|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/dfafe5af-e2ed-4482-87fa-d4f995016fa1" width="300" alt="Stats Screen 1"> | <img src="https://github.com/user-attachments/assets/f6ebe805-dca3-4e88-b3d6-221dfb3c892a" width="300" alt="Stats Screen 2"> |



### 5.4 Viimeistely: Portrait-first -suunnittelu
Sovellus on optimoitu ensisijaisesti **pystyasentoon (Portrait-first design)**, mikä tukee parhaiten tarinoiden lukemista ja mobiililaitteen luontevaa käyttöä yhdellä kädellä.

* **Tekninen joustavuus:** Vaikka käyttöliittymä on lukittu pystysuuntaiseen asetteluun, kaikki näkymät on kääritty skrollattaviin säiliöihin (`LazyColumn`, `verticalScroll`). Tämä varmistaa, että sovellus pysyy toimintakykyisenä ja kaikki elementit ovat saavutettavissa myös matalilla näytöillä tai jos laite käännetään vaakatilaan.
* **Skaalautuvuus:** Graafit ja kartat mukautuvat näytön leveyteen, mutta tekstisisältö on optimoitu pystylukemiseen.
* <img width="843" height="449" alt="image" src="https://github.com/user-attachments/assets/cf9ff1c2-e840-460d-9045-e05dbf9df4cf" />




___
## 📚 6. Projekti – arkkitehtuuri & koodin laatu

Sovellus on rakennettu noudattaen **kevennettyä Clean Architecture -mallia (Pragmatic Clean Architecture)** ja **MVVM**-suunnittelumallia (Model-View-ViewModel). Olemme valinneet pragmaattisen lähestymistavan, joka yhdistää arkkitehtuurin parhaat puolet (testattavuus ja vastuunjako) ilman pienen sovelluksen turhaa monimutkaisuutta.

### 6.1 Selkeä kerrosjako (MVVM & Clean Architecture)
Sovellus on jaettu kolmeen itsenäiseen kerrokseen, joissa riippuvuudet osoittavat aina sisäänpäin (Data -> Domain <- Presentation).

1.  **Presentation Layer (UI):**
    * Vastaa näkymien piirtämisestä (**Jetpack Compose**) ja tilan hallinnasta (**ViewModel**).
    * UI reagoi reaktiiviseen tilaan eikä sisällä bisneslogiikkaa.

2.  **Domain Layer (Logiikka):**
    * Sisältää sovelluksen "sydämen": tietomallit (`Story`) ja rajapinnat (`interface StoryRepository`).
    * **Mitä tehdään:** Rajapinta määrittelee sovelluksen kyvykkyydet, kuten `generateStoryPreview` (luo esikatselu) ja `deleteStory` (poista satu), ottamatta kantaa tekniseen toteutukseen.

3.  **Data Layer (Tiedonhallinta):**
    * **Miten tehdään:** Toteuttaa Domain-kerroksen rajapinnat (`class StoryRepositoryImpl`) ja koordinoi datavirtaa:
        * **Lukeminen:** Aina paikallisesta **Room-tietokannasta** (`StoryDao`), mikä takaa nopean UI:n ja offline-toimivuuden.
        * **Kirjoitus/Generointi:** Käyttää **Firebase Cloud Functions** -palvelua (`StoryFunctionsSource`) tekoälyn ajamiseen.
        * **Poisto:** Hyödyntää **Optimistic UI** -mallia: satu poistetaan heti paikallisesti, ja **WorkManager** hoitaa poiston pilvestä taustalla (`DeleteStoryWorker`).

> **💡 Esimerkki vastuunjaosta:**
> * **Domain (`StoryRepository`):** "Poista satu ID:llä X."
> * **Data (`StoryRepositoryImpl`):** "Poistan sadun heti Roomista, jotta lista päivittyy käyttäjälle heti. Sen jälkeen käynnistän taustatyön, joka yrittää poistaa sadun pilvestä, kun verkko on saatavilla."

> **Huomio arkkitehtuurivalinnasta:**
> Puhdasoppisessa Clean Architecturessa käytettäisiin erillisiä *Use Case* -luokkia. Tässä projektissa olemme tietoisesti yhdistäneet Domain-logiikan Repository-rajapintoihin koodin selkeyden vuoksi, mikä on Googlen suosittelema tapa tämän kokoluokan sovelluksissa.

**Hilt**-riippuvuuksien injektio sitoo nämä kerrokset yhteen ja mahdollistaa mm. WorkManagerin käytön repositoryssa.

### 6.2 State-hallinta ja datavirta (Unidirectional Data Flow)
Sovellus hyödyntää **yksisuuntaista datavirtaa (UDF)**. Tila virtaa alaspäin (ViewModel -> UI) ja tapahtumat ylöspäin (UI -> ViewModel).

* **StateFlow & UiState:** ViewModelit paljastavat UI:lle `StateFlow`-virran. Tila on mallinnettu `Sealed Interface` -luokilla (esim. `StoryUiState`), joka pakottaa UI:n käsittelemään kaikki tilanteet (`Loading`, `Success`, `Error`).
* **Single Source of Truth:** UI ei koskaan muokkaa dataa itse. Muutokset kulkevat ViewModelin kautta, ja lopullinen totuus tallentuu tietokantaan vasta käyttäjän toimesta.

#### Esimerkki datavirrasta: Sadun luonti ja tallennus
Havainnollistaaksemme arkkitehtuurin toimintaa, seurataan prosessia, jossa käyttäjä luo ja lopulta tallentaa sadun:

1.  **UI-tapahtuma (Generointipyyntö):**
    * Käyttäjä syöttää taikasanat `StoryScreen`-näkymässä ja painaa "Luo satu".
    * UI kutsuu `StoryViewModel.generateStory(...)` -funktiota.
2.  **Tilan muutos (Loading):**
    * ViewModel päivittää tilaksi `Loading`, jolloin UI näyttää latausindikaattorin.
3.  **Bisneslogiikka ja Backend:**
    * ViewModel kutsuu `StoryRepository.generateStoryPreview(...)` -funktiota.
    * Repository ottaa yhteyden **Firebase Cloud Functions** -rajapintaan (`StoryFunctionsSource`), joka generoi sadun Gemini AI:lla.
    * **Huom:** Tässä vaiheessa dataa *ei vielä tallenneta* tietokantaan, vaan Repository palauttaa generoidun sadun väliaikaisena `Story`-objektina ViewModelille.
4.  **UI-tapahtuma (Esikatselu):**
    * ViewModel päivittää tilaksi `Success(story)`, ja UI esittää valmiin sadun käyttäjälle luettavaksi.
5.  **Käyttäjän päätös (Tallennus):**
    * Jos käyttäjä on tyytyväinen, hän painaa "Tallenna"-painiketta.
    * UI kutsuu `StoryViewModel.saveCurrentStory()`.
    * Vasta nyt Repository tallentaa sadun pysyvästi paikalliseen **Room-tietokantaan** (`StoryDao`) ja käynnistää taustasynkronoinnin pilveen.

### 6.3 Luettava koodi ja rakenne
Projektin rakenne on organisoitu loogisesti teknisen vastuun mukaan (**Package by Layer**), mikä helpottaa koodin ylläpitoa:

* **Nimeäminen:** Luokat ja funktiot on nimetty englanniksi ja kuvaavasti (esim. `SyncStoriesWorker`, `MagicWordInput`), noudattaen Kotlinin nimeämiskäytäntöjä.
* **Komponentointi:** Käyttöliittymä on pilkottu pieniin, uudelleenkäytettäviin Compose-komponentteihin, mikä vähentää koodin toistoa.
* **Taustatyöt:** Raskaat synkronointioperaatiot on eriytetty **WorkManager**-työntekijöihin (`Worker`), jotta ne eivät kuormita käyttöliittymää.
### 🛠️ Arkkitehtuuri ja Rakenne

| | |
|:---:|:---:|
| <img src="https://github.com/user-attachments/assets/aa7d3607-c49e-4764-9b94-8a156ddba229" width="300" alt="Architecture Overview"> | <img src="https://github.com/user-attachments/assets/25189dfc-8295-463e-a959-ecd10bfc2529" width="300" alt="Project Structure"> |
| <img src="https://github.com/user-attachments/assets/0aafc442-8822-419b-9306-6ed5d1dc0503" width="300" alt="Data Flow Diagram"> | <img src="https://github.com/user-attachments/assets/97da0497-5561-407f-9566-eed39ee47186" width="300" alt="Navigation Graph"> |




___
## 🚀 7. Advanced Mobile -osuus: Secure AI with Firebase Cloud Functions

Tässä osiossa toteutimme "Backend-for-Frontend" -arkkitehtuurin. Android-sovellus ei keskustele suoraan tekoälyn (Gemini API) kanssa, vaan liikenne kulkee **Firebase Cloud Functions** -palvelun läpi. Tämä ratkaisee mobiilikehityksen suurimman tietoturvariskin: API-avaimien vuotamisen.

### 7.1 Aihe & opetusrunko (mikä/miksi/miten)
* **Mikä:** Serverless-backendin (Firebase Functions 2nd Gen) toteutus, joka toimii turvallisena välikätenä sovelluksen ja Gemini AI:n välillä.
* **Miksi:**
    * **Tietoturva:** API-avaimet (`GEMINI_API_KEY`) säilytetään **Google Secret Managerissa**, eikä niitä koskaan kovakoodata sovellukseen.
    * **Hallinta:** Voimme hyödyntää Firebasen ominaisuuksia kuten **App Check** (estää luvattomat kutsut), **Authentication** (tunnistaa käyttäjän) ja **Firestor**.
* **Miten:** Sovellus käyttää Firebasen Client SDK:ta kutsuakseen pilvifunktiota (`onCall`). Funktio suoritetaan Googlen palvelimella, se kutsuu tekoälyä ja palauttaa validoidun sadun JSON-objektina sovellukselle.

### 7.2 Käyttöönotto & riippuvuudet
Ratkaisu vaatii konfiguraatiot sekä Android-sovellukseen että backend-ympäristöön.

**Android (`build.gradle.kts`):**

> implementation(libs.firebase.functions)

**Backend (`package.json`):**
Backend-projektissa on määritelty seuraavat kriittiset riippuvuudet (Gemini SDK, Admin, Functions):

> "@google/generative-ai": "^0.24.1",
    "firebase-admin": "^13.6.0",
    "firebase-functions": "^7.0.0",

### 7.3 Koodidemo: Datan matka (Data Flow)
Videolla käymme läpi yksityiskohtaisesti, miten data kulkee sovelluksen ja pilven välillä. Prosessi etenee seuraavasti:

**1. UI & ViewModel (Triggeröinti):**
Käyttäjä painaa "Luo satu" -painiketta. `StoryViewModel` kerää parametrit, validoi ne ja asettaa UI:n `Loading`-tilaan.

**2. Repository & DataSource (Lähetys):**
Repository tarkistaa verkkoyhteyden ja delegoi kutsun `StoryFunctionsSource`-luokalle. Tässä tapahtuu varsinainen pilvifunktiokutsu:

> val data = hashMapOf(
                "childName" to childName,
                "keywords" to keywords,
                "length" to length,
                "style" to style
            )

            
            val result = functions
                .getHttpsCallable("generateStory")
                .withTimeout(60, TimeUnit.SECONDS)
                .call(data)
                .await()

**3. Backend (Logiikka & AI):**
Cloud Function (`generateStory`) vastaanottaa kutsun. Videolla käymme läpi backendin logiikan:
* **Tietoturva:** Funktio tarkistaa `request.auth` (käyttäjä) ja App Check -tokenin.
* **Rate Limiting:** Varmistaa, ettei käyttäjä tee liikaa pyyntöjä lyhyessä ajassa.
* **Gemini-kutsu:** Hakee API-avaimen turvallisesti ympäristömuuttujista, rakentaa promptin ja pyytää Geminiltä sadun JSON-muodossa.
* **Preview-tallennus:** Luo sadulle väliaikaisen ID:n ja tallentaa sen Firestoreen.

**4. Vastaus ja Esikatselu (Response):**
Backend palauttaa generoidun sadun objektina Android-sovellukselle.
* `StoryFunctionsSource` parsii vastauksen `Map`-objektista `Story`-olioksi.
* ViewModel päivittää tilaksi `Success`, ja UI:n `AnimatedVisibility` tuo sadun näkyviin.

### 7.4 Soveltaminen: Integraatio projektiin
Tämä on sovelluksen keskeisin ominaisuus. Integraatio on toteutettu **Repository Pattern** -mallin mukaisesti:
* **Abstraktio:** Repository piilottaa sen, että data tulee pilvifunktiosta. UI ei tiedä backendin olemassaolosta.
* **Virheenkäsittely:** Mahdolliset virheet (esim. AI-mallin ruuhkautuminen tai verkkokatkos) käsitellään `Result`-wrapperilla ja näytetään käyttäjälle selkeinä suomenkielisinä virheilmoituksina.

### 7.5 Videon laatu & hyödyllisyys
Videolla demonstroin koko putken toiminnan:
1.  **Arkkitehtuuri:** Miten Android-projekti konfiguroidaan käyttämään Cloud Functionsia.
2.  **Backend:** Käymme läpi TypeScript-koodin logiikan ja tietoturvamekanismit.
3.  **Live-demo:** Näytän emulaattorissa, miten napin painallus käynnistää prosessin ja tuo tekoälyn luoman sadun ruudulle.

[Linkki videoon: https://www.youtube.com/watch?v=8J9VjU4Qw-U&t=8s]

### 7.6 BONUS: Advanced-aihe integroituna varsinaiseen projektiin
✅ **Kyllä.** Tämä ei ole erillinen harjoitus, vaan Satumaa-sovelluksen ydintoiminnallisuus ("Core Feature"). Koko sadun luonti- ja tallennusprosessi nojaa tähän turvalliseen Cloud Functions -arkkitehtuuriin. Ratkaisu on tuotantovalmis ja noudattaa Googlen suosittelemia tietoturvakäytäntöjä (Secure by Default).

___
## 💎 8. Bonukset ja viimeistely

### 8.1 Projekti: poikkeuksellisen hyvä viimeistely (UX & Error Handling)
Sovelluksessa on kiinnitetty erityistä huomiota käyttökokemukseen ja helppouteen (UX) ja siihen, miten virhetilanteet viestitään loppukäyttäjälle (lapselle). Emme näytä teknisiä virhekoodeja, vaan tarinaa tukevia viestejä.

**1. Keskitetty virheenkäsittely (`ErrorUtils.kt`):**
Olemme toteuttaneet `Throwable.toUserFriendlyMessage` -laajennusfunktion, joka kääntää backendin virhekoodit ymmärrettäväksi suomeksi.
* **Esimerkki:** Jos tekoäly on ruuhkautunut (backend palauttaa koodin `GEMINI_BUSY`), sovellus ei näytä "Server Error 503", vaan kertoo käyttäjälle: *"Pukilla on kova kiire juuri nyt. Yritä hetken päästä uudelleen."*
* **Hyöty:** Tämä pitää sovelluksen "taianomaisen" tunnelman ehyenä myös ongelmatilanteissa.

**2. Lokalisointi ja "Tone of Voice" (`strings.xml`):**
Kaikki tekstit on eriytetty resurssitiedostoon, ja niiden sävy on suunniteltu lapsille sopivaksi:
* *Tekninen:* "Lataa dataa..." -> *Sovellus:* "Taikuutta ladataan..."
* *Tekninen:* "Odota vastausta..." -> *Sovellus:* "Pukki miettii vastausta... 🎅"

### 8.2 Muu opettajan hyväksymä bonus
Sovelluksen tekninen laajuus ylittää peruskurssin vaatimukset kahdella merkittävällä alueella:

1.  **Generatiivinen AI :** Sovellus luo uniikkia sisältöä (satuja ja kirjeitä) reaaliajassa käyttäjän syötteiden perusteella, eikä vain hae valmista dataa tietokannasta.
2.  **"Light AR" -ominaisuus:** Olemme integroineet laitteen kameran osaksi tarinankerrontaa. Kirjeen saapuessa käyttäjä voi "etsiä" kirjettä oikeasta maailmasta kameran avulla. Tämä yhdistää digitaalisen sisällön ja reaalimaailman (Augmented Reality -tyyppinen kokemus), mikä tekee sovelluksesta lapselle jännittävämmän.
