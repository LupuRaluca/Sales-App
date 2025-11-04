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











