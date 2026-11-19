# Security policy

## Reporting a vulnerability

Please do not report security problems in public issues, pull requests or
discussions.

Report privately through GitHub: open the **Security** tab of this repository
and choose **Report a vulnerability**. If you cannot use GitHub, write to
<dev-support@openfyde.io> with "fyDroid-RT security" in the subject.

Include what you can of the following:

- the lunch target, build and host system you tested on;
- the component affected and how the problem can be triggered;
- what an attacker gains, for example escaping the container or reading
  another app's data;
- a proof of concept, logs or a suggested fix.

## What to expect

- We acknowledge a report within 5 working days.
- We tell you whether we accept it and how we plan to fix it, and keep you
  informed until it is resolved.
- We credit you in the fix unless you ask us not to.
- Please give us reasonable time to release a fix before you publish details.

## Scope

In scope: code in this repository, including the patches it applies to AOSP
and the container's interfaces to the host.

Out of scope: vulnerabilities in upstream AOSP, the Linux kernel, Mesa or
other third-party projects that fyDroid-RT does not modify. Report those
upstream. If you are unsure, report to us and we will route it.

## Supported versions

Security fixes go to the `main` branch.
