<?php

  // 데이터베이스에 접근하는 php 포함시킴
  require('../dbconn.php');

  // 포스트로 전달받은 값을 선언
  $userId = $_POST['userId'];

  // sql문을 준비한다.
  $stmt = $conn->prepare('SELECT * FROM userlist WHERE phonenum = :phonenum');
  $stmt -> bindParam(':phonenum', $userId);

  // 준비된 스테이트먼트를 실행함.
  try {
    if($stmt -> execute()){
      // 결과를 펫치에 담음
      $result = $stmt -> fetch();

      // 결과를 제이슨화 하기 위해 어레이에 담음
      $arr = array('response' => $result);
      // 제이슨화 하고 에코로 보냄
      echo json_encode($result);
    }else{
      // sql문을 날리기에 실패했다면 해당 내용을 안내
      echo json_encode(['response' => '값을 불러오는데 실패했어요.']);
    }
  } catch (PDOException $e) {
    echo json_encode(['response' => 'PDOException :'.$e.getMessage()]);
  }

  $conn = null;
 ?>
