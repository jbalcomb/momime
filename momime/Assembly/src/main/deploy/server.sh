java -version
MOMIME_UI_SCALE="${MOMIME_UI_SCALE:-2.0}"
java -Xmx2048m -Dsun.java2d.uiScale="$MOMIME_UI_SCALE" -classpath lib/momime-server-${project.version}.jar:$(cat lib/classpaths/server-classpath-unix.txt) momime.server.MomServerKickOff server