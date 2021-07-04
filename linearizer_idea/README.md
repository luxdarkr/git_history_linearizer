Git history linearizer:  
Intellij Idea plugin

This project can be built automatically with Gradle  
command: gradle build  
Gradle creates file "linearizer_idea-X.X.X.zip", it contains plugin code and all needed libraries.  
To build plugin correctly you should set project JDK and Gradle JRE to Java version 11 (as shown on screenshots below).  
You can then install plugin from this file into any IDE (supported versions are 183 - 221).

Linearizer plugin has a set of parameters (tool window is shown below).
- Repository - A path to git repository to work with
- Start - ID of commit to start linearization from it
- Branch name - Name of branch (name of HEAD) to which we should linearize

Plugin also has a set of options to automatically modify commit messages
- Strip commit messages
- Fix case
- Fix bad starts  
  Bad starts patterns also can be modified. The default patterns are "*,+" (asterisk and plus) 
  But you can write your own patterns separated by comma.
  
The "Linearize" button checks validity of all arguments and starts linearizer algorithm.
The status of operation is being displayed at the bottom of tool window.

Plugin works in two modes. First mode is manual. You enter arguments all manually.  
Second mode allows you to fill some arguments automatically. You should go to git log in Intellij idea (git4idea plugin), click on any commit and in context menu select "Linearize from here".  
This action automatically starts linearizer from this commit to the selected head.
