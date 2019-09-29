<?php
    $con = mysqli_connect("localhost","pyg941007","amiga2327!","pyg941007");

    mysqli_set_charset($con, "utf8");

    if(mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL : " . mysqli_connect_error();
    }

    $table = $_POST['tablename'];
    $tablename = "shareroom".$table;

    $result = mysqli_query($con, "SELECT * FROM GroupRoomMenu WHERE RoomId = '$tablename'");

    $data = array();

    if($result){  
        while($row=mysqli_fetch_array($result)){
            array_push($data, 
                array('MenuName'=>$row[1],
                'StartTime'=>$row[2],
                'EndTime'=>$row[3]
            ));
        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }
    
    mysqli_close($con);
?>