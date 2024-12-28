$param1 = $args[0] # Nume fisier Java (de exemplu, Main.java)
$param6 = $args[1] # Nr. total de threaduri (p)
$param2 = $args[2] # Nr. de threaduri cititoare (p_r)
$param3 = $args[3] # Nr. de rulări (de obicei 10)

# Măsurarea timpului de rulare
$suma = 0

for ($i = 0; $i -lt $param3; $i++) {
    Write-Host "Rulare" ($i + 1)

    # Măsurăm timpul de început
    $startTime = Get-Date

    # Executăm programul Java cu argumentele necesare
    $output = java -cp . $param1 $param6 $param2 $param3 

    # Măsurăm timpul de sfârșit
    $endTime = Get-Date

    # Calculăm durata execuției
    $duration = $endTime - $startTime
    $durationInSeconds = $duration.TotalSeconds
    Write-Host "Timpul de execuție al rundei $($i + 1): $durationInSeconds secunde"

    # Adăugăm timpul la sumă pentru calculul mediei
    $suma += $durationInSeconds

    # Comparăm fișierele de ieșire dacă este cazul
    # Poți adăuga aici logica de comparare a fișierelor dacă este necesar

    Write-Host ""
}

# Calculăm media timpului de execuție
$media = $suma / $param3
Write-Host "Timpul mediu de execuție după $param3 rulări: $media secunde"

# Crearea fișierului CSV pentru salvarea rezultatelor
if (!(Test-Path "outJ.csv")) {
    New-Item "outJ.csv" -ItemType File
    Set-Content "outJ.csv" "Nr threaduri,Nr cititoare,Timp executie"
}

# Adăugăm timpul mediu în fișierul CSV
Add-Content "outJ.csv" "$param6,$param2,$media"
