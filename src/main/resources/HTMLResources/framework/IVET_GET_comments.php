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
 
    $IVET = mysqli_real_escape_string($conn, $_POST['IVET']); 
    $CID = mysqli_real_escape_string($conn, $_POST['CID']); 
    $pageName = mysqli_real_escape_string($conn, $_POST['pageName']); 
    
  
$sql = "SELECT id, created, content, commentsUserName, upvoteCount, userHasUpvoted FROM comments WHERE (ivet = '$IVET' AND pageName = '$pageName')  ORDER BY created DESC LIMIT 15;";
$indexnumber = 0;
$record_result = $conn->query($sql);

echo "[";
while ($row = mysqli_fetch_array($record_result, MYSQLI_BOTH)) :
if($indexnumber > 0) { echo ",";};
$indexnumber++;
echo "{";
echo '"id": '.$row[0].', '."";
echo '"created": "'.$row[1].'", '."";
echo '"content": "'.$row[2].'", '."";
echo '"fullname": "'.$row[3].'", '."";
echo '"upvote_count": '.$row[4].', '."";
echo '"user_has_upvoted": '.$row[5].' '."";
echo "}";
endwhile;
echo "]";

//  do not put any other echo statements in this - the returned content must be an array

	$conn->close();
?>