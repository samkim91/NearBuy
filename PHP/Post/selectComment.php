<?php

  require('../dbconn.php');

  // 해당 게시물의 댓글들을 불러옴
  $postNum = $_GET['postNum'];

  // TODO: pagenation

  // 유저 정보도 불러와야함!!
  $stmt = $conn -> prepare('SELECT *, (SELECT image FROM userlist u WHERE u.phonenum = c.phonenum) image,
                            (SELECT nickname FROM userlist u WHERE u.phonenum = c.phonenum) nickname FROM comments c
                            WHERE postnum = :postnum ORDER BY IF(ISNULL(parent), num, parent), sequence');

  $stmt -> bindParam(':postnum', $postNum);

  if($stmt -> execute()){
    $result = $stmt -> fetchAll(PDO::FETCH_ASSOC);
    echo json_encode($result);
  }else{

    echo "failed";
  }











 ?>
