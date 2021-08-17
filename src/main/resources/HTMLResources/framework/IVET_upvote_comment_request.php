<?php
    session_start();
    
    date_default_timezone_set("America/New_York");
	
	
    require('SiteConfigVars.php');
    $servername = '';
    $username = '';
    $password = '';
    $dbname = '';

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

	
	if($userHasUpvoted == "true") {		
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



//  do not put any other echo statements in this - no returned content 

  
    

	$conn->close();
?>