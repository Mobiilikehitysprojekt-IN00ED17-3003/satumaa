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
Sovellus käyttää **Firebase Authentication** -palvelua ja **Firebase App Check** -suojausta varmistaakseen käyttäjien tunnistamisen ja sovelluksen eheyden. Menetelmänä on **Google Sign-In**, joka tarjoaa turvallisen tavan kirjautua sisään.

**Toteutuksen yksityiskohdat:**
* **Google OAuth:** Sovellus pyytää käyttäjältä ID-tokenin Googlen kirjautumispalvelusta ja välittää sen Firebaselle validointia varten.
* **Firebase App Check:** Sovelluksessa on käytössä App Check (Play Integrity), joka varmistaa, että backend-pyynnöt tulevat vain aidosta ja muokkaamattomasta sovellusversiosta. Tämä estää API-rajapintojen väärinkäytön sovelluksen ulkopuolelta.
* **Käyttäjäkohtainen näkymä:** Jokaisella käyttäjällä on oma `UID` (Unique ID), jonka perusteella sovellus suodattaa vain kyseisen käyttäjän omat tiedot Firestoresta.
* **Auto-login:** `AuthViewModel` tarkistaa sovelluksen käynnistyessä, onko käyttäjä jo kirjautunut (`currentUser != null`), jolloin istunto jatkuu saumattomasti.

**Kooditodisteet:**

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

4.5 Notifikaatiot (push tai local)

4.6 Kartta / paikkatieto

4.7 Sensorit / laiteominaisuus

4.8 Kamera / kuvagalleria

4.9 Taustatyö / elinkaarikestävyys (esim. WorkManager)

4.10 Vapaa vaativa ominaisuus (opettajan hyväksymä) – kuvaa tarkasti

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

