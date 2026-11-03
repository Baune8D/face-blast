# face-blast

A Swing game written at school in 2012, built with Maven into a runnable jar.

- `src/main/java`: every class is in the default package; `AGameFrame` is the main class. The images are in `src/main/resources/resource` and loaded with `getResource("/resource/...")`.
- Game state lives in static fields (`AGameFrame`, `Entities`, `Controls`), and `javax.swing.Timer`s drive the game, so everything runs on the event dispatch thread. Use Swing timers for new timed events. Only `PanelHighscore` leaves the event dispatch thread, with a `SwingWorker` for the database.
- `PanelHighscore`: the Survival Mode highscores in MySQL, configured with `FACEBLAST_DB_URL`, `FACEBLAST_DB_USER` and `FACEBLAST_DB_PASSWORD`. The README has the table schema.
- Keep the 2012 code as it is: fix bugs, but don't restructure it.
- The README documents the controls, the modes and the highscore setup. Keep it in step with the code.

## Workflow

- Conventional commits (`feat:`, `fix:`, `chore(deps):`, `ci:`, `build:`, `docs:`), one commit per concern, directly on `main`.
- Only push, tag, publish or edit GitHub releases when asked.
- Commit messages must be accurate. If one turns out to be wrong before it is pushed, reword it (replay the commits and check that the tree is unchanged). Never rewrite pushed history.

## Dependencies

- The code targets Java 21 (`maven.compiler.release`). Every plugin version is pinned in `pluginManagement`.
- MySQL Connector/J is the only dependency. The shade plugin bundles it into `target/face-blast.jar`, without its Protocol Buffers dependency, which only the X DevAPI uses.
- The Maven wrapper (`only-script`, no jar) downloads Maven and checks its SHA-256. To upgrade Maven, verify the new `apache-maven-<version>-bin.zip` against the `.sha512` on Maven Central, then run `./mvnw -N org.apache.maven.plugins:maven-wrapper-plugin:3.3.4:wrapper -Dtype=only-script -Dmaven=<version> -DdistributionSha256Sum=<sha256 of the zip>`.
- Dependabot updates the Maven dependencies and plugins and the GitHub Actions weekly on Monday with a 7-day cooldown, in grouped pull requests. Major versions are upgraded by hand. Connector/J's versions follow the year and month (26.7, 26.10), so its first release each year counts as a major.

## Testing

- There are no automated tests. CI runs `./mvnw -B verify -Dmaven.compiler.failOnWarning=true` on JDK 21; run it before committing, with `JAVA_HOME=/opt/homebrew/opt/openjdk@21` (the default JDK here is newer). Local builds only show the compiler warnings, so a newer JDK with new lint checks doesn't break them.
- Use a scratch `MAVEN_USER_HOME` and `-Dmaven.repo.local`, so the build doesn't write to `~/.m2`.
- To check a change, run `java -jar target/face-blast.jar`. `screencapture` can't capture the screen from here, so to see the window, render the frame's content pane into a `BufferedImage` from a small harness on the classpath.
- To test the highscores, start `mysql:8.4` in Docker (OrbStack is installed), create the table from the README and run the game with the three variables set.
