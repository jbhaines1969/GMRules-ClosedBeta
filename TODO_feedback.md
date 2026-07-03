# TODO_feedback

Goal: add closed-beta in-app feedback and bug reporting without sending private draft/ruleset content to Discord by default.

## Privacy Boundary

- Feedback reports should include account and debugging metadata.
- Feedback reports should not include full draft files, ruleset text, exports, or generated character files by default.
- User-created content storage should remain separate from account state, NDA audit logs, and feedback intake.
- Discord is for triage and follow-up, not long-term private content storage.

## In-App Feedback Features

- [x] Decide the first beta report types: feedback, bug report, and blocker/crash.
- [x] Add a persistent in-app report action that is easy to reach from builder/account screens.
- [x] Build a report modal or panel with:
  - [x] Report type selector.
  - [x] Severity selector.
  - [x] Short title field.
  - [x] Message/details field.
  - [x] Optional reproduction steps field for bug/blocker reports.
  - [x] Clear submit/cancel states.
- [x] Capture safe metadata automatically:
  - [x] Account email or account id.
  - [x] Current route/page.
  - [x] Current builder stage, if available.
  - [x] Draft id or filename reference, if available.
  - [x] Browser user agent.
  - [x] Client timestamp.
  - [x] Server timestamp.
- [x] Add a server endpoint for report submission.
- [x] Validate report payloads server-side.
- [x] Add size limits so feedback cannot become a large content upload path.
- [x] Save submitted reports locally before Discord integration, using a private server-side feedback storage path.
- [x] Return clear success/failure messages in the UI.
- [x] Add basic rate limiting for report submission.
- [ ] Add a manual smoke test for submitting each report type.

## Discord Integration

- [x] Create private Discord channels for beta intake:
  - [x] Feedback.
  - [x] Bugs.
  - [x] Blockers/crashes.
- [x] Create one Discord webhook per channel.
- [x] Add webhook placeholders to `.env.example`.
- [x] Add real webhook URLs to production `.env` only.
- [x] Add server config loading for:
  - [x] `GMRULES_DISCORD_FEEDBACKWEBHOOKURL`
  - [x] `GMRULES_DISCORD_BUGWEBHOOKURL`
  - [x] `GMRULES_DISCORD_BLOCKERWEBHOOKURL`
- [x] Build a Discord delivery service.
- [x] Format Discord messages with:
  - [x] Report type.
  - [x] Severity.
  - [x] Title.
  - [x] Account email/id.
  - [x] Route/page/stage.
  - [x] Draft reference only, not full content.
  - [x] Browser/user agent.
  - [x] Timestamp.
  - [x] User message.
- [x] Store the report locally even if Discord delivery fails.
- [x] Surface Discord delivery failure in server logs without exposing webhook URLs.
- [ ] Add a manual smoke test for each webhook.

## Later

- [ ] Add admin-only report viewing if Discord triage becomes insufficient.
- [ ] Add report statuses such as new, acknowledged, fixed, deferred, and needs follow-up.
- [ ] Add optional attachment/export upload only after private encrypted content storage is designed.
