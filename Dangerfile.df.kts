/*
 * Copyright (C) 2025 Nicholas Doglio
 * SPDX-License-Identifier: MIT
 */

import systems.danger.kotlin.danger
import systems.danger.kotlin.onGitHub
import systems.danger.kotlin.warn

danger(args) {
    val allSourceFiles = git.modifiedFiles + git.createdFiles
    val sourceChanges = allSourceFiles.filter { path -> path.contains("src") }

    onGitHub {

        // Ensure all major changes are mentioned in changelog
        if (!allSourceFiles.contains("CHANGELOG.md") && sourceChanges.isNotEmpty()) {
            warn(
                "Any major changes should be added to our [`CHANGELOG.md`](CHANGELOG.md) file\n" +
                    "Changelog format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)."
            )
        }

        // Ensure all PRs are small and easy to review
        if ((pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0) > 300) {
            warn(
                "This is a large pull request, can we break it down into multiple smaller PRs? " +
                    "Considering [stacking](https://www.stacking.dev/) them if that helps"
            )
        }
    }
}
