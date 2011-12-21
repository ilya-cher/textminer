@echo off
if "%1" == "" goto error
java -cp .\textprocessor.jar ru.spbau.textminer.processor.ProcessOriginalText %1 text.txt
java -Dfile.encoding=cp866 -cp .\textprocessor.jar ru.spbau.textminer.processor.ExecMystem .\mystem.exe text.txt mystem-output.txt temp.txt temp-output.txt
java -cp .\textprocessor.jar ru.spbau.textminer.processor.ProcessMystemOutput mystem-output.txt chunker-input.txt
call bin\opennlp.bat ChunkerME chunker-model\ru-chunker.bin < chunker-input.txt > chunker-output.txt
java -cp .\textprocessor.jar ru.spbau.textminer.processor.ProcessChunkerOutput chunker-output.txt extractor-input.txt
call bin\opennlp.bat ChunkerME extractor-model\rel-extractor.bin < extractor-input.txt > extractor-output.txt
java -cp .\textprocessor.jar ru.spbau.textminer.processor.ProcessExtractorOutput extractor-output.txt result.txt
goto end
:error
echo usage extract.bat filename
:end

