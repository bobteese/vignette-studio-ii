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
    $sid = session_id();
    $ivet = mysqli_real_escape_string($conn, $_POST['IVET']);

    $cid = mysqli_real_escape_string($conn, $_POST['CID']);
    
    $pageInteger = mysqli_real_escape_string($conn, $_POST['pageInteger']);
    $duration = mysqli_real_escape_string($conn, $_POST['duration']);
    $pageName = mysqli_real_escape_string($conn, $_POST['pageName']);
    $VidTxt = mysqli_real_escape_string($conn, $_POST['VidTxt']);
    																	
    $firstname = mysqli_real_escape_string($conn, $_POST['firstname']);
    $lastname = mysqli_real_escape_string($conn, $_POST['lastname']);
    $pass = mysqli_real_escape_string($conn, $_POST['pass']);
    $school = mysqli_real_escape_string($conn, $_POST['school']);  // for creating comments table
    
    $VideoLength = mysqli_real_escape_string($conn, $_POST['VideoLength']);
    $VideoPlayed = mysqli_real_escape_string($conn, $_POST['VideoPlayed']);
    $VideoPosition = mysqli_real_escape_string($conn, $_POST['VideoPosition']);
    $VideoTitle = mysqli_real_escape_string($conn, $_POST['VideoTitle']);
    $VidPercentViewed = mysqli_real_escape_string($conn, $_POST['VidPercentViewed']);
    $CombiAnswer = mysqli_real_escape_string($conn, $_POST['CombiAnswer']);
        

        
    $pageData = mysqli_real_escape_string($conn, $_POST['pageData']);

    $ip = get_ip();
    $ip = mysqli_real_escape_string($conn, $ip);
    $browserInfo = "";
    $browserInfo = mysqli_real_escape_string($conn, $_SERVER['HTTP_USER_AGENT']);
    
    if($pageName == "login") {
		// first check student table to see if firstname, CID and school already exist.
		// if  yes, get the stdntNum and commentsUserName. store in an array.
		// if no, then create a record and get the stdntNum and commentsUserName. store in an array, return to client
		
		$sql =  " INSERT INTO students (`firstname`, `CID`, `school`) SELECT * FROM (SELECT '$firstname', ".$cid.", '$school') AS tmp WHERE NOT EXISTS ( SELECT `firstname`, `CID`, `school` FROM students WHERE `firstname` = '$firstname' AND `CID` = `".$cid."` AND `school` = '$school') LIMIT 1 " ;
		
		
		    if ($conn->query($sql) === TRUE) {
		        //echo "students record created successfully";
		    } else {
		        echo "Error: " . $sql . "\r     " . $conn->error. "\r";
		    }
		
		$sql =  "SELECT stdntNum FROM students WHERE (firstname = '$firstname' AND CID = '$cid' AND school = '$school') ORDER by stdntNum DESC LIMIT 1 " ;
		$record_result = $conn->query($sql);
		
		//$record_row = $record_result->fetch_array(MYSQLI_BOTH);
		$record_row = $record_result->fetch_row();
		$stdntNum = $record_row[0];
		
		//echo "    record result = ".json_encode($record_row);
		//echo "    record result 0 = ".$record_row[0];
		
		$commentsUserName = $school.'-'.dechex($stdntNum+170);
				
		echo "     stdntNum~~~~~".$stdntNum."~~~~~".$commentsUserName."~~~~~CommentsUserName    " ;
		// in client:   str=returned string from server;      var result = str.split("~~~~~");   stdntNum=result[1];   commentsUserName=result[2];
		
		$sql =  "UPDATE students SET  commentsUserName = '$commentsUserName'  WHERE stdntNum = ". $stdntNum ." ; ";
		
		    if ($conn->query($sql) === TRUE) {
		        //echo "commentsUserName updated successfully";
		    } else {
		        echo "Error: " . $sql . "\r     " . $conn->error. "\r";
		    }
	}  // end of if pagename = login


    
    
    $sql = "INSERT INTO ivet (SID, IVET, CID, pageInteger, duration, pageName, firstname, lastname, pass, pageData, VidTxt, IP, browserInfo, VideoLength, VideoPlayed, VideoPosition, VideoTitle, VidPercentViewed, CombiAnswer) ".
            "VALUES ('". $sid ."', '". $ivet ."', '". $cid ."', '". $pageInteger ."','". $duration ."','". $pageName ."','". $firstname ."','". $lastname ."','". $pass ."','". $pageData ."','". $VidTxt ."','". $ip ."','". $browserInfo ."','". $VideoLength ."','". $VideoPlayed ."','". $VideoPosition ."','". $VideoTitle ."','". $VidPercentViewed ."','". $CombiAnswer ."')";
    
    if ($conn->query($sql) === TRUE) {
        echo "\r     New record created successfully";
    } else {
        echo "Error: " . $sql . "<br>" . $conn->error;
    }

	$conn->close();
	
	var_dump($_POST);
    
   function get_ip() {
    // from https://www.chriswiegman.com/2014/05/getting-correct-ip-address-php/
		//Just get the headers if we can or else use the SERVER global
		if ( function_exists( 'apache_request_headers' ) ) {
			$headers = apache_request_headers();
		} else {
			$headers = $_SERVER;
		}
		//Get the forwarded IP if it exists
		if ( array_key_exists( 'X-Forwarded-For', $headers ) && filter_var( $headers['X-Forwarded-For'], FILTER_VALIDATE_IP, FILTER_FLAG_IPV4 ) ) {
			$the_ip = $headers['X-Forwarded-For'];
		} elseif ( array_key_exists( 'HTTP_X_FORWARDED_FOR', $headers ) && filter_var( $headers['HTTP_X_FORWARDED_FOR'], FILTER_VALIDATE_IP, FILTER_FLAG_IPV4 )
		) {
			$the_ip = $headers['HTTP_X_FORWARDED_FOR'];
		} else {
			
			$the_ip = filter_var( $_SERVER['REMOTE_ADDR'], FILTER_VALIDATE_IP, FILTER_FLAG_IPV4 );
		}
		if($the_ip=='' && getenv('REMOTE_ADDR'))  $the_ip = getenv('REMOTE_ADDR');
		if($the_ip=='')  $the_ip = 'None reported';
		return $the_ip;
	}
?>