# TODO_feedback

Goal: add closed-beta in-app feedback and bug reporting without sending private draft/ruleset content to Discord by default.

## Privacy Boundary

- Feedback reports should include account and debugging metadata.
- Feedback reports should not include full draft files, ruleset text, exports, or generated character files by default.
- User-created content storage should remain separate from account state, NDA audit logs, and feedback intake.
- Discord is for triage and follow-up, not long-term private content storage.

## In-App Feedback Features

- [ ] Decide the first beta report types: feedback, bug report, and blocker/crash.
- [ ] Add a persistent in-app report action that is easy to reach from builder/account screens.
- [ ] Build a report modal or panel with:
  - [ ] Report type selector.
  - [ ] Severity selector.
  - [ ] Short title field.
  - [ ] Message/details field.
  - [ ] Optional reproduction steps field for bug/blocker reports.
  - [ ] Clear submit/cancel states.
- [ ] Capture safe metadata automatically:
  - [ ] Account email or account id.
  - [ ] Current route/page.
  - [ ] Current builder stage, if available.
  - [ ] Draft id or filename reference, if available.
  - [ ] Browser user agent.
  - [ ] Client timestamp.
  - [ ] Server timestamp.
- [ ] Add a server endpoint for report submission.
- [ ] Validate report payloads server-side.
- [ ] Add size limits so feedback cannot become a large content upload path.
- [ ] Save submitted reports locally before Discord integration, using a private server-side feedback storage path.
- [ ] Return clear success/failure messages in the UI.
- [ ] Add basic rate limiting for report submission.
- [ ] Add a manual smoke test for submitting each report type.

## Discord Integration

- [ ] Create private Discord channels for beta intake:
  - [ ] Feedback.
  - [ ] Bugs.
  - [ ] Blockers/crashes.
- [ ] Create one Discord webhook per channel.
- [ ] Add webhook placeholders to `.env.example`.
- [ ] Add real webhook URLs to production `.env` only.
- [ ] Add server config loading for:
  - [ ] `GMRULES_DISCORD_FEEDBACKWEBHOOKURL`
  - [ ] `GMRULES_DISCORD_BUGWEBHOOKURL`
  - [ ] `GMRULES_DISCORD_BLOCKERWEBHOOKURL`
- [ ] Build a Discord delivery service.
- [ ] Format Discord messages with:
  - [ ] Report type.
  - [ ] Severity.
  - [ ] Title.
  - [ ] Account email/id.
  - [ ] Route/page/stage.
  - [ ] Draft reference only, not full content.
  - [ ] Browser/user agent.
  - [ ] Timestamp.
  - [ ] User message.
- [ ] Store the report locally even if Discord delivery fails.
- [ ] Surface Discord delivery failure in server logs without exposing webhook URLs.
- [ ] Add a manual smoke test for each webhook.

## Later

- [ ] Add admin-only report viewing if Discord triage becomes insufficient.
- [ ] Add report statuses such as new, acknowledged, fixed, deferred, and needs follow-up.
- [ ] Add optional attachment/export upload only after private encrypted content storage is designed.
