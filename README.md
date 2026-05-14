# Infinite Runner - JavaFX Mänguprojekt

## Autorid
- Innar
- Endel

---

## Projekti kirjeldus

### Eesmärk
Infinite Runner on klassikaline lõputu jooksmise mäng, kus mängija (kollane tibu) peab vältima takistusi hüpates ja sliidides. Mängu eesmärk on koguda võimalikult palju punkte ellu jäädes nii kaua kui võimalik. Mäng muutub aja jooksul kiiremaks ja takistusi genereeritakse rohkem, muutes selle järjest keeriulisemaks.

### Programmi üldine töö
Mäng on loodud JavaFX raamistiku abil ja koosneb mitmest põhikomponendist:

1. **Peamenüü** - Võimaldab alustada mängu või vaadata tulemuste tabelit (scoreboard)
2. **Mängustseen** - Põhimäng, kus mängija jookseb ja väldib takistusi
3. **Game Over ekraan** - Kuvab tulemuse ja võimaldab seda salvestada
4. **Scoreboard** - Näitab top 10 tulemust

Mäng kasutab animeeritud lindu (Peetrit) mängija erinevate tegevuste (jooksmine, hüppamine, sliidimine) kuvamiseks. Takistusi genereeritakse mängu edenedes rohkem, muutes selle keerulisemaks.

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
- `naitaPeamenuu()` - Kuvab peamenüü (alustamine ja scoreboard)
- `naitaGameOver(int skoor)` - Kuvab mängu lõpu ekraani koos skoori sisestamisega
- `salvestaSkoor(String nimi, int skoor)` - Salvestab mängija tulemuse faili
- `naitaScoreboard()` - Kuvab top 10 tulemusi sorteeritult
- `loeFailist()` - Loeb tulemused binaarfailist (DataInputStream)

### GameScene.java
**Eesmärk:** Mängu põhiloogika ja visuaalide haldamine.

**Olulisemad meetodid:**
- `uuenda()` - Uuendab mängu olekut iga kaadri kohta
- `genereeriTakistus()` - Loob uusi takistusi mustrite põhiselt
- `kontrolliKokkuporget()` - Kontrollib kokkupõrkeid mängija ja takistuste vahel

### Player.java
**Eesmärk:** Mängija (Peetri) haldamine, liikumine ja animatsioon.

**Olulisemad meetodid:**
- `Player(Pane manguvali)` - Loob mängija ja laeb sprite'id
- `uuenda()` - Uuendab füüsikat, animatsiooni ja hitboxi iga kaadri kohta
- `updateAnimation()` - Uuendab sprite animatsiooni frame'e

### ObstacleType.java
**Eesmärk:** Enum takistuste tüüpide defineerimiseks.

### ObstaclePattern.java
**Eesmärk:** Takistuste mustrite defineerimine.

**Olulisemad meetodid:**
- `ObstaclePattern(int spacing, ObstacleType... types)` - Loob mustri takistuste vahemaaga

### ViganeMängijaNimi.java
**Eesmärk:** Custom exception mängija nime valideerimiseks.


## Projekti tegemise protsess

### Etapp 1: Projekti seadistamine ja põhistruktuur
- Maven projekti loomine ja JavaFX seadistamine
- Põhiklasside (Main, GameScene, Player) skelettide loomine
- **Osalejad:** Innar, Endel

### Etapp 2: Mängija mehaanika
- Player klassi arendamine (hüppamine, liikumine)
- Sprite'ide laadimine ja animatsiooni süsteem
- Füüsika (gravitatsioon, kiirus) lisamine
- **Osalejad:** Innar, Endel

### Etapp 3: Takistuste süsteem
- ObstacleType ja ObstaclePattern klasside loomine
- Takistuste genereerimise loogika
- Kokkupõrgete tuvastamine (hitbox süsteem)
- **Osalejad:** Innar

### Etapp 4: Failisüsteem ja andmete salvestamine
- Binaarfailide lugemine/kirjutamine (DataInputStream/DataOutputStream)
- Tulemuste salvestamise loogika
- Exception handling (ViganeMängijaNimi)
- **Osalejad:** Endel

### Etapp 5: Kasutajaliides ja menüüd
- Peamenüü disain
- Game Over ekraan
- Scoreboard implementeerimine
- **Osalejad:** Innar, Endel

### Etapp 6: Viimistlemine ja testimine
- Bug-ide parandamine ja testimine
- Füüsika lihvimine
- **Osalejad:** Innar, Endel

---

## Tehisintellekti kasutamine
- Claude - Readme kirjutamiseks, linnu (Peetri) animeerimiseks

## Rühmaliikmete panus ja ajakulu

### Endel, Innar
**Tehtud klassid/meetodid:**
- [Main.java - kõik menüüd ja UI]
- [GameScene.java - game loop ja kokkupõrked]

**Ajakulu:** ~10 tundi

---

### Endel, Innar
**Tehtud klassid/meetodid:**
- [Player.java - animatsioon ja füüsika]
- [ObstacleType.java, ObstaclePattern.java]
- [Peetri joonistamine ja animeerimine]

**Ajakulu:** ~15 tundi

---

### Endel, Innar
**Tehtud klassid/meetodid:**
- [Failisüsteem - salvestamine/laadimine]
- [Scoreboard loogikas]

**Ajakulu:** ~6 tundi

### Endel, Innar
**Mängu testimine**

**Ajakulu** ~4 tundi

---

## Tegemise mured ja väljakutsed

### Puuduvad teadmised/oskused
- Peetri animeerimine

### Tekkinud probleemid
- Kasutaja sisestused ei saanud erindeid visata, tuli mõelda mingi meetod, kus seda saaks teha

## Hinnang lõpptulemusele

### Hästi õnnestus
-  Toimiv füüsika süsteem (gravitatsioon, hüppamine)
-  Binaarfailide salvestamine ja laadimine
-  UI ja menüüd
-  Mustripõhine takistuste süsteem

### Tulevikus
- Rohkem erinevaid takistuste mustreid
- Helide ja muusika lisamine
- Mitmekordne elu süsteem
- Online scoreboard (hetkel ainult lokaalne)

---

## Testimine
- Mängisime mängu ja vaatasime et midagi valesti ei läheks. Kasutasime printe eirinevates kohtrades, et hoida mängu tegevusel silma peal mängimise ajal. Vea leidmisel parandasime selle, vajadusel muutsime mängu parameetreid, et oleks lihtsam testida edge-case.

---

## Kokkuvõte
Infinite Runner projekt õnnestus hästi ja kõik põhinõuded said täidetud. Mäng on mängitav, lõbus ja tehniliselt korralikult implementeeritud. Projekt andis head praktikat JavaFX, failisüsteemi käsitlemise ja game loop'i osas.

**Projekti tugevused:** 
Hästi struktureeritud kood, toimiv animatsioon, dünaamiline raskusaste ja kasutajasõbralik liides.

**Edasiarenduse võimalused:** 
Helid, rohkem funktsioone (Mitme tasandiline, mitu elu), online scoreboard.
