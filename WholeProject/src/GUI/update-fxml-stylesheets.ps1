# PowerShell script to update all FXML files with base-theme.css integration
# This adds base-theme.css, shared-controls.css, and scene-specific CSS in correct order

$fxmlFiles = Get-ChildItem -Path "src\view" -Filter "*.fxml" -Recurse

foreach ($file in $fxmlFiles) {
    Write-Host "Processing: $($file.FullName)"
    
    $content = Get-Content $file.FullName -Raw
    
    # Skip if already has base-theme.css
    if ($content -match "base-theme\.css") {
        Write-Host "  ✓ Already updated"
        continue
    }
    
    # Determine scene-specific CSS based on file path
    $sceneCss = ""
    if ($file.Name -match "Scene(\d+|[A-Z]\w+)\.fxml") {
        $dir = Split-Path -Parent $file.FullName
        $dirName = Split-Path -Leaf $dir
        
        # Map FXML files to their CSS files
        switch ($file.Name) {
            "Scene3.fxml" { $sceneCss = "/view/auth/Style3.css" }
            "SceneProfile.fxml" { $sceneCss = "/view/profile/StyleProfile.css" }
            "SceneChangePassword.fxml" { $sceneCss = "/view/profile/StyleProfileActions.css" }
            "SceneChangeFitnessLevel.fxml" { $sceneCss = "/view/profile/StyleProfileActions.css" }
            "SceneChangeGoal.fxml" { $sceneCss = "/view/profile/StyleProfileActions.css" }
            "SceneGenerateWorkout.fxml" { $sceneCss = "/view/workout/StyleGenerateWorkout.css" }
            "SceneWorkoutDisplay.fxml" { $sceneCss = "/view/workout/StyleWorkoutDisplay.css" }
            "SceneGenerateNutration.fxml" { $sceneCss = "/view/nutrition/StyleGenerateNutration.css" }
            "SceneNutritionDisplay.fxml" { $sceneCss = "/view/nutrition/StyleNutritionDisplay.css" }
            "SceneMealPlanDisplay.fxml" { $sceneCss = "/view/nutrition/StyleMealPlanDisplay.css" }
            "SceneTrackBodyWeight.fxml" { $sceneCss = "/view/tracking/StyleTrackBodyWeight.css" }
            "SceneWeightProgress.fxml" { $sceneCss = "/view/tracking/StyleWeightProgress.css" }
        }
    }
    
    # Build stylesheets block
    $stylesheetsBlock = "    <stylesheets>`n        <URL value=`"@/view/shared/base-theme.css`"/>`n        <URL value=`"@/view/shared/shared-controls.css`"/>"
    if ($sceneCss -ne "") {
        $stylesheetsBlock += "`n        <URL value=`"@$sceneCss`"/>"
    }
    $stylesheetsBlock += "`n    </stylesheets>"
    
    # Replace stylesheets attribute with stylesheets block
    $content = $content -replace 'stylesheets="[^"]*"', ''
    $content = $content -replace '(\<AnchorPane[^>]+)(>)', "`$1>`n$stylesheetsBlock"
    
    Set-Content -Path $file.FullName -Value $content -NoNewline
    Write-Host "  ✓ Updated with base-theme.css"
}

Write-Host "`nAll FXML files updated!"
