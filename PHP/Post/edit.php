<?php

  require('../dbconn.php');


  // $location = $_POST['location'];
  // 클라이언트단에서 사진을 보내기 위해 multipart 를 사용한 뒤부터 모든 변수값에 ""가 붙어서 나옴. db에 저장해주기 위해 ""를 제거하는 과정.
  $rawNo = $_POST['no'];
  $except = substr($rawNo, 1);
  $no = substr($except, 0, -1);

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

  $rawImageUriList = $_POST['imageUriList'];
  $ImageUriList = json_decode($rawImageUriList);

  // DB에 사진주소를 저장하기 위한 값
  $serverURL = "http://15.165.57.108/images/";
  // 이미지를 저장할 폴더를 지정해줌.
  $saveLocation = '../images/';
  // 사진 주소들을 DB에 저장하기 위해 임시배열을 하나 만듦.
  // $tmpArray = array();

  // 편집에서 살아남은 사진들의 주소를 DB에 저장하기 위한 임시 배열에 넣어둔다.
  // foreach ($ImageUriList as $key => $value) {
  //   $tmpArray[] = $ImageUriList[$key];
  // }

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
      $ImageUriList[] = $serverURL.$uploadName;
      // array_push($tmpArray, $serverURL.$uploadName);
      // echo json_encode(['response' => 'successUpload']);
    }
  }

  $stmt = $conn -> prepare('UPDATE post SET title = :title, address = :address, category = :category, price = :price, content = :content, images = :images WHERE no = :no');

  $stmt -> bindParam(':no', $no);
  $stmt -> bindParam(':title', $title);
  $stmt -> bindParam(':address', $address);
  $stmt -> bindParam(':category', $category);
  $stmt -> bindParam(':price', $price);
  $stmt -> bindParam(':content', $content);
  $stmt -> bindParam(':images', json_encode($ImageUriList, JSON_FORCE_OBJECT));

  if($stmt -> execute()){
    echo json_encode(['response' => 'successUpdateDB']);
  }else{
    echo json_encode(['response' => 'failed']);
  }

  $conn = null;
 ?>
