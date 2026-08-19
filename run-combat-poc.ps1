param(
    [string]$RulesetFile = "",
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

$javaArguments = @("-jar", $pocJar)
if (-not [string]::IsNullOrWhiteSpace($RulesetFile)) {
    $resolvedRuleset = (Resolve-Path -LiteralPath $RulesetFile).Path
    $javaArguments += $resolvedRuleset
}

& java @javaArguments
