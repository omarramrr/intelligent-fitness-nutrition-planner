# CSS Variable Refactoring Script
# This script refactors all CSS files to use theme variables

$replacements = @{
    # Background gradients
    'linear-gradient\(to bottom right, #0a1a3c, #112141\)' = 'var(--bg-gradient)'
    
    # Button gradients
    'linear-gradient\(to right, #1a73e8, #4dabff\)' = 'var(--btn-primary-bg)'
    'linear-gradient\(to right, #1669d8, #3ea1ff\)' = 'var(--btn-hover-bg)'
    'linear-gradient\(to right, #0f55b3, #2689e6\)' = 'var(--btn-pressed-bg)'
    
    # Colors
    '#1a73e8' = 'var(--primary-blue)'
    '#4dabff' = 'var(--accent-blue)'
    '#81c3fd' = 'var(--light-accent)'
    '#3ea1ff' = 'var(--hover-blue)'
    '#ffffff(?!")' = 'var(--text-primary)'
    'white(?!")' = 'var(--text-primary)'
    '#cccccc' = 'var(--text-secondary)'
    '#ff4d4d' = 'var(--error-red)'
    '#ff6666' = 'var(--error-hover)'
    '#112141' = 'var(--bg-gradient-end)'
    '#0a1a3c' = 'var(--bg-gradient-start)'
    
    # Common RGBA values
    'rgba\(255, 255, 255, 0\.1\)' = 'var(--input-bg)'
    'rgba\(255, 255, 255, 0\.15\)' = 'var(--input-bg-hover)'
    'rgba\(255, 255, 255, 0\.05\)' = 'var(--card-bg)'
    'rgba\(255, 255, 255, 0\.2\)' = 'var(--border-light)'
    'rgba\(255, 255, 255, 0\.3\)' = 'var(--input-border)'
    'rgba\(255, 255, 255, 0\.5\)' = 'var(--input-border-hover)'
    'rgba\(0, 0, 0, 0\.3\)' = 'var(--scrollbar-track)'
    'rgba\(77, 171, 255, 0\.3\)' = 'var(--border-accent-light)'
    'rgba\(77, 171, 255, 0\.5\)' = 'var(--scrollbar-thumb)'
    'rgba\(255, 255, 255, 0\.6\)' = 'var(--text-muted)'
    
    # Border radius
    '8px' = 'var(--border-radius-sm)'
    '10px' = 'var(--border-radius-md)'
    '12px' = 'var(--border-radius-lg)'
    '15px' = 'var(--border-radius-xl)'
    
    # Shadows
    'dropshadow\(gaussian, rgba\(0, 0, 0, 0\.3\), 10, 0\.3, 0, 2\)' = 'var(--shadow-medium)'
    'dropshadow\(gaussian, rgba\(255, 255, 255, 0\.4\), 15, 0\.4, 0, 0\)' = 'var(--shadow-text)'
    'dropshadow\(gaussian, rgba\(255, 255, 255, 0\.2\), 8, 0\.3, 0, 0\)' = 'var(--shadow-text-subtle)'
}

$cssFiles = Get-ChildItem -Path "src\view" -Filter "*.css" -Recurse | Where-Object { 
    $_.Name -ne "base-theme.css" -and $_.Name -ne "shared-controls.css" -and $_.Name -ne "Style.css"
}

foreach ($file in $cssFiles) {
    Write-Host "Processing: $($file.FullName)"
    
    $content = Get-Content $file.FullName -Raw
    
    foreach ($pattern in $replacements.Keys) {
        $replacement = $replacements[$pattern]
        $content = $content -replace $pattern, $replacement
    }
    
    Set-Content -Path $file.FullName -Value $content -NoNewline
    Write-Host "  ✓ Updated"
}

Write-Host "`nRefactoring complete!"
