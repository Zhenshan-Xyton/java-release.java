# java-release.java

B2 fixture: **release.java**. The original Java source is unchanged and packaged as an ordinary Maven library under `src/main/java/release.java`.

- Original repository: `Xyton-semi/Xycode`
- Original revision: `83204a74ee270cc2f000016f548552ab83563219`
- Original path: `hardening/b2_stress_fixtures/java/release.java`
- Source SHA-256: `14d9162169c97f4be2bd3b9dccee0b898b1b15dfce4085b613a3e478a7fe3661`
- Source bytes: 5755

## Build

Use the selected Java 17 JDK and run `mvn package`. Compiler source and target are explicitly17; no `--release` override is used. Maven compiler plugin3.13.0 and help plugin3.5.1 are pinned for reproducible generic build discovery. The fixture uses only JDK APIs, including java.sql, and has no third-party application dependency. No main or test harness is added.

The old direct Make/javac packaging was replaced after pinned Frontend produced zero operational coverage. The conventional Maven layout exposes the unchanged source to ordinary Maven build discovery; it does not bypass Frontend or fabricate producer inputs. Do not execute the fixture example methods as a smoke test.
