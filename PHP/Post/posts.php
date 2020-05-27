<?php
// echo "start";
  require('../dbconn.php');

  // TODO: 클라이언트 단에서 키워드를 받는 작업을 해둘것이다.. 그럼 이 키워드에 따라 데이터베이스를 검색하면 된다.
  $keyword = $_GET['request'];

  $lat = $_GET['lat'];
  $lng = $_GET['lng'];
  $radius = $_GET['radius'];
  // $radius = (int)$getradius;
  // 페이징을 위한 페지이 숫자와 시작할 포인트를 정함.
  $page = $_GET['page'];
  $count = 10; // 한번에 몇개를 불러올지?
  $start_point = $page * $count;

  // 클라이언트 단에서 온 키워드가 뭔가에 따라서 쿼리문을 다르게 만들어준다.
  if($keyword == "all"){
    // $stmt = $conn -> prepare('SELECT * FROM post ORDER BY no DESC LIMIT :start_point, :count');
    $stmt = $conn -> prepare('SELECT *,
      (SELECT COUNT(num) FROM comments c WHERE c.postnum = p.no) AS commentNum,
      (6371* acos( cos( radians(:lat)) * cos( radians(lat)) * cos( radians(lng) - radians(:lng)) + sin( radians(:lat)) * sin( radians(lat)))) AS distance
      FROM post p HAVING distance < :radius ORDER BY no DESC LIMIT :start_point, :count');
    $stmt -> bindValue(':lat', $lat);
    $stmt -> bindValue(':lng', $lng);
    $stmt -> bindValue(':radius', $radius);
    $stmt -> bindValue(':start_point', $start_point, PDO::PARAM_INT);
    $stmt -> bindValue(':count', $count, PDO::PARAM_INT);
  }else{
    $stmt = $conn -> prepare('SELECT * FROM post WHERE title = :keyword ORDER BY no DESC');
    $stmt -> bindValue(':keyword', $keyword);
  }

  try{
    // 쿼리 날리기.
    if($stmt -> execute()){
      // 모든 결과를 불러온다. 어레이형태로 되어있기 때문에 바로 제이슨으로 변형해주면, 제이슨 어레이가 나올 것이다. 따라서 클라이언트 단에서 제이슨 어레이로 접근할 필요가 있음!
      $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
      $json = json_encode($result);

      echo $json;
    }else{
      echo "failed";
    }
  }catch(PDOException $e){
    echo "error: ". $e->getMessage();
  }
// echo "end";
  $conn = null;
 ?>
