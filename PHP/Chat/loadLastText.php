<?php
  // 에러 띄워주는 기능
  ini_set('display_errors', 1);
  ini_set('error_reporting', E_ALL);
  // 데이터베이스 연결해 놓은 코드 가져옴
  require('../dbconn.php');

  $roomId = $_GET['roomId'];

  $stmt = $conn -> prepare('SELECT *, u.image image, u.nickname nickname
    FROM chatText c, userlist u WHERE c.roomId = :roomId AND c.userId = u.phonenum');

  $stmt -> bindParam(':roomId', $roomId);
  
  if($stmt -> execute()){

    $result = $stmt -> fetchAll(PDO::FETCH_ASSOC);
    $result = json_encode($result);

    echo $result;

  }else {
    echo "failed";
  }

 ?>
