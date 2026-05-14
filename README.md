# Infinite Runner - JavaFX Mänguprojekt

## Autorid
- Innar
- Endel
- Jõuluvana

---

## Projekti kirjeldus

### Eesmärk
Infinite Runner on klassikaline lõputu jooksmise mäng, kus mängija (kollane tibu) peab vältima takistusi hüpates ja sliidides. Mängu eesmärk on koguda võimalikult palju punkte ellu jäädes nii kaua kui võimalik. Mäng muutub aja jooksul kiiremaks, muutes selle järjest väljakutsuvamaks.

### Programmi üldine töö
Mäng on loodud JavaFX raamistiku abil ja koosneb mitmest põhikomponendist:

1. **Peamenüü** - Võimaldab alustada mängu või vaadata tulemuste tabelit (scoreboard)
2. **Mängustseen** - Põhimäng, kus mängija jookseb ja väldib takistusi
3. **Game Over ekraan** - Kuvab tulemuse ja võimaldab seda salvestada
4. **Scoreboard** - Näitab top 10 tulemust

Mäng kasutab animeeritud sprite'e mängija erinevate tegevuste (jooksmine, hüppamine, sliidimine) kuvamiseks. Takistused genereeritakse mustrite põhiselt, mis muutuvad mängu edenedes keerulisemaks.

### Kasutusjuhend
**Klahvid:**
- `SPACE` või `↑` - Hüppamine
- `↓` - Sliidimine

**Mängu alustamine:**
1. Käivita programm
2. Vali peamenüüst "ALUSTA MÄNGU"
3. Vältida madalatest takistustest hüpates ja kõrgetest takistustest alla sliidides
4. Pärast mängu lõppu sisesta oma nimi ja salvesta tulemus

**Tehnilised nõuded:**
- Java 25 või uuem
- JavaFX 21.0.6
- Maven projekti haldamiseks

**Käivitamine:**
```bash
mvn clean javafx:run
```

---

## Klasside kirjeldus

### Main.java
**Eesmärk:** Rakenduse peaklass, mis haldab kõiki stseene ja kasutajaliidest.

**Olulisemad meetodid:**
- `start(Stage primaryStage)` - JavaFX rakenduse käivitamise meetod
- `naitaPeamenuu()` - Kuvab peamenüü (alustamine ja scoreboard)
- `alustaMangu()` - Alustab uut mängu, loob GameScene'i
- `naitaGameOver(int skoor)` - Kuvab mängu lõpu ekraani koos skoori sisestamisega
- `salvestaSkoor(String nimi, int skoor)` - Salvestab mängija tulemuse faili
- `naitaScoreboard()` - Kuvab top 10 tulemusi sorteeritult
- `kirjutaFaili(Map<String,Integer> tulemused)` - Kirjutab tulemused binaarfaili (DataOutputStream)
- `loeFailist()` - Loeb tulemused binaarfailist (DataInputStream)

**Eripärad:**
- Kasutab DataInputStream/DataOutputStream binaarfailide käsitlemiseks
- Rakendab custom exception'it (`ViganeMängijaNimi`) sisendi valideerimiseks
- Tulemused salvestatakse Map'i ja sorteeritakse stream API abil

### GameScene.java
**Eesmärk:** Mängu põhiloogika ja visuaalide haldamine.

**Olulisemad meetodid:**
- `GameScene(Consumer<Integer> onGameOver, double width, double height)` - Konstruktor, mis loob mänguvälja
- `alusta()` - Käivitab mängu peamise game loop'i (AnimationTimer)
- `uuenda()` - Uuendab mängu olekut iga kaadri kohta (60 FPS)
- `handleKeyPress(KeyEvent event)` - Käsitleb klaviatuuri sisendeid
- `handleKeyRelease(KeyEvent event)` - Käsitleb klaviatuuri vabastamist
- `genereeriTakistus()` - Loob uusi takistusi mustrite põhiselt
- `kontrolliKokkuporget()` - Kontrollib kokkupõrkeid mängija ja takistuste vahel
- `uuendaSkoori()` - Uuendab skoori ja raskusastet
- `uuendaTausta()` - Liigutab taustakihte paralax efekti jaoks

**Eripärad:**
- Kasutab AnimationTimer'it 60 FPS mängutsiklist
- Dünaamiline raskusaste - kiirus kasvab skoori suurenedes
- Paralax taustakihid (taevas, mäed, maastik)
- Mustripõhine takistuste genereerimine

### Player.java
**Eesmärk:** Mängija tegelase (kollane tibu) haldamine, liikumine ja animatsioon.

**Olulisemad meetodid:**
- `Player(Pane manguvali)` - Loob mängija ja laadib sprite'id
- `uuenda()` - Uuendab füüsikat, animatsiooni ja hitboxi iga kaadri kohta
- `hyppa()` - Alustab hüppamist (kui maapinnal või sliidimise ajal)
- `lopetaHype()` - Lõpetab hüppe (võimaldab muutuvat hüppe kõrgust)
- `alustaSliidimist()` - Alustab sliidimist (vähendab hitboxi)
- `updateAnimation()` - Uuendab sprite animatsiooni frame'e
- `getKuvand()` - Tagastab hitboxi kokkupõrgete jaoks

