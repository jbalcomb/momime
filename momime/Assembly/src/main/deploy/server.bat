title MoM IME Server ${project.version}

set javapath=
if exist jre\bin\java.exe set javapath=jre\bin\

for /F %%a in (lib\classpaths\server-classpath-windows.txt) do set libcp=%%a
%javapath%java -version
if "%MOMIME_UI_SCALE%"=="" set MOMIME_UI_SCALE=2.0
%javapath%java -Xmx2048m -Dsun.java2d.uiScale=%MOMIME_UI_SCALE% -classpath lib\momime-server-${project.version}.jar;%libcp% momime.server.MomServerKickOff server