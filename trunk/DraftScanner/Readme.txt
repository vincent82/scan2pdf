In ambiente Windows:

Prima di lanciare l'applicativo assicurarsi che sia installata la libreria jtwain nel percorso C:\Windows\system32\AspriseJTwain.dll

Se così non fosse copiare il file dll (presente nella cartella ./lib) nell'apposita directory.


# Per la firma del jar
keytool -genkey -alias hitech14_2 -keypass hitech -keystore hitech -storepass hitech

jarsigner -keystore hitech -storepass hitech -keypass hitech DraftScanner.jar hitech14_2