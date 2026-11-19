---
name: ice-and-fire-build
description: Build the Ice and Fire 1.12.2 mod in this repository with the machine-local Minecraft toolchain, cached dependencies, and the required Java/Gradle versions.
---

# Build workflow

Use this skill when compiling, packaging, or validating this repository's Minecraft 1.12.2 mod. Run commands from the repository root.

The supported local toolchain is under `/opt/minecraft-tools`:

- Gradle: `/opt/minecraft-tools/gradle/gradle-9.6.1/bin/gradle`
- Java: `/opt/minecraft-tools/jdks/jdk-25.0.4.1+1`
- Gradle cache and locally downloaded dependencies: `/opt/minecraft-tools/gradle-home`
- Server-side prerequisite JARs: `/opt/minecraft-tools/mods/`

Run the bundled helper for the normal build:

```bash
.codex/skills/ice-and-fire-build/scripts/build.sh
```

It runs `gradle --no-daemon build` with the local Gradle home and Java 25 toolchain, then prints the generated JAR path. The normal build is offline so it uses the machine-local cache and does not unexpectedly fetch dependencies.

For a compile-only check:

```bash
.codex/skills/ice-and-fire-build/scripts/build.sh compileJava
```

If the offline build reports a missing dependency, populate the local cache through the configured proxy, then rerun offline:

```bash
BUILD_ONLINE=1 .codex/skills/ice-and-fire-build/scripts/build.sh build
```

`BUILD_ONLINE=1` sets both HTTP and HTTPS proxy variables to `192.168.30.118:7890`; dependencies remain in `/opt/minecraft-tools/gradle-home` for subsequent offline builds. Do not change the project's Gradle or Java version unless the build itself requires a repository-level migration.

The standard obfuscated mod artifact is `build/libs/Ice and Fire-<mod_version>.jar`, where `<mod_version>` comes from `gradle.properties`.

After a build, report the task result and artifact path. A successful `build` task is the required validation; there are currently no project tests.
