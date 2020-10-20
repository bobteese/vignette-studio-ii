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
	
    $userHasUpvoted = mysqli_real_escape_string($conn, $_POST['user_has_upvoted']);
    $id = mysqli_real_escape_string($conn, $_POST['id']);		
    $upvoteCount = mysqli_real_escape_string($conn, $_POST['upvote_count']);
    
   // var_dump($_POST);
   //  echo "  ___userHasUpvoted: ".$userHasUpvoted;
   //  echo "  upvoteCount: ".$upvoteCount;	

	
/*	if($userHasUpvoted == "true") {		
			$sql = "UPDATE comments SET upvoteCount = upvoteCount + 1 WHERE  id = ".$id." ; ";
				echo " UPVOTED ".$userHasUpvoted."   ";    
	} else {
			$sql = "UPDATE comments SET upvoteCount = upvoteCount - 1 WHERE  id = ".$id." AND upvoteCount > 0; "; 
				echo " DOWNVOTED ";
    }     
    
    if ($conn->query($sql) === TRUE) {
        //echo " ...New record created successfully in comments... ".$content;
    } else {
        echo "Error: " . $sql . "<first call>" . $conn->error;
    } 
*/    

$sql = "SELECT * FROM comments WHERE id = ".$id." LIMIT 1; "; 
/*
    if ($conn->query($sql) === TRUE) {
        //echo " ...New record created successfully in comments... ".$content;
    } else {
        echo "Error: " . $sql . "<first call>" . $conn->error;
    } 
*/    
    
    

// get callback data from server: need $sql for 1 updated comment


// cannot put two querys in a script unless one is prepared statement (sql-injection issue)

/*		$stmt = $conn->stmt_init();
		if (!($stmt->prepare($query2))) {
			echo "Prepare failed: (" . $conn->errno . ") " . $conn->error."<br />";
		} else {//echo "Prepare successful<br />";
		}
		//
		if (!($stmt->bind_param("s", $id))) {
		    echo "Bind failed: (" . $conn->errno . ") " . $conn->error."<br />";
		} else {//echo "Bind successful<br />";
		}


		// $stmt->execute();
		if (!(mysqli_stmt_execute($stmt))) {
		    echo "Execute failed: (" . $conn->errno . ") " . $conn->error."<br />";
		} 
		else {//echo "<br />Execute successful,  stmt: <br />".print_r($stmt)."<br />";
		}	

		if (!($record_result = $stmt->get_result())) {
		    echo "stmt->get_result failed: (" . $conn->errno . ") " . $conn->error."<br />";
		} 
		else {//echo "..."; echo "stmt->get_result successful<br />"; echo "..."; 
		}
		
		//$row2 = $result2->fetch_array(MYSQLI_BOTH);  

*/ 

$indexnumber = 0;
$record_result = $conn->query($sql);

$record_object = "{";
while ($row = mysqli_fetch_array($record_result, MYSQLI_BOTH)) :
$record_object = $record_object.  '"id": '.$row["id"].', '  ;
$record_object = $record_object.  '"created": "'.$row["created"].'", ';
$record_object = $record_object.  '"content": "'.$row["content"].'", ';
$record_object = $record_object.  '"fullname": "'.$row["commentsUserName"].'", ';
$record_object = $record_object.  '"upvote_count": '.$row["upvoteCount"].', ';
$record_object = $record_object.  '"user_has_upvoted": '.$row["userHasUpvoted"].'} ';
endwhile;

//$record_json = json_encode($record_object);
$record_json = $record_object; 



//$record_json = '{"id":55,"created":"2020-06-12T17:39:48.866Z","content":"ge 5ee","fullname":"RIT-bb","upvote_count":9,"user_has_upvoted":true}';




echo $record_json;

//  do not put any other echo statements in this - the returned content must be an array

  
    

	$conn->close();
?>