<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $tablename = $_POST['tablename'];
    $sharetablename = "shareroom".$tablename;

    $result  = mysqli_query($con, "SELECT * FROM USER_CONTENTS JOIN $sharetablename ON USER_CONTENTS.userid = $sharetablename.userid");

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
    }else{
        echo "failure";
    }

    mysqli_close($con);
?>