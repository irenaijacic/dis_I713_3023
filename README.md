
Radi se o mikroservisnoj arhitekturi koja obuhvata sledeće mikroservise: user-service, course-service, payment-service, review-service i notification-service. Pored navedenih mikroservisa kreirani su i api-gateway, eureka-server i common-security koji su takođe bili neophodni za funkcionisanje sistema. 

U okviru ovog rada prikazan je jedan od načina funkcionisanja sajta za kupovanje online kurseva. Korisnici koji su kreirali svoj nalog mogu da kupuju odgovarajuće kurseve. Kada kažemo kurseve, ideja je bila da se oni odnose na sticanje znanja i vještine iz oblasti programnjiranja, tako su i sami kursevi orijentisani na teme iz pomenute oblasti. Inspiracija za odabir teme bila je stranica Udemy, koja pruža veliki broj kurseva različitih kategorija. Dakle, korisnici su obavezni da prvo kreiraju svoj nalog, a zatim imaju mogućnost pregleda svih kurseva, plaćanja i upisa na odabrani kurs. 

User-service : mogućnost pregleda svih i pojedinačnih registrovanih korisnika, kreiranje novog profila, brisanje i izmjena korisničnog profila. Što se tiče uloga, razlikuje se USER, običan registrovan kosrinik, i sa druge strane ADMIN. Neke od funkcija su omogućene samo za admina, dok većini pristup imaju i user i admin. Takođe, moguće je za svakog korisnika izlistati sve kurseve na koje je on upisan. Korisnik se jednom upiše na kurs i taj kurs mu nadalje ostaje dostupan bez vremenskog ograničenja trajanja pristupa. 

Course-service: mogućnost pregleda svih kreiranih kurseva, kao i dobijanje informacija o pojedinačnom kursu. Kreiranje, brisanje i izmjena kursa su rezervisane samo za korisnika sa ulogom admin. U okviru ovog servisa realizovana je i logika koja se odnosi na upis korisnika na kurs. Da bi korisnik imao pravo da se upiše na kurs, prvo mora izvršiti uplatu. Taj proces kontroliše payment-service.

Payment-service: mogućnost pregleda svih izvršenih transakcija u smislu koji korisnik je uplatio novac za koji kurs. Takođe, brisanje i izmjena transakcije. Mogućnost brisanja i izmjene transakcije ima samo admin. Korisnik je dužan da uplati tačan iznos prilikom kupovine određenog kursa. Ukoliko je iznos manji ili veći od traženog, korisniku se ispisuje odgovarajuća poruka. Realizacija plaćanja izvršena je bez uvođenja stranog sistema, te se u okviru ovog servisa nalazi metoda koja simulira rad banke i prihvata ili odbacuje izvršene uplate.

Review-service: mogućnost pregleda svih recenzija koje su ostavljene od strane korisnika. Recenzije se odnose na utiske korisnika pri korišćenju kreiranih kurseva. Omogućene su i kreiranje, izmjena i brisanje recenzije. Korisnik će ocjenom od 1 do 5, kao i pratećim komentarom, ocijeniti zadovoljstvo kupljenim kursom.

Notification-service: ovaj servis je zadužen za slanje obavještenja o izvršenim transakcijama i upisu na kurseve. U okviru ovog servisa definisana je i asinhrona komunikacija kojom se prate notifikacije o kupovini i upisu na kurs, koje su definisane u course-service i payment-service. Za realizaciju asinhrone komunikacije korišten je RabbitMQ.

Pored prethodno detaljno objašnjenih servisa, u okviru projekta se nalaze i api-gateway, eureka server i common-service. Što se tiče common-security , on u stvari predstavlja izdvojeni kod koji se ponavlja u svih pet glavnih servisa, te se na ovaj način izbjegava redudantnost koda. Tu je definisan CustomException koji se koristi za obradu greške izazvane dodavanjem circuit breaker-a. 
Api-gateway je posrednička komponenta između klijenata i backend servisa koja upravlja API pozivima, pruža autentifikaciju, autorizaciju, balansiranje opterećenja, keširanje i transformaciju podataka. Osigurava sigurnost, ograničava broj zahtjeva (rate limiting), bilježi logove i poboljšava performanse sistema. Ključna je u arhitekturi mikroservisa jer pojednostavljuje komunikaciju i optimizuje rad aplikacija. Tako i u primjeru ovog rada, api-gateway je taj koji koordiniše radom čitavog sistema. Tu su takođe definisani i rate limiter kao i circuit breaker koji poboljšavaju performanse. 
API Gateway koristi Oktu za autentifikaciju i autorizaciju, dok se OAuth2 koristi kao metod za slanje i verifikaciju tokena.
Tehnologije i biblioteke: Spring Boot, Spring Cloud Gateway, Spring Security + OAuth2, Resilience4J, Redis Rate Limiter, Reactor (Mono). Baza podataka je H2 in-memory baza. 

Na slici ispod prikazan je dijagram klasa kojim se predstavlja veza između 5 osnovnih servisa.

![Untitled Diagram drawio](https://github.com/user-attachments/assets/59d76bd2-af90-4f40-84d8-cf4763798cff)
