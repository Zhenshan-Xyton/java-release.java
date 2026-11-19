# java-release

B2 fixture: **release**. Original source bytes are preserved.

- Source repository: `Xyton-semi/Xycode`
- Source revision: `83204a74ee270cc2f000016f548552ab83563219`
- Original source path: `hardening/b2_stress_fixtures/java/release.java`
- Source SHA-256: `14d9162169c97f4be2bd3b9dccee0b898b1b15dfce4085b613a3e478a7fe3661`
- Source bytes: 5755

## Build

Use a selected delivered JDK supporting Java 17:

```sh
sh build.sh
```

Set `JAVAC` to the governed compiler path when required. The library-style class compiles into `target/classes`; no main/harness is added. JDK APIs only, including java.sql; no third-party dependency. Do not execute the OPEN/CLOSED example methods as a smoke test. The original B2 corpus defines R1-observable syntax, not a guarantee of full producer support or semantic qualification.

## Packaging additions

README.md records provenance; build.sh declares compilation; .gitignore excludes build output; .gitattributes preserves Java bytes across checkout platforms. Source code is unchanged.

The minimal `Makefile` exposes the same direct `javac --release 17` command to generic frontend build discovery. Use `make` with the delivered JDK on PATH.
