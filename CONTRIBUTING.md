# Contributing to fyDroid-RT

Thank you for helping. This page covers what we need from a contribution so it
can be reviewed and merged quickly.

## Where things go

| You have | Go to |
|----------|-------|
| A bug or a feature request | [openFyde/issues](https://github.com/openFyde/issues), the central tracker for all openFyde projects |
| A security problem | [SECURITY.md](SECURITY.md). Do not open a public issue |
| A code or documentation change | A pull request against `main` in this repository |

## Before you start

- For anything larger than a small fix, open an issue first and describe the
  approach. It saves you from writing code we cannot merge.
- Build and boot the target you changed. [BUILD.md](BUILD.md) has the full
  guide. Say in the pull request which lunch targets you built and what you
  tested on.

## Making changes

**Changes to AOSP projects are patches.** fyDroid-RT does not fork AOSP. A
change to an AOSP project lives as a `git format-patch` file under
`fydroid-rt-common/patches/<project path>/`, numbered in series order. Add a
new patch at the end of the series instead of rewriting earlier ones, unless
you are fixing the patch itself.

**libdrm, minigbm and Mesa** are pinned upstream revisions plus patches under
`fydroid-rt-common/external/`. Edit the patches and the `sync.sh` pin, never
the checked-out project.

**Follow the surrounding style.** Code in this repository follows the AOSP
style for the language in question. Match the file you are editing.

## Commit messages

```
area: short summary in the imperative, 72 characters or fewer

Explain what was wrong and why this change fixes it. Describe behaviour,
not the diff. Wrap at 72 characters.

Signed-off-by: Your Name <you@example.com>
```

`area` is the component you touched, for example `wm`, `SF`, `build`,
`audio` or `fydroid-rt` for changes that span the tree.

## Sign your work

Every commit must carry a `Signed-off-by` line (`git commit -s`). By adding
it you certify the [Developer Certificate of Origin 1.1](https://developercertificate.org/):
that you wrote the change or otherwise have the right to submit it under the
project's license.

Contributions are accepted under the license of the file they change:
Apache License 2.0 for most of the repository, and the upstream license for
patches to third-party projects. See [NOTICE](NOTICE).

## License headers

Every new source file needs a license header. Use the year the file is
created and do not update it later.

Apache 2.0 files (the default), in the comment style of the language:

```java
/*
 * Copyright (C) 2026 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```

Files derived from Chromium or ChromiumOS stay under the BSD license:

```cpp
// Copyright 2026 Fyde Innovations
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE.BSD file.
```

Rules:

- Outside contributors use their own name or their employer's name in the
  copyright line of files they create.
- Never remove or change someone else's copyright line. If you copy or adapt
  a file from another project, keep its header and add yours below it only
  when your changes are substantial.
- Patch files, generated files and binary assets do not get a header.

## Naming

The project is **fyDroid-RT** in documentation and anything a user sees. In
source code, paths and identifiers it is `fydroid-rt`, or `fydroid` for
short. Build variables and lunch targets use `fydroid_rt` and `FYDROID_`,
and class names use `Fydroid`, because those languages do not allow hyphens.

## Review

A maintainer listed in [CODEOWNERS](.github/CODEOWNERS) reviews every pull
request. Expect questions; small, focused pull requests are merged fastest.
