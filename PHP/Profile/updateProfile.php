<?php

  require('../dbconn.php');

  // 안드로이드에서 보낸 파일을 받아서 이름을 바꿔주는 절차를 함.(서버에서 중복되면 안 되기 때문에)
  $name = $_FILES['file']['name'];

  // 사진이 있을 때와, 없을 때에 클라이언트에서 보내는 값이 다르기 때문에 이를 조건문으로 나눠줌.
  if($name != null){
    // DB에 사진주소를 저장하기 위한 값
    $serverURL = "http://15.165.57.108/userImage/";
    // 이미지를 저장할 폴더를 지정해줌.
    $saveLocation = '../userImage/';

    // 안드로이드에서 보낸 전화번호와 닉네임을 선언
    $rawPhonenum = $_POST['phonenum'];
    $except = substr($rawPhonenum, 1);
    $phonenum = substr($except, 0, -1);

    $rawNickname = $_POST['nickname'];
    $except = substr($rawNickname, 1);
    $nickname = substr($except, 0, -1);

    $rawLastImg = $_POST['lastImg'];
    $except = substr($rawLastImg, 1);
    $lastImg = substr($except, 0, -1);

    // 이미지를 변경할 때 지난 이미지는 삭제한다.
    unlink('../userImage/'.$lastImg);

    // 위 파일의 이름이 파일명.확장자 형식으로 되어 있을테니 이것을 쪼갠다.
    $uploadName = explode('.', $name);
    // 서버에 저장할 이름이 중복되면 안되기 때문에 시간+키값(몇번째 파일)로 다시 이름을 만들고, 여기에 위에서 빼온 파일확장자를 붙여준다.(uploadName[1]이 확장자)
    $uploadName = time().$key.'.'.$uploadName[1];
    // 저장할 위치와 파일이름을 합쳐서 서버에 저장하기 위한 마무리 작업을 준비한다.
    $uploadFile = $saveLocation.$uploadName;
    // 서버에 본격적으로 저장하는 부분. 이 파일을 위에서 만들어준 이름으로 이동(저장)하겠다는 의미.
    move_uploaded_file($_FILES['file']['tmp_name'], $uploadFile);
    // 데이터베이스에 파일 위치를 저장하기 위해 만들어준다.
    $tmp = $serverURL.$uploadName;

    // SQL 문을 준비한다.
    $stmt = $conn -> prepare('UPDATE userlist SET nickname = :nickname, image = :image WHERE phonenum = :phonenum');
    $stmt -> bindParam(':phonenum', $phonenum);
    $stmt -> bindParam(':nickname', $nickname);
    $stmt -> bindParam(':image', $tmp);
  } else {
    // 사진이 없을 때 여기로 들어와서 닉네임만 바꿔준다.
    $phonenum = $_GET['phonenum'];
    $nickname = $_GET['nickname'];

    // 이미지는 없을 때의 SQL 문을 준비한다.
    $stmt = $conn -> prepare('UPDATE userlist SET nickname = :nickname WHERE phonenum = :phonenum');
    $stmt -> bindParam(':phonenum', $phonenum);
    $stmt -> bindParam(':nickname', $nickname);
  }

  // 쿼리를 보내고, 결과를 리턴한다.
  try{
    if($stmt -> execute()){
      echo "success";
    }else{
      echo "fail";
    }
  }catch(PDOException $e){
    echo "error: ".$e->getMessage();
  }

  $conn = null;

 ?>
