# ✨ Satumaa

**MVVM-arkkitehtuurilla** varustettu, **Kotlin** ohjelmointikielellä ja **Jetpack Composea** hyödyntäen rakennettu satukirja-mobiilisovellus, joka tarjoaa ainutlaatuisia hetkiä satujen parissa sekä jännittävän tavan olla yhteydessä Joulupukkiin.  
**Firebase** huolehtii käyttäjähallinnasta, datan synkronoinnista ja reaaliaikaisista ilmoituksista. Sovellus hyödyntää mm. **Hilt-riippuvuuksien injektiota**, **Room-tietokantaa**, sekä **OSMdroid-karttakirjastoa**.


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

3.6 Scrum Review -osallistumiset: Ryhmä on osallistunut säännöllisiin katselmointiin.

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
---
Sovellus käyttää **Room**-kirjastoa datan pysyvään tallennukseen laitteelle. Tämä mahdollistaa satujen selaamisen ja tallentamisen strukturoidussa SQL-tietokannassa. Arkkitehtuuri noudattaa **Repository-mallia**, jossa tietokanta on eriytetty sovelluksen käyttöliittymästä.

**Toteutuksen yksityiskohdat:**
* **DAO (Data Access Object):** Määrittelee SQL-kyselyt (`SELECT`, `INSERT`) ja palauttaa datan reaktiivisena `Flow`-virtana.
* **Repository-integraatio:** Repository injektoi DAO:n ja kutsuu sen metodeja, piilottaen tietokantatoteutuksen ViewModelilta.
* **Data Mapping:** Repository muuntaa tietokannan `StoryEntity`-oliot sovelluksen sisäisiksi `Story`-domain-olioiksi (`toDomainModel`), jolloin tietokantariippuvuus ei vuoda UI-tasolle.

**Kooditodisteet:**

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
🖼 5. Projekti – UI/UX & navigaatio
-
5.1 Näkymät + navigaatio (väh. 3 näkymää, järkevä polku) 

5.2 UI johdonmukainen (teema/typografia/komponentit) 

5.3 Käytettävyys: lataus/virhe/tyhjä-tilat, validoinnit 

5.4 Viimeistely: responsiivisuus/landscape/tablet tms.

___
📚 6. Projekti – arkkitehtuuri & koodin laatu
-
6.1 Selkeä kerrosjako (MVVM tai vastaava) 

6.2 State-hallinta ja datavirta järkevästi 

6.3 Luettava koodi (nimeäminen, komponentointi, ei turhaa toistoa)

___
🚀 7. Advanced Mobile -osuus
-
7.1 Aihe & opetusrunko (mikä/miksi/miten) 

7.2 Käyttöönotto & riippuvuudet (kirjastot, konfiguraatiot, permissionit) 

7.3 Koodidemo (toimiva esimerkki) 

7.4 Soveltaminen: miten integroitaisiin projektiin (tai demo-projekti) 

7.5 Videon laatu & hyödyllisyys 

7.6 BONUS: Advanced-aihe integroituna myös varsinaiseen projektiin

___
⭐ 8. Bonukset
-
8.1 Projekti: poikkeuksellisen hyvä viimeistely (UX, virhetilat, tyhjätilat, demo) 

8.2 Muu opettajan hyväksymä bonus (kirjaa mikä) 

