<?php
  // 에러 띄워주는 기능
  ini_set('display_errors', 1);
  ini_set('error_reporting', E_ALL);
  // 데이터베이스 연결해 놓은 코드 가져옴
  require('../dbconn.php');

  $userId = $_GET['userId'];

  $stmt = $conn -> prepare('SELECT * FROM(
SELECT u.image image, u.nickname uNickname, u.phonenum uId, c1.roomId roomId, t.content content, t.date uDate
     	 FROM chatUsers c1, chatUsers c2, chatText t, userlist u
      	 WHERE (c1.userId = :userId AND c2.userId != :userId) AND c1.roomId = t.roomId AND c2.userId = u.phonenum AND c1.roomId = c2.roomId
         ORDER BY uDate DESC) sub GROUP BY sub.roomId DESC');

  $stmt -> bindParam(':userId', $userId);

  if($stmt -> execute()){

    $result = $stmt -> fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($result);

  }else {
    echo "failed";
  }

  $conn = null;
 ?>
