# run.ps1 - Start MoM IME Server then Client
# Run from the workspace root: .\run.ps1
# Optional: pass -Version to override the detected version, e.g. .\run.ps1 -Version 1.7.0

param(
    [string]$Version = "",
    [int]$ServerWaitSeconds = 15,
    [double]$UiScale = 1.5
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# ------------------------------------------------------------------
# Locate the assembled deploy folder under momime/Assembly/target
# ------------------------------------------------------------------
$assemblyTarget = Join-Path $PSScriptRoot "momime\Assembly\target"

if ($Version -eq "") {
    # Pick the most recently modified expanded assembly folder
    $deployDir = Get-ChildItem -Path $assemblyTarget -Directory -Filter "momime-*" |
        Where-Object { Test-Path (Join-Path $_.FullName "server.bat") } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1 -ExpandProperty FullName

    if (-not $deployDir) {
        Write-Error "No assembled deploy folder found under $assemblyTarget. Run 'Build: ALL' first."
        exit 1
    }
} else {
    $deployDir = Join-Path $assemblyTarget "momime-$Version"
    if (-not (Test-Path $deployDir)) {
        Write-Error "Deploy folder not found: $deployDir"
        exit 1
    }
}

Write-Host "Using deploy folder: $deployDir" -ForegroundColor Cyan

# ------------------------------------------------------------------
# Resolve java (bundled JRE wins over PATH)
# ------------------------------------------------------------------
$javaExe = "java"
$bundledJava = Join-Path $deployDir "jre\bin\java.exe"
if (Test-Path $bundledJava) {
    $javaExe = $bundledJava
    Write-Host "Using bundled JRE: $javaExe" -ForegroundColor DarkGray
}

# ------------------------------------------------------------------
# Build classpaths (replicates what the .bat files do)
# ------------------------------------------------------------------
function Get-CP($classpathFile, $jarFile) {
    $cp = Get-Content $classpathFile -Raw -ErrorAction Stop
    $cp = $cp.Trim()
    return "$jarFile;$cp"
}

$version = (Split-Path $deployDir -Leaf) -replace "^momime-", ""
$serverJar  = Join-Path $deployDir "lib\momime-server-$version.jar"
$clientJar  = Join-Path $deployDir "lib\momime-client-$version.jar"
$serverCPFile = Join-Path $deployDir "lib\classpaths\server-classpath-windows.txt"
$clientCPFile = Join-Path $deployDir "lib\classpaths\client-classpath-windows.txt"
$clientMods   = Join-Path $deployDir "client\mods\*"

$serverCP = Get-CP $serverCPFile $serverJar
$clientCP = "$clientJar;$(Get-Content $clientCPFile -Raw -ErrorAction Stop | ForEach-Object { $_.Trim() });$clientMods"

# ------------------------------------------------------------------
# Start Server in a new console window
# ------------------------------------------------------------------
Write-Host "Starting server..." -ForegroundColor Green
$serverArgs = "-Xmx2048m -Dsun.java2d.uiScale=$UiScale -classpath `"$serverCP`" momime.server.MomServerKickOff server"
$serverProcess = Start-Process -FilePath $javaExe `
    -ArgumentList $serverArgs `
    -WorkingDirectory $deployDir `
    -PassThru `
    -WindowStyle Normal

Write-Host "Server PID $($serverProcess.Id). Waiting $ServerWaitSeconds seconds for it to initialise..." -ForegroundColor DarkGray
Start-Sleep -Seconds $ServerWaitSeconds

if ($serverProcess.HasExited) {
    Write-Error "Server process exited prematurely (exit code $($serverProcess.ExitCode))."
    exit 1
}

# ------------------------------------------------------------------
# Start Client in a new console window
# ------------------------------------------------------------------
Write-Host "Starting client..." -ForegroundColor Green
$clientArgs = "-Xmx2048m -Dsun.java2d.uiScale=$UiScale -classpath `"$clientCP`" momime.client.MomClientKickOff client"
$clientLog    = Join-Path $PSScriptRoot "client.log"
$clientErrLog = Join-Path $PSScriptRoot "client.err.log"
Write-Host "Client output -> $clientLog" -ForegroundColor DarkGray
$clientProcess = Start-Process -FilePath $javaExe `
    -ArgumentList $clientArgs `
    -WorkingDirectory $deployDir `
    -PassThru `
    -WindowStyle Normal `
    -RedirectStandardOutput $clientLog `
    -RedirectStandardError  $clientErrLog

Write-Host "Client PID $($clientProcess.Id) launched." -ForegroundColor Green
Write-Host ""
Write-Host "Press Enter to stop both processes..." -ForegroundColor Yellow
$null = Read-Host

# ------------------------------------------------------------------
# Cleanup
# ------------------------------------------------------------------
foreach ($proc in @($clientProcess, $serverProcess)) {
    if (-not $proc.HasExited) {
        Write-Host "Stopping PID $($proc.Id)..." -ForegroundColor DarkGray
        $proc.Kill()
    }
}
Write-Host "Done." -ForegroundColor Cyan
