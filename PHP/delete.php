<?php

  require('dbconn.php');

  // strpos 는 앞의 변수에, 뒤 변수(문자열)가 포함되어 있는지 확인하는 함수.
  // $_SERVER['HTTP_USER_AGENT'] 는 사용자가 이용하고 있는 브라우저의 정보를 가져온다.
  // 즉, 현재 사용자가 안드로이드를 이용하고 있는지 확인함.
  // $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

  $no = $_GET['no'];

    // TODO: 사진이랑 위치 추가 필요..
  $stmt = $conn -> prepare('DELETE FROM post WHERE no = :no');
  $stmt -> bindParam(':no', $no);


  if($stmt -> execute()){
    echo "success";
  }else{
    echo "failed";
  }

  $conn = null;
 ?>
