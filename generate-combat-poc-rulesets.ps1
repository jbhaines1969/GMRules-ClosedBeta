param(
    [string]$OutputDirectory = "games\combat-poc-examples",
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
$repoRoot = $PSScriptRoot
Set-Location -LiteralPath $repoRoot

if (-not $SkipBuild) {
    & mvn -q -pl gmrules-combat-poc -am package
    if ($LASTEXITCODE -ne 0) {
        throw "Combat PoC build failed."
    }
}

$pocJar = Join-Path $repoRoot "target\gmrules-combat-poc.jar"
if (-not (Test-Path -LiteralPath $pocJar)) {
    throw "Missing target\gmrules-combat-poc.jar. Run without -SkipBuild first."
}

if ([System.IO.Path]::IsPathRooted($OutputDirectory)) {
    $resolvedOutputDirectory = [System.IO.Path]::GetFullPath($OutputDirectory)
} else {
    $resolvedOutputDirectory = [System.IO.Path]::GetFullPath((Join-Path $repoRoot $OutputDirectory))
}

& java -jar $pocJar --generate-examples $resolvedOutputDirectory
if ($LASTEXITCODE -ne 0) {
    throw "Ruleset generation failed. Existing files are not overwritten."
}
