:<<"::CMDLITERAL"
@ECHO OFF
GOTO :CMDSCRIPT
::CMDLITERAL

java -jar ./linearizer_console/build/libs/Linearizer.jar "$@"
exit $?

:CMDSCRIPT
java -jar ./linearizer_console/build/libs/Linearizer.jar %*
