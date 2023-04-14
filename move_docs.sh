#!/bin/bash

#
# Copyright (C) 2023 Nicholas Doglio
# SPDX-License-Identifier: MIT
#

set -ex

# Copy in special files that GitHub wants in the project root.
cp CHANGELOG.md docs/CHANGELOG.md
cp .github/CONTRIBUTING.md docs/CONTRIBUTING.md
cp .github/CODE_OF_CONDUCT.md docs/CODE_OF_CONDUCT.md
