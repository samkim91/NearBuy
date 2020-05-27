<?php

  require('../dbconn.php');

  $phonenum = $_GET['phonenum'];
  $page = $_GET['page'];
  $status = $_GET['status'];
  $count = 10; // 한번에 몇개를 불러올지?
  $start_point = $page * $count;

  // SQL문을 준비한다.
  if($status == "all"){
    $stmt = $conn -> prepare('SELECT * FROM post WHERE phonenum = :phonenum ORDER BY no DESC LIMIT :start_point, :count');
    $stmt -> bindValue(':phonenum', $phonenum);
    $stmt -> bindValue(':start_point', $start_point, PDO::PARAM_INT);
    $stmt -> bindValue(':count', $count, PDO::PARAM_INT);
  }else{
    $stmt = $conn -> prepare('SELECT * FROM post WHERE phonenum = :phonenum AND status = :status ORDER BY no DESC LIMIT :start_point, :count');
    $stmt -> bindValue(':phonenum', $phonenum);
    $stmt -> bindValue(':status', $status);
    $stmt -> bindValue(':start_point', $start_point, PDO::PARAM_INT);
    $stmt -> bindValue(':count', $count, PDO::PARAM_INT);
  }

  try{
    // 쿼리 날리기.
    if($stmt -> execute()){
      // 모든 결과를 불러온다. 어레이형태로 되어있기 때문에 바로 제이슨으로 변형해주면, 제이슨 어레이가 나올 것이다. 따라서 클라이언트 단에서 제이슨 어레이부터 해체해준다.
      $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
      $json = json_encode($result);

      echo $json;
    }else{
      echo "failed";
    }
  }catch(PDOException $e){
    echo "error: ". $e->getMessage();
  }

  $conn = null;

 ?>
