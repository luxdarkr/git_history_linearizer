@ %1 - repo path
@ %2 - commit to pick
@ %3 - new message
@ %4 %5 - cherry-pick args
cd %1
git cherry-pick %4 %5 %2
git checkout --theirs Cargo.toml
git checkout --theirs src/tcp/mod.rs
git add .
git commit -m "resolve conflict"
git commit --amend -m %3
