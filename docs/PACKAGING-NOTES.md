# Packaging notes

This folder was prepared from copies of the `Base` Eclipse project and the matching files in Downloads. The originals were left in place. No Git repository was initialized, and no commit, push, or GitHub publication was performed.

## Included material

- All 37 Java source files, with their original package structure.
- The JavaFX logo and chart stylesheet in `src/FX/`, beside the classes that load them.
- All three SQL files from `sqlschemas.zip`, organized by purpose.
- The original project report and ER diagram, with descriptive filenames.
- A PNG rendering of the ER diagram for inline README display.
- Dashboard and reports screenshots captured from the existing application connected to the local database.
- Eclipse project metadata with a portable JDBC JAR reference.

## Changes in this copy

1. `src/DAO/DBConnection.java`: replaced the hardcoded database password with `YOUR_MYSQL_PASSWORD`. The original connection code, URL, and username remain otherwise unchanged. Configure a local password before running; keep it out of future public commits.
2. `.classpath`: changed the machine-specific MySQL JAR path to `lib/mysql-connector-j.jar`. The `JavaFX21` Eclipse user library name is retained.
3. `database/schema.sql`: extracted only the final 17 `CREATE TABLE` statements from the original schema file, with `USE grandpharm` at the top. Table definitions and constraints are unchanged. Earlier revisions, `ALTER TABLE` statements, and `DROP TABLE` statements remain in `database/original/createTables.sql`.
4. Added README instructions, ignore rules, and this packaging record.

## Original filenames

| Original file | Packaged location |
| --- | --- |
| `41_1230329_1230517.pdf` | `docs/report/project-report.pdf` |
| `41_ERDIAGRAM.pdf` | `docs/diagrams/er-diagram.pdf` |
| `sqlschemas/createTables.sql` | `database/original/createTables.sql` |
| `sqlschemas/dummydata.sql` | `database/seed/dummydata.sql` |
| `sqlschemas/testing.sql` | `database/queries/testing.sql` |

The older executable JAR and `javacode.zip` in Downloads were omitted because the active Eclipse project is the source for this package. Compiled classes, SDK distributions, IDE caches, and database binaries were omitted as well.

The report and ER diagram describe the original database design; `database/schema.sql` is the reference for exact implemented column names and constraints. The ER diagram represents several many-to-many relationships as relationships; the SQL implements these with junction tables.
