##### Git History Linearizer.
linearizer - tool for simplyfying git history.
##### Usage:
`linearizer [options]... <start> <end>`
##### Description:
`-h, --help`  
Show help screen.  
`-l, --linearize`  
Performs linearization between first and last commits, then puts it in a new fork.  
`-s, --squash`  
Performs git squash on several commits.  
`-t, --trim`  
 Trims messages.  
`-f, --fixcase`  
 Fixes cases.  
`-r, --remove`  
 Removes stars and pluses.  
 `-m, --fixbig`  
 Fixes big messages.  
