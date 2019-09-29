<?php  

$link=mysqli_connect("localhost","pyg941007","amiga2327!", "pyg941007" );  
if (!$link)  
{  
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();  
}  

mysqli_set_charset($link,"utf8"); 

$id = $_POST['userid'];

$sql="SELECT * FROM USER_CONTENTS WHERE userid = '$id' ORDER BY _id ASC";

$result=mysqli_query($link,$sql);
$data = array();   
if($result){  
    
    while($row=mysqli_fetch_array($result)){
        array_push($data, 
            array('_id'=>$row[0],
            'userid'=>$row[1],
            'title'=>$row[2],
            'contents'=>$row[3],
            'previoustime'=>$row[4],
            'aftertime'=>$row[5],
            'savedate'=>$row[6]
        ));
    }

    header('Content-Type: application/json; charset=utf8');
    $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
    echo $json;

}  
else{  
    echo "SQL문 처리중 에러 발생 : "; 
    echo mysqli_error($link);
} 


 
mysqli_close($link);  
   
?>