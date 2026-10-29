# Traduel

Platformă pentru compararea, evaluarea și post-editarea traducerilor automate.

Proiect de licență în Informatică: o aplicație web care permite compararea traducerilor produse de mai multe motoare, alegerea variantei preferate și corectarea acesteia de către utilizator.

Prin post-editare se înțelege corectarea manuală a unei traduceri generate automat.

## Motivație

Proiectul pornește de la tema utilizării și percepției traducerilor automate. Cercetarea care a inspirat aplicația evidențiază aprecierea rapidității și a economiei de timp, dar și rezerve privind acuratețea, interpretarea contextului și necesitatea verificării umane.

Platforma urmărește să combine accesul rapid la mai multe traduceri cu posibilitatea utilizatorului de a le compara și revizui.

## Fluxul propus

1. Utilizatorul introduce textul, limbile sursă, respectiv țintă și contextul utilizării.
2. Aplicația solicită traduceri de la motoarele selectate.
3. Utilizatorul compară rezultatele și alege varianta preferată.
4. Poate indica motivul alegerii și poate corecta traducerea.
5. Aplicația salvează rezultatele, alegerea și modificările pentru consultare și analiză.

## Funcționalități planificate

- Integrarea Google Cloud Translation, DeepL API, Microsoft Azure Translator și Amazon Translate.
- Solicitarea traducerilor în paralel și tratarea separată a erorilor fiecărui serviciu.
- Compararea rezultatelor și selectarea traducerii preferate.
- Mod de evaluare anonimă, cu rezultate prezentate în ordine aleatorie și furnizori dezvăluiți după alegere.
- Editor pentru corectarea traducerii și păstrarea versiunii inițiale.
- Conturi de utilizator și istoric al traducerilor.
- Statistici despre preferințe, timpi de răspuns și efortul de corectare.
- Limitarea solicitărilor și urmărirea consumului API-urilor.

## Componenta experimentală

Evaluarea aplicației va urmări:

- Relația dintre motorul declarat favorit și traducerea aleasă în evaluarea anonimă.
- Diferențele de preferință în funcție de contextul textului.
- Relația dintre traducerea preferată și volumul corecturilor efectuate.

Analiza corecturilor va lua în considerare inserările, ștergerile, înlocuirile și timpul petrecut în editor. Aceste măsurători descriu efortul de post-editare; nu demonstrează singure corectitudinea lingvistică.

## Domeniul primei versiuni

Prima versiune va procesa texte introduse manual. Evaluarea experimentală se va concentra pe o pereche principală de limbi, care urmează să fie stabilită.

Importul documentelor, recunoașterea textului din imagini, traducerea audio și antrenarea unui model propriu nu fac parte din prima versiune.

## Tehnologii

Tehnologiile pentru frontend, backend și baza de date vor fi alese într-o etapă următoare.