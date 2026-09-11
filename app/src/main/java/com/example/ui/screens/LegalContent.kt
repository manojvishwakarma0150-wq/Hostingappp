package com.example.ui.screens

object LegalContent {
    val TERMS_OF_USE_HTML = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    background-color: #0a0015;
                    color: #e9d5ff;
                    font-family: sans-serif;
                    padding: 20px;
                    line-height: 1.6;
                }
                h1 { color: #a855f7; font-size: 22px; }
                h2 { color: #fbbf24; font-size: 16px; margin-top: 20px; }
                p { color: #9ca3af; font-size: 14px; }
                hr { border: none; height: 1px; background-color: #4c1d95; }
            </style>
        </head>
        <body>
            <h1>VIP Dark Host — Terms of Use</h1>
            <p>Last updated: September 2026</p>
            <hr/>
            <h2>1. Service Overview</h2>
            <p>VIP Dark Host provides isolated container hosting environments for executing user-provided bot scripts in Python. Each container is allocated dedicated vCPU and RAM slices according to subscription tier.</p>
            <h2>2. Acceptable Use Policy</h2>
            <p>Users must not execute malicious code, DDoS scripts, unauthorized crypto mining, or spam bots. Any violation will result in immediate termination of the bot container without notice.</p>
            <h2>3. Uptime & Availability</h2>
            <p>Sessions run for up to 24 hours per cycle and can be extended continuously by resetting the session timer. We endeavor to maintain 99.9% uptime for active containers.</p>
            <h2>4. Limitation of Liability</h2>
            <p>VIP Dark Host is not liable for data loss, API rate limits imposed by third-party bot platforms (such as Telegram or Discord), or network interruptions.</p>
        </body>
        </html>
    """.trimIndent()

    val PRIVACY_POLICY_HTML = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    background-color: #0a0015;
                    color: #e9d5ff;
                    font-family: sans-serif;
                    padding: 20px;
                    line-height: 1.6;
                }
                h1 { color: #a855f7; font-size: 22px; }
                h2 { color: #fbbf24; font-size: 16px; margin-top: 20px; }
                p { color: #9ca3af; font-size: 14px; }
                hr { border: none; height: 1px; background-color: #4c1d95; }
            </style>
        </head>
        <body>
            <h1>VIP Dark Host — Privacy Policy</h1>
            <p>Last updated: September 2026</p>
            <hr/>
            <h2>1. Information We Collect</h2>
            <p>We collect your Google account authentication credentials (User ID, Name, Email, Profile Picture) strictly to provide account access and manage container tenancy.</p>
            <h2>2. Script & Container Data</h2>
            <p>Uploaded Python scripts (bot.py, requirements.txt) and cloned GitHub repository links are stored only within your container instance and are purged upon container destruction.</p>
            <h2>3. Advertising Partners</h2>
            <p>We integrate Google AdMob to deliver rewarded video ads. AdMob may collect anonymous device identifiers and performance telemetry in compliance with Google Play Policies.</p>
            <h2>4. Contact Us</h2>
            <p>For questions regarding your privacy or data removal requests, contact our support team at support@vipdarkhost.com.</p>
        </body>
        </html>
    """.trimIndent()
}
