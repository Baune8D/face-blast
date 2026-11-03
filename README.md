# FaceBlast

[![CI](https://github.com/Baune8D/face-blast/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Baune8D/face-blast/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/github/license/Baune8D/face-blast)](https://github.com/Baune8D/face-blast/blob/main/LICENSE.txt)

A small Swing game I wrote in Java while I went to school in 2012. Faces and robots close in from every side of the screen, and you shoot them before they reach you.

## Requirements

- JDK 21 or later. The Maven wrapper downloads Maven itself.

## Build and run

```sh
./mvnw package
java -jar target/face-blast.jar
```

On Windows, use `mvnw.cmd package`. Every CI run also uploads `face-blast.jar` as a workflow artifact. To work on the game in IntelliJ IDEA, open the folder and IntelliJ imports `pom.xml`.

## How to play

- **Menu:** pick Campaign Mode or Survival Mode with the up and down arrows, and start with Enter.
- **Move:** the arrow keys.
- **Shoot:** `W`, `A`, `S` and `D` shoot up, left, down and right.
- **Campaign Mode:** five levels. Kill 10 enemies to clear the first level, and two more for each level after it. Robots, which shoot back, join from level 2.
- **Survival Mode:** enemies come faster and faster until they get you. Then you can submit your kills to the highscores.
- **Power-ups:** the boost makes you faster for 10 seconds, and the machine gun lets you shoot much faster.

## Highscores

Survival Mode ends with a form that submits your kills to a top 10 in MySQL. Set these environment variables before you start the game:

| Variable | Value |
| --- | --- |
| `FACEBLAST_DB_URL` | The JDBC URL of the database, e.g. `jdbc:mysql://localhost:3306/faceblast` |
| `FACEBLAST_DB_USER` | The database user |
| `FACEBLAST_DB_PASSWORD` | The user's password |

Without `FACEBLAST_DB_URL`, the form says that highscores aren't set up. The MySQL driver is bundled in the jar.

The database needs a `highscore` table with a row for each placement:

```sql
CREATE TABLE highscore (
    Placement INT NOT NULL PRIMARY KEY,
    Name VARCHAR(20) NOT NULL,
    Score INT NOT NULL
);

INSERT INTO highscore (Placement, Name, Score)
VALUES (1, '-', 0), (2, '-', 0), (3, '-', 0), (4, '-', 0), (5, '-', 0),
       (6, '-', 0), (7, '-', 0), (8, '-', 0), (9, '-', 0), (10, '-', 0);
```

A score that beats one of the 10 takes its placement, and the scores below it move down one place. The game only submits scores; it doesn't show the list.

## License

[MIT](LICENSE.txt)
