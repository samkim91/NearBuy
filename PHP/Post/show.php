<?php

  require('../dbconn.php');

  // TODO: 클라이언트 단에서 키워드를 받는 작업을 해둘것이다.. 그럼 이 키워드에 따라 데이터베이스를 검색하면 된다.
  $no = $_GET['no'];

  // 먼저 해당하는 게시물을 불러온다.
  $stmt = $conn -> prepare('SELECT *,
    (SELECT COUNT(num) FROM comments c WHERE c.postnum = p.no) AS commentNum
    FROM post p WHERE no = :no');
  $stmt -> bindParam(':no', $no);

  $stmt -> execute();
  $result = $stmt->fetch();

  // 게시물을 클라이언트에 띄울 때 작성한 유저의 정보가 필요하기에 ID를 가져온다.
  $phonenum = $result['phonenum'];

  // 유저 정보를 가져오는 쿼리를 만든다.
  $stmt = $conn -> prepare('SELECT nickname FROM userlist WHERE phonenum = :phonenum');
  $stmt -> bindParam(':phonenum', $phonenum);

  $stmt -> execute();

  // 유저정보에서 닉네임만 빼온다.
  $result1 = $stmt->fetch();
  $nickname = $result1['nickname'];

  // 이 게시물의 좋아요한 사람들을 꺼내온다.
  $stmt = $conn -> prepare('SELECT phonenum FROM marked WHERE postnum = :no');
  $stmt -> bindParam(':no', $no);

  $stmt -> execute();
  $selectMarked = $stmt->fetch();

  // 클라이언트로 보낼 값을 어레이로 담고, Json 형태로 바꾸어 보내준다.
  $arr = array('postInfo' => $result, 'nickname' => $nickname, 'marked' => $selectMarked);
  echo json_encode($arr);


  $conn = null;

 ?>
