<?php

  require('../dbconn.php');

  // 안드로이드에서 받아온 값을 변수화 해준다.
  $no = $_GET['no'];
  $phoneNum = $_GET['phoneNum'];
  $isMarked = $_GET['isMarked'];

  // echo json_encode(['response' => $isMarked]);

  try {
    // 마크가 참이었다면, 이전에 마크를 했었다는 것이고, 이번에 마크를 해제한다는 것이다. 따라서 해당 row를 찾아서 삭제해줘야 한다.
    // 이후에 해당 게시물을 찾아가서 좋아요 숫자를 -1 해줘야함.
    if($isMarked == 'true'){
      $stmt = $conn -> prepare('DELETE FROM marked WHERE postnum = :no AND phonenum = :phonenum');
      $stmt -> bindParam(':no', $no);
      $stmt -> bindParam(':phonenum', $phoneNum);

      if($stmt -> execute()){
        $stmt = $conn -> prepare('UPDATE post SET marked = marked-1 WHERE no = :no');
        $stmt -> bindParam(':no', $no);

        if($stmt -> execute()){
          echo json_encode(['boolean' => $isMarked, 'response' => 'eraseAll']);
        }else{
          echo json_encode(['boolean' => $isMarked, 'response' => 'failedMinus']);
        }
      }else{
        echo json_encode(['boolean' => $isMarked, 'response' => 'failedDelete']);
      }

    }else{
    // 마크가 거짓이었다면, 이전에 마크를 안 했었다는 것이고, 이번에 마크를 하겠다는 것이다. 따라서 새로 row를 만들어 넣어줘야 한다.
    // 이후에 해당 게시물을 찾아가서 좋아요 숫자를 +1 해줘야함.
      $stmt = $conn -> prepare('INSERT INTO marked(postnum, phonenum) VALUES (:no, :phonenum)');
      $stmt -> bindParam(':no', $no);
      $stmt -> bindParam(':phonenum', $phoneNum);

      if($stmt -> execute()){
        $stmt = $conn -> prepare('UPDATE post SET marked = marked+1 WHERE no = :no');
        $stmt -> bindParam(':no', $no);

        if($stmt -> execute()){
          echo json_encode(['boolean' => $isMarked, 'response' => 'completeAll']);
        }else{
          echo json_encode(['boolean' => $isMarked, 'response' => 'failedPlus']);
        }
      }else{
        echo json_encode(['boolean' => $isMarked, 'response' => 'failedInsert']);
      }
    }
  } catch (PDOException $e) {
    echo "err: ".$e->getMessage();
  }

  $conn = null;
 ?>
