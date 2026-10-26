# NEXA Volume TV

Android app with a TV-friendly interface and an optional `LoudnessEnhancer` effect on audio session 0. Android devices may reject or ignore global audio effects, so compatibility and the amount of audible amplification must be tested on the target projector. This app cannot promise unlimited amplification.

## Build

The GitHub Actions workflow builds a debug APK using an Ubuntu runner with Android SDK tooling and uploads it as the `NEXA-Volume-TV-debug` artifact.
