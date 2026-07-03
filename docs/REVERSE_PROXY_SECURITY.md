# Reverse Proxy And Security Headers

Updated: 2026-07-03

This document is the intended production front-door checklist for the GMRules Closed Beta. The Java app should remain a private backend service; public HTTPS, redirects, proxy headers, and browser security headers should be handled by the reverse proxy unless the deployment architecture changes.

## Intended Shape

```text
Browser
  -> https://gmrules.com
  -> Nginx reverse proxy
  -> Java app on 127.0.0.1:8080
```

Expected production assumptions to confirm on the Droplet:

- Public domain: `https://gmrules.com`
- Reverse proxy: Nginx
- Java bind: `GMRULES_WEB_HOST=127.0.0.1`
- Java port: `GMRULES_WEB_PORT=8080`
- HTTP port 80 redirects to HTTPS port 443
- Java app is not directly exposed to the public internet

## Required Proxy Headers

Nginx should forward the original host, protocol, and client IP:

```nginx
proxy_set_header Host $host;
proxy_set_header X-Real-IP $remote_addr;
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
proxy_set_header X-Forwarded-Proto $scheme;
```

These matter because the Java app uses forwarded IP headers for audit records, rate limits, feedback metadata, request logs, and IP blocking.

## Baseline Nginx Sketch

This is a guideline, not a confirmed copy of production config:

```nginx
server {
    listen 80;
    server_name gmrules.com www.gmrules.com;
    return 301 https://gmrules.com$request_uri;
}

server {
    listen 443 ssl http2;
    server_name gmrules.com;

    # TLS certificate settings are managed on the server.

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Recommended Security Headers

Add these at the HTTPS server block level after confirming they do not break the app:

```nginx
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
add_header X-Content-Type-Options "nosniff" always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
add_header X-Frame-Options "DENY" always;
add_header Permissions-Policy "camera=(), microphone=(), geolocation=(), payment=()" always;
add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; connect-src 'self'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'" always;
```

Notes:

- `Strict-Transport-Security` should only be enabled after HTTPS is confirmed stable.
- The CSP allows inline script/style because the current web app has inline debug/error handling and inline styles in the health page.
- If external services or assets are added later, update `connect-src`, `img-src`, `script-src`, or `style-src` deliberately.
- `frame-ancestors 'none'` is the CSP equivalent of frame blocking; `X-Frame-Options: DENY` is kept for older browser behavior.

## Verification Commands

From any shell:

```bash
curl -I http://gmrules.com
curl -I https://gmrules.com
curl -I https://gmrules.com/api/health
```

Expected:

- `http://gmrules.com` returns a redirect to `https://gmrules.com`.
- `https://gmrules.com/api/health` returns `200` when the app and runtime storage are healthy.
- HTTPS responses include the security headers above.

Useful focused checks:

```bash
curl -I https://gmrules.com | grep -iE 'strict-transport-security|content-security-policy|x-content-type-options|referrer-policy|permissions-policy|x-frame-options'
curl -s https://gmrules.com/api/health
```

## Do Not Log Or Expose

Do not put these in Nginx config, public headers, error pages, or docs copied to public places:

- Resend API key
- Discord webhook URLs
- `.env` contents
- session bearer tokens
- verification tokens
- account password hashes or salts
- full request bodies
- uploaded `.gmrf` content

## Production Details To Fill In Later

- Nginx site config path:
- TLS certificate provider:
- Service name for the Java app:
- Confirmed redirect behavior:
- Confirmed header set:
- Date last verified:
