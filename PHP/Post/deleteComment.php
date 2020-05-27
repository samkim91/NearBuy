<?php

  ini_set('display_errors', 1);
  ini_set('error_reporting', E_ALL);

  require('../dbconn.php');

  $commentId = $_GET['commentId'];
  $uId = $_GET['uId'];

  $altSentence = '삭제된 댓글입니다.';

  $stmt = $conn -> prepare('UPDATE comments SET comment = :altSentence WHERE num = :commentId AND phonenum = :uId');

  $stmt -> bindParam(':altSentence', $altSentence);
  $stmt -> bindParam(':commentId', $commentId);
  $stmt -> bindParam(':uId', $uId);

  if($stmt -> execute()){
    echo "success";
  }else{
    echo "fail";
  }

 ?>
