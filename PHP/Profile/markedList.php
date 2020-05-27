<?php

  require('../dbconn.php');

  // 안드로이드에서 넘겨준 값들을 받아옴.
  $phonenum = $_GET['phonenum'];
  $page = $_GET['page'];
  $count = 10; // 한번에 몇개를 불러올지?
  $start_point = $page * $count;

  // SQL문을 준비함. marked 테이블에서 입력된 휴대폰 번호를 기반으로 찜한 게시물 아이디를 가져오고, 이 아이디를 포스트 테이블에서 찾아 가져온다는 쿼리문.
  $stmt = $conn -> prepare('SELECT post.* FROM post INNER JOIN marked ON post.no = marked.postnum WHERE marked.phonenum = :phonenum ORDER BY post.no DESC LIMIT :start_point, :count');
  $stmt -> bindParam(':phonenum', $phonenum);
  $stmt -> bindParam(':start_point', $start_point, PDO::PARAM_INT);
  $stmt -> bindParam(':count', $count, PDO::PARAM_INT);

  // 쿼리를 보냄.
  try {
    if($stmt -> execute()){

      $stmt -> setFetchMode(PDO::FETCH_ASSOC);
      $postnum = $stmt -> fetchAll();

      echo json_encode($postnum);

    }else{
      echo "failed to select marked";
    }

  } catch (PDOException $e) {
    echo "error: ".$e->getMessage();
  }

 ?>
