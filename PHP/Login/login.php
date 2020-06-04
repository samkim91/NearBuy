<?php
  // echo "php start";
  require('../dbconn.php');

  // strpos 는 앞의 변수에, 뒤 변수(문자열)가 포함되어 있는지 확인하는 함수.
  // $_SERVER['HTTP_USER_AGENT'] 는 사용자가 이용하고 있는 브라우저의 정보를 가져온다.
  // 즉, 현재 사용자가 안드로이드를 이용하고 있는지 확인함.
  $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

  // post로 요청되었고 submit이 설정되었다면! 또는 안드로이드 사용자라면 실행한다.
  if(($_SERVER['REQUEST_METHOD'] == 'POST') || $android){

    // 안드로이드 코드에서 작성한 postParameters 변수에 적어둔 이름을 가지고 값을 전달 받는다.
    $phoneNum = $_POST['phoneNum'];
    $password = $_POST['password'];

    try{
      // sql문을 준비한다.
      $stmt = $conn -> prepare("SELECT phonenum, nickname, email, image FROM userlist WHERE phonenum = :phonenum AND password = :password");

      // 휴대폰 번호를 넣는다.
      $stmt -> bindParam(':phonenum', $phoneNum);
      $stmt -> bindParam(':password', $password);
      // 준비된 sql문을 보낸다.(쿼리)
      $stmt -> execute();

      // 결과를 fetch로 연동한다.
      $row = $stmt -> fetch(PDO::FETCH_ASSOC);

      if($row!=null){
        // echo "아이디 있음"
        echo json_encode($row);

      }else{
        echo "null";
      }

    }catch(PDOException $e){
      echo "Error: ". $e->getMessage();
    }
  }

  $conn = null;
 ?>
