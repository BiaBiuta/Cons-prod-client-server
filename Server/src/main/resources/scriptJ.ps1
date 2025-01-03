#$param1 = $args[0] # Nume fisier Java pentru client (de exemplu, Main.java)
#$param2 = $args[1] # Nr. total de threaduri (p) - același pentru toți clienții
#$param3 = $args[2] # Nr. de threaduri cititoare (p_r) - același pentru toți clienții
#$param4 = $args[3] # Nr. de rulări (de obicei 10)
#$param5 = $args[4] # Numele fișierului pentru server (ex: Server.java)
#$param6 = $args[5] # dx
#
## Creăm fișierul CSV pentru salvarea rezultatelor, dacă nu există deja
#if (!(Test-Path "outJ.csv")) {
#    New-Item "outJ.csv" -ItemType File
#    Set-Content "outJ.csv" "Nr client,Nr threaduri,Nr cititoare,Timp executie client,Timp executie server"
#}
#
## Variabila pentru a salva timpii de execuție ai serverului
#$serverDurations = @()
#
## Repetăm rularea pentru numărul de ori specificat
#for ($run = 1; $run -le $param4; $run++) {
#    Write-Host "Rularea numărul $run"
#
#    # Măsurarea timpului de rulare pentru server și clienți
#    $sumaServer = 0
#
#    # Creăm un Job pentru server astfel încât să ruleze în paralel cu clienții
#    $serverJob = Start-Job -ScriptBlock {
#        param($param5, $param6, $param2, $param3)
#
#        # Măsurăm timpul de început pentru server
#        $startTimeServer = Get-Date
#
#        # Pornim serverul
#        Write-Host "Pornirea serverului..."
#        $serverOutput = Start-Process java -ArgumentList "-cp . $param5 $param2 $param3 $param2 $param3" -PassThru
#        $serverOutput.WaitForExit()
#
#        # Măsurăm timpul de sfârșit pentru server
#        $endTimeServer = Get-Date
#        $durationServer = $endTimeServer - $startTimeServer
#        $durationServerInSeconds = $durationServer.TotalSeconds
#        Write-Host "Timpul de execuție al serverului: $durationServerInSeconds secunde"
#
#        return $durationServerInSeconds
#    }
#
#    # Executarea clienților (pentru fiecare client 1-5)
#    for ($i = 1; $i -le 5; $i++) {
#        Write-Host "Pornirea clientului $i"
#        # Executăm același fișier Java pentru fiecare client cu argumente diferite
#        $clientOutput = Start-Process java -ArgumentList "-cp . $param1 $i" -PassThru
#        Write-Host ""
#    }
#
#    # Așteptăm finalizarea serverului
#    $serverDuration = Receive-Job -Job $serverJob
#    Remove-Job -Job $serverJob
#
#    Write-Host "Timpul total de execuție al serverului: $serverDuration secunde"
#
#    # Adăugăm timpul de execuție al serverului în lista de timpi
#    $serverDurations += $serverDuration
#
#    # Calculăm timpul mediu pentru clienți
#    $mediaClienti = 0
#    for ($i = 1; $i -le 5; $i++) {
#        # Calculăm timpul de execuție pentru fiecare client
#        # Aceasta presupune că aveți logica pentru a salva sau a calcula durata clientului
#        # Puteți înlocui cu măsurarea reală a duratei pentru fiecare client
#        $clientDuration = Get-Random -Minimum 1 -Maximum 5 # Exemplu de durată aleatorie pentru client
#        $mediaClienti += $clientDuration
#    }
#    $mediaClienti /= 5
#    Write-Host "Timpul mediu de execuție pentru clienți: $mediaClienti secunde"
#
#    # Adăugăm timpul mediu al clientului și timpul serverului în fișierul CSV
#    Add-Content "outJ.csv" "$run, $param2, $param3, $mediaClienti, $serverDuration"
#
#    Write-Host "-----------------------------"
#}
#
## Calculăm media timpului de execuție al serverului pentru 10 rulari
#$averageServerDuration = ($serverDurations | Measure-Object -Average).Average
#Write-Host "Timpul mediu de execuție al serverului după $param4 rulări: $averageServerDuration secunde"
#
## Salvăm media timpului serverului în fișierul CSV
#Add-Content "outJ.csv" "Timpul mediu de execuție al serverului pentru $param4 rulări: $averageServerDuration secunde"
#
#Write-Host "Scriptul s-a terminat."
