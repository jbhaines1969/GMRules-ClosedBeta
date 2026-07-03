# Agent Instructions

## Start Here

Before making changes, read these root files:

- `AGENT_HANDOFF.md`
- `PROJECT_NOTES.md`
- `TODO.md`
- `PROJECT_STRUCTURE.md`
- `USER.md` if it exists locally. It is ignored by Git and contains user-specific collaboration preferences.

Treat `AGENT_HANDOFF.md` as the primary recovery file for any new Codex/ChatGPT agent joining mid-stream.

## Current Product Focus

- This repo is the closed-beta deploy release for the web-delivered GMRules proof of concept.
- Work web-first unless the user explicitly asks for Swing or core-only changes.
- The hosted product runs from `gmrules-builder` through `com.gamemaker.gmrules.web.WebMain`.
- The richer `gmrules-character` project is currently outside this repo and should be treated as a reference unless the user asks to integrate it.

## Operating Rules

- Keep edits scoped to the user's request.
- When giving commands for the Digital Ocean web console, assume the user is already logged in as `root`; do not add `sudo` unless the user says they are using a non-root shell.
- Update `PROJECT_STRUCTURE.md` whenever adding, deleting, or moving tracked files.
- Update `AGENT_HANDOFF.md` when status, launch priorities, deployment assumptions, active blockers, or the recommended next starting point changes. At end of session, make sure it points to where the next agent should resume, not just where this session began.
- Keep `PROJECT_NOTES.md` and `TODO.md` concise and current; do not append noisy historical logs.
- Do not commit, push, reset, or revert unless the user explicitly asks.
- Do not overwrite user/runtime data in `server-data/`, `drafts/`, `.env`, or `.gmrf` files.
- Preserve the repository's non-null Java convention: normalize external absent values immediately; prefer empty strings/collections over null fields.
- Use `rg` / `rg --files` for searching.
- Use `apply_patch` for manual file edits.

## Build And Verification

Use Maven from the repo root:

```powershell
mvn test
mvn package
```

Notes:

- `mvn test` currently compiles the reactor but there are no automated test sources.
- `mvn package` creates the deployable shaded jar and copies it to `target/gmrules-app.jar`.
- `deploy.sh` currently runs `mvn -q -DskipTests compile`; confirm/update it before depending on packaged jar deployment.

## Web Runtime

Important configuration keys are read as Java system properties or environment variables:

- `GMRULES_WEB_HOST`
- `GMRULES_WEB_PORT`
- `GMRULES_WEB_THREADS`
- `GMRULES_WEB_MAXUPLOADBYTES`
- `GMRULES_WEB_SESSIONMINUTES`
- `GMRULES_WEB_DRAFTSDIR`
- `GMRULES_WEB_ACCOUNTSFILE`
- `GMRULES_WEB_NDAAUDITDIR`
- `GMRULES_WEB_FEEDBACKDIR`
- `GMRULES_WEB_PUBLICBASEURL`
- `GMRULES_EMAIL_API_URL`
- `GMRULES_EMAIL_API_KEY`
- `GMRULES_EMAIL_FROM`
- `GMRULES_NDAAUDIT_EMAILTO`
- `GMRULES_DISCORD_FEEDBACKWEBHOOKURL`
- `GMRULES_DISCORD_BUGWEBHOOKURL`
- `GMRULES_DISCORD_BLOCKERWEBHOOKURL`

## Core Architecture Pointers

- Source of truth: `gmrules-core/src/main/java/com/gamemaker/gmrules/Game.java`
- Persistence: `gmrules-core/src/main/java/com/gamemaker/gmrules/GameIO.java`
- Server account storage: `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/AccountStore.java`
- Server draft storage: `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/DraftStore.java`
- Web API: `gmrules-builder/src/main/java/com/gamemaker/gmrules/web/ApiRoutes.java`
- Static web app: `gmrules-builder/src/main/resources/web/app.js`
- Strings: `gmrules-builder/src/main/resources/i18n/strings.properties`

## Beta Launch Priorities

Follow `TODO.md` order unless the user redirects:

1. Add minimum smoke tests for auth, drafts, and export.
2. Fix remaining beta-facing UI polish.
3. Launch to a small controlled cohort.
