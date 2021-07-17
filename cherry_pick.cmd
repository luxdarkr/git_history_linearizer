@ %1 - repo path
@ %2 - commit to pick
@ %3 - new message
@ %4 - cherry-pick args
cd %1
git cherry-pick %4 %2
git commit --amend -m %3