**Eripärad:**
- Sprite sheet animatsioon (jooksmine 12 frame'i, hüppamine 6, sliidimine 9)
- Dünaamiline hitbox (muutub sliidimise ajal)
- Gravitatsiooni ja füüsika simulatsioon
- Fallback ristkülik kui sprite'id ei laadi

### ObstacleType.java
**Eesmärk:** Enum takistuste tüüpide defineerimiseks.

**Väärtused:**
- `LOW` - Madal takistus (üle tuleb hüpata)
- `HIGH` - Kõrge takistus (alt tuleb sliidida)

### ObstaclePattern.java
**Eesmärk:** Takistuste mustrite defineerimine.

**Olulisemad meetodid:**
- `ObstaclePattern(int spacing, ObstacleType... types)` - Loob mustri takistuste vahemaaga
- `getObstacles()` - Tagastab takistuste loendi
- `getSpacing()` - Tagastab vahemaa takistuste vahel

**Eripärad:**
- Võimaldab luua keerulisi takistuste kombinatsioone
- Määrab vahemaa takistuste vahel mustri sees

### ViganeMängijaNimi.java
**Eesmärk:** Custom exception mängija nime valideerimiseks.

**Kasutusjuht:**
- Visatakse kui mängija nimi sisaldab numbreid
- Extends `RuntimeException`

---

## Projekti tegemise protsess

### Etapp 1: Projekti seadistamine ja põhistruktuur
- Maven projekti loomine ja JavaFX seadistamine
- Põhiklasside (Main, GameScene, Player) skelettide loomine
- **Osalejad:** Innar, Endel

### Etapp 2: Mängija mehaanika
- Player klassi arendamine (hüppamine, liikumine)
- Sprite'ide laadimine ja animatsiooni süsteem
- Füüsika (gravitatsioon, kiirus) implementeerimine
- **Osalejad:** [NIMED]

### Etapp 3: Takistuste süsteem
- ObstacleType ja ObstaclePattern klasside loomine
- Takistuste genereerimise loogika
- Kokkupõrgete tuvastamine (hitbox süsteem)
- **Osalejad:** [NIMED]

### Etapp 4: Kasutajaliides ja menüüd
- Peamenüü disain
- Game Over ekraan
- Scoreboard implementeerimine
- **Osalejad:** [NIMED]

### Etapp 5: Failisüsteem ja andmete salvestamine
- Binaarfailide lugemine/kirjutamine (DataInputStream/DataOutputStream)
- Tulemuste salvestamise loogika
- Exception handling (ViganeMängijaNimi)
- **Osalejad:** [NIMED]

### Etapp 6: Viimistlemine ja testimine
- Sliidimise mehaanika lisamine
- Tausta paralax efekt
- Dünaamiline raskusaste
- Bug-ide parandamine ja testimine
- **Osalejad:** [KÕIK]

---

## Tehisintellekti kasutamine

### Kasutatud tööriistad
- [NB: Siia palun lisada konkreetsed tööriistad, nt ChatGPT, GitHub Copilot, Claude, jne]

### Kasutamise viisid
[NB: Siia palun kirjeldada, kuidas ja milleks AI'd kasutati, näiteks:]
- Koodi struktuuri planeerimine ja nõuanded
- Sprite animatsiooni loogika väljatöötamine
- Exception handling'u implementeerimine
- JavaDoc kommentaaride genereerimine
- Debug'imine ja vea otsimine

### AI abil loodud osa
[NB: Siia palun märkida konkreetsed klassid/meetodid/koodiosad, mis on AI abil loodud või mille puhul AI andis olulist abi. Näiteks:]
- ~30% Player.java animatsiooni süsteemist
- ObstaclePattern klassi struktuur
- Failisüsteemi meetodid (kirjutaFaili, loeFailist)
- [JNE...]

---

## Rühmaliikmete panus ja ajakulu

### [NIMI 1]
**Tehtud klassid/meetodid:**
- [Näiteks: Main.java - kõik menüüd ja UI]
- [GameScene.java - game loop ja kokkupõrked]

**Ajakulu:** ~[X] tundi

---

### [NIMI 2]
**Tehtud klassid/meetodid:**
- [Näiteks: Player.java - animatsioon ja füüsika]
- [ObstacleType.java, ObstaclePattern.java]

**Ajakulu:** ~[X] tundi

---

### [NIMI 3]
**Tehtud klassid/meetodid:**
- [Näiteks: Failisüsteem - salvestamine/laadimine]
- [Scoreboard loogikas]

**Ajakulu:** ~[X] tundi

---

## Tegemise mured ja väljakutsed

### Puuduvad teadmised/oskused
[NB: Siia palun lisada, millistest teadmistest tunti puudust, näiteks:]
- JavaFX AnimationTimer'i täpne toimimine
- Sprite sheet'ide käsitlemine ja viewport'ide kasutamine
- DataInputStream/DataOutputStream binaarfailide jaoks
- Kokkupõrgete tuvastamise optimeerimine
- Game loop'i ja FPS haldamine

### Tekkinud probleemid
[NB: Konkreetsed probleemid ja lahendused:]
- Sliidimise hitbox ei toiminud õigesti alguses → lahendati dünaamilise hitbox kõrgusega
- Hüppe kõrgus oli raske kontrollida → lisati muutuv hüpe (lopetaHype meetod)
- Sprite'id ei laadinud õigel teel → lisati resource path ja fallback ristkülik
- [JNE...]

---

## Hinnang lõpptulemusele

### Hästi õnnestus
[NB: Mille saite hästi hakkama:]
- ✅ Sujuv animatsioon ja sprite'ide kasutamine
- ✅ Toimiv füüsika süsteem (gravitatsioon, hüppamine)
- ✅ Dünaamiline raskusaste
- ✅ Binaarfailide salvestamine ja laadimine
- ✅ Kasutajasõbralik UI ja menüüd
- ✅ Mustripõhine takistuste süsteem
- ✅ Paralax tausta efekt

### Vajab arendamist
[NB: Mis jäi puudu või võiks paremini olla:]
- 🔧 Rohkem erinevaid takistuste mustreid
- 🔧 Helide ja muusika lisamine
- 🔧 PowerUp'id (kaitse, aeglustamine jne)
- 🔧 Mitmekordne elu süsteem
- 🔧 Paremad graafilised efektid (particles)
- 🔧 Online scoreboard (hetkel ainult lokaalne)
- 🔧 Mobiilse versiooni tugi

---

## Testimine

### Üksiktestid (unit tests)
[NB: Kirjeldage, kuidas testisitte üksikuid klasse/meetodeid:]

**Player klassi testimine:**
- Testitud hüppamist ja sliidimist konsoolile väljundeid printides
- Kontrollitud hitboxi muutumist sliidimise ajal (visual debug - punane ääris)
- Testitud gravitatsiooni ja füüsikat erinevatel kiirustel

**GameScene testimine:**
- Testitud kokkupõrgete tuvastamist hitbox'ide kattumise kaudu
- Kontrollitud takistuste genereerimist (konsooli väljund)
- Testitud skoori arvestust ja raskusastme muutumist

**Failisüsteemi testimine:**
- Testitud salvestamist ja laadimist erinevate nimedega
- Kontrollitud, et suurem skoor kirjutatakse üle
- Testitud exception'it (ViganeMängijaNimi) numbrite sisestamisel

### Integratsioonitestid
[NB: Kuidas testisitte kogu programmi tervikuna:]

**Täielik mängu läbimäng:**
1. Käivitatud programm ja kontrollitud, et peamenüü avaneb korrektselt
2. Alustatud mängu ja mängitud ~30 sekundit
3. Testitud mõlemaid kontrolle (hüpe ja sliid)
4. Lasknud teadlikult kokkupõrke juhtuda
5. Sisestatud nimi ja salvestatud skoor
6. Kontrollitud, et skoor ilmub scoreboard'ile

**Edge case'id:**
- Tühja nime sisestamine → muutub "Anonüümne"
- Numbriga nime sisestamine → visatakse ViganeMängijaNimi exception
- Kiire nuppude vajutamine → ei teki duplitseeritud hüppeid
- Hüppamine sliidimise ajal → sliid lõpeb ja hüpe algab
- Akna suuruse muutmine → elemendid jäävad korrektselt paika

**Manuaalne testimine:**
- Mängitud mängu 10+ korda erinevatel raskusastmetel
- Testitud kõiki menüü nuppe ja navigeerimist
- Kontrollitud, et animatsioonid töötavad sujuvalt
- Veendutud, et mäng ei jookse kinni pikema mängimise korral

### Tulemused
- ✅ Kõik põhifunktsioonid töötavad ootuspäraselt
- ✅ Exception handling toimib korrektselt
- ✅ Failisüsteem salvestab ja laadib andmeid õigesti
- ✅ Kasutajaliides on responsiitvne ja intuitiivne
- ✅ Performance on hea (60 FPS stabiilselt)

---

## Kokkuvõte

Infinite Runner projekt õnnestus hästi ja kõik põhinõuded said täidetud. Mäng on mängitav, lõbus ja tehniliselt korralikult implementeeritud. Projekt andis head praktikat JavaFX, objektorienteeritud programmeerimise, failisüsteemi käsitlemise ja game loop'i dizaini osas.

**Projekti tugevused:** Hästi struktureeritud kood, toimiv animatsioon, dünaamiline raskusaste ja kasutajasõbralik liides.

**Edasiarenduse võimalused:** Helid, rohkem funktsioone (PowerUp'id, mitu elu), online scoreboard.

---

**Projekti repositoorium:** [GIT LINK kui on]
**Loodud:** 2026
**Kursus:** [KURSUSE NIMI]
**Õppeasutus:** Tartu Ülikool, Loodus- ja täppisteaduste valdkond, Arvutiteaduse instituut
