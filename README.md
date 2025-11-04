# Sales-App
Sales App for SIA  Multistrat12

## Echipa
- Paun Marius - developer full-stack, tester.
- Ciobanu Rares - developer front-end , project manager.
- Lupu Raluca - developer back-end , analist.
  

## Descriere App
Aplicație web pentru un magazin online de componente hardware care permite gestionarea și vânzarea produselor. Platforma oferă funcționalități pentru administratori (gestiune produse, comenzi, utilizatori) și pentru clienți (navigare produse, coș de cumpărături, finalizare comenzi).

Obiective:

- Oferirea unei platforme moderne pentru prezentarea și vânzarea produselor.

- Automatizarea proceselor de comandă.

- Gestionarea centralizată a stocurilor și a comenzilor.

- Crearea unei experiențe plăcute pentru utilizatori printr-un UI intuitiv.

## Arhitectura
Am ales arhitectura monolitică modulară, deoarece oferă o structură unitară a aplicației, menținând totodată o separare clară a responsabilităților între module.

Fiecare componentă (ex: utilizatori, produse, comenzi) este organizată ca modul intern distinct, ceea ce facilitează mentenanța, extinderea și înțelegerea sistemului, fără complexitatea suplimentară a comunicării între servicii.


## Stack tehnologic
Aplicația utilizează un set de tehnologii moderne, alese pentru a garanta performanță și ușurință în mentenanță:

### Frontend:
- React: Framework  pentru interfețe dinamice, component-based.
- Vite / Create React App:	tool pentru inițializarea și build-ul rapid al aplicației.
- React Router:	navigare între pagini fără reîncărcare completă.
           
### Backend:  
- Java + Spring Boot: framework  pentru aplicații web RESTful.
- Spring Data JPA + Hibernate:	acces și mapare obiect-relațională la baza de date.
- Spring Security :	autentificare și autorizare sigură pentru utilizatori.
- Lombok:	reduce codul boilerplate (getters, setters, constructori).
- Maven:	management al dependențelor și build-ului.

### Baza de date: 
- Postgres - pentru stocarea informațiilor despre produse, utilizatori și comenzi.
### Sistem de versionare: 
- GitHub - versiunilor codului sursă și colaborarea între membri echipei.


## Backlog inițial


### Epics:

- Gestionare produse (catalog, categorii, branduri)
- Gestionare stocuri (inventory & alerte)
- Autentificare și cont utilizator
- Coș de cumpărături & Checkout
- Gestionare comenzi
- Facturare
- Raportare & administrare

### Exemple User Stories (scrise în Gherkin):

- Feature: Gestionare produse
  
  As an Admin
  I want to adaug/editez/dezactivare produse
  So that pot gestiona catalogul magazinului

  Scenario: Adăugare produs nou
    Given că sunt autentificat ca administrator
    And deschid pagina "Produse -> Adaugă"
    When introduc numele, codul produsului, categoria, brandul și prețul
    And salvez produsul
    Then produsul apare în lista de produse active
    And produsul este vizibil în catalogul public

  Scenario: Prevenire SKU duplicat
    Given există un produs cu codul "4070"
    And sunt autentificat ca administrator
    When încerc să creez un produs cu același cod
    Then primesc eroare "cod deja folosit"
    And produsul nu este salvat

  Scenario: Dezactivare produs (ascundere din catalog)
    Given există produsul "Tastatură X" activ
    When îl setezi ca inactiv
    Then nu mai apare în listarea publică a produselor
    And rămâne vizibil în panoul de admin

- Feature: Căutare și filtrare produse
  
  Scenario: Filtrare după categorie și brand
    Given există produse în categoria "Plăci video" brand "NVIDIA"
    When aplic filtrul categorie "Plăci video" și brand "NVIDIA"
    Then văd doar produsele corespunzătoare filtrului

  Scenario: Vizualizare detalii produs
    Given există produsul "RTX 4070" în catalog
    When accesez pagina de detalii
    Then văd prețul, descrierea și disponibilitatea în stoc
- Feature: Gestionare stocuri
  
  Scenario: Actualizare stoc la recepție
    Given produsul "SSD 1TB" are 5 bucăți în stoc
    When adaug 15 bucăți în stoc
    Then stocul disponibil devine 20

  Scenario: Alertă stoc scăzut
    Given pragul de alertă pentru "SSD 1TB" este 5
    And stocul curent este 4
    Then văd un indicator "Low stock" în panoul de admin

- Feature: Autentificare și cont
  
  Scenario: Înregistrare cu email valid
    Given sunt vizitator pe pagina de înregistrare
    When introduc email valid și parolă puternică
    Then contul este creat
    And sunt autentificat automat

  Scenario: Login reușit
    Given am un cont existent
    When introduc credențiale valide
    Then sunt autentificat
    And văd secțiunea "Comenzile mele"

  Scenario: Login eșuat
    Given am un cont existent
    When introduc o parolă greșită
    Then primesc mesaj "Email sau parolă incorecte"
- Feature: Coș de cumpărături
  
  Background:
    Given există produsul "Mouse Pro" cu preț 199.99

  Scenario: Adăugare în coș
    Given coșul meu este gol
    When adaug 2 bucăți de "Mouse Pro" în coș
    Then coșul afișează 2 bucăți și total 399.98

  Scenario Outline: Actualizare cantitate cu validare stoc
    Given "Mouse Pro" are <stoc> bucăți disponibile
    And am deja <cantitate_initiala> bucăți în coș
    When actualizez cantitatea la <cantitate_noua>
    Then sistemul <rezultat>
- Feature: Checkout
  
  Scenario: Checkout reușit (fără cont)
    Given am produse în coș
    And introduc date de livrare valide
    When confirm comanda
    Then comanda este creată cu status "PENDING"
    And liniile comenzii conțin snapshotul de preț și TVA

  Scenario: Calcul TVA și total
    Given coșul conține produse cu TVA 19%
    When accesez pagina de confirmare
    Then văd subtotal, TVA și total general corecte
- Feature: Administrare comenzi
  
  Scenario: Actualizare status comandă
    Given comanda #200 este "PAID"
    When setez statusul la "FULFILLED"
    Then comanda apare cu status "FULFILLED" în lista de comenzi

  Scenario: Vizualizare comenzi client
    Given clientul "ion.popescu@example.com" are 3 comenzi
    When caut după email în admin
    Then văd toate comenzile ordonate descrescător după dată
- Feature: Emitere factură
  
  Scenario: Generare număr de factură unic
    Given comanda #300 este "PAID"
    When emit factura
    Then se creează o înregistrare în "invoices" cu "invoice_number" unic
    And factura este asociată comenzii #300
- Feature: Raportare vânzări și stoc
  
  Scenario: Vânzări pe perioadă
    Given există comenzi plătite în luna curentă
    When generez raportul de vânzări pentru luna curentă
    Then văd totalul încasărilor și numărul de comenzi

  Scenario: Produse cu stoc scăzut
    Given există produse sub pragul de stoc
    When deschid raportul "Stoc scăzut"
    Then văd lista de produse cu cantitatea disponibilă și pragul







