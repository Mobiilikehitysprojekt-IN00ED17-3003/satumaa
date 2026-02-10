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

3.3 Tehtävissä assigneet: Jokaisella tehtävällä on nimetty vastuuhenkilö.

3.4 Kanban elää: GitHub Projects Kanban-taulua on päivitetty reaaliajassa kehityksen aikana.

3.5 Sprint Planning: Suunnittelupalaverit on pidetty ennen jokaista Review-vaihetta.

3.6 Scrum Review -osallistumiset: Ryhmä on osallistunut säännöllisiin katselmointiin.

___
⚙️ 4. Projekti-ominaisuudet
-
4.1 HTTP / avoin API + listaus + detalji + haku/suodatus + lataus/virhetilat

4.2 Paikallinen tallennus (Room/SQLite/SQLDelight/AsyncStorage tms.)

4.3 Offline-/välimuistikäytös (näytä viimeisin data ilman nettiä) 

4.4 Autentikointi (Firebase/Auth0 tms.) + käyttäjäkohtainen näkymä

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

