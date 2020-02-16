<!DOCTYPE html>
<html lang="en">
<head>
    <title> bakeMetrics</title>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; " />
	<meta name="description" content="bakeMetrics is a page for the bake plugin (see https://github.com/Geolykt/bake) to get the most recent version and to track which version is used how often."/>
 	<meta name="keywords" content="Geolykt, Minecraft, Bake, Metrics, why are you here">
 	<meta name="author" content="Geolykt">
</head>
<body><p>$1.5.1$1.5.1$<br> Most recent nightly version / Most recent public version <br> To see the version usage use <a href="versions.txt">this file</a></p> 
 <?php
 #handle versiuon submittion
 if (count($_GET) == 0) {
   # user checks metrics
 } else {
   # plugin sends metrics
  if ($_GET["version"] > 1) { #validate the length
    #user is messing around - ignore
  } else {
    echo $_GET["version"];
    #valid

    $array = explode("\n", file_get_contents("./versions.txt"));
    $array[$_GET["version"]]++;
    $data = implode("\n", $array);

    $datafile = fopen("./versions.txt", "r+");
    fwrite($datafile, $data);
    fclose($datafile);
  }
 }
 ?>
</body>
