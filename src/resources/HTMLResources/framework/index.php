<?php 
session_start();

$_SESSION['CID']          = "";
$_SESSION['IVETTitle']    = "Newton&apos;s Second Law Tutorial 2";
$_SESSION['IVETProject']  = "Interactive Video-Enhanced Tutorials";
$_SESSION['ivet']         = "N2n6";
$_SESSION['school']       = "RIT";
$_SESSION['instructor']   = "Dr. Teese";
$_SESSION['course']       = "University Physics I";
$_SESSION['courseNumber'] = "UP-I Summer 2020";
?>
<!DOCTYPE html >
<html lang="en">	
<head>
        <title>Interactive Video-Enhanced Tutorials</title> 
		<style> html{display : none ; } </style>
		<script>
		   if( self == top ) {
	       document.documentElement.style.display = 'block' ; 
		   } else {
	       top.location = self.location ; 
		   }
		</script>
	<META HTTP-EQUIV="refresh" CONTENT="0;URL=main.php">
</head>
<body>
	<br /><br /><br /><br /><br /><br /><br /><br /><br />
	<center>Loading...</center>
</body>
</head>
