# Register with AffordMed
$registrationUrl = "http://4.224.186.213/evaluation-service/register"

$registrationBody = @{
    email = "manikumarreddyalla56@gmail.com"
    name = "Mani Kumar Reddy Alla"
    mobileNo = "9999999999"
    githubUsername = "manikumarreddyalla"
    rollNo = "VTU29659"
    accessCode = "juFphv"
} | ConvertTo-Json

Write-Host "Registering with AffordMed..."
$registrationResponse = Invoke-RestMethod -Uri $registrationUrl -Method POST -Body $registrationBody -ContentType "application/json"
Write-Host "Registration Response:" 
$registrationResponse | ConvertTo-Json

# Extract credentials
$clientId = $registrationResponse.clientID
$clientSecret = $registrationResponse.clientSecret

Write-Host "`nClientID: $clientId"
Write-Host "ClientSecret: $clientSecret"

# Now get the Authorization Token
$authUrl = "http://4.224.186.213/evaluation-service/auth"

$authBody = @{
    email = "manikumarreddyalla56@gmail.com"
    name = "Mani Kumar Reddy Alla"
    rollNo = "VTU29659"
    accessCode = "juFphv"
    clientID = $clientId
    clientSecret = $clientSecret
} | ConvertTo-Json

Write-Host "`nFetching Authorization Token..."
$authResponse = Invoke-RestMethod -Uri $authUrl -Method POST -Body $authBody -ContentType "application/json"
Write-Host "Auth Response:"
$authResponse | ConvertTo-Json

$accessToken = $authResponse.access_token
$tokenType = $authResponse.token_type

Write-Host "`nToken Type: $tokenType"
Write-Host "Access Token: $($accessToken.Substring(0, 50))..."
Write-Host "`nAuthorization Header: $tokenType $accessToken"

# Save to file for later use
$credentials = @{
    clientID = $clientId
    clientSecret = $clientSecret
    accessToken = $accessToken
    tokenType = $tokenType
} | ConvertTo-Json

$credentials | Out-File "c:\Users\allas\Downloads\vehicle-maintenance-scheduler\credentials.json" -Encoding UTF8
Write-Host "`nCredentials saved to credentials.json"
