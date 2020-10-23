<?php
    session_start();
    
    date_default_timezone_set("America/New_York");

    $servername = '127.0.0.1';
    $username = 'w_ivet';
    $password = 'Livephoto';
    $dbname = 'w_ivet';
/*	
    require('SiteConfigVars.php');
    $servername = getConfigValue('dbHost_w_ivet');
    $username = 'w-ivet';
    $password = getConfigValue('dbPass_w_ivet');
    $dbname = 'w_ivet';
*/

    // Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	}	
	// use mysqli_real_escape_string to stop SQL Injection in POST vars
	
    $created = mysqli_real_escape_string($conn, $_POST['created']); 
    $content = mysqli_real_escape_string($conn, $_POST['content']); 
    $commentsUserName = mysqli_real_escape_string($conn, $_POST['commentsUserName']); 
    $upvoteCount = mysqli_real_escape_string($conn, $_POST['upvoteCount']); 
    $userHasUpvoted = mysqli_real_escape_string($conn, $_POST['userHasUpvoted']); 
    $IVET = mysqli_real_escape_string($conn, $_POST['IVET']); 
    $CID = mysqli_real_escape_string($conn, $_POST['CID']); 
    $pageName = mysqli_real_escape_string($conn, $_POST['pageName']); 
    $firstname = mysqli_real_escape_string($conn, $_POST['firstname']);
    
									
									
    $sql = "INSERT INTO comments (created, content, commentsUserName, upvoteCount, userHasUpvoted, IVET, CID, pageName, firstname) ".
	"VALUES ('$created', '$content', '$commentsUserName', '$upvoteCount', '$userHasUpvoted', '$IVET', '$CID', '$pageName', '$firstname')";
    
    if ($conn->query($sql) === TRUE) {
        //echo " ...New record created successfully in comments... ".$content;
    } else {
        echo "Error: " . $sql . "<br>" . $conn->error;
    }    

	$conn->close();
?>