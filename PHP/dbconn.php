<?php

$hostname = 'localhost';
$username = 'root';
$dbpassword = '525aud16';
$dbname = 'nearbuy';

try {
  $conn = new PDO("mysql:host=$hostname;dbname=$dbname", $username, $dbpassword);
  // PDO 에러모드를 예외로 설정한다.
  $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
  // echo "connected successfully";

} catch (PDOException $e) {
  echo "connection failed: ". $e->getMessage();
}

// $conn = mysqli_connect($hostname, $username, $password, $db);


// if(mysqli_connect_errno()){
//   echo "Failed to connect to MySQL: ". mysqli_connect_error();
//   exit();
// }else{
  // echo "Connection success!";
// }

 ?>
