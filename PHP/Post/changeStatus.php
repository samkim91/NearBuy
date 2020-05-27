<?php

  require('../dbconn.php');

  // 클라이언트에서 상태에 대한 스트링값을 받아올 것이고, 받은 스트링 값을 게시물 아이디로 찾아서 업데이트 해준다.

  $phonenum = $_GET['phonenum'];
  $no = $_GET['no'];
  $status = $_GET['status'];

  // sql 문을 만들어준다.
  $stmt = $conn -> prepare('UPDATE post SET status = :status WHERE no = :no AND phonenum = :phonenum');
  $stmt -> bindParam(':status', $status);
  $stmt -> bindParam(':no', $no);
  $stmt -> bindParam(':phonenum', $phonenum);

  // 실행하고 결과를 리턴한다.
  try {
    if($stmt -> execute()){
      echo "success";
    }else{
      echo "failed";
    }

  } catch (PDOException $e) {
    echo "error: ".$e->getMessage();
  }

  $conn = null;
 ?>
