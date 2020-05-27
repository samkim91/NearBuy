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
  $nickname = $_POST['nickname'];
  $email = $_POST['email'];
  // echo $phoneNum. $password. $nickname. $email;
  try {
    // 쿼리문 양식을 만든다.
    $stmt = $conn->prepare('INSERT INTO userlist(phonenum, password, nickname, email) VALUES (:phonenum, :password, :nickname, :email)');

    // 위에서 만든 쿼리문에 넣을 변수들을 묶어준다.
    $stmt->bindParam(':phonenum', $phoneNum);
    $stmt->bindParam(':password', $password);
    $stmt->bindParam(':nickname', $nickname);
    $stmt->bindParam(':email', $email);

    // 쿼리문을 보냄. 결과에 따라 참/거짓이 나옴.

    if($stmt->execute()){
     // 정상적으로 가입되었을 때 반환하는 메세지
      $successMSG = "가입이 완료되었습니다.";
      echo $successMSG;
    }else{
     // 쿼리문 보내는 데에 문제가 발생했을 때 출력할 메세지
      $errMSG = "가입에 실패하였습니다.";
      echo $errMSG;
    }

  } catch (PDOException $e) {
    // 예외로 인한 오류가 발생했을 때 띄울 부분
    echo "Database error: ". $e->getMessage();
  }
}

$conn = null;
// echo "끝";
 ?>
