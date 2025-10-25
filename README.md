Wir sollen uns eine App überlegen, mit zwei Domain-Models. 
Es soll zwei Entities geben welche in der Beziehung "One to any" stehen.
Spring Data muss verstehen das es da ein @OneToMany gibt

Es braucht 1 Controller, 1 Service, die 2 Domain-Klassen (Model/Domain) / deren 2 Repositories (Persistence).
(Nice to have): 1 Guard (für Validation), 1 Request-Klasse (View-Model) (für First Line of Defence), 

Später wird gemeinsam der Controller-Test durchgeführt mit der H2-Datenbank.
Dies ist also nicht zu tun.

![Klassen-Diagramm-Mini_Shop_App.png](Klassen-Diagramm-Mini_Shop_App.png)