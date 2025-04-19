### nettgame server
An example game server built upon my Nettgame game server framework

#### Perforamnce analysis

Pro prošetření této nastolené problematiky budou využity automatizované testy, vytvořené jako součást herního klienta, pro scénáře:
•	běhu herního serveru přímo v .jar artefaktu mimo kontejner
•	a běhu herního serveru v rámci .jar artefaktu uvnitř Docker kontejneru.
Předmětem a náplní testování bude zatížit herní server množstvím požadavků, za účelem nalezení možných nedostatků ve výkonu či znatelných zpoždění, zapříčiněných vysokou časovou deltou.
Šetření bude provedeno pomocí otevřeného analytického, široce uznávaného, nástroje WireShark, který slouží k vizualizaci, observaci, a především analytice provozu síťových rozhraní v rámci domény počítačových sítí.
Výzkum byl proveden pomocí zasílání požadavků a odpovědí mezi dvěma síťovými hosty, dále subjekty, klientem a serverem. Oba subjekty jsou platformami poháněnými procesorem AMD a operačním systémem Windows NT. Propojení mezi subjekty tvořil tzv. „domácí router“, přepínač 2. vrstvy (L2), ethernetový a bezdrátový spoj. Je ale axiomaticky známo, že rychlost spojení mezi dvěma a více síťovými hosty je omezena rychlostí nejpomalejšího článku tohoto spojení.
Jak již bylo avizováno, pro vytvoření požadavků směrem k serveru, byly využity automatizované testy vytvořené v rámci implementace ukázkového klienta. Testy jsou postaveny na základech frameworku JUnit a probíhají dle následujícího scénáře:
•	je vyvolán herní klient,
•	klient je připojen ke vzdálenému hernímu serveru,
•	jsou zadány požadované údaje pro spuštění herní relace a
•	poté je provedeno 60 konsekutivních pohybů zvolených pseudonáhodným generátorem programovacího jazyka Java Random.
Dalším krokem výzkumu je analýza síťového provozu probíhajícího při průběhu automatizovaných testů pomocí nástroje WireShark, kterým byly zachyceny celkem čtyři průběhy automatizovaných testů, pro každou z možností nasazení herního serveru.
V rámci výzkumu byl běh herního serveru přímo skrze .jar artefakt považován za „kontrolní“. Je také vhodné podotknout, že byl nástroj WireShark při provádění záznamu provozu umístěn na stranu klienta, což do jisté míry, ačkoliv nevyhnutelně, ovlivnilo výsledky analýzy.
Před zahájením jednotlivých analýz je také kritické ustanovit, že tyto testy jsou pouze orientačního charakteru a využívají pouze základních prostředků pro získání vhledu do výkonu v rámci Docker kontejneru. Bylo by tedy více než vhodné provést rigoróznější testování, obsahující, ku příkladu, simultánní požadavky od více než jednoho klienta a komplexnější logiku síťové hry.
Pro provedení první části výzkumu byl spuštěn .jar artefakt na vzdáleném serveru, čímž byl dočasně transformován na server herní. Pro běh databáze MySQL byla využita část Docker Compose kompozice, resp. Docker kontejner poskytující databázové služby. Při nastavení prostředí a infrastruktury pro provedení analýzy byl také vypnut firewall pro privátní síť, což není v žádném ohledu dobrou praxí, ale v tomto případě se jednalo o kontrolované testovací prostředí, tudíž nebyly případné následky natolik kritické.
 
Obrázek 8: První průběh běhu v rámci .jar artefaktu. Zdroj: autor.
Výsledky záznamů síťového provozu při běhu herního serveru jako „samotný“ .jar artefakt, viz Obrázek 9, jejichž hodnoty časové delty mezi požadavky, známé jako RTT (Round Trip Time), se koncentrovaly kolem doby trvání přibližně 15 milisekund, s okolními rozptyly v přibližném intervalu 5 (minimum) až 20 (maximum) milisekund.
To je očekávaný výsledek, neb herní server vyřizuje svou aktualizaci přibližně jednou za 17 milisekund. 
Podařilo se zaznamenat i značně větší delty, sahající až k hodnotám v okolí 60 milisekund. Ty mohly být zapříčiněny i neaktivitou klienta nebo ztrátou paketů (Packet Loss).
 
Obrázek 9: Druhý průběh výzkumu běhu v rámci .jar artefaktu. Zdroj: autor.
Druhý průběh pokusu s během v rámci .jar artefaktu vykazuje velmi podobné statistiky. Doba RTT se znovu pohybuje v rozsahu od 5 do 20 milisekund s občasnými deviacemi do vyšších hodnot, ty ale nemusely být způsobeny pouze dobou přenosu, nýbrž spíše externími faktory, jako výkon jednoho či obou subjektů.
Druhá část výzkumu byla provedena spuštěním Docker Compose konfigurace, pocházející z podkapitoly o jejím návrhu a následoval obdobný, takřka stejný analytický postup, jako v části první. 
Zkoumalo se, zda vnese přidaná složitost běhu v podobě Docker kontejneru nějaká značná zpoždění, či jiné vedlejší efekty, které by svědčily o tom, že není pro tuto úlohu vhodným.
 
Obrázek 10: První průběh běhu herního serveru v izolaci Docker kontejneru. Zdroj: autor.
Analýza požadavků učiněných vůči hernímu serveru, viz Obrázek 10, běžícímu v záštitě Docker kontejneru poukázala na srovnatelné časové delty, kvality komunikace a výměny dat, ovšem s menším rozdílem: rozptyly kolem maxima 20 milisekund se zdají být více četné, a dokonce i konvergentní k přibližným 30 milisekundám.
 
Obrázek 11: Druhý průběh pokusu s využitím Docker kontejneru jako běhového prostředí herního serveru. Zdroj: autor.
I u druhého průběhu testování běhu herního serveru v rámci Docker kontejneru se vyskytují srovnatelné hodnoty časových delt, až na pozoruhodný detail: delty již tolik nekonvergují k hranici 20 milisekund, spíše pak vůbec. Zdá se tedy, že se u prvního průběhu výzkumu případu nasazení v rámci Docker kontejneru jednalo o ojedinělý případ.
Výsledek obecně vyšších deviací hodnoty RTT u případu Docker kontejneru, vzhledem k tomu, že herní server nyní musí navíc projít skrze virtualizovanou izolační vrstvu kontejneru, by v ohledu intuitivní predikce byl smysluplný. Přidaná složitost virtualizované sítě technologie Docker by z úsudku měla přidat požadavkům na latenci. Jak se ale v rámci testů ukázalo, tuto úvahu nelze s jistotou ani potvrdit, ani vyvrátit. Proto zůstává pouze nejistou predikcí a nabízí se možnost provedení značně rigoróznějších testů, prověřujících tento problém.
Výsledkem a závěrem výzkumu tedy je, že kvality běhu herního serveru v rámci .jar artefaktu a Docker kontejneru jsou zdánlivě srovnatelné. Docker kontejner nevnáší, na základě provedených pokusů, do komunikace žádné elementy, které by prokazatelně omezovaly nebo jinak narušovaly jeho běžnou operaci.
Docker kontejner je tedy jednoznačně vhodným prostředím pro nasazení herního serveru. Navíc, oproti běhu v prostředí .jar artefaktu, přináší výhodu v podobě platformní nezávislosti, a to bez prokazatelných vedlejších efektů.
