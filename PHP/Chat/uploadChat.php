<?php
  // 에러 띄워주는 기능
  ini_set('display_errors', 1);
  ini_set('error_reporting', E_ALL);
  // 데이터베이스 연결해 놓은 코드 가져옴
  require('../dbconn.php');

  $roomId = $_POST['roomId'];
  $userId = $_POST['userId'];
  $content =$_POST['content'];

  $stmt = $conn -> prepare('INSERT INTO chatText (roomId, userId, content) VALUES (:roomId, :userId, :content)');

  $stmt -> bindParam(':roomId', $roomId);
  $stmt -> bindParam(':userId', $userId);
  $stmt -> bindParam(':content', $content);

  if($stmt -> execute()){
    echo "successful";
  }else{
    echo "failed";
  }

  $conn = null;

 ?>
