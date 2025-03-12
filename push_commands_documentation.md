# Git Push Commands Documentation

This document outlines the common Git push commands used in the IndiCab project.

## Basic Push Commands

1. Push to current branch:
```bash
git push origin HEAD
```

2. Push to specific branch:
```bash
git push origin branch-name
```

3. Push all branches:
```bash
git push --all origin
```

## Feature Branch Workflow

1. Create and push new feature branch:
```bash
git checkout -b feature/new-feature
git push -u origin feature/new-feature
```

2. Update feature branch with main:
```bash
git checkout feature/new-feature
git fetch origin
git merge origin/main
git push origin feature/new-feature
```

## Release Process

1. Create and push release branch:
```bash
git checkout -b release/v1.0.0
git push -u origin release/v1.0.0
```

2. Tag and push release:
```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

## Best Practices

1. Always pull before pushing:
```bash
git pull origin branch-name
```

2. Force push with lease (safer than force push):
```bash
git push --force-with-lease origin branch-name
```

3. Verify remote before pushing:
```bash
git remote -v
```

## Submodule Updates

1. Push submodule changes:
```bash
git submodule update --remote
git add .
git commit -m "Update submodules"
git push origin main
```

## Troubleshooting

If push fails:
1. Check remote connection
2. Verify branch existence
3. Ensure local branch is up to date
4. Check for authentication issues

For authentication issues:
```bash
git config --global credential.helper cache
