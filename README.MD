##### Git History Linearizer.
linearizer - tool for simplyfying git history.
##### Usage:
`linearizer -l <repo_path> <branch> <start> [commit_message_options]`
##### Description:
`-h, --help`  
Shows help screen.  
`-l, --linearize`  
Performs linearization between first and last commits, then puts it in a new fork.   
`-s, --strip`  
 Trims messages (only with linearization).  
`-f, --fixcase`  
 Fixes cases (only with linearization).  
`-r, --badstarts`  
 Removes stars and pluses (only with linearization).  
 `-b, --fixbig`  
 Fixes big messages (only with linearization).  
