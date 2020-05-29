<?php
  // 에러 띄워주는 기능
  ini_set('display_errors', 1);
  ini_set('error_reporting', E_ALL);
  // 데이터베이스 연결해 놓은 코드 가져옴
  require('../dbconn.php');

  $user1 = $_GET['user1'];
  $user2 = $_GET['user2'];

  // 챗유저 테이블을 셀프조인한다. 셀프조인해서, 받아온 유저1과 유저2가 있는지 확인하고.. 여기서 중복되는 roomId를 가졌는지 확인한다.
  // 값이 있다면 그 roomId를 리턴하고, 없다면 0이 나옴.
  $stmt = $conn -> prepare('SELECT c1.roomId FROM chatUsers c1, chatUsers c2 WHERE (c1.userId = :user1 AND c2.userId = :user2)
                            OR (c1.userId = :user2 AND c2.userId = :user1) GROUP BY c1.roomId HAVING COUNT(c1.roomId) > 1');

  // 쿼리문에 값을 넣어주기
  $stmt -> bindParam(':user1', $user1);
  $stmt -> bindParam(':user2', $user2);

  // stmt를 실행한다. 결과에 따라 true / false 가 나온다.
  if($stmt -> execute()){
    // 쿼리결과 카운트
    $row_count = $stmt -> rowCount();

    // 쿼리결과가 1 이상이면.. 기존에 방이 있다는 것이므로, 이걸 반환해줄 것이다.
    // 쿼리 결과가 0보다 작다면.. 기존 방이 없다는 뜻이므로, 새로 생성해주자.
    if( $row_count >= 1){
      $result = $stmt -> fetch(PDO::FETCH_ASSOC);
      $result = json_encode($result);

      echo $result;
    }else{
      // 방을 새로 생성해줄 것이다. 이때 방의 유니크 값으로 id도 넣어줄 것이다.
      $roomId = uniqid();

      // sql 문
      $stmt = $conn -> prepare('INSERT INTO chatUsers (roomId, userId) VALUES (:roomId, :user1), (:roomId, :user2)');

      // 값 바인딩
      $stmt -> bindParam(':roomId', $roomId);
      $stmt -> bindParam(':user1', $user1);
      $stmt -> bindParam(':user2', $user2);

      // 실행
      if($stmt -> execute()){
        // 입력이 성공했으면.. roomId를 클라이언트로 넘겨주자.
        echo $roomId;
      }else{
        echo "failed";
      }
    }

  }else{
    echo "failed";
  }

  $conn = null;

 ?>
