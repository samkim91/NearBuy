<?php

  //서버세팅. 이메일 보내는 기능을 쓰기 위해 필요한 것들을 선언함.
  use PHPMailer\PHPMailer\PHPMailer;
  use PHPMailer\PHPMailer\Exception;

  require "./PHPMailer/src/PHPMailer.php";
  require "./PHPMailer/src/SMTP.php";
  require "./PHPMailer/src/Exception.php";

  //데이터베이스 접근이 필요하기 때문에 선언해주고, 포스트로 받아온 값을 선언함.
  require('../dbconn.php');

  // strpos 는 앞의 변수에, 뒤 변수(문자열)가 포함되어 있는지 확인하는 함수.
  // $_SERVER['HTTP_USER_AGENT'] 는 사용자가 이용하고 있는 브라우저의 정보를 가져온다.
  // 즉, 현재 사용자가 안드로이드를 이용하고 있는지 확인함.
  $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

  // 안드로이드 코드에서 작성한 postParameters 변수에 적어둔 이름을 가지고 값을 전달 받는다.
  $phoneNum = $_POST['phoneNum'];
  $email = $_POST['email'];

  try{
    // sql문을 준비한다.
    $stmt = $conn -> prepare("SELECT * FROM userlist WHERE phonenum = :phonenum");
    // 휴대폰 번호 칸과 이메일 칸을 받아온 값으로 채운다.
    $stmt -> bindParam(':phonenum', $phoneNum);
    // $stmt -> bindParam(':email', $email);

    // sql문을 보낸다.
    $stmt -> execute();

    // 결과를 fetch로 연동
    $row = $stmt -> fetch();

    if($row!=null){
      // 가입된 휴대폰 번호라면 이메일도 일치하는지 검사하고 메일로 임시비밀번호를 보내준다.
      if($row['phonenum'] == $phoneNum && $row['email'] == $email){
        //6자리의 임시 비밀번호 생성
        function generateRandomString($length) {
            $characters = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
            //abcdefghijklmnopqrstuvwxyz 소문자 대문자 합치면 I와 l 이 분별이 안되기에 뺌.
            $charactersLength = strlen($characters);
            $randomString = '';
            for ($i = 0; $i < $length; $i++) {
                $randomString .= $characters[rand(0, $charactersLength - 1)];
            }
            return $randomString;
        }
        $temppwd = generateRandomString(6);

        // TODO:  데이터베이스에 있는 기존 비밀번호를 임시비밀번호로 바꾸는 작업.

        //실질적으로 메일을 보내는 부분!
        $mail = new PHPMailer(true);

        try {

          //디버깅 설정을 0 으로 하면 아무런 메시지가 출력되지 않는다.
          $mail -> SMTPDebug = 2; // 디버깅 설정
          $mail -> isSMTP(); // SMTP 사용 설정

          // 지메일일 경우 smtp.gmail.com, 네이버일 경우 smtp.naver.com
          $mail -> Host = "smtp.naver.com";               // 네이버의 smtp 서버
          $mail -> SMTPAuth = true;                         // SMTP 인증을 사용함
          $mail -> Username = "kimh0000@naver.com";    // 메일 계정 (지메일일경우 지메일 계정)
          $mail -> Password = "525Aud616";                  // 메일 비밀번호
          $mail -> SMTPSecure = "ssl";                       // SSL을 사용함
          $mail -> Port = 465;                                  // email 보낼때 사용할 포트를 지정

          $mail -> CharSet = "utf-8"; // 문자셋 인코딩

          // 보내는 메일
          $mail -> setFrom("kimh0000@naver.com", "Near Buy 관리자");

          // 받는 메일
          $mail -> addAddress($email, "고객님");

          // 메일 내용
          $mail -> isHTML(true); // HTML 태그 사용 여부
          $mail -> Subject = "Near Buy 에서 임시 비밀번호가 도착했습니다.";  // 메일 제목
          $mail -> Body = "임시비밀번호는 ".$temppwd." 입니다. 로그인하신 후에 비밀번호를 즉시 변경하는 것을 권장합니다.";     // 메일 내용

          // 메일을 발송하기 위해서는 CA인증이 필요하다.
          // CA 인증을 받지 못한 경우에는 아래 설정하여 인증체크를 해지하여야 한다.
          $mail -> SMTPOptions = array(
            "ssl" => array(
            "verify_peer" => false
            , "verify_peer_name" => false
            , "allow_self_signed" => true
            )
          );

          // 메일 전송
          $mail -> send();

          $stmt = $conn -> prepare("UPDATE userlist SET password = :password WHERE phonenum = :phonenum");
          $stmt -> bindParam(':phonenum', $phoneNum);
          $stmt -> bindParam(':password', $temppwd);

          $stmt -> execute();

          $count = $stmt -> rowCount();

          if($count>0){
            echo "ok";
          }else{
            echo "failed";
          }

        } catch (Exception $e) {
          echo "Message could not be sent. Mailer Error : ", $mail -> ErrorInfo;
        }

      }else{
        // 가입된 휴대폰 번호와 이메일이 데이터베이스에 일치하지 않을 때
        echo "wrong";
      }
    }else{
      // 가입된 휴대폰 번호와 이메일이 없을 때
      echo "null";
    }

  }catch(PDOException $e){
    echo "Error: ". $e->getMessage();
  }

?>
