#!/usr/bin/env bash
# build-all.sh - Build all MoM IME Maven projects in dependency order.
# Usage: ./build-all.sh [-DskipTests] [other Maven args...]
#
# All arguments are passed through to each mvn invocation.
# Stops on the first failure.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MVN="$SCRIPT_DIR/mvnw"

# Require JDK 17+. Auto-detect Adoptium installs on Windows if needed.
find_adoptium_jdk() {
    for jdk in "/c/Program Files/Eclipse Adoptium"/jdk-*; do
        if [ -d "$jdk" ]; then
            echo "$jdk"
            return
        fi
    done
}

java_major_version() {
    "$1/bin/java" -version 2>&1 | head -1 | sed -E 's/.*"([0-9]+).*/\1/'
}

if [ -n "${JAVA_HOME:-}" ]; then
    ver=$(java_major_version "$JAVA_HOME" 2>/dev/null || echo 0)
    if [ "$ver" -lt 17 ]; then
        echo "JAVA_HOME ($JAVA_HOME) is JDK $ver — need 17+, searching for newer JDK..."
        newer=$(find_adoptium_jdk)
        if [ -n "$newer" ]; then
            export JAVA_HOME="$newer"
        else
            echo "ERROR: No JDK 17+ found. Install one and set JAVA_HOME." >&2
            exit 1
        fi
    fi
else
    jdk=$(find_adoptium_jdk)
    if [ -n "$jdk" ]; then
        export JAVA_HOME="$jdk"
    else
        echo "ERROR: JAVA_HOME not set and no JDK found. Install a JDK 17+ and set JAVA_HOME." >&2
        exit 1
    fi
fi
echo "Using JAVA_HOME: $JAVA_HOME"

# Projects in strict dependency order
PROJECTS=(
    parent          # POM only - all others depend on this
    utils           # ndgutils - depends on parent
    map             # ndgmap   - depends on parent, ndgutils
    audio           # ndgaudio - depends on parent, ndgutils
    xmleditor       # xmleditor - depends on parent, ndgutils
    multiplayer     # multi-module reactor - depends on parent, ndgutils
    momime          # multi-module reactor - depends on all above
)

FAILED=0

for project in "${PROJECTS[@]}"; do
    dir="$SCRIPT_DIR/$project"
    if [ ! -d "$dir" ]; then
        echo "ERROR: Directory not found: $dir" >&2
        exit 1
    fi
    echo ""
    echo "========================================"
    echo "  Building: $project"
    echo "========================================"
    if (cd "$dir" && "$MVN" install "$@"); then
        echo "  $project: OK"
    else
        echo "  $project: FAILED" >&2
        exit 1
    fi
done

echo ""
echo "========================================"
echo "  All projects built successfully!"
echo "========================================"

# ------------------------------------------------------------------
# Sync freshly built momime-*.jar files into each extracted deploy
# folder (momime/Assembly/target/*/lib). The assembly plugin creates
# the distribution zip but does NOT refresh the already-extracted
# folder, so run.ps1 would otherwise keep launching stale JARs.
# ------------------------------------------------------------------
DEPLOY_BASE="$SCRIPT_DIR/momime/Assembly/target"
synced_any=0

for deploy in "$DEPLOY_BASE"/*/; do
    lib="${deploy}lib"
    [ -d "$lib" ] || continue
    echo ""
    echo "Syncing JARs into: $deploy"
    for jar in "$lib"/momime-*.jar; do
        [ -f "$jar" ] || continue
        name=$(basename "$jar")
        src=$(find "$SCRIPT_DIR/momime" -mindepth 3 -maxdepth 3 -type f -name "$name" -path '*/target/*' -not -path '*/Assembly/target/*' | head -1)
        if [ -n "$src" ] && [ "$src" -nt "$jar" ]; then
            cp -p "$src" "$jar"
            echo "  Updated: $name"
            synced_any=1
        fi
    done
done

if [ "$synced_any" -eq 0 ]; then
    echo ""
    echo "  (no JARs needed updating)"
fi
