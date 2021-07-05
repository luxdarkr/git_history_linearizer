# Git history linearizer
Linearizer - tool for simplyfying git history.  

## Command Line Interface
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
Project can be built by using "build gradle" command. In the \git_history_linearizer\linearizer_console\build\libs folder Linearizer.jar will be created. You can run it with "java -jar" command, or by running "Linearizer.cmd" from git_history_linearizer folder.  

When performing linearization (-l). Linearizer receives three arguments: path to the repository, the branch and the starting commit. After those arguments, you can add four flags for fixing commit messages. They allow fixing case, big messages, bad starts and striping messages. If linearization was successful, cmd will print out result commit.  

## Intellij Idea plugin
This project can be built automatically with Gradle  

`gradle build`  
Gradle creates file *"linearizer_idea-X.X.X.zip"*, it contains plugin code and all needed libraries.  
To build plugin correctly you should set project JDK and Gradle JRE to Java version 11 (as shown on screenshots below).  
You can then install plugin from this file into any IDE (supported versions are 183 - 221).  

Linearizer plugin has a set of parameters (tool window is shown below).  
* Repository - A path to git repository to work with
*	Start - ID of commit to start linearization from it
*	Branch name - Name of branch (name of HEAD) to which we should linearize   

Plugin also has a set of options to automatically modify commit messages
*	Strip commit messages
*	Fix case
*	Fix bad starts  

Bad starts patterns also can be modified. The default patterns are "* , +" (asterisk and plus), but you can write your own patterns separated by comma.  

The "Linearize" button checks validity of all arguments and starts linearizer algorithm.  
The status of operation is being displayed at the bottom of tool window.  
Plugin works in two modes. First mode is manual. You enter arguments all manually.
Second mode allows you to fill some arguments automatically. You should go to git log in Intellij idea (git4idea plugin), click on any commit and in context menu select "Linearize from here".  
This action automatically starts linearizer from this commit to the selected head.  

![image](https://user-images.githubusercontent.com/79219538/124463154-237d0f80-dd9b-11eb-9841-8947d6c52eca.png)
![image](https://user-images.githubusercontent.com/79219538/124463671-e6654d00-dd9b-11eb-9eb4-8c439f65bb3e.png)
![image](https://user-images.githubusercontent.com/79219538/124463688-eb2a0100-dd9b-11eb-9644-1d88aa52b4c5.png)

## VSCode plugin  

Plugin is written on JavaScript.  
There are two main commands: "start" and "help".  

**1. Start.**  
 Command **"Start"** opens the main window.  
Main window includes three input fields (Path, Name and Start), one checkbox with special options and two buttons (Linearize and Help).
* Field "Path to repository" is designed to input absolute path to local repository;
* Field "Name of linearized branch" is designed to input name of linearized branch;
* Field "Start point" is designed to input hash of linearization's starting point;
* Checkbox is designed to choose additional options of linearization:
		 - Strip commit messages;  
		 - Fix case;  
		 - Remove extra symbols;  
		 - Fix big messages.  
* Button *"Linearize"* starts linearization;
* Button *"Help"* executes command "Help".  
 
![image](https://user-images.githubusercontent.com/79219538/124465177-c33b9d00-dd9d-11eb-9fc8-a62593f6c195.png)
![image](https://user-images.githubusercontent.com/79219538/124465194-c9317e00-dd9d-11eb-93d7-8f97335afa16.png)  

**2. Help.**  
 Command **"Help"** opens window with information about input parameters.  
 
![image](https://user-images.githubusercontent.com/79219538/124465355-fda53a00-dd9d-11eb-892b-f0815ef1382b.png)


