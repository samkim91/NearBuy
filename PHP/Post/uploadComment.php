<?php
  // 에러 띄워주는 기능
  ini_set('display_errors', 1);
  ini_set('error_reporting', E_ALL);

  require('../dbconn.php');

  $postNum = $_POST['postnum'];
  $phoneNum = $_POST['phonenum'];
  $comment = $_POST['comment'];
  $parentNum = $_POST['parent'];
  $seq = $_POST['sequence']+1;

  // 부모댓글이 있는지 없는지에 따라서 다른 쿼리문이 실행되게끔 한다.
  if($parentNum == -1){
    // 부모댓글이 없을땐, 이 댓글이 대댓글이 아니란 것이고, 기본값만 넣어준다.
    $stmt = $conn -> prepare('INSERT INTO comments(phonenum, postnum, comment)
    VALUES (:phonenum, :postnum, :comment)');

    $stmt -> bindParam(':phonenum', $phoneNum);
    $stmt -> bindParam(':postnum', $postNum);
    $stmt -> bindParam(':comment', $comment);
  }else{
    // 부모댓글이 있을 때, 이 댓글이 대댓글이라는 것이고, 기본 댓글 요소에 부모댓글도 넣어준다.
    // 또한 sequence를 현재 부모댓글 아래에 있는 대댓글 중에 가장 마지막 번호에 +1 해서 넣어준다.
    $stmt = $conn -> prepare('INSERT INTO comments(phonenum, postnum, comment, parent, sequence) VALUES
      (:phoneNum, :postNum, :comment, :parent, :seq)');

    $stmt -> bindParam(':phoneNum', $phoneNum);
    $stmt -> bindParam(':postNum', $postNum);
    $stmt -> bindParam(':comment', $comment);
    $stmt -> bindParam(':parent', $parentNum);
    $stmt -> bindParam(':seq', $seq);
  }

  // statement를 실행시키고, 결과를 클라이언트측으로 보내준다.
  if($stmt -> execute()){
    echo "ok";
  }else{
    echo "not ok";
  }


  $conn = null;
?>
