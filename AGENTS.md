# Agent Instructions

## Start Here

Before making changes, read these root files:

- `AGENT_HANDOFF.md`
- `PROJECT_NOTES.md`
- `TODO.md`
- `PROJECT_STRUCTURE.md`
- `PRODUCT_DESIGN_CONTEXT.md` for product-design, UX, workflow, information-hierarchy, or user-facing-copy work.
- `USER.md` if it exists locally. It is ignored by Git and contains user-specific collaboration preferences.

Treat `AGENT_HANDOFF.md` as the primary recovery file for any new Codex/ChatGPT agent joining mid-stream.

## Current Product Focus

- This repo is the closed-beta deploy release for the web-delivered GMRules proof of concept.
- The current partner/funding milestone is the contract-driven
  character-to-combat PoC in `TODO.md`: at least three materially different Game
  files must each generate two combat-ready characters and complete combat through
  core-owned Character Generation, Attack, Damage, Harm, decision, and combat-state
  contracts. Existing consumers are provisional and may be rebuilt as thin
  contract demonstrations.
- That milestone is the first proof of a universal platform boundary, not its
  limit. Every rules domain--eventually including movement, spellcasting, and
  others--must be executable by asking the loaded `Game` through a published,
  versioned contract. Whether core runs in-process, in a background JVM, behind a
  service, or through a language bridge, consumers must not contain game-specific
  formulas. A VTT that implements a compatible contract should run any compatible
  GMRules Game.
- The web application is the only general product UI implementation in this
  repository. Legacy Swing product sources and launchers were removed; do not
  restore them. Purpose-limited thin consumers are allowed for contract proofs.
  Do not create a new general standalone product UI unless the user explicitly
  requests it.
- The hosted product runs from `gmrules-builder` through `com.gamemaker.gmrules.web.WebMain`.
- The richer `gmrules-character` project is currently outside this repo and should be treated as a reference unless the user asks to integrate it.

## Operating Rules

- Keep edits scoped to the user's request.
- If the user's intent is ambiguous, stop and ask for clarification before making the affected change. Do not resolve meaningful ambiguity by assumption.
- Do not use the in-app browser for UI verification in this repository. John performs visual acceptance through the local launcher and screenshots because usability, clarity, and human-friendliness require his review. Continue running applicable code, syntax, build, and non-visual checks before handing UI work back for that smoke.
- Always stop verification servers and their launcher processes before completing a task. Confirm that any ports opened for verification, including local port 8080, are free before handing the task back to John.
- When giving commands for the Digital Ocean web console, assume the user is already logged in as `root`; do not add `sudo` unless the user says they are using a non-root shell.
- Update `PROJECT_STRUCTURE.md` whenever adding, deleting, or moving tracked files.
- Update `AGENT_HANDOFF.md` when status, launch priorities, deployment assumptions, active blockers, or the recommended next starting point changes. At end of session, make sure it points to where the next agent should resume, not just where this session began.
- Keep `PROJECT_NOTES.md` and `TODO.md` concise and current; do not append noisy historical logs.
- Do not commit, push, reset, or revert unless the user explicitly asks.
- Do not overwrite user/runtime data in `server-data/`, `drafts/`, `.env`, or `.gmrf` files.
- Preserve the repository's non-null Java convention: normalize external absent values immediately; prefer empty strings/collections over null fields.
- Use `rg` / `rg --files` for searching.
- Use `apply_patch` for manual file edits.
- Keep agent-created, ad hoc verification tests local. Name them `*LocalTest.java` under a module's `src/test/` tree so the root `.gitignore` excludes them automatically; do not force-add or publish them. This does not apply to permanent test coverage explicitly requested for the repository.

## Build And Verification

Use Maven from the repo root:

```powershell
mvn test
mvn package
```

Notes:

- `mvn test` compiles the reactor, the tracked Attack Resolution audit tests, and
  any ignored `*LocalTest.java` verification tests present in the local workspace.
- `mvn package` creates the deployable shaded jar and copies it to `target/gmrules-app.jar`.
- `deploy.sh` currently runs `mvn -q -DskipTests clean install` for the hosted
  Maven `exec:java` service flow; the shaded jar remains available for a future
  package-based deployment.

## Web Runtime

Important configuration keys are read as Java system properties or environment variables:

- `GMRULES_WEB_HOST`
- `GMRULES_WEB_PORT`
- `GMRULES_WEB_THREADS`
- `GMRULES_WEB_MAXUPLOADBYTES`
- `GMRULES_WEB_SESSIONMINUTES`
- `GMRULES_WEB_LOCALMODE`
- `GMRULES_WEB_LOCALACCESSKEYFILE`
- `GMRULES_WEB_DRAFTSDIR`
- `GMRULES_WEB_ACCOUNTSFILE`
- `GMRULES_WEB_NDAAUDITDIR`
- `GMRULES_WEB_FEEDBACKDIR`
- `GMRULES_WEB_ADMINEMAILS`
- `GMRULES_WEB_BLOCKEDACCESSFILE`
- `GMRULES_WEB_REQUESTLOGDIR`
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

## Current Priority Order

Follow `TODO.md` order unless the user redirects:

1. Select the three demonstration systems and define their complete
   character/combat support matrix.
2. Audit current consumers and settle public core boundaries before expanding UI.
3. Complete combat-ready Character Generation in core.
4. Complete Attack, Damage, Harm, combat-session, decision, and event contracts in
   core.
5. Prove consumer independence with automated and player-input full combats for
   two core-generated characters under each Game.
