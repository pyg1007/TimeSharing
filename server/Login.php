<?
  header('content-type: text/html; charset=utf-8'); 
  // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호)
  $connect=mysql_connect( "localhost", "pyg941007", "amiga2327!") or die( "SQL server에 연결할 수 없습니다.");
 
  mysql_query("SET NAMES UTF8");
  // 데이터베이스 선택
  mysql_select_db("pyg941007",$connect);
 
  // 세션 시작
  session_start();
 
  $id = $_POST['userid'];
  $pw = $_POST['userpassword'];
  $sql = "SELECT IF(strcmp(userpassword, '$pw'),'0','1') pw_chk FROM USER WHERE userid = '$id'";
 
  $result = mysql_query($sql);
 
  // result of sql query
  if($result)
  {
    $row = mysql_fetch_array($result);
    if(is_null($row['pw_chk']))
    {
      echo "Can not find ID";
    }
    else
    {
      echo "$row[pw_chk]";
    }
  }
  else
  {
   echo mysql_errno($connect);
  }


출처: https://cholol.tistory.com/404?category=572900 [IT, I Think ]
?>