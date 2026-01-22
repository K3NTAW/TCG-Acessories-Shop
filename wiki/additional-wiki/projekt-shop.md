Projekt: GitHub Workflows
Ausgangslage:
Den Abschluss im Modul werden wir mit einem Projekt unser Können unter Beweis stellen.
Es gilt als Zusatz zu dem Projekt welches ihr im Modul M321 bearbeitet.
Zeitbedarf: 14 Lektionen (mit Projekt M321 zusammen)
Hilfsmittel: Java-Spring-Boot RestFul API
Methode/Sozialform:
, ,
Lernziele: ✓ siehe Abschlussarbeit-Bewertungsraster!
Legende: Einzelarbeit, Partnerarbeit, Dokumentation, Code, Präsentation
Beispiel Pipeline
1 PROJEKT-TESTING & PIPELINES 2
1.1 ZIEL 2
1.2 AUFTRAG 2
2 AUFGABEN – PIPELINE 2
12_1_Projekt_biztrips_M450-M324.docx / 27.11.2025
BBW-Informatik Luigi Cavuoti 1
| Abteilung Informatik
Informatik
Modul 321-324 – Abschluss-Projekt-MS
& Pipelines
1 Projekt-MS & Pipelines
1.1 Ziel
Hier geht es um das Projekt – MS & Pipelines aus den Modulen 321/324:
1.2 Auftrag
- Sie verwenden als Deployment Plattform Ihres Projektes GitHub/GitLab.
- Das Testing muss automatisiert werden und bei jedem push muss die Deployment Pipeline (Modul 324
devOps) aufgerufen werden mit den Stages: Build - Test – Deploy aufgerufen werden!
- Sie definieren ein iteratives Vorgehen mit GitHub, das Sprint Backlog und die dazugehörenden Items oder
User Stories.
o Jeder Microservice muss getestet werden Unit Testing, Postman Tests automatisch
2 Aufgaben – Pipeline
- Das Testing muss automatisiert werden und bei jedem push muss die Deployment Pipeline (Modul 324
devOps) aufgerufen werden mit den Stages: Test – Build – Deploy aufgerufen werden!
- Definieren Sie eine Pipeline auf GitLab/GitHub
o Die Stages: Build, Test, Deploy
o Benutzen Sie ihr Vorwissen mit den GitLab/GitHub Actions
o Aut. Build bei jedem Push soll ein Image mit Versionierung im Docker Registry oder
o Als Registry können Sie:
▪ GitHub Registry
▪ DockerHub
▪ AWS benutzen
o Aufgaben – Deploy
▪ Die Docker Image muss euf eine Cloud deployed werden wir z.B.:
• AWS
• Azure
• Fly.io
• Etc.
12_1_Projekt_biztrips_M450-M324.docx / 27.11.2025
BBW-Informatik Luigi Cavuoti