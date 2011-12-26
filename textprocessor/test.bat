@echo off
set res=false
if "%1" == "" set res=true
if "%2" == "" set res=true
if "%res%" == "true" goto error
java -Dfile.encoding=cp866 -cp .\textprocessor.jar ru.spbau.textminer.processor.ComputeAccuracy %1 %2
goto end
:error
echo usage test.bat result-file test-file
:end

