<?php
  // 데이터베이스 연결해 놓은 코드 가져옴
  require('../dbconn.php');
  // 클라이언트에서 받은 인풋을 선언
  $input = $_GET['input'];

  // 데이터베이스 쿼리문을 준비
  $stmt = $conn -> prepare('SELECT phonenum FROM userlist WHERE nickname = :input');
  $stmt -> bindParam(':input', $input);

  // 쿼리문을 실행하고, 행 개수를 보고 에코를 보내줌.
  try{
    if($stmt -> execute()){

      $row_count = $stmt -> rowCount();
      if($row_count >= 1){
        echo "unavailable";
      }else{
        echo "available";
      }
    }else{
      echo "failed";
    }
  }catch(PDOException $e){
    echo "error: ".$e->getMessage();
  }

  $conn = null;
 ?>
