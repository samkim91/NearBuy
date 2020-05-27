<?php

  require('../dbconn.php');

  // strpos 는 앞의 변수에, 뒤 변수(문자열)가 포함되어 있는지 확인하는 함수.
  // $_SERVER['HTTP_USER_AGENT'] 는 사용자가 이용하고 있는 브라우저의 정보를 가져온다.
  // 즉, 현재 사용자가 안드로이드를 이용하고 있는지 확인함.
  // $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

  // DB에 사진주소를 저장하기 위한 값
  $serverURL = "http://15.165.57.108/images/";
  // 이미지를 저장할 폴더를 지정해줌.
  $saveLocation = '../images/';
  // 사진 주소들을 DB에 저장하기 위해 임시배열을 하나 만듦.
  $tmpArray = array();

  // 클라이언트단에서 사진을 보내기 위해 multipart 를 사용한 뒤부터 모든 변수값에 ""가 붙어서 나옴. db에 저장해주기 위해 ""를 제거하는 과정.
  $rawPhoneNum = $_POST['phoneNum'];
  $except = substr($rawPhoneNum, 1);
  $phoneNum = substr($except, 0, -1);

  $rawTitle = $_POST['title'];
  $except = substr($rawTitle, 1);
  $title = substr($except, 0, -1);

  $rawAddress = $_POST['address'];
  $except = substr($rawAddress, 1);
  $address = substr($except, 0, -1);

  $rawCategory = $_POST['category'];
  $except = substr($rawCategory, 1);
  $category = substr($except, 0, -1);

  $rawPrice = $_POST['price'];
  $except = substr($rawPrice, 1);
  $price = substr($except, 0, -1);

  $rawContent = $_POST['content'];
  $except = substr($rawContent, 1);
  $content = substr($except, 0, -1);

  $lat = $_POST['lat'];
  $lng = $_POST['lng'];

  // 다수 이미지를 저장하기 위해 foreach문을 사용함.
  // files라는 태그를 달고 클라이언트로부터 받아온 데이터를 다룬다.
  // $_FILES['태그명']['필요한 내용']에 대해서 보자면 '필요한 내용'에 name이 들어가면 받은 파일의 이름, size가 들어가면 받은 파일의 크기,
  // type은 받은 파일의 형식, tmp_name은 받은 파일의 임시 이름 등을 출력할 수 있다.
  foreach ($_FILES['files']['name'] as $key => $value) {
    // key번째 파일의 이름을 받아온다.
    $name = $_FILES['files']['name'][$key];
    // 위 파일의 이름이 아마 파일명.확장자 형식으로 되어 있을테니 이것을 쪼갠다.
    $uploadName = explode('.', $name);
    // 서버에 저장할 이름이 중복되면 안되기 때문에 시간+키값(몇번째 파일)로 다시 이름을 만들고, 여기에 위에서 빼온 파일확장자를 붙여준다.(uploadName[1]이 확장자)
    $uploadName = time().$key.'.'.$uploadName[1];
    // 저장할 위치와 파일이름을 합쳐서 서버에 저장하기 위한 마무리 작업을 준비한다.
    $uploadFile = $saveLocation.$uploadName;

    // 서버에 본격적으로 저장하는 부분. key번째 파일을, 위에서 만들어준 이름으로 이동(저장)하겠다는 의미.
    if(move_uploaded_file($_FILES['files']['tmp_name'][$key], $uploadFile)){
      $tmpArray[] = $serverURL.$uploadName;
      // array_push($tmpArray, $serverURL.$uploadName);
      // echo json_encode(['response' => 'successUpload']);
    }
  }


  $stmt = $conn -> prepare('INSERT INTO post(phonenum, lat, lng, title, address, category, price, content, images)
  VALUES (:phonenum, :lat, :lng, :title, :address, :category, :price, :content, :images)');

  $stmt -> bindParam(':phonenum', $phoneNum);
  $stmt -> bindParam(':lat', $lat);
  $stmt -> bindParam(':lng', $lng);
  $stmt -> bindParam(':title', $title);
  $stmt -> bindParam(':address', $address);
  $stmt -> bindParam(':category', $category);
  $stmt -> bindParam(':price', $price);
  $stmt -> bindParam(':content', $content);
  $stmt -> bindParam(':images', json_encode($tmpArray, JSON_FORCE_OBJECT));

  // foreach ($tmpArray as $key => $value) {
  //   $stmt -> bindParam(':image'.$key, $tmpArray[$key]);
  // }

  // 쿼리를 실행하고, 결과에 따라서 성공인지 실패인지를 json으로 인코딩 하고 보낸다.
  try{


    if($stmt -> execute()){
      echo json_encode(['response' => 'success']);
    }else{
      echo json_encode(['response' => 'failed']);
    }


  }catch(PDOException $e){
    echo "error: ". $e->getMessage();
  }

  $conn = null;
 ?>
